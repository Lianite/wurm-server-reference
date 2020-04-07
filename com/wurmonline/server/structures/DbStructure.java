// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.Zone;
import java.util.Set;
import java.util.List;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.players.PermissionsHistories;
import java.util.Iterator;
import com.wurmonline.server.zones.VolaTile;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import java.util.logging.Logger;

public class DbStructure extends Structure
{
    private static final Logger logger;
    private static final String GET_STRUCTURE = "SELECT * FROM STRUCTURES WHERE WURMID=?";
    private static final String SAVE_STRUCTURE = "UPDATE STRUCTURES SET CENTERX=?,CENTERY=?,ROOF=?,SURFACED=?,NAME=?,FINISHED=?,WRITID=?,FINFINISHED=?,ALLOWSVILLAGERS=?,ALLOWSALLIES=?,ALLOWSKINGDOM=?,PLANNER=?,OWNERID=?,SETTINGS=?,VILLAGE=? WHERE WURMID=?";
    private static final String CREATE_STRUCTURE = "INSERT INTO STRUCTURES(WURMID, STRUCTURETYPE) VALUES(?,?)";
    private static final String DELETE_STRUCTURE = "DELETE FROM STRUCTURES WHERE WURMID=?";
    private static final String ADD_BUILDTILE = "INSERT INTO BUILDTILES(STRUCTUREID,TILEX,TILEY,LAYER) VALUES (?,?,?,?)";
    private static final String DELETE_BUILDTILE = "DELETE FROM BUILDTILES WHERE STRUCTUREID=? AND TILEX=? AND TILEY=? AND LAYER=?";
    private static final String DELETE_ALLBUILDTILES = "DELETE FROM BUILDTILES WHERE STRUCTUREID=?";
    private static final String LOAD_ALLBUILDTILES = "SELECT * FROM BUILDTILES";
    private static final String SET_FINISHED = "UPDATE STRUCTURES SET FINISHED=? WHERE WURMID=?";
    private static final String SET_FIN_FINISHED = "UPDATE STRUCTURES SET FINFINISHED=? WHERE WURMID=?";
    private static final String SET_WRITID = "UPDATE STRUCTURES SET WRITID=? WHERE WURMID=?";
    private static final String SET_OWNERID = "UPDATE STRUCTURES SET OWNERID=? WHERE WURMID=?";
    private static final String SET_SETTINGS = "UPDATE STRUCTURES SET SETTINGS=?,VILLAGE=? WHERE WURMID=?";
    private static final String SET_NAME = "UPDATE STRUCTURES SET NAME=? WHERE WURMID=?";
    
    DbStructure(final byte theStructureType, final String aName, final long id, final int x, final int y, final boolean isSurfaced) {
        super(theStructureType, aName, id, x, y, isSurfaced);
    }
    
    DbStructure(final long id) throws IOException, NoSuchStructureException {
        super(id);
    }
    
    DbStructure(final byte theStructureType, final String aName, final long aId, final boolean aIsSurfaced, final byte aRoof, final boolean aFinished, final boolean aFinFinished, final long aWritId, final String aPlanner, final long aOwnerId, final int aSettings, final int aVillageId, final boolean aAllowsVillagers, final boolean aAllowsAllies, final boolean aAllowKingdom) {
        super(theStructureType, aName, aId, aIsSurfaced, aRoof, aFinished, aFinFinished, aWritId, aPlanner, aOwnerId, aSettings, aVillageId, aAllowsVillagers, aAllowsAllies, aAllowKingdom);
    }
    
    @Override
    void load() throws IOException, NoSuchStructureException {
        if (!this.isLoading()) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                this.setLoading(true);
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM STRUCTURES WHERE WURMID=?");
                ps.setLong(1, this.getWurmId());
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NoSuchStructureException("No structure found with id " + this.getWurmId());
                }
                this.setStructureType(rs.getByte("STRUCTURETYPE"));
                this.setSurfaced(rs.getBoolean("SURFACED"));
                this.setRoof(rs.getByte("ROOF"));
                String lName = rs.getString("NAME");
                if (lName == null) {
                    lName = "Unknown structure";
                }
                if (lName.length() >= 50) {
                    lName = lName.substring(0, 49);
                }
                this.setName(lName, false);
                this.finished = rs.getBoolean("FINISHED");
                this.finalfinished = rs.getBoolean("FINFINISHED");
                this.allowsVillagers = rs.getBoolean("ALLOWSVILLAGERS");
                this.allowsAllies = rs.getBoolean("ALLOWSALLIES");
                this.allowsKingdom = rs.getBoolean("ALLOWSKINGDOM");
                this.setPlanner(rs.getString("PLANNER"));
                this.setOwnerId(rs.getLong("OWNERID"));
                this.setSettings(rs.getInt("SETTINGS"));
                this.villageId = rs.getInt("VILLAGE");
                if (this.isTypeHouse()) {
                    try {
                        this.setWritid(rs.getLong("WRITID"), false);
                    }
                    catch (SQLException nsi) {
                        DbStructure.logger.log(Level.INFO, "No writ for house with id:" + this.getWurmId() + " creating new after loading.", nsi);
                    }
                }
            }
            catch (SQLException sqex) {
                throw new IOException(sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (!this.exists(dbcon)) {
                this.create(dbcon);
            }
            ps = dbcon.prepareStatement("UPDATE STRUCTURES SET CENTERX=?,CENTERY=?,ROOF=?,SURFACED=?,NAME=?,FINISHED=?,WRITID=?,FINFINISHED=?,ALLOWSVILLAGERS=?,ALLOWSALLIES=?,ALLOWSKINGDOM=?,PLANNER=?,OWNERID=?,SETTINGS=?,VILLAGE=? WHERE WURMID=?");
            ps.setInt(1, this.getCenterX());
            ps.setInt(2, this.getCenterY());
            for (final VolaTile t : this.structureTiles) {
                final Wall[] wallArr = t.getWalls();
                for (int x = 0; x < wallArr.length; ++x) {
                    try {
                        wallArr[x].save();
                    }
                    catch (IOException iox) {
                        DbStructure.logger.log(Level.WARNING, "Failed to save wall: " + wallArr[x]);
                    }
                }
                final Floor[] floorArr = t.getFloors();
                for (int x2 = 0; x2 < floorArr.length; ++x2) {
                    try {
                        floorArr[x2].save();
                    }
                    catch (IOException iox2) {
                        DbStructure.logger.log(Level.WARNING, "Failed to save floor: " + floorArr[x2]);
                    }
                }
                final BridgePart[] partsArr = t.getBridgeParts();
                for (int x3 = 0; x3 < partsArr.length; ++x3) {
                    try {
                        partsArr[x3].save();
                    }
                    catch (IOException iox3) {
                        DbStructure.logger.log(Level.WARNING, "Failed to save bridge part: " + partsArr[x3]);
                    }
                }
            }
            ps.setByte(3, this.getRoof());
            ps.setBoolean(4, this.isSurfaced());
            ps.setString(5, this.getName());
            ps.setBoolean(6, this.isFinished());
            ps.setLong(7, this.getWritId());
            ps.setBoolean(8, this.isFinalFinished());
            ps.setBoolean(9, this.allowsCitizens());
            ps.setBoolean(10, this.allowsAllies());
            ps.setBoolean(11, this.allowsKingdom());
            ps.setString(12, this.getPlanner());
            ps.setLong(13, this.getOwnerId());
            ps.setInt(14, this.getSettings().getPermissions());
            ps.setInt(15, this.getVillageId());
            ps.setLong(16, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbStructure.logger.log(Level.WARNING, "Problem", sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void create(final Connection dbcon) throws IOException {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement("INSERT INTO STRUCTURES(WURMID, STRUCTURETYPE) VALUES(?,?)");
            ps.setLong(1, this.getWurmId());
            ps.setByte(2, this.getStructureType());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            DbStructure.logger.log(Level.WARNING, "Problem", sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT * FROM STRUCTURES WHERE WURMID=?");
            ps.setLong(1, this.getWurmId());
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    @Override
    void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM STRUCTURES WHERE WURMID=?");
            ps.setLong(1, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbStructure.logger.log(Level.WARNING, "Failed to delete structure with id=" + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        StructureSettings.remove(this.getWurmId());
        PermissionsHistories.remove(this.getWurmId());
        Structures.removeStructure(this.getWurmId());
        this.deleteAllBuildTiles();
    }
    
    @Override
    public void endLoading() throws IOException {
        if (!this.hasLoaded()) {
            this.setHasLoaded(true);
            final List<Wall> structureWalls = Wall.getWallsAsArrayListFor(this.getWurmId());
            if (this.loadStructureTiles(structureWalls)) {
                while (this.fillHoles()) {
                    DbStructure.logger.log(Level.INFO, "Filling holes " + this.getWurmId());
                }
            }
            final Set<Floor> floorset = Floor.getFloorsFor(this.getWurmId());
            if (floorset != null) {
                final Iterator<Floor> it = floorset.iterator();
                while (it.hasNext()) {
                    try {
                        final Floor floor = it.next();
                        final int tilex = floor.getTileX();
                        final int tiley = floor.getTileY();
                        final Zone zone = Zones.getZone(tilex, tiley, this.isSurfaced());
                        final VolaTile tile = zone.getOrCreateTile(tilex, tiley);
                        if (this.structureTiles.contains(tile)) {
                            tile.addFloor(floor);
                        }
                        else {
                            DbStructure.logger.log(Level.FINE, "Floor #" + floor.getId() + " thinks it belongs to structure " + this.getWurmId() + " but structureTiles disagrees.");
                        }
                    }
                    catch (NoSuchZoneException e) {
                        DbStructure.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            final Set<BridgePart> bridgePartsset = BridgePart.getBridgePartsFor(this.getWurmId());
            final Iterator<BridgePart> it2 = bridgePartsset.iterator();
            while (it2.hasNext()) {
                try {
                    final BridgePart bridgePart = it2.next();
                    final int tilex = bridgePart.getTileX();
                    final int tiley = bridgePart.getTileY();
                    final Zone zone2 = Zones.getZone(tilex, tiley, this.isSurfaced());
                    final VolaTile tile2 = zone2.getOrCreateTile(tilex, tiley);
                    if (this.structureTiles.contains(tile2)) {
                        tile2.addBridgePart(bridgePart);
                    }
                    else {
                        DbStructure.logger.log(Level.FINE, "BridgePart #" + bridgePart.getId() + " thinks it belongs to structure " + this.getWurmId() + " but structureTiles disagrees.");
                    }
                }
                catch (NoSuchZoneException e2) {
                    DbStructure.logger.log(Level.WARNING, e2.getMessage(), e2);
                }
            }
            Zone northW = null;
            Zone northE = null;
            Zone southW = null;
            Zone southE = null;
            try {
                northW = Zones.getZone(this.minX, this.minY, this.surfaced);
                northW.addStructure(this);
            }
            catch (NoSuchZoneException ex) {}
            try {
                northE = Zones.getZone(this.maxX, this.minY, this.surfaced);
                if (northE != northW) {
                    northE.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex2) {}
            try {
                southE = Zones.getZone(this.maxX, this.maxY, this.surfaced);
                if (southE != northE && southE != northW) {
                    southE.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex3) {}
            try {
                southW = Zones.getZone(this.minX, this.maxY, this.surfaced);
                if (southW != northE && southW != northW && southW != southE) {
                    southW.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex4) {}
        }
    }
    
    @Override
    public void setFinished(final boolean finish) {
        if (this.isFinished() != finish) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.finished = finish;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE STRUCTURES SET FINISHED=? WHERE WURMID=?");
                ps.setBoolean(1, this.isFinished());
                ps.setLong(2, this.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbStructure.logger.log(Level.WARNING, "Failed to set finished to " + finish + " for structure " + this.getName() + " with id " + this.getWurmId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void setFinalFinished(final boolean finfinish) {
        if (this.isFinalFinished() != finfinish) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.finalfinished = finfinish;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE STRUCTURES SET FINFINISHED=? WHERE WURMID=?");
                ps.setBoolean(1, this.isFinalFinished());
                ps.setLong(2, this.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbStructure.logger.log(Level.WARNING, "Failed to set finfinished to " + finfinish + " for structure " + this.getName() + " with id " + this.getWurmId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void saveWritId() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE STRUCTURES SET WRITID=? WHERE WURMID=?");
            ps.setLong(1, this.writid);
            ps.setLong(2, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbStructure.logger.log(Level.WARNING, "Failed to set writId to " + this.writid + " for structure " + this.getName() + " with id " + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void saveOwnerId() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE STRUCTURES SET OWNERID=? WHERE WURMID=?");
            ps.setLong(1, this.ownerId);
            ps.setLong(2, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbStructure.logger.log(Level.WARNING, "Failed to set ownerId to " + this.ownerId + " for structure " + this.getName() + " with id " + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void saveSettings() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE STRUCTURES SET SETTINGS=?,VILLAGE=? WHERE WURMID=?");
            ps.setInt(1, this.getSettings().getPermissions());
            ps.setInt(2, this.getVillageId());
            ps.setLong(3, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbStructure.logger.log(Level.WARNING, "Failed to set settings to " + this.getSettings().getPermissions() + " for structure " + this.getName() + " with id " + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void saveName() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE STRUCTURES SET NAME=? WHERE WURMID=?");
            ps.setString(1, this.getName());
            ps.setLong(2, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbStructure.logger.log(Level.WARNING, "Failed to set name to " + this.getName() + " for structure with id " + this.getWurmId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setAllowVillagers(final boolean allow) {
        if (this.allowsCitizens() != allow) {
            this.allowsVillagers = allow;
            if (allow) {
                this.addDefaultCitizenPermissions();
            }
            else {
                this.removeStructureGuest(-30L);
            }
        }
    }
    
    @Override
    public void setAllowKingdom(final boolean allow) {
        if (this.allowsKingdom() != allow) {
            this.allowsKingdom = allow;
            if (allow) {
                this.addDefaultKingdomPermissions();
            }
            else {
                this.removeStructureGuest(-40L);
            }
        }
    }
    
    @Override
    public void setAllowAllies(final boolean allow) {
        if (this.allowsAllies() != allow) {
            this.allowsAllies = allow;
            if (allow) {
                this.addDefaultAllyPermissions();
            }
            else {
                this.removeStructureGuest(-20L);
            }
        }
    }
    
    public void addNewGuest(final long guestId, final int aSettings) {
        StructureSettings.addPlayer(this.getWurmId(), guestId, aSettings);
    }
    
    public void removeStructureGuest(final long guestId) {
        StructureSettings.removePlayer(this.getWurmId(), guestId);
    }
    
    @Override
    public void removeBuildTile(final int tilex, final int tiley, final int layer) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM BUILDTILES WHERE STRUCTUREID=? AND TILEX=? AND TILEY=? AND LAYER=?");
            ps.setLong(1, this.getWurmId());
            ps.setInt(2, tilex);
            ps.setInt(3, tiley);
            ps.setInt(4, layer);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DbStructure.logger.log(Level.WARNING, "Failed to remove build tile for structure with id " + this.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void addNewBuildTile(final int tilex, final int tiley, final int layer) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO BUILDTILES(STRUCTUREID,TILEX,TILEY,LAYER) VALUES (?,?,?,?)");
            ps.setLong(1, this.getWurmId());
            ps.setInt(2, tilex);
            ps.setInt(3, tiley);
            ps.setInt(4, layer);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DbStructure.logger.log(Level.WARNING, "Failed to add build tile for structure with id " + this.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void loadBuildTiles() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM BUILDTILES");
            rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    final Structure structure = Structures.getStructure(rs.getLong("STRUCTUREID"));
                    structure.addBuildTile(new BuildTile(rs.getInt("TILEX"), rs.getInt("TILEY"), rs.getInt("LAYER")));
                }
                catch (NoSuchStructureException nss) {
                    DbStructure.logger.log(Level.WARNING, nss.getMessage());
                }
            }
        }
        catch (SQLException ex) {
            DbStructure.logger.log(Level.WARNING, "Failed to load all tiles for structures" + ex.getMessage(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void deleteAllBuildTiles() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM BUILDTILES WHERE STRUCTUREID=?");
            ps.setLong(1, this.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DbStructure.logger.log(Level.WARNING, "Failed to delete all build tiles for structure with id " + this.getWurmId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public boolean isItem() {
        return false;
    }
    
    static {
        logger = Logger.getLogger(DbStructure.class.getName());
    }
}
