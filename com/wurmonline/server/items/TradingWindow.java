// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.HashMap;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.FailedException;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.villages.NoSuchRoleException;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.NoSuchItemException;
import java.util.HashSet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.shared.constants.CreatureTypes;
import com.wurmonline.server.villages.VillageStatus;
import com.wurmonline.server.MiscConstants;

public final class TradingWindow implements MiscConstants, ItemTypes, VillageStatus, CreatureTypes, MonetaryConstants
{
    private final Creature windowowner;
    private final Creature watcher;
    private final boolean offer;
    private final long wurmId;
    private Set<Item> items;
    private final Trade trade;
    private static final Logger logger;
    private static final Map<String, Logger> loggers;
    
    TradingWindow(final Creature aOwner, final Creature aWatcher, final boolean aOffer, final long aWurmId, final Trade aTrade) {
        this.windowowner = aOwner;
        this.watcher = aWatcher;
        this.offer = aOffer;
        this.wurmId = aWurmId;
        this.trade = aTrade;
    }
    
    public static final void stopLoggers() {
        for (final Logger logger : TradingWindow.loggers.values()) {
            if (logger != null) {
                for (final Handler h : logger.getHandlers()) {
                    h.close();
                }
            }
        }
    }
    
    private static Logger getLogger(final long wurmid) {
        final String name = "trader" + wurmid;
        Logger personalLogger = TradingWindow.loggers.get(name);
        if (personalLogger == null) {
            personalLogger = Logger.getLogger(name);
            personalLogger.setUseParentHandlers(false);
            final Handler[] h = TradingWindow.logger.getHandlers();
            for (int i = 0; i != h.length; ++i) {
                personalLogger.removeHandler(h[i]);
            }
            try {
                final FileHandler fh = new FileHandler(name + ".log", 0, 1, true);
                fh.setFormatter(new SimpleFormatter());
                personalLogger.addHandler(fh);
            }
            catch (IOException ie) {
                Logger.getLogger(name).log(Level.WARNING, name + ":no redirection possible!");
            }
            TradingWindow.loggers.put(name, personalLogger);
        }
        return personalLogger;
    }
    
    public boolean mayMoveItemToWindow(final Item item, final Creature creature, final long window) {
        boolean toReturn = false;
        if (this.wurmId == 3L) {
            if (window == 1L) {
                toReturn = true;
            }
        }
        else if (this.wurmId == 4L) {
            if (window == 2L) {
                toReturn = true;
            }
        }
        else if (this.wurmId == 2L) {
            if (!this.windowowner.equals(creature)) {
                if (creature.isPlayer() && item.isCoin() && !this.windowowner.isPlayer()) {
                    return false;
                }
                if (window == 4L) {
                    toReturn = true;
                }
            }
        }
        else if (this.wurmId == 1L && !this.windowowner.equals(creature) && window == 3L && this.watcher == creature && item.getOwnerId() == this.windowowner.getWurmId()) {
            toReturn = true;
        }
        return toReturn;
    }
    
    public boolean mayAddFromInventory(final Creature creature, final Item item) {
        if (!item.isTraded()) {
            if (item.isNoTrade()) {
                creature.getCommunicator().sendSafeServerMessage(item.getNameWithGenus() + " is not tradable.");
            }
            else if (this.windowowner.equals(creature)) {
                try {
                    final long owneri = item.getOwner();
                    if (owneri != this.watcher.getWurmId() && owneri != this.windowowner.getWurmId()) {
                        this.windowowner.setCheated("Traded " + item.getName() + "[" + item.getWurmId() + "] with " + this.watcher.getName() + " owner=" + owneri);
                    }
                }
                catch (NotOwnedException not) {
                    this.windowowner.setCheated("Traded " + item.getName() + "[" + item.getWurmId() + "] with " + this.watcher.getName() + " not owned?");
                }
                if (this.wurmId == 2L || this.wurmId == 1L) {
                    if (item.isHollow()) {
                        final Item[] allItems;
                        final Item[] its = allItems = item.getAllItems(true);
                        for (final Item lIt : allItems) {
                            if (lIt.isNoTrade() || lIt.isVillageDeed() || lIt.isHomesteadDeed() || lIt.getTemplateId() == 781) {
                                creature.getCommunicator().sendSafeServerMessage(item.getNameWithGenus() + " contains a non-tradable item.");
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public long getWurmId() {
        return this.wurmId;
    }
    
    public Item[] getItems() {
        if (this.items != null) {
            return this.items.toArray(new Item[this.items.size()]);
        }
        return new Item[0];
    }
    
    private void removeExistingContainedItems(final Item item) {
        if (item.isHollow()) {
            final Item[] itemsAsArray;
            final Item[] itemarr = itemsAsArray = item.getItemsAsArray();
            for (final Item lElement : itemsAsArray) {
                this.removeExistingContainedItems(lElement);
                if (lElement.getTradeWindow() == this) {
                    this.removeFromTrade(lElement, false);
                }
                else if (lElement.getTradeWindow() != null) {
                    lElement.getTradeWindow().removeItem(lElement);
                }
            }
        }
    }
    
    public Item[] getAllItems() {
        if (this.items != null) {
            final Set<Item> toRet = new HashSet<Item>();
            for (final Item item : this.items) {
                toRet.add(item);
                final Item[] allItems;
                final Item[] toAdd = allItems = item.getAllItems(false);
                for (final Item lElement : allItems) {
                    if (lElement.tradeWindow == this) {
                        toRet.add(lElement);
                    }
                }
            }
            return toRet.toArray(new Item[toRet.size()]);
        }
        return new Item[0];
    }
    
    public void stopReceivingItems() {
    }
    
    public void startReceivingItems() {
    }
    
    public void addItem(final Item item) {
        if (this.items == null) {
            this.items = new HashSet<Item>();
        }
        if (item.tradeWindow == null) {
            this.removeExistingContainedItems(item);
            Item parent = item;
            try {
                parent = item.getParent();
            }
            catch (NoSuchItemException ex) {}
            this.items.add(item);
            this.addToTrade(item, parent);
            if (item == parent || parent.isViewableBy(this.windowowner)) {
                if (!this.windowowner.isPlayer()) {
                    this.windowowner.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, 0);
                }
                else if (!this.watcher.isPlayer()) {
                    this.windowowner.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, this.watcher.getTradeHandler().getTraderBuyPriceForItem(item));
                }
                else {
                    this.windowowner.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, item.getPrice());
                }
            }
            if (item == parent || parent.isViewableBy(this.watcher)) {
                if (!this.watcher.isPlayer()) {
                    this.watcher.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, 0);
                }
                else if (!this.windowowner.isPlayer()) {
                    this.watcher.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, this.windowowner.getTradeHandler().getTraderSellPriceForItem(item, this));
                }
                else {
                    this.watcher.getCommunicator().sendAddToInventory(item, this.wurmId, (parent.tradeWindow == this) ? parent.getWurmId() : 0L, item.getPrice());
                }
            }
        }
        this.tradeChanged();
    }
    
    private void addToTrade(final Item item, final Item parent) {
        if (item.tradeWindow != this) {
            item.setTradeWindow(this);
        }
        final Item[] itemsAsArray;
        final Item[] its = itemsAsArray = item.getItemsAsArray();
        for (final Item lIt : itemsAsArray) {
            this.addToTrade(lIt, item);
        }
    }
    
    private void removeFromTrade(final Item item, final boolean noSwap) {
        this.windowowner.getCommunicator().sendRemoveFromInventory(item, this.wurmId);
        this.watcher.getCommunicator().sendRemoveFromInventory(item, this.wurmId);
        if (noSwap && item.isCoin()) {
            if (item.getOwnerId() == -10L) {
                Economy.getEconomy().returnCoin(item, "Notrade", true);
            }
            item.setTradeWindow(null);
        }
        else {
            item.setTradeWindow(null);
        }
    }
    
    public void removeItem(final Item item) {
        if (this.items != null && item.tradeWindow == this) {
            this.removeExistingContainedItems(item);
            this.items.remove(item);
            this.removeFromTrade(item, true);
            this.tradeChanged();
        }
    }
    
    public void updateItem(final Item item) {
        if (this.items != null && item.tradeWindow == this) {
            if (!this.windowowner.isPlayer()) {
                this.windowowner.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, 0);
            }
            else if (!this.watcher.isPlayer()) {
                this.windowowner.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, this.watcher.getTradeHandler().getTraderBuyPriceForItem(item));
            }
            else {
                this.windowowner.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, item.getPrice());
            }
            if (!this.watcher.isPlayer()) {
                this.watcher.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, 0);
            }
            else if (!this.windowowner.isPlayer()) {
                this.watcher.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, this.windowowner.getTradeHandler().getTraderSellPriceForItem(item, this));
            }
            else {
                this.watcher.getCommunicator().sendUpdateInventoryItem(item, this.wurmId, item.getPrice());
            }
            this.tradeChanged();
        }
    }
    
    private void tradeChanged() {
        if (this.wurmId == 2L && !this.trade.creatureTwo.isPlayer()) {
            this.trade.setCreatureTwoSatisfied(false);
        }
        if (this.wurmId == 3L || this.wurmId == 4L) {
            this.trade.setCreatureOneSatisfied(false);
            this.trade.setCreatureTwoSatisfied(false);
            final int c = this.trade.getNextTradeId();
            this.windowowner.getCommunicator().sendTradeChanged(c);
            this.watcher.getCommunicator().sendTradeChanged(c);
        }
    }
    
    boolean hasInventorySpace() {
        if (this.offer) {
            this.windowowner.getCommunicator().sendAlertServerMessage("There is a bug in the trade system. This shouldn't happen. Please report.");
            this.watcher.getCommunicator().sendAlertServerMessage("There is a bug in the trade system. This shouldn't happen. Please report.");
            TradingWindow.logger.log(Level.WARNING, "Inconsistency! This is offer window number " + this.wurmId + ". Traders are " + this.watcher.getName() + ", " + this.windowowner.getName());
            return false;
        }
        if (!(this.watcher instanceof Player)) {
            return true;
        }
        final Item inventory = this.watcher.getInventory();
        if (inventory == null) {
            this.windowowner.getCommunicator().sendAlertServerMessage("Could not find inventory for " + this.watcher.getName() + ". Trade aborted.");
            this.watcher.getCommunicator().sendAlertServerMessage("Could not find your inventory item. Trade aborted. Please contact administrators.");
            TradingWindow.logger.log(Level.WARNING, "Failed to locate inventory for " + this.watcher.getName());
            return false;
        }
        if (this.items != null) {
            int nums = 0;
            for (final Item item : this.items) {
                if (!inventory.testInsertItem(item)) {
                    return false;
                }
                if (!item.isCoin()) {
                    ++nums;
                }
                if (!item.canBeDropped(false) && ((Player)this.watcher).isGuest()) {
                    this.windowowner.getCommunicator().sendAlertServerMessage("Guests cannot receive the item " + item.getName() + ".");
                    this.watcher.getCommunicator().sendAlertServerMessage("Guests cannot receive the item " + item.getName() + ".");
                    return false;
                }
            }
            if (this.watcher.getPower() <= 0 && nums + inventory.getNumItemsNotCoins() > 99) {
                this.watcher.getCommunicator().sendAlertServerMessage("You may not carry that many items in your inventory.");
                this.windowowner.getCommunicator().sendAlertServerMessage(this.watcher.getName() + " may not carry that many items in " + this.watcher.getHisHerItsString() + " inventory.");
                return false;
            }
        }
        return true;
    }
    
    int getWeight() {
        int toReturn = 0;
        if (this.items != null) {
            for (final Item item : this.items) {
                toReturn += item.getFullWeight();
            }
        }
        return toReturn;
    }
    
    boolean validateTrade() {
        if (this.windowowner.isDead()) {
            return false;
        }
        if (this.windowowner instanceof Player && !this.windowowner.hasLink()) {
            return false;
        }
        if (this.items != null) {
            for (final Item tit : this.items) {
                if ((this.windowowner instanceof Player || !tit.isCoin()) && tit.getOwnerId() != this.windowowner.getWurmId()) {
                    this.windowowner.getCommunicator().sendAlertServerMessage(tit.getName() + " is not owned by you. Trade aborted.");
                    this.watcher.getCommunicator().sendAlertServerMessage(tit.getName() + " is not owned by " + this.windowowner.getName() + ". Trade aborted.");
                    return false;
                }
                final Item[] allItems2;
                final Item[] allItems = allItems2 = tit.getAllItems(false);
                for (final Item lAllItem : allItems2) {
                    if ((this.windowowner instanceof Player || !lAllItem.isCoin()) && lAllItem.getOwnerId() != this.windowowner.getWurmId()) {
                        this.windowowner.getCommunicator().sendAlertServerMessage(lAllItem.getName() + " is not owned by you. Trade aborted.");
                        this.watcher.getCommunicator().sendAlertServerMessage(lAllItem.getName() + " is not owned by " + this.windowowner.getName() + ". Trade aborted.");
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    void swapOwners() {
        boolean errors = false;
        if (!this.offer) {
            final Item inventory = this.watcher.getInventory();
            final Item ownInventory = this.windowowner.getInventory();
            Shop shop = null;
            int moneyAdded = 0;
            int moneyLost = 0;
            if (this.windowowner.isNpcTrader()) {
                shop = Economy.getEconomy().getShop(this.windowowner);
            }
            else if (this.watcher.isNpcTrader()) {
                shop = Economy.getEconomy().getShop(this.watcher);
            }
            if (this.items != null) {
                final Item[] array;
                final Item[] its = array = this.items.toArray(new Item[this.items.size()]);
                for (final Item item : array) {
                    final Item lIt = item;
                    this.removeExistingContainedItems(lIt);
                    this.removeFromTrade(item, false);
                    final boolean coin = item.isCoin();
                    final long parentId = item.getParentId();
                    boolean ok = true;
                    if (this.windowowner instanceof Player) {
                        if (this.watcher instanceof Player) {
                            if (item.isVillageDeed() || item.isHomesteadDeed()) {
                                final int data = item.getData2();
                                if (data > 0) {
                                    if (!this.watcher.isPaying()) {
                                        this.windowowner.getCommunicator().sendNormalServerMessage("You need to be premium in order to receive a deed.");
                                        ok = false;
                                    }
                                    else {
                                        try {
                                            final Village village = Villages.getVillage(data);
                                            final Citizen oldMayor = village.getCitizen(this.windowowner.getWurmId());
                                            final Village oldVillage = this.watcher.getCitizenVillage();
                                            if (this.windowowner.getKingdomId() != this.watcher.getKingdomId()) {
                                                this.windowowner.getCommunicator().sendNormalServerMessage("You cannot trade the deed for " + village.getName() + " to another kingdom.");
                                                ok = false;
                                            }
                                            if (ok && oldVillage != null && oldVillage != village) {
                                                final Citizen oldCit = oldVillage.getCitizen(this.watcher.getWurmId());
                                                final VillageRole role = oldCit.getRole();
                                                if (role.getStatus() == 2) {
                                                    this.watcher.getCommunicator().sendNormalServerMessage("You cannot trade the deed for " + village.getName() + " since you are already the mayor of " + oldVillage.getName());
                                                    this.windowowner.getCommunicator().sendNormalServerMessage("You cannot trade the deed for " + village.getName() + " since " + this.watcher.getName() + " is already the mayor of " + oldVillage.getName());
                                                    ok = false;
                                                }
                                                if (ok && oldCit != null) {
                                                    oldVillage.removeCitizen(this.watcher);
                                                }
                                            }
                                            if (ok) {
                                                if (oldMayor != null) {
                                                    try {
                                                        if (item.isVillageDeed()) {
                                                            oldMayor.setRole(village.getRoleForStatus((byte)3));
                                                        }
                                                        else {
                                                            village.removeCitizen(oldMayor);
                                                        }
                                                    }
                                                    catch (IOException iox) {
                                                        TradingWindow.logger.log(Level.WARNING, "Error when removing " + this.windowowner.getName() + " as mayor: " + iox.getMessage(), iox);
                                                        this.watcher.getCommunicator().sendSafeServerMessage("An error occured when removing " + this.windowowner.getName() + " as mayor. Please contact administration.");
                                                        this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when removing you as mayor. Please contact administration.");
                                                    }
                                                }
                                                if (village.getMayor() != null) {
                                                    TradingWindow.logger.log(Level.WARNING, "Error when changing mayor. Mayor should have been removed - " + this.windowowner.getName() + " with wurmid: " + this.windowowner.getWurmId() + ". Current mayor is " + village.getMayor().getId() + ". Removing that mayor anyways.");
                                                    try {
                                                        village.getMayor().setRole(village.getRoleForStatus((byte)3));
                                                    }
                                                    catch (IOException iox) {
                                                        TradingWindow.logger.log(Level.WARNING, "Error when removing " + this.windowowner.getName() + " as mayor: " + iox.getMessage(), iox);
                                                        this.watcher.getCommunicator().sendSafeServerMessage("An error occured when removing " + this.windowowner.getName() + " as mayor. Please contact administration.");
                                                        this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when removing you as mayor. Please contact administration.");
                                                    }
                                                }
                                                final Citizen newMayor = village.getCitizen(this.watcher.getWurmId());
                                                if (newMayor == null) {
                                                    try {
                                                        village.addCitizen(this.watcher, village.getRoleForStatus((byte)2));
                                                    }
                                                    catch (IOException iox2) {
                                                        TradingWindow.logger.log(Level.WARNING, "Error when setting " + this.watcher.getName() + " as mayor: " + iox2.getMessage(), iox2);
                                                        this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when setting " + this.watcher.getName() + " as mayor. Please contact administration.");
                                                        this.watcher.getCommunicator().sendSafeServerMessage("An error occured when setting you as mayor. Please contact administration.");
                                                    }
                                                }
                                                else {
                                                    try {
                                                        newMayor.setRole(village.getRoleForStatus((byte)2));
                                                    }
                                                    catch (IOException iox2) {
                                                        TradingWindow.logger.log(Level.WARNING, "Error when setting " + this.watcher.getName() + " as mayor: " + iox2.getMessage(), iox2);
                                                        this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when setting " + this.watcher.getName() + " as mayor. Please contact administration.");
                                                        this.watcher.getCommunicator().sendSafeServerMessage("An error occured when setting you as mayor. Please contact administration.");
                                                    }
                                                }
                                                try {
                                                    village.setMayor(this.watcher.getName());
                                                }
                                                catch (IOException iox2) {
                                                    TradingWindow.logger.log(Level.WARNING, this.watcher.getName() + ", " + this.windowowner.getName() + ":" + iox2.getMessage(), iox2);
                                                }
                                            }
                                        }
                                        catch (NoSuchVillageException nsv) {
                                            TradingWindow.logger.log(Level.WARNING, "Weird. No village with id " + data + " when " + this.windowowner.getName() + " sold deed with id " + item.getWurmId());
                                        }
                                        catch (NoSuchRoleException nsr) {
                                            TradingWindow.logger.log(Level.WARNING, "Error when setting " + this.watcher.getName() + " as mayor: " + nsr.getMessage(), nsr);
                                            this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when setting " + this.watcher.getName() + " as mayor. Please contact administration.");
                                            this.watcher.getCommunicator().sendSafeServerMessage("An error occured when setting you as mayor. Please contact administration.");
                                        }
                                    }
                                }
                            }
                            else if (item.getTemplateId() == 300) {
                                final long traderId = item.getData();
                                if (traderId != -1L) {
                                    try {
                                        final Creature trader = Server.getInstance().getCreature(traderId);
                                        if (trader.isNpcTrader()) {
                                            shop = Economy.getEconomy().getShop(trader);
                                        }
                                        shop.setOwner(this.watcher.getWurmId());
                                        this.watcher.getCommunicator().sendNormalServerMessage("You are now in control of " + trader.getName() + ".");
                                        this.windowowner.getCommunicator().sendNormalServerMessage("You are no longer in control of " + trader.getName() + ".");
                                    }
                                    catch (NoSuchPlayerException nsp) {
                                        TradingWindow.logger.log(Level.WARNING, "Trader for " + traderId + " is a player? Well it can't be found.");
                                        item.setData(-10L);
                                    }
                                    catch (NoSuchCreatureException nsc) {
                                        TradingWindow.logger.log(Level.WARNING, "Trader for " + traderId + " can't be found.");
                                        item.setData(-10L);
                                    }
                                }
                            }
                            else if (item.getTemplateId() == 1129) {
                                final long wagonerId = item.getData();
                                if (wagonerId != -1L) {
                                    final Wagoner wagoner = Wagoner.getWagoner(wagonerId);
                                    if (wagoner != null) {
                                        wagoner.setOwnerId(this.watcher.getWurmId());
                                        this.watcher.getCommunicator().sendNormalServerMessage("You are now in control of " + wagoner.getName() + ".");
                                        this.windowowner.getCommunicator().sendNormalServerMessage("You are no longer in control of " + wagoner.getName() + ".");
                                    }
                                }
                            }
                            else if (item.isRoyal()) {
                                if (item.getTemplateId() != 530 && item.getTemplateId() != 533 && item.getTemplateId() != 536 && !this.watcher.isKing()) {
                                    this.watcher.getCommunicator().sendNormalServerMessage(this.windowowner.getName() + " seems hesitatant about trading " + item.getName() + ". You need to be crowned the ruler first.");
                                    this.windowowner.getCommunicator().sendNormalServerMessage("Those noble items should not be tainted by simple trade. You need to crown " + this.watcher.getName() + " ruler first.");
                                    ok = false;
                                }
                            }
                            else if (item.getTemplateId() == 781) {
                                this.watcher.getCommunicator().sendNormalServerMessage("You may not trade the " + item.getName() + ".");
                                this.windowowner.getCommunicator().sendNormalServerMessage("You may not trade the " + item.getName() + ".");
                                ok = false;
                            }
                        }
                        else {
                            if (item.isVillageDeed() || item.isHomesteadDeed()) {
                                final int data = item.getData2();
                                if (data > 0) {
                                    try {
                                        final Village village = Villages.getVillage(data);
                                        final Citizen oldMayor = village.getCitizen(this.windowowner.getWurmId());
                                        if (oldMayor != null) {
                                            try {
                                                oldMayor.setRole(village.getRoleForStatus((byte)3));
                                            }
                                            catch (IOException iox3) {
                                                TradingWindow.logger.log(Level.WARNING, "Error when removing " + this.windowowner.getName() + " as mayor: " + iox3.getMessage(), iox3);
                                                this.watcher.getCommunicator().sendSafeServerMessage("An error occured when removing " + this.windowowner.getName() + " as mayor. Please contact administration.");
                                                this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when removing you as mayor. Please contact administration.");
                                            }
                                            catch (NoSuchRoleException nsr2) {
                                                TradingWindow.logger.log(Level.WARNING, "Error when removing " + this.windowowner.getName() + " as mayor: " + nsr2.getMessage(), nsr2);
                                                this.watcher.getCommunicator().sendSafeServerMessage("An error occured when removing " + this.windowowner.getName() + " as mayor. Please contact administration.");
                                                this.windowowner.getCommunicator().sendSafeServerMessage("An error occured when removing you as mayor. Please contact administration.");
                                            }
                                        }
                                    }
                                    catch (NoSuchVillageException nsv) {
                                        TradingWindow.logger.log(Level.WARNING, "Weird. No village with id " + data + " when " + this.windowowner.getName() + " sold deed with id " + item.getWurmId());
                                    }
                                }
                            }
                            if (this.windowowner.isLogged()) {
                                this.windowowner.getLogger().log(Level.INFO, this.windowowner.getName() + " selling " + item.getName() + " with id " + item.getWurmId() + " to " + this.watcher.getName());
                            }
                        }
                        if (item.getTemplateId() == 166 && this.watcher.getPower() == 0) {
                            try {
                                final Structure s = Structures.getStructureForWrit(item.getWurmId());
                                if (s != null && MissionTargets.destroyStructureTargets(s.getWurmId(), this.watcher.getName())) {
                                    this.watcher.getCommunicator().sendAlertServerMessage("A mission trigger was removed for " + s.getName() + ".");
                                }
                            }
                            catch (NoSuchStructureException ex) {}
                        }
                    }
                    else if (this.watcher.isLogged()) {
                        this.watcher.getLogger().log(Level.INFO, this.windowowner.getName() + " buying " + item.getName() + " with id " + item.getWurmId() + " from " + this.watcher.getName());
                    }
                    if (ok) {
                        try {
                            final Item parent = Items.getItem(parentId);
                            parent.dropItem(item.getWurmId(), false);
                        }
                        catch (NoSuchItemException nsi) {
                            if (!coin) {
                                TradingWindow.logger.log(Level.WARNING, "Parent not found for item " + item.getWurmId());
                            }
                        }
                        if (this.watcher instanceof Player) {
                            inventory.insertItem(item);
                            if (coin && shop != null) {
                                if (!shop.isPersonal() || shop.getOwnerId() == this.watcher.getWurmId()) {
                                    final long v = Economy.getValueFor(item.getTemplateId());
                                    moneyLost += (int)v;
                                }
                                if (shop.getOwnerId() == this.watcher.getWurmId()) {
                                    getLogger(shop.getWurmId()).log(Level.INFO, this.watcher.getName() + " received " + MaterialUtilities.getMaterialString(item.getMaterial()) + " " + item.getName() + ", id: " + item.getWurmId() + ", QL: " + item.getQualityLevel());
                                }
                            }
                            else if (shop != null) {
                                if (!shop.isPersonal()) {
                                    int deminc = 1;
                                    if (item.isCombine()) {
                                        deminc = Math.max(1, item.getWeightGrams() / item.getTemplate().getWeightGrams());
                                    }
                                    Economy.getEconomy().addItemSoldByTraders(item.getTemplateId());
                                    shop.getLocalSupplyDemand().addItemSold(item.getTemplateId(), deminc);
                                }
                                else {
                                    getLogger(shop.getWurmId()).log(Level.INFO, this.watcher.getName() + " received " + MaterialUtilities.getMaterialString(item.getMaterial()) + " " + item.getName() + ", id: " + item.getWurmId() + ", QL: " + item.getQualityLevel());
                                }
                            }
                        }
                        else if (coin) {
                            if (shop != null) {
                                if (shop.isPersonal()) {
                                    getLogger(shop.getWurmId()).log(Level.INFO, this.watcher.getName() + " received " + MaterialUtilities.getMaterialString(item.getMaterial()) + " " + item.getName() + ", id: " + item.getWurmId() + ", QL: " + item.getQualityLevel());
                                    if (this.windowowner.getWurmId() == shop.getOwnerId()) {
                                        inventory.insertItem(item);
                                        moneyAdded += Economy.getValueFor(item.getTemplateId());
                                    }
                                    else {
                                        Economy.getEconomy().returnCoin(item, "PersonalShop");
                                    }
                                }
                                else {
                                    Economy.getEconomy().returnCoin(item, "TraderShop");
                                    final long v = Economy.getValueFor(item.getTemplateId());
                                    moneyAdded += (int)v;
                                }
                            }
                            else {
                                TradingWindow.logger.log(Level.WARNING, this.windowowner.getName() + ", id=" + this.windowowner.getWurmId() + " failed to locate TraderMoney.");
                            }
                        }
                        else {
                            inventory.insertItem(item);
                            if (shop != null) {
                                if (!shop.isPersonal()) {
                                    item.setPrice(0);
                                    int deminc = 1;
                                    if (item.isCombine()) {
                                        deminc = Math.max(1, item.getWeightGrams() / item.getTemplate().getWeightGrams());
                                    }
                                    Economy.getEconomy().addItemBoughtByTraders(item.getTemplateId());
                                    shop.getLocalSupplyDemand().addItemPurchased(item.getTemplateId(), deminc);
                                    if (item.isVillageDeed() || item.isHomesteadDeed()) {
                                        final Shop kingsMoney = Economy.getEconomy().getKingsShop();
                                        kingsMoney.setMoney(kingsMoney.getMoney() - item.getValue());
                                        item.setAuxData((byte)0);
                                        TradingWindow.logger.log(Level.INFO, "King bought a deed for " + item.getValue() + " and now has " + kingsMoney.getMoney());
                                        final long v2 = Economy.getValueFor(item.getTemplateId());
                                        moneyLost -= (int)v2;
                                    }
                                }
                                else {
                                    getLogger(shop.getWurmId()).log(Level.INFO, this.watcher.getName() + " received " + MaterialUtilities.getMaterialString(item.getMaterial()) + " " + item.getName() + ", id: " + item.getWurmId() + ", QL: " + item.getQualityLevel());
                                }
                            }
                        }
                    }
                    if (!(this.windowowner instanceof Player) && !coin && ok && !item.isPurchased() && !shop.isPersonal()) {
                        try {
                            if (this.windowowner.getCarriedItem(item.getTemplateId()) == null) {
                                byte material = item.getMaterial();
                                if (item.isFullprice() || item.isNoSellback()) {
                                    material = item.getTemplate().getMaterial();
                                }
                                final Item newItem = ItemFactory.createItem(item.getTemplateId(), item.getQualityLevel(), material, (byte)0, null);
                                ownInventory.insertItem(newItem);
                            }
                            if (item.isVillageDeed() || item.isHomesteadDeed()) {
                                final Shop kingsMoney2 = Economy.getEconomy().getKingsShop();
                                kingsMoney2.setMoney(kingsMoney2.getMoney() + item.getValue() / 2);
                                item.setLeftAuxData(0);
                                Economy.getEconomy().addItemSoldByTraders(item.getName(), item.getValue(), this.windowowner.getName(), this.watcher.getName(), item.getTemplateId());
                                moneyAdded -= item.getValue();
                            }
                            if (item.isNoSellback() || item.getTemplateId() == 682) {
                                final Shop kingsMoney2 = Economy.getEconomy().getKingsShop();
                                kingsMoney2.setMoney(kingsMoney2.getMoney() + item.getValue() / 4);
                                Economy.getEconomy().addItemSoldByTraders(item.getName(), item.getValue(), this.windowowner.getName(), this.watcher.getName(), item.getTemplateId());
                                moneyAdded -= item.getValue() * 3 / 4;
                            }
                            if (item.getTemplateId() == 300 || item.getTemplateId() == 299) {
                                final Shop kingsMoney2 = Economy.getEconomy().getKingsShop();
                                kingsMoney2.setMoney(kingsMoney2.getMoney() + item.getValue() / 4);
                                Economy.getEconomy().addItemSoldByTraders(item.getName(), item.getValue(), this.windowowner.getName(), this.watcher.getName(), item.getTemplateId());
                                moneyAdded -= item.getValue() * 3 / 4;
                            }
                            else if (item.getTemplateId() == 1129) {
                                final Shop kingsMoney2 = Economy.getEconomy().getKingsShop();
                                kingsMoney2.setMoney(kingsMoney2.getMoney() + item.getValue() / 2);
                                Economy.getEconomy().addItemSoldByTraders(item.getName(), item.getValue(), this.windowowner.getName(), this.watcher.getName(), item.getTemplateId());
                                moneyAdded -= item.getValue();
                            }
                        }
                        catch (NoSuchTemplateException nst) {
                            TradingWindow.logger.log(Level.WARNING, nst.getMessage(), nst);
                        }
                        catch (FailedException fe) {
                            TradingWindow.logger.log(Level.WARNING, fe.getMessage(), fe);
                        }
                    }
                    if (!ok) {
                        errors = true;
                    }
                }
            }
            if (!errors) {
                this.windowowner.getCommunicator().sendNormalServerMessage("The trade was completed successfully.");
            }
            else {
                this.windowowner.getCommunicator().sendNormalServerMessage("The trade was completed, not all items were traded.");
            }
            if (shop != null) {
                final int diff = moneyAdded - moneyLost;
                if (shop.isPersonal()) {
                    if (diff != 0) {
                        shop.setMoney(shop.getMoney() + diff);
                    }
                    final long moneyToAdd = (long)(this.trade.getMoneyAdded() * 0.9f);
                    final long kadd = this.trade.getMoneyAdded() - moneyToAdd;
                    if (moneyToAdd != 0L) {
                        if (this.windowowner.isNpcTrader()) {
                            final Item[] coins;
                            final Item[] c = coins = Economy.getEconomy().getCoinsFor(moneyToAdd);
                            for (final Item lElement : coins) {
                                ownInventory.insertItem(lElement, true);
                            }
                            shop.setMoney(shop.getMoney() + moneyToAdd);
                            if (this.watcher.getWurmId() != shop.getOwnerId()) {
                                if (kadd != 0L) {
                                    final Shop kingsMoney3 = Economy.getEconomy().getKingsShop();
                                    kingsMoney3.setMoney(kingsMoney3.getMoney() + kadd);
                                    shop.addMoneySpent(kadd);
                                }
                                shop.addMoneyEarned(moneyToAdd);
                            }
                        }
                        shop.setLastPolled(System.currentTimeMillis());
                    }
                }
                else {
                    if (diff >= 1000000) {
                        this.watcher.achievement(132);
                    }
                    this.trade.addShopDiff(diff);
                }
            }
        }
        else {
            this.windowowner.getCommunicator().sendAlertServerMessage("There is a bug in the trade system. This shouldn't happen. Please report.");
            this.watcher.getCommunicator().sendAlertServerMessage("There is a bug in the trade system. This shouldn't happen. Please report.");
            TradingWindow.logger.log(Level.WARNING, "Inconsistency! This is offer window number " + this.wurmId + ". Traders are " + this.watcher.getName() + ", " + this.windowowner.getName());
        }
    }
    
    void endTrade() {
        if (this.items != null) {
            final Item[] array;
            final Item[] its = array = this.items.toArray(new Item[this.items.size()]);
            for (final Item lIt : array) {
                this.removeExistingContainedItems(lIt);
                this.items.remove(lIt);
                this.removeFromTrade(lIt, true);
            }
        }
        this.items = null;
    }
    
    static {
        logger = Logger.getLogger(TradingWindow.class.getName());
        loggers = new HashMap<String, Logger>();
    }
}
