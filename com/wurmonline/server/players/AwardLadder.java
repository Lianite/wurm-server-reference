// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.ItemTemplate;
import java.util.Iterator;
import java.util.HashSet;
import com.wurmonline.server.items.Item;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public final class AwardLadder implements MiscConstants, ItemMaterials
{
    private final String name;
    private final String imageUrl;
    private final int monthsRequiredSinceReset;
    private final int totalMonthsRequired;
    private final int silversRequired;
    private final int titleAwarded;
    private final int itemAwarded;
    private final int achievementAwarded;
    private final float qlOfItemAward;
    private static final Logger logger;
    private static final Map<Integer, AwardLadder> resetLadder;
    private static final Map<Integer, AwardLadder> totalLadder;
    private static final Map<Integer, AwardLadder> silverLadder;
    private static final Map<Player, Set<Item>> itemsToAward;
    
    public AwardLadder(final String _name, final String _imageUrl, final int _totalMonthsRequired, final int _monthsRequiredSinceReset, final int _silversRequired, final int _titleAwarded, final int _itemAwarded, final int _achievementAwarded, final float qlAwarded) {
        this.name = _name;
        this.imageUrl = _imageUrl;
        this.monthsRequiredSinceReset = _monthsRequiredSinceReset;
        this.totalMonthsRequired = _totalMonthsRequired;
        this.silversRequired = _silversRequired;
        this.titleAwarded = _titleAwarded;
        this.itemAwarded = _itemAwarded;
        this.achievementAwarded = _achievementAwarded;
        this.qlOfItemAward = qlAwarded;
        if (this.monthsRequiredSinceReset > 0) {
            AwardLadder.resetLadder.put(this.monthsRequiredSinceReset, this);
        }
        if (this.totalMonthsRequired > 0) {
            AwardLadder.totalLadder.put(this.totalMonthsRequired, this);
        }
        if (this.silversRequired > 0) {
            AwardLadder.silverLadder.put(this.silversRequired, this);
        }
    }
    
    public static final AwardLadder getLadderStepForReset(final int months) {
        return AwardLadder.resetLadder.get(months);
    }
    
    public static final AwardLadder[] getLadderStepsForTotal(final int months) {
        final Set<AwardLadder> steps = new HashSet<AwardLadder>();
        for (final AwardLadder step : AwardLadder.totalLadder.values()) {
            if (step.getTotalMonthsRequired() <= months) {
                steps.add(step);
            }
        }
        return steps.toArray(new AwardLadder[steps.size()]);
    }
    
    public static final AwardLadder getLadderStepForSilver(final int silver) {
        return AwardLadder.silverLadder.get(silver);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getImageUrl() {
        return this.imageUrl;
    }
    
    public int getMonthsRequiredReset() {
        return this.monthsRequiredSinceReset;
    }
    
    public int getTotalMonthsRequired() {
        return this.totalMonthsRequired;
    }
    
    public int getSilversRequired() {
        return this.silversRequired;
    }
    
    public int getTitleNumberAwarded() {
        return this.titleAwarded;
    }
    
    public int getItemNumberAwarded() {
        return this.itemAwarded;
    }
    
    public int getAchievementNumberAwarded() {
        return this.achievementAwarded;
    }
    
    public Titles.Title getTitleAwarded() {
        return Titles.Title.getTitle(this.titleAwarded);
    }
    
    public AchievementTemplate getAchievementAwarded() {
        return Achievement.getTemplate(this.achievementAwarded);
    }
    
    public ItemTemplate getItemAwarded() {
        try {
            return ItemTemplateFactory.getInstance().getTemplate(this.itemAwarded);
        }
        catch (NoSuchTemplateException nst) {
            return null;
        }
    }
    
    public static final AwardLadder getNextTotalAward(final int totalMonthsSinceReset) {
        AwardLadder next = null;
        for (final Map.Entry<Integer, AwardLadder> entry : AwardLadder.resetLadder.entrySet()) {
            if (next == null) {
                if (entry.getKey() <= totalMonthsSinceReset) {
                    continue;
                }
                next = entry.getValue();
            }
            else {
                if (entry.getKey() <= totalMonthsSinceReset || entry.getKey() >= next.getMonthsRequiredReset()) {
                    continue;
                }
                next = entry.getValue();
            }
        }
        return next;
    }
    
    public static final void clearItemAwards() {
        for (final Map.Entry<Player, Set<Item>> entry : AwardLadder.itemsToAward.entrySet()) {
            for (final Item i : entry.getValue()) {
                entry.getKey().getInventory().insertItem(i, true);
                if (entry.getKey().getCommunicator() != null) {
                    entry.getKey().getCommunicator().sendSafeServerMessage("You receive " + i.getNameWithGenus() + " as premium bonus!");
                }
            }
        }
        AwardLadder.itemsToAward.clear();
    }
    
    public static final void awardTotalLegacy(final PlayerInfo p) {
        if (p.awards != null) {
            try {
                final AwardLadder[] total = getLadderStepsForTotal(p.awards.getMonthsPaidEver());
                final Set<Item> itemSet = new HashSet<Item>();
                for (final AwardLadder step : total) {
                    if (step.getItemNumberAwarded() > 0) {
                        if (step.getItemNumberAwarded() == 229) {
                            final int numawarded = 274 + Server.rand.nextInt(14);
                            try {
                                final Player player = Players.getInstance().getPlayer(p.wurmId);
                                final Item i = ItemFactory.createItem(numawarded, step.getQlOfItemAward(), (byte)67, (byte)0, "");
                                itemSet.add(i);
                                AwardLadder.itemsToAward.put(player, itemSet);
                            }
                            catch (NoSuchPlayerException nsp) {
                                final long inventoryId = DbCreatureStatus.getInventoryIdFor(p.wurmId);
                                final Item j = ItemFactory.createItem(numawarded, step.getQlOfItemAward(), (byte)67, (byte)0, "");
                                j.setParentId(inventoryId, true);
                                j.setOwnerId(p.wurmId);
                            }
                        }
                        else {
                            try {
                                final Player player2 = Players.getInstance().getPlayer(p.wurmId);
                                final Item k = ItemFactory.createItem(step.getItemNumberAwarded(), step.getQlOfItemAward(), "");
                                itemSet.add(k);
                                AwardLadder.itemsToAward.put(player2, itemSet);
                            }
                            catch (NoSuchPlayerException nsp2) {
                                final long inventoryId2 = DbCreatureStatus.getInventoryIdFor(p.wurmId);
                                final Item l = ItemFactory.createItem(step.getItemNumberAwarded(), step.getQlOfItemAward(), "");
                                l.setParentId(inventoryId2, true);
                                l.setOwnerId(p.wurmId);
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                AwardLadder.logger.log(Level.WARNING, ex.getMessage() + " " + p.getName() + " " + p.awards.getMonthsPaidSinceReset() + ": " + p.awards.getMonthsPaidInARow(), ex);
            }
        }
    }
    
    public static final float consecutiveItemQL(final int consecutiveMonths) {
        return Math.min(100.0f, consecutiveMonths * 16);
    }
    
    public static final void award(final PlayerInfo p, final boolean tickedMonth) {
        if (p.awards != null) {
            try {
                if (tickedMonth) {
                    final float ql = consecutiveItemQL(p.awards.getMonthsPaidInARow());
                    try {
                        final Player player = Players.getInstance().getPlayer(p.wurmId);
                        final Item inventory = player.getInventory();
                        final Item i = ItemFactory.createItem(834, ql, "");
                        inventory.insertItem(i, true);
                        player.getCommunicator().sendSafeServerMessage("You receive " + i.getNameWithGenus() + " at ql " + ql + " for staying premium!");
                    }
                    catch (NoSuchPlayerException nsp) {
                        final long inventoryId = DbCreatureStatus.getInventoryIdFor(p.wurmId);
                        final Item j = ItemFactory.createItem(834, ql, "");
                        j.setParentId(inventoryId, true);
                        j.setOwnerId(p.wurmId);
                    }
                    final AwardLadder sinceReset = getLadderStepForReset(p.awards.getMonthsPaidSinceReset());
                    if (sinceReset != null) {
                        if (sinceReset.getTitleAwarded() != null) {
                            p.addTitle(sinceReset.getTitleAwarded());
                            try {
                                final Player player2 = Players.getInstance().getPlayer(p.wurmId);
                                player2.getCommunicator().sendSafeServerMessage("You receive the title " + sinceReset.getTitleAwarded().getName(player2.getSex() == 0) + " for staying premium!");
                            }
                            catch (NoSuchPlayerException ex2) {}
                        }
                        if (sinceReset.getAchievementNumberAwarded() > 0) {
                            Achievements.triggerAchievement(p.wurmId, sinceReset.getAchievementNumberAwarded());
                        }
                        if (sinceReset.getItemNumberAwarded() > 0) {
                            try {
                                final Player player2 = Players.getInstance().getPlayer(p.wurmId);
                                final Item i = ItemFactory.createItem(sinceReset.getItemNumberAwarded(), sinceReset.getQlOfItemAward(), "");
                                player2.getInventory().insertItem(i, true);
                            }
                            catch (NoSuchPlayerException nsp2) {
                                final long inventoryId2 = DbCreatureStatus.getInventoryIdFor(p.wurmId);
                                final Item k = ItemFactory.createItem(sinceReset.getItemNumberAwarded(), sinceReset.getQlOfItemAward(), "");
                                k.setParentId(inventoryId2, true);
                                k.setOwnerId(p.wurmId);
                            }
                        }
                    }
                }
                else {
                    final AwardLadder silvers = getLadderStepForSilver(p.awards.getSilversPaidEver());
                    if (silvers != null) {
                        if (silvers.getTitleAwarded() != null) {
                            p.addTitle(silvers.getTitleAwarded());
                        }
                        if (silvers.getAchievementNumberAwarded() > 0) {
                            Achievements.triggerAchievement(p.wurmId, silvers.getAchievementNumberAwarded());
                        }
                    }
                }
            }
            catch (Exception ex) {
                AwardLadder.logger.log(Level.WARNING, ex.getMessage() + " " + p.getName() + " " + p.awards.getMonthsPaidSinceReset() + ": " + p.awards.getMonthsPaidInARow(), ex);
            }
        }
    }
    
    private static final void generateLadder() {
        new AwardLadder("Title: Soldier of Lomaner", "", 0, 1, 0, 254, 0, 0, 0.0f);
        new AwardLadder("Achievement: Landed", "", 0, 1, 0, 0, 0, 343, 0.0f);
        new AwardLadder("Achievement: Survived", "", 0, 3, 0, 0, 0, 344, 0.0f);
        new AwardLadder("Title: Rider of Lomaner", "", 0, 4, 0, 255, 0, 0, 0.0f);
        new AwardLadder("Achievement: Scouted", "", 0, 6, 0, 0, 0, 345, 0.0f);
        new AwardLadder("Title: Chieftain of Lomaner", "", 0, 7, 0, 256, 0, 0, 0.0f);
        new AwardLadder("Achievement: Experienced", "", 0, 9, 0, 0, 0, 346, 0.0f);
        new AwardLadder("Title: Ambassador of Lomaner", "", 0, 10, 0, 257, 0, 0, 0.0f);
        new AwardLadder("Item: Spyglass", "", 0, 12, 0, 0, 489, 0, 70.0f);
        new AwardLadder("Achievement: Owning", "", 0, 13, 0, 0, 0, 347, 0.0f);
        new AwardLadder("Title: Baron of Lomaner", "", 0, 14, 0, 258, 0, 0, 0.0f);
        new AwardLadder("Achievement: Shined", "", 0, 16, 0, 0, 0, 348, 0.0f);
        new AwardLadder("Title: Jarl of Lomaner", "", 0, 18, 0, 259, 0, 0, 0.0f);
        new AwardLadder("Achievement: Glittered", "", 0, 20, 0, 0, 0, 349, 0.0f);
        new AwardLadder("Title: Duke of Lomaner", "", 0, 23, 0, 260, 0, 0, 0.0f);
        new AwardLadder("Achievement: Highly Illuminated", "", 0, 26, 0, 0, 0, 350, 0.0f);
        new AwardLadder("Title: Provost of Lomaner", "", 0, 30, 0, 261, 0, 0, 0.0f);
        new AwardLadder("Achievement: Foundation Pillar", "", 0, 36, 0, 0, 0, 351, 0.0f);
        new AwardLadder("Title: Marquis of Lomaner", "", 0, 40, 0, 262, 0, 0, 0.0f);
        new AwardLadder("Achievement: Revered One", "", 0, 48, 0, 0, 0, 352, 0.0f);
        new AwardLadder("Title: Grand Duke of Lomaner", "", 0, 54, 0, 263, 0, 0, 0.0f);
        new AwardLadder("Achievement: Patron Of The Net", "", 0, 60, 0, 0, 0, 353, 0.0f);
        new AwardLadder("Title: Viceroy of Lomaner", "", 0, 70, 0, 264, 0, 0, 0.0f);
        new AwardLadder("Achievement: Myth Or Legend?", "", 0, 80, 0, 0, 0, 354, 0.0f);
        new AwardLadder("Title: Prince of Lomaner", "", 0, 100, 0, 265, 0, 0, 0.0f);
        new AwardLadder("Achievement: Atlas Reincarnated", "", 0, 120, 0, 0, 0, 355, 0.0f);
        new AwardLadder("1 month", "", 1, 0, 0, 0, 834, 0, 50.0f);
        new AwardLadder("3 months", "", 3, 0, 0, 0, 700, 0, 80.0f);
        new AwardLadder("6 months", "", 6, 0, 0, 0, 466, 0, 99.0f);
        new AwardLadder("9 months", "", 9, 0, 0, 0, 837, 0, 80.0f);
        new AwardLadder("12 months", "", 12, 0, 0, 0, 229, 0, 30.0f);
        new AwardLadder("15 months", "", 15, 0, 0, 0, 837, 0, 90.0f);
        new AwardLadder("24 months", "", 24, 0, 0, 0, 229, 0, 90.0f);
        new AwardLadder("30 months", "", 30, 0, 0, 0, 837, 0, 90.0f);
        new AwardLadder("36 months", "", 36, 0, 0, 0, 668, 0, 40.0f);
        new AwardLadder("42 months", "", 42, 0, 0, 0, 837, 0, 93.0f);
        new AwardLadder("48 months", "", 48, 0, 0, 0, 837, 0, 94.0f);
        new AwardLadder("54 months", "", 54, 0, 0, 0, 837, 0, 95.0f);
        new AwardLadder("60 months", "", 60, 0, 0, 0, 837, 0, 96.0f);
        AwardLadder.logger.info("Finished generating AwardLadder");
    }
    
    public float getQlOfItemAward() {
        return this.qlOfItemAward;
    }
    
    static {
        logger = Logger.getLogger(AwardLadder.class.getName());
        resetLadder = new ConcurrentHashMap<Integer, AwardLadder>();
        totalLadder = new ConcurrentHashMap<Integer, AwardLadder>();
        silverLadder = new ConcurrentHashMap<Integer, AwardLadder>();
        itemsToAward = new ConcurrentHashMap<Player, Set<Item>>();
        generateLadder();
    }
}
