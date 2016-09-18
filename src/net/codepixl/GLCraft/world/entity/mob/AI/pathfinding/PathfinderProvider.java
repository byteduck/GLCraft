package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import java.util.ArrayList;

public interface PathfinderProvider<T> {
	public boolean isValidPos(T pos);
	public ArrayList<PathfindingNode> getNeighborNodes(PathfindingNode node, ArrayList<PathfindingNode> visited);
}
