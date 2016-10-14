package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class PacketPlayerAction extends Packet {
	
	public enum Type{
		DROPHELDITEM, DROPOTHERITEM, SELECTSLOT;
	}
	
	public Type type;
	public byte id;
	public byte meta;
	public boolean isTile;
	public int count;
	public boolean all;
	
	private PacketPlayerAction(EntityPlayer p, Type type){
		this.type = type;
	}
	
	public static PacketPlayerAction dropHeldItem(EntityPlayer p, boolean all){
		PacketPlayerAction pa = new PacketPlayerAction(p,Type.DROPHELDITEM);
		pa.all = all;
		return pa;
	}
	
	public static PacketPlayerAction dropOtherItem(EntityPlayer p, ItemStack stack){
		PacketPlayerAction pa = new PacketPlayerAction(p,Type.DROPOTHERITEM);
		pa.id = stack.getId();
		pa.meta = stack.getMeta();
		pa.isTile = stack.isTile();
		pa.count = stack.count;
		return pa;
	}
	
	public static PacketPlayerAction selectSlot(EntityPlayer p){
		PacketPlayerAction pa = new PacketPlayerAction(p,Type.SELECTSLOT);
		pa.count = p.getSelectedSlot();
		return pa;
	}
	
}
