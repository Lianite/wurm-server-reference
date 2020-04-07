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
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

public class Triggers2Effects
{
    private static Logger logger;
    private static final String LOAD_ALL_LINKS = "SELECT * FROM TRIGGERS2EFFECTS";
    private static final String CREATE_LINK = "INSERT INTO TRIGGERS2EFFECTS (TRIGGERID, EFFECTID) VALUES(?,?)";
    private static final String DELETE_LINK = "DELETE FROM TRIGGERS2EFFECTS WHERE TRIGGERID=? AND EFFECTID=?";
    private static final String DELETE_TRIGGER = "DELETE FROM TRIGGERS2EFFECTS WHERE TRIGGERID=?";
    private static final String DELETE_EFFECT = "DELETE FROM TRIGGERS2EFFECTS WHERE EFFECTID=?";
    private static final Map<Integer, HashSet<Integer>> triggers2Effects;
    private static final Map<Integer, HashSet<Integer>> effects2Triggers;
    
    public static TriggerEffect[] getEffectsForTrigger(final int triggerId, final boolean incInactive) {
        final Set<TriggerEffect> effs = new HashSet<TriggerEffect>();
        final HashSet<Integer> effects = Triggers2Effects.triggers2Effects.get(triggerId);
        if (effects != null) {
            for (final Integer effectId : effects) {
                final TriggerEffect eff = TriggerEffects.getTriggerEffect(effectId);
                if (eff != null && (incInactive || (!incInactive && !eff.isInactive()))) {
                    effs.add(eff);
                }
            }
        }
        return effs.toArray(new TriggerEffect[effs.size()]);
    }
    
    public static MissionTrigger[] getTriggersForEffect(final int effectId, final boolean incInactive) {
        final Set<MissionTrigger> trgs = new HashSet<MissionTrigger>();
        final HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.get(effectId);
        if (triggers != null) {
            for (final Integer triggerId : triggers) {
                final MissionTrigger trg = MissionTriggers.getTriggerWithId(triggerId);
                if (trg != null && (incInactive || (!incInactive && !trg.isInactive()))) {
                    trgs.add(trg);
                }
            }
        }
        return trgs.toArray(new MissionTrigger[trgs.size()]);
    }
    
    public static boolean hasLink(final int triggerId, final int effectId) {
        final HashSet<Integer> effects = Triggers2Effects.triggers2Effects.get(triggerId);
        return effects != null && effects.contains(effectId);
    }
    
    public static boolean hasEffect(final int triggerId) {
        final HashSet<Integer> effects = Triggers2Effects.triggers2Effects.get(triggerId);
        return effects != null && !effects.isEmpty();
    }
    
    public static boolean hasTrigger(final int effectId) {
        final HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.get(effectId);
        return triggers != null && !triggers.isEmpty();
    }
    
    public static void addLink(final int triggerId, final int effectId, final boolean loading) {
        if (triggerId <= 0 || effectId <= 0) {
            return;
        }
        HashSet<Integer> effects = Triggers2Effects.triggers2Effects.get(triggerId);
        if (effects == null) {
            effects = new HashSet<Integer>();
        }
        final boolean effAdded = effects.add(effectId);
        if (!effects.isEmpty()) {
            Triggers2Effects.triggers2Effects.put(triggerId, effects);
        }
        HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.get(effectId);
        if (triggers == null) {
            triggers = new HashSet<Integer>();
        }
        final boolean trgAdded = triggers.add(triggerId);
        if (!triggers.isEmpty()) {
            Triggers2Effects.effects2Triggers.put(effectId, triggers);
        }
        if (!loading && (effAdded || trgAdded)) {
            dbCreateLink(triggerId, effectId);
        }
    }
    
    public static void deleteLink(final int triggerId, final int effectId) {
        final HashSet<Integer> effects = Triggers2Effects.triggers2Effects.remove(triggerId);
        if (effects != null) {
            effects.remove(effectId);
            if (!effects.isEmpty()) {
                Triggers2Effects.triggers2Effects.put(triggerId, effects);
            }
        }
        final HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.remove(effectId);
        if (triggers != null) {
            triggers.remove(triggerId);
            if (!triggers.isEmpty()) {
                Triggers2Effects.effects2Triggers.put(effectId, triggers);
            }
        }
        dbDeleteLink(triggerId, effectId);
    }
    
    public static void deleteTrigger(final int triggerId) {
        final HashSet<Integer> effects = Triggers2Effects.triggers2Effects.remove(triggerId);
        if (effects != null) {
            for (final Integer effectId : effects) {
                final HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.remove(effectId);
                if (triggers != null) {
                    triggers.remove(triggerId);
                    if (triggers.isEmpty()) {
                        continue;
                    }
                    Triggers2Effects.effects2Triggers.put((int)effectId, triggers);
                }
            }
        }
        dbDeleteTrigger(triggerId);
    }
    
    public static void deleteEffect(final int effectId) {
        final HashSet<Integer> triggers = Triggers2Effects.effects2Triggers.remove(effectId);
        if (triggers != null) {
            for (final Integer triggerId : triggers) {
                final HashSet<Integer> effects = Triggers2Effects.effects2Triggers.remove(triggerId);
                if (effects != null) {
                    effects.remove(effectId);
                    if (effects.isEmpty()) {
                        continue;
                    }
                    Triggers2Effects.effects2Triggers.put(effectId, triggers);
                }
            }
        }
        dbDeleteEffect(effectId);
    }
    
    private static void dbCreateLink(final int triggerId, final int effectId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO TRIGGERS2EFFECTS (TRIGGERID, EFFECTID) VALUES(?,?)");
            ps.setInt(1, triggerId);
            ps.setInt(2, effectId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Triggers2Effects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbDeleteLink(final int triggerId, final int effectId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TRIGGERS2EFFECTS WHERE TRIGGERID=? AND EFFECTID=?");
            ps.setInt(1, triggerId);
            ps.setInt(2, effectId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Triggers2Effects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbDeleteTrigger(final int triggerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TRIGGERS2EFFECTS WHERE TRIGGERID=?");
            ps.setInt(1, triggerId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Triggers2Effects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbDeleteEffect(final int effectId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TRIGGERS2EFFECTS WHERE EFFECTID=?");
            ps.setInt(1, effectId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Triggers2Effects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbLoadAllTriggers2Effects() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM TRIGGERS2EFFECTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int triggerId = rs.getInt("TRIGGERID");
                final int effectId = rs.getInt("EFFECTID");
                addLink(triggerId, effectId, true);
            }
        }
        catch (SQLException sqx) {
            Triggers2Effects.logger.log(Level.WARNING, sqx.getMessage());
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        Triggers2Effects.logger = Logger.getLogger(Triggers2Effects.class.getName());
        triggers2Effects = new ConcurrentHashMap<Integer, HashSet<Integer>>();
        effects2Triggers = new ConcurrentHashMap<Integer, HashSet<Integer>>();
        try {
            dbLoadAllTriggers2Effects();
        }
        catch (Exception ex) {
            Triggers2Effects.logger.log(Level.WARNING, "Problems loading all Triggers 2 Effects", ex);
        }
    }
}
