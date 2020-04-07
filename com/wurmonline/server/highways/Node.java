// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.items.Item;

public class Node extends AStarNode
{
    private final Item waystone;
    private Village village;
    final ConcurrentHashMap<Byte, Route> routes;
    final ConcurrentHashMap<Byte, AStarNode> neighbours;
    final ConcurrentHashMap<Byte, ClosestVillage> pointers;
    
    public Node(final Item waystone) {
        this.village = null;
        this.routes = new ConcurrentHashMap<Byte, Route>();
        this.neighbours = new ConcurrentHashMap<Byte, AStarNode>();
        this.pointers = new ConcurrentHashMap<Byte, ClosestVillage>();
        this.waystone = waystone;
    }
    
    public long getWurmId() {
        return this.waystone.getWurmId();
    }
    
    public Item getWaystone() {
        return this.waystone;
    }
    
    public void setVillage(final Village village) {
        this.village = village;
    }
    
    public int getRouteCount() {
        return this.routes.size();
    }
    
    public Village getVillage() {
        return this.village;
    }
    
    public void AddRoute(final byte direction, final Route route) {
        this.routes.put(direction, route);
        this.neighbours.put(direction, route.getEndNode());
    }
    
    public void addClosestVillage(final byte direction, final String name, final short distance) {
        this.pointers.put(direction, new ClosestVillage(name, distance));
    }
    
    @Nullable
    public ClosestVillage getClosestVillage(final byte direction) {
        return this.pointers.get(direction);
    }
    
    @Nullable
    public Route getRoute(final byte direction) {
        return this.routes.get(direction);
    }
    
    public byte getNodeDir(final Node node) {
        byte bestdir = 0;
        float bestCost = 99999.0f;
        for (final Map.Entry<Byte, Route> entry : this.routes.entrySet()) {
            final Route route = entry.getValue();
            final byte dir = entry.getKey();
            if ((route.getEndNode() == node || route.getStartNode() == node) && route.getCost() < bestCost) {
                bestCost = route.getCost();
                bestdir = dir;
            }
        }
        return bestdir;
    }
    
    public boolean removeRoute(final Route oldRoute) {
        for (final Map.Entry<Byte, Route> entry : this.routes.entrySet()) {
            if (entry.getValue() == oldRoute) {
                this.routes.remove(entry.getKey());
            }
        }
        for (final Map.Entry<Byte, AStarNode> entry2 : this.neighbours.entrySet()) {
            if (entry2.getValue() == oldRoute.getEndNode()) {
                this.neighbours.remove(entry2.getKey());
            }
        }
        return this.village == null && this.routes.isEmpty();
    }
    
    @Override
    public float getCost(final AStarNode node) {
        final Route route = this.findRoute(node);
        if (route != null) {
            return route.getCost();
        }
        return 99999.0f;
    }
    
    public float getDistance(final AStarNode node) {
        final Route route = this.findRoute(node);
        if (route != null) {
            return route.getDistance();
        }
        return 99999.0f;
    }
    
    @Override
    public float getEstimatedCost(final AStarNode node) {
        final Route route = this.findRoute(node);
        if (route != null) {
            final int diffx = Math.abs(this.waystone.getTileX() - ((Node)node).waystone.getTileX());
            final int diffy = Math.abs(this.waystone.getTileY() - ((Node)node).waystone.getTileY());
            return diffx + diffy;
        }
        return 99999.0f;
    }
    
    @Nullable
    private Route findRoute(final AStarNode node) {
        for (final Map.Entry<Byte, AStarNode> entry : this.neighbours.entrySet()) {
            if (entry.getValue() == node) {
                return this.routes.get(entry.getKey());
            }
        }
        return null;
    }
    
    @Override
    public List<AStarNode> getNeighbours(final byte dir) {
        final ArrayList<AStarNode> alist = new ArrayList<AStarNode>();
        if (dir != 0) {
            final Route route = this.getRoute(dir);
            if (route != null && route.getEndNode() != null) {
                alist.add(this.neighbours.get(dir));
            }
        }
        else {
            for (final Map.Entry<Byte, AStarNode> entry : this.neighbours.entrySet()) {
                if (!alist.contains(entry.getValue())) {
                    alist.add(entry.getValue());
                }
            }
        }
        return alist;
    }
    
    @Override
    public ConcurrentHashMap<Byte, Route> getRoutes(final byte dir) {
        if (dir != 0) {
            final ConcurrentHashMap<Byte, Route> lroutes = new ConcurrentHashMap<Byte, Route>();
            final Route route = this.routes.get(dir);
            if (route != null) {
                lroutes.put(dir, route);
            }
            return lroutes;
        }
        return this.routes;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("{Node:" + this.waystone.getWurmId());
        boolean first = true;
        for (final Map.Entry<Byte, Route> entry : this.routes.entrySet()) {
            if (first) {
                first = false;
                buf.append("{");
            }
            else {
                buf.append(",");
            }
            buf.append(" {Dir:");
            buf.append(MethodsHighways.getLinkDirString(entry.getKey()));
            buf.append(",Cost:");
            buf.append(entry.getValue().getCost());
            buf.append(",Route:");
            buf.append(entry.getValue().getId());
            buf.append("}");
        }
        if (!first) {
            buf.append("}");
        }
        buf.append("}");
        return buf.toString();
    }
}
