// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.NoSuchItemException;
import java.util.LinkedList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.Iterator;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.TradingWindow;
import com.wurmonline.server.Server;
import com.wurmonline.server.Items;
import com.wurmonline.server.Features;
import java.util.logging.Level;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import java.util.HashSet;
import java.util.HashMap;
import com.wurmonline.server.economy.Shop;
import java.util.List;
import com.wurmonline.server.items.Item;
import java.util.Set;
import java.util.Map;
import com.wurmonline.server.items.Trade;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;

public final class TradeHandler implements MiscConstants, ItemTypes, MonetaryConstants
{
    private static final Logger logger;
    private Creature creature;
    private Trade trade;
    private boolean balanced;
    private boolean waiting;
    private final Map<Integer, Set<Item>> itemMap;
    private final Map<Integer, List<Item>> currentDemandMap;
    private final Map<Integer, Set<Item>> purchaseMap;
    private static int maxPersonalItems;
    private final Shop shop;
    private long pdemand;
    private static final float maxNums = 80.0f;
    private final boolean ownerTrade;
    private final Set<Item> fullPriceItems;
    private boolean hasOtherItems;
    
    TradeHandler(final Creature aCreature, final Trade _trade) {
        this.balanced = false;
        this.waiting = false;
        this.itemMap = new HashMap<Integer, Set<Item>>();
        this.currentDemandMap = new HashMap<Integer, List<Item>>();
        this.purchaseMap = new HashMap<Integer, Set<Item>>();
        this.pdemand = 0L;
        this.fullPriceItems = new HashSet<Item>();
        this.hasOtherItems = false;
        this.creature = aCreature;
        this.trade = _trade;
        this.shop = Economy.getEconomy().getShop(aCreature);
        if (this.shop.isPersonal()) {
            this.ownerTrade = (this.shop.getOwnerId() == this.trade.creatureOne.getWurmId());
            if (this.ownerTrade) {
                this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'Welcome back, " + this.trade.creatureOne.getName() + "!'");
            }
            else {
                this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I will not buy anything, but i can offer these things.'");
            }
        }
        else {
            this.ownerTrade = false;
            if (this.shop.getMoney() <= 1L) {
                if (this.trade.creatureOne.getPower() >= 3) {
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I only have " + this.shop.getMoney() + " and can't buy anything right now.'");
                }
                else {
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I am a bit low on money and can't buy anything right now.'");
                }
            }
            else if (this.shop.getMoney() < 100L) {
                if (this.trade.creatureOne.getPower() >= 3) {
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I only have " + new Change(this.shop.getMoney()).getChangeShortString() + ".'");
                }
                else {
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I am a bit low on money, but let's see what you have.'");
                }
            }
            else if (this.trade.creatureOne.getPower() >= 3) {
                this.trade.creatureOne.getCommunicator().sendSafeServerMessage(aCreature.getName() + " says, 'I have " + new Change(this.shop.getMoney()).getChangeShortString() + ".'");
            }
        }
    }
    
    void end() {
        this.creature = null;
        this.trade = null;
    }
    
    void addToInventory(final Item item, final long inventoryWindow) {
        if (this.trade != null) {
            if (inventoryWindow == 2L) {
                this.tradeChanged();
                if (TradeHandler.logger.isLoggable(Level.FINEST) && item != null) {
                    TradeHandler.logger.finest("Added " + item.getName() + " to his offer window.");
                }
            }
            else if (inventoryWindow == 1L) {
                if (TradeHandler.logger.isLoggable(Level.FINEST) && item != null) {
                    TradeHandler.logger.finest("Added " + item.getName() + " to my offer window.");
                }
            }
            else if (inventoryWindow == 3L) {
                if (TradeHandler.logger.isLoggable(Level.FINEST) && item != null) {
                    TradeHandler.logger.finest("Added " + item.getName() + " to his request window.");
                }
            }
            else if (inventoryWindow == 4L && TradeHandler.logger.isLoggable(Level.FINEST) && item != null) {
                TradeHandler.logger.finest("Added " + item.getName() + " to my request window.");
            }
        }
    }
    
    void tradeChanged() {
        this.balanced = false;
        this.waiting = false;
    }
    
    void addItemsToTrade() {
        if (this.trade != null) {
            boolean foundDeclaration = false;
            boolean foundWagonerContract = false;
            final Set<Item> ite = this.creature.getInventory().getItems();
            final Item[] itarr = ite.toArray(new Item[ite.size()]);
            final TradingWindow myOffers = this.trade.getTradingWindow(1L);
            int templateId = -10;
            myOffers.startReceivingItems();
            for (int x = 0; x < itarr.length; ++x) {
                templateId = itarr[x].getTemplateId();
                Set<Item> its = this.itemMap.get(templateId);
                if (its == null) {
                    its = new HashSet<Item>();
                }
                its.add(itarr[x]);
                if (this.shop.isPersonal()) {
                    if (this.ownerTrade) {
                        myOffers.addItem(itarr[x]);
                    }
                    else if (!itarr[x].isCoin()) {
                        myOffers.addItem(itarr[x]);
                    }
                }
                else {
                    if (itarr[x].getTemplateId() == 843) {
                        if (!Features.Feature.NAMECHANGE.isEnabled()) {
                            its.remove(itarr[x]);
                            Items.destroyItem(itarr[x].getWurmId());
                            continue;
                        }
                        foundDeclaration = true;
                    }
                    if (itarr[x].getTemplateId() == 1129) {
                        if (!Features.Feature.WAGONER.isEnabled()) {
                            its.remove(itarr[x]);
                            Items.destroyItem(itarr[x].getWurmId());
                            continue;
                        }
                        foundWagonerContract = true;
                    }
                    if (its.size() < 10 && itarr[x].getLockId() == -10L) {
                        myOffers.addItem(itarr[x]);
                    }
                    else if (its.size() > 50) {
                        its.remove(itarr[x]);
                        Items.destroyItem(itarr[x].getWurmId());
                    }
                }
                this.itemMap.put(templateId, its);
            }
            if (!this.shop.isPersonal()) {
                boolean newMerchandise = false;
                if (Features.Feature.NAMECHANGE.isEnabled() && !foundDeclaration) {
                    try {
                        final Item inventory = this.creature.getInventory();
                        final Item item = Creature.createItem(843, 60 + Server.rand.nextInt(40));
                        inventory.insertItem(item);
                        newMerchandise = true;
                    }
                    catch (Exception ex) {
                        TradeHandler.logger.log(Level.INFO, "Failed to create name change cert for creature.", ex);
                    }
                }
                if (Features.Feature.WAGONER.isEnabled() && !foundWagonerContract) {
                    try {
                        final Item inventory = this.creature.getInventory();
                        final Item item = Creature.createItem(1129, 60 + Server.rand.nextInt(40));
                        inventory.insertItem(item);
                        newMerchandise = true;
                    }
                    catch (Exception ex) {
                        TradeHandler.logger.log(Level.INFO, "Failed to create wagoner contract for creature.", ex);
                    }
                }
                if (newMerchandise) {
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(this.creature.getName() + " says, 'Oh, I forgot I have some new merchandise. Let us trade again and I will show them to you.'");
                }
            }
            myOffers.stopReceivingItems();
        }
    }
    
    public int getTraderSellPriceForItem(final Item item, final TradingWindow window) {
        if (item.isFullprice()) {
            return item.getValue();
        }
        if (this.shop.isPersonal() && item.getPrice() > 0) {
            return item.getPrice();
        }
        int numberSold = 1;
        if (window == this.trade.getCreatureOneRequestWindow() && (!this.shop.isPersonal() || this.shop.usesLocalPrice())) {
            if (item.isCombine()) {
                final ItemTemplate temp = item.getTemplate();
                numberSold = Math.max(1, item.getWeightGrams() / temp.getWeightGrams());
            }
            final Item[] whatPlayerWants = this.trade.getCreatureOneRequestWindow().getItems();
            for (int x = 0; x < whatPlayerWants.length; ++x) {
                if (whatPlayerWants[x] != item && whatPlayerWants[x].getTemplateId() == item.getTemplateId()) {
                    if (whatPlayerWants[x].isCombine()) {
                        final ItemTemplate temp2 = whatPlayerWants[x].getTemplate();
                        numberSold += Math.max(1, whatPlayerWants[x].getWeightGrams() / temp2.getWeightGrams());
                    }
                    else {
                        ++numberSold;
                    }
                }
            }
        }
        final double localPrice = this.shop.getLocalTraderSellPrice(item, 100, numberSold);
        if (TradeHandler.logger.isLoggable(Level.FINEST)) {
            TradeHandler.logger.finest("localSellPrice for " + item.getName() + "=" + localPrice + " numberSold=" + numberSold);
        }
        return (int)Math.max(2.0, localPrice * this.shop.getPriceModifier());
    }
    
    public int getTraderBuyPriceForItem(final Item item) {
        if (item.isFullprice()) {
            return item.getValue();
        }
        int price = 1;
        final List<Item> itlist = this.currentDemandMap.get(item.getTemplateId());
        int extra = 1;
        if (itlist == null) {
            if (TradeHandler.logger.isLoggable(Level.FINEST)) {
                TradeHandler.logger.finest("Weird. We're trading items which don't exist.");
            }
            extra = 1;
        }
        else if (item.isCombine()) {
            final ItemTemplate temp = item.getTemplate();
            for (final Item i : itlist) {
                extra += Math.max(1, i.getWeightGrams() / temp.getWeightGrams());
            }
        }
        else {
            extra += itlist.size();
        }
        price = (int)this.shop.getLocalTraderBuyPrice(item, 1, extra);
        if (TradeHandler.logger.isLoggable(Level.FINEST)) {
            TradeHandler.logger.finest("localBuyPrice for " + item.getName() + "=" + price + " extra=" + extra + " price for extra+1=" + (int)this.shop.getLocalTraderBuyPrice(item, 1, extra + 1));
        }
        return Math.max(0, price);
    }
    
    private long getDiff() {
        if (this.shop.isPersonal() && this.ownerTrade) {
            return 0L;
        }
        final Item[] whatPlayerWants = this.trade.getCreatureOneRequestWindow().getItems();
        final Item[] whatIWant = this.trade.getCreatureTwoRequestWindow().getItems();
        this.pdemand = 0L;
        long mydemand = 0L;
        int templateId = -10;
        this.fullPriceItems.clear();
        this.hasOtherItems = false;
        this.purchaseMap.clear();
        for (int x = 0; x < whatPlayerWants.length; ++x) {
            if (whatPlayerWants[x].isFullprice()) {
                this.pdemand += whatPlayerWants[x].getValue();
                this.fullPriceItems.add(whatPlayerWants[x]);
            }
            else {
                double localPrice = 2.0;
                if (this.shop.isPersonal() && whatPlayerWants[x].getPrice() > 0) {
                    localPrice = whatPlayerWants[x].getPrice();
                    this.pdemand += (long)localPrice;
                }
                else {
                    templateId = whatPlayerWants[x].getTemplateId();
                    int numberSold = 0;
                    try {
                        if (!this.shop.isPersonal() || this.shop.usesLocalPrice()) {
                            final int tid = whatPlayerWants[x].getTemplateId();
                            Set<Item> itlist = this.purchaseMap.get(tid);
                            if (itlist == null) {
                                itlist = new HashSet<Item>();
                            }
                            itlist.add(whatPlayerWants[x]);
                            this.purchaseMap.put(tid, itlist);
                            if (whatPlayerWants[x].isCombine()) {
                                final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(templateId);
                                for (final Item i : itlist) {
                                    numberSold += Math.max(1, i.getWeightGrams() / temp.getWeightGrams());
                                }
                            }
                            else {
                                numberSold = itlist.size();
                            }
                        }
                        localPrice = this.shop.getLocalTraderSellPrice(whatPlayerWants[x], 100, numberSold);
                        if (TradeHandler.logger.isLoggable(Level.FINEST)) {
                            TradeHandler.logger.finest("LocalSellPrice for " + whatPlayerWants[x].getName() + "=" + localPrice + " mod=" + this.shop.getPriceModifier() + " sum=" + Math.max(2.0, localPrice * this.shop.getPriceModifier()));
                        }
                        this.pdemand += (long)Math.max(2.0, localPrice * this.shop.getPriceModifier());
                    }
                    catch (NoSuchTemplateException nst) {
                        TradeHandler.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
        }
        this.purchaseMap.clear();
        for (int x = 0; x < whatIWant.length; ++x) {
            if (whatIWant[x].isFullprice()) {
                mydemand += whatIWant[x].getValue();
            }
            else {
                this.hasOtherItems = true;
                if (this.fullPriceItems.isEmpty()) {
                    long price = 1L;
                    final int tid2 = whatIWant[x].getTemplateId();
                    Set<Item> itlist2 = this.purchaseMap.get(tid2);
                    if (itlist2 == null) {
                        itlist2 = new HashSet<Item>();
                    }
                    itlist2.add(whatIWant[x]);
                    this.purchaseMap.put(tid2, itlist2);
                    int extra = 0;
                    if (whatIWant[x].isCombine()) {
                        try {
                            final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(whatIWant[x].getTemplateId());
                            for (final Item i : itlist2) {
                                extra += Math.max(1, i.getWeightGrams() / temp.getWeightGrams());
                            }
                        }
                        catch (NoSuchTemplateException nst2) {
                            TradeHandler.logger.log(Level.WARNING, nst2.getMessage(), nst2);
                        }
                    }
                    else {
                        extra = itlist2.size();
                    }
                    price = this.shop.getLocalTraderBuyPrice(whatIWant[x], 1, extra);
                    mydemand += Math.max(0L, price);
                }
            }
        }
        final long diff = this.pdemand - mydemand;
        return diff;
    }
    
    public static final int getMaxNumPersonalItems() {
        return TradeHandler.maxPersonalItems;
    }
    
    private void suckInterestingItems() {
        final TradingWindow currWin = this.trade.getTradingWindow(2L);
        final TradingWindow targetWin = this.trade.getTradingWindow(4L);
        final Item[] offItems = currWin.getItems();
        final Item[] setItems = targetWin.getItems();
        if (!this.shop.isPersonal()) {
            this.currentDemandMap.clear();
            for (int x = 0; x < setItems.length; ++x) {
                final int templateId = setItems[x].getTemplateId();
                List<Item> itlist = this.currentDemandMap.get(templateId);
                if (itlist == null) {
                    itlist = new LinkedList<Item>();
                }
                itlist.add(setItems[x]);
                this.currentDemandMap.put(templateId, itlist);
            }
            int templateId2 = -10;
            targetWin.startReceivingItems();
            for (int x2 = 0; x2 < offItems.length; ++x2) {
                if (!offItems[x2].isArtifact() && offItems[x2].isPurchased() && offItems[x2].getLockId() == -10L) {
                    Item parent = offItems[x2];
                    try {
                        parent = offItems[x2].getParent();
                    }
                    catch (NoSuchItemException ex) {}
                    if (offItems[x2] == parent || parent.isViewableBy(this.creature)) {
                        if (offItems[x2].isHollow() && !offItems[x2].isEmpty(true)) {
                            this.trade.creatureOne.getCommunicator().sendSafeServerMessage(this.creature.getName() + " says, 'Please empty the " + offItems[x2].getName() + " first.'");
                        }
                        else {
                            templateId2 = offItems[x2].getTemplateId();
                            List<Item> itlist2 = this.currentDemandMap.get(templateId2);
                            if (itlist2 == null) {
                                itlist2 = new LinkedList<Item>();
                            }
                            if (itlist2.size() < 80.0f) {
                                currWin.removeItem(offItems[x2]);
                                targetWin.addItem(offItems[x2]);
                                itlist2.add(offItems[x2]);
                                this.currentDemandMap.put(templateId2, itlist2);
                            }
                        }
                    }
                }
                else if ((offItems[x2].isHomesteadDeed() || offItems[x2].isVillageDeed()) && offItems[x2].getData2() <= 0) {
                    templateId2 = offItems[x2].getTemplateId();
                    List<Item> itlist = this.currentDemandMap.get(templateId2);
                    if (itlist == null) {
                        itlist = new LinkedList<Item>();
                    }
                    currWin.removeItem(offItems[x2]);
                    targetWin.addItem(offItems[x2]);
                    itlist.add(offItems[x2]);
                    this.currentDemandMap.put(templateId2, itlist);
                }
            }
            targetWin.stopReceivingItems();
        }
        else if (this.ownerTrade) {
            final TradingWindow myOffers = this.trade.getTradingWindow(1L);
            final Item[] currItems = myOffers.getItems();
            int size = 0;
            for (int c = 0; c < currItems.length; ++c) {
                if (!currItems[c].isCoin()) {
                    ++size;
                }
            }
            size += setItems.length;
            if (size > TradeHandler.maxPersonalItems) {
                this.trade.creatureOne.getCommunicator().sendNormalServerMessage(this.creature.getName() + " says, 'I cannot add more items to my stock right now.'");
            }
            else {
                final TradingWindow hisReq = this.trade.getTradingWindow(3L);
                final Item[] reqItems = hisReq.getItems();
                for (int c2 = 0; c2 < reqItems.length; ++c2) {
                    if (!reqItems[c2].isCoin()) {
                        ++size;
                    }
                }
                if (size > TradeHandler.maxPersonalItems) {
                    this.trade.creatureOne.getCommunicator().sendNormalServerMessage(this.creature.getName() + " says, 'I cannot add more items to my stock right now.'");
                }
                else {
                    targetWin.startReceivingItems();
                    for (int x3 = 0; x3 < offItems.length; ++x3) {
                        if (offItems[x3].getTemplateId() != 272 && offItems[x3].getTemplateId() != 781 && !offItems[x3].isArtifact() && !offItems[x3].isRoyal() && ((!offItems[x3].isVillageDeed() && !offItems[x3].isHomesteadDeed()) || !offItems[x3].hasData()) && (offItems[x3].getTemplateId() != 300 || offItems[x3].getData2() == -1)) {
                            if (size > TradeHandler.maxPersonalItems) {
                                if (offItems[x3].isCoin()) {
                                    currWin.removeItem(offItems[x3]);
                                    targetWin.addItem(offItems[x3]);
                                }
                            }
                            else if ((offItems[x3].isLockable() && offItems[x3].isLocked()) || (offItems[x3].isHollow() && !offItems[x3].isEmpty(true) && !offItems[x3].isSealedByPlayer())) {
                                if (offItems[x3].isLockable() && offItems[x3].isLocked()) {
                                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(this.creature.getName() + " says, 'I don't accept locked items any more. Sorry for the inconvenience.'");
                                }
                                else {
                                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(this.creature.getName() + " says, 'Please empty the " + offItems[x3].getName() + " first.'");
                                }
                            }
                            else {
                                currWin.removeItem(offItems[x3]);
                                targetWin.addItem(offItems[x3]);
                                ++size;
                            }
                        }
                    }
                    targetWin.stopReceivingItems();
                }
            }
        }
        else {
            targetWin.startReceivingItems();
            for (int x = 0; x < offItems.length; ++x) {
                if (offItems[x].isCoin()) {
                    Item parent2 = offItems[x];
                    try {
                        parent2 = offItems[x].getParent();
                    }
                    catch (NoSuchItemException ex2) {}
                    if (offItems[x] == parent2 || parent2.isViewableBy(this.creature)) {
                        currWin.removeItem(offItems[x]);
                        targetWin.addItem(offItems[x]);
                    }
                }
            }
            targetWin.stopReceivingItems();
        }
    }
    
    void balance() {
        if (!this.balanced) {
            if (this.ownerTrade) {
                this.suckInterestingItems();
                this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                this.balanced = true;
            }
            else if (this.shop.isPersonal() && !this.waiting) {
                this.suckInterestingItems();
                this.removeCoins(this.trade.getCreatureOneRequestWindow().getItems());
                final long diff = this.getDiff();
                if (diff > 0L) {
                    this.waiting = true;
                    final Change change = new Change(diff);
                    String toSend = this.creature.getName() + " demands ";
                    toSend += change.getChangeString();
                    toSend += " coins to make the trade.";
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(toSend);
                }
                else if (diff < 0L) {
                    final Item[] mon = Economy.getEconomy().getCoinsFor(Math.abs(diff));
                    this.trade.getCreatureOneRequestWindow().startReceivingItems();
                    for (int x = 0; x < mon.length; ++x) {
                        this.trade.getCreatureOneRequestWindow().addItem(mon[x]);
                    }
                    this.trade.getCreatureOneRequestWindow().stopReceivingItems();
                    this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                    this.trade.setMoneyAdded(this.pdemand);
                    this.balanced = true;
                }
                else {
                    this.trade.setMoneyAdded(this.pdemand);
                    this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                    this.balanced = true;
                }
            }
            else if (!this.waiting) {
                this.suckInterestingItems();
                this.removeCoins(this.trade.getCreatureOneRequestWindow().getItems());
                final long diff = this.getDiff();
                if (TradeHandler.logger.isLoggable(Level.FINEST)) {
                    TradeHandler.logger.finest("diff is " + diff);
                }
                if (!this.fullPriceItems.isEmpty() && this.hasOtherItems) {
                    for (final Item lItem : this.fullPriceItems) {
                        this.trade.creatureOne.getCommunicator().sendSafeServerMessage(this.creature.getName() + " says, 'Sorry, " + this.trade.creatureOne.getName() + ". I must charge full price in coin value for the " + lItem.getName() + ".'");
                    }
                    this.waiting = true;
                }
                else if (diff > 0L) {
                    this.waiting = true;
                    final Change change = new Change(diff);
                    String toSend = this.creature.getName() + " demands ";
                    toSend += change.getChangeString();
                    toSend += " coins to make the trade.";
                    this.trade.creatureOne.getCommunicator().sendSafeServerMessage(toSend);
                }
                else if (diff < 0L) {
                    if (Math.abs(diff) > this.shop.getMoney()) {
                        for (final Item i : this.trade.getCreatureTwoRequestWindow().getAllItems()) {
                            if (!i.isCoin()) {
                                final String toSend2 = this.creature.getName() + " says, 'I am low on cash and can not purchase those items.'";
                                this.waiting = true;
                                this.trade.creatureOne.getCommunicator().sendSafeServerMessage(toSend2);
                                return;
                            }
                        }
                        final Item[] mon = Economy.getEconomy().getCoinsFor(Math.abs(diff));
                        this.trade.getCreatureOneRequestWindow().startReceivingItems();
                        for (int x = 0; x < mon.length; ++x) {
                            this.trade.getCreatureOneRequestWindow().addItem(mon[x]);
                        }
                        this.trade.getCreatureOneRequestWindow().stopReceivingItems();
                        this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                        this.balanced = true;
                    }
                    else {
                        final Item[] mon = Economy.getEconomy().getCoinsFor(Math.abs(diff));
                        this.trade.getCreatureOneRequestWindow().startReceivingItems();
                        for (int x = 0; x < mon.length; ++x) {
                            this.trade.getCreatureOneRequestWindow().addItem(mon[x]);
                        }
                        this.trade.getCreatureOneRequestWindow().stopReceivingItems();
                        this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                        this.balanced = true;
                    }
                }
                else {
                    this.trade.setSatisfied(this.creature, true, this.trade.getCurrentCounter());
                    this.balanced = true;
                }
            }
        }
    }
    
    private boolean removeCoins(final Item[] items) {
        boolean foundCoins = false;
        for (int x = 0; x < items.length; ++x) {
            if (items[x].isCoin()) {
                foundCoins = true;
                this.trade.getCreatureOneRequestWindow().removeItem(items[x]);
            }
        }
        return foundCoins;
    }
    
    static {
        logger = Logger.getLogger(TradeHandler.class.getName());
        TradeHandler.maxPersonalItems = 50;
    }
}
