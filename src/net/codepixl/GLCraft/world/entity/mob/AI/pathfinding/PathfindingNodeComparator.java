package net.codepixl.GLCraft.world.entity.mob.AI.pathfinding;

import java.util.Comparator;

public class PathfindingNodeComparator implements Comparator<PathfindingNode>{
    @Override
    public int compare(PathfindingNode x, PathfindingNode y){
        return (int) ((x.distance + x.startDistance) - (y.distance + y.startDistance));
    }
}