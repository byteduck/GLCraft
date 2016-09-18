package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;

public class GLCraftPathfinderProvider implements PathfinderProvider<Vector3i>{
	
	WorldManager worldManager;
	
	public GLCraftPathfinderProvider(WorldManager w){
		this.worldManager = w;
	}
	
	@Override
	public boolean isValidPos(Vector3i pos) {
		Tile t = Tile.getTile((byte) worldManager.getTileAtPos(pos.toVector3f()));
		return t != Tile.Void && t.canPassThrough() && !Tile.getTile((byte)worldManager.getTileAtPos(new Vector3f(pos.x, pos.y-1, pos.z))).canPassThrough();
	}

	@Override
	public ArrayList<PathfindingNode> getNeighborNodes(PathfindingNode node, ArrayList<PathfindingNode> visited) {
		ArrayList<PathfindingNode> ret = new ArrayList<PathfindingNode>();
		getNeighborNodesMain(node, visited, ret, 0);
		getNeighborNodesMain(node, visited, ret, 1);
		getNeighborNodesMain(node, visited, ret, -1);
		return ret;
	}
	
	private void getNeighborNodesMain(PathfindingNode node, ArrayList<PathfindingNode> visited, ArrayList<PathfindingNode> ret, int y){
		Vector3i pos = new Vector3i(node.pos.x+1, node.pos.y+y, node.pos.z);
		if(!visited.contains(new PathfindingNode(pos, null, null, null)) && isValidPos(pos)){
			ret.add(new PathfindingNode(pos, node, node.start, node.dest));
		}
		pos = new Vector3i(node.pos.x-1, node.pos.y+y, node.pos.z);
		if(!visited.contains(new PathfindingNode(pos, null, null, null)) && isValidPos(pos)){
			ret.add(new PathfindingNode(pos, node, node.start, node.dest));
		}
		pos = new Vector3i(node.pos.x, node.pos.y+y, node.pos.z+1);
		if(!visited.contains(new PathfindingNode(pos, null, null, null)) && isValidPos(pos)){
			ret.add(new PathfindingNode(pos, node, node.start, node.dest));
		}
		pos = new Vector3i(node.pos.x, node.pos.y+y, node.pos.z-1);
		if(!visited.contains(new PathfindingNode(pos, null, null, null)) && isValidPos(pos)){
			ret.add(new PathfindingNode(pos, node, node.start, node.dest));
		}
	}

}
