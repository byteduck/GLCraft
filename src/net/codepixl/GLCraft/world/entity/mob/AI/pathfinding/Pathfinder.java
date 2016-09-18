package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;

public class Pathfinder{
	public PriorityQueue<PathfindingNode> frontier = new PriorityQueue<PathfindingNode>(1, new PathfindingNodeComparator());
	public ArrayList<PathfindingNode> visited = new ArrayList<PathfindingNode>();
	public ArrayList<Vector3i> path = new ArrayList<Vector3i>();
	public PathfinderProvider<Vector3i> provider;
	public Vector3i target, pos;
	public Pathfinder(Vector3i pos, Vector3i target, WorldManager w){
		this.provider = new GLCraftPathfinderProvider(w);
		this.target = target;
		this.pos = pos;
	}
	
	public boolean pathfind(int maxNodes){
		boolean done = false;
		frontier.clear();
		path.clear();
		PathfindingNode startNode = new PathfindingNode(pos, null, null, new PathfindingNode(target, null, null, null));
		startNode.start = startNode;
		frontier.add(startNode);
		PathfindingNode endNode = null;
		int nodes = 0;
		while(!done && frontier.size() > 0 && nodes <= maxNodes){
			nodes++;
			PathfindingNode frontierNode = frontier.remove();
			visited.add(frontierNode);
			for(PathfindingNode node : provider.getNeighborNodes(frontierNode, visited)){
				if(node.pos.equals(target)){
					done = true;
					endNode = node;
				}else{
					frontier.add(node);
				}
			}
		}
		if(endNode != null){
			path.add(endNode.pos);
			PathfindingNode cnode = endNode;
			while(cnode.parent != null){
				path.add(cnode.parent.pos);
				cnode = cnode.parent;
			}
			Collections.reverse(path);
		}
		return endNode != null;
	}
}
