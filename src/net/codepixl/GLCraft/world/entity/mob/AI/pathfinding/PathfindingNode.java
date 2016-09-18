package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Vector3i;

public class PathfindingNode{
	public Vector3i pos;
	public PathfindingNode parent, dest, start;
	public float distance = 0, startDistance = 0;
	
	public PathfindingNode(Vector3i pos, PathfindingNode parent, PathfindingNode start, PathfindingNode dest){
		this.pos = pos;
		this.parent = parent;
		this.dest = dest;
		this.start = start;
		if(dest != null)
			this.distance = MathUtils.distance(dest.pos.toVector3f(), pos.toVector3f());
		if(start != null)
			this.startDistance = MathUtils.distance(start.pos.toVector3f(), pos.toVector3f());
	}
	
	@Override
	public boolean equals(Object cmp){
		return cmp instanceof PathfindingNode && ((PathfindingNode)cmp).pos.equals(pos);
	}
}
