// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.util.HashMap;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import com.wurmonline.server.items.TradingWindow;
import java.io.IOException;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import java.util.LinkedList;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public abstract class Economy implements MonetaryConstants, MiscConstants, TimeConstants
{
    static long goldCoins;
    static long lastPolledTraders;
    static long copperCoins;
    static long silverCoins;
    static long ironCoins;
    private static final LinkedList<Item> goldOnes;
    private static final LinkedList<Item> goldFives;
    private static final LinkedList<Item> goldTwentys;
    private static final LinkedList<Item> silverOnes;
    private static final LinkedList<Item> silverFives;
    private static final LinkedList<Item> silverTwentys;
    private static final LinkedList<Item> copperOnes;
    private static final LinkedList<Item> copperFives;
    private static final LinkedList<Item> copperTwentys;
    private static final LinkedList<Item> ironOnes;
    private static final LinkedList<Item> ironFives;
    private static final LinkedList<Item> ironTwentys;
    private static final Logger logger;
    private static final Logger moneylogger;
    final int id;
    private static final Map<Integer, SupplyDemand> supplyDemand;
    @GuardedBy("SHOPS_RW_LOCK")
    private static final Map<Long, Shop> shops;
    private static final ReentrantReadWriteLock SHOPS_RW_LOCK;
    private static Economy economy;
    private static final int minTraderDistance = 64;
    
    public static Economy getEconomy() {
        if (Economy.economy == null) {
            final long start = System.nanoTime();
            try {
                Economy.economy = new DbEconomy(Servers.localServer.id);
            }
            catch (IOException iox) {
                Economy.logger.log(Level.WARNING, "Failed to create economy: " + iox.getMessage(), iox);
                Server.getInstance().shutDown();
            }
            Economy.logger.log(Level.INFO, "Loading economy took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        }
        return Economy.economy;
    }
    
    Economy(final int aEconomy) throws IOException {
        Economy.goldCoins = 0L;
        Economy.copperCoins = 0L;
        Economy.silverCoins = 0L;
        Economy.ironCoins = 0L;
        this.id = aEconomy;
        this.initialize();
    }
    
    public long getGold() {
        return Economy.goldCoins;
    }
    
    public long getSilver() {
        return Economy.silverCoins;
    }
    
    public long getCopper() {
        return Economy.copperCoins;
    }
    
    public long getIron() {
        return Economy.ironCoins;
    }
    
    public static final int getValueFor(final int coinType) {
        switch (coinType) {
            case 50: {
                return 100;
            }
            case 54: {
                return 500;
            }
            case 58: {
                return 2000;
            }
            case 51: {
                return 1;
            }
            case 55: {
                return 5;
            }
            case 59: {
                return 20;
            }
            case 52: {
                return 10000;
            }
            case 56: {
                return 50000;
            }
            case 60: {
                return 200000;
            }
            case 53: {
                return 1000000;
            }
            case 57: {
                return 5000000;
            }
            case 61: {
                return 20000000;
            }
            default: {
                return 0;
            }
        }
    }
    
    LinkedList<Item> getListForCointype(final int type) {
        switch (type) {
            case 50: {
                return Economy.copperOnes;
            }
            case 54: {
                return Economy.copperFives;
            }
            case 58: {
                return Economy.copperTwentys;
            }
            case 51: {
                return Economy.ironOnes;
            }
            case 55: {
                return Economy.ironFives;
            }
            case 59: {
                return Economy.ironTwentys;
            }
            case 52: {
                return Economy.silverOnes;
            }
            case 56: {
                return Economy.silverFives;
            }
            case 60: {
                return Economy.silverTwentys;
            }
            case 53: {
                return Economy.goldOnes;
            }
            case 57: {
                return Economy.goldFives;
            }
            case 61: {
                return Economy.goldTwentys;
            }
            default: {
                Economy.logger.log(Level.WARNING, "Found no list for type " + type);
                return new LinkedList<Item>();
            }
        }
    }
    
    public void returnCoin(final Item coin, final String message) {
        this.returnCoin(coin, message, false);
    }
    
    public void returnCoin(final Item coin, final String message, final boolean dontLog) {
        if (!dontLog) {
            this.transaction(coin.getWurmId(), coin.getOwnerId(), this.id, message, coin.getValue());
        }
        coin.setTradeWindow(null);
        coin.setOwner(-10L, false);
        coin.setLastOwnerId(-10L);
        coin.setZoneId(-10, true);
        coin.setParentId(-10L, true);
        coin.setRarity((byte)0);
        coin.setBanked(true);
        final int templateid = coin.getTemplateId();
        final List<Item> toAdd = this.getListForCointype(templateid);
        toAdd.add(coin);
    }
    
    public Item[] getCoinsFor(final long value) {
        if (value > 0L) {
            try {
                if (value >= 1000000L) {
                    return this.getGoldTwentyCoinsFor(value, new HashSet<Item>());
                }
                if (value >= 10000L) {
                    return this.getSilverTwentyCoinsFor(value, new HashSet<Item>());
                }
                if (value >= 100L) {
                    return this.getCopperTwentyCoinsFor(value, new HashSet<Item>());
                }
                return this.getIronTwentyCoinsFor(value, new HashSet<Item>());
            }
            catch (FailedException fe) {
                Economy.logger.log(Level.WARNING, "Failed to create coins: " + fe.getMessage(), fe);
            }
            catch (NoSuchTemplateException nst) {
                Economy.logger.log(Level.WARNING, "Failed to create coins: " + nst.getMessage(), nst);
            }
        }
        return new Item[0];
    }
    
    private Item[] getGoldTwentyCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 20000000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.goldTwentys.size() > 0) {
                    coin = Economy.goldTwentys.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(61, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedGold(Economy.goldCoins += 20L);
                    Economy.logger.log(Level.INFO, "CREATING COIN GOLD20 " + coin.getWurmId(), new Exception());
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 20000000L;
            }
        }
        return this.getGoldFiveCoinsFor(value, items);
    }
    
    public Shop[] getShops() {
        Economy.SHOPS_RW_LOCK.readLock().lock();
        try {
            return Economy.shops.values().toArray(new Shop[Economy.shops.size()]);
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
    }
    
    public static Shop[] getTraders() {
        final Map<Long, Shop> traders = new HashMap<Long, Shop>();
        Economy.SHOPS_RW_LOCK.readLock().lock();
        try {
            for (final Shop s : Economy.shops.values()) {
                if (!s.isPersonal() && s.getWurmId() != 0L) {
                    traders.put(s.getWurmId(), s);
                }
            }
            return traders.values().toArray(new Shop[traders.size()]);
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
    }
    
    public long getShopMoney() {
        long toRet = 0L;
        final Shop[] shops;
        final Shop[] lShops = shops = this.getShops();
        for (final Shop lLShop : shops) {
            if (lLShop.getMoney() > 0L) {
                toRet += lLShop.getMoney();
            }
        }
        return toRet;
    }
    
    public void pollTraderEarnings() {
        if (System.currentTimeMillis() - Economy.lastPolledTraders > 2419200000L) {
            this.resetEarnings();
            this.updateLastPolled();
            Economy.logger.log(Level.INFO, "Economy reset earnings.");
        }
    }
    
    private Item[] getGoldFiveCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 5000000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.goldFives.size() > 0) {
                    coin = Economy.goldFives.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(57, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedGold(Economy.goldCoins += 5L);
                    Economy.logger.log(Level.INFO, "CREATING COIN GOLD5 " + coin.getWurmId(), new Exception());
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 5000000L;
            }
        }
        return this.getGoldOneCoinsFor(value, items);
    }
    
    private Item[] getGoldOneCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 1000000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.goldOnes.size() > 0) {
                    coin = Economy.goldOnes.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(53, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedGold(++Economy.goldCoins);
                    Economy.logger.log(Level.INFO, "CREATING COIN GOLD1 " + coin.getWurmId(), new Exception());
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 1000000L;
            }
        }
        return this.getSilverTwentyCoinsFor(value, items);
    }
    
    private Item[] getSilverTwentyCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 200000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.silverTwentys.size() > 0) {
                    coin = Economy.silverTwentys.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(60, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedSilver(Economy.silverCoins += 20L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 200000L;
            }
        }
        return this.getSilverFiveCoinsFor(value, items);
    }
    
    private Item[] getSilverFiveCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 50000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.silverFives.size() > 0) {
                    coin = Economy.silverFives.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(56, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedSilver(Economy.silverCoins += 5L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 50000L;
            }
        }
        return this.getSilverOneCoinsFor(value, items);
    }
    
    private Item[] getSilverOneCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 10000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.silverOnes.size() > 0) {
                    coin = Economy.silverOnes.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(52, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedSilver(++Economy.silverCoins);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 10000L;
            }
        }
        return this.getCopperTwentyCoinsFor(value, items);
    }
    
    private Item[] getCopperTwentyCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 2000L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.copperTwentys.size() > 0) {
                    coin = Economy.copperTwentys.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(58, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedCopper(Economy.copperCoins += 20L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 2000L;
            }
        }
        return this.getCopperFiveCoinsFor(value, items);
    }
    
    private Item[] getCopperFiveCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 500L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.copperFives.size() > 0) {
                    coin = Economy.copperFives.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(54, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedCopper(Economy.copperCoins += 5L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 500L;
            }
        }
        return this.getCopperOneCoinsFor(value, items);
    }
    
    private Item[] getCopperOneCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 100L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.copperOnes.size() > 0) {
                    coin = Economy.copperOnes.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(50, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedCopper(++Economy.copperCoins);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 100L;
            }
        }
        return this.getIronTwentyCoinsFor(value, items);
    }
    
    private Item[] getIronTwentyCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 20L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.ironTwentys.size() > 0) {
                    coin = Economy.ironTwentys.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(59, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedIron(Economy.ironCoins += 20L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 20L;
            }
        }
        return this.getIronFiveCoinsFor(value, items);
    }
    
    private Item[] getIronFiveCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value / 5L;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (long x = 0L; x < num; ++x) {
                Item coin = null;
                if (Economy.ironFives.size() > 0) {
                    coin = Economy.ironFives.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(55, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedIron(Economy.ironCoins += 5L);
                }
                items.add(coin);
                coin.setBanked(false);
                value -= 5L;
            }
        }
        return this.getIronOneCoinsFor(value, items);
    }
    
    private Item[] getIronOneCoinsFor(long value, Set<Item> items) throws FailedException, NoSuchTemplateException {
        if (value > 0L) {
            final long num = value;
            if (items == null) {
                items = new HashSet<Item>();
            }
            for (int x = 0; x < num; ++x) {
                Item coin = null;
                if (Economy.ironOnes.size() > 0) {
                    coin = Economy.ironOnes.removeFirst();
                }
                else {
                    coin = ItemFactory.createItem(51, Server.rand.nextFloat() * 100.0f, null);
                    this.updateCreatedIron(++Economy.ironCoins);
                }
                items.add(coin);
                coin.setBanked(false);
                --value;
            }
        }
        return items.toArray(new Item[items.size()]);
    }
    
    SupplyDemand getSupplyDemand(final int itemTemplateId) {
        SupplyDemand sd = Economy.supplyDemand.get(itemTemplateId);
        if (sd == null) {
            sd = this.createSupplyDemand(itemTemplateId);
        }
        return sd;
    }
    
    public int getPool(final int itemTemplateId) {
        final SupplyDemand sd = this.getSupplyDemand(itemTemplateId);
        return sd.getPool();
    }
    
    public Shop getShop(final Creature creature) {
        return this.getShop(creature, false);
    }
    
    public Shop getShop(final Creature creature, final boolean destroying) {
        Shop tm = null;
        if (creature.isNpcTrader()) {
            Economy.SHOPS_RW_LOCK.readLock().lock();
            try {
                tm = Economy.shops.get(new Long(creature.getWurmId()));
            }
            finally {
                Economy.SHOPS_RW_LOCK.readLock().unlock();
            }
            if (!destroying && tm == null) {
                tm = this.createShop(creature.getWurmId());
            }
        }
        return tm;
    }
    
    public Shop[] getShopsForOwner(final long owner) {
        final Set<Shop> sh = new HashSet<Shop>();
        Economy.SHOPS_RW_LOCK.readLock().lock();
        try {
            for (final Shop shop : Economy.shops.values()) {
                if (shop.getOwnerId() == owner) {
                    sh.add(shop);
                }
            }
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
        return sh.toArray(new Shop[sh.size()]);
    }
    
    public Shop getKingsShop() {
        Economy.SHOPS_RW_LOCK.readLock().lock();
        Shop tm;
        try {
            tm = Economy.shops.get(0L);
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
        if (tm == null) {
            tm = this.createShop(0L);
        }
        return tm;
    }
    
    static void addShop(final Shop tm) {
        Economy.SHOPS_RW_LOCK.writeLock().lock();
        try {
            Economy.shops.put(new Long(tm.getWurmId()), tm);
        }
        finally {
            Economy.SHOPS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public static void deleteShop(final long wurmid) {
        Economy.SHOPS_RW_LOCK.writeLock().lock();
        try {
            final Shop shop = Economy.shops.get(new Long(wurmid));
            if (shop != null) {
                shop.delete();
            }
            Economy.shops.remove(new Long(wurmid));
        }
        finally {
            Economy.SHOPS_RW_LOCK.writeLock().unlock();
        }
    }
    
    static void addSupplyDemand(final SupplyDemand sd) {
        Economy.supplyDemand.put(sd.getId(), sd);
    }
    
    public void addItemSoldByTraders(final int templateId) {
        this.getSupplyDemand(templateId).addItemSoldByTrader();
    }
    
    public abstract void addItemSoldByTraders(final String p0, final long p1, final String p2, final String p3, final int p4);
    
    public void addItemBoughtByTraders(final int templateId) {
        this.getSupplyDemand(templateId).addItemBoughtByTrader();
    }
    
    public Change getChangeFor(final long value) {
        return new Change(value);
    }
    
    public Creature getRandomTrader() {
        Creature toReturn = null;
        Economy.SHOPS_RW_LOCK.readLock().lock();
        try {
            final int size = Economy.shops.size();
            for (final Shop shop : Economy.shops.values()) {
                if (!shop.isPersonal() && shop.getWurmId() > 0L && Server.rand.nextInt(Math.max(2, size / 2)) == 0) {
                    try {
                        toReturn = Creatures.getInstance().getCreature(shop.getWurmId());
                        return toReturn;
                    }
                    catch (NoSuchCreatureException nsc) {
                        Economy.logger.log(Level.WARNING, "Weird, shop with id " + shop.getWurmId() + " has no creature.");
                    }
                }
            }
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
        return toReturn;
    }
    
    public Creature getTraderForZone(final int x, final int y, final boolean surfaced) {
        int sx = 0;
        int sy = 0;
        int ex = 64;
        int ey = 64;
        Creature toReturn = null;
        Economy.SHOPS_RW_LOCK.readLock().lock();
        try {
            for (final Shop shop : Economy.shops.values()) {
                if (!shop.isPersonal() && shop.getWurmId() > 0L) {
                    final VolaTile tile = shop.getPos();
                    if (tile == null) {
                        continue;
                    }
                    sx = tile.getTileX() - 64;
                    sy = tile.getTileY() - 64;
                    ex = tile.getTileX() + 64;
                    ey = tile.getTileY() + 64;
                    if (x >= ex || x <= sx || y >= ey || y <= sy || tile.isOnSurface() != surfaced) {
                        continue;
                    }
                    try {
                        toReturn = Creatures.getInstance().getCreature(shop.getWurmId());
                        return toReturn;
                    }
                    catch (NoSuchCreatureException nsc) {
                        Economy.logger.log(Level.WARNING, "Weird, shop with id " + shop.getWurmId() + " has no creature.");
                    }
                }
            }
        }
        finally {
            Economy.SHOPS_RW_LOCK.readLock().unlock();
        }
        return null;
    }
    
    public abstract void updateCreatedIron(final long p0);
    
    public abstract void updateCreatedSilver(final long p0);
    
    public abstract void updateCreatedCopper(final long p0);
    
    public abstract void updateCreatedGold(final long p0);
    
    abstract void loadSupplyDemand();
    
    abstract void loadShopMoney();
    
    abstract void initialize() throws IOException;
    
    abstract SupplyDemand createSupplyDemand(final int p0);
    
    public abstract Shop createShop(final long p0);
    
    public abstract Shop createShop(final long p0, final long p1);
    
    public abstract void transaction(final long p0, final long p1, final long p2, final String p3, final long p4);
    
    public abstract void updateLastPolled();
    
    public final void resetEarnings() {
        for (final Shop s : Economy.shops.values()) {
            s.resetEarnings();
        }
    }
    
    static {
        goldOnes = new LinkedList<Item>();
        goldFives = new LinkedList<Item>();
        goldTwentys = new LinkedList<Item>();
        silverOnes = new LinkedList<Item>();
        silverFives = new LinkedList<Item>();
        silverTwentys = new LinkedList<Item>();
        copperOnes = new LinkedList<Item>();
        copperFives = new LinkedList<Item>();
        copperTwentys = new LinkedList<Item>();
        ironOnes = new LinkedList<Item>();
        ironFives = new LinkedList<Item>();
        ironTwentys = new LinkedList<Item>();
        logger = Logger.getLogger(Economy.class.getName());
        moneylogger = Logger.getLogger("Money");
        supplyDemand = new HashMap<Integer, SupplyDemand>();
        shops = new HashMap<Long, Shop>();
        SHOPS_RW_LOCK = new ReentrantReadWriteLock();
    }
}
