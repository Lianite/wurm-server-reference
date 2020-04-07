// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.structures.Structure;
import java.util.Iterator;
import com.wurmonline.server.Features;
import com.wurmonline.server.Items;
import java.util.HashSet;
import java.util.Set;
import com.wurmonline.server.items.Item;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class VolaTileItems
{
    private static final Logger logger;
    private int[] itemsPerLevel;
    private int[] decoItemsPerLevel;
    private final int PILECOUNT = 3;
    private boolean hasFire;
    private final int maxFloorLevel = 20;
    private ConcurrentHashMap<Integer, Item> pileItems;
    private ConcurrentHashMap<Integer, Item> onePerTileItems;
    private ConcurrentHashMap<Integer, Set<Item>> fourPerTileItems;
    private Set<Item> allItems;
    private Set<Item> alwaysPoll;
    
    public VolaTileItems() {
        this.itemsPerLevel = new int[1];
        this.decoItemsPerLevel = new int[1];
        this.hasFire = false;
        this.pileItems = new ConcurrentHashMap<Integer, Item>();
        this.onePerTileItems = new ConcurrentHashMap<Integer, Item>();
        this.fourPerTileItems = new ConcurrentHashMap<Integer, Set<Item>>();
        this.allItems = new HashSet<Item>();
    }
    
    private void incrementItemsOnLevelByOne(final int level) {
        if (this.itemsPerLevel.length > level) {
            final int[] itemsPerLevel = this.itemsPerLevel;
            ++itemsPerLevel[level];
        }
    }
    
    private void incrementDecoItemsOnLevelByOne(final int level) {
        if (this.decoItemsPerLevel.length > level) {
            final int[] decoItemsPerLevel = this.decoItemsPerLevel;
            ++decoItemsPerLevel[level];
        }
    }
    
    private void decrementItemsOnLevelByOne(final int level) {
        if (this.itemsPerLevel.length > level && this.itemsPerLevel[level] > 0) {
            final int[] itemsPerLevel = this.itemsPerLevel;
            --itemsPerLevel[level];
        }
    }
    
    private void decrementDecoItemsOnLevelByOne(final int level) {
        if (this.decoItemsPerLevel.length > level && this.decoItemsPerLevel[level] > 0) {
            final int[] decoItemsPerLevel = this.decoItemsPerLevel;
            --decoItemsPerLevel[level];
        }
    }
    
    public final boolean addItem(final Item item, final boolean starting) {
        if (!this.allItems.contains(item)) {
            if (item.isFire()) {
                if (this.hasFire && item.getTemplateId() == 37) {
                    final Item c = this.getCampfire(false);
                    if (c != null) {
                        final short maxTemp = 30000;
                        c.setTemperature((short)Math.min(30000, c.getTemperature() + item.getWeightGrams()));
                        Items.destroyItem(item.getWurmId());
                        return false;
                    }
                }
                this.hasFire = true;
            }
            if (item.isOnePerTile()) {
                this.onePerTileItems.put(item.getFloorLevel(), item);
            }
            if (item.isFourPerTile()) {
                Set<Item> itemSet = this.fourPerTileItems.get(item.getFloorLevel());
                if (itemSet == null) {
                    itemSet = new HashSet<Item>();
                }
                final int count = this.getFourPerTileCount(item.getFloorLevel());
                if (item.isPlanted() && count >= 4) {
                    VolaTileItems.logger.info("Unplanted " + item.getName() + " (" + item.getWurmId() + ") as tile " + item.getTileX() + "," + item.getTileY() + " already has " + count + " items");
                    item.setIsPlanted(false);
                }
                itemSet.add(item);
                this.fourPerTileItems.put(item.getFloorLevel(), itemSet);
            }
            final int fl = this.trimFloorLevel(item.getFloorLevel());
            if (fl + 1 > this.itemsPerLevel.length) {
                this.itemsPerLevel = this.createNewLevelArrayAt(fl + 1);
            }
            if (item.isDecoration()) {
                if (fl + 1 > this.decoItemsPerLevel.length) {
                    this.decoItemsPerLevel = this.createNewDecoLevelArrayAt(fl + 1);
                }
                this.incrementDecoItemsOnLevelByOne(fl);
            }
            this.allItems.add(item);
            this.incrementItemsOnLevelByOne(fl);
            if (item.isTent()) {
                Items.addTent(item);
            }
            if (item.isRoadMarker() && item.isPlanted() && Features.Feature.HIGHWAYS.isEnabled()) {
                Items.addMarker(item);
            }
            if (item.getTemplateId() == 677 && item.isPlanted()) {
                Items.addGmSign(item);
            }
            if (item.getTemplateId() == 1309 && item.isPlanted() && Features.Feature.HIGHWAYS.isEnabled()) {
                Items.addWagonerContainer(item);
            }
            if (item.isAlwaysPoll()) {
                this.addAlwaysPollItem(item);
            }
            if (item.isSpawnPoint()) {
                Items.addSpawn(item);
            }
            return true;
        }
        return false;
    }
    
    private final int[] createNewLevelArrayAt(final int floorLevels) {
        final int[] newArr = new int[floorLevels];
        for (int level = 0; level < floorLevels; ++level) {
            if (level < this.itemsPerLevel.length) {
                newArr[level] = this.itemsPerLevel[level];
            }
        }
        return newArr;
    }
    
    private final int[] createNewDecoLevelArrayAt(final int floorLevels) {
        final int[] newArr = new int[floorLevels];
        for (int level = 0; level < floorLevels; ++level) {
            if (level < this.decoItemsPerLevel.length) {
                newArr[level] = this.decoItemsPerLevel[level];
            }
        }
        return newArr;
    }
    
    public final boolean contains(final Item item) {
        return this.allItems.contains(item);
    }
    
    public final boolean removeItem(final Item item) {
        if (item.isAlwaysPoll()) {
            this.removeAlwaysPollItem(item);
        }
        final int fl = this.trimFloorLevel(item.getFloorLevel());
        this.decrementItemsOnLevelByOne(fl);
        if (item.isDecoration()) {
            this.decrementDecoItemsOnLevelByOne(fl);
        }
        this.allItems.remove(item);
        if (item.isFire()) {
            this.hasFire = this.stillHasFire();
        }
        if (item.isOnePerTile()) {
            this.onePerTileItems.remove(item.getFloorLevel());
        }
        if (item.isTent()) {
            Items.removeTent(item);
        }
        if (item.isSpawnPoint()) {
            Items.removeSpawn(item);
        }
        return this.isEmpty();
    }
    
    final void destroy(final VolaTile toSendTo) {
        for (final Item i : this.allItems) {
            toSendTo.sendRemoveItem(i);
        }
        for (final Item pile : this.pileItems.values()) {
            toSendTo.sendRemoveItem(pile);
        }
    }
    
    void moveToNewFloorLevel(final Item item, final int oldFloorLevel) {
        if (item.getFloorLevel() != oldFloorLevel) {
            final int fl = this.trimFloorLevel(item.getFloorLevel());
            final int old = this.trimFloorLevel(oldFloorLevel);
            if (fl + 1 > this.itemsPerLevel.length) {
                this.itemsPerLevel = this.createNewLevelArrayAt(fl + 1);
            }
            this.incrementItemsOnLevelByOne(fl);
            this.decrementItemsOnLevelByOne(old);
            if (item.isDecoration()) {
                if (fl + 1 > this.decoItemsPerLevel.length) {
                    this.decoItemsPerLevel = this.createNewDecoLevelArrayAt(fl + 1);
                }
                this.incrementDecoItemsOnLevelByOne(fl);
                this.decrementDecoItemsOnLevelByOne(old);
            }
        }
    }
    
    boolean movePileItemToNewFloorLevel(final Item item, final int oldFloorLevel) {
        if (item.getFloorLevel() != oldFloorLevel) {
            final Item oldPile = this.pileItems.get(oldFloorLevel);
            if (oldPile == item) {
                this.pileItems.remove(oldFloorLevel);
            }
            final Item newPile = this.pileItems.get(item.getFloorLevel());
            if (newPile == null) {
                this.pileItems.put(item.getFloorLevel(), item);
            }
            else if (newPile != item) {
                return true;
            }
        }
        return false;
    }
    
    final Item[] getPileItems() {
        return this.pileItems.values().toArray(new Item[this.pileItems.size()]);
    }
    
    private boolean stillHasFire() {
        return this.getCampfire(true) != null;
    }
    
    protected final boolean hasOnePerTileItem(final int floorLevel) {
        return this.onePerTileItems.get(floorLevel) != null;
    }
    
    protected final Item getOnePerTileItem(final int floorLevel) {
        return this.onePerTileItems.get(floorLevel);
    }
    
    protected final int getFourPerTileCount(final int floorLevel) {
        final Set<Item> itemSet = this.fourPerTileItems.get(floorLevel);
        if (itemSet == null) {
            return 0;
        }
        int count = 0;
        for (final Item item : itemSet) {
            if (item.isPlanted()) {
                ++count;
            }
            if (item.getTemplateId() == 1311) {
                ++count;
            }
        }
        return count;
    }
    
    public final boolean hasFire() {
        return this.hasFire;
    }
    
    public final Item[] getAllItemsAsArray() {
        return this.allItems.toArray(new Item[this.allItems.size()]);
    }
    
    public final Set<Item> getAllItemsAsSet() {
        return this.allItems;
    }
    
    private final int trimFloorLevel(final int floorLevel) {
        return Math.min(20, Math.max(0, floorLevel));
    }
    
    public final int getNumberOfItems(final int floorLevel) {
        if (this.itemsPerLevel == null) {
            return 0;
        }
        if (this.itemsPerLevel.length - 1 < this.trimFloorLevel(floorLevel)) {
            return 0;
        }
        return this.itemsPerLevel[this.trimFloorLevel(floorLevel)];
    }
    
    public final int getNumberOfDecorations(final int floorLevel) {
        if (this.decoItemsPerLevel == null) {
            return 0;
        }
        if (this.decoItemsPerLevel.length - 1 < this.trimFloorLevel(floorLevel)) {
            return 0;
        }
        return this.decoItemsPerLevel[this.trimFloorLevel(floorLevel)];
    }
    
    void poll(final boolean pollItems, final int seed, final boolean lava, final Structure structure, final boolean surfaced, final Village village, final long now) {
        if (pollItems) {
            final Item[] lTempItems = this.getAllItemsAsArray();
            for (int x = 0; x < lTempItems.length; ++x) {
                if (lava && !lTempItems[x].isIndestructible() && !lTempItems[x].isHugeAltar()) {
                    lTempItems[x].setDamage(lTempItems[x].getDamage() + 1.0f);
                }
                else {
                    lTempItems[x].poll((structure != null && structure.isFinished()) || !surfaced, village != null, 1L);
                }
            }
        }
        else if (this.alwaysPoll != null) {
            final Item[] lTempItems = this.alwaysPoll.toArray(new Item[this.alwaysPoll.size()]);
            for (int x = 0; x < lTempItems.length; ++x) {
                if (!lTempItems[x].poll((structure != null && structure.isFinished()) || !surfaced, village != null, seed) && lava) {
                    lTempItems[x].setDamage(lTempItems[x].getDamage() + 0.1f);
                }
            }
        }
    }
    
    private void addAlwaysPollItem(final Item item) {
        if (this.alwaysPoll == null) {
            this.alwaysPoll = new HashSet<Item>();
        }
        this.alwaysPoll.add(item);
    }
    
    private void removeAlwaysPollItem(final Item item) {
        if (this.alwaysPoll != null) {
            this.alwaysPoll.remove(item);
            if (this.alwaysPoll.isEmpty()) {
                this.alwaysPoll = null;
            }
        }
    }
    
    final void removePileItem(final int floorLevel) {
        this.pileItems.remove(floorLevel);
    }
    
    final boolean checkIfCreatePileItem(final int floorLevel) {
        if (this.itemsPerLevel.length - 1 < this.trimFloorLevel(floorLevel)) {
            return false;
        }
        int decoItems = 0;
        if (this.decoItemsPerLevel.length > this.trimFloorLevel(floorLevel)) {
            decoItems = this.decoItemsPerLevel[this.trimFloorLevel(floorLevel)];
        }
        return this.itemsPerLevel[this.trimFloorLevel(floorLevel)] > decoItems + 3 - 1 && this.itemsPerLevel[this.trimFloorLevel(floorLevel)] >= 3;
    }
    
    final boolean checkIfRemovePileItem(final int floorLevel) {
        if (this.itemsPerLevel.length - 1 < this.trimFloorLevel(floorLevel)) {
            return false;
        }
        int decoItems = 0;
        if (this.decoItemsPerLevel.length > this.trimFloorLevel(floorLevel)) {
            decoItems = this.decoItemsPerLevel[this.trimFloorLevel(floorLevel)];
        }
        return this.itemsPerLevel[this.trimFloorLevel(floorLevel)] < decoItems + 3;
    }
    
    final Item getPileItem(final int floorLevel) {
        return this.pileItems.get(floorLevel);
    }
    
    final void addPileItem(final Item pile) {
        this.pileItems.put(pile.getFloorLevel(), pile);
    }
    
    final Item getCampfire(final boolean requiresBurning) {
        for (final Item i : this.allItems) {
            if (i.getTemplateId() == 37 && (!requiresBurning || i.getTemperature() >= 1000)) {
                return i;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        return this.allItems == null || this.allItems.size() == 0;
    }
    
    static {
        logger = Logger.getLogger(VolaTileItems.class.getName());
    }
}
