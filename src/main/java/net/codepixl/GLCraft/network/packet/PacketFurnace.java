package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;

public class PacketFurnace extends Packet {

	private static final long serialVersionUID = 8671414568566918249L;
	
	public float progress, fuelTime;
	public boolean cooking;
	public int entityID, progressPercent, fuelPercent;
	public PacketFurnace(TileEntityFurnace f){
		this.progress = f.getProgress();
		this.fuelTime = f.getFuelTime();
		this.cooking = f.isCooking();
		this.progressPercent = f.getProgressPercentServer();
		this.fuelPercent = f.getFuelPercentServer();
		this.entityID = f.getID();
	}
	public void updateFurnace(WorldManager w){
		Entity e = w.getEntity(entityID);
		if(e != null && e instanceof TileEntityFurnace)
			((TileEntityFurnace)e).update(this);
	}
}
