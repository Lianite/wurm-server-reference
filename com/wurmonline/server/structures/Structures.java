// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.shared.constants.StructureConstants;
import com.wurmonline.shared.constants.StructureStateEnum;
import com.wurmonline.shared.constants.StructureMaterialEnum;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Server;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.io.IOException;
import com.wurmonline.server.WurmId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Set;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import java.util.HashSet;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public final class Structures implements MiscConstants, CounterTypes
{
    private static final String GET_STRUCTURES = "SELECT * FROM STRUCTURES";
    private static Map<Long, Structure> structures;
    private static Map<Long, Structure> bridges;
    private static final Structure[] emptyStructures;
    private static final Logger logger;
    
    public static int getNumberOfStructures() {
        if (Structures.structures != null) {
            return Structures.structures.size();
        }
        return 0;
    }
    
    public static final Structure[] getAllStructures() {
        if (Structures.structures == null) {
            return Structures.emptyStructures;
        }
        return Structures.structures.values().toArray(new Structure[Structures.structures.size()]);
    }
    
    public static final Structure[] getManagedBuildingsFor(final Player player, final int villageId, final boolean includeAll) {
        if (Structures.structures == null) {
            return Structures.emptyStructures;
        }
        final Set<Structure> buildings = new HashSet<Structure>();
        for (final Structure structure : Structures.structures.values()) {
            if (structure.isTypeHouse()) {
                if (structure.canManage(player)) {
                    buildings.add(structure);
                }
                if (includeAll && ((villageId >= 0 && structure.getVillageId() == villageId) || structure.isActualOwner(player.getWurmId()))) {
                    buildings.add(structure);
                }
                if (structure.getWritid() == -10L || !structure.isActualOwner(player.getWurmId())) {
                    continue;
                }
                Items.destroyItem(structure.getWritId());
                structure.setWritid(-10L, true);
            }
        }
        return buildings.toArray(new Structure[buildings.size()]);
    }
    
    public static final Structure[] getOwnedBuildingFor(final Player player) {
        if (Structures.structures == null) {
            return Structures.emptyStructures;
        }
        final Set<Structure> buildings = new HashSet<Structure>();
        for (final Structure structure : Structures.structures.values()) {
            if (structure.isTypeHouse() && (structure.isOwner(player) || structure.isActualOwner(player.getWurmId()))) {
                buildings.add(structure);
            }
        }
        return buildings.toArray(new Structure[buildings.size()]);
    }
    
    public static final Structure getStructureOrNull(final long id) {
        Structure structure = null;
        if (Structures.structures == null) {
            Structures.structures = new ConcurrentHashMap<Long, Structure>();
        }
        else {
            structure = Structures.structures.get(new Long(id));
        }
        if (structure == null && WurmId.getType(id) == 4) {
            try {
                structure = loadStructure(id);
                addStructure(structure);
            }
            catch (IOException ex) {}
            catch (NoSuchStructureException ex2) {}
        }
        return structure;
    }
    
    public static final Structure getStructure(final long id) throws NoSuchStructureException {
        final Structure structure = getStructureOrNull(id);
        if (structure == null) {
            throw new NoSuchStructureException("No such structure.");
        }
        return structure;
    }
    
    public static void addStructure(final Structure structure) {
        if (Structures.structures == null) {
            Structures.structures = new ConcurrentHashMap<Long, Structure>();
        }
        Structures.structures.put(new Long(structure.getWurmId()), structure);
        if (structure.isTypeBridge()) {
            addBridge(structure);
        }
    }
    
    public static final void addBridge(final Structure bridge) {
        if (Structures.bridges == null) {
            Structures.bridges = new ConcurrentHashMap<Long, Structure>();
        }
        Structures.bridges.put(new Long(bridge.getWurmId()), bridge);
    }
    
    public static void removeBridge(final long id) {
        if (Structures.bridges != null) {
            Structures.bridges.remove(new Long(id));
        }
    }
    
    public static final Structure getBridge(final long id) {
        Structure bridge = null;
        if (Structures.bridges != null) {
            bridge = Structures.bridges.get(new Long(id));
        }
        return bridge;
    }
    
    public static void removeStructure(final long id) {
        if (Structures.structures != null) {
            Structures.structures.remove(new Long(id));
        }
    }
    
    public static final Structure createStructure(final byte theStructureType, final String name, final long id, final int startx, final int starty, final boolean surfaced) {
        Structure toReturn = null;
        toReturn = new DbStructure(theStructureType, name, id, startx, starty, surfaced);
        addStructure(toReturn);
        return toReturn;
    }
    
    private static final Structure loadStructure(final long id) throws IOException, NoSuchStructureException {
        Structure toReturn = null;
        toReturn = new DbStructure(id);
        addStructure(toReturn);
        return toReturn;
    }
    
    public static Structure getStructureForWrit(final long writId) throws NoSuchStructureException {
        if (writId == -10L) {
            throw new NoSuchStructureException("No structure for writid " + writId);
        }
        for (final Structure s : Structures.structures.values()) {
            if (s.getWritId() == writId) {
                return s;
            }
        }
        throw new NoSuchStructureException("No structure for writid " + writId);
    }
    
    public static void endLoadAll() {
        if (Structures.structures != null) {
            for (final Structure struct : Structures.structures.values()) {
                try {
                    struct.endLoading();
                }
                catch (IOException iox) {
                    Structures.logger.log(Level.WARNING, iox.getMessage() + ": " + struct.getWurmId() + " writ " + struct.getWritid());
                }
            }
        }
    }
    
    public static void loadAllStructures() throws IOException {
        Structures.logger.info("Loading all Structures");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM STRUCTURES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final byte structureType = rs.getByte("STRUCTURETYPE");
                final boolean surfaced = rs.getBoolean("SURFACED");
                final byte roof = rs.getByte("ROOF");
                String name = rs.getString("NAME");
                if (name == null) {
                    name = "Unknown structure";
                }
                if (name.length() >= 50) {
                    name = name.substring(0, 49);
                }
                final boolean finished = rs.getBoolean("FINISHED");
                final boolean finalfinished = rs.getBoolean("FINFINISHED");
                final boolean allowsCitizens = rs.getBoolean("ALLOWSVILLAGERS");
                final boolean allowsAllies = rs.getBoolean("ALLOWSALLIES");
                final boolean allowsKingdom = rs.getBoolean("ALLOWSKINGDOM");
                final String planner = rs.getString("PLANNER");
                final long ownerId = rs.getLong("OWNERID");
                final int settings = rs.getInt("SETTINGS");
                final int villageId = rs.getInt("VILLAGE");
                long writid = -10L;
                try {
                    writid = rs.getLong("WRITID");
                }
                catch (Exception nsi) {
                    if (structureType == 0) {
                        Structures.logger.log(Level.INFO, "No writ for house with id:" + wurmid + " creating new after loading.", nsi);
                    }
                }
                addStructure(new DbStructure(structureType, name, wurmid, surfaced, roof, finished, finalfinished, writid, planner, ownerId, settings, villageId, allowsCitizens, allowsAllies, allowsKingdom));
            }
        }
        catch (SQLException sqex) {
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final int numberOfStructures = (Structures.structures != null) ? Structures.structures.size() : 0;
            Structures.logger.log(Level.INFO, "Structures loaded. Number of structures=" + numberOfStructures + ". That took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        }
    }
    
    public static Structure getStructureForTile(final int tilex, final int tiley, final boolean onSurface) {
        if (Structures.structures != null) {
            for (final Structure s : Structures.structures.values()) {
                if (s.isOnSurface() == onSurface && s.contains(tilex, tiley)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    public static Structure getBuildingForTile(final int tilex, final int tiley) {
        if (Structures.structures != null) {
            for (final Structure s : Structures.structures.values()) {
                if (s.contains(tilex, tiley)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    public static final void createRandomStructure(final Creature creator, final int stx, final int endtx, final int sty, final int endty, final int centerx, final int centery, final byte material, final String sname) {
        if (creator.getCurrentTile() != null) {
            if (creator.getCurrentTile().getStructure() != null) {
                return;
            }
        }
        try {
            final Structure struct = createStructure((byte)0, sname, WurmId.getNextPlanId(), centerx, centery, true);
            for (int currx = stx; currx <= endtx; ++currx) {
                for (int curry = sty; curry <= endty; ++curry) {
                    if (currx != stx || (curry != sty && Server.rand.nextInt(3) < 2)) {
                        final VolaTile vtile = Zones.getOrCreateTile(currx, curry, true);
                        struct.addBuildTile(vtile, false);
                        struct.clearAllWallsAndMakeWallsForStructureBorder(vtile);
                    }
                }
            }
            final float rot = Creature.normalizeAngle(creator.getStatus().getRotation());
            struct.makeFinal(creator, sname);
            for (final VolaTile bt : struct.getStructureTiles()) {
                StructureTypeEnum wtype = StructureTypeEnum.SOLID;
                if (Server.rand.nextInt(2) == 0) {
                    wtype = StructureTypeEnum.WINDOW;
                }
                for (final Wall plan : bt.getWalls()) {
                    if (!plan.isHorizontal() && plan.getStartY() == creator.getTileY() && rot <= 315.0f && rot >= 235.0f) {
                        wtype = StructureTypeEnum.DOOR;
                    }
                    if (plan.isHorizontal() && plan.getStartX() == creator.getTileX() && ((rot >= 315.0f && rot <= 360.0f) || (rot >= 0.0f && rot <= 45.0f))) {
                        wtype = StructureTypeEnum.DOOR;
                    }
                    if (plan.isHorizontal() && plan.getStartX() == creator.getTileX() && rot >= 135.0f && rot <= 215.0f) {
                        wtype = StructureTypeEnum.DOOR;
                    }
                    if (!plan.isHorizontal() && plan.getStartY() == creator.getTileY() && rot <= 135.0f && rot >= 45.0f) {
                        wtype = StructureTypeEnum.DOOR;
                    }
                    if (material == 15) {
                        plan.setMaterial(StructureMaterialEnum.STONE);
                    }
                    else {
                        plan.setMaterial(StructureMaterialEnum.WOOD);
                    }
                    plan.setType(wtype);
                    plan.setQualityLevel(80.0f);
                    plan.setState(StructureStateEnum.FINISHED);
                    bt.updateWall(plan);
                    if (plan.isDoor()) {
                        final Door door = new DbDoor(plan);
                        door.setStructureId(struct.getWurmId());
                        struct.addDoor(door);
                        door.save();
                        door.addToTiles();
                    }
                }
            }
            struct.setFinished(true);
            struct.setFinalFinished(true);
            for (final VolaTile bt : struct.getStructureTiles()) {
                final Floor floor = new DbFloor(StructureConstants.FloorType.FLOOR, bt.getTileX(), bt.getTileY(), 0, 80.0f, struct.getWurmId(), StructureConstants.FloorMaterial.WOOD, 0);
                floor.setFloorState(StructureConstants.FloorState.COMPLETED);
                bt.addFloor(floor);
                floor.save();
                final Floor roof = new DbFloor(StructureConstants.FloorType.ROOF, bt.getTileX(), bt.getTileY(), 30, 80.0f, struct.getWurmId(), StructureConstants.FloorMaterial.THATCH, 0);
                roof.setFloorState(StructureConstants.FloorState.COMPLETED);
                bt.addFloor(roof);
                roof.save();
            }
        }
        catch (Exception ex) {
            Structures.logger.log(Level.WARNING, "exception " + ex, ex);
            creator.getCommunicator().sendAlertServerMessage(ex.getMessage());
        }
    }
    
    public static final void createSquareStructure(final Creature creator, final int stx, final int endtx, final int sty, final int endty, final int centerx, final int centery, final byte material, final String sname) {
        if (creator.getCurrentTile() != null) {
            if (creator.getCurrentTile().getStructure() != null) {
                return;
            }
        }
        try {
            final Structure struct = createStructure((byte)0, sname, WurmId.getNextPlanId(), centerx, centery, true);
            for (int currx = stx; currx <= endtx; ++currx) {
                for (int curry = sty; curry <= endty; ++curry) {
                    final VolaTile vtile = Zones.getOrCreateTile(currx, curry, true);
                    struct.addBuildTile(vtile, false);
                    struct.clearAllWallsAndMakeWallsForStructureBorder(vtile);
                }
            }
            final float rot = Creature.normalizeAngle(creator.getStatus().getRotation());
            struct.makeFinal(creator, sname);
            for (int currx2 = stx; currx2 <= endtx; ++currx2) {
                for (int curry2 = sty; curry2 <= endty; ++curry2) {
                    final VolaTile vtile2 = Zones.getOrCreateTile(currx2, curry2, true);
                    StructureTypeEnum wtype = StructureTypeEnum.SOLID;
                    if (Server.rand.nextInt(2) == 0) {
                        wtype = StructureTypeEnum.WINDOW;
                    }
                    if (currx2 == stx) {
                        for (final Wall plan : vtile2.getWalls()) {
                            if (!plan.isHorizontal() && plan.getStartX() == currx2) {
                                if (curry2 == creator.getTileY() && rot <= 315.0f && rot >= 235.0f) {
                                    wtype = StructureTypeEnum.DOOR;
                                }
                                if (material == 15) {
                                    plan.setMaterial(StructureMaterialEnum.STONE);
                                }
                                else {
                                    plan.setMaterial(StructureMaterialEnum.WOOD);
                                }
                                plan.setType(wtype);
                                plan.setQualityLevel(80.0f);
                                plan.setState(StructureStateEnum.FINISHED);
                                vtile2.updateWall(plan);
                                if (plan.isDoor()) {
                                    final Door door = new DbDoor(plan);
                                    door.setStructureId(struct.getWurmId());
                                    struct.addDoor(door);
                                    door.save();
                                    door.addToTiles();
                                }
                            }
                        }
                    }
                    if (curry2 == sty) {
                        for (final Wall plan : vtile2.getWalls()) {
                            if (plan.isHorizontal() && plan.getStartY() == curry2) {
                                if (currx2 == creator.getTileX() && ((rot >= 315.0f && rot <= 360.0f) || (rot >= 0.0f && rot <= 45.0f))) {
                                    wtype = StructureTypeEnum.DOOR;
                                }
                                if (material == 15) {
                                    plan.setMaterial(StructureMaterialEnum.STONE);
                                }
                                else {
                                    plan.setMaterial(StructureMaterialEnum.WOOD);
                                }
                                plan.setType(wtype);
                                plan.setQualityLevel(80.0f);
                                plan.setState(StructureStateEnum.FINISHED);
                                vtile2.updateWall(plan);
                                if (plan.isDoor()) {
                                    final Door door = new DbDoor(plan);
                                    door.setStructureId(struct.getWurmId());
                                    struct.addDoor(door);
                                    door.save();
                                    door.addToTiles();
                                }
                            }
                        }
                    }
                    if (curry2 == endty) {
                        for (final Wall plan : vtile2.getWalls()) {
                            if (plan.isHorizontal() && plan.getStartY() == curry2 + 1) {
                                if (currx2 == creator.getTileX() && rot >= 135.0f && rot <= 215.0f) {
                                    wtype = StructureTypeEnum.DOOR;
                                }
                                if (material == 15) {
                                    plan.setMaterial(StructureMaterialEnum.STONE);
                                }
                                else {
                                    plan.setMaterial(StructureMaterialEnum.WOOD);
                                }
                                plan.setType(wtype);
                                plan.setQualityLevel(80.0f);
                                plan.setState(StructureStateEnum.FINISHED);
                                vtile2.updateWall(plan);
                                if (plan.isDoor()) {
                                    final Door door = new DbDoor(plan);
                                    door.setStructureId(struct.getWurmId());
                                    struct.addDoor(door);
                                    door.save();
                                    door.addToTiles();
                                }
                            }
                        }
                    }
                    if (currx2 == endtx) {
                        for (final Wall plan : vtile2.getWalls()) {
                            if (!plan.isHorizontal() && plan.getStartX() == currx2 + 1) {
                                if (curry2 == creator.getTileY() && rot <= 135.0f && rot >= 45.0f) {
                                    wtype = StructureTypeEnum.DOOR;
                                }
                                if (material == 15) {
                                    plan.setMaterial(StructureMaterialEnum.STONE);
                                }
                                else {
                                    plan.setMaterial(StructureMaterialEnum.WOOD);
                                }
                                plan.setType(wtype);
                                plan.setQualityLevel(80.0f);
                                plan.setState(StructureStateEnum.FINISHED);
                                vtile2.updateWall(plan);
                                if (plan.isDoor()) {
                                    final Door door = new DbDoor(plan);
                                    door.setStructureId(struct.getWurmId());
                                    struct.addDoor(door);
                                    door.save();
                                    door.addToTiles();
                                }
                            }
                        }
                    }
                }
            }
            struct.setFinished(true);
            struct.setFinalFinished(true);
            for (int currx2 = stx; currx2 <= endtx; ++currx2) {
                for (int curry2 = sty; curry2 <= endty; ++curry2) {
                    final VolaTile vtile2 = Zones.getOrCreateTile(currx2, curry2, true);
                    final Floor floor = new DbFloor(StructureConstants.FloorType.FLOOR, currx2, curry2, 0, 80.0f, struct.getWurmId(), StructureConstants.FloorMaterial.WOOD, 0);
                    floor.setFloorState(StructureConstants.FloorState.COMPLETED);
                    vtile2.addFloor(floor);
                    floor.save();
                    final Floor roof = new DbFloor(StructureConstants.FloorType.ROOF, currx2, curry2, 30, 80.0f, struct.getWurmId(), StructureConstants.FloorMaterial.THATCH, 0);
                    roof.setFloorState(StructureConstants.FloorState.COMPLETED);
                    vtile2.addFloor(roof);
                    roof.save();
                }
            }
        }
        catch (Exception ex) {
            Structures.logger.log(Level.WARNING, "exception " + ex, ex);
            creator.getCommunicator().sendAlertServerMessage(ex.getMessage());
        }
    }
    
    static {
        emptyStructures = new Structure[0];
        logger = Logger.getLogger(Structures.class.getName());
    }
}
