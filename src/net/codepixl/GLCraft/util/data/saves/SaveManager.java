package net.codepixl.GLCraft.util.data.saves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;
import com.evilco.mc.nbt.tag.TagLong;
import com.evilco.mc.nbt.tag.TagString;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class SaveManager {
	
	public static String formatV0 = "GLCWorldv0";
	public static String formatV1 = "GLCWorldv1";
	public static String formatV2 = "GLCWorldv2";
	public static String currentFormat = formatV2;
	
	public static boolean saveWorld(WorldManager tworldManager, Save tsave, boolean tquit){
    	System.out.println("Saving world "+tsave+"...");
    	
		tsave.name = tsave.name.replaceAll("[^ a-zA-Z0-9.-]", "_");
		
		final WorldManager worldManager = tworldManager;
		final Save save = tsave;
		final boolean quit = tquit;
		worldManager.setSaving(true);
		Runnable r = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//PLAYER SAVING
				EntityPlayer p = worldManager.getEntityManager().getPlayer();
				TagCompound compound = new TagCompound("Player");
				TagList posList = new TagList("Pos");
				posList.addTag(new TagFloat("",p.getPos().x));
				posList.addTag(new TagFloat("",p.getPos().y));
				posList.addTag(new TagFloat("",p.getPos().z));
				TagList rotList = new TagList("Rot");
				rotList.addTag(new TagFloat("",p.getRot().x));
				rotList.addTag(new TagFloat("",p.getRot().y));
				rotList.addTag(new TagFloat("",p.getRot().z));
				TagList velList = new TagList("Vel");
				velList.addTag(new TagFloat("",p.getVel().x));
				velList.addTag(new TagFloat("",p.getVel().y));
				velList.addTag(new TagFloat("",p.getVel().z));
				TagLong timeTag = new TagLong("TimeAlive", p.timeAlive);
				TagString typeTag = new TagString("type", EntityPlayer.class.getSimpleName());
				compound.setTag(posList);
				compound.setTag(rotList);
				compound.setTag(velList);
				compound.setTag(timeTag);
				compound.setTag(typeTag);
				TagList inventory = new TagList("Inventory");
				for(int i = 0; i < p.getInventory().length; i++){
					ItemStack stack = p.getInventory(i);
					if(!stack.isNull()){
						TagCompound slot = stack.toNBT();
						slot.setTag(new TagInteger("slot",i));
						inventory.addTag(slot);
					}
				}
				compound.setTag (inventory);
				
				try{
					File f = new File(Constants.GLCRAFTDIR+"saves/"+save.name+"/");
					f.mkdirs();
					FileOutputStream outputStream;
					outputStream = new FileOutputStream(new File(f,"player.nbt"));
					NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);
					nbtOutputStream.write(compound);
					nbtOutputStream.close();
					
					//WORLD SAVING
					worldManager.saveChunks(save.name);
					
					//ENTITY SAVING
					worldManager.getEntityManager().save(save.name);
					
					//METADATA SAVING
					writeMetadata(save);
					
				}catch(IOException e){
					e.printStackTrace();
				}
				worldManager.setSaving(false);
				if(quit)
					System.exit(0);
			}
		};
		
		new Thread(r).start();
		return true;
	}

	public static boolean loadWorld(WorldManager worldManager, String name) {
		try {
			FileInputStream inputStream;
			NbtInputStream nbtInputStream;
			TagCompound tag;
			
			//METADATA LOADING
			Save s = getSave(name);
			if(s != null){
				if(!s.format.equals(currentFormat)){
					if(!upgradeWorld(s)){
						JOptionPane.showMessageDialog(null, "Unsupported world format: "+s.format, "Error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			}else{
				JOptionPane.showMessageDialog(null, "Unknown error loading the world.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			EntityPlayer p = worldManager.getEntityManager().getPlayer();
			
			//WORLD LOADING
			worldManager.loadChunks(s);
			
			//ENTITY & PLAYER LOADING
			worldManager.entityManager.removeAll();
			p.setInventory(new ItemStack[p.getInventorySize()]);
			for(int i = 0; i < p.getInventorySize(); i++){
				p.setInventory(i, new ItemStack());
			}
			inputStream = new FileInputStream(Constants.GLCRAFTDIR+"/saves/"+name+"/player.nbt");
			nbtInputStream = new NbtInputStream(inputStream);
			tag = (TagCompound)nbtInputStream.readTag();
			if(tag != null){
				if(tag.getList("Inventory", TagCompound.class) != null){
					List<TagCompound> inventory = tag.getList("Inventory",TagCompound.class);
					Iterator<TagCompound> i = inventory.iterator();
					while(i.hasNext()){
						TagCompound t = i.next();
						int slot = t.getInteger("slot");
						ItemStack stack = ItemStack.fromNBT(t);
						p.setInventory(slot, stack);
					}
				}
				List<TagFloat> pos = tag.getList("Pos", TagFloat.class);
				p.setPos(new Vector3f(pos.get(0).getValue(),pos.get(1).getValue(),pos.get(2).getValue()));
				List<TagFloat> rot = tag.getList("Rot", TagFloat.class);
				p.setRot(new Vector3f(rot.get(0).getValue(),rot.get(1).getValue(),rot.get(2).getValue()));
				List<TagFloat> vel = tag.getList("Vel", TagFloat.class);
				p.setVel(new Vector3f(vel.get(0).getValue(),vel.get(1).getValue(),vel.get(2).getValue()));
			}
			inputStream = new FileInputStream(Constants.GLCRAFTDIR+"saves/"+name+"/entities.nbt");
			nbtInputStream = new NbtInputStream(inputStream);
			tag = (TagCompound)nbtInputStream.readTag();
			List<TagCompound> l = tag.getList("Entities", TagCompound.class);
			Iterator<TagCompound> i = l.iterator();
			while(i.hasNext()){
				TagCompound t = i.next();
				Entity e = null;
				e = NBTUtil.readEntity(t, worldManager);
				if(e != null){
					worldManager.spawnEntity(e);
				}else{
					//System.err.println("WARNING: ENTITY IN SAVE WAS NULL.");
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unknown error loading the world.", "Error", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	private static boolean upgradeWorld(Save s) throws IOException {
		while(!s.format.equals(currentFormat)){
			if(s.format.equals(formatV1)){
				Constants.world.initSplashText();
				for(int chunk = 0; chunk < 1000; chunk++){
					Constants.world.renderSplashText("Upgrading world...", "V1 to V2", (int) (((float)chunk/1000f)*100));
					FileInputStream inputStream = new FileInputStream(new File(s.getFolder(),"chunks/chunk"+chunk+".nbt"));
					NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
					TagCompound tag = (TagCompound) nbtInputStream.readTag();
					byte[] buf = new byte[(int) Math.pow(Constants.CHUNKSIZE,3)];
					int i = 0;
					for(int x = 0; x < Constants.CHUNKSIZE; x++){
						for(int y = 0; y < Constants.CHUNKSIZE; y++){
							for(int z = 0; z < Constants.CHUNKSIZE; z++){
								buf[i] = 0;
								i++;
							}
						}
					}
					TagByteArray meta = new TagByteArray("meta", buf);
					tag.setTag(meta);
					nbtInputStream.close();
					NbtOutputStream out = new NbtOutputStream(new FileOutputStream(new File(s.getFolder(),"chunks/chunk"+chunk+".nbt")));
					out.write(tag);
					out.close();
				}
				s.format = formatV2;
			}else{
				return false;
			}
		}
		writeMetadata(s);
		return true;
	}

	private static void writeMetadata(Save save) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(new File(Constants.GLCRAFTDIR+"saves/"+save.name+"/world.nbt"));
		NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);
		TagCompound t = new TagCompound("level");
		TagString nameTag = new TagString("name",save.dispName);
		t.setTag(nameTag);
		TagString versionTag = new TagString("version", GLCraft.version);
		t.setTag(versionTag);
		TagString formatTag = new TagString("format", currentFormat);
		t.setTag(formatTag);
		save.timestamp = new Date().getTime();
		TagLong timeTag = new TagLong("timestamp", save.timestamp);
		t.setTag(timeTag);
		nbtOutputStream.write(t);
		nbtOutputStream.close();
	}
	
	public static Save getSave(String name) throws IOException{
		if(new File(Constants.GLCRAFTDIR+"saves/"+name+"/world.nbt").exists()){
			FileInputStream inputStream = new FileInputStream(Constants.GLCRAFTDIR+"saves/"+name+"/world.nbt");
			NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
			TagCompound tag = (TagCompound)nbtInputStream.readTag();
			nbtInputStream.close();
			long timestamp = 0;
			if(tag.getTag("timestamp") != null)
				timestamp = tag.getLong("timestamp");
			return new Save(name,tag.getString("name"),tag.getString("version"),tag.getString("format"),timestamp);
		}else if(new File(Constants.GLCRAFTDIR+"saves/"+name+"/player.nbt").exists()){ //No metadata file, check if player.nbt exists
			return new Save(name,name,"?",formatV0);
		}else{
			return null;
		}
	}
	
	public static Save[] getSaves() throws IOException{
		ArrayList<Save> saves = new ArrayList<Save>();
		File savesDir = new File(Constants.GLCRAFTDIR+"saves/");
		if(savesDir.exists()){
			for(File f : savesDir.listFiles()){
				Save s = getSave(f.getName());
				if(s != null)
					saves.add(getSave(f.getName()));
			}
		}
		Collections.sort(saves);
		return (Save[]) saves.toArray(new Save[saves.size()]);
	}
}
