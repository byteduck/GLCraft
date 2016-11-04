package net.codepixl.GLCraft.world.entity.mob.hostile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.AI.AIFollowNearest;
import net.codepixl.GLCraft.world.entity.mob.AI.AIHurtNearest;
import net.codepixl.GLCraft.world.entity.mob.AI.AIWander;
import net.codepixl.GLCraft.world.entity.mob.AI.pathfinding.Pathfinder;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityTestHostile extends Hostile{

	private Vector3f lastpos;

	public EntityTestHostile(Vector3f pos, WorldManager w) {
		super(pos, w);
		this.lastpos = new Vector3f(0,0,0);
		this.addAI(new AIFollowNearest(this, EntityPlayer.class));
		this.addAI(new AIWander(this));
		this.addAI(new AIHurtNearest(this, EntityPlayer.class));
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		GL11.glRotatef(this.rot.y, 0f, 2.0f, 0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(-0.5f,0,-0.5f, this.getColor(), Tile.Lava.getTexCoords(), 1f);
		//Shape.createCube(-0.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		//Shape.createCube(-1.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		//Shape.createCube(0.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		GL11.glEnd();
		GL11.glPopMatrix();
		if(worldManager.centralManager.renderDebug){
			Pathfinder p = ((AIFollowNearest)this.getAI(AIFollowNearest.class)).getPathfinder();
			if(p != null){
				p.renderPath();
			}
		}
			
	}

}
