// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.Random;
import com.wurmonline.server.Server;
import com.wurmonline.shared.constants.EffectConstants;

public final class LongPosition implements EffectConstants
{
    private final long id;
    private final int tilex;
    private final int tiley;
    private final short effectType;
    
    LongPosition(final long _id, final int _tilex, final int _tiley) {
        this.id = _id;
        this.tilex = _tilex;
        this.tiley = _tiley;
        this.effectType = getRandomEffectType(Server.rand);
    }
    
    static short getRandomEffectType(final Random randomSource) {
        return (short)(5 + randomSource.nextInt(5));
    }
    
    long getId() {
        return this.id;
    }
    
    public int getTilex() {
        return this.tilex;
    }
    
    public int getTiley() {
        return this.tiley;
    }
    
    short getEffectType() {
        return this.effectType;
    }
}
