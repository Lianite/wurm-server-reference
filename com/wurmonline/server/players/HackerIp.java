// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

public final class HackerIp
{
    public int timesFailed;
    public long mayTryAgain;
    public String name;
    
    public HackerIp(final int _timesFailed, final long _mayTryAgain, final String _name) {
        this.timesFailed = 0;
        this.mayTryAgain = 0L;
        this.timesFailed = _timesFailed;
        this.mayTryAgain = _mayTryAgain;
        this.name = _name;
    }
    
    public int getTimesFailed() {
        return this.timesFailed;
    }
    
    public void incrementTimesFailed() {
        ++this.timesFailed;
    }
    
    public long getMayTryAgain() {
        return this.mayTryAgain;
    }
    
    public void setMayTryAgain(final long aMayTryAgain) {
        this.mayTryAgain = aMayTryAgain;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String aName) {
        this.name = aName;
    }
}
