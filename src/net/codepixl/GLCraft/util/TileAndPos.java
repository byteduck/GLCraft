package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.world.tile.Tile;

public class TileAndPos {
	public final Vector3f pos;
	public final Tile tile;
	public TileAndPos(Tile t, Vector3f pos){
		this.pos = new Vector3f(pos);
		tile = t;
	}
	public TileAndPos(Tile t, int x, int y, int z){
		pos = new Vector3f(x,y,z);
		tile = t;
	}
}
