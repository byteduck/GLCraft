package net.codepixl.GLCraft.world.entity.mob.animal;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.AI.AIRunAway;
import net.codepixl.GLCraft.world.entity.mob.AI.AIWander;
import net.codepixl.GLCraft.world.entity.mob.hostile.Hostile;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityTestAnimal extends Animal{

	public EntityTestAnimal(Vector3f pos, WorldManager w) {
		super(pos, w);
		this.addAI(new AIRunAway(this, Hostile.class));
		this.addAI(new AIWander(this));
	}
	
	@Override
	public void render(){
		GL11.glEnd();
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		GL11.glRotatef(this.rot.y, 0f, 2.0f, 0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(-0.5f, 0, -0.5f, Color4f.WHITE, Tile.Glass.getTexCoords(), 1f);
		GL11.glEnd();
		GL11.glPopMatrix();
	}

}
