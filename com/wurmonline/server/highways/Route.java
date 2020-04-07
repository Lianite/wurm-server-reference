// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import java.util.Iterator;
import java.util.List;
import com.wurmonline.server.players.Player;
import java.util.Collection;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import java.util.LinkedList;

public class Route
{
    private final Node startNode;
    private final byte direction;
    private final int id;
    private final LinkedList<Item> catseyes;
    private Node endNode;
    private float cost;
    private float distance;
    private Route oppositeRoute;
    
    public Route(final Node startNode, final byte direction, final int id) {
        this.catseyes = new LinkedList<Item>();
        this.endNode = null;
        this.cost = 0.0f;
        this.distance = 0.0f;
        this.oppositeRoute = null;
        this.startNode = startNode;
        this.direction = direction;
        this.id = id;
        this.addCost(startNode.getWaystone(), direction);
    }
    
    public Node getStartNode() {
        return this.startNode;
    }
    
    public byte getDirection() {
        return this.direction;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Nullable
    public Route getOppositeRoute() {
        return this.oppositeRoute;
    }
    
    void SetOppositeRoute(final Route oppositeRoute) {
        this.oppositeRoute = oppositeRoute;
    }
    
    public void AddCatseye(final Item catseye, final boolean atFront, final byte direction) {
        if (atFront) {
            this.catseyes.addFirst(catseye);
        }
        else {
            this.catseyes.add(catseye);
        }
        this.addCost(catseye, direction);
    }
    
    private void addCost(final Item marker, final byte direction) {
        float thiscost = 1.0f;
        if (direction == 2 || direction == 8 || direction == 32 || direction == -128) {
            thiscost = 1.414f;
        }
        this.distance += thiscost;
        if (!marker.isOnSurface()) {
            thiscost *= 1.1f;
        }
        if (marker.getBridgeId() != -10L) {
            thiscost *= 1.05f;
        }
        this.cost += thiscost;
    }
    
    public Item[] getCatseyes() {
        return this.catseyes.toArray(new Item[this.catseyes.size()]);
    }
    
    public LinkedList<Item> getCatseyesList() {
        return this.catseyes;
    }
    
    public LinkedList<Item> getCatseyesListCopy() {
        return new LinkedList<Item>(this.catseyes);
    }
    
    public boolean containsCatseye(final Item catseye) {
        return this.catseyes.contains(catseye);
    }
    
    public float getCost() {
        return this.cost;
    }
    
    public float getDistance() {
        return this.distance;
    }
    
    public void AddEndNode(final Node node) {
        this.endNode = node;
    }
    
    @Nullable
    public Node getEndNode() {
        return this.endNode;
    }
    
    public final short isOnHighwayPath(final Player player) {
        final List<Route> highwayPath = player.getHighwayPath();
        if (highwayPath != null) {
            for (int x = 0; x < highwayPath.size(); ++x) {
                if (highwayPath.get(x) == this) {
                    float distance = 0.0f;
                    for (int y = x; y < highwayPath.size(); ++y) {
                        distance += highwayPath.get(y).getDistance();
                    }
                    return (short)distance;
                }
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("{route:" + this.id);
        buf.append(" Start Waystone:" + this.startNode.toString());
        buf.append(" End waystone:");
        if (this.endNode != null) {
            buf.append(this.endNode.getWaystone().toString());
        }
        else {
            buf.append("missing");
        }
        boolean first = true;
        for (final Item catseye : this.catseyes) {
            if (first) {
                first = false;
                buf.append(" Catseyes:{");
            }
            else {
                buf.append(",");
            }
            buf.append(catseye.toString());
        }
        if (!first) {
            buf.append("}");
        }
        buf.append("}");
        return buf.toString();
    }
}
