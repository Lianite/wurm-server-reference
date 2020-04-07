// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.Item;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;

public class LootItem
{
    private int itemTemplateId;
    private byte maxRarity;
    private byte minRarity;
    private float minQuality;
    private float maxQuality;
    private double itemChance;
    private ItemCreateFunc itemCreateFunc;
    
    public LootItem() {
        this.minQuality = 1.0f;
        this.maxQuality = 100.0f;
        this.itemChance = 1.0;
        this.itemCreateFunc = new DefaultItemCreateFunc();
    }
    
    public LootItem(final int aItemTemplateId, final byte aMinRarity, final byte aMaxRarity, final float aMinQuality, final float aMaxQuality, final double aItemChance, final ItemCreateFunc aItemCreateFunc) {
        this(aItemTemplateId, aMinRarity, aMaxRarity, aMinQuality, aMaxQuality, aItemChance);
        this.itemCreateFunc = aItemCreateFunc;
    }
    
    public LootItem(final int aItemTemplateId, final byte aMinRarity, final byte aMaxRarity, final float aMinQuality, final float aMaxQuality, final double aItemChance) {
        this.minQuality = 1.0f;
        this.maxQuality = 100.0f;
        this.itemChance = 1.0;
        this.itemCreateFunc = new DefaultItemCreateFunc();
        this.itemTemplateId = aItemTemplateId;
        this.maxRarity = aMaxRarity;
        this.minRarity = aMinRarity;
        this.maxQuality = aMaxQuality;
        this.minQuality = aMinQuality;
        this.itemChance = aItemChance;
    }
    
    public byte getMaxRarity() {
        return this.maxRarity;
    }
    
    public LootItem setMaxRarity(final byte aMaxRarity) {
        this.maxRarity = aMaxRarity;
        return this;
    }
    
    public byte getMinRarity() {
        return this.minRarity;
    }
    
    public LootItem setMinRarity(final byte aMinRarity) {
        this.minRarity = aMinRarity;
        return this;
    }
    
    public float getMaxQuality() {
        return this.maxQuality;
    }
    
    public LootItem setMaxQuality(final float aMaxQuality) {
        this.maxQuality = aMaxQuality;
        return this;
    }
    
    public float getMinQuality() {
        return this.minQuality;
    }
    
    public LootItem setMinQuality(final float aMinQuality) {
        this.minQuality = aMinQuality;
        return this;
    }
    
    public double getItemChance() {
        return this.itemChance;
    }
    
    public LootItem setItemChance(final double itemChance) {
        this.itemChance = itemChance;
        return this;
    }
    
    public LootItem setItemCreateFunc(final ItemCreateFunc func) {
        this.itemCreateFunc = func;
        return this;
    }
    
    public Optional<Item> createItem(final Creature victim, final Creature receiver) {
        return this.itemCreateFunc.create(victim, receiver, this);
    }
    
    public int getItemTemplateId() {
        return this.itemTemplateId;
    }
    
    public LootItem setItemTemplateId(final int id) {
        this.itemTemplateId = id;
        return this;
    }
    
    public String getItemName() {
        try {
            return ItemTemplateFactory.getInstance().getTemplate(this.getItemTemplateId()).getName();
        }
        catch (NoSuchTemplateException e) {
            e.printStackTrace();
            return "<invalid template for id# " + this.getItemTemplateId() + ">";
        }
    }
}
