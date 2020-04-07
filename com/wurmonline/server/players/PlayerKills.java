// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.statistics.ChallengeSummary;
import com.wurmonline.server.statistics.ChallengePointEnum;
import com.wurmonline.server.Servers;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerKills
{
    private final long wurmid;
    private static final Logger logger;
    private static final String GET_KILLS = "SELECT * FROM KILLS WHERE WURMID=?";
    private final Map<Long, PlayerKill> kills;
    
    public PlayerKills(final long _wurmId) {
        this.kills = new HashMap<Long, PlayerKill>();
        this.wurmid = _wurmId;
        this.load();
    }
    
    private void load() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM KILLS WHERE WURMID=?");
            ps.setLong(1, this.wurmid);
            rs = ps.executeQuery();
            while (rs.next()) {
                final Long vid = new Long(rs.getLong("VICTIM"));
                final PlayerKill pk = this.kills.get(vid);
                if (pk != null) {
                    pk.addKill(rs.getLong("KILLTIME"), rs.getString("VICTIMNAME"), true);
                }
                else {
                    this.kills.put(vid, new PlayerKill(vid, rs.getLong("KILLTIME"), rs.getString("VICTIMNAME"), 1));
                }
            }
        }
        catch (SQLException ex) {
            PlayerKills.logger.log(Level.INFO, "Failed to load kills for " + this.wurmid, ex);
        }
        catch (Exception ex2) {
            PlayerKills.logger.log(Level.INFO, "Failed to load kills for " + this.wurmid, ex2);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getLastKill(final long victimId) {
        final PlayerKill pk = this.kills.get(victimId);
        if (pk != null) {
            return pk.getLastKill();
        }
        return 0L;
    }
    
    public long getNumKills(final long victimId) {
        final PlayerKill pk = this.kills.get(victimId);
        if (pk != null) {
            return pk.getNumKills();
        }
        return 0L;
    }
    
    public void addKill(final long victimId, final String victimName) {
        final Long vid = new Long(victimId);
        PlayerKill pk = this.kills.get(vid);
        if (pk != null) {
            pk.kill(this.wurmid, victimName);
        }
        else {
            pk = new PlayerKill(victimId, System.currentTimeMillis(), victimName, 0);
            if (Servers.localServer.isChallengeServer()) {
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(this.wurmid);
                if (pinf != null) {
                    final Achievements ach = Achievements.getAchievementObject(this.wurmid);
                    if (ach != null && ach.getAchievement(369) != null) {
                        ChallengeSummary.addToScore(pinf, ChallengePointEnum.ChallengePoint.PLAYERKILLS.getEnumtype(), 1.0f);
                        ChallengeSummary.addToScore(pinf, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 10.0f);
                    }
                }
            }
            pk.kill(this.wurmid, victimName);
            this.kills.put(vid, pk);
        }
    }
    
    public boolean isOverKilling(final long victimId) {
        final Long vid = new Long(victimId);
        final PlayerKill pk = this.kills.get(vid);
        return pk != null && pk.isOverkilling();
    }
    
    public int getNumberOfKills() {
        return this.kills.size();
    }
    
    static {
        logger = Logger.getLogger(PlayerKills.class.getName());
    }
}
