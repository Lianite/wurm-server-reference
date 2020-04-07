// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui;

import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.DbConnector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PlayerDBInterface
{
    private static final String GET_ALL_PLAYERS = "SELECT * FROM PLAYERS";
    private static final String GET_ALL_POSITION = "SELECT * FROM POSITION";
    private static final Logger logger;
    private static final ConcurrentHashMap<String, PlayerData> playerDatas;
    
    public static final void loadAllData() {
        PlayerDBInterface.playerDatas.clear();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PLAYERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("NAME");
                name = LoginHandler.raiseFirstLetter(name);
                final PlayerData pinf = new PlayerData();
                pinf.setName(name);
                pinf.setWurmid(rs.getLong("WURMID"));
                pinf.setPower(rs.getByte("POWER"));
                pinf.setServer(rs.getInt("CURRENTSERVER"));
                pinf.setUndeadType(rs.getByte("UNDEADTYPE"));
                PlayerDBInterface.playerDatas.put(name, pinf);
            }
        }
        catch (SQLException ex) {
            PlayerDBInterface.logger.log(Level.WARNING, "Failed to load all player data.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final PlayerData getFromWurmId(final long wurmid) {
        for (final PlayerData pd : PlayerDBInterface.playerDatas.values()) {
            if (pd.getWurmid() == wurmid) {
                return pd;
            }
        }
        return null;
    }
    
    public static final void loadAllPositionData() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM POSITION");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final PlayerData pd = getFromWurmId(wurmid);
                if (pd != null) {
                    pd.setPosx(rs.getFloat("POSX"));
                    pd.setPosy(rs.getFloat("POSY"));
                }
            }
        }
        catch (SQLException ex) {
            PlayerDBInterface.logger.log(Level.WARNING, "Failed to load all player data.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final PlayerData[] getAllData() {
        return PlayerDBInterface.playerDatas.values().toArray(new PlayerData[PlayerDBInterface.playerDatas.size()]);
    }
    
    public static final PlayerData getPlayerData(final String name) {
        return PlayerDBInterface.playerDatas.get(name);
    }
    
    static {
        logger = Logger.getLogger(PlayerDBInterface.class.getName());
        playerDatas = new ConcurrentHashMap<String, PlayerData>();
    }
}
