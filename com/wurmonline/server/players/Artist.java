// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

public final class Artist
{
    private final long wurmid;
    private final boolean sound;
    private final boolean graphics;
    
    public Artist(final long wurmId, final boolean isSound, final boolean isGraphics) {
        this.wurmid = wurmId;
        this.sound = isSound;
        this.graphics = isGraphics;
    }
    
    public long getWurmid() {
        return this.wurmid;
    }
    
    public boolean isSound() {
        return this.sound;
    }
    
    public boolean isGraphics() {
        return this.graphics;
    }
}
