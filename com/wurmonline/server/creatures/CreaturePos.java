// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.utils.CreaturePositionDbUpdatable;
import com.wurmonline.server.utils.PlayerPositionDbUpdatable;
import com.wurmonline.server.Constants;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.math.Vector3f;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Server;
import com.wurmonline.server.utils.PlayerPositionDatabaseUpdater;
import com.wurmonline.server.utils.CreaturePositionDatabaseUpdater;
import java.sql.PreparedStatement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;

public class CreaturePos implements CounterTypes, TimeConstants, MiscConstants
{
    private static final Logger logger;
    private static final String createPos = "insert into POSITION (POSX, POSY, POSZ, ROTATION,ZONEID,LAYER,ONBRIDGE, WURMID) values (?,?,?,?,?,?,?,?)";
    private static final String updatePosOld = "update POSITION set POSX=?, POSY=?, POSZ=?, ROTATION=?,ZONEID=?,LAYER=?,ONBRIDGE=? where WURMID=?";
    private static final String updatePos = "INSERT INTO POSITION (POSX, POSY, POSZ, ROTATION, ZONEID, LAYER, ONBRIDGE, WURMID) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE POSX=VALUES(POSX), POSY=VALUES(POSY), POSZ=VALUES(POSZ), ROTATION=VALUES(ROTATION), ZONEID=VALUES(ZONEID), LAYER=VALUES(LAYER), ONBRIDGE=VALUES(ONBRIDGE)";
    private static final String loadAllPos = "SELECT * FROM POSITION";
    private static final String deletePosition = "DELETE FROM POSITION WHERE WURMID=?";
    private boolean changed;
    private float posX;
    private float posY;
    private float posZ;
    private float rotation;
    private int zoneId;
    private int layer;
    private long bridgeId;
    private final long wurmid;
    private final boolean isPlayer;
    protected long lastSavedPos;
    public static boolean logCreaturePos;
    protected static final long saveIntervalPlayer = 60000L;
    protected static final long saveIntervalCreature = 600000L;
    private static final ConcurrentHashMap<Long, CreaturePos> allPositions;
    private static PreparedStatement cretPosPS;
    private static int cretPosPSCount;
    public static int totalCretPosPSCount;
    private static PreparedStatement playPosPS;
    private static int playPosPSCount;
    public static int totalPlayPosPSCount;
    private static final CreaturePositionDatabaseUpdater creatureDbPosUpdater;
    private static final PlayerPositionDatabaseUpdater playerDbPosUpdater;
    
    public CreaturePos(final long wurmId, final float posx, final float posy, final float posz, final float rot, final int zone, final int layerId, final long bridge, final boolean createInDatabase) {
        this.changed = false;
        this.bridgeId = -10L;
        this.lastSavedPos = System.currentTimeMillis() - Server.rand.nextInt(2000000);
        this.wurmid = wurmId;
        this.isPlayer = (WurmId.getType(this.wurmid) == 0);
        this.setPosX(posx);
        this.setPosY(posy);
        this.setPosZ(posz, false);
        this.rotation = rot;
        this.setZoneId(zone);
        this.setLayer(layerId);
        this.setBridgeId(bridge);
        CreaturePos.allPositions.put(this.wurmid, this);
        if (createInDatabase) {
            this.save(this.changed = true);
        }
    }
    
    public boolean isChanged() {
        return this.changed;
    }
    
    public void setChanged(final boolean hasChanged) {
        this.changed = hasChanged;
    }
    
    public final Vector2f getPos2f() {
        return new Vector2f(this.posX, this.posY);
    }
    
    public final Vector3f getPos3f() {
        return new Vector3f(this.posX, this.posY, this.posZ);
    }
    
    public float getPosX() {
        return this.posX;
    }
    
    public void setPosX(final float posx) {
        if (this.posX != posx) {
            if ((int)this.posX >> 2 != (int)posx >> 2) {
                this.changed = true;
            }
            this.posX = posx;
        }
    }
    
    public float getPosY() {
        return this.posY;
    }
    
    public void setPosY(final float posy) {
        if (this.posY != posy) {
            if ((int)this.posY >> 2 != (int)posy >> 2) {
                this.changed = true;
            }
            this.posY = posy;
        }
    }
    
    public float getPosZ() {
        return this.posZ;
    }
    
    public void setPosZ(final float posz, final boolean forceSave) {
        if (this.posZ != posz) {
            this.posZ = posz;
            if (forceSave) {
                this.changed = true;
            }
        }
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final float rot) {
        if (this.rotation != rot) {
            this.rotation = rot;
            this.changed = true;
            PlayerTutorial.firePlayerTrigger(this.wurmid, PlayerTutorial.PlayerTrigger.MOVED_PLAYER_VIEW);
        }
    }
    
    public int getZoneId() {
        return this.zoneId;
    }
    
    public void setZoneId(final int zoneid) {
        if (zoneid != this.zoneId) {
            this.zoneId = zoneid;
            this.changed = true;
        }
    }
    
    public int getLayer() {
        return this.layer;
    }
    
    public void setLayer(final int layerId) {
        if (this.layer != layerId) {
            this.layer = layerId;
            this.changed = true;
        }
    }
    
    public long getBridgeId() {
        return this.bridgeId;
    }
    
    public void setBridgeId(final long bridgeid) {
        if (this.bridgeId != bridgeid) {
            this.bridgeId = bridgeid;
            this.changed = true;
            this.save(false);
        }
    }
    
    public long getWurmid() {
        return this.wurmid;
    }
    
    public final void save(final boolean create) {
        if (this.changed) {
            this.changed = false;
            PreparedStatement ps = null;
            Connection dbcon = null;
            try {
                if (this.isPlayer()) {
                    dbcon = DbConnector.getPlayerDbCon();
                }
                else {
                    dbcon = DbConnector.getCreatureDbCon();
                }
                if (create) {
                    ps = dbcon.prepareStatement("insert into POSITION (POSX, POSY, POSZ, ROTATION,ZONEID,LAYER,ONBRIDGE, WURMID) values (?,?,?,?,?,?,?,?)");
                }
                else {
                    ps = dbcon.prepareStatement("update POSITION set POSX=?, POSY=?, POSZ=?, ROTATION=?,ZONEID=?,LAYER=?,ONBRIDGE=? where WURMID=?");
                }
                ps.setFloat(1, this.getPosX());
                ps.setFloat(2, this.getPosY());
                ps.setFloat(3, this.getPosZ());
                ps.setFloat(4, this.getRotation());
                ps.setInt(5, this.getZoneId());
                ps.setInt(6, this.getLayer());
                ps.setLong(7, this.getBridgeId());
                ps.setLong(8, this.getWurmid());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                CreaturePos.logger.log(Level.WARNING, "Failed to update creaturePos for " + this.getWurmid() + " " + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static void clearBatches() {
        try {
            if (CreaturePos.cretPosPS != null) {
                final int[] x = CreaturePos.cretPosPS.executeBatch();
                CreaturePos.logger.log(Level.INFO, "Creatures Position saved batch size " + x.length);
                DbUtilities.closeDatabaseObjects(CreaturePos.cretPosPS, null);
                CreaturePos.cretPosPS = null;
                CreaturePos.cretPosPSCount = 0;
            }
            if (CreaturePos.playPosPS != null) {
                final int[] x = CreaturePos.playPosPS.executeBatch();
                CreaturePos.logger.log(Level.INFO, "Players Position saved batch size " + x.length);
                DbUtilities.closeDatabaseObjects(CreaturePos.playPosPS, null);
                CreaturePos.playPosPS = null;
                CreaturePos.playPosPSCount = 0;
            }
        }
        catch (Exception iox) {
            CreaturePos.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
    }
    
    protected final void savePlayerPosition(final int zoneid, final boolean immediately) throws SQLException {
        this.setZoneId(zoneid);
        if ((System.currentTimeMillis() - this.lastSavedPos > 60000L || immediately) && this.changed) {
            if (Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase) {
                final PlayerPositionDbUpdatable lUpdatable = new PlayerPositionDbUpdatable(this.getWurmid(), this.getPosX(), this.getPosY(), this.getPosZ(), this.getRotation(), this.getZoneId(), this.getLayer(), this.getBridgeId());
                CreaturePos.playerDbPosUpdater.addToQueue(lUpdatable);
                ++CreaturePos.totalPlayPosPSCount;
                if (immediately) {
                    CreaturePos.playerDbPosUpdater.saveImmediately();
                }
            }
            else {
                if (CreaturePos.playPosPS == null) {
                    final Connection dbcon = DbConnector.getPlayerDbCon();
                    if (Server.getInstance().isPS()) {
                        CreaturePos.playPosPS = dbcon.prepareStatement("update POSITION set POSX=?, POSY=?, POSZ=?, ROTATION=?,ZONEID=?,LAYER=?,ONBRIDGE=? where WURMID=?");
                    }
                    else {
                        CreaturePos.playPosPS = dbcon.prepareStatement("INSERT INTO POSITION (POSX, POSY, POSZ, ROTATION, ZONEID, LAYER, ONBRIDGE, WURMID) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE POSX=VALUES(POSX), POSY=VALUES(POSY), POSZ=VALUES(POSZ), ROTATION=VALUES(ROTATION), ZONEID=VALUES(ZONEID), LAYER=VALUES(LAYER), ONBRIDGE=VALUES(ONBRIDGE)");
                    }
                }
                CreaturePos.playPosPS.setFloat(1, this.getPosX());
                CreaturePos.playPosPS.setFloat(2, this.getPosY());
                CreaturePos.playPosPS.setFloat(3, this.getPosZ());
                this.setRotation(Creature.normalizeAngle(this.getRotation()));
                CreaturePos.playPosPS.setFloat(4, this.getRotation());
                CreaturePos.playPosPS.setInt(5, this.getZoneId());
                CreaturePos.playPosPS.setInt(6, this.getLayer());
                CreaturePos.playPosPS.setLong(7, this.getBridgeId());
                CreaturePos.playPosPS.setLong(8, this.getWurmid());
                CreaturePos.playPosPS.addBatch();
                ++CreaturePos.playPosPSCount;
                ++CreaturePos.totalPlayPosPSCount;
                if (CreaturePos.playPosPSCount > Constants.numberOfDbPlayerPositionsToUpdateEachTime || immediately) {
                    final long checkms = System.nanoTime();
                    CreaturePos.playPosPS.executeBatch();
                    DbUtilities.closeDatabaseObjects(CreaturePos.playPosPS, null);
                    CreaturePos.playPosPS = null;
                    final float elapsedMilliseconds = (System.nanoTime() - checkms) / 1000000.0f;
                    if (elapsedMilliseconds > 300.0f || CreaturePos.logger.isLoggable(Level.FINER)) {
                        CreaturePos.logger.log(Level.WARNING, "SavePlayerPos batch took " + elapsedMilliseconds + " ms for " + CreaturePos.playPosPSCount + " updates.");
                    }
                    CreaturePos.playPosPSCount = 0;
                }
            }
            this.changed = false;
            this.lastSavedPos = System.currentTimeMillis();
        }
    }
    
    protected void saveCreaturePosition(final int zoneid, final boolean immediately) throws SQLException {
        this.setZoneId(zoneid);
        if ((System.currentTimeMillis() - this.lastSavedPos > 600000L || immediately) && this.changed) {
            if (Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase && !immediately) {
                final CreaturePositionDbUpdatable lUpdatable = new CreaturePositionDbUpdatable(this.getWurmid(), this.getPosX(), this.getPosY(), this.getPosZ(), this.getRotation(), this.getZoneId(), this.getLayer(), this.getBridgeId());
                CreaturePos.creatureDbPosUpdater.addToQueue(lUpdatable);
                ++CreaturePos.totalCretPosPSCount;
                this.lastSavedPos = System.currentTimeMillis();
            }
            else {
                if (CreaturePos.cretPosPS == null) {
                    final Connection dbcon = DbConnector.getCreatureDbCon();
                    if (Server.getInstance().isPS()) {
                        CreaturePos.cretPosPS = dbcon.prepareStatement("update POSITION set POSX=?, POSY=?, POSZ=?, ROTATION=?,ZONEID=?,LAYER=?,ONBRIDGE=? where WURMID=?");
                    }
                    else {
                        CreaturePos.cretPosPS = dbcon.prepareStatement("INSERT INTO POSITION (POSX, POSY, POSZ, ROTATION, ZONEID, LAYER, ONBRIDGE, WURMID) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE POSX=VALUES(POSX), POSY=VALUES(POSY), POSZ=VALUES(POSZ), ROTATION=VALUES(ROTATION), ZONEID=VALUES(ZONEID), LAYER=VALUES(LAYER), ONBRIDGE=VALUES(ONBRIDGE)");
                    }
                }
                CreaturePos.cretPosPS.setFloat(1, this.getPosX());
                CreaturePos.cretPosPS.setFloat(2, this.getPosY());
                CreaturePos.cretPosPS.setFloat(3, this.getPosZ());
                this.setRotation(Creature.normalizeAngle(this.getRotation()));
                CreaturePos.cretPosPS.setFloat(4, this.getRotation());
                CreaturePos.cretPosPS.setInt(5, this.getZoneId());
                CreaturePos.cretPosPS.setInt(6, this.getLayer());
                CreaturePos.cretPosPS.setLong(7, this.getBridgeId());
                CreaturePos.cretPosPS.setLong(8, this.getWurmid());
                CreaturePos.cretPosPS.addBatch();
                ++CreaturePos.cretPosPSCount;
                ++CreaturePos.totalCretPosPSCount;
                if (CreaturePos.cretPosPSCount > Constants.numberOfDbCreaturePositionsToUpdateEachTime || immediately) {
                    CreaturePos.cretPosPS.executeBatch();
                    DbUtilities.closeDatabaseObjects(CreaturePos.cretPosPS, null);
                    CreaturePos.cretPosPS = null;
                    CreaturePos.cretPosPSCount = 0;
                }
            }
            this.changed = false;
        }
    }
    
    public boolean isPlayer() {
        return this.isPlayer;
    }
    
    public static final void loadAllPositions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM POSITION");
            rs = ps.executeQuery();
            while (rs.next()) {
                new CreaturePos(rs.getLong("WURMID"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getInt("ZONEID"), rs.getInt("LAYER"), rs.getLong("ONBRIDGE"), false);
            }
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM POSITION");
            rs = ps.executeQuery();
            while (rs.next()) {
                new CreaturePos(rs.getLong("WURMID"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getInt("ZONEID"), rs.getInt("LAYER"), rs.getLong("ONBRIDGE"), false);
            }
        }
        catch (Exception sqex) {
            CreaturePos.logger.log(Level.WARNING, "Failed to load all positions", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final CreaturePos getPosition(final long wurmId) {
        return CreaturePos.allPositions.get(wurmId);
    }
    
    public static final void delete(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            boolean player = false;
            final CreaturePos pos = getPosition(wurmId);
            if (pos != null) {
                player = pos.isPlayer();
                CreaturePos.allPositions.remove(wurmId);
            }
            if (player) {
                dbcon = DbConnector.getPlayerDbCon();
            }
            else {
                dbcon = DbConnector.getCreatureDbCon();
            }
            ps = dbcon.prepareStatement("DELETE FROM POSITION WHERE WURMID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (Exception sqex) {
            CreaturePos.logger.log(Level.WARNING, "Failed to load all positions", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static CreaturePositionDatabaseUpdater getCreatureDbPosUpdater() {
        return CreaturePos.creatureDbPosUpdater;
    }
    
    public static PlayerPositionDatabaseUpdater getPlayerDbPosUpdater() {
        return CreaturePos.playerDbPosUpdater;
    }
    
    static {
        logger = Logger.getLogger(CreaturePos.class.getName());
        CreaturePos.logCreaturePos = false;
        allPositions = new ConcurrentHashMap<Long, CreaturePos>();
        CreaturePos.cretPosPS = null;
        CreaturePos.cretPosPSCount = 0;
        CreaturePos.totalCretPosPSCount = 0;
        CreaturePos.playPosPS = null;
        CreaturePos.playPosPSCount = 0;
        CreaturePos.totalPlayPosPSCount = 0;
        creatureDbPosUpdater = new CreaturePositionDatabaseUpdater("Creature Database Position Updater", Constants.numberOfDbCreaturePositionsToUpdateEachTime);
        playerDbPosUpdater = new PlayerPositionDatabaseUpdater("Player Database Position Updater", Constants.numberOfDbPlayerPositionsToUpdateEachTime);
    }
}
