// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.DbConnector;

public final class DbSearcher
{
    private static final String getPlayerName = "select * from PLAYERS where WURMID=?";
    private static final String getPlayerId = "select * from PLAYERS where NAME=?";
    
    public static String getNameForPlayer(final long wurmId) throws IOException, NoSuchPlayerException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select * from PLAYERS where WURMID=?");
            ps.setLong(1, wurmId);
            rs = ps.executeQuery();
            if (rs.next()) {
                final String name = rs.getString("NAME");
                return name;
            }
            throw new NoSuchPlayerException("No player with id " + wurmId);
        }
        catch (SQLException sqx) {
            throw new IOException("Problem finding Player ID " + wurmId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static long getWurmIdForPlayer(final String name) throws IOException, NoSuchPlayerException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select * from PLAYERS where NAME=?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                final long id = rs.getLong("WURMID");
                return id;
            }
            throw new NoSuchPlayerException("No player with name " + name);
        }
        catch (SQLException sqx) {
            throw new IOException("Problem finding Player name " + name, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
}
