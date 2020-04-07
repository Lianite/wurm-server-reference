// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.items.CoinDbStrings;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.items.ItemDbStrings;
import com.wurmonline.server.items.FrozenItemDbStrings;
import com.wurmonline.server.items.ItemMetaData;
import com.wurmonline.server.creatures.Delivery;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.highways.MethodsHighways;
import java.util.HashMap;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.DbItem;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.sql.PreparedStatement;
import java.sql.Connection;
import com.wurmonline.server.items.InitialContainer;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.items.Itempool;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.items.ItemRequirement;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.items.TradingWindow;
import com.wurmonline.server.items.ItemFactory;
import javax.annotation.Nullable;
import com.wurmonline.server.items.DbStrings;
import java.util.List;
import com.wurmonline.server.players.Player;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.LinkedList;
import javax.annotation.concurrent.GuardedBy;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.items.ItemData;
import java.util.Set;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.economy.MonetaryConstants;

public final class Items implements MiscConstants, MonetaryConstants, CounterTypes, TimeConstants
{
    private static final ConcurrentHashMap<Long, Item> items;
    private static Logger logger;
    public static final Logger debug;
    private static final ConcurrentHashMap<Item, Creature> draggedItems;
    private static final String GETITEMDATA = "SELECT * FROM ITEMDATA";
    private static final String GETITEMINSCRIPTIONDATA = "SELECT * FROM INSCRIPTIONS";
    private static final ConcurrentHashMap<Long, Set<Item>> containedItems;
    private static final ConcurrentHashMap<Long, Set<Item>> creatureItemsMap;
    private static final ConcurrentHashMap<Long, ItemData> itemDataMap;
    private static final ConcurrentHashMap<Long, InscriptionData> itemInscriptionDataMap;
    public static final int MAX_COUNT_ITEMS = 100;
    public static final int MAX_DECO_ITEMS = 15;
    private static final int MAX_EGGS = 1000;
    private static int currentEggs;
    @GuardedBy("ITEM_DATA_RW_LOCK")
    private static final Set<Long> protectedCorpses;
    @GuardedBy("HIDDEN_ITEMS_RW_LOCK")
    private static final Set<Item> hiddenItems;
    private static final Set<Long> unstableRifts;
    private static final LinkedList<Item> spawnPoints;
    private static final Set<Item> tents;
    private static boolean loadedCorpses;
    private static final ConcurrentHashMap<Long, Item> gmsigns;
    private static final ConcurrentHashMap<Long, Item> markers;
    private static final ConcurrentHashMap<Integer, Map<Integer, Set<Item>>> markersXY;
    private static final ConcurrentHashMap<Long, Item> waystones;
    private static final ConcurrentHashMap<Long, Integer> waystoneContainerCount;
    private static final ReentrantReadWriteLock HIDDEN_ITEMS_RW_LOCK;
    private static final Set<Item> tempHiddenItems;
    private static final Set<Item> warTargetItems;
    private static final Set<Item> sourceSprings;
    private static final Set<Item> supplyDepots;
    private static final Set<Item> harvestableItems;
    private static final Item[] emptyItems;
    private static final String moveItemsToFreezerForPlayer;
    private static final String deleteInventoryItemsForPlayer = "DELETE FROM ITEMS WHERE OWNERID=?";
    private static final String returnItemsFromFreezerForPlayer;
    private static final String deleteFrozenItemsForPlayer = "DELETE FROM FROZENITEMS WHERE OWNERID=?";
    private static final String returnItemFromFreezer;
    private static final String deleteFrozenItem = "DELETE FROM FROZENITEMS WHERE WURMID=?";
    private static final String insertProtectedCorpse = "INSERT INTO PROTECTEDCORPSES(WURMID)VALUES(?)";
    private static final String deleteProtectedCorpse = "DELETE FROM PROTECTEDCORPSES WHERE WURMID=?";
    private static final String loadProtectedCorpse = "SELECT * FROM PROTECTEDCORPSES";
    private static final Map<Integer, Set<Item>> zoneItemsAtLoad;
    public static final long riftEndTime = 1482227988600L;
    private static long cpOne;
    private static long cpTwo;
    private static long cpThree;
    private static long cpFour;
    private static long numCoins;
    private static long numItems;
    
    public static long getCpOne() {
        return Items.cpOne;
    }
    
    public static long getCpTwo() {
        return Items.cpTwo;
    }
    
    public static long getCpThree() {
        return Items.cpThree;
    }
    
    public static long getCpFour() {
        return Items.cpFour;
    }
    
    public static long getNumCoins() {
        return Items.numCoins;
    }
    
    public static long getNumItems() {
        return Items.numItems;
    }
    
    public static void putItem(final Item item) {
        Items.items.put(new Long(item.getWurmId()), item);
        if (item.isItemSpawn()) {
            addSupplyDepot(item);
        }
        if (item.isUnstableRift()) {
            addUnstableRift(item);
        }
        if (item.getTemplate().isHarvestable()) {
            addHarvestableItem(item);
        }
    }
    
    public static boolean exists(final Item item) {
        return Items.items.get(new Long(item.getWurmId())) != null;
    }
    
    public static boolean exists(final long id) {
        return Items.items.get(new Long(id)) != null;
    }
    
    public static Item getItem(final long id) throws NoSuchItemException {
        final Item toReturn = Items.items.get(new Long(id));
        if (toReturn == null) {
            throw new NoSuchItemException("No item found with id " + id);
        }
        return toReturn;
    }
    
    public static Optional<Item> getItemOptional(final long id) {
        Item item = null;
        item = Items.items.get(new Long(id));
        final Optional<Item> toReturn = Optional.ofNullable(item);
        return toReturn;
    }
    
    public static Set<Item> getItemsWithDesc(final String descpart, final boolean boat) throws NoSuchItemException {
        final Set<Item> itemsToRet = new HashSet<Item>();
        final String lDescpart = descpart.toLowerCase();
        for (final Item ne : Items.items.values()) {
            if (((boat && ne.isBoat()) || !boat) && ne.getDescription().length() > 0 && ne.getDescription().toLowerCase().contains(lDescpart)) {
                itemsToRet.add(ne);
            }
        }
        return itemsToRet;
    }
    
    public static void countEggs() {
        final long start = System.nanoTime();
        Items.currentEggs = 0;
        int fountains = 0;
        int wildhives = 0;
        int hives = 0;
        for (final Item nextEgg : Items.items.values()) {
            if (nextEgg.getTemplateId() == 1239) {
                ++wildhives;
            }
            else if (nextEgg.getTemplateId() == 1175) {
                ++hives;
            }
            else if (nextEgg.isEgg() && nextEgg.getData1() > 0) {
                ++Items.currentEggs;
                if (!Items.logger.isLoggable(Level.FINER)) {
                    continue;
                }
                Items.logger.finer("Found egg number: " + Items.currentEggs + ", Item: " + nextEgg);
            }
            else {
                if (nextEgg.getParentId() == -10L || (nextEgg.getTemplateId() != 408 && nextEgg.getTemplateId() != 635 && nextEgg.getTemplateId() != 405)) {
                    continue;
                }
                ++fountains;
            }
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        Items.logger.log(Level.INFO, "Current number of eggs is " + Items.currentEggs + " (max eggs is " + 1000 + ") That took " + lElapsedTime + " ms.");
        Items.logger.log(Level.INFO, "Current number of wild hives is " + wildhives + " and domestic hives is " + hives + ".");
        if (Servers.isThisATestServer()) {
            Players.getInstance().sendGmMessage(null, "System", "Debug: Current number of wild hives is " + wildhives + " and domestic hives is " + hives + ".", false);
        }
        if (fountains > 0) {
            Items.logger.log(Level.INFO, "Current number of fountains found in containers is " + fountains + ".");
        }
    }
    
    public static boolean mayLayEggs() {
        return Items.currentEggs < 1000;
    }
    
    public static Item[] getHiddenItemsAt(final int tilex, final int tiley, final float height, final boolean surfaced) {
        Items.HIDDEN_ITEMS_RW_LOCK.readLock().lock();
        Items.tempHiddenItems.clear();
        try {
            for (final Item i : Items.hiddenItems) {
                if ((int)i.getPosX() >> 2 == tilex && (int)i.getPosY() >> 2 == tiley && i.getPosZ() >= height && surfaced == i.isOnSurface()) {
                    Items.tempHiddenItems.add(i);
                }
            }
            if (Items.tempHiddenItems.size() > 0) {
                return Items.tempHiddenItems.toArray(new Item[Items.tempHiddenItems.size()]);
            }
        }
        finally {
            Items.HIDDEN_ITEMS_RW_LOCK.readLock().unlock();
        }
        return Items.emptyItems;
    }
    
    public static void revealItem(final Item item) {
        Items.HIDDEN_ITEMS_RW_LOCK.writeLock().lock();
        try {
            Items.hiddenItems.remove(item);
        }
        finally {
            Items.HIDDEN_ITEMS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public static void hideItem(final Creature performer, final Item item, final float height, final boolean putOnSurface) {
        if (putOnSurface) {
            try {
                item.putInVoid();
                item.setPosX(performer.getPosX());
                item.setPosY(performer.getPosY());
                performer.getCurrentTile().getZone().addItem(item);
            }
            catch (Exception ex) {
                Items.logger.log(Level.INFO, performer.getName() + " failed to hide item:" + ex.getMessage(), ex);
                performer.getCommunicator().sendNormalServerMessage("Failed to put the item on surface: " + ex.getMessage());
            }
        }
        else {
            Items.HIDDEN_ITEMS_RW_LOCK.writeLock().lock();
            try {
                Items.hiddenItems.add(item);
            }
            finally {
                Items.HIDDEN_ITEMS_RW_LOCK.writeLock().unlock();
            }
            item.setHidden(true);
            int zoneId = item.getZoneId();
            if (zoneId < 0) {
                zoneId = performer.getCurrentTile().getZone().getId();
            }
            item.putInVoid();
            item.setPosX(performer.getPosX());
            item.setPosY(performer.getPosY());
            item.setPosZ(height);
            item.setZoneId(zoneId, true);
        }
    }
    
    public static void removeItem(final long id) {
        final Item i = Items.items.remove(new Long(id));
        if (i != null && i.getTemplate() != null && i.getTemplate().isHarvestable()) {
            removeHarvestableItem(i);
        }
    }
    
    public static Item[] getAllItems() {
        return Items.items.values().toArray(new Item[Items.items.size()]);
    }
    
    public static Item[] getManagedCartsFor(final Player player, final boolean includeAll) {
        final Set<Item> carts = new HashSet<Item>();
        for (final Item item : Items.items.values()) {
            if (item.isCart() && item.canManage(player) && (includeAll || item.isLocked())) {
                carts.add(item);
            }
        }
        return carts.toArray(new Item[carts.size()]);
    }
    
    public static Item[] getOwnedCartsFor(final Player player) {
        final Set<Item> carts = new HashSet<Item>();
        for (final Item item : Items.items.values()) {
            if (item.isCart() && item.canManage(player)) {
                carts.add(item);
            }
        }
        return carts.toArray(new Item[carts.size()]);
    }
    
    public static Item[] getManagedShipsFor(final Player player, final boolean includeAll) {
        final Set<Item> ships = new HashSet<Item>();
        for (final Item item : Items.items.values()) {
            if (item.isBoat() && item.canManage(player) && (includeAll || item.isLocked())) {
                ships.add(item);
            }
        }
        return ships.toArray(new Item[ships.size()]);
    }
    
    public static Item[] getOwnedShipsFor(final Player player) {
        final Set<Item> ships = new HashSet<Item>();
        for (final Item item : Items.items.values()) {
            if (item.isBoat() && item.canManage(player)) {
                ships.add(item);
            }
        }
        return ships.toArray(new Item[ships.size()]);
    }
    
    public static void getOwnedCorpsesCartsShipsFor(final Player player, final List<Item> corpses, final List<Item> carts, final List<Item> ships) {
        for (final Item item : Items.items.values()) {
            if (item.isCart()) {
                if (!item.canManage(player)) {
                    continue;
                }
                carts.add(item);
            }
            else if (item.isBoat()) {
                if (!item.canManage(player)) {
                    continue;
                }
                ships.add(item);
            }
            else {
                if (item.getTemplateId() != 272 || !item.getName().equals("corpse of " + player.getName()) || item.getZoneId() <= -1) {
                    continue;
                }
                corpses.add(item);
            }
        }
    }
    
    public static int getNumberOfItems() {
        return Items.items.size();
    }
    
    public static int getNumberOfNormalItems() {
        int numberOfNormalItems = 0;
        for (final Item lItem : Items.items.values()) {
            final int templateId = lItem.getTemplateId();
            if (templateId != 0 && templateId != 521 && (templateId < 50 || templateId > 61) && (templateId < 10 || templateId > 19)) {
                ++numberOfNormalItems;
            }
        }
        return numberOfNormalItems;
    }
    
    public static void decay(final long id, @Nullable final DbStrings dbstrings) {
        if (WurmId.getType(id) != 19) {
            ItemFactory.decay(id, dbstrings);
        }
        removeItem(id);
        setProtected(id, false);
    }
    
    public static void destroyItem(final long id) {
        destroyItem(id, true);
    }
    
    public static void destroyItem(final long id, final boolean destroyKey) {
        destroyItem(id, destroyKey, false);
    }
    
    public static void destroyItem(final long id, final boolean destroyKey, final boolean destroyRecycled) {
        Item dest = null;
        try {
            dest = getItem(id);
            if (dest.isTraded()) {
                dest.getTradeWindow().removeItem(dest);
                dest.setTradeWindow(null);
            }
            if (dest.isTent()) {
                Items.tents.remove(dest);
            }
            if (dest.isRoadMarker() && dest.isPlanted()) {
                if (dest.getWhatHappened().length() == 0) {
                    dest.setWhatHappened("decayed away");
                }
                removeMarker(dest);
            }
            if (dest.getTemplateId() == 677) {
                removeGmSign(dest);
            }
            if (dest.getTemplateId() == 1309) {
                removeWagonerContainer(dest);
            }
            stopDragging(dest);
            if (dest.isVehicle() || dest.isTent()) {
                Vehicles.destroyVehicle(id);
                ItemSettings.remove(id);
                if (dest.isBoat() && dest.getData() != -1L) {
                    destroyItem(dest.getData(), destroyKey, destroyRecycled);
                }
            }
            MissionTargets.destroyMissionTarget(id, true);
            dest.deleteAllEffects();
            final ItemSpellEffects effs = ItemSpellEffects.getSpellEffects(id);
            if (effs != null) {
                effs.destroy();
            }
            ItemRequirement.deleteRequirements(id);
            if (dest.isHugeAltar() || dest.isArtifact()) {
                if (dest.isArtifact()) {
                    EndGameItems.deleteEndGameItem(EndGameItems.getEndGameItem(dest));
                }
            }
            else if (dest.isCoin()) {
                Server.getInstance().transaction(id, dest.getOwnerId(), -10L, "Destroyed", dest.getValue());
            }
            if ((dest.isUnfinished() || dest.isUseOnGroundOnly()) && dest.getWatcherSet() != null) {
                for (final Creature cret : dest.getWatcherSet()) {
                    cret.getCommunicator().sendRemoveFromCreationWindow(dest.getWurmId());
                }
            }
            if (dest.getTemplate().getInitialContainers() != null) {
                for (final Item i : dest.getItemsAsArray()) {
                    for (final InitialContainer ic : dest.getTemplate().getInitialContainers()) {
                        if (i.getTemplateId() == ic.getTemplateId()) {
                            destroyItem(i.getWurmId(), false, false);
                            break;
                        }
                    }
                }
            }
            if (destroyKey && dest.isKey()) {
                try {
                    final long lockId = dest.getLockId();
                    final Item lock = getItem(lockId);
                    lock.removeKey(id);
                }
                catch (NoSuchItemException nsi2) {
                    Items.logger.log(Level.INFO, "No lock when destroying key " + dest.getWurmId() + ", ownerId: " + dest.getOwnerId() + ", lastOwnerId: " + dest.getLastOwnerId());
                }
            }
            if (dest.isHollow() && dest.getLockId() != -10L) {
                destroyItem(dest.getLockId(), destroyKey, destroyRecycled);
            }
            if (dest.isLock() && destroyKey) {
                for (final long l : dest.getKeyIds()) {
                    try {
                        final Item k = getItem(l);
                        if (Items.logger.isLoggable(Level.FINEST)) {
                            Items.logger.finest("Destroying key with name: " + k.getName() + " and template: " + k.getTemplate().getName());
                        }
                        if (k.getTemplateId() == 166 || k.getTemplateId() == 663) {
                            dest.removeKey(l);
                        }
                        else {
                            destroyItem(l, destroyKey, destroyRecycled);
                        }
                    }
                    catch (NoSuchItemException ni) {
                        Items.logger.log(Level.WARNING, "Unable to find item for key: " + l, ni);
                    }
                }
                Connection dbcon = null;
                PreparedStatement ps3 = null;
                PreparedStatement ps4 = null;
                try {
                    dbcon = DbConnector.getItemDbCon();
                    ps3 = dbcon.prepareStatement("DELETE FROM ITEMKEYS WHERE LOCKID=?");
                    ps3.setLong(1, id);
                    ps3.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps3, null);
                    ps4 = dbcon.prepareStatement("DELETE FROM LOCKS WHERE WURMID=?");
                    ps4.setLong(1, id);
                    ps4.executeUpdate();
                }
                catch (SQLException ex) {
                    Items.logger.log(Level.WARNING, "Failed to destroy lock/keys for item with id " + id, ex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps3, null);
                    DbUtilities.closeDatabaseObjects(ps4, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
            if (dest.getTemplateId() == 1127) {
                for (final Item i : dest.getItemsAsArray()) {
                    if (i.getTemplateId() == 1128) {
                        destroyItem(i.getWurmId(), false, false);
                    }
                }
            }
        }
        catch (NoSuchItemException nsi) {
            Items.logger.log(Level.INFO, "Destroying " + id, nsi);
        }
        if (dest != null) {
            if (dest.getTemplateId() == 169) {
                Items.debug.info("** removeAndEmpty: " + dest.getWurmId());
            }
            dest.removeAndEmpty();
            if (!destroyRecycled && dest.isTypeRecycled()) {
                if (dest.getTemplateId() == 169) {
                    Items.debug.info("** removeItem: " + dest.getWurmId());
                }
                removeItem(id);
                Itempool.returnRecycledItem(dest);
            }
            else {
                decay(id, dest.getDbStrings());
            }
        }
        ItemSettings.remove(id);
        PermissionsHistories.remove(id);
        ItemMealData.delete(id);
    }
    
    public static boolean isItemLoaded(final long id) {
        final Item item = Items.items.get(new Long(id));
        return item != null;
    }
    
    public static void startDragging(final Creature dragger, final Item dragged) {
        Items.draggedItems.put(dragged, dragger);
        if (!dragged.isVehicle()) {
            dragged.setLastOwnerId(dragger.getWurmId());
            final Item[] allItems;
            final Item[] itemarr = allItems = dragged.getAllItems(false);
            for (final Item lElement : allItems) {
                lElement.setLastOwnerId(dragger.getWurmId());
            }
        }
        dragger.setDraggedItem(dragged);
        if (dragger.getVisionArea() != null) {
            dragger.getVisionArea().broadCastUpdateSelectBar(dragged.getWurmId());
        }
    }
    
    public static void stopDragging(final Item dragged) {
        dragged.savePosition();
        final Creature creature = Items.draggedItems.get(dragged);
        if (creature != null) {
            Items.draggedItems.remove(dragged);
            creature.setDraggedItem(null);
        }
    }
    
    public static boolean isItemDragged(final Item item) {
        return Items.draggedItems.keySet().contains(item);
    }
    
    public static Creature getDragger(final Item item) {
        return Items.draggedItems.get(item);
    }
    
    static void loadAllItempInscriptionData() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM INSCRIPTIONS");
            rs = ps.executeQuery();
            String inscription = "";
            String inscriber = "";
            int num = 0;
            while (rs.next()) {
                final long iid = rs.getLong("WURMID");
                inscription = rs.getString("INSCRIPTION");
                inscriber = rs.getString("INSCRIBER");
                final int penColor = rs.getInt("PENCOLOR");
                new InscriptionData(iid, inscription, inscriber, penColor);
                ++num;
            }
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Items.logger.log(Level.INFO, "Loaded " + num + " item inscription data entries, that took " + lElapsedTime + " ms");
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load item inscription datas: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static void loadAllItemData() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ITEMDATA");
            rs = ps.executeQuery();
            int d1 = 0;
            int d2 = 0;
            int e1 = 0;
            int e2 = 0;
            int num = 0;
            while (rs.next()) {
                final long iid = rs.getLong("WURMID");
                d1 = rs.getInt("DATA1");
                d2 = rs.getInt("DATA2");
                e1 = rs.getInt("EXTRA1");
                e2 = rs.getInt("EXTRA2");
                new ItemData(iid, d1, d2, e1, e2);
                ++num;
            }
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Items.logger.log(Level.INFO, "Loaded " + num + " item data entries, that took " + lElapsedTime + " ms");
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load item datas: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        ItemSpellEffects.loadSpellEffectsForItems();
    }
    
    public static void addItemInscriptionData(final InscriptionData data) {
        Items.itemInscriptionDataMap.put(new Long(data.getWurmId()), data);
    }
    
    public static InscriptionData getItemInscriptionData(final long itemid) {
        InscriptionData toReturn = null;
        toReturn = Items.itemInscriptionDataMap.get(new Long(itemid));
        return toReturn;
    }
    
    public static void addData(final ItemData data) {
        Items.itemDataMap.put(new Long(data.wurmid), data);
    }
    
    public static ItemData getItemData(final long itemid) {
        ItemData toReturn = null;
        toReturn = Items.itemDataMap.get(new Long(itemid));
        return toReturn;
    }
    
    public static final Set<Item> getAllItemsForZone(final int zid) {
        return Items.zoneItemsAtLoad.get(zid);
    }
    
    public static final boolean reloadAllSubItems(final Player performer, final long wurmId) {
        Items.logger.log(Level.INFO, performer.getName() + " forcing a reload of all subitems of " + wurmId);
        final long s = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final Item parentItem = getItem(wurmId);
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT WURMID FROM ITEMS WHERE PARENTID=?");
            ps.setLong(1, wurmId);
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                try {
                    final Item i = getItem(iid);
                    parentItem.insertItem(i, false);
                }
                catch (NoSuchItemException nsi) {
                    Items.logger.log(Level.INFO, "Could not reload subitem:" + iid + " for " + wurmId + " as item could not be found.", nsi);
                    performer.getCommunicator().sendNormalServerMessage("Could not reload subitem:" + iid + " for " + wurmId + " as that item could not be found.");
                }
            }
            return true;
        }
        catch (NoSuchItemException nsi2) {
            Items.logger.log(Level.WARNING, "Could not reload subitems for " + wurmId + " as item could not be found.", nsi2);
            performer.getCommunicator().sendNormalServerMessage("Could not reload subitems for " + wurmId + " as that item could not be found.");
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to reload subitems: " + sqx.getMessage(), sqx);
            performer.getCommunicator().sendNormalServerMessage("Could not reload subitems for " + wurmId + " due to a database error.");
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            Items.logger.log(Level.INFO, performer.getName() + " reloaded subitems for " + wurmId + ". That took " + (System.nanoTime() - s) / 1000000.0f + " ms.");
        }
        return false;
    }
    
    static void loadAllZoneItems(final DbStrings dbstrings) {
        if (!Items.loadedCorpses) {
            loadAllProtectedItems();
        }
        try {
            Items.logger.log(Level.INFO, "Loading all zone items using " + dbstrings);
            final long s = System.nanoTime();
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement(dbstrings.getZoneItems());
                rs = ps.executeQuery();
                long iid = -10L;
                while (rs.next()) {
                    iid = rs.getLong("WURMID");
                    final String name = rs.getString("NAME");
                    final float posx = rs.getFloat("POSX");
                    final float posy = rs.getFloat("POSY");
                    try {
                        final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(rs.getInt("TEMPLATEID"));
                        final Item item = new DbItem(iid, temp, name, rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), posx, posy, rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), -10L, rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), dbstrings);
                        if (item.hidden) {
                            Items.HIDDEN_ITEMS_RW_LOCK.writeLock().lock();
                            try {
                                Items.hiddenItems.add(item);
                            }
                            finally {
                                Items.HIDDEN_ITEMS_RW_LOCK.writeLock().unlock();
                            }
                        }
                        if (item.isWarTarget()) {
                            addWarTarget(item);
                        }
                        if (item.isSourceSpring()) {
                            addSourceSpring(item);
                        }
                        final long pid = item.getParentId();
                        if (pid != -10L) {
                            Set<Item> contained = Items.containedItems.get(new Long(pid));
                            if (contained == null) {
                                contained = new HashSet<Item>();
                            }
                            contained.add(item);
                            Items.containedItems.put(new Long(pid), contained);
                        }
                        if (item.getParentId() == -10L && item.getZoneId() > 0) {
                            Set<Item> itemset = Items.zoneItemsAtLoad.get(item.getZoneId());
                            if (itemset == null) {
                                itemset = new HashSet<Item>();
                                Items.zoneItemsAtLoad.put(item.getZoneId(), itemset);
                            }
                            itemset.add(item);
                        }
                        if (item.isEgg() && item.getData1() > 0) {
                            ++Items.currentEggs;
                        }
                        if (temp.getTemplateId() != 330) {
                            continue;
                        }
                        item.setData(-1L);
                    }
                    catch (NoSuchTemplateException nst) {
                        Items.logger.log(Level.WARNING, "Problem getting Template for item " + name + " (" + iid + ") @" + ((int)posx >> 2) + "," + ((int)posy >> 2) + "- " + nst.getMessage(), nst);
                    }
                }
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to load zone items: " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
            for (final Item item2 : Items.items.values()) {
                item2.getContainedItems();
            }
            final long e = System.nanoTime();
            Items.logger.log(Level.INFO, "Loaded " + Items.items.size() + " zone items. That took " + (e - s) / 1000000.0f + " ms.");
        }
        catch (Exception ex) {
            Items.logger.log(Level.WARNING, "Problem loading zone items due to " + ex.getMessage(), ex);
        }
        Itempool.checkRecycledItems();
    }
    
    public static final Set<Item> getTents() {
        return Items.tents;
    }
    
    public static final void addTent(final Item tent) {
        Items.tents.add(tent);
    }
    
    public static final void addGmSign(final Item gmSign) {
        Items.gmsigns.put(gmSign.getWurmId(), gmSign);
    }
    
    public static final void removeGmSign(final Item gmSign) {
        Items.gmsigns.remove(gmSign.getWurmId());
    }
    
    public static final Item[] getGMSigns() {
        return Items.gmsigns.values().toArray(new Item[Items.gmsigns.size()]);
    }
    
    public static final void addMarker(final Item marker) {
        Items.markers.put(marker.getWurmId(), marker);
        if (marker.getTemplateId() == 1112) {
            Items.waystones.put(marker.getWurmId(), marker);
        }
        Map<Integer, Set<Item>> ymap = Items.markersXY.get(marker.getTileX());
        if (ymap == null) {
            ymap = new HashMap<Integer, Set<Item>>();
            final HashSet<Item> mset = new HashSet<Item>();
            mset.add(marker);
            ymap.put(marker.getTileY(), mset);
            Items.markersXY.put(marker.getTileX(), ymap);
        }
        else {
            Set<Item> mset2 = ymap.get(marker.getTileY());
            if (mset2 == null) {
                mset2 = new HashSet<Item>();
                ymap.put(marker.getTileY(), mset2);
                mset2.add(marker);
            }
            else {
                mset2.add(marker);
            }
        }
    }
    
    public static final Item getMarker(final int tilex, final int tiley, final boolean onSurface, final int floorlevel, final long bridgeId) {
        final Map<Integer, Set<Item>> ymap = Items.markersXY.get(tilex);
        if (ymap != null) {
            final Set<Item> mset = ymap.get(tiley);
            if (mset != null) {
                for (final Item marker : mset) {
                    if (marker.isOnSurface() == onSurface && marker.getFloorLevel() == floorlevel && marker.getBridgeId() == bridgeId) {
                        return marker;
                    }
                }
            }
        }
        return null;
    }
    
    public static final void removeMarker(final Item marker) {
        if (marker.getAuxData() != 0) {
            MethodsHighways.removeLinksTo(marker);
        }
        Items.markers.remove(marker.getWurmId());
        if (marker.getTemplateId() == 1112) {
            Items.waystones.remove(marker.getWurmId());
        }
        Routes.remove(marker);
        final Map<Integer, Set<Item>> ymap = Items.markersXY.get(marker.getTileX());
        if (ymap != null) {
            final Set<Item> mset = ymap.get(marker.getTileY());
            if (mset != null) {
                for (final Item item : mset) {
                    if (item.getWurmId() == marker.getWurmId()) {
                        mset.remove(marker);
                        if (mset.isEmpty()) {
                            ymap.remove(marker.getTileY());
                            if (ymap.isEmpty()) {
                                Items.markersXY.remove(marker.getTileX());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static final Item[] getWaystones() {
        return Items.waystones.values().toArray(new Item[Items.waystones.size()]);
    }
    
    public static final Item[] getMarkers() {
        return Items.markers.values().toArray(new Item[Items.markers.size()]);
    }
    
    public static final void addWagonerContainer(final Item wagonerContainer) {
        final long waystoneId = wagonerContainer.getData();
        final Integer count = Items.waystoneContainerCount.get(waystoneId);
        if (count == null) {
            Items.waystoneContainerCount.put(waystoneId, 1);
        }
        else {
            Items.waystoneContainerCount.put(waystoneId, count + 1);
        }
    }
    
    public static final void removeWagonerContainer(final Item wagonerContainer) {
        final long waystoneId = wagonerContainer.getData();
        final Integer count = Items.waystoneContainerCount.get(waystoneId);
        if (count != null) {
            final int icount = count;
            if (icount > 1) {
                Items.waystoneContainerCount.put(waystoneId, icount - 1);
            }
            else {
                Items.waystoneContainerCount.remove(waystoneId);
            }
        }
    }
    
    public static final boolean isWaystoneInUse(final long waystoneId) {
        final Integer count = Items.waystoneContainerCount.get(waystoneId);
        return count != null || Delivery.isDeliveryPoint(waystoneId);
    }
    
    public static final void addSpawn(final Item spawn) {
        Items.spawnPoints.add(spawn);
    }
    
    public static final void removeSpawn(final Item spawn) {
        Items.spawnPoints.remove(spawn);
    }
    
    public static final Item[] getSpawnPoints() {
        return Items.spawnPoints.toArray(new Item[Items.spawnPoints.size()]);
    }
    
    public static final void addUnstableRift(final Item rift) {
        Items.unstableRifts.add(rift.getWurmId());
    }
    
    public static final void pollUnstableRifts() {
        if (System.currentTimeMillis() > 1482227988600L && !Items.unstableRifts.isEmpty()) {
            if (Items.unstableRifts.size() >= 15) {
                Server.getInstance().broadCastAlert("A shimmering wave of light runs over all the land as all the source rifts collapse.");
            }
            for (final Long rift : Items.unstableRifts) {
                destroyItem(rift);
            }
            Items.unstableRifts.clear();
        }
    }
    
    public static final void removeTent(final Item tent) {
        Items.tents.remove(tent);
    }
    
    public static final void addWarTarget(final Item target) {
        Items.warTargetItems.add(target);
    }
    
    public static final Item[] getWarTargets() {
        return Items.warTargetItems.toArray(new Item[Items.warTargetItems.size()]);
    }
    
    public static final void addSourceSpring(final Item spring) {
        Items.sourceSprings.add(spring);
    }
    
    public static final Item[] getSourceSprings() {
        return Items.sourceSprings.toArray(new Item[Items.sourceSprings.size()]);
    }
    
    public static final void addSupplyDepot(final Item depot) {
        Items.supplyDepots.add(depot);
    }
    
    public static final Item[] getSupplyDepots() {
        return Items.supplyDepots.toArray(new Item[Items.supplyDepots.size()]);
    }
    
    public static final void addHarvestableItem(final Item harvestable) {
        Items.harvestableItems.add(harvestable);
    }
    
    public static final void removeHarvestableItem(final Item harvestable) {
        Items.harvestableItems.remove(harvestable);
    }
    
    public static final Item[] getHarvestableItems() {
        return Items.harvestableItems.toArray(new Item[Items.harvestableItems.size()]);
    }
    
    public static boolean isHighestQLForTemplate(final int itemTemplate, final float itemql, final long itemId, final boolean before) {
        if (itemTemplate == 179) {
            return false;
        }
        if (itemTemplate == 386) {
            return false;
        }
        if (itemql < 80.0f) {
            return false;
        }
        boolean searched = false;
        for (final Item i : Items.items.values()) {
            if (i.getTemplateId() == itemTemplate && (!before || itemId != i.getWurmId())) {
                searched = true;
                if (i.getOriginalQualityLevel() > itemql) {
                    return false;
                }
                continue;
            }
        }
        return searched;
    }
    
    static Item createMetaDataItem(final ItemMetaData md) {
        final long iid = md.itemId;
        try {
            final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(md.itemtemplateId);
            final Item item = new DbItem(iid, temp, md.itname, md.lastmaintained, md.ql, md.origQl, md.sizex, md.sizey, md.sizez, md.posx, md.posy, md.posz, 0.0f, md.parentId, md.ownerId, -10, md.itemdam, md.weight, md.material, md.lockid, md.place, md.price, md.temp, md.desc, md.bless, md.enchantment, md.banked, md.lastowner, md.auxbyte, md.creationDate, md.creationState, md.realTemplate, md.wornAsArmour, md.color, md.color2, md.female, md.mailed, false, md.creator, false, (byte)0, md.rarity, md.onBridge, md.settings, false, md.instance);
            if (item.hidden) {
                Items.HIDDEN_ITEMS_RW_LOCK.writeLock().lock();
                try {
                    Items.hiddenItems.add(item);
                }
                finally {
                    Items.HIDDEN_ITEMS_RW_LOCK.writeLock().unlock();
                }
            }
            if (Servers.localServer.testServer) {
                Items.logger.log(Level.INFO, "Converting " + item.getName() + ", " + item.getWurmId());
            }
            if (item.isDraggable()) {
                final float newPosX = ((int)item.getPosX() >> 2 << 2) + 0.5f + Server.rand.nextFloat() * 2.0f;
                final float newPosY = ((int)item.getPosY() >> 2 << 2) + 0.5f + Server.rand.nextFloat() * 2.0f;
                item.setTempPositions(newPosX, newPosY, item.getPosZ(), item.getRotation());
            }
            final long pid = item.getParentId();
            if (pid != -10L) {
                Set<Item> contained = Items.containedItems.get(pid);
                if (contained == null) {
                    contained = new HashSet<Item>();
                }
                contained.add(item);
                Items.containedItems.put(pid, contained);
            }
            if (item.isEgg() && item.getData1() > 0) {
                ++Items.currentEggs;
            }
            return item;
        }
        catch (NoSuchTemplateException nst) {
            Items.logger.log(Level.WARNING, "Problem getting Template for item with Wurm ID " + iid + " - " + nst.getMessage(), nst);
            return null;
        }
    }
    
    public static void convertItemMetaData(final ItemMetaData[] metadatas) {
        final long start = System.nanoTime();
        final Set<Item> mditems = new HashSet<Item>();
        for (final ItemMetaData lMetadata : metadatas) {
            mditems.add(createMetaDataItem(lMetadata));
        }
        for (final Item item : mditems) {
            if (Servers.localServer.testServer) {
                Items.logger.log(Level.INFO, "Found " + item.getName());
            }
            if (item != null) {
                item.getContainedItems();
            }
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        if (Items.logger.isLoggable(Level.FINER)) {
            Items.logger.finer("Unpacked " + mditems.size() + " transferred items. That took " + lElapsedTime + " ms.");
        }
    }
    
    static void loadAllItemEffects() {
        Items.logger.info("Loading item effects.");
        final long now = System.nanoTime();
        int numberOfEffectsLoaded = 0;
        final Item[] allItems;
        final Item[] itarr = allItems = getAllItems();
        for (final Item lElement : allItems) {
            if (lElement.getTemperature() > 1000 || lElement.isAlwaysLit() || lElement.isItemSpawn()) {
                lElement.loadEffects();
                ++numberOfEffectsLoaded;
            }
        }
        Items.logger.log(Level.INFO, "Loaded " + numberOfEffectsLoaded + " item effects. That took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
    }
    
    public static final void returnItemFromFreezer(final long wurmId) {
        boolean ok = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(Items.returnItemFromFreezer);
            ps.setLong(1, wurmId);
            ps.execute();
            ok = true;
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to move item from freezer  " + wurmId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (ok) {
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("DELETE FROM FROZENITEMS WHERE WURMID=?");
                ps.setLong(1, wurmId);
                ps.execute();
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to delete item when moved to freezer " + wurmId + " : " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        if (ok) {
            try {
                final Item i = getItem(wurmId);
                if (i.getDbStrings() == FrozenItemDbStrings.getInstance()) {
                    i.setDbStrings(ItemDbStrings.getInstance());
                }
            }
            catch (NoSuchItemException ex) {}
        }
    }
    
    public static final void returnItemsFromFreezerFor(final long playerId) {
        boolean ok = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(Items.returnItemsFromFreezerForPlayer);
            ps.setLong(1, playerId);
            ps.execute();
            ok = true;
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to move items from freezer for creature " + playerId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (ok) {
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("DELETE FROM FROZENITEMS WHERE OWNERID=?");
                ps.setLong(1, playerId);
                ps.execute();
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to delete items when moved to freezer for creature " + playerId + " : " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        if (ok) {
            final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(playerId);
            if (pinf != null) {
                pinf.setMovedInventory(false);
                try {
                    pinf.save();
                }
                catch (IOException iox) {
                    Items.logger.log(Level.WARNING, iox.getMessage());
                }
            }
            for (final Item i : Items.items.values()) {
                if (i.getOwnerId() == playerId && i.getDbStrings() == FrozenItemDbStrings.getInstance()) {
                    i.setDbStrings(ItemDbStrings.getInstance());
                }
            }
        }
    }
    
    public static final boolean moveItemsToFreezerFor(final long playerId) {
        boolean ok = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(Items.moveItemsToFreezerForPlayer);
            ps.setLong(1, playerId);
            ps.execute();
            ok = true;
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to move items to freezer for creature " + playerId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (ok) {
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("DELETE FROM ITEMS WHERE OWNERID=?");
                ps.setLong(1, playerId);
                ps.execute();
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to delete items when moved to freezer for creature " + playerId + " : " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        if (ok) {
            for (final Item i : Items.items.values()) {
                if (i.getOwnerId() == playerId && i.getDbStrings() == ItemDbStrings.getInstance()) {
                    i.setDbStrings(FrozenItemDbStrings.getInstance());
                    Items.logger.log(Level.INFO, "Changed dbstrings for item " + i.getWurmId() + " and player " + playerId + " to frozen");
                }
            }
        }
        return ok;
    }
    
    public static Set<Long> loadAllNonTransferredItemsIdsForCreature(final long creatureId, final PlayerInfo info) {
        if (Items.logger.isLoggable(Level.FINER)) {
            Items.logger.finer("Loading items for " + creatureId);
        }
        final Set<Long> creatureItemIds = new HashSet<Long>();
        if (info != null && info.hasMovedInventory()) {
            returnItemsFromFreezerFor(creatureId);
            info.setMovedInventory(false);
            PlayerInfoFactory.getDeleteLogger().log(Level.INFO, "Returned items for " + info.getName() + " after transfer");
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(ItemDbStrings.getInstance().getCreatureItemsNonTransferred());
            ps.setLong(1, creatureId);
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                creatureItemIds.add(new Long(iid));
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load items for creature " + creatureId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(CoinDbStrings.getInstance().getCreatureItemsNonTransferred());
            ps.setLong(1, creatureId);
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                creatureItemIds.add(new Long(iid));
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load coin items for creature " + creatureId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(FrozenItemDbStrings.getInstance().getCreatureItemsNonTransferred());
            ps.setLong(1, creatureId);
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                creatureItemIds.add(new Long(iid));
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load frozen items for creature " + creatureId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return creatureItemIds;
    }
    
    public static final void clearCreatureLoadMap() {
        Items.creatureItemsMap.clear();
    }
    
    public static final void loadAllCreatureItems() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ITEMS WHERE OWNERID&0xFF=1");
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                try {
                    final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(rs.getInt("TEMPLATEID"));
                    Item item = null;
                    boolean load = true;
                    if (temp.alwaysLoaded) {
                        load = false;
                        try {
                            item = getItem(iid);
                            item.setOwnerStuff(temp);
                        }
                        catch (NoSuchItemException nsi) {
                            load = true;
                        }
                    }
                    if (load) {
                        item = new DbItem(iid, temp, rs.getString("NAME"), rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), rs.getLong("OWNERID"), rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), ItemDbStrings.getInstance());
                    }
                    final long pid = item.getParentId();
                    if (pid != -10L) {
                        Set<Item> contained = Items.containedItems.get(pid);
                        if (contained == null) {
                            contained = new HashSet<Item>();
                        }
                        contained.add(item);
                        Items.containedItems.put(pid, contained);
                    }
                    if (item.getOwnerId() > 0L) {
                        Set<Item> contained = Items.creatureItemsMap.get(item.getOwnerId());
                        if (contained == null) {
                            contained = new HashSet<Item>();
                        }
                        contained.add(item);
                        Items.creatureItemsMap.put(item.getOwnerId(), contained);
                    }
                    ++Items.numItems;
                }
                catch (NoSuchTemplateException nst) {
                    Items.logger.log(Level.WARNING, "Problem getting Template for item", nst);
                }
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load items " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM COINS WHERE OWNERID&0xFF=1");
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                try {
                    final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(rs.getInt("TEMPLATEID"));
                    Item item = null;
                    boolean load = true;
                    if (temp.alwaysLoaded) {
                        load = false;
                        try {
                            item = getItem(iid);
                            item.setOwnerStuff(temp);
                        }
                        catch (NoSuchItemException nsi) {
                            load = true;
                        }
                    }
                    if (load) {
                        item = new DbItem(iid, temp, rs.getString("NAME"), rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), rs.getLong("OWNERID"), rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), CoinDbStrings.getInstance());
                    }
                    final long pid = item.getParentId();
                    if (pid != -10L) {
                        Set<Item> contained = Items.containedItems.get(pid);
                        if (contained == null) {
                            contained = new HashSet<Item>();
                        }
                        contained.add(item);
                        Items.containedItems.put(pid, contained);
                    }
                    if (item.getOwnerId() > 0L) {
                        Set<Item> contained = Items.creatureItemsMap.get(item.getOwnerId());
                        if (contained == null) {
                            contained = new HashSet<Item>();
                        }
                        contained.add(item);
                        Items.creatureItemsMap.put(item.getOwnerId(), contained);
                    }
                    ++Items.numCoins;
                }
                catch (NoSuchTemplateException nst) {
                    Items.logger.log(Level.WARNING, "Problem getting Template for item", nst);
                }
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load coins " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void loadAllItemsForNonPlayer(final Creature creature, final long inventoryId) {
        long cpS = System.nanoTime();
        if (Items.logger.isLoggable(Level.FINEST)) {
            Items.logger.finest("Loading items for creature " + creature.getWurmId());
        }
        final Set<Item> creatureItems = Items.creatureItemsMap.get(creature.getWurmId());
        Items.cpOne += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        if (creatureItems != null) {
            for (final Item item : creatureItems) {
                item.getContainedItems();
            }
        }
        try {
            creature.loadPossessions(inventoryId);
        }
        catch (Exception ex) {
            Items.logger.log(Level.WARNING, creature.getName() + " failed to load possessions - inventory not found " + ex.getMessage(), ex);
        }
        Items.cpThree += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        if (creatureItems == null || creatureItems.size() == 0) {
            return;
        }
        for (final Item item : creatureItems) {
            if (!item.isInventory() && !item.isBodyPart()) {
                try {
                    final Item parent = item.getParent();
                    if (parent.isBodyPart()) {
                        if (moveItemFromIncorrectSlot(item, parent, creature) || parent.getItems().contains(item) || parent.insertItem(item, false)) {
                            continue;
                        }
                        resetParentToInventory(item, creature);
                        Items.logger.log(Level.INFO, "INSERTED IN INVENTORY " + item.getName() + " for " + creature.getName() + " wid=" + item.getWurmId());
                    }
                    else {
                        if (!parent.isInventory() || creature.isPlayer() || (!creature.isHorse() && !creature.getTemplate().isHellHorse() && !creature.getTemplate().isKingdomGuard())) {
                            continue;
                        }
                        final byte[] spaces = item.getBodySpaces();
                        for (int i = 0; i < spaces.length; ++i) {
                            try {
                                final Item bp = creature.getBody().getBodyPart(spaces[i]);
                                if (bp != null && bp.testInsertItem(item)) {
                                    bp.insertItem(item);
                                    break;
                                }
                            }
                            catch (NoSpaceException nse) {
                                Items.logger.log(Level.INFO, "Unable to find body part, inserting in inventory");
                                resetParentToInventory(item, creature);
                            }
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    if (creature.isHorse() || creature.getTemplate().isHellHorse() || creature.getTemplate().isKingdomGuard()) {
                        final byte[] spaces = item.getBodySpaces();
                        for (int i = 0; i < spaces.length; ++i) {
                            try {
                                final Item bp = creature.getBody().getBodyPart(spaces[i]);
                                if (bp != null && bp.testInsertItem(item)) {
                                    bp.insertItem(item);
                                    break;
                                }
                            }
                            catch (NoSpaceException nse) {
                                Items.logger.log(Level.INFO, "Unable to find body part, inserting in inventory");
                                resetParentToInventory(item, creature);
                            }
                        }
                    }
                    else {
                        Items.logger.log(Level.INFO, "Unable to find parent slot, inserting in inventory");
                        resetParentToInventory(item, creature);
                    }
                }
            }
        }
        Items.cpFour += System.nanoTime() - cpS;
    }
    
    public static Set<Item> loadAllItemsForCreature(final Creature creature, final long inventoryId) {
        long cpS = System.nanoTime();
        if (Items.logger.isLoggable(Level.FINEST)) {
            Items.logger.finest("Loading items for creature " + creature.getWurmId());
        }
        final Set<Item> creatureItems = new HashSet<Item>();
        if (creature.isPlayer() && ((Player)creature).getSaveFile().hasMovedInventory()) {
            returnItemsFromFreezerFor(creature.getWurmId());
            ((Player)creature).getSaveFile().setMovedInventory(false);
        }
        loadAllItemsForCreatureAndItemtype(creature.getWurmId(), CoinDbStrings.getInstance(), creatureItems);
        Items.cpOne += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        loadAllItemsForCreatureAndItemtype(creature.getWurmId(), ItemDbStrings.getInstance(), creatureItems);
        Items.cpTwo += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        for (final Item item : creatureItems) {
            item.getContainedItems();
        }
        try {
            creature.loadPossessions(inventoryId);
        }
        catch (Exception ex) {
            Items.logger.log(Level.WARNING, creature.getName() + " failed to load possessions - inventory not found " + ex.getMessage(), ex);
        }
        Items.cpThree += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        for (final Item item : creatureItems) {
            if (!item.isInventory() && !item.isBodyPart()) {
                try {
                    final Item parent = item.getParent();
                    if (parent.isBodyPart()) {
                        if (moveItemFromIncorrectSlot(item, parent, creature) || parent.getItems().contains(item) || parent.insertItem(item, false)) {
                            continue;
                        }
                        resetParentToInventory(item, creature);
                        Items.logger.log(Level.INFO, "Inserted in inventory " + item.getName() + " for " + creature.getName() + " wid=" + item.getWurmId());
                    }
                    else {
                        if (!parent.isInventory() || creature.isPlayer() || (!creature.isHorse() && !creature.getTemplate().isHellHorse() && !creature.getTemplate().isKingdomGuard())) {
                            continue;
                        }
                        final byte[] spaces = item.getBodySpaces();
                        for (int i = 0; i < spaces.length; ++i) {
                            try {
                                final Item bp = creature.getBody().getBodyPart(spaces[i]);
                                if (bp != null && bp.testInsertItem(item)) {
                                    bp.insertItem(item);
                                    break;
                                }
                            }
                            catch (NoSpaceException nse) {
                                Items.logger.log(Level.INFO, "Unable to find body part, inserting in inventory");
                                resetParentToInventory(item, creature);
                            }
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    if (creature.isHorse() || creature.getTemplate().isHellHorse() || creature.getTemplate().isKingdomGuard()) {
                        final byte[] spaces = item.getBodySpaces();
                        for (int i = 0; i < spaces.length; ++i) {
                            try {
                                final Item bp = creature.getBody().getBodyPart(spaces[i]);
                                if (bp != null && bp.testInsertItem(item)) {
                                    bp.insertItem(item);
                                    break;
                                }
                            }
                            catch (NoSpaceException nse) {
                                Items.logger.log(Level.INFO, "Unable to find body part, inserting in inventory");
                                resetParentToInventory(item, creature);
                            }
                        }
                    }
                    else {
                        Items.logger.log(Level.INFO, "Unable to find parent slot, inserting in inventory");
                        resetParentToInventory(item, creature);
                    }
                }
            }
        }
        Items.cpFour += System.nanoTime() - cpS;
        cpS = System.nanoTime();
        return creatureItems;
    }
    
    private static boolean moveItemFromIncorrectSlot(final Item item, final Item parent, final Creature creature) {
        if (!creature.isHuman() || item.isBodyPart() || item.isEquipmentSlot()) {
            return false;
        }
        if (item.isBelt()) {
            if (parent.isEquipmentSlot() && parent.getAuxData() == 22) {
                return false;
            }
            if (parent.isBodyPart()) {
                resetParentToInventory(item, creature);
                return true;
            }
        }
        else {
            if (parent.isBodyPart() && item.isInventoryGroup()) {
                resetParentToInventory(item, creature);
                return true;
            }
            if (parent.isBodyPart() && !parent.isEquipmentSlot()) {
                if (item.isArmour()) {
                    return false;
                }
                if (item.getTemplateId() == 231) {
                    return false;
                }
                if (!creature.isPlayer() && item.isWeapon()) {
                    return false;
                }
                resetParentToInventory(item, creature);
                return true;
            }
        }
        return false;
    }
    
    public static void resetParentToInventory(final Item item, final Creature creature) {
        final Set<Item> contained = Items.containedItems.get(new Long(item.getParentId()));
        if (contained != null) {
            final boolean found = contained.remove(item);
            if (found) {
                Items.logger.log(Level.INFO, "Success! removed the " + item.getName());
            }
        }
        try {
            creature.getInventory().insertItem(item, false);
            Items.logger.log(Level.INFO, "Inventory id: " + creature.getInventory().getWurmId() + ", item parent now: " + item.getParentId() + " owner id=" + creature.getWurmId() + " item owner id=" + item.getOwnerId());
            try {
                final Item itemorig = getItem(item.getWurmId());
                Items.logger.log(Level.INFO, "Inventory id: " + creature.getInventory().getWurmId() + ", item parent now: " + itemorig.getParentId() + " owner id=" + itemorig.getOwnerId());
            }
            catch (Exception ex) {
                Items.logger.log(Level.WARNING, "retrieval failed", ex);
            }
        }
        catch (Exception ex) {
            Items.logger.log(Level.WARNING, "Inserting " + item + " into inventory instead for creature " + creature.getWurmId() + " failed: " + ex.getMessage(), ex);
        }
    }
    
    public static Set<Item> loadAllItemsForCreatureWithId(final long creatureId, final boolean frozen) {
        final Set<Item> creatureItems = new HashSet<Item>();
        loadAllItemsForCreatureAndItemtype(creatureId, CoinDbStrings.getInstance(), creatureItems);
        if (frozen) {
            loadAllItemsForCreatureAndItemtype(creatureId, FrozenItemDbStrings.getInstance(), creatureItems);
        }
        else {
            loadAllItemsForCreatureAndItemtype(creatureId, ItemDbStrings.getInstance(), creatureItems);
        }
        return creatureItems;
    }
    
    public static void loadAllItemsForCreatureAndItemtype(final long creatureId, final DbStrings dbstrings, final Set<Item> creatureItems) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(dbstrings.getCreatureItems());
            ps.setLong(1, creatureId);
            rs = ps.executeQuery();
            long iid = -10L;
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                try {
                    final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(rs.getInt("TEMPLATEID"));
                    Item item = null;
                    boolean load = true;
                    if (temp.alwaysLoaded) {
                        load = false;
                        try {
                            item = getItem(iid);
                            item.setOwnerStuff(temp);
                        }
                        catch (NoSuchItemException nsi) {
                            load = true;
                        }
                    }
                    if (load) {
                        item = new DbItem(iid, temp, rs.getString("NAME"), rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), rs.getLong("OWNERID"), rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), dbstrings);
                    }
                    final long pid = item.getParentId();
                    if (pid != -10L) {
                        Set<Item> contained = Items.containedItems.get(new Long(pid));
                        if (contained == null) {
                            contained = new HashSet<Item>();
                        }
                        contained.add(item);
                        Items.containedItems.put(new Long(pid), contained);
                    }
                    creatureItems.add(item);
                }
                catch (NoSuchTemplateException nst) {
                    Items.logger.log(Level.WARNING, "Problem getting Template for item with Wurm ID " + iid + "  for creature " + creatureId + " - " + nst.getMessage(), nst);
                }
            }
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load items for creature " + creatureId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static Set<Item> getContainedItems(final long id) {
        final Long lid = new Long(id);
        return Items.containedItems.remove(lid);
    }
    
    static void loadAllStaticItems() {
        Items.logger.log(Level.INFO, "Loading all static preloaded items");
        final long start = System.nanoTime();
        final ItemTemplate[] templates2;
        final ItemTemplate[] templates = templates2 = ItemTemplateFactory.getInstance().getTemplates();
        for (final ItemTemplate lTemplate : templates2) {
            if (lTemplate.alwaysLoaded) {
                loadAllStaticItems(lTemplate.getTemplateId());
            }
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        Items.logger.log(Level.INFO, "Loaded all static preloaded items, that took " + lElapsedTime + " ms");
    }
    
    private static void loadAllStaticItems(final int templateId) {
        if (Items.logger.isLoggable(Level.FINER)) {
            Items.logger.finer("Loading all static items for template ID " + templateId);
        }
        DbStrings dbstrings = Item.getDbStrings(templateId);
        long iid = -10L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(dbstrings.getPreloadedItems());
            ps.setInt(1, templateId);
            rs = ps.executeQuery();
            final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(templateId);
            while (rs.next()) {
                iid = rs.getLong("WURMID");
                new DbItem(iid, temp, rs.getString("NAME"), rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), rs.getLong("OWNERID"), rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), dbstrings);
            }
        }
        catch (NoSuchTemplateException nst) {
            Items.logger.log(Level.WARNING, "Problem getting Template ID " + templateId + " - " + nst.getMessage(), nst);
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load items for template " + templateId + " : " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        if (dbstrings == ItemDbStrings.getInstance()) {
            dbstrings = FrozenItemDbStrings.getInstance();
            iid = -10L;
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement(dbstrings.getPreloadedItems());
                ps.setInt(1, templateId);
                rs = ps.executeQuery();
                final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(templateId);
                while (rs.next()) {
                    iid = rs.getLong("WURMID");
                    new DbItem(iid, temp, rs.getString("NAME"), rs.getLong("LASTMAINTAINED"), rs.getFloat("QUALITYLEVEL"), rs.getFloat("ORIGINALQUALITYLEVEL"), rs.getInt("SIZEX"), rs.getInt("SIZEY"), rs.getInt("SIZEZ"), rs.getFloat("POSX"), rs.getFloat("POSY"), rs.getFloat("POSZ"), rs.getFloat("ROTATION"), rs.getLong("PARENTID"), rs.getLong("OWNERID"), rs.getInt("ZONEID"), rs.getFloat("DAMAGE"), rs.getInt("WEIGHT"), rs.getByte("MATERIAL"), rs.getLong("LOCKID"), rs.getShort("PLACE"), rs.getInt("PRICE"), rs.getShort("TEMPERATURE"), rs.getString("DESCRIPTION"), rs.getByte("BLESS"), rs.getByte("ENCHANT"), rs.getBoolean("BANKED"), rs.getLong("LASTOWNERID"), rs.getByte("AUXDATA"), rs.getLong("CREATIONDATE"), rs.getByte("CREATIONSTATE"), rs.getInt("REALTEMPLATE"), rs.getBoolean("WORNARMOUR"), rs.getInt("COLOR"), rs.getInt("COLOR2"), rs.getBoolean("FEMALE"), rs.getBoolean("MAILED"), rs.getBoolean("TRANSFERRED"), rs.getString("CREATOR"), rs.getBoolean("HIDDEN"), rs.getByte("MAILTIMES"), rs.getByte("RARITY"), rs.getLong("ONBRIDGE"), rs.getInt("SETTINGS"), rs.getBoolean("PLACEDONPARENT"), dbstrings);
                }
            }
            catch (NoSuchTemplateException nst) {
                Items.logger.log(Level.WARNING, "Problem getting Template ID " + templateId + " - " + nst.getMessage(), nst);
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to load frozen items for template " + templateId + " : " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static void deleteSpawnPoints() {
        final Item[] _items = getAllItems();
        int dist = 0;
        for (final Item lItem : _items) {
            if (lItem.getTemplateId() == 521) {
                dist = ((int)lItem.getPosX() >> 2) - ((int)lItem.getPosY() >> 2);
                if (dist < 2 && dist > -2) {
                    destroyItem(lItem.getWurmId());
                }
            }
        }
    }
    
    public static void transferRegaliaForKingdom(final byte kingdom, final long newOwnerId) {
        if (Kingdoms.getKingdom(kingdom) != null && Kingdoms.getKingdom(kingdom).isCustomKingdom()) {
            final Item[] allItems;
            final Item[] _items = allItems = getAllItems();
            for (final Item lItem : allItems) {
                if (lItem.isRoyal() && lItem.getOwnerId() != -10L && lItem.getKingdom() == kingdom && lItem.getOwnerId() != newOwnerId) {
                    lItem.putInVoid();
                    lItem.setTransferred(false);
                    lItem.setBanked(false);
                    try {
                        final Creature c = Server.getInstance().getCreature(newOwnerId);
                        c.getInventory().insertItem(lItem);
                    }
                    catch (Exception ex) {
                        Items.logger.log(Level.WARNING, ex.getMessage());
                    }
                }
            }
        }
    }
    
    public static void deleteRoyalItemForKingdom(final byte kingdom, final boolean onSurface, final boolean destroy) {
        final boolean putOnGround = Kingdoms.getKingdom(kingdom).isCustomKingdom();
        final Item[] allItems;
        final Item[] _items = allItems = getAllItems();
        for (final Item lItem : allItems) {
            if (lItem.isRoyal()) {
                if (kingdom == 2 && lItem.getTemplateId() == 538) {
                    lItem.updatePos();
                }
                else if (lItem.getKingdom() == kingdom) {
                    if (!destroy && putOnGround) {
                        int tilex = lItem.getTileX();
                        int tiley = lItem.getTileY();
                        try {
                            final Creature owner = Server.getInstance().getCreature(lItem.getOwnerId());
                            tilex = owner.getTileX();
                            tiley = owner.getTileY();
                            lItem.setPosXY(owner.getPosX(), owner.getPosY());
                            owner.getCommunicator().sendAlertServerMessage("As a sign of abdication, you put the " + lItem.getName() + " at your feet.");
                        }
                        catch (Exception ex) {}
                        try {
                            final Zone z = Zones.getZone(tilex, tiley, onSurface);
                            lItem.putInVoid();
                            lItem.setTransferred(false);
                            lItem.setBanked(false);
                            z.addItem(lItem);
                        }
                        catch (NoSuchZoneException nsz) {
                            Items.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                        }
                    }
                    else {
                        destroyItem(lItem.getWurmId());
                    }
                }
            }
        }
    }
    
    public static void deleteChristmasItems() {
        final Item[] allItems;
        final Item[] lItems = allItems = getAllItems();
        for (final Item lLItem : allItems) {
            if (lLItem.getTemplateId() == 442) {
                destroyItem(lLItem.getWurmId());
            }
        }
    }
    
    public static final void loadAllProtectedItems() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PROTECTEDCORPSES");
            rs = ps.executeQuery();
            int num = 0;
            while (rs.next()) {
                Items.protectedCorpses.add(rs.getLong("WURMID"));
                ++num;
            }
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Items.logger.log(Level.INFO, "Loaded " + num + " protected corpse entries, that took " + lElapsedTime + " ms");
        }
        catch (SQLException sqx) {
            Items.logger.log(Level.WARNING, "Failed to load protected corpses: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        Items.loadedCorpses = true;
    }
    
    public static final boolean isProtected(final Item item) {
        return Items.protectedCorpses.contains(item.getWurmId());
    }
    
    public static final void setProtected(final long wurmid, final boolean isProtected) {
        if (!isProtected) {
            if (Items.protectedCorpses.remove(wurmid)) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getItemDbCon();
                    ps = dbcon.prepareStatement("DELETE FROM PROTECTEDCORPSES WHERE WURMID=?");
                    ps.setLong(1, wurmid);
                    ps.execute();
                }
                catch (SQLException sqx) {
                    Items.logger.log(Level.WARNING, "Failed to set protected false " + wurmid + " : " + sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        else if (!Items.protectedCorpses.contains(wurmid)) {
            Items.protectedCorpses.add(wurmid);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getItemDbCon();
                ps = dbcon.prepareStatement("INSERT INTO PROTECTEDCORPSES(WURMID)VALUES(?)");
                ps.setLong(1, wurmid);
                ps.execute();
            }
            catch (SQLException sqx) {
                Items.logger.log(Level.WARNING, "Failed to add protected " + wurmid + " : " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static final int getBattleCampControl(final byte kingdom) {
        int nums = 0;
        for (final Item item : getWarTargets()) {
            if (item.getKingdom() == kingdom) {
                ++nums;
            }
        }
        return nums;
    }
    
    public static final Item findMerchantContractFromId(final long contractId) {
        for (final Item item : getAllItems()) {
            if (item.getTemplateId() == 300 && item.getData() == contractId) {
                return item;
            }
        }
        return null;
    }
    
    static {
        items = new ConcurrentHashMap<Long, Item>();
        Items.logger = Logger.getLogger(Items.class.getName());
        debug = Logger.getLogger("ItemDebug");
        draggedItems = new ConcurrentHashMap<Item, Creature>();
        containedItems = new ConcurrentHashMap<Long, Set<Item>>();
        creatureItemsMap = new ConcurrentHashMap<Long, Set<Item>>();
        itemDataMap = new ConcurrentHashMap<Long, ItemData>();
        itemInscriptionDataMap = new ConcurrentHashMap<Long, InscriptionData>();
        Items.currentEggs = 0;
        protectedCorpses = new HashSet<Long>();
        hiddenItems = new HashSet<Item>();
        unstableRifts = new HashSet<Long>();
        spawnPoints = new LinkedList<Item>();
        tents = new HashSet<Item>();
        Items.loadedCorpses = false;
        gmsigns = new ConcurrentHashMap<Long, Item>();
        markers = new ConcurrentHashMap<Long, Item>();
        markersXY = new ConcurrentHashMap<Integer, Map<Integer, Set<Item>>>();
        waystones = new ConcurrentHashMap<Long, Item>();
        waystoneContainerCount = new ConcurrentHashMap<Long, Integer>();
        HIDDEN_ITEMS_RW_LOCK = new ReentrantReadWriteLock();
        tempHiddenItems = new HashSet<Item>();
        warTargetItems = new HashSet<Item>();
        sourceSprings = new HashSet<Item>();
        supplyDepots = new HashSet<Item>();
        harvestableItems = new HashSet<Item>();
        emptyItems = new Item[0];
        moveItemsToFreezerForPlayer = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO FROZENITEMS SELECT * FROM ITEMS WHERE OWNERID=?" : "INSERT IGNORE INTO FROZENITEMS (SELECT * FROM ITEMS WHERE OWNERID=?)");
        returnItemsFromFreezerForPlayer = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO ITEMS SELECT * FROM FROZENITEMS WHERE OWNERID=?" : "INSERT IGNORE INTO ITEMS (SELECT * FROM FROZENITEMS WHERE OWNERID=?)");
        returnItemFromFreezer = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO ITEMS SELECT * FROM FROZENITEMS WHERE WURMID=?" : "INSERT IGNORE INTO ITEMS (SELECT * FROM FROZENITEMS WHERE WURMID=?)");
        zoneItemsAtLoad = new ConcurrentHashMap<Integer, Set<Item>>();
        Items.cpOne = 0L;
        Items.cpTwo = 0L;
        Items.cpThree = 0L;
        Items.cpFour = 0L;
        Items.numCoins = 0L;
        Items.numItems = 0L;
    }
    
    static class EggCounter implements Runnable
    {
        @Override
        public void run() {
            if (Items.logger.isLoggable(Level.FINE)) {
                Items.logger.fine("Running newSingleThreadScheduledExecutor for calling Items.countEggs()");
            }
            try {
                final long start = System.nanoTime();
                Items.countEggs();
                final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    Items.logger.info("Finished calling Items.countEggs(), which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                Items.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling Items.countEggs()", e);
                throw e;
            }
        }
    }
}
