package net.codepixl.GLCraft.util;

import java.util.HashMap;
import java.util.Map;

public enum EnumFacing {
	
	DOWN(0), UP(1), NORTH(2), SOUTH(3), EAST(4), WEST(5), DOWNN(6), DOWNS(7), DOWNE(8), DOWNW(9), UPN(10), UPS(11), UPE(12), UPW(13);
	//North - -z
	//East - +x
	
	public final int value;
	private static final Map<Integer, EnumFacing> lookup = new HashMap<Integer, EnumFacing>();
	private EnumFacing(int value){
		this.value = value;
	}
	static{
        for (EnumFacing f : EnumFacing.values()){
        	EnumFacing.lookup.put(f.value, f);
        }
    }
	public EnumFacing removeUpDown(){
		if(this.value >= DOWNN.value && this.value < UPN.value)
			return lookup.get(this.value-4);
		else if(this.value >= UPN.value && this.value <= UPW.value)
			return lookup.get(this.value-8);
		else if(this == UP || this == DOWN)
			return NORTH;
		else
			return this;
	}
	public EnumFacing inverse(){
		switch(this){
			case DOWN:
				return UP;
			case UP:
				return DOWN;
			case NORTH:
				return SOUTH;
			case SOUTH:
				return NORTH;
			case EAST:
				return WEST;
			case WEST:
				return EAST;
			case DOWNN:
				return UPS;
			case DOWNS:
				return UPN;
			case DOWNE:
				return UPW;
			case DOWNW:
				return UPE;
			case UPN:
				return DOWNS;
			case UPS:
				return DOWNN;
			case UPE:
				return DOWNW;
			case UPW:
				return DOWNE;
			default:
				return NORTH;
		}
	}
}
