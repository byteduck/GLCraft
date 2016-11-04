package net.codepixl.GLCraft.world.tile.tick;

public class TickHelper {
	boolean updated = false;
	int tick = 0;
	int rate;
	public TickHelper(int rate){
		this.rate = rate;
	}
	
	public void setUpdated(boolean updated){
		this.updated = updated;
	}
	
	public boolean needsTick(){
		return tick>=rate;
	}
	
	public void cycle(){
		if(!updated){
			updated = true;
			if(tick>=rate){
				tick=0;
			}
			tick++;
		}
	}
	
	public int getRate(){
		return rate;
	}
	
}
