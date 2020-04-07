// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.HashMap;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public final class AreaSpellEffect implements MiscConstants
{
    private final int tilex;
    private final int tiley;
    private final int layer;
    private final byte type;
    private final long expireTime;
    private final long creator;
    private final float power;
    private final long id;
    private final int floorLevel;
    private final int heightOffset;
    private static final Map<Long, AreaSpellEffect> LAYER_0;
    private static final Map<Long, AreaSpellEffect> LAYER_MINI;
    
    public AreaSpellEffect(final long _creator, final int _tilex, final int _tiley, final int _layer, final byte _type, final long _expireTime, final float _power, final int _floorLevel, final int _heightOffset, final boolean loop) {
        this.tilex = _tilex;
        this.tiley = _tiley;
        this.layer = _layer;
        this.type = _type;
        this.expireTime = _expireTime;
        this.power = _power;
        this.floorLevel = _floorLevel;
        this.id = calculateId(this.tilex, this.tiley);
        this.creator = _creator;
        this.heightOffset = _heightOffset;
        addToMap(this);
        addToWorld(this, loop);
    }
    
    public int getFloorLevel() {
        return this.floorLevel;
    }
    
    int getTilex() {
        return this.tilex;
    }
    
    int getTiley() {
        return this.tiley;
    }
    
    int getHeightOffset() {
        return this.heightOffset;
    }
    
    int getLayer() {
        return this.layer;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public long getCreator() {
        return this.creator;
    }
    
    public float getPower() {
        return this.power;
    }
    
    public long getExpireTime() {
        return this.expireTime;
    }
    
    public long getId() {
        return this.id;
    }
    
    private static void addToMap(final AreaSpellEffect sp) {
        switch (sp.layer) {
            case 0: {
                AreaSpellEffect.LAYER_0.put(sp.id, sp);
                break;
            }
            case -1: {
                AreaSpellEffect.LAYER_MINI.put(sp.id, sp);
                break;
            }
            default: {
                AreaSpellEffect.LAYER_0.put(sp.id, sp);
                break;
            }
        }
    }
    
    public static void addToWorld(final AreaSpellEffect sp, final boolean loop) {
        final VolaTile vt = Zones.getOrCreateTile(sp.tilex, sp.tiley, sp.layer >= 0);
        vt.sendAddTileEffect(sp, loop);
    }
    
    private static Map<Long, AreaSpellEffect> getMap(final int layer) {
        switch (layer) {
            case 0: {
                return AreaSpellEffect.LAYER_0;
            }
            case -1: {
                return AreaSpellEffect.LAYER_MINI;
            }
            default: {
                return AreaSpellEffect.LAYER_0;
            }
        }
    }
    
    private static long calculateId(final int tileX, final int tileY) {
        return (tileX << 16) + tileY;
    }
    
    public static void pollEffects() {
        pollEffects(AreaSpellEffect.LAYER_0, 0);
        pollEffects(AreaSpellEffect.LAYER_MINI, -1);
    }
    
    private static void pollEffects(final Map<Long, AreaSpellEffect> map, final int layer) {
        final AreaSpellEffect[] eff = map.values().toArray(new AreaSpellEffect[map.size()]);
        final long now = System.currentTimeMillis();
        for (int as = 0; as < eff.length; ++as) {
            if (eff[as].expireTime < now) {
                map.remove(eff[as].getId());
                final VolaTile vt = Zones.getOrCreateTile(eff[as].tilex, eff[as].tiley, layer >= 0);
                vt.sendRemoveTileEffect(eff[as]);
            }
        }
    }
    
    public static void removeAreaEffect(final int tilex, final int tiley, final int layer) {
        final Map<Long, AreaSpellEffect> map = getMap(layer);
        final AreaSpellEffect sp = map.remove(calculateId(tilex, tiley));
        if (sp != null) {
            final VolaTile vt = Zones.getOrCreateTile(sp.tilex, sp.tiley, layer >= 0);
            vt.sendRemoveTileEffect(sp);
        }
    }
    
    public static AreaSpellEffect getEffect(final int tilex, final int tiley, final int layer) {
        final Map<Long, AreaSpellEffect> map = getMap(layer);
        if (map != null) {
            return map.get(calculateId(tilex, tiley));
        }
        return null;
    }
    
    static {
        LAYER_0 = new HashMap<Long, AreaSpellEffect>();
        LAYER_MINI = new HashMap<Long, AreaSpellEffect>();
    }
}
