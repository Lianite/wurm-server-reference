// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.Arrays;
import com.wurmonline.server.items.Item;
import java.util.Optional;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.behaviours.MethodsItems;
import java.util.Collection;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.logging.Logger;

public class LootPool
{
    protected static final Logger logger;
    private String name;
    private double lootPoolChance;
    private boolean groupLoot;
    private List<LootItem> lootItems;
    private Random random;
    private List<Integer> excludeIds;
    private List<Integer> includeIds;
    private ActiveFunc activeFunc;
    private ItemMessageFunc itemLootMessageFunc;
    private LootPoolChanceFunc lootPoolChanceFunc;
    private LootItemFunc itemFunc;
    
    public LootPool() {
        this.lootItems = new ArrayList<LootItem>();
        this.random = new Random();
        this.excludeIds = new ArrayList<Integer>();
        this.includeIds = new ArrayList<Integer>();
        this.activeFunc = new DefaultActiveFunc();
        this.itemLootMessageFunc = new DefaultItemMessageFunc();
        this.lootPoolChanceFunc = new PercentChanceLootPoolChanceFunc();
        this.itemFunc = new PercentChanceLootItemFunc();
    }
    
    public LootPool(final int aChance) {
        this.lootItems = new ArrayList<LootItem>();
        this.random = new Random();
        this.excludeIds = new ArrayList<Integer>();
        this.includeIds = new ArrayList<Integer>();
        this.activeFunc = new DefaultActiveFunc();
        this.itemLootMessageFunc = new DefaultItemMessageFunc();
        this.lootPoolChanceFunc = new PercentChanceLootPoolChanceFunc();
        this.itemFunc = new PercentChanceLootItemFunc();
        this.lootPoolChance = aChance;
    }
    
    public LootPool(final int aChance, final ActiveFunc aActiveFunc) {
        this(aChance);
        this.activeFunc = aActiveFunc;
    }
    
    public List<LootItem> getLootItems() {
        return this.lootItems;
    }
    
    public LootPool setLootItems(final List<LootItem> items) {
        this.lootItems = items;
        return this;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public String getName() {
        return this.name;
    }
    
    public LootPool setName(final String name) {
        this.name = name;
        return this;
    }
    
    public LootPool setItemLootMessageFunc(final ItemMessageFunc itemLootMessageFunc) {
        this.itemLootMessageFunc = itemLootMessageFunc;
        return this;
    }
    
    public boolean isGroupLoot() {
        return this.groupLoot;
    }
    
    public LootPool setGroupLoot(final boolean groupLoot) {
        this.groupLoot = groupLoot;
        return this;
    }
    
    public boolean isActive(final Creature victim, final Creature receiver) {
        if (this.excludeIds.contains(victim.getTemplate().getTemplateId())) {
            LootPool.logger.info("Skipping loot pool '" + this.getName() + "' for " + receiver.getName() + ": " + victim.getTemplate().getTemplateId() + " is excluded (" + victim.getTemplate().getName() + ").");
            return false;
        }
        if (this.includeIds.size() > 0 && !this.includeIds.contains(victim.getTemplate().getTemplateId())) {
            LootPool.logger.info("Skipping loot pool '" + this.getName() + "' for " + receiver.getName() + ": " + victim.getTemplate().getTemplateId() + " is not included (" + victim.getTemplate().getName() + ").");
            return false;
        }
        return this.activeFunc.active(victim, receiver);
    }
    
    public LootPool setActiveFunc(final ActiveFunc func) {
        this.activeFunc = func;
        return this;
    }
    
    public LootPool setItemFunc(final LootItemFunc func) {
        this.itemFunc = func;
        return this;
    }
    
    protected void awardLootItem(final Creature victim, final HashSet<Creature> receivers) {
        if (this.isGroupLoot()) {
            receivers.forEach(receiver -> this.awardLootItem(victim, receiver));
        }
        else if (receivers.size() > 0) {
            this.awardLootItem(victim, new ArrayList<Creature>(receivers).get(this.random.nextInt(receivers.size())));
        }
    }
    
    protected void awardLootItem(final Creature victim, final Creature receiver) {
        if (!this.isActive(victim, receiver)) {
            return;
        }
        if (!this.passedChanceRoll(victim, receiver)) {
            return;
        }
        final Optional<Item> item = this.itemFunc.item(victim, receiver, this).flatMap(loot -> loot.createItem(victim, receiver));
        item.ifPresent(i -> {
            receiver.getInventoryOptional().ifPresent(inv -> inv.insertItem(i));
            this.sendLootMessages(victim, receiver, i);
            LootPool.logger.info("Awarding loot " + MethodsItems.getRarityName(i.getRarity()) + " " + i.getName() + " [" + Materials.convertMaterialByteIntoString(i.getMaterial()) + "] (" + i.getWurmId() + ") to " + receiver.getName() + " (" + receiver.getWurmId() + ")");
        });
    }
    
    public double getLootPoolChance() {
        return this.lootPoolChance;
    }
    
    public LootPool setLootPoolChance(final double aWeight) {
        this.lootPoolChance = aWeight;
        return this;
    }
    
    public LootPool setLootItems(final LootItem[] items) {
        return this.setLootItems(Arrays.asList(items));
    }
    
    public LootPool setLootPoolChanceFunc(final LootPoolChanceFunc func) {
        this.lootPoolChanceFunc = func;
        return this;
    }
    
    public boolean passedChanceRoll(final Creature victim, final Creature receiver) {
        final boolean chance = this.lootPoolChanceFunc.chance(victim, receiver, this);
        if (!chance) {
            LootPool.logger.info("Skipping loot pool '" + this.getName() + "' for " + receiver.getName() + ": failed chance roll.");
        }
        return chance;
    }
    
    public void sendLootMessages(final Creature victim, final Creature receiver, final Item item) {
        this.itemLootMessageFunc.message(victim, receiver, item);
    }
    
    public LootPool addIncludeIds(final Integer... ids) {
        this.includeIds.addAll(Arrays.asList(ids));
        return this;
    }
    
    public LootPool addExcludeIds(final Integer... ids) {
        this.excludeIds.addAll(Arrays.asList(ids));
        return this;
    }
    
    static {
        logger = Logger.getLogger(LootPool.class.getName());
    }
}
