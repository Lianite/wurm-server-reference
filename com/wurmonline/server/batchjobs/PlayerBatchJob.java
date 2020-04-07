// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.batchjobs;

import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.intra.IntraServerConnection;
import com.wurmonline.server.Server;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.Servers;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.items.CoinDbStrings;
import com.wurmonline.server.items.BodyDbStrings;
import com.wurmonline.server.items.ItemDbStrings;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public final class PlayerBatchJob implements TimeConstants, MiscConstants
{
    private static final String deleteSkills = "DELETE FROM SKILLS WHERE OWNER=?";
    private static final String deletePlayer = "DELETE FROM PLAYERS WHERE WURMID=?";
    private static final String getPlayers;
    private static final String getAllPlayers = "SELECT WURMID,PASSWORD FROM WURMPLAYERS2.PLAYERS";
    private static final String getAllReimbs = "SELECT * FROM REIMB WHERE CREATED=0";
    private static final String insertPassword = "UPDATE PLAYERS SET PASSWORD=? WHERE WURMID=?";
    private static final String getChampions = "SELECT WURMID FROM PLAYERS WHERE REALDEATH>0 AND REALDEATH<5";
    private static final String revertChampFaithFavor = "UPDATE PLAYERS SET FAITH=50, FAVOR=50, REALDEATH=0,PRIEST=1 WHERE WURMID=?";
    private static final String updateChampSkillStepOne = "UPDATE SKILLS SET VALUE=VALUE-50, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=?";
    private static final String updateChampSkillStepTwo = "UPDATE SKILLS SET VALUE=10, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=? AND VALUE<10";
    private static final String updateChampSkillStatOne = "UPDATE SKILLS SET VALUE=VALUE-5, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=?";
    private static final String selectChanneling = "SELECT * FROM SKILLS WHERE OWNER=? AND NUMBER=10067";
    private static final String updateChanneling = "UPDATE SKILLS SET VALUE=?, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=10067";
    private static final String reimburseFatigue = "UPDATE PLAYERS SET FATIGUE=?,LASTFATIGUE=?,SLEEP=LEAST(36000,SLEEP+?)";
    private static Logger logger;
    private static final String updateReimb = "UPDATE REIMB SET CREATED=1 WHERE NAME=?";
    
    public static final void monthlyPrune() {
        try {
            final Connection pdbcon = DbConnector.getPlayerDbCon();
            final Connection idbcon = DbConnector.getItemDbCon();
            final PreparedStatement ps = pdbcon.prepareStatement(PlayerBatchJob.getPlayers);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final long owner = rs.getLong("WURMID");
                final PreparedStatement ps2 = idbcon.prepareStatement(ItemDbStrings.getInstance().deleteByOwnerId());
                ps2.setLong(1, owner);
                ps2.executeUpdate();
                ps2.close();
                final PreparedStatement ps3 = pdbcon.prepareStatement("DELETE FROM SKILLS WHERE OWNER=?");
                ps3.setLong(1, owner);
                ps3.executeUpdate();
                ps3.close();
                final PreparedStatement ps4 = pdbcon.prepareStatement("DELETE FROM PLAYERS WHERE WURMID=?");
                ps4.setLong(1, owner);
                ps4.executeUpdate();
                ps4.close();
                final PreparedStatement ps5 = idbcon.prepareStatement(BodyDbStrings.getInstance().deleteByOwnerId());
                ps5.setLong(1, owner);
                ps5.executeUpdate();
                ps5.close();
                final PreparedStatement ps6 = idbcon.prepareStatement(CoinDbStrings.getInstance().deleteByOwnerId());
                ps6.setLong(1, owner);
                ps6.executeUpdate();
                ps6.close();
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    public static final void reimburseFatigue() {
        PlayerBatchJob.logger.log(Level.INFO, "Wurm crashed. Reimbursing fatigue for all players.");
        try {
            final Connection pdbcon = DbConnector.getPlayerDbCon();
            final PreparedStatement ps = pdbcon.prepareStatement("UPDATE PLAYERS SET FATIGUE=?,LASTFATIGUE=?,SLEEP=LEAST(36000,SLEEP+?)");
            ps.setLong(1, 43200L);
            ps.setLong(2, System.currentTimeMillis());
            ps.setLong(3, 18000L);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    public static final void fixPasswords() {
        PlayerBatchJob.logger.log(Level.INFO, "Fixing passwords.");
        try {
            long wurmid = -1L;
            String password = "";
            final Connection pdbcon = DbConnector.getPlayerDbCon();
            final PreparedStatement ps = pdbcon.prepareStatement("SELECT WURMID,PASSWORD FROM WURMPLAYERS2.PLAYERS");
            final ResultSet rs = ps.executeQuery();
            int nums = 0;
            while (rs.next()) {
                wurmid = rs.getLong("WURMID");
                password = rs.getString("PASSWORD");
                final PreparedStatement ps2 = pdbcon.prepareStatement("UPDATE PLAYERS SET PASSWORD=? WHERE WURMID=?");
                ps2.setString(1, password);
                ps2.setLong(2, wurmid);
                ps2.executeUpdate();
                ps2.close();
                ++nums;
            }
            rs.close();
            ps.close();
            PlayerBatchJob.logger.log(Level.INFO, "Fixed " + nums + " passwords.");
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    public static final void createReimbursedAccs() {
        try {
            final Connection pdbcon = DbConnector.getPlayerDbCon();
            final PreparedStatement ps = pdbcon.prepareStatement("SELECT * FROM REIMB WHERE CREATED=0");
            final ResultSet rs = ps.executeQuery();
            int nums = 0;
            boolean wild = false;
            int serverId = 2;
            byte kingdom = 1;
            String name = "";
            String password = "";
            String email = "";
            String challengePhrase = "";
            String answer = "";
            byte power = 0;
            byte gender = 0;
            while (rs.next()) {
                serverId = 2;
                name = rs.getString("NAME");
                email = rs.getString("EMAIL");
                password = rs.getString("PASSWORD");
                if (password == null || password.length() == 0) {
                    password = name + "kjhoiu1131";
                }
                challengePhrase = rs.getString("PWQUESTION");
                if (challengePhrase == null || challengePhrase.length() == 0) {
                    challengePhrase = "";
                }
                answer = rs.getString("PWANSWER");
                if (answer == null || answer.length() == 0) {
                    answer = "";
                }
                wild = rs.getBoolean("WILD");
                power = rs.getByte("POWER");
                kingdom = rs.getByte("KINGDOM");
                gender = rs.getByte("GENDER");
                if (wild) {
                    serverId = 3;
                }
                final ServerEntry toCreateOn = Servers.getServerWithId(serverId);
                if (toCreateOn != null) {
                    int tilex = toCreateOn.SPAWNPOINTJENNX;
                    int tiley = toCreateOn.SPAWNPOINTJENNY;
                    if (kingdom == 3) {
                        tilex = toCreateOn.SPAWNPOINTLIBX;
                        tiley = toCreateOn.SPAWNPOINTLIBY;
                    }
                    final LoginServerWebConnection lsw = new LoginServerWebConnection(serverId);
                    try {
                        PlayerBatchJob.logger.log(Level.INFO, "Creating " + name + " on server " + serverId);
                        final byte[] playerData = lsw.createAndReturnPlayer(name, password, challengePhrase, answer, email, kingdom, power, Server.rand.nextLong(), gender, true, true, true);
                        final long wurmId = IntraServerConnection.savePlayerToDisk(playerData, tilex, tiley, true, true);
                        ++nums;
                        final PreparedStatement ps2 = pdbcon.prepareStatement("UPDATE REIMB SET CREATED=1 WHERE NAME=?");
                        ps2.setString(1, name);
                        ps2.executeUpdate();
                        ps2.close();
                    }
                    catch (Exception ex) {
                        PlayerBatchJob.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
                else {
                    PlayerBatchJob.logger.log(Level.WARNING, "Failed to create player " + name + ": The desired server " + serverId + " does not exist.");
                }
            }
            rs.close();
            ps.close();
            PlayerBatchJob.logger.log(Level.INFO, "Created " + nums + " players.");
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    public static final void removeChampions() {
        try {
            final Connection pdbcon = DbConnector.getPlayerDbCon();
            final PreparedStatement ps = pdbcon.prepareStatement("SELECT WURMID FROM PLAYERS WHERE REALDEATH>0 AND REALDEATH<5");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final long owner = rs.getLong("WURMID");
                final PreparedStatement ps2 = pdbcon.prepareStatement("UPDATE PLAYERS SET FAITH=50, FAVOR=50, REALDEATH=0,PRIEST=1 WHERE WURMID=?");
                ps2.setLong(1, owner);
                ps2.executeUpdate();
                ps2.close();
                updateChampSkill(10066, owner, pdbcon);
                updateChampSkill(10068, owner, pdbcon);
                updateChampStat(104, owner, pdbcon);
                updateChampStat(102, owner, pdbcon);
                updateChampStat(103, owner, pdbcon);
                updateChampStat(100, owner, pdbcon);
                updateChampStat(101, owner, pdbcon);
                updateChampStat(106, owner, pdbcon);
                updateChampStat(105, owner, pdbcon);
                fixChanneling(owner, pdbcon);
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    private static final void updateChampSkill(final int number, final long wurmid, final Connection dbcon) {
        try {
            final PreparedStatement ps = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=VALUE-50, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=?");
            ps.setLong(1, wurmid);
            ps.setInt(2, number);
            ps.executeUpdate();
            ps.close();
            final PreparedStatement ps2 = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=10, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=? AND VALUE<10");
            ps2.setLong(1, wurmid);
            ps2.setInt(2, number);
            ps2.executeUpdate();
            ps2.close();
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    private static final void updateChampStat(final int number, final long wurmid, final Connection dbcon) {
        try {
            final PreparedStatement ps = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=VALUE-5, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=?");
            ps.setLong(1, wurmid);
            ps.setInt(2, number);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    private static final void fixChanneling(final long wurmid, final Connection dbcon) {
        try {
            final PreparedStatement ps = dbcon.prepareStatement("SELECT * FROM SKILLS WHERE OWNER=? AND NUMBER=10067");
            ps.setLong(1, wurmid);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final float value = rs.getFloat("VALUE");
                ps.close();
                rs.close();
                float newValue = value - 50.0f;
                if (value > 80.0f) {
                    newValue += (value - 80.0f) * 2.0f;
                    if (value > 85.0f) {
                        newValue += (value - 85.0f) * 2.0f;
                    }
                    if (value > 90.0f) {
                        newValue += (value - 90.0f) * 2.0f;
                    }
                }
                final PreparedStatement ps2 = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=?, MINVALUE=VALUE WHERE OWNER=? AND NUMBER=10067");
                ps2.setFloat(1, newValue);
                ps2.setLong(2, wurmid);
                ps2.executeUpdate();
                ps2.close();
            }
        }
        catch (SQLException sqx) {
            PlayerBatchJob.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
    }
    
    static {
        getPlayers = "SELECT WURMID FROM PLAYERS WHERE LASTLOGOUT<" + (System.currentTimeMillis() - 2419200000L);
        PlayerBatchJob.logger = Logger.getLogger(PlayerBatchJob.class.getName());
    }
}
