package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class PacketSetInventory extends Packet{
	public byte[] invid;
	public byte[] invmeta;
	public int[] invcount;
	public boolean[] invisTile;
	public boolean[] invisNull;
	public int entityID;
	public PacketSetInventory(EntityPlayer p){
		invid = new byte[p.getInventory().length];
		invmeta = new byte[p.getInventory().length];
		invcount = new int[p.getInventory().length];
		invisTile = new boolean[p.getInventory().length];
		invisNull = new boolean[p.getInventory().length];
		for(int i = 0; i < p.getInventory().length; i++){
			if(p.getInventory(i) == null || p.getInventory(i).isNull()){
				invisNull[i] = true;
			}else{
				invid[i] = p.getInventory(i).getId();
				invmeta[i] = p.getInventory(i).getMeta();
				invcount[i] = p.getInventory(i).count;
				invisTile[i] = p.getInventory(i).isTile();
				invisNull[i] = false;
			}
		}
		this.entityID = p.getID();
	}
	public void setInventory(WorldManager w){
		Entity e = w.getEntityManager().getEntity(this.entityID);
		if(e != null && e instanceof EntityPlayer){
			for(int i = 0; i < invid.length; i++){
				if(!invisNull[i])
					if(invisTile[i])
						((EntityPlayer) e).setInventoryNoUpdate(i, new ItemStack(Tile.getTile(invid[i]), invcount[i], invmeta[i]));
					else
						((EntityPlayer) e).setInventoryNoUpdate(i, new ItemStack(Item.getItem(invid[i]), invcount[i], invmeta[i]));
				else
					((EntityPlayer) e).setInventoryNoUpdate(i, new ItemStack());
			}
		}
	}
}
