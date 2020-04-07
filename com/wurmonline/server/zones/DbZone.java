// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.structures.Fence;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.structures.DbFenceGate;
import com.wurmonline.server.structures.DbFence;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.shared.constants.StructureStateEnum;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import com.wurmonline.server.structures.Structure;
import java.util.HashSet;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;

final class DbZone extends Zone implements CounterTypes
{
    private static final Logger logger;
    private static final String GET_FENCES = "Select * from FENCES where ZONEID=?";
    private static final String DELETE_FENCES = "DELETE from FENCES where ZONEID=?";
    
    DbZone(final int aStartX, final int aEndX, final int aStartY, final int aEndY, final boolean aIsOnSurface) throws IOException {
        super(aStartX, aEndX, aStartY, aEndY, aIsOnSurface);
        this.zoneWatchers = new HashSet<VirtualZone>();
        this.structures = new HashSet<Structure>();
    }
    
    @Override
    void load() throws IOException {
    }
    
    @Override
    void loadFences() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("Select * from FENCES where ZONEID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int fid = -10;
                try {
                    fid = rs.getInt("ID");
                    final int tilex = rs.getInt("TILEX");
                    final int tiley = rs.getInt("TILEY");
                    final float currentQL = rs.getFloat("ORIGINALQL");
                    final float originalQL = rs.getFloat("CURRENTQL");
                    final long lastUsed = rs.getLong("LASTMAINTAINED");
                    final StructureConstantsEnum type = StructureConstantsEnum.getEnumByValue(rs.getShort("TYPE"));
                    final StructureStateEnum state = StructureStateEnum.getStateByValue(rs.getByte("STATE"));
                    final int color = rs.getInt("COLOR");
                    final int dir = rs.getByte("DIR");
                    final float damage = rs.getFloat("DAMAGE");
                    final int heightOffset = rs.getInt("HEIGHTOFFSET");
                    final int layer = rs.getInt("LAYER");
                    final int settings = rs.getInt("SETTINGS");
                    final Fence fence = new DbFence(fid, type, state, color, tilex, tiley, heightOffset, currentQL, originalQL, lastUsed, (dir == 0) ? Tiles.TileBorderDirection.DIR_HORIZ : Tiles.TileBorderDirection.DIR_DOWN, this.id, this.isOnSurface, damage, layer, settings);
                    if (dir != 3) {
                        if (dir != 1) {
                            this.addFence(fence);
                            if (fence.isDoor() && fence.isFinished()) {
                                final FenceGate gate = new DbFenceGate(fence);
                                gate.addToTiles();
                                continue;
                            }
                            continue;
                        }
                    }
                    try {
                        fence.delete();
                    }
                    catch (Exception ex) {
                        DbZone.logger.log(Level.WARNING, "Failed to delete fence " + ex.getMessage(), ex);
                    }
                }
                catch (SQLException iox) {
                    DbZone.logger.log(Level.WARNING, "Failed to load fence with id " + fid, iox);
                }
            }
        }
        catch (SQLException sqx) {
            DbZone.logger.log(Level.WARNING, "Failed to load fences for zone with id " + this.id, sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void save() throws IOException {
    }
    
    static {
        logger = Logger.getLogger(DbZone.class.getName());
    }
}
