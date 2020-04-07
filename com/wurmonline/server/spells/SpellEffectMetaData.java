// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public final class SpellEffectMetaData implements MiscConstants, CounterTypes
{
    private static final String CREATE_EFFECT = "INSERT INTO SPELLEFFECTS (WURMID, OWNER,TYPE,POWER,TIMELEFT) VALUES(?,?,?,?,?)";
    private static final String CREATE_ITEM_EFFECT = "INSERT INTO SPELLEFFECTS (WURMID, ITEMID,TYPE,POWER,TIMELEFT) VALUES(?,?,?,?,?)";
    private static final Logger logger;
    private final long id;
    private final float power;
    private final int timeleft;
    private final long owner;
    private final byte type;
    
    public SpellEffectMetaData(final long aWurmid, final long aOwner, final byte aType, final float aPower, final int aTimeleft, final boolean aAddToTables) {
        this.owner = aOwner;
        this.type = aType;
        this.power = aPower;
        this.timeleft = aTimeleft;
        this.id = aWurmid;
        if (aAddToTables && (WurmId.getType(aOwner) == 2 || WurmId.getType(aOwner) == 19 || WurmId.getType(aOwner) == 20)) {
            final SpellEffect sp = new SpellEffect(aWurmid, aOwner, aType, aPower, aTimeleft, (byte)9, (byte)0);
            ItemSpellEffects eff = ItemSpellEffects.getSpellEffects(sp.owner);
            if (eff == null) {
                eff = new ItemSpellEffects(sp.owner);
            }
            eff.addSpellEffect(sp);
        }
    }
    
    public void save() {
        if (WurmId.getType(this.owner) == 0) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("INSERT INTO SPELLEFFECTS (WURMID, OWNER,TYPE,POWER,TIMELEFT) VALUES(?,?,?,?,?)");
                ps.setLong(1, this.id);
                ps.setLong(2, this.owner);
                ps.setByte(3, this.type);
                ps.setFloat(4, this.power);
                ps.setInt(5, this.timeleft);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                SpellEffectMetaData.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else if (WurmId.getType(this.owner) == 2) {
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
                SpellEffectMetaData.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(SpellEffectMetaData.class.getName());
    }
}
