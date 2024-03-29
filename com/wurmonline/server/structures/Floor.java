// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import java.util.HashMap;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.highways.MethodsHighways;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.HashSet;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.math.Vector3f;
import java.util.Set;
import java.util.Map;
import com.wurmonline.shared.constants.StructureConstants;
import java.util.logging.Logger;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public abstract class Floor implements MiscConstants, TimeConstants, Blocker, IFloor, Permissions.IAllow
{
    private static final Logger logger;
    private long structureId;
    private int number;
    float originalQL;
    float currentQL;
    float damage;
    private int tilex;
    private int tiley;
    private int heightOffset;
    long lastUsed;
    private StructureConstants.FloorType type;
    private StructureConstants.FloorMaterial material;
    private StructureConstants.FloorState floorState;
    protected byte dbState;
    private byte layer;
    private int floorLevel;
    private int color;
    private byte direction;
    private static final byte FLOOR_DBSTATE_PLANNED = -1;
    private static final byte FLOOR_DBSTATE_UNINITIALIZED = 0;
    private static final Map<Long, Set<Floor>> floors;
    private static final String GETALLFLOORS = "SELECT * FROM FLOORS";
    private static final Vector3f normal;
    Permissions permissions;
    private Vector3f centerPoint;
    
    public Floor(final int id, final StructureConstants.FloorType floorType, final int aTileX, final int aTileY, final byte adbState, final int aheightOffset, final float ql, final long structure, final StructureConstants.FloorMaterial floorMaterial, final int aLayer, final float origQl, final float dam, final long lastmaint, final byte dir) {
        this.structureId = -10L;
        this.number = -10;
        this.type = StructureConstants.FloorType.FLOOR;
        this.material = StructureConstants.FloorMaterial.WOOD;
        this.floorState = StructureConstants.FloorState.PLANNING;
        this.dbState = -1;
        this.layer = 0;
        this.floorLevel = 0;
        this.color = -1;
        this.direction = 0;
        this.permissions = new Permissions();
        this.setNumber(id);
        this.type = floorType;
        this.tilex = aTileX;
        this.tiley = aTileY;
        this.dbState = adbState;
        this.floorState = StructureConstants.FloorState.fromByte(this.dbState);
        this.heightOffset = aheightOffset;
        this.currentQL = ql;
        this.originalQL = origQl;
        this.damage = dam;
        this.structureId = structure;
        this.material = floorMaterial;
        this.layer = (byte)(aLayer & 0xFF);
        this.lastUsed = lastmaint;
        this.direction = dir;
        this.setFloorLevel();
    }
    
    public Floor(final StructureConstants.FloorType floorType, final int aTileX, final int aTileY, final int height, final float ql, final long structure, final StructureConstants.FloorMaterial floorMaterial, final int aLayer) {
        this.structureId = -10L;
        this.number = -10;
        this.type = StructureConstants.FloorType.FLOOR;
        this.material = StructureConstants.FloorMaterial.WOOD;
        this.floorState = StructureConstants.FloorState.PLANNING;
        this.dbState = -1;
        this.layer = 0;
        this.floorLevel = 0;
        this.color = -1;
        this.direction = 0;
        this.permissions = new Permissions();
        this.type = floorType;
        this.tilex = aTileX;
        this.tiley = aTileY;
        this.heightOffset = height;
        this.currentQL = ql;
        this.structureId = structure;
        this.material = floorMaterial;
        this.layer = (byte)(aLayer & 0xFF);
        this.setFloorLevel();
    }
    
    @Override
    public boolean isFloor() {
        return true;
    }
    
    @Override
    public final boolean isStair() {
        return this.isFinished() && this.type.isStair();
    }
    
    @Override
    public final Vector3f getNormal() {
        return Floor.normal;
    }
    
    private final Vector3f calculateCenterPoint() {
        return new Vector3f(this.tilex * 4 + 2, this.tiley * 4 + 2, this.getMinZ() + (this.isRoof() ? 1.0f : 0.125f));
    }
    
    @Override
    public final Vector3f getCenterPoint() {
        if (this.centerPoint == null) {
            this.centerPoint = this.calculateCenterPoint();
        }
        return this.centerPoint;
    }
    
    @Override
    public int getTileX() {
        return this.tilex;
    }
    
    void setTilex(final int aTilex) {
        this.tilex = aTilex;
    }
    
    @Override
    public int getTileY() {
        return this.tiley;
    }
    
    void setTiley(final int aTiley) {
        this.tiley = aTiley;
    }
    
    @Override
    public final float getPositionX() {
        return this.tilex * 4;
    }
    
    @Override
    public final float getPositionY() {
        return this.tiley * 4;
    }
    
    public int getHeightOffset() {
        return this.heightOffset;
    }
    
    void setHeightOffset(final int aHeightOffset) {
        this.heightOffset = aHeightOffset;
    }
    
    public byte getLayer() {
        return this.layer;
    }
    
    public byte getDir() {
        return this.direction;
    }
    
    public boolean leavingStairOnTop(final int tilexDiff, final int tileyDiff) {
        if (this.isStair()) {
            if (this.getDir() == 0) {
                return tileyDiff < 0;
            }
            if (this.getDir() == 2) {
                return tilexDiff > 0;
            }
            if (this.getDir() == 6) {
                return tilexDiff < 0;
            }
            if (this.getDir() == 4) {
                return tileyDiff > 0;
            }
        }
        return false;
    }
    
    public void setLayer(final byte newLayer) {
        this.layer = newLayer;
    }
    
    @Override
    public boolean isOnSurface() {
        return this.layer == 0;
    }
    
    public void rotate(final int change) {
        this.direction = (byte)((this.direction + 8 + change) % 8);
        try {
            this.save();
        }
        catch (IOException e) {
            Floor.logger.log(Level.WARNING, e.getMessage(), e);
        }
        final VolaTile volaTile = Zones.getOrCreateTile(this.getTileX(), this.getTileY(), this.getLayer() >= 0);
        volaTile.updateFloor(this);
    }
    
    @Override
    public boolean isFinished() {
        return this.floorState == StructureConstants.FloorState.COMPLETED;
    }
    
    @Override
    public final boolean isMetal() {
        return this.material == StructureConstants.FloorMaterial.METAL_COPPER || this.material == StructureConstants.FloorMaterial.METAL_GOLD || this.material == StructureConstants.FloorMaterial.METAL_IRON || this.material == StructureConstants.FloorMaterial.METAL_SILVER || this.material == StructureConstants.FloorMaterial.METAL_STEEL;
    }
    
    @Override
    public final boolean isWood() {
        return this.material == StructureConstants.FloorMaterial.WOOD || this.material == StructureConstants.FloorMaterial.THATCH || this.material == StructureConstants.FloorMaterial.STANDALONE;
    }
    
    @Override
    public final boolean isStone() {
        return this.material == StructureConstants.FloorMaterial.STONE_BRICK || this.material == StructureConstants.FloorMaterial.STONE_SLAB;
    }
    
    @Override
    public final boolean isSlate() {
        return this.material == StructureConstants.FloorMaterial.SLATE_SLAB;
    }
    
    @Override
    public final boolean isMarble() {
        return this.material == StructureConstants.FloorMaterial.MARBLE_SLAB;
    }
    
    @Override
    public final boolean isSandstone() {
        return this.material == StructureConstants.FloorMaterial.SANDSTONE_SLAB;
    }
    
    public final boolean isGold() {
        return this.material == StructureConstants.FloorMaterial.METAL_GOLD;
    }
    
    public final boolean isSilver() {
        return this.material == StructureConstants.FloorMaterial.METAL_SILVER;
    }
    
    public final boolean isIron() {
        return this.material == StructureConstants.FloorMaterial.METAL_IRON;
    }
    
    public final boolean isSteel() {
        return this.material == StructureConstants.FloorMaterial.METAL_STEEL;
    }
    
    public final boolean isCopper() {
        return this.material == StructureConstants.FloorMaterial.METAL_COPPER;
    }
    
    @Override
    public final boolean isThatch() {
        return this.material == StructureConstants.FloorMaterial.THATCH;
    }
    
    public final boolean isClay() {
        return this.material == StructureConstants.FloorMaterial.CLAY_BRICK;
    }
    
    public final boolean isSolid() {
        return this.isFinished() && !this.isStair();
    }
    
    public StructureConstants.FloorType getType() {
        return this.type;
    }
    
    public void setType(final StructureConstants.FloorType newType) {
        this.type = newType;
    }
    
    public StructureConstants.FloorMaterial getMaterial() {
        return this.material;
    }
    
    public void setMaterial(final StructureConstants.FloorMaterial newMaterial) {
        this.material = newMaterial;
    }
    
    public abstract void save() throws IOException;
    
    @Override
    public long getId() {
        return Tiles.getFloorId(this.tilex, this.tiley, this.heightOffset, this.getLayer());
    }
    
    public static int getHeightOffsetFromWurmId(final long wurmId) {
        return (int)(wurmId >> 48) & 0xFFFF;
    }
    
    public StructureConstants.FloorState getFloorState() {
        return this.floorState;
    }
    
    long getLastUsed() {
        return this.lastUsed;
    }
    
    @Override
    public abstract void setLastUsed(final long p0);
    
    int getNumber() {
        return this.number;
    }
    
    void setNumber(final int aNumber) {
        this.number = aNumber;
    }
    
    public float getOriginalQL() {
        return this.originalQL;
    }
    
    public float getCurrentQL() {
        return this.currentQL;
    }
    
    public byte getState() {
        return this.dbState;
    }
    
    protected abstract void setState(final byte p0);
    
    public void setFloorState(final StructureConstants.FloorState aFloorState) {
        this.floorState = aFloorState;
        switch (this.floorState) {
            case BUILDING: {
                if (this.getState() <= 0) {
                    this.setState((byte)0);
                    break;
                }
                break;
            }
            case COMPLETED: {
                this.setState(StructureConstants.FloorState.COMPLETED.getCode());
                break;
            }
            case PLANNING: {
                this.setState((byte)(-1));
                break;
            }
        }
    }
    
    int getColor() {
        return this.color;
    }
    
    void setColor(final int aColor) {
        this.color = aColor;
    }
    
    public abstract void delete();
    
    @Override
    public String getName() {
        switch (this.type) {
            case DOOR: {
                return "hatch";
            }
            case OPENING: {
                return "opening";
            }
            case ROOF: {
                return "roof";
            }
            case FLOOR: {
                return "floor";
            }
            case STAIRCASE: {
                return "staircase";
            }
            case WIDE_STAIRCASE: {
                return "wide staircase";
            }
            case WIDE_STAIRCASE_RIGHT: {
                return "wide staircase with banisters on right";
            }
            case WIDE_STAIRCASE_LEFT: {
                return "wide staircase with banisters on left";
            }
            case WIDE_STAIRCASE_BOTH: {
                return "wide staircase with banisiters on both sides";
            }
            case RIGHT_STAIRCASE: {
                return "right staircase";
            }
            case LEFT_STAIRCASE: {
                return "left staircase";
            }
            case CLOCKWISE_STAIRCASE: {
                return "clockwise spiral staircase";
            }
            case CLOCKWISE_STAIRCASE_WITH: {
                return "clockwise spiral staircase with banisters";
            }
            case ANTICLOCKWISE_STAIRCASE: {
                return "counter clockwise spiral staircase";
            }
            case ANTICLOCKWISE_STAIRCASE_WITH: {
                return "counter clockwise spiral staircase with banisters";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public final boolean isOpening() {
        return this.type == StructureConstants.FloorType.OPENING;
    }
    
    @Override
    public boolean isHorizontal() {
        return false;
    }
    
    @Override
    public final Vector3f isBlocking(final Creature creature, final Vector3f startPos, final Vector3f endPos, final Vector3f aNormal, final int blockType, final long target, final boolean followGround) {
        if (target == this.getId()) {
            return null;
        }
        if (this.isAPlan()) {
            return null;
        }
        if (this.isOpening()) {
            return null;
        }
        final Vector3f inter = this.getIntersectionPoint(startPos, endPos, aNormal, creature);
        return inter;
    }
    
    @Override
    public final boolean isDoor() {
        return this.type == StructureConstants.FloorType.DOOR;
    }
    
    @Override
    public final boolean isRoof() {
        return this.type == StructureConstants.FloorType.ROOF;
    }
    
    @Override
    public final boolean isTile() {
        return false;
    }
    
    @Override
    public final boolean canBeOpenedBy(final Creature creature, final boolean wentThroughDoor) {
        return true;
    }
    
    @Override
    public final float getBlockPercent(final Creature creature) {
        if (this.isFinished()) {
            return 100.0f;
        }
        return Math.max(0, this.getState());
    }
    
    @Override
    public final boolean isWithinFloorLevels(final int maxFloorLevel, final int minFloorLevel) {
        return this.floorLevel <= maxFloorLevel && this.floorLevel >= minFloorLevel;
    }
    
    public final Vector3f getIntersectionPoint(final Vector3f startPos, final Vector3f endPos, final Vector3f aNormal, final Creature creature) {
        if (this.isWithinBounds(startPos, creature)) {
            return startPos.clone();
        }
        float zPlane = this.getMinZ();
        if (Math.abs(startPos.z - this.getMinZ()) > Math.abs(startPos.z - this.getMaxZ())) {
            zPlane = this.getMaxZ();
        }
        float xPlane = this.getMinX() * 4;
        if (Math.abs(startPos.x - this.getMinX() * 4) > Math.abs(startPos.x - (this.getMinX() * 4 + 4))) {
            xPlane += 4.0f;
        }
        float yPlane = this.getMinY() * 4;
        if (Math.abs(startPos.y - this.getMinY() * 4) > Math.abs(startPos.y - (this.getMinY() * 4 + 4))) {
            yPlane += 4.0f;
        }
        for (int i = 0; i < 3; ++i) {
            final float planeVal = (i == 0) ? zPlane : ((i == 1) ? xPlane : yPlane);
            final Vector3f centerPoint = this.getCenterPoint().clone();
            switch (i) {
                case 0: {
                    centerPoint.setZ(planeVal);
                    break;
                }
                case 1: {
                    centerPoint.setX(planeVal);
                    break;
                }
                case 2: {
                    centerPoint.setY(planeVal);
                    break;
                }
            }
            final Vector3f diff = startPos.subtract(centerPoint);
            final float diffVal = (i == 0) ? diff.z : ((i == 1) ? diff.x : diff.y);
            final float normalVal = (i == 0) ? aNormal.z : ((i == 1) ? aNormal.x : aNormal.y);
            if (normalVal != 0.0f) {
                final float steps = diffVal / normalVal;
                final Vector3f intersection = startPos.add(aNormal.mult(-steps));
                final Vector3f diffend = endPos.subtract(startPos);
                final Vector3f interDiff = intersection.subtract(startPos);
                if (interDiff.length() < diffend.length() && this.isWithinBounds(intersection, creature)) {
                    final float u = aNormal.dot(centerPoint.subtract(startPos)) / aNormal.dot(endPos.subtract(startPos));
                    if (u >= 0.0f && u <= 1.0f) {
                        return intersection;
                    }
                }
            }
        }
        return null;
    }
    
    private final boolean isWithinBounds(final Vector3f pointToCheck, final Creature creature) {
        return pointToCheck.getY() >= this.tiley * 4 && pointToCheck.getY() <= (this.tiley + 1) * 4 && pointToCheck.getX() >= this.tilex * 4 && pointToCheck.getX() <= (this.tilex + 1) * 4 && this.isWithinZ(pointToCheck.getZ(), pointToCheck.getZ(), creature != null && creature.followsGround());
    }
    
    public static final void loadAllFloors() throws IOException {
        Floor.logger.log(Level.INFO, "Loading all floors.");
        final long s = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM FLOORS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final StructureConstants.FloorType floorType = StructureConstants.FloorType.fromByte(rs.getByte("TYPE"));
                final StructureConstants.FloorMaterial floorMaterial = StructureConstants.FloorMaterial.fromByte(rs.getByte("MATERIAL"));
                final long sid = rs.getLong("STRUCTURE");
                Set<Floor> flset = Floor.floors.get(sid);
                if (flset == null) {
                    flset = new HashSet<Floor>();
                    Floor.floors.put(sid, flset);
                }
                flset.add(new DbFloor(rs.getInt("ID"), floorType, rs.getInt("TILEX"), rs.getInt("TILEY"), rs.getByte("STATE"), rs.getInt("HEIGHTOFFSET"), rs.getFloat("CURRENTQL"), sid, floorMaterial, rs.getInt("LAYER"), rs.getFloat("ORIGINALQL"), rs.getFloat("DAMAGE"), rs.getLong("LASTMAINTAINED"), rs.getByte("DIR")));
            }
        }
        catch (SQLException sqx) {
            Floor.logger.log(Level.WARNING, "Failed to load walls!" + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long e = System.nanoTime();
            Floor.logger.log(Level.INFO, "Loaded " + Floor.floors.size() + " floors. That took " + (e - s) / 1000000.0f + " ms.");
        }
    }
    
    public static final Set<Floor> getFloorsFor(final long structureId) {
        return Floor.floors.get(structureId);
    }
    
    @Override
    public long getStructureId() {
        return this.structureId;
    }
    
    void setStructureId(final long aStructureId) {
        this.structureId = aStructureId;
    }
    
    @Override
    public boolean isAPlan() {
        return this.floorState == StructureConstants.FloorState.PLANNING;
    }
    
    public void revertToPlan() {
        MethodsHighways.removeNearbyMarkers(this);
        this.setFloorState(StructureConstants.FloorState.PLANNING);
        try {
            this.save();
        }
        catch (IOException e) {
            Floor.logger.log(Level.WARNING, e.getMessage(), e);
        }
        final VolaTile volaTile = Zones.getOrCreateTile(this.getTileX(), this.getTileY(), this.getLayer() >= 0);
        volaTile.updateFloor(this);
    }
    
    @Override
    public void destroyOrRevertToPlan() {
        Structure struct = null;
        try {
            struct = Structures.getStructure(this.getStructureId());
        }
        catch (NoSuchStructureException e) {
            Floor.logger.log(Level.WARNING, " Failed to find Structures.getStructure(" + this.getStructureId() + " for a Floor about to be deleted: " + e.getMessage(), e);
        }
        if (struct != null && struct.wouldCreateFlyingStructureIfRemoved(this)) {
            this.revertToPlan();
        }
        else {
            this.destroy();
        }
    }
    
    public void destroy() {
        this.delete();
        final Set<Floor> flset = Floor.floors.get(this.getStructureId());
        if (flset != null) {
            flset.remove(this);
        }
        final VolaTile volaTile = Zones.getOrCreateTile(this.getTileX(), this.getTileY(), this.getLayer() >= 0);
        volaTile.removeFloor(this);
    }
    
    @Override
    public final float getDamageModifierForItem(final Item item) {
        float mod = 0.0f;
        switch (this.material) {
            case METAL_COPPER:
            case METAL_GOLD:
            case METAL_SILVER: {
                if (item.isWeaponCrush()) {
                    mod = 0.03f;
                    break;
                }
                mod = 0.007f;
                break;
            }
            case METAL_IRON:
            case METAL_STEEL: {
                if (item.isWeaponCrush()) {
                    mod = 0.02f;
                    break;
                }
                mod = 0.007f;
                break;
            }
            case CLAY_BRICK:
            case SLATE_SLAB:
            case STONE_BRICK:
            case STONE_SLAB:
            case MARBLE_SLAB:
            case SANDSTONE_SLAB: {
                if (item.isWeaponCrush()) {
                    mod = 0.03f;
                    break;
                }
                mod = 0.007f;
                break;
            }
            case THATCH:
            case WOOD:
            case STANDALONE: {
                mod = 0.03f;
                break;
            }
            default: {
                mod = 0.0f;
                break;
            }
        }
        return mod;
    }
    
    @Override
    public final boolean isOnPvPServer() {
        return Zones.isOnPvPServer(this.tilex, this.tiley);
    }
    
    @Override
    public final int getFloorLevel() {
        return this.floorLevel;
    }
    
    private final void setFloorLevel() {
        this.floorLevel = this.heightOffset / 30;
    }
    
    public void buildProgress(int numSteps) {
        if (numSteps > 127) {
            numSteps = 127;
        }
        if (this.getFloorState() == StructureConstants.FloorState.BUILDING) {
            this.setState((byte)(this.getState() + numSteps));
        }
        else {
            Floor.logger.log(Level.WARNING, "buildProgress method called on floor when floor was not in buildable state: " + this.getId() + " " + this.floorState.toString());
        }
    }
    
    @Override
    public final VolaTile getTile() {
        try {
            final Zone zone = Zones.getZone(this.tilex, this.tiley, this.getLayer() == 0);
            final VolaTile toReturn = zone.getTileOrNull(this.tilex, this.tiley);
            if (toReturn != null) {
                if (toReturn.isTransition()) {
                    return Zones.getZone(this.tilex, this.tiley, false).getOrCreateTile(this.tilex, this.tiley);
                }
                return toReturn;
            }
            else {
                Floor.logger.log(Level.WARNING, "Tile not in zone, this shouldn't happen " + this.tilex + ", " + this.tiley);
            }
        }
        catch (NoSuchZoneException nsz) {
            Floor.logger.log(Level.WARNING, "This shouldn't happen " + this.tilex + ", " + this.tiley, nsz);
        }
        return null;
    }
    
    public final Village getVillage() {
        final VolaTile t = this.getTile();
        if (t != null && t.getVillage() != null) {
            return t.getVillage();
        }
        return null;
    }
    
    @Override
    public final float getDamageModifier() {
        return 100.0f / Math.max(1.0f, this.currentQL * (100.0f - this.damage) / 100.0f);
    }
    
    public final boolean poll(final long currTime, final VolaTile t, final Structure struct) {
        if (struct == null) {
            return true;
        }
        final HighwayPos highwaypos = MethodsHighways.getHighwayPos(this);
        if (highwaypos != null && MethodsHighways.onHighway(highwaypos)) {
            return false;
        }
        if (currTime - struct.getCreationDate() <= 172800000L) {
            return false;
        }
        float mod = 1.0f;
        final Village village = this.getVillage();
        if (village != null) {
            if (village.moreThanMonthLeft()) {
                return false;
            }
            if (!village.lessThanWeekLeft()) {
                mod *= 10.0f;
            }
        }
        else if (Zones.getKingdom(this.tilex, this.tiley) == 0) {
            mod *= 0.5f;
        }
        if (t != null && !t.isOnSurface()) {
            mod *= 0.75f;
        }
        if (currTime - this.lastUsed > (Servers.localServer.testServer ? (60000.0f * mod) : (8.64E7f * mod)) && !this.hasNoDecay()) {
            final long ownerId = struct.getOwnerId();
            if (ownerId == -10L) {
                this.damage += 20.0f + Server.rand.nextFloat() * 10.0f;
            }
            else {
                boolean ownerIsInactive = false;
                final long aMonth = Servers.isThisATestServer() ? 86400000L : 2419200000L;
                final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(ownerId);
                if (pInfo == null) {
                    ownerIsInactive = true;
                }
                else if (pInfo.lastLogin == 0L && pInfo.lastLogout < System.currentTimeMillis() - 3L * aMonth) {
                    ownerIsInactive = true;
                }
                if (ownerIsInactive) {
                    this.damage += 3.0f;
                }
                if (village == null && t != null) {
                    final Village v = Villages.getVillageWithPerimeterAt(t.tilex, t.tiley, t.isOnSurface());
                    if (v != null && !v.isCitizen(ownerId) && ownerIsInactive) {
                        this.damage += 3.0f;
                    }
                }
            }
            this.setLastUsed(currTime);
            if (this.setDamage(this.damage + 0.1f * this.getDamageModifier())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final float getCurrentQualityLevel() {
        return this.currentQL * Math.max(1.0f, 100.0f - this.damage) / 100.0f;
    }
    
    @Override
    public final int getRepairItemTemplate() {
        if (this.isWood()) {
            return 22;
        }
        if (this.isStone()) {
            return 132;
        }
        if (this.isSlate()) {
            return 770;
        }
        if (this.isMarble()) {
            return 786;
        }
        if (this.isSandstone()) {
            return 1121;
        }
        if (this.isGold()) {
            return 44;
        }
        if (this.isSilver()) {
            return 45;
        }
        if (this.isIron()) {
            return 46;
        }
        if (this.isSteel()) {
            return 205;
        }
        if (this.isCopper()) {
            return 47;
        }
        if (this.isThatch()) {
            return 756;
        }
        if (this.isClay()) {
            return 130;
        }
        return 22;
    }
    
    @Override
    public final int getStartX() {
        return this.getTileX();
    }
    
    @Override
    public final int getStartY() {
        return this.getTileY();
    }
    
    @Override
    public final int getMinX() {
        return this.getTileX();
    }
    
    @Override
    public final int getMinY() {
        return this.getTileY();
    }
    
    @Override
    public final boolean supports() {
        return true;
    }
    
    @Override
    public final boolean supports(final StructureSupport support) {
        if (!this.supports()) {
            return false;
        }
        if (support.isFloor()) {
            if (this.getFloorLevel() == support.getFloorLevel()) {
                if (this.getStartX() == support.getStartX()) {
                    if (this.getEndY() == support.getStartY() || this.getStartY() == support.getEndY()) {
                        return true;
                    }
                }
                else if (this.getStartY() == support.getStartY() && (this.getEndX() == support.getStartX() || this.getStartX() == support.getEndX())) {
                    return true;
                }
            }
        }
        else if (!support.supports()) {
            if (support.getFloorLevel() == this.getFloorLevel()) {
                return this.isOnSideOfThis(support);
            }
        }
        else if (support.getFloorLevel() >= this.getFloorLevel() - 1 && support.getFloorLevel() <= this.getFloorLevel()) {
            return this.isOnSideOfThis(support);
        }
        return false;
    }
    
    @Override
    public float getFloorZ() {
        return this.heightOffset / 10.0f;
    }
    
    @Override
    public float getMinZ() {
        return Zones.getHeightForNode(this.tilex, this.tiley, this.getLayer()) + this.getFloorZ();
    }
    
    @Override
    public float getMaxZ() {
        return this.getMinZ() + (this.isRoof() ? 2.0f : 0.25f);
    }
    
    @Override
    public boolean isWithinZ(final float maxZ, final float minZ, final boolean followGround) {
        return this.getFloorLevel() > 0 && minZ <= this.getMaxZ() && maxZ >= this.getMinZ();
    }
    
    @Override
    public final boolean equals(final StructureSupport support) {
        return this == support || (support != null && support.getId() == this.getId());
    }
    
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        final Floor support = (Floor)other;
        return support.getId() == this.getId();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)this.getId();
        return result;
    }
    
    private final boolean isOnSideOfThis(final StructureSupport support) {
        if (support.isHorizontal()) {
            if (support.getMinX() == this.getMinX() && (support.getMinY() == this.getMinY() || support.getMinY() == this.getMinY() + 1)) {
                return true;
            }
        }
        else if (support.getMinY() == this.getMinY() && (support.getMinX() == this.getMinX() || support.getMinX() == this.getMinX() + 1)) {
            return true;
        }
        return false;
    }
    
    @Override
    public final int getEndX() {
        return this.getStartX() + 1;
    }
    
    @Override
    public final int getEndY() {
        return this.getStartY() + 1;
    }
    
    @Override
    public boolean isSupportedByGround() {
        return this.getFloorLevel() == 0;
    }
    
    @Override
    public String toString() {
        return "Floor [number=" + this.number + ", structureId=" + this.structureId + ", type=" + this.type + "]";
    }
    
    @Override
    public long getTempId() {
        return -10L;
    }
    
    @Override
    public boolean canBeAlwaysLit() {
        return false;
    }
    
    @Override
    public boolean canBeAutoFilled() {
        return false;
    }
    
    @Override
    public boolean canBeAutoLit() {
        return false;
    }
    
    @Override
    public final boolean canBePeggedByPlayer() {
        return false;
    }
    
    @Override
    public boolean canBePlanted() {
        return false;
    }
    
    @Override
    public final boolean canBeSealedByPlayer() {
        return false;
    }
    
    @Override
    public boolean canChangeCreator() {
        return false;
    }
    
    @Override
    public boolean canDisableDecay() {
        return true;
    }
    
    @Override
    public boolean canDisableDestroy() {
        return true;
    }
    
    @Override
    public boolean canDisableDrag() {
        return false;
    }
    
    @Override
    public boolean canDisableDrop() {
        return false;
    }
    
    @Override
    public boolean canDisableEatAndDrink() {
        return false;
    }
    
    @Override
    public boolean canDisableImprove() {
        return true;
    }
    
    @Override
    public boolean canDisableLocking() {
        return false;
    }
    
    @Override
    public boolean canDisableLockpicking() {
        return false;
    }
    
    @Override
    public boolean canDisableMoveable() {
        return false;
    }
    
    @Override
    public final boolean canDisableOwnerMoveing() {
        return false;
    }
    
    @Override
    public final boolean canDisableOwnerTurning() {
        return false;
    }
    
    @Override
    public boolean canDisablePainting() {
        return false;
    }
    
    @Override
    public boolean canDisablePut() {
        return false;
    }
    
    @Override
    public boolean canDisableRepair() {
        return true;
    }
    
    @Override
    public boolean canDisableRuneing() {
        return false;
    }
    
    @Override
    public boolean canDisableSpellTarget() {
        return false;
    }
    
    @Override
    public boolean canDisableTake() {
        return false;
    }
    
    @Override
    public boolean canDisableTurning() {
        return true;
    }
    
    @Override
    public boolean canHaveCourier() {
        return false;
    }
    
    @Override
    public boolean canHaveDakrMessenger() {
        return false;
    }
    
    @Override
    public String getCreatorName() {
        return null;
    }
    
    @Override
    public float getDamage() {
        return this.damage;
    }
    
    @Override
    public float getQualityLevel() {
        return this.currentQL;
    }
    
    @Override
    public boolean hasCourier() {
        return this.permissions.hasPermission(Permissions.Allow.HAS_COURIER.getBit());
    }
    
    @Override
    public boolean hasDarkMessenger() {
        return this.permissions.hasPermission(Permissions.Allow.HAS_DARK_MESSENGER.getBit());
    }
    
    @Override
    public boolean hasNoDecay() {
        return this.permissions.hasPermission(Permissions.Allow.DECAY_DISABLED.getBit());
    }
    
    @Override
    public boolean isAlwaysLit() {
        return this.permissions.hasPermission(Permissions.Allow.ALWAYS_LIT.getBit());
    }
    
    @Override
    public boolean isAutoFilled() {
        return this.permissions.hasPermission(Permissions.Allow.AUTO_FILL.getBit());
    }
    
    @Override
    public boolean isAutoLit() {
        return this.permissions.hasPermission(Permissions.Allow.AUTO_LIGHT.getBit());
    }
    
    @Override
    public boolean isIndestructible() {
        return this.permissions.hasPermission(Permissions.Allow.NO_BASH.getBit());
    }
    
    @Override
    public boolean isNoDrag() {
        return this.permissions.hasPermission(Permissions.Allow.NO_DRAG.getBit());
    }
    
    @Override
    public boolean isNoDrop() {
        return this.permissions.hasPermission(Permissions.Allow.NO_DROP.getBit());
    }
    
    @Override
    public boolean isNoEatOrDrink() {
        return this.permissions.hasPermission(Permissions.Allow.NO_EAT_OR_DRINK.getBit());
    }
    
    @Override
    public boolean isNoImprove() {
        return this.permissions.hasPermission(Permissions.Allow.NO_IMPROVE.getBit());
    }
    
    @Override
    public boolean isNoMove() {
        return this.permissions.hasPermission(Permissions.Allow.NOT_MOVEABLE.getBit());
    }
    
    @Override
    public boolean isNoPut() {
        return this.permissions.hasPermission(Permissions.Allow.NO_PUT.getBit());
    }
    
    @Override
    public boolean isNoRepair() {
        return this.permissions.hasPermission(Permissions.Allow.NO_REPAIR.getBit());
    }
    
    @Override
    public boolean isNoTake() {
        return this.permissions.hasPermission(Permissions.Allow.NO_TAKE.getBit());
    }
    
    @Override
    public boolean isNotLockable() {
        return this.permissions.hasPermission(Permissions.Allow.NOT_LOCKABLE.getBit());
    }
    
    @Override
    public boolean isNotLockpickable() {
        return this.permissions.hasPermission(Permissions.Allow.NOT_LOCKPICKABLE.getBit());
    }
    
    @Override
    public boolean isNotPaintable() {
        return this.permissions.hasPermission(Permissions.Allow.NOT_PAINTABLE.getBit());
    }
    
    @Override
    public boolean isNotRuneable() {
        return true;
    }
    
    @Override
    public boolean isNotSpellTarget() {
        return this.permissions.hasPermission(Permissions.Allow.NO_SPELLS.getBit());
    }
    
    @Override
    public boolean isNotTurnable() {
        return this.permissions.hasPermission(Permissions.Allow.NOT_TURNABLE.getBit());
    }
    
    @Override
    public boolean isOwnerMoveable() {
        return this.permissions.hasPermission(Permissions.Allow.OWNER_MOVEABLE.getBit());
    }
    
    @Override
    public boolean isOwnerTurnable() {
        return this.permissions.hasPermission(Permissions.Allow.OWNER_TURNABLE.getBit());
    }
    
    @Override
    public boolean isPlanted() {
        return this.permissions.hasPermission(Permissions.Allow.PLANTED.getBit());
    }
    
    @Override
    public final boolean isSealedByPlayer() {
        return this.permissions.hasPermission(Permissions.Allow.SEALED_BY_PLAYER.getBit());
    }
    
    @Override
    public void setCreator(final String aNewCreator) {
    }
    
    @Override
    public abstract boolean setDamage(final float p0);
    
    @Override
    public void setHasCourier(final boolean aCourier) {
        this.permissions.setPermissionBit(Permissions.Allow.HAS_COURIER.getBit(), aCourier);
    }
    
    @Override
    public void setHasDarkMessenger(final boolean aDarkmessenger) {
        this.permissions.setPermissionBit(Permissions.Allow.HAS_DARK_MESSENGER.getBit(), aDarkmessenger);
    }
    
    @Override
    public void setHasNoDecay(final boolean aNoDecay) {
        this.permissions.setPermissionBit(Permissions.Allow.DECAY_DISABLED.getBit(), aNoDecay);
    }
    
    @Override
    public void setIsAlwaysLit(final boolean aAlwaysLit) {
        this.permissions.setPermissionBit(Permissions.Allow.ALWAYS_LIT.getBit(), aAlwaysLit);
    }
    
    @Override
    public void setIsAutoFilled(final boolean aAutoFill) {
        this.permissions.setPermissionBit(Permissions.Allow.AUTO_FILL.getBit(), aAutoFill);
    }
    
    @Override
    public void setIsAutoLit(final boolean aAutoLight) {
        this.permissions.setPermissionBit(Permissions.Allow.AUTO_LIGHT.getBit(), aAutoLight);
    }
    
    @Override
    public void setIsIndestructible(final boolean aNoDestroy) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_BASH.getBit(), aNoDestroy);
    }
    
    @Override
    public void setIsNoDrag(final boolean aNoDrag) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_DRAG.getBit(), aNoDrag);
    }
    
    @Override
    public void setIsNoDrop(final boolean aNoDrop) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_DROP.getBit(), aNoDrop);
    }
    
    @Override
    public void setIsNoEatOrDrink(final boolean aNoEatOrDrink) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_EAT_OR_DRINK.getBit(), aNoEatOrDrink);
    }
    
    @Override
    public void setIsNoImprove(final boolean aNoImprove) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_IMPROVE.getBit(), aNoImprove);
    }
    
    @Override
    public void setIsNoMove(final boolean aNoMove) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_MOVEABLE.getBit(), aNoMove);
    }
    
    @Override
    public void setIsNoPut(final boolean aNoPut) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_PUT.getBit(), aNoPut);
    }
    
    @Override
    public void setIsNoRepair(final boolean aNoRepair) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_REPAIR.getBit(), aNoRepair);
    }
    
    @Override
    public void setIsNoTake(final boolean aNoTake) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_TAKE.getBit(), aNoTake);
    }
    
    @Override
    public void setIsNotLockable(final boolean aNoLock) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_LOCKABLE.getBit(), aNoLock);
    }
    
    @Override
    public void setIsNotLockpickable(final boolean aNoLockpick) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_LOCKPICKABLE.getBit(), aNoLockpick);
    }
    
    @Override
    public void setIsNotPaintable(final boolean aNoPaint) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_PAINTABLE.getBit(), aNoPaint);
    }
    
    @Override
    public void setIsNotRuneable(final boolean aNoRune) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_RUNEABLE.getBit(), aNoRune);
    }
    
    @Override
    public void setIsNotSpellTarget(final boolean aNoSpells) {
        this.permissions.setPermissionBit(Permissions.Allow.NO_SPELLS.getBit(), aNoSpells);
    }
    
    @Override
    public void setIsNotTurnable(final boolean aNoTurn) {
        this.permissions.setPermissionBit(Permissions.Allow.NOT_TURNABLE.getBit(), aNoTurn);
    }
    
    @Override
    public void setIsOwnerMoveable(final boolean aOwnerMove) {
        this.permissions.setPermissionBit(Permissions.Allow.OWNER_MOVEABLE.getBit(), aOwnerMove);
    }
    
    @Override
    public void setIsOwnerTurnable(final boolean aOwnerTurn) {
        this.permissions.setPermissionBit(Permissions.Allow.OWNER_TURNABLE.getBit(), aOwnerTurn);
    }
    
    @Override
    public void setIsPlanted(final boolean aPlant) {
        this.permissions.setPermissionBit(Permissions.Allow.PLANTED.getBit(), aPlant);
    }
    
    @Override
    public void setIsSealedByPlayer(final boolean aSealed) {
        this.permissions.setPermissionBit(Permissions.Allow.SEALED_BY_PLAYER.getBit(), aSealed);
    }
    
    @Override
    public abstract boolean setQualityLevel(final float p0);
    
    @Override
    public void setOriginalQualityLevel(final float newQL) {
    }
    
    @Override
    public abstract void savePermissions();
    
    @Override
    public final boolean isOnSouthBorder(final TilePos pos) {
        return false;
    }
    
    @Override
    public final boolean isOnNorthBorder(final TilePos pos) {
        return false;
    }
    
    @Override
    public final boolean isOnWestBorder(final TilePos pos) {
        return false;
    }
    
    @Override
    public final boolean isOnEastBorder(final TilePos pos) {
        return false;
    }
    
    static {
        logger = Logger.getLogger(Wall.class.getName());
        floors = new HashMap<Long, Set<Floor>>();
        normal = new Vector3f(0.0f, 0.0f, 1.0f);
    }
}
