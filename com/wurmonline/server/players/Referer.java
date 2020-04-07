// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.MiscConstants;

public final class Referer implements MiscConstants
{
    public static final byte R_TYPE_NOTHANDLED = 0;
    public static final byte R_TYPE_MONEY = 1;
    static final byte R_TYPE_TIME = 2;
    private final long wurmid;
    private final long referer;
    private boolean money;
    private boolean handled;
    
    Referer(final long aWurmid, final long aReferer) {
        this.money = false;
        this.handled = false;
        this.wurmid = aWurmid;
        this.referer = aReferer;
    }
    
    Referer(final long aWurmid, final long aReferer, final boolean aMoney, final boolean aHandled) {
        this.money = false;
        this.handled = false;
        this.wurmid = aWurmid;
        this.referer = aReferer;
        this.money = aMoney;
        this.handled = aHandled;
    }
    
    long getWurmid() {
        return this.wurmid;
    }
    
    long getReferer() {
        return this.referer;
    }
    
    boolean isMoney() {
        return this.money;
    }
    
    void setMoney(final boolean aMoney) {
        this.money = aMoney;
    }
    
    boolean isHandled() {
        return this.handled;
    }
    
    void setHandled(final boolean aHandled) {
        this.handled = aHandled;
    }
}
