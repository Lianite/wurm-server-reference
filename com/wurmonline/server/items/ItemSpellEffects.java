// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.HashMap;
import java.util.logging.Logger;
import com.wurmonline.server.spells.SpellEffect;
import java.util.Map;

public final class ItemSpellEffects
{
    private static final String GET_ALL_ITEMSPELLEFFECTS = "SELECT * FROM SPELLEFFECTS";
    private final Map<Byte, SpellEffect> spellEffects;
    private static final Logger logger;
    private static final Map<Long, ItemSpellEffects> itemSpellEffects;
    
    public ItemSpellEffects(final long _itemId) {
        this.spellEffects = new HashMap<Byte, SpellEffect>();
        ItemSpellEffects.itemSpellEffects.put(new Long(_itemId), this);
    }
    
    public void addSpellEffect(final SpellEffect effect) {
        final SpellEffect old = this.getSpellEffect(effect.type);
        if (old != null && old.power > effect.power) {
            effect.delete();
            return;
        }
        if (old != null) {
            old.delete();
        }
        this.spellEffects.put(effect.type, effect);
    }
    
    public byte getRandomRuneEffect() {
        for (int i = -128; i <= -51; ++i) {
            if (this.spellEffects.containsKey((byte)i)) {
                return (byte)i;
            }
        }
        return -10;
    }
    
    public float getRuneEffect(final RuneUtilities.ModifierEffect effect) {
        float toReturn = 1.0f;
        for (int i = -128; i <= -51; ++i) {
            if (this.spellEffects.containsKey((byte)i)) {
                toReturn += RuneUtilities.getModifier((byte)i, effect);
            }
        }
        return toReturn;
    }
    
    public int getNumberOfRuneEffects() {
        int toReturn = 0;
        for (int i = -128; i <= -51; ++i) {
            if (this.spellEffects.containsKey((byte)i)) {
                ++toReturn;
            }
        }
        return toReturn;
    }
    
    public SpellEffect getSpellEffect(final byte type) {
        if (this.spellEffects.containsKey(type)) {
            return this.spellEffects.get(type);
        }
        return null;
    }
    
    public SpellEffect[] getEffects() {
        return this.spellEffects.values().toArray(new SpellEffect[this.spellEffects.size()]);
    }
    
    public SpellEffect removeSpellEffect(final byte number) {
        final SpellEffect old = this.getSpellEffect(number);
        if (old != null) {
            old.delete();
            this.spellEffects.remove(number);
        }
        return old;
    }
    
    public void destroy() {
        final SpellEffect[] effects = this.getEffects();
        for (int x = 0; x < effects.length; ++x) {
            effects[x].delete();
        }
        this.spellEffects.clear();
    }
    
    public void clear() {
        this.spellEffects.clear();
    }
    
    public static ItemSpellEffects getSpellEffects(final long itemid) {
        return ItemSpellEffects.itemSpellEffects.get(new Long(itemid));
    }
    
    public static void loadSpellEffectsForItems() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SPELLEFFECTS");
            rs = ps.executeQuery();
            int numEffects = 0;
            while (rs.next()) {
                final SpellEffect sp = new SpellEffect(rs.getLong("WURMID"), rs.getLong("ITEMID"), rs.getByte("TYPE"), rs.getFloat("POWER"), rs.getInt("TIMELEFT"), (byte)9, (byte)0);
                final Long id = new Long(sp.owner);
                ItemSpellEffects eff = ItemSpellEffects.itemSpellEffects.get(id);
                if (eff == null) {
                    eff = new ItemSpellEffects(sp.owner);
                }
                eff.addSpellEffect(sp);
                ++numEffects;
            }
            ItemSpellEffects.logger.log(Level.INFO, "Loaded " + numEffects + " Spell Effects For Items, that took " + (System.nanoTime() - start) / 1000000.0f + " ms");
        }
        catch (SQLException sqx) {
            ItemSpellEffects.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(ItemSpellEffects.class.getName());
        itemSpellEffects = new HashMap<Long, ItemSpellEffects>();
    }
}
