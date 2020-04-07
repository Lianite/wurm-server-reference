// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.creatures.SpellEffects;
import com.wurmonline.server.creatures.Creature;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class SpellEffect implements MiscConstants
{
    private static final String DELETE_EFFECT = "DELETE FROM SPELLEFFECTS WHERE WURMID=?";
    private static final String DELETE_EFFECTS_FOR_PLAYER = "DELETE FROM SPELLEFFECTS WHERE OWNER=?";
    private static final String DELETE_EFFECTS_FOR_ITEM = "DELETE FROM SPELLEFFECTS WHERE ITEMID=?";
    private static final String UPDATE_POWER = "UPDATE SPELLEFFECTS SET POWER=? WHERE WURMID=?";
    private static final String UPDATE_TIMELEFT = "UPDATE SPELLEFFECTS SET TIMELEFT=? WHERE WURMID=?";
    private static final String GET_EFFECTS_FOR_PLAYER = "SELECT * FROM SPELLEFFECTS WHERE OWNER=?";
    private static final String CREATE_EFFECT = "INSERT INTO SPELLEFFECTS (WURMID, OWNER,TYPE,POWER,TIMELEFT,EFFTYPE,INFLUENCE) VALUES(?,?,?,?,?,?,?)";
    private static final String CREATE_ITEM_EFFECT = "INSERT INTO SPELLEFFECTS (WURMID, ITEMID,TYPE,POWER,TIMELEFT) VALUES(?,?,?,?,?)";
    private static final Logger logger;
    public final long id;
    public float power;
    public int timeleft;
    public final long owner;
    public final byte type;
    private final boolean isplayer;
    private final boolean isItem;
    private final byte effectType;
    private final byte influence;
    private boolean persistant;
    
    public SpellEffect(final long aOwner, final byte aType, final float aPower, final int aTimeleft) {
        this(aOwner, aType, aPower, aTimeleft, (byte)9, (byte)0, true);
    }
    
    public SpellEffect(final long aOwner, final byte aType, final float aPower, final int aTimeleft, final byte effType, final byte influenceType, final boolean persist) {
        this.power = 0.0f;
        this.timeleft = 0;
        this.persistant = true;
        this.owner = aOwner;
        this.type = aType;
        this.power = aPower;
        this.timeleft = aTimeleft;
        this.effectType = effType;
        this.influence = influenceType;
        this.persistant = persist;
        this.id = WurmId.getNextSpellId();
        if (WurmId.getType(aOwner) == 0) {
            this.isplayer = true;
            this.isItem = false;
        }
        else if (WurmId.getType(aOwner) == 2 || WurmId.getType(aOwner) == 19 || WurmId.getType(aOwner) == 20) {
            this.isplayer = false;
            this.isItem = true;
        }
        else {
            this.isplayer = false;
            this.isItem = false;
        }
        if ((this.isplayer || this.isItem) && this.persistant) {
            this.save();
        }
    }
    
    public SpellEffect(final long aId, final long aOwner, final byte aType, final float aPower, final int aTimeleft, final byte efftype, final byte influ) {
        this.power = 0.0f;
        this.timeleft = 0;
        this.persistant = true;
        this.id = aId;
        this.owner = aOwner;
        this.type = aType;
        this.power = aPower;
        this.timeleft = aTimeleft;
        this.effectType = efftype;
        this.influence = influ;
        this.persistant = true;
        if (WurmId.getType(aOwner) == 0) {
            this.isplayer = true;
            this.isItem = false;
        }
        else if (WurmId.getType(aOwner) == 2) {
            this.isplayer = false;
            this.isItem = true;
        }
        else {
            this.isplayer = false;
            this.isItem = false;
        }
    }
    
    public byte getSpellEffectType() {
        return this.effectType;
    }
    
    public byte getSpellInfluenceType() {
        return this.influence;
    }
    
    public final boolean isSmeared() {
        return this.type >= 77 && this.type <= 92;
    }
    
    public String getName() {
        if (this.type == 22 && this.getPower() > 70.0f) {
            return "Thornshell";
        }
        if (this.type == 73) {
            return "Newbie agg range buff";
        }
        if (this.type == 74) {
            return "Newbie food and drink buff";
        }
        if (this.type == 75) {
            return "Newbie healing buff";
        }
        if (this.type == 64) {
            return "Hunted";
        }
        if (this.type == 72) {
            return "Illusion";
        }
        if (this.type == 78) {
            return "potion of the ropemaker";
        }
        if (this.type == 79) {
            return "potion of mining";
        }
        if (this.type == 77) {
            return "oil of the weapon smith";
        }
        if (this.type == 80) {
            return "ointment of tailoring";
        }
        if (this.type == 81) {
            return "oil of the armour smith";
        }
        if (this.type == 82) {
            return "fletching potion";
        }
        if (this.type == 83) {
            return "oil of the blacksmith";
        }
        if (this.type == 84) {
            return "potion of leatherworking";
        }
        if (this.type == 85) {
            return "potion of shipbuilding";
        }
        if (this.type == 86) {
            return "ointment of stonecutting";
        }
        if (this.type == 87) {
            return "ointment of masonry";
        }
        if (this.type == 88) {
            return "potion of woodcutting";
        }
        if (this.type == 89) {
            return "potion of carpentry";
        }
        if (this.type == 99) {
            return "potion of butchery";
        }
        if (this.type == 94) {
            return "Incineration";
        }
        if (this.type == 98) {
            return "Shatter Protection";
        }
        if (this.type < -10L) {
            return RuneUtilities.getRuneName(this.type);
        }
        return Spells.getEnchantment(this.type).name;
    }
    
    public String getLongDesc() {
        if (this.type == 78) {
            return "improves rope making max ql";
        }
        if (this.type == 79) {
            return "improves mining max ql";
        }
        if (this.type == 77) {
            return "improves weapon smithing max ql";
        }
        if (this.type == 80) {
            return "improves tailoring max ql";
        }
        if (this.type == 81) {
            return "improves armour smithing max ql";
        }
        if (this.type == 82) {
            return "improves fletching max ql";
        }
        if (this.type == 83) {
            return "improves blacksmithing max ql";
        }
        if (this.type == 84) {
            return "improves leather working max ql";
        }
        if (this.type == 85) {
            return "improves ship building max ql";
        }
        if (this.type == 86) {
            return "improves stone cutting max ql";
        }
        if (this.type == 87) {
            return "improves masonry max ql";
        }
        if (this.type == 88) {
            return "improves wood cutting max ql";
        }
        if (this.type == 89) {
            return "improves carpentry max ql";
        }
        if (this.type == 99) {
            return "improves butchery product max ql";
        }
        if (this.type == 98) {
            return "protects against damage when spells are cast upon it";
        }
        if (this.type < -10L) {
            return "will " + RuneUtilities.getRuneLongDesc(this.type);
        }
        return Spells.getEnchantment(this.type).effectdesc;
    }
    
    private void save() {
        if (this.isplayer && this.persistant) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("INSERT INTO SPELLEFFECTS (WURMID, OWNER,TYPE,POWER,TIMELEFT,EFFTYPE,INFLUENCE) VALUES(?,?,?,?,?,?,?)");
                ps.setLong(1, this.id);
                ps.setLong(2, this.owner);
                ps.setByte(3, this.type);
                ps.setFloat(4, this.power);
                ps.setInt(5, this.timeleft);
                ps.setByte(6, this.effectType);
                ps.setByte(7, this.influence);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else if (this.isItem && this.persistant) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("INSERT INTO SPELLEFFECTS (WURMID, ITEMID,TYPE,POWER,TIMELEFT) VALUES(?,?,?,?,?)");
                ps.setLong(1, this.id);
                ps.setLong(2, this.owner);
                ps.setByte(3, this.type);
                ps.setFloat(4, this.power);
                ps.setInt(5, this.timeleft);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public float getPower() {
        return this.power;
    }
    
    public void setPower(final float newpower) {
        if (this.power != newpower) {
            this.power = newpower;
            if (this.persistant) {
                if (this.isplayer) {
                    Connection dbcon = null;
                    PreparedStatement ps = null;
                    try {
                        dbcon = DbConnector.getPlayerDbCon();
                        ps = dbcon.prepareStatement("UPDATE SPELLEFFECTS SET POWER=? WHERE WURMID=?");
                        ps.setFloat(1, this.power);
                        ps.setLong(2, this.id);
                        ps.executeUpdate();
                    }
                    catch (SQLException sqex) {
                        SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                    }
                    finally {
                        DbUtilities.closeDatabaseObjects(ps, null);
                        DbConnector.returnConnection(dbcon);
                    }
                }
                else if (this.isItem) {
                    Connection dbcon = null;
                    PreparedStatement ps = null;
                    try {
                        dbcon = DbConnector.getItemDbCon();
                        ps = dbcon.prepareStatement("UPDATE SPELLEFFECTS SET POWER=? WHERE WURMID=?");
                        ps.setFloat(1, this.power);
                        ps.setLong(2, this.id);
                        ps.executeUpdate();
                    }
                    catch (SQLException sqex) {
                        SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                    }
                    finally {
                        DbUtilities.closeDatabaseObjects(ps, null);
                        DbConnector.returnConnection(dbcon);
                    }
                }
            }
        }
    }
    
    public void improvePower(final Creature performer, final float newpower) {
        final float mod = 5.0f * (1.0f - this.power / (performer.hasFlag(82) ? 105.0f : 100.0f));
        this.setPower(mod + newpower);
    }
    
    public void setTimeleft(final int newTimeleft) {
        if (this.timeleft != newTimeleft) {
            this.timeleft = newTimeleft;
            if (this.isplayer && this.persistant) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getPlayerDbCon();
                    ps = dbcon.prepareStatement("UPDATE SPELLEFFECTS SET TIMELEFT=? WHERE WURMID=?");
                    ps.setInt(1, this.timeleft);
                    ps.setLong(2, this.id);
                    ps.executeUpdate();
                }
                catch (SQLException sqex) {
                    SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
    }
    
    private void saveTimeleft() {
        if (this.isplayer && this.persistant) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("UPDATE SPELLEFFECTS SET TIMELEFT=? WHERE WURMID=?");
                ps.setInt(1, this.timeleft);
                ps.setLong(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void delete() {
        if (this.persistant) {
            if (this.isplayer) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getPlayerDbCon();
                    ps = dbcon.prepareStatement("DELETE FROM SPELLEFFECTS WHERE WURMID=?");
                    ps.setLong(1, this.id);
                    ps.executeUpdate();
                }
                catch (SQLException sqex) {
                    SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
            else if (this.isItem) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getItemDbCon();
                    ps = dbcon.prepareStatement("DELETE FROM SPELLEFFECTS WHERE WURMID=?");
                    ps.setLong(1, this.id);
                    ps.executeUpdate();
                }
                catch (SQLException sqex) {
                    SpellEffect.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
    }
    
    public boolean poll(final SpellEffects effects) {
        --this.timeleft;
        if (this.timeleft <= 0) {
            effects.removeSpellEffect(this);
            return true;
        }
        if (this.timeleft % 60 == 0) {
            this.saveTimeleft();
        }
        return false;
    }
    
    public static final SpellEffect[] loadEffectsForPlayer(final long wurmid) {
        SpellEffect[] spells = new SpellEffect[0];
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SPELLEFFECTS WHERE OWNER=?");
            ps.setLong(1, wurmid);
            rs = ps.executeQuery();
            final Set<SpellEffect> spset = new HashSet<SpellEffect>();
            while (rs.next()) {
                final SpellEffect sp = new SpellEffect(rs.getLong("WURMID"), rs.getLong("OWNER"), rs.getByte("TYPE"), rs.getFloat("POWER"), rs.getInt("TIMELEFT"), rs.getByte("EFFTYPE"), rs.getByte("INFLUENCE"));
                spset.add(sp);
            }
            if (spset.size() > 0) {
                spells = spset.toArray(new SpellEffect[spset.size()]);
            }
        }
        catch (SQLException sqx) {
            SpellEffect.logger.log(Level.WARNING, wurmid + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return spells;
    }
    
    public static final void deleteEffectsForPlayer(final long playerid) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (SpellEffect.logger.isLoggable(Level.FINEST)) {
                SpellEffect.logger.finest("Deleting Effects for Player ID: " + playerid);
            }
            ps = dbcon.prepareStatement("DELETE FROM SPELLEFFECTS WHERE OWNER=?");
            ps.setLong(1, playerid);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            SpellEffect.logger.log(Level.WARNING, "Problem deleting effects for playerid: " + playerid + " due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteEffectsForItem(final long itemid) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM SPELLEFFECTS WHERE ITEMID=?");
            ps.setLong(1, itemid);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            SpellEffect.logger.log(Level.WARNING, "Problem deleting effects for itemid: " + itemid + " due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(SpellEffect.class.getName());
    }
}
