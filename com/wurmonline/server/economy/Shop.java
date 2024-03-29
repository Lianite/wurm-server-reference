// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Features;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public abstract class Shop implements MiscConstants
{
    final long wurmid;
    long money;
    long taxPaid;
    private static final Logger logger;
    long ownerId;
    float priceModifier;
    static int numTraders;
    boolean followGlobalPrice;
    boolean useLocalPrice;
    long lastPolled;
    long moneyEarned;
    long moneySpent;
    long moneySpentLastMonth;
    long moneyEarnedLife;
    long moneySpentLife;
    private final LocalSupplyDemand localSupplyDemand;
    float tax;
    int numberOfItems;
    long whenEmpty;
    
    Shop(final long aWurmid, final long aMoney) {
        this.taxPaid = 0L;
        this.ownerId = -10L;
        this.priceModifier = 1.4f;
        this.followGlobalPrice = true;
        this.useLocalPrice = true;
        this.lastPolled = System.currentTimeMillis();
        this.moneyEarned = 0L;
        this.moneySpent = 0L;
        this.moneySpentLastMonth = 0L;
        this.moneyEarnedLife = 0L;
        this.moneySpentLife = 0L;
        this.tax = 0.0f;
        this.numberOfItems = 0;
        this.whenEmpty = 0L;
        this.wurmid = aWurmid;
        this.money = aMoney;
        if (!this.traderMoneyExists()) {
            this.create();
            if (aWurmid > 0L) {
                try {
                    final Creature c = Server.getInstance().getCreature(aWurmid);
                    createShop(c);
                    Economy.getEconomy().getKingsShop().setMoney(Economy.getEconomy().getKingsShop().getMoney() - aMoney);
                }
                catch (NoSuchCreatureException nsc) {
                    Shop.logger.log(Level.WARNING, "Failed to locate creature owner for shop id " + aWurmid, nsc);
                }
                catch (NoSuchPlayerException nsp) {
                    Shop.logger.log(Level.WARNING, "Creature a player?: Failed to locate creature owner for shop id " + aWurmid, nsp);
                }
            }
        }
        this.ownerId = -10L;
        this.localSupplyDemand = new LocalSupplyDemand(aWurmid);
        Economy.addShop(this);
    }
    
    Shop(final long aWurmid, final long aMoney, final long aOwnerid) {
        this.taxPaid = 0L;
        this.ownerId = -10L;
        this.priceModifier = 1.4f;
        this.followGlobalPrice = true;
        this.useLocalPrice = true;
        this.lastPolled = System.currentTimeMillis();
        this.moneyEarned = 0L;
        this.moneySpent = 0L;
        this.moneySpentLastMonth = 0L;
        this.moneyEarnedLife = 0L;
        this.moneySpentLife = 0L;
        this.tax = 0.0f;
        this.numberOfItems = 0;
        this.whenEmpty = 0L;
        this.wurmid = aWurmid;
        this.money = aMoney;
        this.ownerId = aOwnerid;
        if (aOwnerid != -10L) {
            this.numberOfItems = 0;
            this.whenEmpty = System.currentTimeMillis();
        }
        if (!this.traderMoneyExists()) {
            this.create();
        }
        this.localSupplyDemand = new LocalSupplyDemand(aWurmid);
        Economy.addShop(this);
    }
    
    Shop(final long aWurmid, final long aMoney, final long aOwnerid, final float aPriceMod, final boolean aFollowGlobalPrice, final boolean aUseLocalPrice, final long aLastPolled, final float aTax, final long spentMonth, final long spentLife, final long earnedMonth, final long earnedLife, final long spentLast, final long taxpaid, final int _numberOfItems, final long _whenEmpty, final boolean aLoad) {
        this.taxPaid = 0L;
        this.ownerId = -10L;
        this.priceModifier = 1.4f;
        this.followGlobalPrice = true;
        this.useLocalPrice = true;
        this.lastPolled = System.currentTimeMillis();
        this.moneyEarned = 0L;
        this.moneySpent = 0L;
        this.moneySpentLastMonth = 0L;
        this.moneyEarnedLife = 0L;
        this.moneySpentLife = 0L;
        this.tax = 0.0f;
        this.numberOfItems = 0;
        this.whenEmpty = 0L;
        this.wurmid = aWurmid;
        this.money = aMoney;
        this.ownerId = aOwnerid;
        this.priceModifier = aPriceMod;
        this.followGlobalPrice = aFollowGlobalPrice;
        this.useLocalPrice = aUseLocalPrice;
        this.lastPolled = aLastPolled;
        this.localSupplyDemand = new LocalSupplyDemand(aWurmid);
        this.tax = aTax;
        this.moneySpent = spentMonth;
        this.moneyEarned = earnedMonth;
        this.moneySpentLife = spentLife;
        this.moneyEarnedLife = earnedLife;
        this.moneySpentLastMonth = spentLast;
        this.taxPaid = taxpaid;
        if (this.ownerId > 0L && _numberOfItems == 0) {
            try {
                final Creature creature = Creatures.getInstance().getCreature(this.wurmid);
                final Item[] invItems = creature.getInventory().getItemsAsArray();
                int noItems = 0;
                for (int x = 0; x < invItems.length; ++x) {
                    if (!invItems[x].isCoin()) {
                        ++noItems;
                    }
                }
                if (noItems == 0) {
                    this.setMerchantData(0, _whenEmpty);
                }
                else {
                    this.setMerchantData(noItems, 0L);
                }
            }
            catch (NoSuchCreatureException e) {
                Shop.logger.log(Level.WARNING, "Merchant not loaded in time. " + e.getMessage(), e);
                this.numberOfItems = _numberOfItems;
                this.whenEmpty = _whenEmpty;
            }
        }
        else {
            this.numberOfItems = _numberOfItems;
            this.whenEmpty = _whenEmpty;
        }
        Economy.addShop(this);
        if (this.ownerId <= 0L) {
            ++Shop.numTraders;
        }
    }
    
    private static void createShop(final Creature toReturn) {
        try {
            final Item inventory = toReturn.getInventory();
            for (int x = 0; x < 3; ++x) {
                Item item = Creature.createItem(143, 10 + Server.rand.nextInt(40));
                inventory.insertItem(item);
                item = Creature.createItem(509, 80.0f);
                inventory.insertItem(item);
                item = Creature.createItem(525, 80.0f);
                inventory.insertItem(item);
                item = Creature.createItem(524, 80.0f);
                inventory.insertItem(item);
                item = Creature.createItem(601, 60 + Server.rand.nextInt(40));
                inventory.insertItem(item);
                item = Creature.createItem(664, 40.0f);
                inventory.insertItem(item);
                item = Creature.createItem(665, 40.0f);
                inventory.insertItem(item);
                if (Features.Feature.NAMECHANGE.isEnabled()) {
                    item = Creature.createItem(843, 60 + Server.rand.nextInt(40));
                    inventory.insertItem(item);
                }
                item = Creature.createItem(666, 99.0f);
                inventory.insertItem(item);
                item = Creature.createItem(668, 60 + Server.rand.nextInt(40));
                inventory.insertItem(item);
                item = Creature.createItem(667, 60 + Server.rand.nextInt(40));
                inventory.insertItem(item);
            }
            if (!Features.Feature.BLOCKED_TRADERS.isEnabled()) {
                final Item contract = Creature.createItem(299, 10 + Server.rand.nextInt(80));
                inventory.insertItem(contract);
            }
            if (Servers.localServer.PVPSERVER) {
                final Item declaration = Creature.createItem(682, 10 + Server.rand.nextInt(80));
                inventory.insertItem(declaration);
            }
            final Item contract = Creature.createItem(300, 10 + Server.rand.nextInt(80));
            inventory.insertItem(contract);
        }
        catch (Exception ex) {
            Shop.logger.log(Level.INFO, "Failed to create merchant inventory items for shop, creature: " + toReturn, ex);
        }
    }
    
    public final boolean followsGlobalPrice() {
        return this.followGlobalPrice;
    }
    
    public final boolean usesLocalPrice() {
        return this.useLocalPrice;
    }
    
    public final long getLastPolled() {
        return this.lastPolled;
    }
    
    public final long howLongEmpty() {
        if (this.numberOfItems == 0) {
            return System.currentTimeMillis() - this.whenEmpty;
        }
        return 0L;
    }
    
    public final long getWurmId() {
        return this.wurmid;
    }
    
    public final long getMoney() {
        return this.money;
    }
    
    public final boolean isPersonal() {
        return this.ownerId > 0L;
    }
    
    public final long getOwnerId() {
        return this.ownerId;
    }
    
    public final float getPriceModifier() {
        return this.priceModifier;
    }
    
    public static final int getNumTraders() {
        return Shop.numTraders;
    }
    
    public final double getLocalTraderSellPrice(final Item item, final int currentStock, final int numberSold) {
        double globalPrice = 1000000.0;
        globalPrice = item.getValue();
        if (this.useLocalPrice) {
            globalPrice = this.localSupplyDemand.getPrice(item.getTemplateId(), globalPrice, numberSold, true);
        }
        return Math.max(0.0, globalPrice);
    }
    
    public final long getLocalTraderBuyPrice(final Item item, final int currentStock, final int extra) {
        long globalPrice = 1L;
        globalPrice = item.getValue();
        if (this.useLocalPrice) {
            globalPrice = (long)this.localSupplyDemand.getPrice(item.getTemplateId(), globalPrice, extra, false);
        }
        return Math.max(0L, globalPrice);
    }
    
    final VolaTile getPos() {
        try {
            final Creature c = Creatures.getInstance().getCreature(this.wurmid);
            return c.getCurrentTile();
        }
        catch (NoSuchCreatureException nsc) {
            Shop.logger.log(Level.WARNING, "No creature for shop " + this.wurmid);
            return null;
        }
    }
    
    abstract void create();
    
    abstract boolean traderMoneyExists();
    
    public abstract void setMoney(final long p0);
    
    public abstract void delete();
    
    public abstract void setPriceModifier(final float p0);
    
    public abstract void setFollowGlobalPrice(final boolean p0);
    
    public abstract void setUseLocalPrice(final boolean p0);
    
    public abstract void setLastPolled(final long p0);
    
    public abstract void setTax(final float p0);
    
    public final float getTax() {
        return this.tax;
    }
    
    public final int getTaxAsInt() {
        return (int)(this.tax * 100.0f);
    }
    
    public float getSellRatio() {
        if (this.moneyEarned > 0L) {
            if (this.moneySpent > 0L) {
                return this.moneyEarned / this.moneySpent;
            }
        }
        else if (this.moneySpent > 0L) {
            return -this.moneySpent;
        }
        return 0.0f;
    }
    
    public long getMoneySpentMonth() {
        return this.moneySpent;
    }
    
    public long getMoneySpentLastMonth() {
        return this.moneySpentLastMonth;
    }
    
    public long getMoneyEarnedMonth() {
        return this.moneyEarned;
    }
    
    public long getMoneySpentLife() {
        return this.moneySpent;
    }
    
    public long getMoneyEarnedLife() {
        return this.moneyEarnedLife;
    }
    
    public long getTaxPaid() {
        return this.taxPaid;
    }
    
    public final LocalSupplyDemand getLocalSupplyDemand() {
        return this.localSupplyDemand;
    }
    
    public final void setMerchantData(final int _numberOfItems) {
        if (_numberOfItems == 0) {
            if (this.numberOfItems == 0) {
                if (this.whenEmpty == 0L) {
                    this.setMerchantData(0, this.lastPolled);
                }
                else {
                    this.setMerchantData(0, this.whenEmpty);
                }
            }
            else {
                this.setMerchantData(0, System.currentTimeMillis());
            }
        }
        else {
            this.setMerchantData(_numberOfItems, 0L);
        }
    }
    
    public final int getNumberOfItems() {
        return this.numberOfItems;
    }
    
    public abstract void addMoneyEarned(final long p0);
    
    public abstract void addMoneySpent(final long p0);
    
    public abstract void resetEarnings();
    
    public abstract void addTax(final long p0);
    
    public abstract void setOwner(final long p0);
    
    public abstract void setMerchantData(final int p0, final long p1);
    
    static {
        logger = Logger.getLogger(Shop.class.getName());
        Shop.numTraders = 0;
    }
}
