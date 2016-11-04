package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityContainer;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class PacketContainer extends Packet{
	public byte[] invid;
	public byte[] invmeta;
	public int[] invcount;
	public boolean[] invisTile;
	public boolean[] invisNull;
	public int entityID;
	public PacketContainer(TileEntityContainer c){
		invid = new byte[c.getInventory().length];
		invmeta = new byte[c.getInventory().length];
		invcount = new int[c.getInventory().length];
		invisTile = new boolean[c.getInventory().length];
		invisNull = new boolean[c.getInventory().length];
		for(int i = 0; i < c.getInventory().length; i++){
			if(c.getInventory(i) == null || c.getInventory(i).isNull()){
				invisNull[i] = true;
			}else{
				invid[i] = c.getInventory(i).getId();
				invmeta[i] = c.getInventory(i).getMeta();
				invcount[i] = c.getInventory(i).count;
				invisTile[i] = c.getInventory(i).isTile();
				invisNull[i] = false;
			}
		}
		this.entityID = c.getID();
	}
	public void setInventory(WorldManager w){
		Entity e = w.getEntityManager().getEntity(this.entityID);
		if(e != null && e instanceof TileEntityContainer){
			for(int i = 0; i < invid.length; i++){
				if(!invisNull[i])
					if(invisTile[i])
						((TileEntityContainer) e).setInventoryNoUpdate(i, new ItemStack(Tile.getTile(invid[i]), invcount[i], invmeta[i]));
					else
						((TileEntityContainer) e).setInventoryNoUpdate(i, new ItemStack(Item.getItem(invid[i]), invcount[i], invmeta[i]));
				else
					((TileEntityContainer) e).setInventoryNoUpdate(i, new ItemStack());
			}
		}
	}
}
