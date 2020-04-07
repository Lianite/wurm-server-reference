// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import javax.annotation.Nonnull;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.Iterator;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

public final class Affinities
{
    private static Logger logger;
    private static final String updatePlayerAffinity = "update AFFINITIES set NUMBER=? where WURMID=? AND SKILL=?";
    private static final String createPlayerAffinity = "INSERT INTO AFFINITIES (WURMID,SKILL,NUMBER) VALUES(?,?,?)";
    private static final String deletePlayerAffinity = "DELETE FROM AFFINITIES WHERE WURMID=? AND SKILL=?";
    private static final String loadAllAffinities = "SELECT * FROM AFFINITIES WHERE NUMBER>0";
    private static final String deleteAllPlayerAffinity = "DELETE FROM AFFINITIES WHERE WURMID=?";
    private static Map<Long, Set<Affinity>> affinities;
    private static Affinity toRemove;
    private static boolean found;
    private static final Affinity[] emptyAffs;
    
    public static void loadAffinities() {
        final long start = System.nanoTime();
        int loadedAffinities = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Affinities.affinities = new HashMap<Long, Set<Affinity>>();
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM AFFINITIES WHERE NUMBER>0");
            rs = ps.executeQuery();
            while (rs.next()) {
                setAffinity(rs.getLong("WURMID"), rs.getInt("SKILL"), rs.getByte("NUMBER"), true);
                ++loadedAffinities;
            }
        }
        catch (SQLException sqx) {
            Affinities.logger.log(Level.WARNING, "Failed to load affinities!", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Affinities.logger.info("Loaded " + loadedAffinities + " affinities from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static void setAffinity(final long playerid, final int skillnumber, int value, final boolean loading) {
        Affinities.found = false;
        final Long idl = playerid;
        Set<Affinity> affs = Affinities.affinities.get(idl);
        if (affs == null) {
            affs = new HashSet<Affinity>();
            Affinities.affinities.put(idl, affs);
        }
        for (final Affinity a : affs) {
            if (a.skillNumber == skillnumber) {
                Affinities.found = true;
                a.number = Math.min(5, value);
                if (!loading) {
                    setAffinityForSkill(playerid, a.skillNumber, a.number);
                    updateAffinity(playerid, a.skillNumber, a.number);
                }
                return;
            }
        }
        value = Math.min(5, value);
        if (!Affinities.found && !loading) {
            createAffinity(playerid, skillnumber, value);
            setAffinityForSkill(playerid, skillnumber, value);
        }
        affs.add(new Affinity(skillnumber, value));
    }
    
    public static void decreaseAffinity(final long playerid, final int skillnum, final int value) {
        final Long idl = playerid;
        final Set<Affinity> affs = Affinities.affinities.get(idl);
        Affinities.toRemove = null;
        if (affs == null) {
            Affinities.logger.log(Level.WARNING, "Affinities not found when removing from " + playerid);
            return;
        }
        for (final Affinity a : affs) {
            if (a.skillNumber == skillnum) {
                final Affinity affinity = a;
                affinity.number -= value;
                setAffinityForSkill(playerid, skillnum, Math.max(0, a.number));
                if (a.number <= 0) {
                    Affinities.toRemove = a;
                    break;
                }
                break;
            }
        }
        if (Affinities.toRemove != null) {
            affs.remove(Affinities.toRemove);
            deleteAffinity(playerid, skillnum);
        }
    }
    
    private static void setAffinityForSkill(final long playerid, final int skillid, final int number) {
        try {
            final Player p = Players.getInstance().getPlayer(playerid);
            if (p != null) {
                try {
                    final Skill s = p.getSkills().getSkill(skillid);
                    s.setAffinity(number);
                }
                catch (NoSuchSkillException ex) {}
            }
        }
        catch (NoSuchPlayerException ex2) {}
    }
    
    private static void deleteAffinity(final long playerid, final int skillnum) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM AFFINITIES WHERE WURMID=? AND SKILL=?");
            ps.setLong(1, playerid);
            ps.setInt(2, skillnum);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Affinities.logger.log(Level.WARNING, "Failed to delete affinity " + skillnum + " for " + playerid, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void deleteAllPlayerAffinity(final long playerid) {
        final Set<Affinity> set = Affinities.affinities.get(playerid);
        if (set != null) {
            set.clear();
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM AFFINITIES WHERE WURMID=?");
            ps.setLong(1, playerid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Affinities.logger.log(Level.WARNING, "Failed to delete affinities for " + playerid, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void updateAffinity(final long playerid, final int skillnum, final int number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("update AFFINITIES set NUMBER=? where WURMID=? AND SKILL=?");
            ps.setByte(1, (byte)number);
            ps.setLong(2, playerid);
            ps.setInt(3, skillnum);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Affinities.logger.log(Level.WARNING, "Failed to update affinity " + skillnum + " for " + playerid, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void createAffinity(final long playerid, final int skillnum, final int number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO AFFINITIES (WURMID,SKILL,NUMBER) VALUES(?,?,?)");
            ps.setLong(1, playerid);
            ps.setInt(2, skillnum);
            ps.setByte(3, (byte)number);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Affinities.logger.log(Level.WARNING, "Failed to create affinity " + skillnum + " for " + playerid + " nums=" + number, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Nonnull
    public static Affinity[] getAffinities(final long playerid) {
        final Set<Affinity> affs = Affinities.affinities.get(playerid);
        if (affs == null || affs.isEmpty()) {
            return Affinities.emptyAffs;
        }
        return affs.toArray(new Affinity[affs.size()]);
    }
    
    static {
        Affinities.logger = Logger.getLogger(Affinities.class.getName());
        Affinities.affinities = new HashMap<Long, Set<Affinity>>();
        Affinities.toRemove = null;
        Affinities.found = false;
        emptyAffs = new Affinity[0];
    }
}
