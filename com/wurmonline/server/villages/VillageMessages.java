// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class VillageMessages
{
    private static final Logger logger;
    private static final Map<Integer, VillageMessages> villagesMessages;
    private static final String LOAD_ALL_MSGS = "SELECT * FROM VILLAGEMESSAGES";
    private static final String DELETE_VILLAGE_MSGS = "DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=?";
    private static final String CREATE_MSG = "INSERT INTO VILLAGEMESSAGES (VILLAGEID,FROMID,TOID,MESSAGE,POSTED,PENCOLOR,EVERYONE) VALUES (?,?,?,?,?,?,?);";
    private static final String DELETE_MSG = "DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=? AND TOID=? AND POSTED=?";
    private static final String DELETE_PLAYER_MSGS = "DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=? AND TOID=?";
    private Map<Long, Map<Long, VillageMessage>> villageMsgs;
    
    public VillageMessages() {
        this.villageMsgs = new ConcurrentHashMap<Long, Map<Long, VillageMessage>>();
    }
    
    public VillageMessage put(final long toId, final VillageMessage value) {
        Map<Long, VillageMessage> msgs = this.villageMsgs.get(toId);
        if (msgs == null) {
            msgs = new ConcurrentHashMap<Long, VillageMessage>();
            this.villageMsgs.put(toId, msgs);
        }
        return msgs.put(value.getPostedTime(), value);
    }
    
    public Map<Long, VillageMessage> get(final long toId) {
        final Map<Long, VillageMessage> msgs = this.villageMsgs.get(toId);
        if (msgs == null) {
            return new ConcurrentHashMap<Long, VillageMessage>();
        }
        return msgs;
    }
    
    public void remove(final long playerId, final long posted) {
        final Map<Long, VillageMessage> msgs = this.villageMsgs.get(playerId);
        if (msgs != null) {
            msgs.remove(posted);
        }
    }
    
    public void remove(final long playerId) {
        this.villageMsgs.remove(playerId);
    }
    
    public static void loadVillageMessages() {
        final long start = System.nanoTime();
        int loadedMsgs = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VILLAGEMESSAGES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final VillageMessage villageMsg = new VillageMessage(rs.getInt("VILLAGEID"), rs.getLong("FROMID"), rs.getLong("TOID"), rs.getString("MESSAGE"), rs.getInt("PENCOLOR"), rs.getLong("POSTED"), rs.getBoolean("EVERYONE"));
                add(villageMsg);
                ++loadedMsgs;
            }
        }
        catch (SQLException sqex) {
            VillageMessages.logger.log(Level.WARNING, "Failed to load village messages due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            VillageMessages.logger.info("Loaded " + loadedMsgs + " village messages from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static void add(final VillageMessage villageMsg) {
        VillageMessages villageMsgs = VillageMessages.villagesMessages.get(villageMsg.getVillageId());
        if (villageMsgs == null) {
            villageMsgs = new VillageMessages();
            VillageMessages.villagesMessages.put(villageMsg.getVillageId(), villageMsgs);
        }
        villageMsgs.put(villageMsg.getToId(), villageMsg);
    }
    
    public static VillageMessage[] getVillageMessages(final int villageId, final long toId) {
        final VillageMessages villageMsgs = VillageMessages.villagesMessages.get(villageId);
        if (villageMsgs == null) {
            return new VillageMessage[0];
        }
        return villageMsgs.get(toId).values().toArray(new VillageMessage[villageMsgs.size()]);
    }
    
    private int size() {
        return 0;
    }
    
    public static final VillageMessage create(final int villageId, final long fromId, final long toId, final String message, final int penColour, final boolean everyone) {
        final long posted = System.currentTimeMillis();
        dbCreate(villageId, fromId, toId, message, posted, penColour, everyone);
        final VillageMessage villageMsg = new VillageMessage(villageId, fromId, toId, message, penColour, posted, everyone);
        add(villageMsg);
        return villageMsg;
    }
    
    public static final void delete(final int villageId) {
        dbDelete(villageId);
        VillageMessages.villagesMessages.remove(villageId);
    }
    
    public static final void delete(final int villageId, final long toId) {
        dbDelete(villageId, toId);
        final VillageMessages villageMsgs = VillageMessages.villagesMessages.get(villageId);
        if (villageMsgs != null) {
            villageMsgs.remove(toId);
        }
    }
    
    public static final void delete(final int villageId, final long toId, final long posted) {
        dbDelete(villageId, toId, posted);
        final VillageMessages villageMsgs = VillageMessages.villagesMessages.get(villageId);
        if (villageMsgs != null) {
            villageMsgs.remove(toId, posted);
        }
    }
    
    private static final void dbCreate(final int villageId, final long fromId, final long toId, final String message, final long posted, final int penColour, final boolean everyone) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO VILLAGEMESSAGES (VILLAGEID,FROMID,TOID,MESSAGE,POSTED,PENCOLOR,EVERYONE) VALUES (?,?,?,?,?,?,?);");
            ps.setInt(1, villageId);
            ps.setLong(2, fromId);
            ps.setLong(3, toId);
            ps.setString(4, message);
            ps.setLong(5, posted);
            ps.setInt(6, penColour);
            ps.setBoolean(7, everyone);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            VillageMessages.logger.log(Level.WARNING, "Failed to create new message for village: " + villageId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void dbDelete(final int villageId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=?");
            ps.setInt(1, villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            VillageMessages.logger.log(Level.WARNING, "Failed to delete all messages for village: " + villageId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void dbDelete(final int villageId, final long toId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=? AND TOID=?");
            ps.setInt(1, villageId);
            ps.setLong(2, toId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            VillageMessages.logger.log(Level.WARNING, "Failed to delete message for village " + villageId + ", and player " + toId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void dbDelete(final int villageId, final long toId, final long posted) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM VILLAGEMESSAGES WHERE VILLAGEID=? AND TOID=? AND POSTED=?");
            ps.setInt(1, villageId);
            ps.setLong(2, toId);
            ps.setLong(3, posted);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            VillageMessages.logger.log(Level.WARNING, "Failed to delete message for village " + villageId + ", and player " + toId + " and posted " + posted + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(VillageMessages.class.getName());
        villagesMessages = new ConcurrentHashMap<Integer, VillageMessages>();
    }
}
