// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

public abstract class Idea
{
    private int priority;
    static final int MOVEMENTPRIO = 0;
    static final int PURCHASEPRIO = 1;
    static final int BUILDPRIO = 0;
    static final int ATTACKPRIO = 5;
    static final int DEFENDPRIO = 7;
    static final int RETREATPRIO = 8;
    static final int SCOUTPRIO = 3;
    
    public Idea() {
        this.priority = 0;
    }
    
    public abstract boolean resolve();
    
    public final int getPriority() {
        return this.priority;
    }
    
    public final void setPriority(final int aPriority) {
        this.priority = aPriority;
    }
}
