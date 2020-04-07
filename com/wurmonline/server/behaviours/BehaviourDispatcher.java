// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.tutorial.Mission;
import com.wurmonline.server.tutorial.Missions;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.mesh.CaveTile;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.structures.Fence;
import java.util.logging.Level;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import java.util.Collection;
import com.wurmonline.server.utils.StringUtil;
import java.util.ArrayList;
import com.wurmonline.server.Server;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.items.Item;
import java.util.LinkedList;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import java.util.List;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.shared.constants.CounterTypes;

public final class BehaviourDispatcher implements CounterTypes, ItemTypes, MiscConstants
{
    private static final Logger logger;
    private static List<ActionEntry> availableActions;
    private static final List<ActionEntry> emptyActions;
    
    public static void requestSelectionActions(final Creature creature, final Communicator comm, final byte requestId, long subject, final long target) throws NoSuchBehaviourException, NoSuchPlayerException, NoSuchCreatureException, NoSuchItemException, NoSuchWallException {
        if (!creature.isTeleporting()) {
            Item item = null;
            BehaviourDispatcher.availableActions = null;
            if (WurmId.getType(subject) == 8 || WurmId.getType(subject) == 18 || WurmId.getType(subject) == 32) {
                subject = -1L;
            }
            final int targetType = WurmId.getType(target);
            final Behaviour behaviour = Action.getBehaviour(target, creature.isOnSurface());
            final boolean onSurface = Action.getIsOnSurface(target, creature.isOnSurface());
            Label_0135: {
                if (subject != -1L) {
                    if (WurmId.getType(subject) != 2 && WurmId.getType(subject) != 6 && WurmId.getType(subject) != 19) {
                        if (WurmId.getType(subject) != 20) {
                            break Label_0135;
                        }
                    }
                    try {
                        item = Items.getItem(subject);
                    }
                    catch (NoSuchItemException nsi) {
                        subject = -10L;
                        item = null;
                    }
                }
            }
            if (targetType == 3) {
                final RequestParam param = requestActionForTiles(creature, target, onSurface, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 1 || targetType == 0) {
                final RequestParam param = requestActionForCreaturesPlayers(creature, target, item, targetType, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 2 || targetType == 6 || targetType == 19 || targetType == 20) {
                final RequestParam param = requestActionForItemsBodyIdsCoinIds(creature, target, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 5) {
                final RequestParam param = requestActionForWalls(creature, target, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 7) {
                final RequestParam param = requestActionForFences(creature, target, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 12) {
                final RequestParam param = requestActionForTileBorder(creature, target, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 17) {
                final RequestParam param = requestActionForCaveTiles(creature, target, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 23) {
                final RequestParam param = requestActionForFloors(creature, target, onSurface, item, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else if (targetType == 24) {
                final RequestParam param = requestActionForIllusions(creature, target, item, targetType, behaviour);
                param.filterForSelectBar();
                sendRequestResponse(requestId, comm, param, true);
            }
            else {
                final RequestParam param = new RequestParam(new LinkedList<ActionEntry>(), "");
                sendRequestResponse(requestId, comm, param, true);
            }
        }
        else {
            comm.sendAlertServerMessage("You are teleporting and cannot perform actions right now.");
        }
    }
    
    public static void requestActions(final Creature creature, final Communicator comm, final byte requestId, long subject, final long target) throws NoSuchPlayerException, NoSuchCreatureException, NoSuchItemException, NoSuchBehaviourException, NoSuchWallException {
        if (!creature.isTeleporting()) {
            Item item = null;
            BehaviourDispatcher.availableActions = null;
            if (WurmId.getType(subject) == 8 || WurmId.getType(subject) == 18 || WurmId.getType(subject) == 32) {
                subject = -1L;
            }
            final int targetType = WurmId.getType(target);
            final Behaviour behaviour = Action.getBehaviour(target, creature.isOnSurface());
            final boolean onSurface = Action.getIsOnSurface(target, creature.isOnSurface());
            Label_0135: {
                if (subject != -1L) {
                    if (WurmId.getType(subject) != 2 && WurmId.getType(subject) != 6 && WurmId.getType(subject) != 19) {
                        if (WurmId.getType(subject) != 20) {
                            break Label_0135;
                        }
                    }
                    try {
                        item = Items.getItem(subject);
                    }
                    catch (NoSuchItemException nsi) {
                        subject = -10L;
                        item = null;
                    }
                }
            }
            if (targetType == 3) {
                final RequestParam param = requestActionForTiles(creature, target, onSurface, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 1 || targetType == 0) {
                final RequestParam param = requestActionForCreaturesPlayers(creature, target, item, targetType, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 2 || targetType == 6 || targetType == 19 || targetType == 20) {
                final RequestParam param = requestActionForItemsBodyIdsCoinIds(creature, target, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 5) {
                final RequestParam param = requestActionForWalls(creature, target, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 7) {
                final RequestParam param = requestActionForFences(creature, target, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 8 || targetType == 32) {
                requestActionForWounds(creature, comm, requestId, target, item, behaviour);
            }
            else if (targetType == 12) {
                final RequestParam param = requestActionForTileBorder(creature, target, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 14) {
                requestActionForPlanets(creature, comm, requestId, target, item, behaviour);
            }
            else if (targetType == 30) {
                requestActionForMenu(creature, comm, requestId, target, behaviour);
            }
            else if (targetType == 17) {
                final RequestParam param = requestActionForCaveTiles(creature, target, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 18) {
                requestActionForSkillIds(comm, requestId, target);
            }
            else if (targetType == 23) {
                final RequestParam param = requestActionForFloors(creature, target, onSurface, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 22) {
                requestActionForMissionPerformed(creature, comm, requestId, target, behaviour);
            }
            else if (targetType == 24) {
                final RequestParam param = requestActionForIllusions(creature, target, item, targetType, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 27) {
                final RequestParam param = requestActionForTileCorner(creature, target, onSurface, item, behaviour);
                sendRequestResponse(requestId, comm, param, false);
            }
            else if (targetType == 28) {
                requestActionForBridgeParts(creature, comm, requestId, target, onSurface, item, behaviour);
            }
            else if (targetType == 25) {
                requestActionForTickets(creature, comm, requestId, target, behaviour);
            }
        }
        else {
            comm.sendAlertServerMessage("You are teleporting and cannot perform actions right now.");
        }
    }
    
    private static void sendRequestResponse(final byte requestId, final Communicator comm, final RequestParam response, final boolean sendToSelectBar) {
        if (!sendToSelectBar) {
            comm.sendAvailableActions(requestId, response.getAvailableActions(), response.getHelpString());
        }
        else {
            comm.sendAvailableSelectBarActions(requestId, response.getAvailableActions());
        }
    }
    
    public static final RequestParam requestActionForTiles(final Creature creature, final long target, final boolean onSurface, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int tile = Server.surfaceMesh.getTile(x, y);
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, x, y, onSurface, tile);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, x, y, onSurface, tile);
        }
        final byte type = Tiles.decodeType(tile);
        final Tiles.Tile t = Tiles.getTile(type);
        return new RequestParam(BehaviourDispatcher.availableActions, t.tiledesc.replaceAll(" ", "_"));
    }
    
    private static final RequestParam requestActionForTileCorner(final Creature creature, final long target, final boolean onSurface, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int heightOffset = Tiles.decodeHeightOffset(target);
        final int tile = Server.surfaceMesh.getTile(x, y);
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, x, y, onSurface, true, tile, heightOffset);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, x, y, onSurface, true, tile, heightOffset);
        }
        final byte type = Tiles.decodeType(tile);
        final Tiles.Tile t = Tiles.getTile(type);
        return new RequestParam(BehaviourDispatcher.availableActions, t.tiledesc.replaceAll(" ", "_"));
    }
    
    public static final RequestParam requestActionForCreaturesPlayers(final Creature creature, final long target, final Item item, final int targetType, final Behaviour behaviour) throws NoSuchPlayerException, NoSuchCreatureException {
        final Creature targetc = Server.getInstance().getCreature(target);
        if (targetc.getTemplateId() == 119) {
            return new RequestParam(new ArrayList<ActionEntry>(), "Fishing");
        }
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, targetc);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, targetc);
        }
        if (targetType == 1) {
            return new RequestParam(BehaviourDispatcher.availableActions, targetc.getTemplate().getName().replaceAll(" ", "_"));
        }
        return new RequestParam(BehaviourDispatcher.availableActions, "Player:" + targetc.getName().replaceAll(" ", "_"));
    }
    
    public static final RequestParam requestActionForItemsBodyIdsCoinIds(final Creature creature, final long target, final Item item, final Behaviour behaviour) throws NoSuchItemException {
        final Item targetItem = Items.getItem(target);
        final long ownerId = targetItem.getOwnerId();
        if (ownerId == -10L || ownerId == creature.getWurmId() || targetItem.isTraded()) {
            if (item == null) {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, targetItem);
            }
            else {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, targetItem);
            }
            if (targetItem.isKingdomMarker() && targetItem.isNoTake()) {
                return new RequestParam(BehaviourDispatcher.availableActions, targetItem.getTemplate().getName().replaceAll(" ", "_"));
            }
            String name = "";
            if (targetItem.getTemplate().sizeString != null && !targetItem.getTemplate().sizeString.isEmpty()) {
                name = StringUtil.format("%s%s", targetItem.getTemplate().sizeString, targetItem.getTemplate().getName()).replaceAll(" ", "_");
            }
            else {
                name = targetItem.getTemplate().getName().replaceAll(" ", "_");
            }
            return new RequestParam(BehaviourDispatcher.availableActions, name);
        }
        else {
            if (ownerId == -10L) {
                return new RequestParam(new LinkedList<ActionEntry>(), "");
            }
            (BehaviourDispatcher.availableActions = new LinkedList<ActionEntry>()).addAll(Actions.getDefaultItemActions());
            if (targetItem.isKingdomMarker() && targetItem.isNoTake()) {
                return new RequestParam(BehaviourDispatcher.availableActions, targetItem.getTemplate().getName().replaceAll(" ", "_"));
            }
            String name = "";
            if (targetItem.getTemplate().sizeString.length() > 0) {
                name = StringUtil.format("%s%s", targetItem.getTemplate().sizeString, targetItem.getTemplate().getName()).replaceAll(" ", "_");
            }
            else {
                name = targetItem.getTemplate().getName().replaceAll(" ", "_");
            }
            return new RequestParam(BehaviourDispatcher.availableActions, name);
        }
    }
    
    private static final RequestParam requestActionForWalls(final Creature creature, final long target, final Item item, final Behaviour behaviour) throws NoSuchWallException {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final boolean onSurface = Tiles.decodeLayer(target) == 0;
        Wall wall = null;
        for (int xx = 1; xx >= -1; --xx) {
            for (int yy = 1; yy >= -1; --yy) {
                try {
                    final Zone zone = Zones.getZone(x + xx, y + yy, onSurface);
                    final VolaTile tile = zone.getTileOrNull(x + xx, y + yy);
                    if (tile != null) {
                        final Wall[] walls = tile.getWalls();
                        for (int s = 0; s < walls.length; ++s) {
                            if (walls[s].getId() == target) {
                                wall = walls[s];
                                break;
                            }
                        }
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        if (wall == null) {
            throw new NoSuchWallException("No wall with id " + target);
        }
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, wall);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, wall);
        }
        return new RequestParam(BehaviourDispatcher.availableActions, wall.getIdName());
    }
    
    private static final RequestParam requestActionForFences(final Creature creature, final long target, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final boolean onSurface = Tiles.decodeLayer(target) == 0;
        Fence fence = null;
        VolaTile tile = Zones.getTileOrNull(x, y, onSurface);
        if (tile != null) {
            fence = tile.getFence(target);
        }
        if (fence == null) {
            BehaviourDispatcher.logger.log(Level.WARNING, "Checking for fence with id " + target + " in other tiles. ");
            for (int tx = x - 1; tx <= x + 1; ++tx) {
                for (int ty = y - 1; ty <= y + 1; ++ty) {
                    tile = Zones.getTileOrNull(tx, ty, onSurface);
                    if (tile != null) {
                        fence = tile.getFence(target);
                        if (fence != null) {
                            try {
                                final Zone zone = Zones.getZone(tx, ty, true);
                                BehaviourDispatcher.logger.log(Level.INFO, "Found fence in zone " + zone.getId() + " fence has id " + fence.getId() + " and tilex=" + fence.getTileX() + ", tiley=" + fence.getTileY() + " dir=" + fence.getDir());
                                final Zone correctZone = Zones.getZone(x, y, true);
                                BehaviourDispatcher.logger.log(Level.INFO, "We looked for it in zone " + correctZone.getId());
                                if (!zone.equals(correctZone)) {
                                    BehaviourDispatcher.logger.log(Level.INFO, "Correcting the mistake.");
                                    zone.removeFence(fence);
                                    fence.setZoneId(correctZone.getId());
                                    correctZone.addFence(fence);
                                    tile.broadCast("The server tried to remedy a fence problem here. Please report if anything happened.");
                                }
                            }
                            catch (NoSuchZoneException nsz) {
                                BehaviourDispatcher.logger.log(Level.WARNING, "Weird: " + nsz.getMessage(), nsz);
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (fence != null) {
            if (item == null) {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, fence);
            }
            else {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, fence);
            }
            return new RequestParam(BehaviourDispatcher.availableActions, fence.getName().replaceAll(" ", "_"));
        }
        BehaviourDispatcher.logger.log(Level.WARNING, "Failed to locate fence with id " + target + ".");
        return new RequestParam(new LinkedList<ActionEntry>(), "");
    }
    
    private static void requestActionForWounds(final Creature creature, final Communicator comm, final byte requestId, final long target, final Item item, final Behaviour behaviour) {
        try {
            boolean found = false;
            final Wounds wounds = creature.getBody().getWounds();
            if (wounds != null) {
                final Wound wound = wounds.getWound(target);
                if (wound != null) {
                    found = true;
                    if (item == null) {
                        BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, wound);
                    }
                    else {
                        BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, wound);
                    }
                    comm.sendAvailableActions(requestId, BehaviourDispatcher.availableActions, wound.getDescription().replaceAll(", bandaged", "").replaceAll(" ", "_"));
                }
            }
            if (!found) {
                final Wound wound = Wounds.getAnyWound(target);
                if (wound != null) {
                    if (item == null) {
                        BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, wound);
                    }
                    else {
                        BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, wound);
                    }
                    comm.sendAvailableActions(requestId, BehaviourDispatcher.availableActions, wound.getDescription().replaceAll(", bandaged", "").replaceAll(" ", "_"));
                }
            }
        }
        catch (Exception ex) {
            if (BehaviourDispatcher.logger.isLoggable(Level.FINE)) {
                BehaviourDispatcher.logger.log(Level.FINE, ex.getMessage(), ex);
            }
        }
    }
    
    private static final RequestParam requestActionForTileBorder(final Creature creature, final long target, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int heightOffset = Tiles.decodeHeightOffset(target);
        final Tiles.TileBorderDirection dir = Tiles.decodeDirection(target);
        final boolean onSurface = Tiles.decodeLayer(target) == 0;
        if (MethodsStructure.doesTileBorderContainWallOrFence(x, y, heightOffset, dir, onSurface, true)) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, x, y, onSurface, dir, true, heightOffset);
        }
        else if (item != null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, x, y, onSurface, dir, true, heightOffset);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, x, y, onSurface, dir, true, heightOffset);
        }
        return new RequestParam(BehaviourDispatcher.availableActions, "Tile_Border");
    }
    
    private static void requestActionForPlanets(final Creature creature, final Communicator comm, final byte requestId, final long target, final Item item, final Behaviour behaviour) {
        final int planetId = (int)(target >> 16) & 0xFFFF;
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, planetId);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, planetId);
        }
        comm.sendAvailableActions(requestId, BehaviourDispatcher.availableActions, PlanetBehaviour.getName(planetId));
    }
    
    private static void requestActionForMenu(final Creature creature, final Communicator comm, final byte requestId, final long target, final Behaviour behaviour) {
        final int planetId = (int)(target >> 16) & 0xFFFF;
        comm.sendAvailableActions(requestId, BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, planetId), "");
    }
    
    private static final RequestParam requestActionForCaveTiles(final Creature creature, final long target, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int dir = CaveTile.decodeCaveTileDir(target);
        final int tile = Server.caveMesh.getTile(x, y);
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, x, y, false, tile, dir);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, x, y, false, tile, dir);
        }
        return new RequestParam(BehaviourDispatcher.availableActions, Tiles.getTile(Tiles.decodeType(tile)).tiledesc.replaceAll(" ", "_"));
    }
    
    private static void requestActionForSkillIds(final Communicator comm, final byte requestId, final long target) {
        final int skillid = (int)(target >> 32) & -1;
        String name = "unknown";
        if (skillid == 2147483644) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Favor");
        }
        else if (skillid == 2147483645) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Faith");
        }
        else if (skillid == 2147483642) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Alignment");
        }
        else if (skillid == 2147483643) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Religion");
        }
        else if (skillid == Integer.MAX_VALUE) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Skills");
        }
        else if (skillid == 2147483646) {
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, "Characteristics");
        }
        else {
            name = SkillSystem.getNameFor(skillid);
            comm.sendAvailableActions(requestId, BehaviourDispatcher.emptyActions, name.replaceAll(" ", "_"));
        }
    }
    
    private static final RequestParam requestActionForFloors(final Creature creature, final long target, final boolean onSurface, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int heightOffset = Tiles.decodeHeightOffset(target);
        String fString = "unknown";
        final Floor[] floors = Zones.getFloorsAtTile(x, y, heightOffset, heightOffset, onSurface ? 0 : -1);
        if (floors == null) {
            BehaviourDispatcher.logger.log(Level.WARNING, "No such floor " + target + " (" + x + "," + y + " heightOffset=" + heightOffset + ")");
            return new RequestParam(new LinkedList<ActionEntry>(), "");
        }
        if (floors.length > 1) {
            BehaviourDispatcher.logger.log(Level.WARNING, "Found more than 1 floor at " + x + "," + y + " heightOffset" + heightOffset);
        }
        final Floor floor = floors[0];
        fString = floor.getName();
        if (item == null) {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, onSurface, floor);
        }
        else {
            BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, onSurface, floor);
        }
        creature.sendToLoggers("Requesting floor " + floor.getId() + " target requested=" + target + " " + floor.getHeightOffset());
        return new RequestParam(BehaviourDispatcher.availableActions, fString);
    }
    
    private static void requestActionForBridgeParts(final Creature creature, final Communicator comm, final byte requestId, final long target, final boolean onSurface, final Item item, final Behaviour behaviour) {
        final int x = Tiles.decodeTileX(target);
        final int y = Tiles.decodeTileY(target);
        final int ht = Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y));
        final int heightOffset = Tiles.decodeHeightOffset(target) - ht;
        String fString = "unknown";
        final BridgePart[] bridgeParts = Zones.getBridgePartsAtTile(x, y, onSurface);
        if (bridgeParts == null) {
            BehaviourDispatcher.logger.log(Level.WARNING, "No such Bridge Part " + target + " (" + x + "," + y + " heightOffset=" + heightOffset + ")");
        }
        else {
            if (bridgeParts.length > 1) {
                BehaviourDispatcher.logger.log(Level.WARNING, "Found more than 1 bridge part at " + x + "," + y + " heightOffset" + heightOffset);
            }
            final BridgePart bridgePart = bridgeParts[0];
            fString = bridgePart.getName();
            if (item == null) {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, onSurface, bridgePart);
            }
            else {
                BehaviourDispatcher.availableActions = behaviour.getBehavioursFor(creature, item, onSurface, bridgePart);
            }
            creature.sendToLoggers("Requesting bridge part " + bridgePart.getId() + " target requested=" + target + " " + bridgePart.getHeightOffset());
            comm.sendAvailableActions(requestId, BehaviourDispatcher.availableActions, fString);
        }
    }
    
    private static void requestActionForMissionPerformed(final Creature creature, final Communicator comm, final byte requestId, final long target, final Behaviour behaviour) {
        final int missionId = MissionPerformed.decodeMissionId(target);
        final Mission m = Missions.getMissionWithId(missionId);
        String mString = "unknown";
        if (m != null) {
            mString = m.getName();
        }
        comm.sendAvailableActions(requestId, behaviour.getBehavioursFor(creature, missionId), "Mission:" + mString);
    }
    
    private static final RequestParam requestActionForIllusions(final Creature creature, final long target, final Item item, final int targetType, final Behaviour behaviour) throws NoSuchPlayerException, NoSuchCreatureException {
        final long wid = Creature.getWurmIdForIllusion(target);
        return requestActionForCreaturesPlayers(creature, wid, item, targetType, behaviour);
    }
    
    private static void requestActionForTickets(final Creature creature, final Communicator comm, final byte requestId, final long target, final Behaviour behaviour) {
        final int ticketId = Tickets.decodeTicketId(target);
        comm.sendAvailableActions(requestId, behaviour.getBehavioursFor(creature, ticketId), "Ticket:" + ticketId);
    }
    
    public static void action(final Creature creature, final Communicator comm, final long subject, final long target, final short action) throws NoSuchPlayerException, NoSuchCreatureException, NoSuchItemException, NoSuchBehaviourException, NoSuchWallException, FailedException {
        String s = "unknown";
        try {
            s = Actions.getVerbForAction(action);
        }
        catch (Exception e) {
            s = "" + action;
        }
        if (creature.isUndead() && action != 326 && action != 1 && !Action.isActionAttack(action) && !Action.isStanceChange(action) && action != 523 && action != 522) {
            creature.getCommunicator().sendNormalServerMessage("Unnn..");
            return;
        }
        creature.sendToLoggers("Received action number " + s + ", target " + target + ", source " + subject + ", action " + action, (byte)2);
        if (creature.isFrozen()) {
            creature.sendToLoggers("Frozen. Ignoring.", (byte)2);
            throw new FailedException("Frozen");
        }
        if (creature.isTeleporting()) {
            comm.sendAlertServerMessage("You are teleporting and cannot perform actions right now.");
            throw new FailedException("Teleporting");
        }
        if (action == 149) {
            try {
                if (creature.getCurrentAction().isSpell() || !creature.getCurrentAction().isOffensive() || !creature.isFighting()) {
                    creature.stopCurrentAction();
                }
            }
            catch (NoSuchActionException ex) {}
        }
        else {
            final float x = creature.getStatus().getPositionX();
            final float y = creature.getStatus().getPositionY();
            final float z = creature.getStatus().getPositionZ() + creature.getAltOffZ();
            final Action toSet = new Action(creature, subject, target, action, x, y, z, creature.getStatus().getRotation());
            if (toSet.isQuick()) {
                toSet.poll();
            }
            else if (toSet.isStanceChange() && toSet.getNumber() != 340) {
                if (!toSet.poll()) {
                    creature.setAction(toSet);
                }
            }
            else {
                toSet.setRarity(creature.getRarity());
                creature.setAction(toSet);
            }
        }
    }
    
    public static void action(final Creature creature, final Communicator comm, final long subject, final long[] targets, final short action) throws FailedException, NoSuchPlayerException, NoSuchCreatureException, NoSuchItemException, NoSuchBehaviourException {
        String s = "unknown";
        try {
            s = Actions.getVerbForAction(action);
        }
        catch (Exception e) {
            s = "" + action;
        }
        if (creature.isUndead()) {
            creature.getCommunicator().sendNormalServerMessage("Unnn..");
            return;
        }
        String tgts = "";
        for (int x = 0; x < targets.length; ++x) {
            if (tgts.length() > 0) {
                tgts += ", ";
            }
            tgts += targets[x];
        }
        creature.sendToLoggers("Received action number " + s + ", target " + tgts, (byte)2);
        if (creature.isFrozen()) {
            creature.sendToLoggers("Frozen. Ignoring.", (byte)2);
            throw new FailedException("Frozen");
        }
        if (creature.isTeleporting()) {
            comm.sendAlertServerMessage("You are teleporting and cannot perform actions right now.");
            throw new FailedException("Teleporting");
        }
        final float x2 = creature.getStatus().getPositionX();
        final float y = creature.getStatus().getPositionY();
        final float z = creature.getStatus().getPositionZ() + creature.getAltOffZ();
        final Action toSet = new Action(creature, subject, targets, action, x2, y, z, creature.getStatus().getRotation());
        if (toSet.isQuick()) {
            toSet.poll();
        }
        else {
            toSet.setRarity(creature.getRarity());
            creature.setAction(toSet);
        }
    }
    
    static {
        logger = Logger.getLogger(BehaviourDispatcher.class.getName());
        BehaviourDispatcher.availableActions = null;
        emptyActions = new LinkedList<ActionEntry>();
    }
    
    public static class RequestParam
    {
        private final String helpString;
        private List<ActionEntry> availableActions;
        
        public RequestParam(final List<ActionEntry> actions, final String help) {
            this.availableActions = actions;
            this.helpString = help;
        }
        
        public final List<ActionEntry> getAvailableActions() {
            return this.availableActions;
        }
        
        public final String getHelpString() {
            return this.helpString;
        }
        
        public void filterForSelectBar() {
            for (int i = this.availableActions.size() - 1; i >= 0; --i) {
                final ActionEntry entry = this.availableActions.get(i);
                if (!entry.isShowOnSelectBar()) {
                    this.availableActions.remove(i);
                }
            }
        }
    }
}
