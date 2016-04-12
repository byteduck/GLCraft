package net.codepixl.GLCraft.world.entity.mob.animal;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityTestAnimal extends Animal{

	public EntityTestAnimal(Vector3f pos, WorldManager w) {
		super(pos, w);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void render(){
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(pos.x-0.5f, pos.y, pos.z-0.5f, Color4f.WHITE, Tile.Glass.getTexCoords(), 1f);
		GL11.glEnd();
	}
	
	@Override
	public void handleAI(){
		Vector3f bPos = new Vector3f(pos);
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 20f);
		if(e.get(0) != null){
			Vector3f ppos = e.get(0).getPos();
			if(pos.x > ppos.x) this.move( -0.25f * (float)Time.getDelta() * 15, 0, 0); else this.move( 0.25f * (float)Time.getDelta() * 15, 0, 0);
			if(pos.z > ppos.z) this.move(0, 0, -0.25f * (float)Time.getDelta() * 15); else this.move(0, 0, 0.25f * (float)Time.getDelta() * 15);
			this.jump();
		}
	}

}
