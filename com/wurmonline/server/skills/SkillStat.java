// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import com.wurmonline.server.DbConnector;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public final class SkillStat implements TimeConstants
{
    private static Logger logger;
    public final Map<Long, Double> stats;
    @GuardedBy("RW_LOCK")
    private static final Map<Integer, SkillStat> allStats;
    private static final ReentrantReadWriteLock RW_LOCK;
    private final String skillName;
    private final int skillnum;
    private static final String loadAllPlayerSkills = "select NUMBER,OWNER,VALUE from SKILLS sk INNER JOIN PLAYERS p ON p.WURMID=sk.OWNER AND p.CURRENTSERVER=? WHERE sk.VALUE>25 ";
    
    private SkillStat(final int num, final String name) {
        this.stats = new HashMap<Long, Double>();
        this.skillName = name;
        this.skillnum = num;
    }
    
    private static int loadAllStats() {
        Connection dbcon = null;
        int numberSkillsLoaded = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select NUMBER,OWNER,VALUE from SKILLS sk INNER JOIN PLAYERS p ON p.WURMID=sk.OWNER AND p.CURRENTSERVER=? WHERE sk.VALUE>25 ");
            ps.setInt(1, Servers.localServer.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final SkillStat sk = getSkillStatForSkill(rs.getInt("NUMBER"));
                if (sk != null) {
                    sk.stats.put(new Long(rs.getLong("OWNER")), new Double(rs.getDouble("VALUE")));
                }
                ++numberSkillsLoaded;
            }
        }
        catch (SQLException sqx) {
            SkillStat.logger.log(Level.WARNING, "Problem loading the Skill stats due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return numberSkillsLoaded;
    }
    
    static final void addSkill(final int skillNum, final String name) {
        SkillStat.RW_LOCK.writeLock().lock();
        try {
            SkillStat.allStats.put(skillNum, new SkillStat(skillNum, name));
        }
        finally {
            SkillStat.RW_LOCK.writeLock().unlock();
        }
    }
    
    public static final void pollSkills() {
        final Thread statsPoller = new Thread("StatsPoller") {
            @Override
            public void run() {
                try {
                    final long now = System.currentTimeMillis();
                    final int numberSkillsLoaded = loadAllStats();
                    SkillStat.logger.log(Level.WARNING, "Polling " + numberSkillsLoaded + " skills for stats v2 took " + (System.currentTimeMillis() - now) + " ms.");
                }
                catch (RuntimeException e) {
                    SkillStat.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        };
        statsPoller.start();
    }
    
    public static final SkillStat getSkillStatForSkill(final int num) {
        SkillStat.RW_LOCK.readLock().lock();
        try {
            return SkillStat.allStats.get(num);
        }
        finally {
            SkillStat.RW_LOCK.readLock().unlock();
        }
    }
    
    static {
        SkillStat.logger = Logger.getLogger(SkillStat.class.getName());
        allStats = new HashMap<Integer, SkillStat>();
        RW_LOCK = new ReentrantReadWriteLock();
    }
}
