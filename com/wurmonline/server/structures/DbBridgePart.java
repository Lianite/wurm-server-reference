// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.zones.VolaTile;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.shared.constants.BridgeConstants;
import java.util.logging.Logger;

public class DbBridgePart extends BridgePart
{
    private static final String CREATEBRIDGEPART = "INSERT INTO BRIDGEPARTS(TYPE, LASTMAINTAINED , CURRENTQL, ORIGINALQL, DAMAGE, STRUCTURE, TILEX, TILEY, STATE, MATERIAL, HEIGHTOFFSET, DIR, SLOPE, STAGECOUNT, NORTHEXIT, EASTEXIT, SOUTHEXIT, WESTEXIT, LAYER) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATEBRIDGEPART = "UPDATE BRIDGEPARTS SET TYPE=?,LASTMAINTAINED=?,CURRENTQL=?,ORIGINALQL=?,DAMAGE=?,STRUCTURE=?,STATE=?,MATERIAL=?,HEIGHTOFFSET=?,DIR=?,SLOPE=?,STAGECOUNT=?,NORTHEXIT=?,EASTEXIT=?,SOUTHEXIT=?,WESTEXIT=?,LAYER=? WHERE ID=?";
    private static final String GETBRIDGEPART = "SELECT * FROM BRIDGEPARTS WHERE ID=?";
    private static final String DELETEBRIDGEPART = "DELETE FROM BRIDGEPARTS WHERE ID=?";
    private static final String SETDAMAGE = "UPDATE BRIDGEPARTS SET DAMAGE=? WHERE ID=?";
    private static final String SETQUALITYLEVEL = "UPDATE BRIDGEPARTS SET CURRENTQL=? WHERE ID=?";
    private static final String SETSTATE = "UPDATE BRIDGEPARTS SET STATE=?,MATERIAL=? WHERE ID=?";
    private static final String SETLASTUSED = "UPDATE BRIDGEPARTS SET LASTMAINTAINED=? WHERE ID=?";
    private static final String SET_SETTINGS = "UPDATE BRIDGEPARTS SET SETTINGS=? WHERE ID=?";
    private static final String SETROADTYPE = "UPDATE BRIDGEPARTS SET ROADTYPE=? WHERE ID=?";
    private static final Logger logger;
    
    @Override
    public boolean isFence() {
        return false;
    }
    
    @Override
    public boolean isWall() {
        return false;
    }
    
    public DbBridgePart(final int id, final BridgeConstants.BridgeType floorType, final int tilex, final int tiley, final byte aDbState, final int heightOffset, final float currentQl, final long structureId, final BridgeConstants.BridgeMaterial floorMaterial, final float origQL, final float dam, final int materialCount, final long lastmaintained, final byte dir, final byte slope, final int aNorthExit, final int aEastExit, final int aSouthExit, final int aWestExit, final byte roadType, final int layer) {
        super(id, floorType, tilex, tiley, aDbState, heightOffset, currentQl, structureId, floorMaterial, origQL, dam, materialCount, lastmaintained, dir, slope, aNorthExit, aEastExit, aSouthExit, aWestExit, roadType, layer);
    }
    
    public DbBridgePart(final BridgeConstants.BridgeType floorType, final int tilex, final int tiley, final int heightOffset, final float qualityLevel, final long structure, final BridgeConstants.BridgeMaterial material, final byte dir, final byte slope, final int aNorthExit, final int aEastExit, final int aSouthExit, final int aWestExit, final byte roadType, final int layer) {
        super(floorType, tilex, tiley, heightOffset, qualityLevel, structure, material, dir, slope, aNorthExit, aEastExit, aSouthExit, aWestExit, roadType, layer);
    }
    
    @Override
    protected void setState(final byte newState) {
        if (this.dbState != newState) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.dbState = newState;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET STATE=?,MATERIAL=? WHERE ID=?");
                ps.setByte(1, this.dbState);
                ps.setByte(2, this.getMaterial().getCode());
                ps.setInt(3, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbBridgePart.logger.log(Level.WARNING, "Failed to set state to " + newState + " for bridge part with id " + this.getNumber(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (this.exists(dbcon)) {
                ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET TYPE=?,LASTMAINTAINED=?,CURRENTQL=?,ORIGINALQL=?,DAMAGE=?,STRUCTURE=?,STATE=?,MATERIAL=?,HEIGHTOFFSET=?,DIR=?,SLOPE=?,STAGECOUNT=?,NORTHEXIT=?,EASTEXIT=?,SOUTHEXIT=?,WESTEXIT=?,LAYER=? WHERE ID=?");
                ps.setByte(1, this.getType().getCode());
                ps.setLong(2, this.getLastUsed());
                ps.setFloat(3, this.getCurrentQL());
                ps.setFloat(4, this.getOriginalQL());
                ps.setFloat(5, this.getDamage());
                ps.setLong(6, this.getStructureId());
                ps.setByte(7, this.getState());
                ps.setByte(8, this.getMaterial().getCode());
                ps.setInt(9, this.getHeightOffset());
                ps.setByte(10, this.getDir());
                ps.setByte(11, this.getSlope());
                ps.setInt(12, this.getMaterialCount());
                ps.setInt(13, this.getNorthExit());
                ps.setInt(14, this.getEastExit());
                ps.setInt(15, this.getSouthExit());
                ps.setInt(16, this.getWestExit());
                ps.setInt(17, this.getLayer());
                ps.setInt(18, this.getNumber());
                ps.executeUpdate();
            }
            else {
                ps = dbcon.prepareStatement("INSERT INTO BRIDGEPARTS(TYPE, LASTMAINTAINED , CURRENTQL, ORIGINALQL, DAMAGE, STRUCTURE, TILEX, TILEY, STATE, MATERIAL, HEIGHTOFFSET, DIR, SLOPE, STAGECOUNT, NORTHEXIT, EASTEXIT, SOUTHEXIT, WESTEXIT, LAYER) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
                ps.setByte(1, this.getType().getCode());
                ps.setLong(2, this.getLastUsed());
                ps.setFloat(3, this.getCurrentQL());
                ps.setFloat(4, this.getOriginalQL());
                ps.setFloat(5, this.getDamage());
                ps.setLong(6, this.getStructureId());
                ps.setInt(7, this.getTileX());
                ps.setInt(8, this.getTileY());
                ps.setByte(9, this.getState());
                ps.setByte(10, this.getMaterial().getCode());
                ps.setInt(11, this.getHeightOffset());
                ps.setByte(12, this.getDir());
                ps.setByte(13, this.getSlope());
                ps.setInt(14, this.getMaterialCount());
                ps.setInt(15, this.getNorthExit());
                ps.setInt(16, this.getEastExit());
                ps.setInt(17, this.getSouthExit());
                ps.setInt(18, this.getWestExit());
                ps.setInt(19, this.getLayer());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    this.setNumber(rs.getInt(1));
                }
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public boolean setDamage(final float aDamage) {
        boolean forcePlan = false;
        if (this.isIndestructible()) {
            return false;
        }
        if (aDamage >= 100.0f) {
            final VolaTile tile = this.getTile();
            forcePlan = true;
            final BridgeConstants.BridgeState oldBridgeState = this.getBridgePartState();
            this.setBridgePartState(BridgeConstants.BridgeState.PLANNED);
            this.setQualityLevel(1.0f);
            this.saveRoadType((byte)0);
            if (tile != null) {
                tile.updateBridgePart(this);
                if (oldBridgeState != BridgeConstants.BridgeState.PLANNED) {
                    final BridgeConstants.BridgeType bType = this.getType();
                    switch (this.getMaterial()) {
                        case BRICK:
                        case MARBLE:
                        case POTTERY:
                        case RENDERED:
                        case ROUNDED_STONE:
                        case SANDSTONE:
                        case SLATE: {
                            if (bType.isSupportType()) {
                                this.damageAdjacent("abutment", 50);
                                break;
                            }
                            if (bType.isAbutment()) {
                                this.damageAdjacent("bracing", 25);
                                break;
                            }
                            if (bType.isBracing()) {
                                this.damageAdjacent("crown", 10);
                                this.damageAdjacent("floating", 10);
                                break;
                            }
                            break;
                        }
                        case WOOD: {
                            if (bType.isSupportType()) {
                                this.damageAdjacent("abutment", 50);
                                this.damageAdjacent("crown", 25);
                                break;
                            }
                            if (bType.isAbutment()) {
                                this.damageAdjacent("crown", 10);
                                break;
                            }
                            break;
                        }
                        case ROPE: {
                            if (bType.isAbutment()) {
                                this.damageAdjacent("crown", 50);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (this.damage != aDamage) {
            boolean updateState = false;
            if ((this.damage >= 60.0f && aDamage < 60.0f) || (this.damage < 60.0f && aDamage >= 60.0f)) {
                updateState = true;
            }
            this.damage = aDamage;
            if (forcePlan) {
                this.damage = 0.0f;
            }
            if (this.damage < 100.0f) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getZonesDbCon();
                    ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET DAMAGE=? WHERE ID=?");
                    ps.setFloat(1, this.getDamage());
                    ps.setInt(2, this.getNumber());
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    DbBridgePart.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
                if (updateState) {
                    final VolaTile tile2 = this.getTile();
                    if (tile2 != null) {
                        this.getTile().updateBridgePartDamageState(this);
                    }
                }
            }
            else {
                final VolaTile t = this.getTile();
                if (t != null) {
                    t.removeBridgePart(this);
                }
                this.delete();
            }
        }
        return this.damage >= 100.0f;
    }
    
    private void damageAdjacent(final String typeName, final int addDamage) {
        final VolaTile vtNorth = Zones.getTileOrNull(this.getTileX(), this.getTileY() - 1, this.isOnSurface());
        if (vtNorth != null) {
            final Structure structNorth = vtNorth.getStructure();
            if (structNorth != null && structNorth.getWurmId() == this.getStructureId()) {
                final BridgePart[] bps = vtNorth.getBridgeParts();
                if (bps.length == 1 && bps[0].getType().getName().equalsIgnoreCase(typeName)) {
                    bps[0].setDamage(bps[0].getDamage() + addDamage);
                }
            }
        }
        final VolaTile vtEast = Zones.getTileOrNull(this.getTileX() + 1, this.getTileY(), this.isOnSurface());
        if (vtEast != null) {
            final Structure structEast = vtEast.getStructure();
            if (structEast != null && structEast.getWurmId() == this.getStructureId()) {
                final BridgePart[] bps2 = vtEast.getBridgeParts();
                if (bps2.length == 1 && bps2[0].getType().getName().equalsIgnoreCase(typeName)) {
                    bps2[0].setDamage(bps2[0].getDamage() + addDamage);
                }
            }
        }
        final VolaTile vtSouth = Zones.getTileOrNull(this.getTileX(), this.getTileY() + 1, this.isOnSurface());
        if (vtSouth != null) {
            final Structure structSouth = vtSouth.getStructure();
            if (structSouth != null && structSouth.getWurmId() == this.getStructureId()) {
                final BridgePart[] bps3 = vtSouth.getBridgeParts();
                if (bps3.length == 1 && bps3[0].getType().getName().equalsIgnoreCase(typeName)) {
                    bps3[0].setDamage(bps3[0].getDamage() + addDamage);
                }
            }
        }
        final VolaTile vtWest = Zones.getTileOrNull(this.getTileX() - 1, this.getTileY(), this.isOnSurface());
        if (vtWest != null) {
            final Structure structWest = vtWest.getStructure();
            if (structWest != null && structWest.getWurmId() == this.getStructureId()) {
                final BridgePart[] bps4 = vtWest.getBridgeParts();
                if (bps4.length == 1 && bps4[0].getType().getName().equalsIgnoreCase(typeName)) {
                    bps4[0].setDamage(bps4[0].getDamage() + addDamage);
                }
            }
        }
    }
    
    @Override
    public void setLastUsed(final long now) {
        if (this.lastUsed != now) {
            this.lastUsed = now;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET LASTMAINTAINED=? WHERE ID=?");
                ps.setLong(1, this.lastUsed);
                ps.setInt(2, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbBridgePart.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public boolean setQualityLevel(float ql) {
        if (ql > 100.0f) {
            ql = 100.0f;
        }
        if (this.currentQL != ql) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.currentQL = ql;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET CURRENTQL=? WHERE ID=?");
                ps.setFloat(1, this.currentQL);
                ps.setInt(2, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbBridgePart.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return ql >= 100.0f;
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT * FROM BRIDGEPARTS WHERE ID=?");
            ps.setInt(1, this.getNumber());
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    @Override
    public void delete() {
        MissionTargets.destroyMissionTarget(this.getId(), true);
        MethodsHighways.removeNearbyMarkers(this);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM BRIDGEPARTS WHERE ID=?");
            ps.setInt(1, this.getNumber());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbBridgePart.logger.log(Level.WARNING, "Failed to delete bridge part with id " + this.getNumber(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public long getTempId() {
        return -10L;
    }
    
    @Override
    public void savePermissions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET SETTINGS=? WHERE ID=?");
            ps.setLong(1, this.permissions.getPermissions());
            ps.setLong(2, this.getNumber());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbBridgePart.logger.log(Level.WARNING, "Failed to save settings for bridge part with id " + this.getNumber(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void saveRoadType(final byte roadType) {
        if (this.roadType != roadType) {
            this.roadType = roadType;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE BRIDGEPARTS SET ROADTYPE=? WHERE ID=?");
                ps.setByte(1, this.roadType);
                ps.setLong(2, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbBridgePart.logger.log(Level.WARNING, "Failed to save roadtype for bridge part with id " + this.getNumber(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
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
        logger = Logger.getLogger(DbWall.class.getName());
    }
}
