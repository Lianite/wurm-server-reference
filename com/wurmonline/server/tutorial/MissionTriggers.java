// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.Players;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.Servers;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.WurmId;
import java.util.Iterator;
import java.util.Set;
import com.wurmonline.server.questions.MissionManager;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class MissionTriggers implements CounterTypes, MiscConstants
{
    private static Logger logger;
    private static final String LOADALLTRIGGERS = "SELECT * FROM MISSIONTRIGGERS";
    private static final Map<Integer, MissionTrigger> triggers;
    public static final int SHOW_ALL = 0;
    public static final int SHOW_LINKED = 1;
    public static final int SHOW_UNLINKED = 2;
    
    public static void addMissionTrigger(final MissionTrigger trigger) {
        MissionTriggers.triggers.put(trigger.getId(), trigger);
        MissionTargets.addMissionTrigger(trigger);
    }
    
    public static MissionTrigger[] getAllTriggers() {
        return MissionTriggers.triggers.values().toArray(new MissionTrigger[MissionTriggers.triggers.size()]);
    }
    
    public static MissionTrigger getRespawnTriggerForMission(final int missionId, final float state) {
        final MissionTrigger[] trigs = getAllTriggers();
        float foundState = -100.0f;
        MissionTrigger toret = null;
        for (int x = 0; x < trigs.length; ++x) {
            if (trigs[x].getMissionRequired() == missionId && trigs[x].getStateRequired() < state && trigs[x].isSpawnPoint() && foundState < trigs[x].getStateRequired()) {
                foundState = trigs[x].getStateRequired();
                toret = trigs[x];
            }
        }
        return toret;
    }
    
    public static int getNumTriggers() {
        return MissionTriggers.triggers.size();
    }
    
    public static MissionTrigger[] getFilteredTriggers(final Creature creature, final int triggerId, final byte creatorType, final long listForUser, final boolean dontListMine, final boolean listMineOnly) {
        final Set<MissionTrigger> trigs = new HashSet<MissionTrigger>();
        for (final MissionTrigger trig : MissionTriggers.triggers.values()) {
            final boolean own = trig.getOwnerId() == creature.getWurmId();
            boolean show = creature.getPower() > 0 || own;
            final boolean userMatch = trig.getOwnerId() == listForUser;
            if (triggerId > 0 && trig.getId() == triggerId) {
                show = true;
            }
            else if (own) {
                if (dontListMine) {
                    show = false;
                }
            }
            else if (listMineOnly) {
                show = false;
                if (listForUser != -10L && userMatch) {
                    show = true;
                }
            }
            else if (listForUser != -10L) {
                show = false;
                if (userMatch) {
                    show = true;
                }
            }
            if (creatorType == 2 && creature.getPower() < MissionManager.CAN_SEE_EPIC_MISSIONS) {
                show = false;
            }
            if (show) {
                trigs.add(trig);
            }
        }
        return trigs.toArray(new MissionTrigger[trigs.size()]);
    }
    
    public static MissionTrigger[] getFilteredTriggers(final Creature creature, final int linked, final boolean includeInactive, final int missionId, final int triggerId) {
        final Set<MissionTrigger> trigs = new HashSet<MissionTrigger>();
        if (triggerId != 0) {
            final MissionTrigger trig = MissionTriggers.triggers.get(triggerId);
            if (trig != null && showTrigger(trig, creature, linked, includeInactive, missionId)) {
                trigs.add(trig);
            }
        }
        else {
            for (final MissionTrigger trig2 : MissionTriggers.triggers.values()) {
                if (showTrigger(trig2, creature, linked, includeInactive, missionId)) {
                    trigs.add(trig2);
                }
            }
        }
        return trigs.toArray(new MissionTrigger[trigs.size()]);
    }
    
    private static boolean showTrigger(final MissionTrigger trig, final Creature creature, final int linked, final boolean includeInactive, final int missionId) {
        boolean show = false;
        if (missionId == 0 || trig.getMissionRequired() == missionId) {
            if (missionId == 0) {
                switch (linked) {
                    case 0: {
                        show = true;
                        break;
                    }
                    case 1: {
                        show = (trig.getMissionRequired() != 0);
                        break;
                    }
                    case 2: {
                        show = (trig.getMissionRequired() == 0);
                        break;
                    }
                }
            }
            else {
                show = true;
            }
            if (!includeInactive && !trig.isInactive()) {
                show = false;
            }
            if (show && creature.getPower() == 0 && trig.getOwnerId() != creature.getWurmId()) {
                show = false;
            }
        }
        return show;
    }
    
    public static MissionTrigger[] getMissionTriggers(final int missionId) {
        final Set<MissionTrigger> trigs = new HashSet<MissionTrigger>();
        for (final MissionTrigger m : MissionTriggers.triggers.values()) {
            if (m.getMissionRequired() == missionId) {
                trigs.add(m);
            }
        }
        return trigs.toArray(new MissionTrigger[trigs.size()]);
    }
    
    public static boolean hasMissionTriggers(final int missionId) {
        for (final MissionTrigger m : MissionTriggers.triggers.values()) {
            if (m.getMissionRequired() == missionId) {
                return true;
            }
        }
        return false;
    }
    
    public static MissionTrigger[] getMissionTriggersWith(final int itemUsed, final int action, final long target) {
        final Set<MissionTrigger> trigs = new HashSet<MissionTrigger>();
        for (final MissionTrigger m : MissionTriggers.triggers.values()) {
            if ((m.getOnActionPerformed() <= 0 || m.getOnActionPerformed() == action) && (m.getItemUsedId() <= 0 || m.getItemUsedId() == itemUsed)) {
                if (m.getItemUsedId() <= 0 && action == 142 && m.getItemUsedId() != itemUsed) {
                    continue;
                }
                if ((WurmId.getType(target) == 3 && WurmId.getType(m.getTarget()) == 3) || (WurmId.getType(target) == 17 && WurmId.getType(m.getTarget()) == 17)) {
                    final int x = Tiles.decodeTileX(target);
                    final int y = Tiles.decodeTileY(target);
                    final int x2 = Tiles.decodeTileX(m.getTarget());
                    final int y2 = Tiles.decodeTileY(m.getTarget());
                    if (x != x2 || y != y2) {
                        continue;
                    }
                    trigs.add(m);
                }
                else {
                    if (m.getTarget() > 0L && m.getTarget() != target) {
                        continue;
                    }
                    final EpicMission mis = EpicServerStatus.getEpicMissionForMission(m.getMissionRequired());
                    if (mis != null && mis.getMissionType() == 116 && !Servers.localServer.PVPSERVER) {
                        try {
                            final Creature killed = Creatures.getInstance().getCreature(target);
                            if (killed.getStatus().getModType() != 99) {
                                continue;
                            }
                        }
                        catch (NoSuchCreatureException ex) {}
                    }
                    trigs.add(m);
                }
            }
        }
        return trigs.toArray(new MissionTrigger[trigs.size()]);
    }
    
    public static MissionTrigger[] getMissionTriggerPlate(final int tilex, final int tiley, final int layer) {
        final Set<MissionTrigger> trigs = new HashSet<MissionTrigger>();
        for (final MissionTrigger m : MissionTriggers.triggers.values()) {
            if (m.getOnActionPerformed() == 475) {
                if (layer == 0 && WurmId.getType(m.getTarget()) == 3) {
                    final int x2 = Tiles.decodeTileX(m.getTarget());
                    final int y2 = Tiles.decodeTileY(m.getTarget());
                    if (tilex != x2 || tiley != y2) {
                        continue;
                    }
                    trigs.add(m);
                }
                else {
                    if (layer >= 0 || WurmId.getType(m.getTarget()) != 17) {
                        continue;
                    }
                    final int x2 = Tiles.decodeTileX(m.getTarget());
                    final int y2 = Tiles.decodeTileY(m.getTarget());
                    if (tilex != x2 || tiley != y2) {
                        continue;
                    }
                    trigs.add(m);
                }
            }
        }
        return trigs.toArray(new MissionTrigger[trigs.size()]);
    }
    
    public static boolean activateTriggerPlate(final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.isPlayer()) {
            final MissionTrigger[] trigs = getMissionTriggerPlate(tilex, tiley, layer);
            if (trigs.length > 0) {
                final TriggerRun tr = new TriggerRun();
                tr.run(performer, trigs, 1);
                return tr.isTriggered();
            }
        }
        return false;
    }
    
    public static boolean activateTriggers(final Creature performer, final int creatureTemplateId, final int actionPerformed, final long targetId, final int counter) {
        boolean done = true;
        final MissionTrigger[] trigs = getMissionTriggersWith(creatureTemplateId, actionPerformed, targetId);
        if (trigs.length > 0) {
            final TriggerRun tr = new TriggerRun();
            tr.run(performer, trigs, counter);
            done = tr.isDone();
        }
        return done;
    }
    
    public static boolean activateTriggers(final Creature performer, final Item item, final int actionPerformed, final long targetId, final int counter) {
        boolean done = true;
        if (performer.isPlayer()) {
            final MissionTrigger[] trigs = getMissionTriggersWith((item != null) ? item.getTemplateId() : 0, actionPerformed, targetId);
            performer.sendToLoggers("Found " + trigs.length + " triggers.", (byte)2);
            if (trigs.length > 0) {
                final TriggerRun tr = new TriggerRun();
                tr.run(performer, trigs, counter);
                done = tr.isDone();
                if (tr.isTriggered()) {
                    if (actionPerformed == 492) {
                        EffectFactory.getInstance().deleteEffByOwner(targetId);
                    }
                    if (actionPerformed == 47 && item != null) {
                        if (tr.getLastTrigger() != null && tr.getLastTrigger().getCreatorType() == 3) {
                            if (WurmId.getType(targetId) == 1 || WurmId.getType(targetId) == 0) {
                                item.putInVoid();
                                try {
                                    final Creature targetC = Server.getInstance().getCreature(targetId);
                                    targetC.getInventory().insertItem(item);
                                }
                                catch (NoSuchCreatureException nsc) {
                                    Items.destroyItem(item.getWurmId());
                                }
                                catch (NoSuchPlayerException nsp) {
                                    Items.destroyItem(item.getWurmId());
                                }
                            }
                        }
                        else {
                            Items.destroyItem(item.getWurmId());
                        }
                    }
                }
            }
        }
        return done;
    }
    
    public static boolean isDoorOpen(final Creature performer, final long doorid, final int counter) {
        final MissionTarget targ = MissionTargets.getMissionTargetFor(doorid);
        if (targ != null) {
            final MissionTrigger[] trigs = targ.getMissionTriggers();
            final TriggerRun tr = new TriggerRun();
            tr.run(performer, trigs, counter);
            return tr.isOpenedDoor();
        }
        return false;
    }
    
    public static MissionTrigger getTriggerWithId(final int id) {
        return MissionTriggers.triggers.get(id);
    }
    
    public static String getTargetAsString(final Creature creature, final long target) {
        final StringBuilder buf = new StringBuilder();
        if (target <= 0L) {
            buf.append("None");
        }
        else if (WurmId.getType(target) == 1) {
            try {
                final Creature c = Creatures.getInstance().getCreature(target);
                if (creature.getPower() > 0) {
                    buf.append(c.getName() + " at " + c.getTileX() + "," + c.getTileY());
                }
                else {
                    buf.append(c.getName());
                }
            }
            catch (NoSuchCreatureException nsc) {
                buf.append("Nonexistant creature.");
            }
        }
        else if (WurmId.getType(target) == 0) {
            try {
                final Player p = Players.getInstance().getPlayer(target);
                if (creature.getPower() > 0) {
                    buf.append(p.getName() + " at " + p.getTileX() + "," + p.getTileY());
                }
                else {
                    buf.append(p.getName());
                }
            }
            catch (NoSuchPlayerException nsc2) {
                buf.append("Nonexistant creature.");
            }
        }
        else if (WurmId.getType(target) == 5) {
            final int x = (int)(target >> 32) & 0xFFFF;
            final int y = (int)(target >> 16) & 0xFFFF;
            final boolean onSurface = Tiles.decodeLayer(target) == 0;
            final Wall wall = Wall.getWall(target);
            String loc = "";
            if (creature.getPower() > 0) {
                loc = " at " + x + "," + y + ", " + onSurface;
            }
            if (wall == null) {
                buf.append("Unknown wall" + loc);
            }
            else {
                buf.append(wall.getName());
                buf.append(" (level:" + wall.getFloorLevel() + ")");
                buf.append(loc);
            }
        }
        else {
            if (WurmId.getType(target) != 2 && WurmId.getType(target) != 6 && WurmId.getType(target) != 19) {
                if (WurmId.getType(target) != 20) {
                    if (WurmId.getType(target) == 7) {
                        final int x = (int)(target >> 32) & 0xFFFF;
                        final int y = (int)(target >> 16) & 0xFFFF;
                        final boolean onSurface = Tiles.decodeLayer(target) == 0;
                        final Fence fence = Fence.getFence(target);
                        String loc = "";
                        if (creature.getPower() > 0) {
                            loc = " at " + x + ", " + y + ", " + onSurface;
                        }
                        if (fence == null) {
                            buf.append("Unknown fence" + loc);
                        }
                        else {
                            buf.append(fence.getName());
                            if (fence.getFloorLevel() > 0) {
                                buf.append(" (level:" + fence.getFloorLevel() + ")");
                            }
                            buf.append(loc);
                        }
                        return buf.toString();
                    }
                    if (WurmId.getType(target) == 28) {
                        final int x = Tiles.decodeTileX(target);
                        final int y = Tiles.decodeTileY(target);
                        final int layer = Tiles.decodeLayer(target);
                        String loc2 = "";
                        if (creature.getPower() > 0) {
                            loc2 = " at " + x + ", " + y + ", " + (layer == 0);
                        }
                        final BridgePart[] bridgeParts = Zones.getBridgePartsAtTile(x, y, layer == 0);
                        if (bridgeParts.length == 0) {
                            buf.append("Unknown bridge part" + loc2);
                        }
                        else if (bridgeParts.length > 1) {
                            buf.append("Too many bridge parts found" + loc2);
                        }
                        else {
                            buf.append(bridgeParts[0].getName() + loc2);
                        }
                        return buf.toString();
                    }
                    if (WurmId.getType(target) == 23) {
                        final int x = Tiles.decodeTileX(target);
                        final int y = Tiles.decodeTileY(target);
                        final int layer = Tiles.decodeLayer(target);
                        final int htOffset = Floor.getHeightOffsetFromWurmId(target);
                        String loc = "";
                        if (creature.getPower() > 0) {
                            loc = " at " + x + ", " + y + ", " + (layer == 0);
                        }
                        final Floor[] floors = Zones.getFloorsAtTile(x, y, htOffset, htOffset, layer);
                        if (floors == null || floors.length == 0) {
                            buf.append("Unknown floor" + loc);
                        }
                        else {
                            buf.append(floors[0].getName());
                            buf.append(" (level:" + floors[0].getFloorLevel() + ")");
                            buf.append(loc);
                        }
                        return buf.toString();
                    }
                    if (WurmId.getType(target) == 3) {
                        boolean broken = false;
                        int x2 = (int)(target >> 32) & 0xFFFF;
                        final int y2 = (int)(target >> 16) & 0xFFFF;
                        if (x2 > Zones.worldTileSizeX) {
                            x2 = ((int)(target >> 40) & 0xFFFFFF);
                            broken = true;
                        }
                        final int heightOffset = (int)(target >> 48) & 0xFFFF;
                        final int tile = Server.surfaceMesh.getTile(x2, y2);
                        final byte type = Tiles.decodeType(tile);
                        final Tiles.Tile t = Tiles.getTile(type);
                        buf.append(t.tiledesc);
                        if (creature.getPower() > 0) {
                            if (broken) {
                                buf.append(" * ");
                            }
                            buf.append(" at ");
                            buf.append(x2);
                            buf.append(", ");
                            buf.append(y2);
                            buf.append(", true");
                        }
                        return buf.toString();
                    }
                    if (WurmId.getType(target) != 17) {
                        return buf.toString();
                    }
                    final int x = (int)(target >> 32) & 0xFFFF;
                    final int y = (int)(target >> 16) & 0xFFFF;
                    final int tile2 = Server.caveMesh.getTile(x, y);
                    final byte type2 = Tiles.decodeType(tile2);
                    final Tiles.Tile t2 = Tiles.getTile(type2);
                    buf.append(t2.tiledesc);
                    if (creature.getPower() > 0) {
                        buf.append(" at ");
                        buf.append(x);
                        buf.append(", ");
                        buf.append(y);
                        buf.append(", false");
                        return buf.toString();
                    }
                    return buf.toString();
                }
            }
            try {
                final Item targetItem = Items.getItem(target);
                final String tgtName = targetItem.getName().replace('\"', '\'');
                if (creature.getPower() > 0) {
                    buf.append(tgtName + " at " + targetItem.getTileX() + "," + targetItem.getTileY());
                }
                else {
                    buf.append(tgtName);
                }
                if (targetItem.getOwnerId() != -10L && creature.getPower() > 0) {
                    buf.append(" owned by " + targetItem.getOwnerId());
                }
            }
            catch (NoSuchItemException nsi) {
                buf.append("Unknown item");
            }
        }
        return buf.toString();
    }
    
    static boolean removeTrigger(final int id) {
        final MissionTrigger trigger = MissionTriggers.triggers.get(id);
        final boolean existed = MissionTriggers.triggers.remove(id) != null;
        if (trigger != null) {
            MissionTargets.removeMissionTrigger(trigger, true);
        }
        return existed;
    }
    
    static void destroyTriggersForTarget(final long target) {
        final MissionTrigger[] mtarr = getAllTriggers();
        for (int t = 0; t < mtarr.length; ++t) {
            if (mtarr[t] != null && mtarr[t].getTarget() == target) {
                TriggerEffects.destroyEffectsForTrigger(mtarr[t].getId());
                removeTrigger(mtarr[t].getId());
                mtarr[t].destroy();
            }
        }
        MissionTargets.destroyMissionTarget(target, false);
    }
    
    private static void loadAllTriggers() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MISSIONTRIGGERS");
            rs = ps.executeQuery();
            int mid = -10;
            while (rs.next()) {
                mid = rs.getInt("ID");
                final MissionTrigger m = new MissionTrigger();
                m.setId(mid);
                m.setName(rs.getString("NAME"));
                m.setDescription(rs.getString("DESCRIPTION"));
                m.setOnItemUsedId(rs.getInt("ONITEMCREATED"));
                m.setOnActionPerformed(rs.getInt("ONACTIONPERFORMED"));
                m.setOnTargetId(rs.getLong("ONTARGET"));
                m.setMissionRequirement(rs.getInt("MISSION_REQ"));
                m.setStateRequirement(rs.getFloat("MISSION_STATE_REQ"));
                m.setStateEnd(rs.getFloat("MISSION_STATE_END"));
                m.setInactive(rs.getBoolean("INACTIVE"));
                m.setLastModifierName(rs.getString("LASTMODIFIER"));
                m.setCreatorName(rs.getString("CREATOR"));
                m.setCreatedDate(rs.getString("CREATEDDATE"));
                m.setLastModifierName(rs.getString("LASTMODIFIER"));
                Timestamp st = new Timestamp(System.currentTimeMillis());
                try {
                    final String lastModified = rs.getString("LASTMODIFIEDDATE");
                    if (lastModified != null) {
                        st = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastModified).getTime());
                    }
                }
                catch (Exception ex) {
                    MissionTriggers.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                m.setLastModifiedDate(st);
                m.setSeconds(rs.getInt("SECONDS"));
                m.setCreatorType(rs.getByte("CREATORTYPE"));
                m.setOwnerId(rs.getLong("CREATORID"));
                m.setIsSpawnpoint(rs.getBoolean("SPAWNPOINT"));
                addMissionTrigger(m);
            }
        }
        catch (SQLException sqx) {
            MissionTriggers.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        MissionTriggers.logger = Logger.getLogger(MissionTriggers.class.getName());
        triggers = new ConcurrentHashMap<Integer, MissionTrigger>();
        try {
            loadAllTriggers();
        }
        catch (Exception ex) {
            MissionTriggers.logger.log(Level.WARNING, "Problems loading all Mission Triggers", ex);
        }
    }
}
