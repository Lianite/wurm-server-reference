// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.batchjobs;

import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Map;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.structures.DbDoor;
import com.wurmonline.shared.constants.StructureStateEnum;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.structures.DbWall;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.Door;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StructureBatchJob
{
    private static final String DELETE_DOORS = "DELETE FROM DOORS WHERE LOCKID<=0";
    private static final String DELETE_GATES = "DELETE FROM GATES WHERE LOCKID<=0";
    private static final String LOAD_WALLS = "SELECT * FROM WALLS";
    private static final String LOAD_FENCES = "SELECT * FROM FENCES";
    private static final String updateGate = "UPDATE GATES SET ID=? WHERE ID=?";
    private static Logger logger;
    
    public static final void runBatch1() {
        StructureBatchJob.logger.log(Level.INFO, "Running batch 1.");
        try {
            final Map<Long, LinkedList<Door>> doors = new HashMap<Long, LinkedList<Door>>();
            final Map<Long, LinkedList<Wall>> walls = new HashMap<Long, LinkedList<Wall>>();
            final Connection dbcon = DbConnector.getZonesDbCon();
            final PreparedStatement psA = dbcon.prepareStatement("DELETE FROM DOORS WHERE LOCKID<=0");
            psA.executeUpdate();
            psA.close();
            final PreparedStatement ps = dbcon.prepareStatement("SELECT * FROM WALLS");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    final Wall wall = new DbWall(rs.getInt("ID"));
                    wall.x1 = rs.getInt("STARTX");
                    wall.x2 = rs.getInt("ENDX");
                    wall.y1 = rs.getInt("STARTY");
                    wall.y2 = rs.getInt("ENDY");
                    wall.tilex = rs.getInt("TILEX");
                    wall.tiley = rs.getInt("TILEY");
                    wall.currentQL = rs.getFloat("ORIGINALQL");
                    wall.originalQL = rs.getFloat("CURRENTQL");
                    wall.lastUsed = rs.getLong("LASTMAINTAINED");
                    wall.structureId = rs.getLong("STRUCTURE");
                    wall.type = StructureTypeEnum.getTypeByINDEX(rs.getByte("TYPE"));
                    wall.state = StructureStateEnum.getStateByValue(rs.getByte("STATE"));
                    wall.damage = rs.getFloat("DAMAGE");
                    wall.setColor(rs.getInt("COLOR"));
                    wall.setIndoor(rs.getBoolean("ISINDOOR"));
                    wall.heightOffset = rs.getInt("HEIGHTOFFSET");
                    if (wall.getType() != StructureTypeEnum.DOOR && wall.getType() != StructureTypeEnum.DOUBLE_DOOR && !wall.isArched()) {
                        continue;
                    }
                    LinkedList<Wall> wallist = walls.get(wall.structureId);
                    if (wallist == null) {
                        wallist = new LinkedList<Wall>();
                        walls.put(wall.structureId, wallist);
                    }
                    wallist.add(wall);
                    LinkedList<Door> doorlist = doors.get(wall.structureId);
                    if (doorlist == null) {
                        doorlist = new LinkedList<Door>();
                        doors.put(wall.structureId, doorlist);
                    }
                    final boolean updated = false;
                    final Door door = new DbDoor(wall);
                    doorlist.add(door);
                    doors.put(wall.structureId, doorlist);
                }
                catch (IOException iox) {
                    StructureBatchJob.logger.log(Level.INFO, "IOException");
                }
            }
            ps.close();
            rs.close();
        }
        catch (SQLException sqx) {
            StructureBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        StructureBatchJob.logger.log(Level.INFO, "Done running batch 1.");
    }
    
    public static final void convertToNewPermissions() {
        StructureBatchJob.logger.log(Level.INFO, "Converting Structures to New Permission System.");
        int structuresDone = 0;
        for (final Structure structure : Structures.getAllStructures()) {
            if (structure.convertToNewPermissions()) {
                ++structuresDone;
            }
        }
        StructureBatchJob.logger.log(Level.INFO, "Converted " + structuresDone + " structures to New Permissions System.");
    }
    
    public static final void convertGatesToNewPermissions() {
        StructureBatchJob.logger.log(Level.INFO, "Converting Gates to New Permission System.");
        int gatesDone = 0;
        for (final FenceGate gate : FenceGate.getAllGates()) {
            if (gate.convertToNewPermissions()) {
                ++gatesDone;
            }
        }
        StructureBatchJob.logger.log(Level.INFO, "Converted " + gatesDone + " gates to New Permissions System.");
    }
    
    public static final void fixGatesForNewPermissions() {
        StructureBatchJob.logger.log(Level.INFO, "fixing Gates for New Permission System.");
        int gatesDone = 0;
        for (final FenceGate gate : FenceGate.getAllGates()) {
            if (gate.fixForNewPermissions()) {
                ++gatesDone;
            }
        }
        StructureBatchJob.logger.log(Level.INFO, "Fixed " + gatesDone + " gates to New Permissions System.");
    }
    
    static {
        StructureBatchJob.logger = Logger.getLogger(StructureBatchJob.class.getName());
    }
}
