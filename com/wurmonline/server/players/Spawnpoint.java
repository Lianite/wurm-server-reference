// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.MiscConstants;

public final class Spawnpoint implements MiscConstants
{
    public final String description;
    public final short tilex;
    public final short tiley;
    public final boolean surfaced;
    public final byte number;
    public final byte kingdom;
    public final String name;
    
    public Spawnpoint(final byte num, final String desc, final short x, final short y, final boolean surf) {
        this("sp1", num, desc, x, y, surf, (byte)0);
    }
    
    public Spawnpoint(final byte num, final String desc, final short x, final short y, final boolean surf, final byte kingdomId) {
        this("sp1", num, desc, x, y, surf, kingdomId);
    }
    
    public Spawnpoint(final String initialName, final byte num, final String desc, final short x, final short y, final boolean surf, final byte kingdomId) {
        this.number = num;
        this.description = desc;
        this.tilex = x;
        this.tiley = y;
        this.surfaced = surf;
        this.name = initialName;
        this.kingdom = kingdomId;
    }
}
