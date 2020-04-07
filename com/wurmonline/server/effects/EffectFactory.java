// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Players;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.shared.constants.EffectConstants;

public final class EffectFactory implements EffectConstants
{
    private final Map<Integer, Effect> effects;
    private static EffectFactory instance;
    private static final Logger logger;
    private static final String GETEFFECTS = "SELECT * FROM EFFECTS";
    
    private EffectFactory() {
        this.effects = new HashMap<Integer, Effect>();
    }
    
    public static EffectFactory getInstance() {
        if (EffectFactory.instance == null) {
            EffectFactory.instance = new EffectFactory();
        }
        return EffectFactory.instance;
    }
    
    private void addEffect(final Effect effect) {
        this.addEffect(effect, false);
    }
    
    private void addEffect(final Effect effect, final boolean temp) {
        if (!temp) {
            this.effects.put(effect.getId(), effect);
        }
        final int tileX = (int)effect.getPosX() >> 2;
        final int tileY = (int)effect.getPosY() >> 2;
        if (effect.isGlobal()) {
            Players.getInstance().sendGlobalNonPersistantEffect(effect.getOwner(), effect.getType(), tileX, tileY, effect.getPosZ());
        }
        else {
            try {
                final Zone zone = Zones.getZone(tileX, tileY, effect.isOnSurface());
                zone.addEffect(effect, temp);
            }
            catch (NoSuchZoneException nsz) {
                EffectFactory.logger.log(Level.WARNING, nsz.getMessage(), nsz);
            }
        }
    }
    
    public Effect createFire(final long id, final float posX, final float posY, final float posZ, final boolean surfaced) {
        final Effect toReturn = new DbEffect(id, (short)0, posX, posY, posZ, surfaced);
        this.addEffect(toReturn);
        return toReturn;
    }
    
    public Effect createProjectileLanding(final float posX, final float posY, final float posZ, final boolean surfaced) {
        final Effect toReturn = new TempEffect(-1L, (short)26, posX, posY, posZ, surfaced);
        this.addEffect(toReturn, true);
        return toReturn;
    }
    
    public Effect createGenericEffect(final long id, final String effectName, final float posX, final float posY, final float posZ, final boolean surfaced, final float timeout, final float rotationOffset) {
        final Effect toReturn = new TempEffect(id, (short)27, posX, posY, posZ, surfaced);
        toReturn.setEffectString(effectName);
        toReturn.setTimeout(timeout);
        toReturn.setRotationOffset(rotationOffset);
        this.addEffect(toReturn, id == -1L);
        return toReturn;
    }
    
    public Effect createGenericTempEffect(final String effectName, final float posX, final float posY, final float posZ, final boolean surfaced, final float timeout, final float rotationOffset) {
        return this.createGenericEffect(-1L, effectName, posX, posY, posZ, surfaced, timeout, rotationOffset);
    }
    
    public Effect createSpawnEff(final long id, final float posX, final float posY, final float posZ, final boolean surfaced) {
        final Effect toReturn = new DbEffect(id, (short)19, posX, posY, posZ, surfaced);
        this.addEffect(toReturn);
        return toReturn;
    }
    
    public Effect createChristmasEff(final long id, final float posX, final float posY, final float posZ, final boolean surfaced) {
        final Effect toReturn = new DbEffect(id, (short)4, posX, posY, posZ, surfaced);
        this.addEffect(toReturn);
        return toReturn;
    }
    
    public final void deleteEffByOwner(final long id) {
        for (final Effect eff : this.getAllEffects()) {
            if (eff.getOwner() == id) {
                this.deleteEffect(eff.getId());
                break;
            }
        }
    }
    
    public final Effect[] getAllEffects() {
        return this.effects.values().toArray(new Effect[this.effects.size()]);
    }
    
    public Effect deleteEffect(final int id) {
        final Effect toRemove = this.effects.get(id);
        this.effects.remove(id);
        if (toRemove != null) {
            if (toRemove.isGlobal()) {
                Players.getInstance().removeGlobalEffect(toRemove.getOwner());
            }
            else {
                final int tileX = (int)toRemove.getPosX() >> 2;
                final int tileY = (int)toRemove.getPosY() >> 2;
                try {
                    final Zone zone = Zones.getZone(tileX, tileY, toRemove.isOnSurface());
                    zone.removeEffect(toRemove);
                }
                catch (NoSuchZoneException nsz) {
                    EffectFactory.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                }
            }
            toRemove.delete();
        }
        return toRemove;
    }
    
    public void getEffectsFor(final Item item) {
        for (final Effect effect : this.effects.values()) {
            if (effect.getOwner() == item.getWurmId()) {
                effect.setPosX(item.getPosX());
                effect.setPosY(item.getPosY());
                effect.setSurfaced(item.isOnSurface());
                item.addEffect(effect);
                final int tileX = (int)effect.getPosX() >> 2;
                final int tileY = (int)effect.getPosY() >> 2;
                try {
                    final Zone zone = Zones.getZone(tileX, tileY, effect.isOnSurface());
                    zone.addEffect(effect, false);
                }
                catch (NoSuchZoneException nsz) {
                    EffectFactory.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                }
            }
        }
    }
    
    public Effect getEffectForOwner(final long id) {
        for (final Effect eff : this.getAllEffects()) {
            if (eff.getOwner() == id) {
                return eff;
            }
        }
        return null;
    }
    
    public void loadEffects() throws IOException {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM EFFECTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final float posX = rs.getFloat("POSX");
                final float posY = rs.getFloat("POSY");
                final float posZ = rs.getFloat("POSZ");
                final short type = rs.getShort("TYPE");
                final long owner = rs.getLong("OWNER");
                final long startTime = rs.getLong("STARTTIME");
                final int id = rs.getInt("ID");
                final DbEffect effect = new DbEffect(id, owner, type, posX, posY, posZ, startTime);
                this.effects.put(id, effect);
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            EffectFactory.logger.info("Loaded " + this.effects.size() + " effects from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    static {
        logger = Logger.getLogger(EffectFactory.class.getName());
    }
}
