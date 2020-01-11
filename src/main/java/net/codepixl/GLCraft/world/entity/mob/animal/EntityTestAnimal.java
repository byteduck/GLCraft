package net.codepixl.GLCraft.world.entity.mob.animal;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityFallingBlock;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.entity.mob.AI.AIRunAway;
import net.codepixl.GLCraft.world.entity.mob.AI.AIWander;
import net.codepixl.GLCraft.world.entity.mob.hostile.Hostile;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class EntityTestAnimal extends Animal {
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
		Shape.createCube(-0.5f, 0, -0.5f, this.getColor(), Tile.Glass.getTexCoords(), 1f);
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		return new EntityTestAnimal(new Vector3f(pos.x, pos.y, pos.z), w);
	}
}
