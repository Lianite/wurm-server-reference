// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SupplyDemand
{
    final int id;
    int itemsBought;
    int itemsSold;
    private static final Logger logger;
    long lastPolled;
    
    SupplyDemand(final int aId, final int aItemsBought, final int aItemsSold) {
        this.id = aId;
        this.itemsBought = aItemsBought;
        this.itemsSold = aItemsSold;
        if (!this.supplyDemandExists()) {
            this.createSupplyDemand(aItemsBought, aItemsSold);
        }
        else {
            SupplyDemand.logger.log(Level.INFO, "Creating supply demand for already existing id: " + aId);
        }
        Economy.addSupplyDemand(this);
    }
    
    SupplyDemand(final int aId, final int aItemsBought, final int aItemsSold, final long aLastPolled) {
        this.id = aId;
        this.itemsBought = aItemsBought;
        this.itemsSold = aItemsSold;
        this.lastPolled = aLastPolled;
        Economy.addSupplyDemand(this);
    }
    
    public final float getDemandMod(final int extraSold) {
        return Math.max(1000.0f, this.itemsSold) / Math.max(1000.0f, this.itemsBought + extraSold);
    }
    
    public final int getItemsBoughtByTraders() {
        return this.itemsBought;
    }
    
    public final int getItemsSoldByTraders() {
        return this.itemsSold;
    }
    
    final void addItemBoughtByTrader() {
        this.updateItemsBoughtByTraders(this.itemsBought + 1);
    }
    
    final void addItemSoldByTrader() {
        this.updateItemsSoldByTraders(this.itemsSold + 1);
    }
    
    public final int getId() {
        return this.id;
    }
    
    public final int getPool() {
        return this.itemsBought - this.itemsSold;
    }
    
    public final long getLastPolled() {
        return this.lastPolled;
    }
    
    abstract void updateItemsBoughtByTraders(final int p0);
    
    abstract void updateItemsSoldByTraders(final int p0);
    
    abstract void createSupplyDemand(final int p0, final int p1);
    
    abstract boolean supplyDemandExists();
    
    abstract void reset(final long p0);
    
    @Override
    public final String toString() {
        return "SupplyDemand [TemplateID: " + this.id + ", Items bought:" + this.itemsBought + ", Sold:" + this.itemsSold + ", Pool: " + this.getPool() + ", Time last polled: " + this.lastPolled + ']';
    }
    
    static {
        logger = Logger.getLogger(SupplyDemand.class.getName());
    }
}
