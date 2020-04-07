// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.Features;
import java.awt.Color;
import com.wurmonline.server.utils.BMLBuilder;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Optional;
import com.wurmonline.server.Players;
import java.util.HashMap;
import java.util.logging.Logger;

public class PlayerJournal implements AchievementList
{
    protected static final Logger logger;
    public static final byte JOUR_TIER_TUT = 0;
    public static final byte JOUR_TIER_BASIC1 = 1;
    public static final byte JOUR_TIER_BASIC2 = 2;
    public static final byte JOUR_TIER_BASIC3 = 3;
    public static final byte JOUR_TIER_INTERMEDIATE1 = 4;
    public static final byte JOUR_TIER_INTERMEDIATE2 = 5;
    public static final byte JOUR_TIER_INTERMEDIATE3 = 6;
    public static final byte JOUR_TIER_EXPERT1 = 7;
    public static final byte JOUR_TIER_EXPERT2 = 8;
    public static final byte JOUR_TIER_EXPERT3 = 9;
    public static final byte JOUR_TIER_PRIEST1 = 10;
    public static final byte JOUR_TIER_PRIEST2 = 11;
    public static final byte JOUR_TIER_PRIEST3 = 12;
    private static final HashMap<Byte, JournalTier> allTiers;
    
    public static HashMap<Byte, JournalTier> getAllTiers() {
        return PlayerJournal.allTiers;
    }
    
    public static void achievementTriggered(final long playerId, final int achievementId) {
        final Optional<JournalTier> theTier = PlayerJournal.allTiers.values().stream().filter(t -> t.containsAchievement(achievementId)).findFirst();
        if (theTier.isPresent() && theTier.get().isVisible(playerId)) {
            final Optional<Player> p = Players.getInstance().getPlayerOptional(playerId);
            final Optional<JournalTier> optional;
            final JournalTier nextTier;
            p.ifPresent(ply -> {
                ply.getCommunicator().sendPersonalJournalAchvUpdate(optional.get().getTierId(), achievementId, true);
                if (optional.get().shouldUnlockNextTier(playerId)) {
                    nextTier = optional.get().getNextTier();
                    sendTierUnlock(ply, nextTier);
                }
                return;
            });
            if (theTier.get().shouldUnlockReward(playerId)) {
                theTier.get().awardReward(playerId);
                final Optional<JournalTier> optional2;
                p.ifPresent(ply -> ply.getCommunicator().sendPersonalJournalTierUpdate(optional2.get().getTierId(), true, optional2.get().getRewardString()));
            }
        }
    }
    
    public static void subAchievementCounterTick(final long playerId, final int subAchievementId) {
        final Optional<JournalTier> subAchTier = PlayerJournal.allTiers.values().stream().filter(t -> t.containsAchievement(subAchievementId)).findFirst();
        if (subAchTier.isPresent() && subAchTier.get().isVisible(playerId)) {
            final Optional<Player> p = Players.getInstance().getPlayerOptional(playerId);
            p.ifPresent(ply -> ply.getCommunicator().sendPersonalJournalAchvUpdate(subAchTier.get().getTierId(), subAchievementId, Achievements.getAchievementObject(playerId).getAchievement(subAchievementId) != null));
        }
    }
    
    public static void sendTierUnlock(@Nullable final Player p, @Nullable final JournalTier toUnlock) {
        if (p == null || toUnlock == null) {
            return;
        }
        final Achievements ach = Achievements.getAchievementObject(p.getWurmId());
        final int[] achievementIds = toUnlock.getAchievementList().stream().mapToInt(i -> i).toArray();
        final boolean[] achievementsCompleted = new boolean[achievementIds.length];
        for (int j = 0; j < achievementIds.length; ++j) {
            if (ach != null) {
                achievementsCompleted[j] = (ach.getAchievement(achievementIds[j]) != null);
            }
            else {
                achievementsCompleted[j] = false;
            }
        }
        p.getCommunicator().sendSafeServerMessage("Congratulations, you have now unlocked " + toUnlock.getTierName() + " in your journal.", (byte)2);
        p.getCommunicator().sendPersonalJournalTier(toUnlock.getTierId(), toUnlock.getTierName(), toUnlock.getRewardString(), toUnlock.isRewardUnlocked(p.getWurmId()) || toUnlock.hasBeenAwarded(p.getWurmId()), achievementIds, achievementsCompleted);
        if (toUnlock.isNextTierUnlocked(p.getWurmId())) {
            if (toUnlock.isRewardUnlocked(p.getWurmId()) && !toUnlock.hasBeenAwarded(p.getWurmId())) {
                toUnlock.awardReward(p.getWurmId());
            }
            sendTierUnlock(p, toUnlock.getNextTier());
        }
    }
    
    public static void sendPersonalJournal(@Nullable final Player plr) {
        if (plr == null) {
            return;
        }
        for (final JournalTier tier : PlayerJournal.allTiers.values()) {
            if (!tier.isVisible(plr.getWurmId())) {
                continue;
            }
            if (tier.isRewardUnlocked(plr.getWurmId()) && !tier.hasBeenAwarded(plr.getWurmId())) {
                tier.awardReward(plr.getWurmId());
            }
            final Achievements ach = Achievements.getAchievementObject(plr.getWurmId());
            final int[] achievementIds = tier.getAchievementList().stream().mapToInt(i -> i).toArray();
            final boolean[] achievementsCompleted = new boolean[achievementIds.length];
            for (int j = 0; j < achievementIds.length; ++j) {
                if (ach != null) {
                    achievementsCompleted[j] = (ach.getAchievement(achievementIds[j]) != null);
                }
                else {
                    achievementsCompleted[j] = false;
                }
            }
            plr.getCommunicator().sendPersonalJournalTier(tier.getTierId(), tier.getTierName(), tier.getRewardString(), tier.isRewardUnlocked(plr.getWurmId()) || tier.hasBeenAwarded(plr.getWurmId()), achievementIds, achievementsCompleted);
        }
    }
    
    public static void sendJournalInfoBML(@Nullable final Player p, final long targetId) {
        if (p == null) {
            return;
        }
        if (targetId == -10L) {
            return;
        }
        final BMLBuilder toSend = BMLBuilder.createVertArrayNode(false);
        final Achievements ach = Achievements.getAchievementObject(targetId);
        for (final JournalTier tier : PlayerJournal.allTiers.values()) {
            final int[] achievementIds = tier.getAchievementList().stream().mapToInt(i -> i).toArray();
            final boolean[] achievementsCompleted = new boolean[achievementIds.length];
            for (int j = 0; j < achievementIds.length; ++j) {
                if (ach != null) {
                    achievementsCompleted[j] = (ach.getAchievement(achievementIds[j]) != null);
                }
                else {
                    achievementsCompleted[j] = false;
                }
            }
            toSend.addLabel(tier.getTierName(), tier.getRewardString(), BMLBuilder.TextType.BOLD, tier.isRewardUnlocked(targetId) ? (tier.hasBeenAwarded(targetId) ? Color.GREEN : Color.YELLOW) : (tier.isVisible(targetId) ? Color.LIGHT_GRAY : Color.GRAY));
            for (int j = 0; j < achievementIds.length; ++j) {
                final AchievementTemplate t = Achievement.getTemplate(achievementIds[j]);
                if (t == null) {
                    PlayerJournal.logger.warning("AchievementTemplate for ID# " + achievementIds[j] + " is null");
                }
                else {
                    toSend.addLabel("  - " + t.getName(), achievementsCompleted[j] ? t.getDescription() : t.getRequirement(), null, achievementsCompleted[j] ? Color.GREEN : (tier.isVisible(targetId) ? Color.LIGHT_GRAY : Color.GRAY));
                }
            }
        }
        toSend.addText("\r\n\r\nKey:", null, BMLBuilder.TextType.BOLD, Color.WHITE);
        toSend.addText("Not visible to player", null, null, Color.GRAY);
        toSend.addText("Visible to player, incomplete", null, null, Color.LIGHT_GRAY);
        toSend.addText("Completed", null, null, Color.GREEN);
        toSend.addText("Completed but unrewarded (should reward on login)", null, null, Color.YELLOW, 300, 40);
        p.getCommunicator().sendBml(300, 500, true, true, BMLBuilder.createBMLBorderPanel(null, null, BMLBuilder.createScrollPanelNode(true, false).addString(toSend.toString()), null, null).toString(), 200, 200, 200, "Journal Info: " + PlayerInfoFactory.getPlayerName(targetId));
    }
    
    static {
        logger = Logger.getLogger(PlayerJournal.class.getName());
        allTiers = new HashMap<Byte, JournalTier>();
        final JournalTier tutorialTier = new JournalTier((byte)0, "First Steps", (byte)(-1), (byte)1, 6, 64, new int[] { 513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, Features.Feature.HIGHWAYS.isEnabled() ? 524 : 576 });
        tutorialTier.setReward(new JournalReward("Apprentice Title & 1hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T0);
                p.getSaveFile().addToSleep(3600);
            }
        });
        final JournalTier basicTier1 = new JournalTier((byte)1, "Finding the Path", (byte)0, (byte)2, 5, 65, new int[] { 525, 526, 529, 530, 531, 532, 533, 534, 535, 536, 537 });
        basicTier1.setReward(new JournalReward("Learned Title & 1hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T1);
                p.getSaveFile().addToSleep(3600);
            }
        });
        final JournalTier basicTier2 = new JournalTier((byte)2, "Gathering Stride", (byte)1, (byte)3, 7, 66, new int[] { 538, 539, 540, 541, 542, 231, 543, 544, 545, 546, 301, 547, 548, 549 });
        basicTier2.setReward(new JournalReward("Experienced Title & 2hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T2);
                p.getSaveFile().addToSleep(7200);
            }
        });
        final JournalTier basicTier3 = new JournalTier((byte)3, "Paved with Stone", (byte)2, (byte)4, 7, 67, new int[] { 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563 });
        basicTier3.setReward(new JournalReward("Skilled Title & 2hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T3);
                p.getSaveFile().addToSleep(7200);
            }
        });
        final JournalTier intermediateTier1 = new JournalTier((byte)4, "On the Highway", (byte)3, (byte)5, 6, 68, new int[] { 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575 });
        intermediateTier1.setReward(new JournalReward("Accomplished Title, Glimmersteel or Adamantine Lump & 3hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T4);
                p.getSaveFile().addToSleep(10800);
                try {
                    int templateId = 698;
                    if (Server.rand.nextBoolean()) {
                        templateId = 694;
                    }
                    final Item lump = ItemFactory.createItem(templateId, Server.rand.nextFloat() * 50.0f + 40.0f, p.getName());
                    p.getInventory().insertItem(lump, true);
                }
                catch (FailedException ex) {}
                catch (NoSuchTemplateException ex2) {}
            }
        });
        final JournalTier intermediateTier2 = new JournalTier((byte)5, "Picking up Speed", (byte)4, (byte)6, 6, 69, new int[] { 586, 588, 578, 581, 580, 579, 584, 583, 587, 582, 585, 577 });
        intermediateTier2.setReward(new JournalReward("Proficient Title, Choice of Affinity & 3hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T5);
                p.getSaveFile().addToSleep(10800);
                try {
                    final Item token = ItemFactory.createItem(1438, 80.0f, p.getName());
                    p.getInventory().insertItem(token, true);
                }
                catch (FailedException ex) {}
                catch (NoSuchTemplateException ex2) {}
            }
        });
        final JournalTier intermediateTier3 = new JournalTier((byte)6, "The Winding Road", (byte)5, (byte)7, 6, 70, new int[] { 590, 591, 594, 589, 592, 596, 597, 600, 599, 595, 593, 598 });
        intermediateTier3.setReward(new JournalReward("Talented Title, Increased Max Sleep Bonus & 6hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_T6);
                p.setFlag(77, true);
                p.getSaveFile().addToSleep(21600);
            }
        });
        final JournalTier priestTier1 = new JournalTier((byte)10, "Dedication", (byte)(-1), (byte)11, 6, 78, new int[] { 604, 606, 609, 608, 614, 615, 605, 607, 612, 611, 610, 613 }) {
            @Override
            public boolean isVisible(final long playerId) {
                final Optional<PlayerInfo> playerInfo = PlayerInfoFactory.getPlayerInfoOptional(playerId);
                return playerInfo.isPresent() && playerInfo.get().isPriest;
            }
        };
        priestTier1.setReward(new JournalReward("Blessed Title, 2h Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_P1);
                p.getSaveFile().addToSleep(7200);
            }
        });
        final JournalTier priestTier2 = new JournalTier((byte)11, "Approbation", (byte)10, (byte)12, 6, 79, new int[] { 617, 619, 627, 616, 625, 623, 622, 621, 626, 618, 624, 620 });
        priestTier2.setReward(new JournalReward("Angelic Title, Increased Max Faith Gains Per Day & 3hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_P2);
                p.setFlag(81, true);
                p.getSaveFile().addToSleep(10800);
            }
        });
        final JournalTier priestTier3 = new JournalTier((byte)12, "Benediction", (byte)11, (byte)(-1), 5, 80, new int[] { 628, 630, 635, 634, 636, 632, 633, 631, 637, 629 });
        priestTier3.setReward(new JournalReward("Divine Title, +5 Power to Spell Casts & 5hr Sleep Bonus") {
            @Override
            public void runReward(final Player p) {
                p.addTitle(Titles.Title.Journal_P3);
                p.setFlag(82, true);
                p.getSaveFile().addToSleep(18000);
            }
        });
        PlayerJournal.allTiers.put((byte)0, tutorialTier);
        PlayerJournal.allTiers.put((byte)1, basicTier1);
        PlayerJournal.allTiers.put((byte)2, basicTier2);
        PlayerJournal.allTiers.put((byte)3, basicTier3);
        PlayerJournal.allTiers.put((byte)4, intermediateTier1);
        PlayerJournal.allTiers.put((byte)5, intermediateTier2);
        PlayerJournal.allTiers.put((byte)6, intermediateTier3);
        PlayerJournal.allTiers.put((byte)10, priestTier1);
        PlayerJournal.allTiers.put((byte)11, priestTier2);
        PlayerJournal.allTiers.put((byte)12, priestTier3);
    }
}
