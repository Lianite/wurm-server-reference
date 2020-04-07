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
import com.wurmonline.server.DbConnector;
import java.util.Random;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.math.BigInteger;
import com.wurmonline.server.creatures.SpellEffectsEnum;
import java.util.Iterator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.WurmCalendar;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AffinitiesTimed
{
    private static final Logger logger;
    private static final Map<Long, AffinitiesTimed> playerTimedAffinities;
    private static final String GET_ALL_PLAYER_TIMED_AFFINITIES = "SELECT * FROM AFFINITIESTIMED";
    private static final String CREATE_PLAYER_TIMED_AFFINITY = "INSERT INTO AFFINITIESTIMED (PLAYERID,SKILL,EXPIRATION) VALUES (?,?,?)";
    private static final String UPDATE_PLAYER_TIMED_AFFINITY = "UPDATE AFFINITIESTIMED SET EXPIRATION=? WHERE PLAYERID=? AND SKILL=?";
    private static final String DELETE_PLAYER_TIMED_AFFINITIES = "DELETE FROM AFFINITIESTIMED WHERE PLAYERID=?";
    private static final String DELETE_PLAYER_SKILL_TIMED_AFFINITIES = "DELETE FROM AFFINITIESTIMED WHERE PLAYERID=? AND Skill=?";
    private final long wurmId;
    private final Map<Integer, Long> timedAffinities;
    private final Map<Integer, Integer> updateAffinities;
    private int lastSkillId;
    private long lastTime;
    
    public AffinitiesTimed(final long playerId) {
        this.timedAffinities = new ConcurrentHashMap<Integer, Long>();
        this.updateAffinities = new ConcurrentHashMap<Integer, Integer>();
        this.lastSkillId = -1;
        this.lastTime = -1L;
        this.wurmId = playerId;
    }
    
    public long getPlayerId() {
        return this.wurmId;
    }
    
    int getLastSkillId() {
        return this.lastSkillId;
    }
    
    long getLastTime() {
        return this.lastTime;
    }
    
    private void put(final int skill, final long expires) {
        this.timedAffinities.put(skill, expires);
    }
    
    @Nullable
    public Long getExpires(final int skill) {
        return this.timedAffinities.get(skill);
    }
    
    public boolean add(final int skill, final long duration) {
        boolean toReturn = false;
        final Long expires = this.getExpires(skill);
        long newExpires = 0L;
        if (expires == null) {
            newExpires = WurmCalendar.getCurrentTime() + duration * 10L;
            toReturn = true;
            this.updateAffinities.put(skill, skill);
        }
        else {
            newExpires = expires + duration;
            this.updateAffinities.put(skill, skill);
        }
        this.timedAffinities.put(skill, newExpires);
        this.lastSkillId = skill;
        this.lastTime = WurmCalendar.getCurrentTime();
        return toReturn;
    }
    
    public void remove(final int skill) {
        dbRemoveTimedAffinity(this.wurmId, skill);
        this.timedAffinities.remove(skill);
    }
    
    private void pollTimeAffinities(final Creature creature) {
        for (final Map.Entry<Integer, Long> entry : this.timedAffinities.entrySet()) {
            final int skillId = entry.getKey();
            final long expires = entry.getValue();
            if (expires < WurmCalendar.getCurrentTime()) {
                this.sendRemoveTimedAffinity(creature, skillId);
            }
        }
        for (final Integer skill : this.updateAffinities.values()) {
            final int skillId = skill;
            final Long expires2 = this.timedAffinities.get(skill);
            if (expires2 == null) {
                this.updateAffinities.remove(skill);
            }
            else if (skillId != this.lastSkillId) {
                this.updateAffinities.remove(skill);
                dbSaveTimedAffinity(this.wurmId, skillId, expires2, true);
            }
            else {
                if (WurmCalendar.getCurrentTime() <= this.lastTime + 50L) {
                    continue;
                }
                this.lastSkillId = -1;
                this.updateAffinities.remove(skill);
                dbSaveTimedAffinity(this.wurmId, skillId, expires2, true);
            }
        }
    }
    
    private boolean isEmpty() {
        return this.timedAffinities.isEmpty();
    }
    
    public void sendTimedAffinities(final Creature creature) {
        for (final Map.Entry<Integer, Long> entry : this.timedAffinities.entrySet()) {
            if (entry.getValue() > WurmCalendar.getCurrentTime()) {
                this.sendTimedAffinity(creature, entry.getKey());
            }
        }
    }
    
    public void sendTimedAffinity(final Creature creature, final int skillNum) {
        final long id = this.makeId(skillNum);
        final Long expires = this.getExpires(skillNum);
        if (expires != null) {
            final int dur = (int)((expires - WurmCalendar.getCurrentTime()) / 8.0f);
            if (dur > 0) {
                creature.getCommunicator().sendAddStatusEffect(id, SpellEffectsEnum.SKILL_TIMED_AFFINITY, dur, SkillSystem.getNameFor(skillNum));
            }
        }
    }
    
    public void sendRemoveTimedAffinities(final Creature creature) {
        for (final Map.Entry<Integer, Long> entry : this.timedAffinities.entrySet()) {
            this.sendRemoveTimedAffinity(creature, entry.getKey());
        }
    }
    
    public void sendRemoveTimedAffinity(final Creature creature, final int skillNum) {
        creature.getCommunicator().sendRemoveFromStatusEffectBar(this.makeId(skillNum));
        this.remove(skillNum);
    }
    
    private long makeId(final int skillNum) {
        final long sid = BigInteger.valueOf(skillNum).shiftLeft(32).longValue() + 18L;
        return SpellEffectsEnum.SKILL_TIMED_AFFINITY.createId(sid);
    }
    
    public static void poll(final Creature creature) {
        final AffinitiesTimed at = getTimedAffinitiesByPlayer(creature.getWurmId(), false);
        if (at != null) {
            at.pollTimeAffinities(creature);
        }
    }
    
    public static void sendTimedAffinitiesFor(final Creature creature) {
        final AffinitiesTimed at = getTimedAffinitiesByPlayer(creature.getWurmId(), false);
        if (at != null) {
            at.sendTimedAffinities(creature);
        }
    }
    
    public static SkillTemplate getTimedAffinitySkill(final Creature creature, final Item item) {
        if (!creature.isPlayer()) {
            return null;
        }
        final long playerId = creature.getWurmId();
        int ibonus = item.getBonus();
        if (ibonus == -1) {
            return null;
        }
        if (Server.getInstance().isPS() || creature.hasFlag(53)) {
            final Random affinityRandom = new Random();
            affinityRandom.setSeed(creature.getWurmId());
            ibonus += affinityRandom.nextInt(SkillSystem.getNumberOfSkillTemplates());
            ibonus %= SkillSystem.getNumberOfSkillTemplates();
        }
        else {
            ibonus += (int)(playerId & 0xFFL);
            ibonus += (int)(playerId >>> 8 & 0xFFL);
            ibonus += (int)(playerId >>> 16 & 0xFFL);
            ibonus += (int)(playerId >>> 24 & 0xFFL);
            ibonus += (int)(playerId >>> 32 & 0xFFL);
            ibonus += (int)(playerId >>> 40 & 0xFFL);
            ibonus += (int)(playerId >>> 48 & 0xFFL);
            ibonus += (int)(playerId >>> 56 & 0xFFL);
            ibonus = (ibonus & 0xFF) % SkillSystem.getNumberOfSkillTemplates();
        }
        return SkillSystem.getSkillTemplateByIndex(ibonus);
    }
    
    public static void addTimedAffinityFromBonus(final Creature creature, final int weight, final Item item) {
        if (!creature.isPlayer()) {
            return;
        }
        final int ibonus = item.getBonus();
        if (ibonus == -1) {
            return;
        }
        final long playerId = creature.getWurmId();
        final SkillTemplate skillTemplate = getTimedAffinitySkill(creature, item);
        if (skillTemplate == null) {
            return;
        }
        final int skillId = skillTemplate.getNumber();
        final float rarityMod = 1.0f + item.getRarity() * item.getRarity() * 0.1f;
        final int duration = (int)(weight * item.getCurrentQualityLevel() * rarityMod * item.getFoodComplexity());
        final AffinitiesTimed at = getTimedAffinitiesByPlayer(playerId, true);
        final boolean sendMessage = at.getLastSkillId() != skillId || WurmCalendar.getCurrentTime() > at.getLastTime() + 50L;
        at.add(skillId, duration);
        if (sendMessage) {
            creature.getCommunicator().sendNormalServerMessage("You suddenly realise that you have more of an insight about " + skillTemplate.getName().toLowerCase() + "!", (byte)2);
        }
        at.sendTimedAffinity(creature, skillTemplate.getNumber());
    }
    
    public static boolean isTimedAffinity(final long playerId, final int skill) {
        final AffinitiesTimed at = getTimedAffinitiesByPlayer(playerId, false);
        if (at != null) {
            final Long expires = at.getExpires(skill);
            if (expires == null) {
                at.remove(skill);
            }
            else {
                if (expires > WurmCalendar.getCurrentTime()) {
                    return true;
                }
                at.remove(skill);
            }
        }
        return false;
    }
    
    @Nullable
    public static final AffinitiesTimed getTimedAffinitiesByPlayer(final long playerId, final boolean autoCreate) {
        AffinitiesTimed at = AffinitiesTimed.playerTimedAffinities.get(playerId);
        if (at == null && autoCreate) {
            at = new AffinitiesTimed(playerId);
            AffinitiesTimed.playerTimedAffinities.put(playerId, at);
        }
        return at;
    }
    
    public static void deleteTimedAffinitiesForPlayer(final long playerId) {
        dbRemovePlayerTimedAffinities(playerId);
        AffinitiesTimed.playerTimedAffinities.remove(playerId);
    }
    
    public static void removeTimedAffinitiesForPlayer(final Creature creature) {
        final AffinitiesTimed at = getTimedAffinitiesByPlayer(creature.getWurmId(), false);
        if (at != null) {
            at.sendRemoveTimedAffinities(creature);
        }
    }
    
    public static final int loadAllPlayerTimedAffinities() {
        AffinitiesTimed.logger.info("Loading all Player Timed Affinities");
        final long start = System.nanoTime();
        int count = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM AFFINITIESTIMED");
            rs = ps.executeQuery();
            while (rs.next()) {
                ++count;
                final long playerId = rs.getLong("PLAYERID");
                final int skill = rs.getInt("SKILL");
                final long expires = rs.getLong("EXPIRATION");
                final AffinitiesTimed at = getTimedAffinitiesByPlayer(playerId, true);
                at.put(skill, expires);
            }
        }
        catch (SQLException sqex) {
            AffinitiesTimed.logger.log(Level.WARNING, "Failed to load all player timed affinities: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        AffinitiesTimed.logger.log(Level.INFO, "Number of player timed affinities=" + count + ".");
        AffinitiesTimed.logger.log(Level.INFO, "Player timed affinities loaded. That took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        return count;
    }
    
    private static void dbSaveTimedAffinity(final long playerId, final int skill, final long expires, final boolean update) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (update) {
                ps = dbcon.prepareStatement("UPDATE AFFINITIESTIMED SET EXPIRATION=? WHERE PLAYERID=? AND SKILL=?");
                ps.setLong(1, expires);
                ps.setLong(2, playerId);
                ps.setInt(3, skill);
                final int did = ps.executeUpdate();
                if (did > 0) {
                    return;
                }
                DbUtilities.closeDatabaseObjects(ps, rs);
            }
            ps = dbcon.prepareStatement("INSERT INTO AFFINITIESTIMED (PLAYERID,SKILL,EXPIRATION) VALUES (?,?,?)");
            ps.setLong(1, playerId);
            ps.setInt(2, skill);
            ps.setLong(3, expires);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            AffinitiesTimed.logger.log(Level.WARNING, "Failed to save player (" + playerId + ") skill (" + skill + ") timed affinities: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemovePlayerTimedAffinities(final long playerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM AFFINITIESTIMED WHERE PLAYERID=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            AffinitiesTimed.logger.log(Level.WARNING, "Failed to remove player (" + playerId + ") timed affiniies: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemoveTimedAffinity(final long playerId, final int skill) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM AFFINITIESTIMED WHERE PLAYERID=? AND Skill=?");
            ps.setLong(1, playerId);
            ps.setInt(2, skill);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            AffinitiesTimed.logger.log(Level.WARNING, "Failed to remove player (" + playerId + ")  skill (" + skill + ") timed affinity: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(AffinitiesTimed.class.getName());
        playerTimedAffinities = new ConcurrentHashMap<Long, AffinitiesTimed>();
    }
}
