// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.questions.MissionManager;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;

public final class TriggerEffects implements CounterTypes
{
    private static Logger logger;
    public static final String LOADALLEFFECTS = "SELECT * FROM TRIGGEREFFECTS";
    private static final Map<Integer, TriggerEffect> effects;
    public static final int SHOW_ALL = 0;
    public static final int SHOW_LINKED = 1;
    public static final int SHOW_UNLINKED = 2;
    
    public static void addTriggerEffect(final TriggerEffect effect) {
        TriggerEffects.effects.put(effect.getId(), effect);
    }
    
    public static int getNumEffects() {
        return TriggerEffects.effects.size();
    }
    
    public static TriggerEffect[] getAllEffects() {
        return TriggerEffects.effects.values().toArray(new TriggerEffect[TriggerEffects.effects.size()]);
    }
    
    public static TriggerEffect[] getEffectsForTrigger(final int triggerId, final boolean incInactive) {
        return Triggers2Effects.getEffectsForTrigger(triggerId, incInactive);
    }
    
    public static TriggerEffect[] getEffectsForMission(final int missionId) {
        final Set<TriggerEffect> effs = new HashSet<TriggerEffect>();
        for (final TriggerEffect effect : TriggerEffects.effects.values()) {
            if (effect.getMissionId() == missionId) {
                effs.add(effect);
            }
        }
        return effs.toArray(new TriggerEffect[effs.size()]);
    }
    
    public static TriggerEffect[] getFilteredEffects(final MissionTrigger[] trigs, final Creature creature, final int linked, final boolean incInactive, final boolean dontListMine, final boolean listMineOnly, final long listForUser, final boolean showAll) {
        final Set<TriggerEffect> effs = new HashSet<TriggerEffect>();
        for (final TriggerEffect effect : TriggerEffects.effects.values()) {
            boolean found = showAll;
            if (!found) {
                for (final MissionTrigger trig : trigs) {
                    if (Triggers2Effects.hasLink(trig.getId(), effect.getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (found && canShow(effect, creature, linked, incInactive, dontListMine, listMineOnly, listForUser)) {
                effs.add(effect);
            }
        }
        return effs.toArray(new TriggerEffect[effs.size()]);
    }
    
    public static TriggerEffect[] getFilteredEffects(final MissionTrigger[] trigs, final Creature creature, final int linked, final boolean incInactive, final boolean dontListMine, final boolean listMineOnly, final long listForUser, final int missionId) {
        final Set<TriggerEffect> effs = new HashSet<TriggerEffect>();
        for (final TriggerEffect effect : TriggerEffects.effects.values()) {
            if (effect.getMissionId() == missionId) {
                boolean found = false;
                for (final MissionTrigger trig : trigs) {
                    if (Triggers2Effects.hasLink(trig.getId(), effect.getId())) {
                        found = true;
                        break;
                    }
                }
                if (found || !canShow(effect, creature, linked, incInactive, dontListMine, listMineOnly, listForUser)) {
                    continue;
                }
                effs.add(effect);
            }
        }
        return effs.toArray(new TriggerEffect[effs.size()]);
    }
    
    public static TriggerEffect[] getFilteredEffects(final Creature creature, final int linked, final boolean incInactive, final boolean dontListMine, final boolean listMineOnly, final long listForUser) {
        final Set<TriggerEffect> effs = new HashSet<TriggerEffect>();
        for (final TriggerEffect effect : TriggerEffects.effects.values()) {
            if (canShow(effect, creature, linked, incInactive, dontListMine, listMineOnly, listForUser)) {
                effs.add(effect);
            }
        }
        return effs.toArray(new TriggerEffect[effs.size()]);
    }
    
    private static boolean canShow(final TriggerEffect effect, final Creature creature, final int linked, final boolean incInactive, final boolean dontListMine, final boolean listMineOnly, final long listForUser) {
        final boolean own = effect.getOwnerId() == creature.getWurmId();
        boolean show = creature.getPower() > 0 || own;
        final boolean userMatch = effect.getOwnerId() == listForUser;
        if (own) {
            if (dontListMine) {
                show = false;
            }
        }
        else if (listMineOnly) {
            show = false;
            if (listForUser != -10L && userMatch) {
                show = true;
            }
        }
        else if (listForUser != -10L) {
            show = false;
            if (userMatch) {
                show = true;
            }
        }
        if (effect.getCreatorType() == 2 && creature.getPower() < MissionManager.CAN_SEE_EPIC_MISSIONS) {
            show = false;
        }
        if (show) {
            switch (linked) {
                case 1: {
                    show = (effect.getMissionId() != 0);
                    break;
                }
                case 2: {
                    show = (effect.getMissionId() == 0);
                    break;
                }
            }
        }
        return show;
    }
    
    protected static boolean removeEffect(final int id) {
        return TriggerEffects.effects.remove(id) != null;
    }
    
    public static TriggerEffect getTriggerEffect(final int id) {
        return TriggerEffects.effects.get(id);
    }
    
    public static void destroyEffectsForTrigger(final int triggerId) {
        final TriggerEffect[] effectsForTrigger;
        final TriggerEffect[] tes = effectsForTrigger = Triggers2Effects.getEffectsForTrigger(triggerId, true);
        for (final TriggerEffect mt : effectsForTrigger) {
            final MissionTrigger trig = MissionTriggers.getTriggerWithId(triggerId);
            if (trig != null && (mt.destroysTarget() || WurmId.getType(trig.getTarget()) == 1) && mt.getCreatorType() != 3) {
                mt.destroyTarget(trig.getTarget());
            }
            if (tes.length == 1) {
                removeEffect(mt.getId());
                mt.destroy();
            }
        }
    }
    
    private static void loadAllTriggerEffects() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM TRIGGEREFFECTS");
            rs = ps.executeQuery();
            int mid = -10;
            while (rs.next()) {
                mid = rs.getInt("ID");
                final TriggerEffect m = new TriggerEffect();
                m.setId(mid);
                m.setName(rs.getString("NAME"));
                m.setDescription(rs.getString("DESCRIPTION"));
                m.setRewardItem(rs.getInt("REWARDITEM"));
                m.setRewardNumbers(rs.getInt("REWARDITEMNUMBERS"));
                m.setRewardQl(rs.getInt("REWARDQUALITY"));
                m.setRewardByteValue(rs.getByte("REWARDBYTE"));
                m.setExistingItemReward(rs.getLong("EXISTINGREWARDITEMID"));
                m.setRewardTargetContainerId(rs.getLong("REWARDTARGETCONTAINERID"));
                m.setRewardSkillNum(rs.getInt("REWARDSKILLNUM"));
                m.setRewardSkillVal(rs.getFloat("REWARDSKILLVAL"));
                m.setSpecialEffect(rs.getInt("SPECIALEFFECTID"));
                final int triggerId = rs.getInt("TRIGGERID");
                if (triggerId > 0) {
                    Triggers2Effects.addLink(triggerId, mid, false);
                }
                m.setSoundName(rs.getString("SOUND"));
                m.setTextDisplayed(rs.getString("TEXT"));
                m.setTopText(rs.getString("TOP"));
                m.setMission(rs.getInt("MISSION"));
                m.setMissionStateChange(rs.getFloat("MISSIONSTATECHANGE"));
                m.setInactive(rs.getBoolean("INACTIVE"));
                m.setLastModifierName(rs.getString("LASTMODIFIER"));
                m.setDestroysTarget(rs.getBoolean("DESTROYTARGET"));
                m.setCreatorName(rs.getString("CREATOR"));
                m.setCreatedDate(rs.getString("CREATEDDATE"));
                m.setLastModifierName(rs.getString("LASTMODIFIER"));
                Timestamp st = new Timestamp(System.currentTimeMillis());
                try {
                    st = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("LASTMODIFIEDDATE")).getTime());
                }
                catch (Exception ex) {
                    TriggerEffects.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                m.setLastModifiedDate(st);
                m.setCreatorType(rs.getByte("CREATORTYPE"));
                m.setOwnerId(rs.getLong("CREATORID"));
                m.setItemMaterial(rs.getByte("ITEMMATERIAL"));
                m.setNewbieItem(rs.getBoolean("NEWBIE"));
                m.setModifyTileX(rs.getInt("MODIFYTILEX"));
                m.setModifyTileY(rs.getInt("MODIFYTILEY"));
                m.setNewTileType(rs.getInt("NEWTILETYPE"));
                m.setNewTileData(rs.getByte("NEWTILEDATA"));
                m.setSpawnTileX(rs.getInt("SPAWNTILEX"));
                m.setSpawnTileY(rs.getInt("SPAWNTILEY"));
                m.setCreatureSpawn(rs.getInt("CREATURESPAWN"));
                m.setCreatureAge(rs.getInt("CREATUREAGE"));
                m.setCreatureType(rs.getByte("CREATURE_TYPE"));
                m.setCreatureName(rs.getString("CREATURE_NAME"));
                m.setTeleportX(rs.getInt("TELEPORTX"));
                m.setTeleportY(rs.getInt("TELEPORTY"));
                m.setTeleportLayer(rs.getInt("TELEPORTLAYER"));
                m.setMissionToActivate(rs.getInt("MISSIONACTIVATED"));
                m.setMissionToDeActivate(rs.getInt("MISSIONDEACTIVATED"));
                m.setWindowSizeX(rs.getInt("WSZX"));
                m.setWindowSizeY(rs.getInt("WSZY"));
                m.setStartSkillgain(rs.getBoolean("STARTSKILLGAIN"));
                m.setStopSkillgain(rs.getBoolean("STOPSKILLGAIN"));
                m.setDestroyInventory(rs.getBoolean("DESTROYITEMS"));
                addTriggerEffect(m);
            }
        }
        catch (SQLException sqx) {
            TriggerEffects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        TriggerEffects.logger = Logger.getLogger(TriggerEffects.class.getName());
        effects = new ConcurrentHashMap<Integer, TriggerEffect>();
        try {
            loadAllTriggerEffects();
        }
        catch (Exception ex) {
            TriggerEffects.logger.log(Level.WARNING, "Problems loading all Trigger Effects", ex);
        }
    }
}
