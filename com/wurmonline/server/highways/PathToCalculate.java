// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import java.util.Iterator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Wagoner;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import javax.annotation.Nullable;
import java.util.List;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.constants.HighwayConstants;

public class PathToCalculate implements HighwayConstants
{
    private final Creature creature;
    private final Node startNode;
    private final Village destinationVillage;
    private List<Route> bestPath;
    private byte checkDir;
    private float bestDistance;
    private float bestCost;
    
    PathToCalculate(@Nullable final Creature creature, final Node startNode, @Nullable final Village village, final byte checkDir) {
        this.bestPath = null;
        this.checkDir = 0;
        this.bestDistance = 99999.0f;
        this.bestCost = 99999.0f;
        this.creature = creature;
        this.startNode = startNode;
        this.destinationVillage = village;
        this.checkDir = checkDir;
    }
    
    void calculate() {
        if (this.destinationVillage == null) {
            final Village[] villages = Routes.getVillages();
            final HashSet<Distanced> distanceSet = new HashSet<Distanced>();
            for (final Village village : villages) {
                if (village != this.startNode.getVillage()) {
                    final int dx = this.startNode.getWaystone().getTileX() - village.getTokenX();
                    final int dy = this.startNode.getWaystone().getTileY() - village.getTokenY();
                    final int crowfly = (int)Math.sqrt(dx * dx + dy * dy);
                    distanceSet.add(new Distanced(village, crowfly));
                }
            }
            if (distanceSet.size() == 0) {
                return;
            }
            final Distanced[] distanced = distanceSet.toArray(new Distanced[distanceSet.size()]);
            Arrays.sort(distanced, new Comparator<Distanced>() {
                @Override
                public int compare(final Distanced o1, final Distanced o2) {
                    return Integer.compare(o1.distance, o2.distance);
                }
            });
            if (this.checkDir == 0) {
                this.calcClosest(distanced, (byte)1);
                this.calcClosest(distanced, (byte)2);
                this.calcClosest(distanced, (byte)4);
                this.calcClosest(distanced, (byte)8);
                this.calcClosest(distanced, (byte)16);
                this.calcClosest(distanced, (byte)32);
                this.calcClosest(distanced, (byte)64);
                this.calcClosest(distanced, (byte)(-128));
            }
            else {
                this.calcClosest(distanced, this.checkDir);
            }
            this.startNode.getWaystone().updateModelNameOnGroundItem();
        }
        else {
            this.calculate(this.destinationVillage, this.checkDir);
        }
    }
    
    void calcClosest(final Distanced[] distanced, final byte dir) {
        final Route route = this.startNode.getRoute(dir);
        if (route == null) {
            return;
        }
        List<Route> closestPath = null;
        float closestDistance = 99999.0f;
        float closestCost = 99999.0f;
        Village closestVillage = null;
        for (final Distanced distance : distanced) {
            if (distance.getDistance() > closestDistance) {
                break;
            }
            this.calculate(distance.getVillage(), dir);
            if (this.bestPath != null && this.bestCost < closestCost) {
                closestPath = this.bestPath;
                closestDistance = this.bestDistance;
                closestCost = this.bestCost;
                closestVillage = distance.getVillage();
            }
        }
        if (closestPath != null) {
            final short distanceTiles = (short)closestDistance;
            this.startNode.addClosestVillage(dir, closestVillage.getName(), distanceTiles);
        }
        else {
            final Node endNode = route.getEndNode();
            final Item waystone = endNode.getWaystone();
            final String wagonerName = Wagoner.getWagonerNameFrom(waystone.getWurmId());
            if (wagonerName.length() > 0) {
                this.startNode.addClosestVillage(dir, wagonerName, (short)route.getDistance());
            }
            else {
                this.startNode.addClosestVillage(dir, "", (short)0);
            }
        }
    }
    
    void calculate(final Village village, final byte initialDir) {
        this.bestPath = null;
        this.bestCost = 99999.0f;
        this.bestDistance = 99999.0f;
        int pno = 1;
        int bestno = 0;
        for (final Node goalNode : Routes.getNodesFor(village)) {
            final List<Route> path = AStarSearch.findPath(this.startNode, goalNode, initialDir);
            if (path != null && !path.isEmpty()) {
                float cost = 0.0f;
                float distance = 0.0f;
                for (final Route route : path) {
                    final float ncost = route.getCost();
                    cost += ncost;
                    final float ndistance = route.getDistance();
                    distance += ndistance;
                }
                if (this.bestPath == null || cost < this.bestCost) {
                    this.bestCost = cost;
                    this.bestPath = path;
                    bestno = pno;
                    this.bestDistance = distance;
                }
                ++pno;
            }
        }
        if (this.creature != null) {
            final String oldDestination = this.creature.getHighwayPathDestination();
            this.creature.setHighwayPath(village.getName(), this.bestPath);
            if (this.bestPath != null && !this.bestPath.isEmpty()) {
                this.creature.setLastWaystoneChecked(this.bestPath.get(0).getStartNode().getWaystone().getWurmId());
            }
            else {
                this.creature.setLastWaystoneChecked(-10L);
            }
            if (this.creature.isPlayer()) {
                if (this.creature.getPower() > 1) {
                    Routes.queuePlayerMessage((Player)this.creature, pno + " route" + ((pno != 1) ? "s" : "") + " checked." + ((bestno > 0) ? (" Best route found was number " + bestno + " and its cost is " + this.bestCost + ".") : " No routes found!"));
                }
                else if (this.bestPath == null) {
                    Routes.queuePlayerMessage((Player)this.creature, "No routes found to " + village.getName() + "!");
                }
                else if (!oldDestination.equals(village.getName())) {
                    Routes.queuePlayerMessage((Player)this.creature, "Route found to " + village.getName() + "!");
                }
            }
        }
    }
    
    public static final List<Route> getRoute(final long startWaystoneId, final long endWaystoneId) {
        final Node startNode = Routes.getNode(startWaystoneId);
        final Node endNode = Routes.getNode(endWaystoneId);
        if (startNode == null || endNode == null) {
            return null;
        }
        final List<Route> path = AStarSearch.findPath(startNode, endNode, (byte)0);
        if (path != null && !path.isEmpty()) {
            return path;
        }
        return null;
    }
    
    public static final float getRouteDistance(final long startWaystoneId, final long endWaystoneId) {
        final List<Route> path = getRoute(startWaystoneId, endWaystoneId);
        if (path != null) {
            float distance = 0.0f;
            for (final Route route : path) {
                distance += route.getDistance();
            }
            return distance;
        }
        return 99999.0f;
    }
    
    public static final boolean isVillageConnected(final long startWaystoneId, final Village village) {
        final Node startNode = Routes.getNode(startWaystoneId);
        if (startNode == null) {
            return false;
        }
        for (final Node goalNode : Routes.getNodesFor(village)) {
            final List<Route> path = AStarSearch.findPath(startNode, goalNode, (byte)0);
            if (path != null && !path.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean isWaystoneConnected(final long startWaystoneId, final long endWaystoneId) {
        final Node startNode = Routes.getNode(startWaystoneId);
        if (startNode == null) {
            return false;
        }
        final Node endNode = Routes.getNode(endWaystoneId);
        if (endNode == null) {
            return false;
        }
        final List<Route> path = AStarSearch.findPath(startNode, endNode, (byte)0);
        return path != null && !path.isEmpty();
    }
    
    class Distanced
    {
        private final Village village;
        private final int distance;
        
        Distanced(final Village village, final int distance) {
            this.village = village;
            this.distance = distance;
        }
        
        Village getVillage() {
            return this.village;
        }
        
        int getDistance() {
            return this.distance;
        }
    }
}
