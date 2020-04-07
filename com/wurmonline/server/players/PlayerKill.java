// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public final class PlayerKill implements TimeConstants
{
    private static final Logger logger;
    private static final String ADD_KILL = "INSERT INTO KILLS(WURMID,VICTIM, VICTIMNAME,KILLTIME) VALUES(?,?,?,?)";
    private final long victimId;
    private long lastKilled;
    private int timesKilled;
    private int timesKilledSinceRestart;
    private String name;
    
    PlayerKill(final long _victimId, final long _lastKilled, final String _name, final int kills) {
        this.lastKilled = -10L;
        this.timesKilled = 0;
        this.timesKilledSinceRestart = 0;
        this.name = "";
        this.victimId = _victimId;
        this.name = _name;
        this.lastKilled = _lastKilled;
        this.timesKilled = kills;
    }
    
    void addKill(final long time, final String victimname, final boolean loading) {
        ++this.timesKilled;
        if (!loading) {
            ++this.timesKilledSinceRestart;
        }
        if (time > this.lastKilled) {
            this.lastKilled = time;
            this.name = victimname;
        }
    }
    
    void kill(final long killerId, final String victimname) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO KILLS(WURMID,VICTIM, VICTIMNAME,KILLTIME) VALUES(?,?,?,?)");
            ps.setLong(1, killerId);
            ps.setLong(2, this.victimId);
            ps.setString(3, victimname);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PlayerKill.logger.log(Level.WARNING, "Failed to add kill for  " + killerId, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        this.addKill(System.currentTimeMillis(), victimname, false);
        if (this.isOverkilling()) {
            String kname = String.valueOf(killerId);
            try {
                kname = Players.getInstance().getNameFor(killerId);
            }
            catch (NoSuchPlayerException nsp) {
                PlayerKill.logger.log(Level.INFO, "weird " + kname + " not online while killing " + killerId);
            }
            catch (IOException iox) {
                PlayerKill.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            PlayerKill.logger.log(Level.INFO, kname + " overkilling " + this.name + " since restart: " + this.timesKilledSinceRestart + " overall: " + this.timesKilled);
        }
    }
    
    long getLastKill() {
        return this.lastKilled;
    }
    
    int getNumKills() {
        return this.timesKilled;
    }
    
    boolean isOverkilling() {
        return (this.lastKilled > System.currentTimeMillis() - 21600000L && this.timesKilledSinceRestart > 3) || this.timesKilled > 20;
    }
    
    static {
        logger = Logger.getLogger(PlayerKill.class.getName());
    }
}
