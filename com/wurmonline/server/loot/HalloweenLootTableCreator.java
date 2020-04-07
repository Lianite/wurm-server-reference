// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class HalloweenLootTableCreator
{
    protected static final Logger logger;
    
    private static Optional<Item> itemCreator(final Creature victim, final Creature receiver, final LootItem lootItem, final byte material) {
        final byte rarity = LootTable.rollForRarity(receiver);
        final float quality = 50.0f + Server.rand.nextFloat() * 40.0f;
        final Optional<Item> item = ItemFactory.createItemOptional(lootItem.getItemTemplateId(), quality, rarity, material, victim.getName());
        final byte rarity2;
        item.ifPresent(i -> {
            i.setMaterial(material);
            i.setRarity(rarity2);
            return;
        });
        return item;
    }
    
    private static boolean lootPoolChance(final Creature victim, final Creature receiver, final LootPool pool) {
        final int r = pool.getRandom().nextInt((int)(650.0f / victim.getBaseCombatRating()));
        final boolean success = victim.isUnique() || r == 0;
        if (!success) {
            HalloweenLootTableCreator.logger.info("Loot Pool Chance failed '" + pool.getName() + "' for " + receiver.getName() + ": r = " + r);
        }
        return success;
    }
    
    private static LootPool[] createPools() {
        final LootItem boneSkullMask = new LootItem().setItemTemplateId(1428).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)35));
        final LootItem goldSkullMask = new LootItem().setItemTemplateId(1428).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)7));
        final LootItem silverSkullMask = new LootItem().setItemTemplateId(1428).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)8));
        final LootItem oleanderSkullMask = new LootItem().setItemTemplateId(1428).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)51));
        final LootItem trollMask = new LootItem().setItemTemplateId(1321).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)0));
        final LootItem witchHat = new LootItem().setItemTemplateId(1429).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)0)).setItemChance(1.0);
        final LootItem pumpkinShoulders = new LootItem().setItemTemplateId(1322).setItemCreateFunc((victim, receiver, lootItem) -> itemCreator(victim, receiver, lootItem, (byte)0)).setItemChance(0.25);
        return new LootPool[] { new LootPool().setName("halloween: shoulders & hat").addExcludeIds(11, 26, 27, 23).setActiveFunc((victim, receiver) -> (WurmCalendar.isHalloween() || Servers.localServer.testServer) && victim.isHunter()).setLootPoolChanceFunc(HalloweenLootTableCreator::lootPoolChance).setLootItems(new LootItem[] { pumpkinShoulders, witchHat }).setItemFunc(new PercentChanceLootItemFunc()), new LootPool().setName("halloween: troll mask & skull mask").addIncludeIds(11, 27).setActiveFunc((victim, receiver) -> WurmCalendar.isHalloween() || Servers.localServer.testServer).setLootPoolChance(0.04).setLootPoolChanceFunc(new IncreasingChanceLootPoolChanceFunc().setPercentIncrease(0.001)).setLootItems(new LootItem[] { boneSkullMask, goldSkullMask, silverSkullMask, oleanderSkullMask, trollMask }).setItemFunc(new RandomIndexLootItemFunc()), new LootPool().setName("halloween: skull mask only").addIncludeIds(26, 23).setActiveFunc((victim, receiver) -> WurmCalendar.isHalloween() || Servers.localServer.testServer).setLootPoolChance(0.04).setLootPoolChanceFunc(new IncreasingChanceLootPoolChanceFunc().setPercentIncrease(0.001)).setLootItems(new LootItem[] { boneSkullMask, goldSkullMask, silverSkullMask, oleanderSkullMask }).setItemFunc(new RandomIndexLootItemFunc()) };
    }
    
    public static void initialize() {
        final LootPool[] halloweenPools = createPools();
        for (final CreatureTemplate template : CreatureTemplateFactory.getInstance().getTemplates()) {
            if (template.isHunter()) {
                template.addLootPool(halloweenPools);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(LootTableCreator.class.getName());
    }
}
