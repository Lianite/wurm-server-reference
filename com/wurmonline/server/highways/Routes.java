// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import com.wurmonline.server.players.Player;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.webinterface.WcTrelloHighway;
import com.wurmonline.server.support.Trello;
import java.util.Map;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Players;
import java.util.logging.Level;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Items;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.HighwayConstants;

public class Routes implements HighwayConstants
{
    private static Logger logger;
    private static int nextId;
    private static ConcurrentHashMap<Integer, Route> allRoutes;
    private static ConcurrentHashMap<Long, Node> allNodes;
    private static final ConcurrentLinkedDeque<PlayerMessageToSend> playerMessagesToSend;
    
    public static final void generateAllRoutes() {
        Routes.logger.info("Calculating All routes.");
        final long start = System.nanoTime();
        for (final Item waystone : Items.getWaystones()) {
            makeNodeFrom(waystone);
        }
        for (final Item waystone : Items.getWaystones()) {
            checkForRoutes(waystone, false, null);
        }
        for (final Item waystone : Items.getWaystones()) {
            final Node startNode = getNode(waystone);
            HighwayFinder.queueHighwayFinding(null, startNode, null, (byte)0);
        }
        Routes.logger.log(Level.INFO, "Calculated " + Routes.allRoutes.size() + " routes and " + Routes.allNodes.size() + " nodes.That took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        Players.getInstance().sendGmMessage(null, "Roads", "Calculated " + Routes.allRoutes.size() + " routes and " + Routes.allNodes.size() + " nodes. That took " + (System.nanoTime() - start) / 1000000.0f + " ms.", false);
    }
    
    private static final boolean checkForRoutes(final Item waystone, final boolean tellGms, final Item marker) {
        boolean foundRoute = false;
        foundRoute |= checkForRoute(waystone, (byte)1, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)2, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)4, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)8, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)16, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)32, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)64, tellGms, marker);
        foundRoute |= checkForRoute(waystone, (byte)(-128), tellGms, marker);
        return foundRoute;
    }
    
    @Nullable
    private static final boolean checkForRoute(final Item waystone, final byte checkdir, final boolean tellGms, final Item planted) {
        if (!MethodsHighways.hasLink(waystone.getAuxData(), checkdir)) {
            return false;
        }
        final Node startNode = getNode(waystone);
        if (startNode.getRoute(checkdir) != null) {
            return false;
        }
        HighwayPos highwayPos = MethodsHighways.getHighwayPos(waystone);
        final Route newRoute = new Route(startNode, checkdir, Routes.nextId);
        boolean checking = true;
        byte linkdir = checkdir;
        while (checking) {
            final int lastx = highwayPos.getTilex();
            final int lasty = highwayPos.getTiley();
            final boolean lastSurf = highwayPos.isOnSurface();
            final long lastbp = highwayPos.getBridgeId();
            final int lastfl = highwayPos.getFloorLevel();
            final byte lastdir = linkdir;
            highwayPos = MethodsHighways.getNewHighwayPosLinked(highwayPos, linkdir);
            final Item marker = MethodsHighways.getMarker(highwayPos);
            if (marker == null) {
                Routes.logger.warning("Lost! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",Surface:" + lastSurf + ",bp:" + lastbp + ",fl:" + lastfl);
                return false;
            }
            final byte fromdir = MethodsHighways.getOppositedir(linkdir);
            if (!MethodsHighways.hasLink(marker.getAuxData(), fromdir)) {
                Routes.logger.info("Missing Link! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",bp:" + lastbp + ",fl:" + lastfl + "  to:x" + highwayPos.getTilex() + ",y:" + highwayPos.getTiley() + "Surf:" + highwayPos.isOnSurface() + ",bp:" + highwayPos.getBridgeId() + ",fl:" + highwayPos.getFloorLevel());
                return false;
            }
            if (marker.getTemplateId() == 1114) {
                final byte todir = MethodsHighways.getOtherdir(marker.getAuxData(), fromdir);
                newRoute.AddCatseye(marker, false, todir);
                if (MethodsHighways.numberOfSetBits(todir) != 1) {
                    if (Servers.isThisATestServer()) {
                        Routes.logger.info("End of road! @" + marker.getTileX() + "," + marker.getTileY() + " (from:" + MethodsHighways.getLinkAsString(fromdir) + ",to:" + MethodsHighways.getLinkAsString(todir) + ")");
                    }
                    return false;
                }
                linkdir = todir;
            }
            else {
                if (marker.getTemplateId() == 1112) {
                    final Node endNode = getNode(marker);
                    newRoute.AddEndNode(endNode);
                    startNode.AddRoute(checkdir, newRoute);
                    Routes.allRoutes.put(newRoute.getId(), newRoute);
                    final LinkedList<Item> catseyes = new LinkedList<Item>();
                    for (final Item catseye : newRoute.getCatseyesList()) {
                        catseyes.addFirst(catseye);
                    }
                    byte backdir = fromdir;
                    final Route backRoute = new Route(endNode, backdir, ++Routes.nextId);
                    for (final Item catseye2 : catseyes) {
                        final byte oppdir = MethodsHighways.getOppositedir(backdir);
                        backRoute.AddCatseye(catseye2, false, oppdir);
                        backdir = MethodsHighways.getOtherdir(catseye2.getAuxData(), oppdir);
                    }
                    backRoute.AddEndNode(startNode);
                    endNode.AddRoute(fromdir, backRoute);
                    Routes.allRoutes.put(backRoute.getId(), backRoute);
                    newRoute.SetOppositeRoute(backRoute);
                    backRoute.SetOppositeRoute(newRoute);
                    if (tellGms) {
                        waystone.updateModelNameOnGroundItem();
                        for (final Item catseye3 : newRoute.getCatseyes()) {
                            catseye3.updateModelNameOnGroundItem();
                        }
                        marker.updateModelNameOnGroundItem();
                        HighwayFinder.queueHighwayFinding(null, startNode, null, checkdir);
                        HighwayFinder.queueHighwayFinding(null, endNode, null, fromdir);
                    }
                    ++Routes.nextId;
                    checking = false;
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    public static final Node getNode(final Item waystone) {
        final Node node = Routes.allNodes.get(waystone.getWurmId());
        if (node != null) {
            return node;
        }
        return makeNodeFrom(waystone);
    }
    
    private static final Node makeNodeFrom(final Item waystone) {
        final Node node = new Node(waystone);
        final VolaTile vt = Zones.getTileOrNull(waystone.getTileX(), waystone.getTileY(), waystone.isOnSurface());
        if (vt != null && vt.getVillage() != null) {
            node.setVillage(vt.getVillage());
        }
        Routes.allNodes.put(waystone.getWurmId(), node);
        return node;
    }
    
    public static final void remove(final Item marker) {
        if (marker.getTemplateId() == 1114) {
            for (final Map.Entry<Integer, Route> entry : Routes.allRoutes.entrySet()) {
                if (entry.getValue().containsCatseye(marker)) {
                    removeRoute(entry.getValue(), marker);
                    break;
                }
            }
        }
        else {
            final Node node = Routes.allNodes.remove(marker.getWurmId());
            if (node != null) {
                removeRoute(node, (byte)1, marker);
                removeRoute(node, (byte)2, marker);
                removeRoute(node, (byte)4, marker);
                removeRoute(node, (byte)8, marker);
                removeRoute(node, (byte)16, marker);
                removeRoute(node, (byte)32, marker);
                removeRoute(node, (byte)64, marker);
                removeRoute(node, (byte)(-128), marker);
            }
        }
    }
    
    private static final void removeRoute(final Node node, final byte checkdir, final Item marker) {
        final Route route = node.getRoute(checkdir);
        if (route != null) {
            removeRoute(route, marker);
        }
    }
    
    private static final void removeRoute(final Route route, final Item marker) {
        final Node nodeStart = route.getStartNode();
        final Node nodeEnd = route.getEndNode();
        boolean doCatseyes = nodeStart.removeRoute(route);
        Routes.allRoutes.remove(route.getId());
        final Route oppRoute = route.getOppositeRoute();
        if (oppRoute != null) {
            final Node oppStart = oppRoute.getStartNode();
            doCatseyes |= oppStart.removeRoute(oppRoute);
            Routes.allRoutes.remove(oppRoute.getId());
        }
        if (doCatseyes) {
            nodeStart.getWaystone().updateModelNameOnGroundItem();
            for (final Item catseye : route.getCatseyes()) {
                catseye.updateModelNameOnGroundItem();
            }
            if (nodeEnd != null) {
                nodeEnd.getWaystone().updateModelNameOnGroundItem();
            }
        }
        if (!marker.isReplacing()) {
            String whatHappened = marker.getWhatHappened();
            if (whatHappened.length() == 0) {
                whatHappened = "unknown";
            }
            final StringBuffer ttl = new StringBuffer();
            ttl.append(marker.getName());
            ttl.append(" @");
            ttl.append(marker.getTileX());
            ttl.append(",");
            ttl.append(marker.getTileY());
            ttl.append(",");
            ttl.append(marker.isOnSurface());
            ttl.append(" ");
            ttl.append(whatHappened);
            final String title = ttl.toString();
            final StringBuffer dsc = new StringBuffer();
            dsc.append("Routes removed between ");
            dsc.append(nodeStart.getWaystone().getTileX());
            dsc.append(",");
            dsc.append(nodeStart.getWaystone().getTileY());
            dsc.append(",");
            dsc.append(nodeStart.getWaystone().isOnSurface());
            dsc.append(" and ");
            if (nodeEnd != null) {
                dsc.append(nodeEnd.getWaystone().getTileX());
                dsc.append(",");
                dsc.append(nodeEnd.getWaystone().getTileY());
                dsc.append(",");
                dsc.append(nodeEnd.getWaystone().isOnSurface());
            }
            else {
                dsc.append(" end node missing!");
            }
            final String description = dsc.toString();
            sendToTrello(title, description);
        }
    }
    
    public static final void sendToTrello(final String title, final String description) {
        Players.getInstance().sendGmMessage(null, "Roads", title, false);
        if (Servers.isThisLoginServer()) {
            Trello.addHighwayMessage(Servers.localServer.getAbbreviation(), title, description);
        }
        else {
            final WcTrelloHighway wtc = new WcTrelloHighway(title, description);
            wtc.sendToLoginServer();
        }
    }
    
    public static final boolean checkForNewRoutes(final Item marker) {
        if (marker.getTemplateId() == 1112) {
            getNode(marker);
            return checkForRoutes(marker, true, marker);
        }
        if (MethodsHighways.numberOfSetBits(marker.getAuxData()) == 2) {
            final byte startdir = getStartdir(marker);
            if (startdir != 0) {
                final Set<Item> markersDone = new HashSet<Item>();
                HighwayPos highwayPos = MethodsHighways.getHighwayPos(marker);
                boolean checking = true;
                byte linkdir = startdir;
                while (checking) {
                    final int lastx = highwayPos.getTilex();
                    final int lasty = highwayPos.getTiley();
                    final boolean lastSurf = highwayPos.isOnSurface();
                    final long lastbp = highwayPos.getBridgeId();
                    final int lastfl = highwayPos.getFloorLevel();
                    final byte lastdir = linkdir;
                    highwayPos = MethodsHighways.getNewHighwayPosLinked(highwayPos, linkdir);
                    final Item nextMarker = MethodsHighways.getMarker(highwayPos);
                    if (nextMarker == null) {
                        Routes.logger.warning("Dead End! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",Surface:" + lastSurf + ",bp:" + lastbp + ",fl:" + lastfl);
                        return false;
                    }
                    if (markersDone.contains(nextMarker)) {
                        Routes.logger.warning("Circular! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",Surface:" + lastSurf + ",bp:" + lastbp + ",fl:" + lastfl);
                        return false;
                    }
                    markersDone.add(nextMarker);
                    final byte fromdir = MethodsHighways.getOppositedir(linkdir);
                    if (MethodsHighways.numberOfSetBits(fromdir) != 1) {
                        Routes.logger.warning("Lost! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",Surface:" + lastSurf + ",bp:" + lastbp + ",fl:" + lastfl);
                        return false;
                    }
                    if (!MethodsHighways.hasLink(nextMarker.getAuxData(), fromdir)) {
                        Routes.logger.info("Missing Link! " + MethodsHighways.getLinkAsString(lastdir) + " from:x:" + lastx + ",y:" + lasty + ",bp:" + lastbp + ",fl:" + lastfl + "  to:x" + highwayPos.getTilex() + ",y:" + highwayPos.getTiley() + "Surf:" + highwayPos.isOnSurface() + ",bp:" + highwayPos.getBridgeId() + ",fl:" + highwayPos.getFloorLevel());
                        return false;
                    }
                    if (nextMarker.getTemplateId() == 1114) {
                        final byte todir = MethodsHighways.getOtherdir(nextMarker.getAuxData(), fromdir);
                        if (MethodsHighways.numberOfSetBits(todir) != 1) {
                            if (Servers.isThisATestServer()) {
                                Routes.logger.info("End of road! @" + nextMarker.getTileX() + "," + nextMarker.getTileY() + " (from:" + MethodsHighways.getLinkAsString(fromdir) + ",to:" + MethodsHighways.getLinkAsString(todir) + ")");
                            }
                            return false;
                        }
                        linkdir = todir;
                    }
                    else {
                        if (nextMarker.getTemplateId() == 1112) {
                            checking = false;
                            return checkForRoute(nextMarker, fromdir, true, marker);
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    private static final byte getStartdir(final Item marker) {
        byte startdir = 0;
        final byte dirs = marker.getAuxData();
        if (MethodsHighways.hasLink(dirs, (byte)1)) {
            startdir = 1;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)2)) {
            startdir = 2;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)4)) {
            startdir = 4;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)8)) {
            startdir = 8;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)16)) {
            startdir = 16;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)32)) {
            startdir = 32;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)64)) {
            startdir = 64;
        }
        else if (MethodsHighways.hasLink(dirs, (byte)(-128))) {
            startdir = -128;
        }
        return startdir;
    }
    
    public static final Item[] getMarkers() {
        final ConcurrentHashMap<Long, Item> markers = new ConcurrentHashMap<Long, Item>();
        for (final Route route : Routes.allRoutes.values()) {
            final Item waystone = route.getStartNode().getWaystone();
            markers.put(waystone.getWurmId(), waystone);
            for (final Item catseye : route.getCatseyes()) {
                markers.put(catseye.getWurmId(), catseye);
            }
            final Node node = route.getEndNode();
            if (node != null) {
                markers.put(node.getWaystone().getWurmId(), node.getWaystone());
            }
        }
        return markers.values().toArray(new Item[markers.size()]);
    }
    
    public static final Item[] getRouteMarkers(final Item marker) {
        final ConcurrentHashMap<Long, Item> markers = new ConcurrentHashMap<Long, Item>();
        if (marker.getTemplateId() == 1114) {
            for (final Route route : Routes.allRoutes.values()) {
                if (route.containsCatseye(marker)) {
                    final Item startWaystone = route.getStartNode().getWaystone();
                    markers.put(startWaystone.getWurmId(), startWaystone);
                    for (final Item catseye : route.getCatseyes()) {
                        markers.put(catseye.getWurmId(), catseye);
                    }
                    final Item endWaystone = route.getEndNode().getWaystone();
                    markers.put(endWaystone.getWurmId(), endWaystone);
                    break;
                }
            }
        }
        else {
            for (final Route route : Routes.allRoutes.values()) {
                if (route.getStartNode().getWurmId() == marker.getWurmId()) {
                    final Item startWaystone = route.getStartNode().getWaystone();
                    markers.put(startWaystone.getWurmId(), startWaystone);
                    for (final Item catseye : route.getCatseyes()) {
                        markers.put(catseye.getWurmId(), catseye);
                    }
                    final Item endWaystone = route.getEndNode().getWaystone();
                    markers.put(endWaystone.getWurmId(), endWaystone);
                }
                if (route.getEndNode().getWurmId() == marker.getWurmId()) {
                    final Item startWaystone = route.getStartNode().getWaystone();
                    markers.put(startWaystone.getWurmId(), startWaystone);
                    for (final Item catseye : route.getCatseyes()) {
                        markers.put(catseye.getWurmId(), catseye);
                    }
                    final Item endWaystone = route.getEndNode().getWaystone();
                    markers.put(endWaystone.getWurmId(), endWaystone);
                }
            }
        }
        return markers.values().toArray(new Item[markers.size()]);
    }
    
    public static final boolean isCatseyeUsed(final Item catseye) {
        for (final Route route : Routes.allRoutes.values()) {
            if (route.containsCatseye(catseye)) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean isMarkerUsed(final Item marker) {
        if (marker.getTemplateId() == 1114) {
            return isCatseyeUsed(marker);
        }
        for (final Route route : Routes.allRoutes.values()) {
            if (route.getStartNode().getWaystone().getWurmId() == marker.getWurmId()) {
                return true;
            }
            if (route.getEndNode() != null && route.getEndNode().getWaystone().getWurmId() == marker.getWurmId()) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    public static final Route getRoute(final int id) {
        return Routes.allRoutes.get(id);
    }
    
    @Nullable
    public static final Node getNode(final long wurmId) {
        return Routes.allNodes.get(wurmId);
    }
    
    public static final Route[] getAllRoutes() {
        return Routes.allRoutes.values().toArray(new Route[Routes.allRoutes.size()]);
    }
    
    public static final Node[] getAllNodes() {
        return Routes.allNodes.values().toArray(new Node[Routes.allNodes.size()]);
    }
    
    public static final Village[] getVillages() {
        final ConcurrentHashMap<Integer, Village> villages = new ConcurrentHashMap<Integer, Village>();
        for (final Node node : Routes.allNodes.values()) {
            final Village vill = node.getVillage();
            if (vill != null && vill.isHighwayFound()) {
                villages.put(vill.getId(), vill);
            }
        }
        return villages.values().toArray(new Village[villages.size()]);
    }
    
    public static final Village[] getVillages(final long waystoneId) {
        final ConcurrentHashMap<Integer, Village> villages = new ConcurrentHashMap<Integer, Village>();
        for (final Node node : Routes.allNodes.values()) {
            final Village vill = node.getVillage();
            if (vill != null && vill.isHighwayFound() && PathToCalculate.isVillageConnected(waystoneId, vill)) {
                villages.put(vill.getId(), vill);
            }
        }
        return villages.values().toArray(new Village[villages.size()]);
    }
    
    public static final Node[] getNodesFor(final Village village) {
        final ConcurrentHashMap<Long, Node> nodes = new ConcurrentHashMap<Long, Node>();
        for (final Node node : Routes.allNodes.values()) {
            final Village vill = node.getVillage();
            if (vill != null && vill.equals(village)) {
                nodes.put(node.getWaystone().getWurmId(), node);
            }
        }
        return nodes.values().toArray(new Node[nodes.size()]);
    }
    
    public static final void handlePathsToSend() {
        for (PlayerMessageToSend playerMessageToSend = Routes.playerMessagesToSend.pollFirst(); playerMessageToSend != null; playerMessageToSend = Routes.playerMessagesToSend.pollFirst()) {
            playerMessageToSend.send();
        }
    }
    
    public static final void queuePlayerMessage(final Player player, final String text) {
        Routes.playerMessagesToSend.add(new PlayerMessageToSend(player, text));
    }
    
    static {
        Routes.logger = Logger.getLogger(Routes.class.getName());
        Routes.nextId = 1;
        Routes.allRoutes = new ConcurrentHashMap<Integer, Route>();
        Routes.allNodes = new ConcurrentHashMap<Long, Node>();
        playerMessagesToSend = new ConcurrentLinkedDeque<PlayerMessageToSend>();
    }
}
