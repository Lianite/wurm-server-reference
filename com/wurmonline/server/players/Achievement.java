// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.Random;
import java.util.LinkedList;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.Creature;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.sql.Timestamp;
import com.wurmonline.server.MiscConstants;

public final class Achievement implements MiscConstants
{
    private final Timestamp dateAchieved;
    private final int achievement;
    private int counter;
    private long holder;
    private int localId;
    private static final String INSERT;
    private static final String INSERT_TRANSFER;
    private static final String UPDATE_COUNTER = "UPDATE ACHIEVEMENTS SET COUNTER=? WHERE ID=?";
    private static final Logger logger;
    private static final ConcurrentHashMap<Integer, AchievementTemplate> templates;
    protected static final List<AchievementTemplate> personalGoalSilverTemplates;
    protected static final List<AchievementTemplate> personalGoalGoldTemplates;
    protected static final List<AchievementTemplate> personalGoalDiamondTemplates;
    
    public Achievement(final int aAchievement, final Timestamp date, final long achiever, final int timesTriggered, final int localid) {
        this.counter = 0;
        this.holder = 0L;
        this.localId = -1;
        this.achievement = aAchievement;
        this.setHolder(achiever);
        this.dateAchieved = date;
        this.counter = timesTriggered;
        this.localId = localid;
        if (getTemplate(aAchievement) == null) {
            addTemplate(new AchievementTemplate(aAchievement, "Unknown", false));
        }
    }
    
    public final Timestamp getDateAchieved() {
        return this.dateAchieved;
    }
    
    public final int getAchievement() {
        return this.achievement;
    }
    
    public final int getCounter() {
        return this.counter;
    }
    
    final int[] setCounter(final int aCounter) {
        if (this.counter != aCounter) {
            this.counter = aCounter;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("UPDATE ACHIEVEMENTS SET COUNTER=? WHERE ID=?");
                ps.setInt(1, this.counter);
                ps.setInt(2, this.localId);
                ps.executeUpdate();
            }
            catch (SQLException ex) {
                Achievement.logger.log(Level.WARNING, "Failed to save achievement " + this.achievement + " counter " + aCounter + " for " + this.holder);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            return this.getTriggeredAchievements();
        }
        return Achievement.EMPTY_INT_ARRAY;
    }
    
    final int[] getTriggeredAchievements() {
        return this.getTemplate().getAchievementsTriggered();
    }
    
    public final AchievementTemplate getTemplate() {
        return getTemplate(this.achievement);
    }
    
    public final long getHolder() {
        return this.holder;
    }
    
    private final void setHolder(final long aHolder) {
        this.holder = aHolder;
    }
    
    public final void create(final boolean transfer) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (transfer) {
                ps = dbcon.prepareStatement(Achievement.INSERT_TRANSFER, 1);
            }
            else {
                ps = dbcon.prepareStatement(Achievement.INSERT, 1);
            }
            ps.setLong(1, this.holder);
            ps.setInt(2, this.achievement);
            ps.setInt(3, this.counter);
            if (transfer) {
                if (DbConnector.isUseSqlite()) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String ts = sdf.format(this.getDateAchieved());
                    ps.setString(4, ts);
                }
                else {
                    ps.setTimestamp(4, this.getDateAchieved());
                }
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.localId = rs.getInt(1);
            }
        }
        catch (SQLException ex) {
            Achievement.logger.log(Level.WARNING, "Failed to save achievement " + this.achievement + " for " + this.holder, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        Achievements.addAchievement(this, true);
    }
    
    public final void sendNewAchievement(final Creature creature) {
        creature.getCommunicator().sendAchievement(this, true);
    }
    
    public final void sendUpdateAchievement(final Creature creature) {
        creature.getCommunicator().sendAchievement(this, false);
    }
    
    public final void sendUpdatePersonalGoal(final Creature creature) {
        creature.getCommunicator().updatePersonalGoal(this, true);
    }
    
    final int getTriggerOnCounter() {
        return this.getTemplate().getTriggerOnCounter();
    }
    
    static final void addTemplate(final AchievementTemplate template) {
        Achievement.templates.put(template.getNumber(), template);
        if (template.isPersonalGoal()) {
            if (template.getType() == 3) {
                Achievement.personalGoalSilverTemplates.add(template);
            }
            else if (template.getType() == 4) {
                Achievement.personalGoalGoldTemplates.add(template);
            }
            else if (template.getType() == 5) {
                Achievement.personalGoalDiamondTemplates.add(template);
            }
        }
    }
    
    static final void removeTemplate(final AchievementTemplate template) {
        Achievement.templates.remove(template.getNumber());
    }
    
    @Nullable
    public static final AchievementTemplate getTemplate(final int number) {
        return Achievement.templates.get(number);
    }
    
    @Nullable
    public static final AchievementTemplate getTemplate(final String name) {
        for (final AchievementTemplate achievement : Achievement.templates.values()) {
            if (achievement.getName().equalsIgnoreCase(name)) {
                return achievement;
            }
        }
        return null;
    }
    
    public final boolean isInVisible() {
        return this.getTemplate().isInvisible();
    }
    
    public final boolean isPlaySoundOnUpdate() {
        return this.getTemplate().isPlaySoundOnUpdate();
    }
    
    public final boolean isOneTimer() {
        return this.getTemplate().isOneTimer();
    }
    
    public static final LinkedList<AchievementTemplate> getSteelAchievements(final Creature creature) {
        final LinkedList<AchievementTemplate> toReturn = new LinkedList<AchievementTemplate>();
        for (final AchievementTemplate achievement : Achievement.templates.values()) {
            if (achievement.getType() == 2 && (creature.getPower() > 0 || creature.getName().equalsIgnoreCase(achievement.getCreator().toLowerCase()))) {
                toReturn.add(achievement);
            }
        }
        return toReturn;
    }
    
    public static final AchievementTemplate getRandomPersonalGoldAchievement(final Random personalRandom) {
        final int num = personalRandom.nextInt(Achievement.personalGoalGoldTemplates.size());
        return Achievement.personalGoalGoldTemplates.get(num);
    }
    
    public static final AchievementTemplate getRandomPersonalSilverAchievement(final Random personalRandom) {
        final int num = personalRandom.nextInt(Achievement.personalGoalSilverTemplates.size());
        return Achievement.personalGoalSilverTemplates.get(num);
    }
    
    public static final AchievementTemplate getRandomPersonalDiamondAchievement(final Random personalRandom) {
        final int num = personalRandom.nextInt(Achievement.personalGoalDiamondTemplates.size());
        return Achievement.personalGoalDiamondTemplates.get(num);
    }
    
    public static ConcurrentHashMap<Integer, AchievementTemplate> getAllTemplates() {
        return Achievement.templates;
    }
    
    public static float getAchievementProgress(final long wurmId, final int achievementNum) {
        final AchievementTemplate t = getTemplate(achievementNum);
        if (t == null) {
            return 0.0f;
        }
        return t.getProgressFor(wurmId);
    }
    
    static {
        INSERT = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO ACHIEVEMENTS (PLAYER,ACHIEVEMENT,COUNTER) VALUES (?,?,?)" : "INSERT IGNORE INTO ACHIEVEMENTS (PLAYER,ACHIEVEMENT,COUNTER) VALUES (?,?,?)");
        INSERT_TRANSFER = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO ACHIEVEMENTS (PLAYER,ACHIEVEMENT,COUNTER,ADATE) VALUES (?,?,?,?)" : "INSERT IGNORE INTO ACHIEVEMENTS (PLAYER,ACHIEVEMENT,COUNTER,ADATE) VALUES (?,?,?,?)");
        logger = Logger.getLogger(Achievement.class.getName());
        templates = new ConcurrentHashMap<Integer, AchievementTemplate>();
        personalGoalSilverTemplates = new LinkedList<AchievementTemplate>();
        personalGoalGoldTemplates = new LinkedList<AchievementTemplate>();
        personalGoalDiamondTemplates = new LinkedList<AchievementTemplate>();
    }
}
