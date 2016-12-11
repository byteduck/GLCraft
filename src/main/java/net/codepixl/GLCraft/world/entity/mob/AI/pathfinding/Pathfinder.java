package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Pathfinder{
	public PriorityQueue<PathfindingNode> frontier = new PriorityQueue<PathfindingNode>(1, new PathfindingNodeComparator());
	public ArrayList<PathfindingNode> visited = new ArrayList<PathfindingNode>();
	public ArrayList<Vector3f> path = new ArrayList<Vector3f>();
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
			path.add(endNode.pos.toVector3f());
			PathfindingNode cnode = endNode;
			while(cnode.parent != null){
				path.add(new Vector3f(cnode.parent.pos.x+.5f, cnode.parent.pos.y, cnode.parent.pos.z+.5f));
				cnode = cnode.parent;
			}
			Collections.reverse(path);
		}
		return endNode != null;
	}
	
	public void renderPath(){
		if(path.size() > 0){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glLineWidth(5);
			GL11.glColor3f(0f, 0f, 1f);
			Vector3f prevPos = new Vector3f(path.get(0).x, path.get(0).y+.5f, path.get(0).z);
			GL11.glBegin(GL11.GL_LINES);
			for(int i = 0; i < path.size(); i++){
				GL11.glVertex3f(prevPos.x, prevPos.y, prevPos.z);
				Vector3f thisPos = new Vector3f(path.get(i).x, path.get(i).y+.5f, path.get(i).z);
				GL11.glVertex3f(thisPos.x, thisPos.y, thisPos.z);
				prevPos = thisPos;
			}
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
}
