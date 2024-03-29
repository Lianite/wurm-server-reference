// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

public class ItemLastOwnerDatabaseUpdatable implements WurmDbUpdatable
{
    private final long id;
    private final long owner;
    private final String updateStatement;
    
    public ItemLastOwnerDatabaseUpdatable(final long aId, final long aOwner, final String aUpdateStatement) {
        this.id = aId;
        this.owner = aOwner;
        this.updateStatement = aUpdateStatement;
    }
    
    @Override
    public String getDatabaseUpdateStatement() {
        return this.updateStatement;
    }
    
    long getId() {
        return this.id;
    }
    
    public long getOwner() {
        return this.owner;
    }
    
    @Override
    public String toString() {
        return "ItemLastOwnerDatabaseUpdatable [id=" + this.id + ", owner=" + this.owner + ", updateStatement=" + this.updateStatement + "]";
    }
}
