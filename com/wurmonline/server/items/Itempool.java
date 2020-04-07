// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.Items;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Itempool implements MiscConstants
{
    private static final Logger logger;
    private static final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Item>> recycleds;
    private static final int MAX_ITEMS_IN_POOL = 200;
    
    public static void checkRecycledItems() {
    }
    
    static void addRecycledItem(final Item item) {
        ConcurrentLinkedQueue<Item> dset = getRecycledItemForTemplateID(item.getTemplateId());
        if (dset == null) {
            dset = new ConcurrentLinkedQueue<Item>();
            Itempool.recycleds.put(item.getTemplateId(), dset);
        }
        if (dset.size() >= 200) {
            Items.decay(item.getWurmId(), item.getDbStrings());
        }
        else {
            dset.add(item);
        }
    }
    
    private static ConcurrentLinkedQueue<Item> getRecycledItemForTemplateID(final int aTemplateId) {
        final ConcurrentLinkedQueue<Item> dset = Itempool.recycleds.get(aTemplateId);
        return dset;
    }
    
    public static void returnRecycledItem(final Item item) {
        item.setZoneId(-10, true);
        item.setBanked(true);
        item.data = null;
        item.ownerId = -10L;
        item.lastOwner = -10L;
        item.parentId = -10L;
        ItemFactory.clearData(item.id);
        ConcurrentLinkedQueue<Item> dset = getRecycledItemForTemplateID(item.getTemplateId());
        if (dset == null) {
            dset = new ConcurrentLinkedQueue<Item>();
            Itempool.recycleds.put(item.getTemplateId(), dset);
        }
        if (dset.size() >= 200) {
            Items.decay(item.getWurmId(), item.getDbStrings());
        }
        else {
            item.setSettings(0);
            item.setRealTemplate(-10);
            dset.add(item);
        }
    }
    
    static Item getRecycledItem(final int templateId, final float qualityLevel) {
        Item toReturn = null;
        final ConcurrentLinkedQueue<Item> dset = getRecycledItemForTemplateID(templateId);
        if (dset == null) {
            return null;
        }
        toReturn = dset.poll();
        if (toReturn == null) {
            return toReturn;
        }
        toReturn.setBanked(false);
        toReturn.deleted = false;
        toReturn.setSettings(0);
        toReturn.setRealTemplate(-10);
        Items.putItem(toReturn);
        return toReturn;
    }
    
    public static void deleteItem(final int templateId, final long wurmid) {
        final ConcurrentLinkedQueue<Item> dset = getRecycledItemForTemplateID(templateId);
        if (dset == null || dset.isEmpty()) {
            return;
        }
        final Item[] array;
        final Item[] items = array = dset.toArray(new Item[0]);
        for (final Item i : array) {
            if (i.getWurmId() == wurmid) {
                dset.remove(i);
                return;
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Itempool.class.getName());
        recycleds = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Item>>();
    }
}
