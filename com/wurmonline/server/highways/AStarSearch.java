// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

public class AStarSearch
{
    protected static List<Route> constructPath(final AStarNode startNode, AStarNode node) {
        final LinkedList<Route> path = new LinkedList<Route>();
        final LinkedList<AStarNode> nodes = new LinkedList<AStarNode>();
        while (node.pathParent != null) {
            if (nodes.contains(node)) {
                return null;
            }
            nodes.addFirst(node);
            path.addFirst(node.pathRoute);
            node = node.pathParent;
        }
        return path;
    }
    
    public static List<Route> findPath(final AStarNode startNode, final AStarNode goalNode, final byte initialDir) {
        final PriorityList openList = new PriorityList();
        final LinkedList<AStarNode> closedList = new LinkedList<AStarNode>();
        startNode.costFromStart = 0.0f;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        startNode.pathRoute = null;
        openList.add(startNode);
        byte checkDir = initialDir;
        while (!openList.isEmpty()) {
            final AStarNode node = openList.removeFirst();
            if (node == goalNode) {
                return constructPath(startNode, goalNode);
            }
            final ConcurrentHashMap<Byte, Route> routesMap = node.getRoutes(checkDir);
            for (final Map.Entry<Byte, Route> entry : routesMap.entrySet()) {
                final Route route = entry.getValue();
                final AStarNode neighbourNode = route.getEndNode();
                final boolean isOpen = openList.contains(neighbourNode);
                final boolean isClosed = closedList.contains(neighbourNode);
                final float costFromStart = node.costFromStart + route.getCost();
                if ((!isOpen && !isClosed) || costFromStart < neighbourNode.costFromStart) {
                    neighbourNode.pathParent = node;
                    neighbourNode.costFromStart = costFromStart;
                    neighbourNode.estimatedCostToGoal = neighbourNode.getEstimatedCost(goalNode);
                    neighbourNode.pathRoute = route;
                    if (isClosed) {
                        closedList.remove(neighbourNode);
                    }
                    if (isOpen) {
                        continue;
                    }
                    openList.add(neighbourNode);
                }
            }
            closedList.add(node);
            checkDir = 0;
        }
        return null;
    }
    
    public static class PriorityList extends LinkedList<AStarNode>
    {
        private static final long serialVersionUID = 1L;
        
        public void add(final Comparable<AStarNode> object) {
            for (int i = 0; i < this.size(); ++i) {
                if (object.compareTo(this.get(i)) <= 0) {
                    this.add(i, (AStarNode)object);
                    return;
                }
            }
            this.addLast((AStarNode)object);
        }
    }
}
