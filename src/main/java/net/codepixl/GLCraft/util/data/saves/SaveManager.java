package net.codepixl.GLCraft.util.data.saves;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.*;
import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIServerError;
import net.codepixl.GLCraft.network.packet.PacketPlayerPos;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.FileUtil;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.Vector2i;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WeatherState;
import net.codepixl.GLCraft.world.WeatherType;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.item.ItemStack;
import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SaveManager {
	
	public static String formatV0 = "GLCWorldv0";
	public static String formatV1 = "GLCWorldv1";
	public static String formatV2 = "GLCWorldv2";
	public static String formatV3 = "GLCWorldv3";
	public static String currentFormat = formatV3;
	
	public static boolean saveWorld(final WorldManager worldManager, final Save save){
		if(!worldManager.isServer) //Only servers should save worlds
			return false;
    	GLogger.log("Saving world "+save+"...", LogSource.SERVER);
    	
		save.name = save.name.replaceAll("[^ a-zA-Z0-9.-]", "_");
		save.worldTime = worldManager.getWorldTime();
		
		worldManager.setSaving(true);
		Runnable r = new Runnable(){
			@Override
			public void run() {
				try{
					
					//PLAYER SAVING
					for(Entity p : worldManager.getEntityManager().getEntities(EntityPlayer.class))
						savePlayer(worldManager, (EntityPlayer)p);
					
					//WORLD SAVING
					worldManager.saveChunks(save);
					
					//ENTITY SAVING
					worldManager.getEntityManager().save(save);
					
					//METADATA SAVING
					writeMetadata(save, worldManager);
					
				}catch(IOException e){
					e.printStackTrace();
				}
				worldManager.setSaving(false);
			}
		};
		
		new Thread(r).start();
		return true;
	}

	public static boolean loadWorld(WorldManager worldManager, Save save) {
		if(!worldManager.isServer) //Only servers should load worlds
			return false;
		try {
			FileInputStream inputStream;
			NbtInputStream nbtInputStream;
			TagCompound tag;
			
			//METADATA LOADING
			if(save != null){
				if(!save.format.equals(currentFormat)){
					if(!upgradeWorld(save, worldManager)){
						if(!GLCraft.getGLCraft().isServer())
							GUIManager.getMainManager().showGUI(new GUIServerError("Error loading world:","Unsupported world format "+save.format));
						return false;
					}
				}
			}else{
				if(!GLCraft.getGLCraft().isServer())
					GUIManager.getMainManager().showGUI(new GUIServerError("Error loading world:\n","Unknown Error"));
				return false;
			}
			
			//EntityPlayer p = worldManager.getEntityManager().getPlayer();
			
			//WORLD LOADING
			worldManager.loadChunks(save);
			if(save.weatherState != null) save.weatherState.worldManager = worldManager; else save.weatherState = new WeatherState(WeatherType.CLEAR, worldManager);
			
			//ENTITY & PLAYER LOADING
			worldManager.entityManager.removeAll();
			inputStream = new FileInputStream(save.getDirectory()+File.separator+"entities.nbt");
			nbtInputStream = new NbtInputStream(inputStream);
			tag = (TagCompound)nbtInputStream.readTag();
			List<TagCompound> l = tag.getList("Entities", TagCompound.class);
			Iterator<TagCompound> i = l.iterator();
			while(i.hasNext()){
				TagCompound t = i.next();
				Entity e = null;
				try{
					e = NBTUtil.readEntity(t, worldManager);
				}catch(final Exception ex){
					JOptionPane.showInternalMessageDialog(null, "There was an error loading an "+e.getClass()+".\nYour world may not load as expected.\nPlease report this bug and upload the log saved at\n"+Constants.GLCRAFTDIR+"GLCraft.log", "Error loading Entity", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
				if(e != null){
					worldManager.spawnEntity(e);
				}else{
					//System.err.println("WARNING: ENTITY IN SAVE WAS NULL.");
				}
			}
			nbtInputStream.close();
			inputStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if(!GLCraft.getGLCraft().isServer())
			GUIManager.getMainManager().showGUI(new GUIServerError("Error loading world:\n","Unknown Error"));
		worldManager.closeWorld("Error", false);
		return false;
	}

	private static boolean upgradeWorld(Save s, WorldManager worldManager) throws IOException {
		while(!s.format.equals(currentFormat)){
			if(s.format.equals(formatV1)){
				for(int chunk = 0; chunk < 1000; chunk++){
					FileInputStream inputStream = new FileInputStream(new File(s.getDirectory(),"chunks/chunk"+chunk+".nbt"));
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
					FileOutputStream fos = new FileOutputStream(new File(s.getDirectory(),"chunks/chunk"+chunk+".nbt"));
					NbtOutputStream out = new NbtOutputStream(fos);
					out.write(tag);
					out.close();
					fos.close();
					inputStream.close();
				}
				s.format = formatV2;
			}else if(s.format.equals(formatV2)){
				HashMap<Vector2i, TagCompound> regions = new HashMap<Vector2i, TagCompound>();
				for(int chunk = 0; chunk < 1000; chunk++){
					FileInputStream inputStream = new FileInputStream(new File(s.getDirectory(),"chunks/chunk"+chunk+".nbt"));
					NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
					TagCompound tag = (TagCompound) nbtInputStream.readTag();
					nbtInputStream.close();
					TagCompound posT = tag.getCompound("pos");
					Vector3f pos = new Vector3f();
					pos.x = posT.getFloat("x");
					pos.y = posT.getFloat("y");
					pos.z = posT.getFloat("z");
					Vector2i reg = new Vector2i((int)Math.floor(pos.x/16f/32f),(int)Math.floor(pos.z/16f/32f));
					if(!regions.containsKey(reg))
						regions.put(reg, new TagCompound(reg.toString()));
					tag.setName("chunk"+pos.toString().replace("Vector3f", ""));
					regions.get(reg).setTag(tag);
					inputStream.close();
				}
				Iterator<Entry<Vector2i, TagCompound>> i = regions.entrySet().iterator();
				new File(s.getDirectory(),"region/").mkdirs();
				while(i.hasNext()){
					Entry<Vector2i, TagCompound> next = i.next();
					FileOutputStream fos = new FileOutputStream(new File(s.getDirectory(),"region/r"+next.getKey().toString().replace("[","").replace("]","").replace(',', '.')+".nbt"));
					NbtOutputStream out = new NbtOutputStream(fos);
					out.write(next.getValue());
					out.close();
					fos.close();
				}
			    FileUtil.deleteDirectory(new File(s.getDirectory(),"chunks"));
				s.format = formatV3;
			}else{
				return false;
			}
		}
		writeMetadata(s, worldManager);
		return true;
	}
	
	public static void loadPlayer(WorldManager w, EntityPlayerMP p){
		try{
			File playerFile;
			if(GLCraft.getGLCraft().getWorldManager(false) != null && GLCraft.getGLCraft().getWorldManager(false).getPlayer().equals(p))
				playerFile = new File(w.currentSave.getDirectory(),"player.nbt");
			else
				playerFile = new File(w.currentSave.getDirectory(),"players"+File.separator+p.getName().toLowerCase()+".nbt");
			p.setInventory(new ItemStack[p.getInventorySize()]);
			for(int i = 0; i < p.getInventorySize(); i++){
				p.setInventory(i, new ItemStack());
			}

			if(!playerFile.exists()){
				p.updatedInventory = true;
				return;
			}
			FileInputStream inputStream = new FileInputStream(playerFile);
			NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
			TagCompound tag = (TagCompound)nbtInputStream.readTag();
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
			p.updatedInventory = true;
			w.sendPacket(new PacketPlayerPos(p));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void savePlayer(WorldManager w, EntityPlayer p){
		try{
			File playerFile;
			if(GLCraft.getGLCraft().getWorldManager(false) != null && GLCraft.getGLCraft().getWorldManager(false).getPlayer().equals(p))
				playerFile = new File(w.currentSave.getDirectory(),"player.nbt");
			else
				playerFile = new File(w.currentSave.getDirectory(),"players"+File.separator+p.getName().toLowerCase()+".nbt");
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
			new File(w.currentSave.getDirectory(),"players").mkdirs();
			FileOutputStream outputStream;
			outputStream = new FileOutputStream(playerFile);
			NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);
			nbtOutputStream.write(compound);
			nbtOutputStream.close();
			w.sendPacket(new PacketPlayerPos(p));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void writeMetadata(Save save, WorldManager worldManager) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(new File(save.getDirectory(),"world.nbt"));
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
		TagLong worldTimeTag = new TagLong("worldTime", save.worldTime);
		t.setTag(worldTimeTag);
		TagByteArray weatherState = new TagByteArray("weatherState", SerializationUtils.serialize(worldManager.currentWeather));
		t.setTag(weatherState);
		if(save.hasSeed){
			TagLong seed = new TagLong("seed", save.seed);
			t.setTag(seed);
		}
		nbtOutputStream.write(t);
		nbtOutputStream.close();
		outputStream.close();
	}
	
	public static Save getSave(String name) throws IOException{
		if(new File(Constants.GLCRAFTDIR+"saves/"+name+"/world.nbt").exists()){
			try{
				FileInputStream inputStream = new FileInputStream(Constants.GLCRAFTDIR+"saves/"+name+"/world.nbt");
				NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
				TagCompound tag = (TagCompound)nbtInputStream.readTag();
				nbtInputStream.close();
				long timestamp = 0;
				timestamp = tag.getLong("timestamp");
				long worldTime = Constants.dayLengthMS/2;
				WeatherState weatherState = null;
				try{
					worldTime = tag.getLong("worldTime");
					weatherState = (WeatherState) SerializationUtils.deserialize(tag.getByteArray("weatherState"));
				}catch(TagNotFoundException | NullPointerException | ClassCastException e){
					
				}
				inputStream.close();
				Save s = new Save(name,tag.getString("name"),tag.getString("version"),tag.getString("format"),timestamp,worldTime,weatherState);
				try{
					s.seed = tag.getLong("seed");
					s.hasSeed = true;
				}catch(TagNotFoundException e){}
				return s;
			}catch(TagNotFoundException | NullPointerException e){
				System.err.println("WARNING: world "+name+" is corrupted!");
				return null;
			}
			
		}else if(new File(Constants.GLCRAFTDIR+"saves/"+name+"/player.nbt").exists()){ //No metadata file, check if player.nbt exists
			Save s = new Save(name,name,"?",formatV0,0);
			s.hasSeed = false;
			return s;
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

	public static Save getDedicatedSave() throws IOException {
		if(new File("world","world.nbt").exists()){
			FileInputStream inputStream = new FileInputStream("world"+File.separator+"world.nbt");
			NbtInputStream nbtInputStream = new NbtInputStream(inputStream);
			TagCompound tag = (TagCompound)nbtInputStream.readTag();
			nbtInputStream.close();
			long timestamp = 0;
			timestamp = tag.getLong("timestamp");
			long worldTime = Constants.dayLengthMS/2;
			WeatherState weatherState = null;
			try{
				worldTime = tag.getLong("worldTime");
				weatherState = (WeatherState) SerializationUtils.deserialize(tag.getByteArray("weatherState"));
			}catch(TagNotFoundException | NullPointerException | ClassCastException e){
				
			}
			inputStream.close();
			return new Save("world",tag.getString("name"),tag.getString("version"),tag.getString("format"),timestamp,worldTime,true,weatherState);
		}else{
			Save s = new Save("world","world","?",formatV0,0);
			s.isDedicated = true;
			s.hasSeed = false;
			return s;
		}
	}
}
