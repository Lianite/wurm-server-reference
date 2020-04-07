// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Features;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.Creature;
import java.util.LinkedList;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public abstract class GuardPlan implements CreatureTemplateIds, TimeConstants, MiscConstants, MonetaryConstants
{
    public static final int GUARD_PLAN_NONE = 0;
    public static final int GUARD_PLAN_LIGHT = 1;
    public static final int GUARD_PLAN_MEDIUM = 2;
    public static final int GUARD_PLAN_HEAVY = 3;
    final LinkedList<Creature> freeGuards;
    private static final Logger logger;
    public int type;
    final int villageId;
    long lastChangedPlan;
    public long moneyLeft;
    private int siegeCount;
    private int waveCounter;
    private long lastSentWarning;
    private static final long polltime = 500000L;
    long lastDrained;
    float drainModifier;
    private static final float maxDrainModifier = 5.0f;
    private static final float drainCumulateFigure = 0.5f;
    private int upkeepCounter;
    int hiredGuardNumber;
    private static final int maxGuards;
    private static final long minMoneyDrained = 7500L;
    
    GuardPlan(final int aType, final int aVillageId) {
        this.freeGuards = new LinkedList<Creature>();
        this.type = 0;
        this.siegeCount = 0;
        this.waveCounter = 0;
        this.lastSentWarning = 0L;
        this.lastDrained = 0L;
        this.drainModifier = 0.0f;
        this.upkeepCounter = 0;
        this.hiredGuardNumber = 0;
        this.type = aType;
        this.villageId = aVillageId;
        this.create();
    }
    
    GuardPlan(final int aVillageId) {
        this.freeGuards = new LinkedList<Creature>();
        this.type = 0;
        this.siegeCount = 0;
        this.waveCounter = 0;
        this.lastSentWarning = 0L;
        this.lastDrained = 0L;
        this.drainModifier = 0.0f;
        this.upkeepCounter = 0;
        this.hiredGuardNumber = 0;
        this.villageId = aVillageId;
        this.load();
    }
    
    final Village getVillage() throws NoSuchVillageException {
        return Villages.getVillage(this.villageId);
    }
    
    public final String getName() {
        if (this.type == 3) {
            return "Heavy";
        }
        if (this.type == 1) {
            return "Light";
        }
        if (this.type == 2) {
            return "Medium";
        }
        return "None";
    }
    
    public final long getTimeLeft() {
        try {
            if (this.getVillage().isPermanent || !Servers.localServer.isUpkeep()) {
                return 29030400000L;
            }
        }
        catch (NoSuchVillageException nsv) {
            GuardPlan.logger.log(Level.WARNING, this.villageId + ", " + nsv.getMessage(), nsv);
        }
        return (long)(this.moneyLeft / Math.max(1.0, this.calculateUpkeep(false)) * 500000.0);
    }
    
    public double calculateUpkeep(final boolean calculateFraction) {
        final long monthlyCost = this.getMonthlyCost();
        final double upkeep = monthlyCost * 2.0667989417989417E-4;
        return upkeep;
    }
    
    public final long getMoneyLeft() {
        return this.moneyLeft;
    }
    
    public static final long getCostForGuards(final int numGuards) {
        if (Servers.localServer.isChallengeOrEpicServer()) {
            return numGuards * 10000 + (numGuards - 1) * numGuards / 2 * 100 * 50;
        }
        return numGuards * Villages.GUARD_UPKEEP;
    }
    
    public final long getMonthlyCost() {
        if (!Servers.localServer.isUpkeep()) {
            return 0L;
        }
        try {
            final Village vill = this.getVillage();
            long cost = vill.getNumTiles() * Villages.TILE_UPKEEP;
            cost += vill.getPerimeterNonFreeTiles() * Villages.PERIMETER_UPKEEP;
            cost += getCostForGuards(this.hiredGuardNumber);
            if (vill.isCapital()) {
                cost *= (long)0.5f;
            }
            if (vill.hasToomanyCitizens()) {
                cost *= 2L;
            }
            return Math.max(Villages.MINIMUM_UPKEEP, cost);
        }
        catch (NoSuchVillageException sv) {
            GuardPlan.logger.log(Level.WARNING, "Guardplan for village " + this.villageId + ": Village not found. Deleting.", sv);
            this.delete();
            return 10000L;
        }
    }
    
    public final boolean mayRaiseUpkeep() {
        return System.currentTimeMillis() - this.lastChangedPlan > 604800000L;
    }
    
    public final boolean mayLowerUpkeep() {
        return true;
    }
    
    public final long calculateUpkeepTimeforType(final int upkeeptype) {
        final int origType = this.type;
        this.type = upkeeptype;
        final long timeleft = this.getTimeLeft();
        this.type = origType;
        return timeleft;
    }
    
    public final long calculateMonthlyUpkeepTimeforType(final int upkeeptype) {
        final int origType = this.type;
        this.type = upkeeptype;
        final long cost = this.getMonthlyCost();
        this.type = origType;
        return cost;
    }
    
    protected long getDisbandMoneyLeft() {
        return this.moneyLeft;
    }
    
    private void pollGuards() {
        if (this.type != 0) {
            try {
                final Village village = this.getVillage();
                final int _maxGuards = this.getConvertedGuardNumber(village);
                final Guard[] guards = village.getGuards();
                if (guards.length < _maxGuards) {
                    try {
                        final Item villToken = village.getToken();
                        byte sex = 0;
                        if (Server.rand.nextInt(2) == 0) {
                            sex = 1;
                        }
                        int templateId = 32;
                        if (Kingdoms.getKingdomTemplateFor(village.kingdom) == 3) {
                            templateId = 33;
                        }
                        for (int x = 0; x < Math.min(this.siegeCount + 1, _maxGuards - guards.length); ++x) {
                            try {
                                if (this.freeGuards.isEmpty()) {
                                    final Creature newc = Creature.doNew(templateId, villToken.getPosX(), villToken.getPosY(), Server.rand.nextInt(360), village.isOnSurface() ? 0 : -1, "", sex, village.kingdom);
                                    village.createGuard(newc, System.currentTimeMillis());
                                }
                                else {
                                    final Creature toReturn = this.freeGuards.removeFirst();
                                    if (toReturn.getTemplate().getTemplateId() != templateId) {
                                        this.removeReturnedGuard(toReturn.getWurmId());
                                        toReturn.destroy();
                                        final Creature newc2 = Creature.doNew(templateId, villToken.getPosX(), villToken.getPosY(), Server.rand.nextInt(360), village.isOnSurface() ? 0 : -1, "", sex, village.kingdom);
                                        village.createGuard(newc2, System.currentTimeMillis());
                                    }
                                    else {
                                        village.createGuard(toReturn, System.currentTimeMillis());
                                        this.removeReturnedGuard(toReturn.getWurmId());
                                        this.putGuardInWorld(toReturn);
                                    }
                                }
                            }
                            catch (Exception ex) {
                                GuardPlan.logger.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        GuardPlan.logger.log(Level.WARNING, "Village " + village.getName() + " has no token.");
                    }
                    if (this.siegeCount > 0) {
                        this.siegeCount += 3;
                    }
                }
                village.checkForEnemies();
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.WARNING, "No village for guardplan with villageid " + this.villageId, nsv);
            }
        }
        else {
            try {
                final Village village = this.getVillage();
                final Guard[] guards2 = village.getGuards();
                Label_0834: {
                    if (guards2.length < this.hiredGuardNumber) {
                        if (this.hiredGuardNumber > 10 && guards2.length > 10) {
                            if (this.siegeCount != 0) {
                                break Label_0834;
                            }
                        }
                        try {
                            final Item villToken2 = village.getToken();
                            if (Features.Feature.TOWER_CHAINING.isEnabled() && !villToken2.isChained()) {
                                ++this.waveCounter;
                                if (this.waveCounter % 3 != 0) {
                                    return;
                                }
                            }
                            byte sex2 = 0;
                            if (Server.rand.nextInt(2) == 0) {
                                sex2 = 1;
                            }
                            int templateId2 = 32;
                            if (village.kingdom == 3) {
                                templateId2 = 33;
                            }
                            for (int minguards = Math.max(1, this.hiredGuardNumber / 10), x = 0; x < Math.min(this.siegeCount + minguards, this.hiredGuardNumber - guards2.length); ++x) {
                                try {
                                    if (this.freeGuards.isEmpty()) {
                                        final Creature newc = Creature.doNew(templateId2, villToken2.getPosX(), villToken2.getPosY(), Server.rand.nextInt(360), village.isOnSurface() ? 0 : -1, "", sex2, village.kingdom);
                                        village.createGuard(newc, System.currentTimeMillis());
                                    }
                                    else {
                                        final Creature toReturn = this.freeGuards.removeFirst();
                                        if (toReturn.getTemplate().getTemplateId() != templateId2) {
                                            this.removeReturnedGuard(toReturn.getWurmId());
                                            toReturn.destroy();
                                            final Creature newc2 = Creature.doNew(templateId2, villToken2.getPosX(), villToken2.getPosY(), Server.rand.nextInt(360), village.isOnSurface() ? 0 : -1, "", sex2, village.kingdom);
                                            village.createGuard(newc2, System.currentTimeMillis());
                                        }
                                        else {
                                            village.createGuard(toReturn, System.currentTimeMillis());
                                            this.removeReturnedGuard(toReturn.getWurmId());
                                            this.putGuardInWorld(toReturn);
                                        }
                                    }
                                }
                                catch (Exception ex) {
                                    GuardPlan.logger.log(Level.WARNING, ex.getMessage(), ex);
                                }
                            }
                        }
                        catch (NoSuchItemException nsi2) {
                            GuardPlan.logger.log(Level.WARNING, "Village " + village.getName() + " has no token.");
                        }
                        if (this.siegeCount > 0) {
                            this.siegeCount += 3;
                        }
                    }
                }
                village.checkForEnemies();
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.WARNING, "No village for guardplan with villageid " + this.villageId, nsv);
            }
        }
    }
    
    public void startSiege() {
        this.siegeCount = 1;
    }
    
    public boolean isUnderSiege() {
        return this.siegeCount > 0;
    }
    
    public int getSiegeCount() {
        return this.siegeCount;
    }
    
    private void putGuardInWorld(final Creature guard) {
        try {
            final Item token = this.getVillage().getToken();
            guard.setPositionX(token.getPosX());
            guard.setPositionY(token.getPosY());
            try {
                guard.setLayer(token.isOnSurface() ? 0 : -1, false);
                guard.setPositionZ(Zones.calculateHeight(guard.getPosX(), guard.getPosY(), token.isOnSurface()));
                guard.respawn();
                final Zone zone = Zones.getZone(guard.getTileX(), guard.getTileY(), guard.isOnSurface());
                zone.addCreature(guard.getWurmId());
                guard.savePosition(zone.getId());
            }
            catch (NoSuchZoneException nsz) {
                GuardPlan.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsz.getMessage(), nsz);
            }
            catch (NoSuchCreatureException nsc) {
                GuardPlan.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsc.getMessage(), nsc);
                this.getVillage().deleteGuard(guard, false);
            }
            catch (NoSuchPlayerException nsp) {
                GuardPlan.logger.log(Level.WARNING, "Guard: " + guard.getWurmId() + ": " + nsp.getMessage(), nsp);
            }
            catch (Exception ex) {
                GuardPlan.logger.log(Level.WARNING, "Failed to return village guard: " + ex.getMessage(), ex);
            }
        }
        catch (NoSuchItemException nsi) {
            GuardPlan.logger.log(Level.WARNING, nsi.getMessage(), nsi);
        }
        catch (NoSuchVillageException nsv) {
            GuardPlan.logger.log(Level.WARNING, nsv.getMessage(), nsv);
        }
    }
    
    public final void returnGuard(final Creature guard) {
        if (!this.freeGuards.contains(guard)) {
            this.freeGuards.add(guard);
            this.addReturnedGuard(guard.getWurmId());
        }
    }
    
    private boolean pollUpkeep() {
        try {
            if (this.getVillage().isPermanent) {
                return false;
            }
        }
        catch (NoSuchVillageException ex) {}
        if (!Servers.localServer.isUpkeep()) {
            return false;
        }
        final long upkeep = (long)this.calculateUpkeep(true);
        if (this.moneyLeft - upkeep <= 0L) {
            try {
                GuardPlan.logger.log(Level.INFO, this.getVillage().getName() + " disbanding. Money left=" + this.moneyLeft + ", upkeep=" + upkeep);
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.INFO, nsv.getMessage(), nsv);
            }
            return true;
        }
        if (upkeep >= 100L) {
            try {
                GuardPlan.logger.log(Level.INFO, this.getVillage().getName() + " upkeep=" + upkeep);
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.INFO, nsv.getMessage(), nsv);
            }
        }
        this.updateGuardPlan(this.type, this.moneyLeft - Math.max(1L, upkeep), this.hiredGuardNumber);
        ++this.upkeepCounter;
        if (this.upkeepCounter == 2) {
            this.upkeepCounter = 0;
            final Shop shop = Economy.getEconomy().getKingsShop();
            if (shop != null) {
                if (upkeep <= 1L) {
                    shop.setMoney(shop.getMoney() + Math.max(1L, upkeep));
                }
                else {
                    shop.setMoney(shop.getMoney() + upkeep);
                }
            }
            else {
                GuardPlan.logger.log(Level.WARNING, "No shop when " + this.villageId + " paying upkeep.");
            }
        }
        final long tl = this.getTimeLeft();
        if (tl < 3600000L) {
            try {
                this.getVillage().broadCastAlert("The village is disbanding within the hour. You may add upkeep money to the village coffers at the token immediately.", (byte)2);
                this.getVillage().broadCastAlert("Any traders who are citizens of " + this.getVillage().getName() + " will disband without refund.");
            }
            catch (NoSuchVillageException nsv2) {
                GuardPlan.logger.log(Level.WARNING, "No Village? " + this.villageId, nsv2);
            }
        }
        else if (tl < 86400000L) {
            if (System.currentTimeMillis() - this.lastSentWarning > 3600000L) {
                this.lastSentWarning = System.currentTimeMillis();
                try {
                    this.getVillage().broadCastAlert("The village is disbanding within 24 hours. You may add upkeep money to the village coffers at the token.", (byte)2);
                    this.getVillage().broadCastAlert("Any traders who are citizens of " + this.getVillage().getName() + " will disband without refund.");
                }
                catch (NoSuchVillageException nsv2) {
                    GuardPlan.logger.log(Level.WARNING, "No Village? " + this.villageId, nsv2);
                }
            }
        }
        else if (tl < 604800000L && System.currentTimeMillis() - this.lastSentWarning > 3600000L) {
            this.lastSentWarning = System.currentTimeMillis();
            try {
                this.getVillage().broadCastAlert("The village is disbanding within one week. Due to the low morale this gives, the guards have ceased their general maintenance of structures.", (byte)4);
                this.getVillage().broadCastAlert("Any traders who are citizens of " + this.getVillage().getName() + " will disband without refund.");
            }
            catch (NoSuchVillageException nsv2) {
                GuardPlan.logger.log(Level.WARNING, "No Village? " + this.villageId, nsv2);
            }
        }
        return false;
    }
    
    public final void destroyGuard(final Creature guard) {
        this.freeGuards.remove(guard);
        this.removeReturnedGuard(guard.getWurmId());
    }
    
    final boolean poll() {
        this.pollGuards();
        if (this.siegeCount > 0) {
            --this.siegeCount;
            this.siegeCount = Math.min(this.siegeCount, 9);
            try {
                if (!this.getVillage().isAlerted()) {
                    this.siegeCount = Math.max(0, this.siegeCount - 1);
                }
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.WARNING, nsv.getMessage());
            }
        }
        if (this.drainModifier > 0.0f && System.currentTimeMillis() - this.lastDrained > 172800000L) {
            this.drainModifier = 0.0f;
            this.saveDrainMod();
        }
        return this.pollUpkeep();
    }
    
    public final long getLastDrained() {
        return this.lastDrained;
    }
    
    public static final int getMaxGuards(final Village village) {
        return getMaxGuards(village.getDiameterX(), village.getDiameterY());
    }
    
    public static final int getMaxGuards(final int diameterX, final int diameterY) {
        return Math.min(GuardPlan.maxGuards, Math.max(3, diameterX * diameterY / 49));
    }
    
    public final int getNumHiredGuards() {
        return this.hiredGuardNumber;
    }
    
    public final int getConvertedGuardNumber(final Village village) {
        int max = getMaxGuards(village);
        if (this.type == 1) {
            max = Math.max(1, max / 4);
        }
        if (this.type == 2) {
            Math.max(1, max /= 2);
        }
        return max;
    }
    
    public final void changePlan(final int newPlan, final int newNumberOfGuards) {
        this.lastChangedPlan = System.currentTimeMillis();
        int changeInGuards = newNumberOfGuards - this.getNumHiredGuards();
        this.updateGuardPlan(newPlan, this.moneyLeft, newNumberOfGuards);
        if (changeInGuards < 0) {
            try {
                final Village village = this.getVillage();
                int deleted = 0;
                changeInGuards = Math.abs(changeInGuards);
                if (this.freeGuards.size() > 0) {
                    final Creature[] crets = this.freeGuards.toArray(new Creature[this.freeGuards.size()]);
                    for (int x = 0; x < Math.min(crets.length, changeInGuards); ++x) {
                        ++deleted;
                        this.removeReturnedGuard(crets[x].getWurmId());
                        crets[x].destroy();
                    }
                }
                if (deleted < changeInGuards) {
                    final Guard[] guards = village.getGuards();
                    for (int x = 0; x < Math.min(guards.length, changeInGuards - deleted); ++x) {
                        if (guards[x].creature.isSpiritGuard()) {
                            village.deleteGuard(guards[x].creature, true);
                        }
                    }
                }
            }
            catch (NoSuchVillageException nsv) {
                GuardPlan.logger.log(Level.WARNING, "Village lacking for plan " + this.villageId, nsv);
            }
        }
    }
    
    public final void addMoney(final long moneyAdded) {
        if (moneyAdded > 0L) {
            this.updateGuardPlan(this.type, this.moneyLeft + moneyAdded, this.hiredGuardNumber);
        }
    }
    
    public final long getTimeToNextDrain() {
        try {
            if (this.getVillage().isPermanent) {
                return 86400000L;
            }
        }
        catch (NoSuchVillageException nsv) {
            GuardPlan.logger.log(Level.WARNING, this.villageId + ", " + nsv.getMessage(), nsv);
            return 86400000L;
        }
        return this.lastDrained + 86400000L - System.currentTimeMillis();
    }
    
    public final long getMoneyDrained() {
        try {
            if (this.getVillage().isPermanent) {
                return 0L;
            }
        }
        catch (NoSuchVillageException nsv) {
            GuardPlan.logger.log(Level.WARNING, this.villageId + ", " + nsv.getMessage(), nsv);
            return 0L;
        }
        return (long)Math.min(this.moneyLeft, (1.0f + this.drainModifier) * Math.max(7500.0f, this.getMonthlyCost() * 0.15f));
    }
    
    public long drainMoney() {
        final long moneyToDrain = this.getMoneyDrained();
        this.drainGuardPlan(this.moneyLeft - moneyToDrain);
        this.drainModifier += 0.5f;
        this.saveDrainMod();
        return moneyToDrain;
    }
    
    public final void fixGuards() {
        try {
            final Guard[] gs = this.getVillage().getGuards();
            for (int x = 0; x < gs.length; ++x) {
                if (gs[x].creature.isDead()) {
                    this.getVillage().deleteGuard(gs[x].creature, false);
                    this.returnGuard(gs[x].creature);
                    GuardPlan.logger.log(Level.INFO, "Destroyed dead guard for " + this.getVillage().getName());
                }
            }
        }
        catch (NoSuchVillageException nsv) {
            GuardPlan.logger.log(Level.WARNING, "Village lacking for plan " + this.villageId, nsv);
        }
    }
    
    public final float getProsperityModifier() {
        if (this.getMoneyLeft() > 1000000L) {
            return 1.05f;
        }
        return 1.0f;
    }
    
    public void updateGuardPlan(final long aMoneyLeft) {
        this.updateGuardPlan(this.type, aMoneyLeft, this.hiredGuardNumber);
    }
    
    abstract void create();
    
    abstract void load();
    
    public abstract void updateGuardPlan(final int p0, final long p1, final int p2);
    
    abstract void delete();
    
    abstract void addReturnedGuard(final long p0);
    
    abstract void removeReturnedGuard(final long p0);
    
    abstract void saveDrainMod();
    
    abstract void deleteReturnedGuards();
    
    public abstract void addPayment(final String p0, final long p1, final long p2);
    
    abstract void drainGuardPlan(final long p0);
    
    static {
        logger = Logger.getLogger(GuardPlan.class.getName());
        maxGuards = (Servers.localServer.isChallengeOrEpicServer() ? 20 : 50);
    }
}
