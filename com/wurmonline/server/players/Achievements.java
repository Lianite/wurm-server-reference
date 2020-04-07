// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.WurmCalendar;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Servers;
import java.util.ArrayList;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.statistics.ChallengeSummary;
import com.wurmonline.server.statistics.ChallengePointEnum;
import com.wurmonline.server.Players;
import java.sql.Timestamp;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.items.Item;
import java.util.logging.Level;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.creatures.CreatureTemplateCreator;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class Achievements implements CounterTypes, MiscConstants, TimeConstants, CreatureTemplateIds
{
    private static final Logger logger;
    private static final Map<Long, Achievements> achievements;
    private final Map<Integer, Achievement> achievementsMap;
    private final Set<AchievementTemplate> personalGoalsSet;
    private static final Achievement[] emptyArray;
    private static final String loadAllAchievements = "SELECT * FROM ACHIEVEMENTS";
    private static final String deleteAllAchievementsForPlayer = "DELETE FROM ACHIEVEMENTS WHERE PLAYER=?";
    private final long wurmId;
    
    public Achievements(final long holderId) {
        this.achievementsMap = new ConcurrentHashMap<Integer, Achievement>();
        this.personalGoalsSet = new HashSet<AchievementTemplate>();
        this.wurmId = holderId;
    }
    
    public Achievements(final long holderId, final boolean createGoals) {
        this(holderId);
        if (createGoals) {
            this.generatePersonalGoals(holderId);
        }
    }
    
    private final long getWurmId() {
        return this.wurmId;
    }
    
    public static boolean hasAchievement(final long wurmId, final int achievementId) {
        final Achievements ach = getAchievementObject(wurmId);
        return ach != null && ach.getAchievement(achievementId) != null;
    }
    
    public static final Set<AchievementTemplate> getOldPersonalGoals(final long wurmId) {
        final Set<AchievementTemplate> initialSet = new HashSet<AchievementTemplate>();
        initialSet.add(Achievement.getTemplate(141));
        initialSet.add(Achievement.getTemplate(237));
        initialSet.add(Achievement.getTemplate(171));
        initialSet.add(Achievement.getTemplate(70));
        initialSet.add(Achievement.getTemplate(57));
        final Random rand = new Random(wurmId);
        while (initialSet.size() < 7) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalDiamondAchievement(rand);
            initialSet.add(originalAch);
        }
        while (initialSet.size() < 9) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalGoldAchievement(rand);
            initialSet.add(originalAch);
        }
        while (initialSet.size() < 20) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalSilverAchievement(rand);
            initialSet.add(originalAch);
        }
        return initialSet;
    }
    
    private final void generatePersonalGoals(final long playerId) {
        if (!canStillWinTheGame()) {
            return;
        }
        final Set<AchievementTemplate> initialSet = new HashSet<AchievementTemplate>();
        final Set<AchievementTemplate> completedAch = new HashSet<AchievementTemplate>();
        for (final Achievement t : getAchievements(playerId)) {
            completedAch.add(t.getTemplate());
        }
        initialSet.add(Achievement.getTemplate(141));
        initialSet.add(Achievement.getTemplate(237));
        initialSet.add(Achievement.getTemplate(171));
        initialSet.add(Achievement.getTemplate(70));
        initialSet.add(Achievement.getTemplate(57));
        final Random rand = new Random(playerId);
        while (initialSet.size() < 7) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalDiamondAchievement(rand);
            initialSet.add(originalAch);
        }
        while (initialSet.size() < 9) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalGoldAchievement(rand);
            initialSet.add(originalAch);
        }
        while (initialSet.size() < 20) {
            final AchievementTemplate originalAch = Achievement.getRandomPersonalSilverAchievement(rand);
            initialSet.add(originalAch);
        }
        this.personalGoalsSet.clear();
        for (final AchievementTemplate t2 : initialSet) {
            int count = 1;
            if (completedAch.contains(t2)) {
                this.personalGoalsSet.add(t2);
            }
            else if (t2.getNumber() >= 300 && t2.getType() == 5) {
                AchievementTemplate newAch;
                for (newAch = Achievement.getRandomPersonalDiamondAchievement(new Random(playerId + count++)); newAch.getNumber() >= 300 || initialSet.contains(newAch) || this.personalGoalsSet.contains(newAch); newAch = Achievement.getRandomPersonalDiamondAchievement(new Random(playerId + count++))) {}
                this.personalGoalsSet.add(newAch);
            }
            else if (AchievementGenerator.isRerollablePersonalGoal(t2.getNumber())) {
                if (t2.getType() == 4) {
                    AchievementTemplate newAch;
                    for (newAch = Achievement.getRandomPersonalGoldAchievement(new Random(playerId + count++)); AchievementGenerator.isRerollablePersonalGoal(newAch.getNumber()) || initialSet.contains(newAch) || this.personalGoalsSet.contains(newAch); newAch = Achievement.getRandomPersonalGoldAchievement(new Random(playerId + count++))) {}
                    this.personalGoalsSet.add(newAch);
                }
                else {
                    if (t2.getType() != 3) {
                        continue;
                    }
                    AchievementTemplate newAch;
                    for (newAch = Achievement.getRandomPersonalSilverAchievement(new Random(playerId + count++)); AchievementGenerator.isRerollablePersonalGoal(newAch.getNumber()) || initialSet.contains(newAch) || this.personalGoalsSet.contains(newAch); newAch = Achievement.getRandomPersonalSilverAchievement(new Random(playerId + count++))) {}
                    this.personalGoalsSet.add(newAch);
                }
            }
            else {
                this.personalGoalsSet.add(t2);
            }
        }
        AchievementTemplate toRemove = null;
        for (final AchievementTemplate t3 : this.personalGoalsSet) {
            if (t3.getNumber() == 298) {
                if (completedAch.contains(t3)) {
                    continue;
                }
                toRemove = t3;
            }
        }
        if (toRemove != null) {
            this.personalGoalsSet.remove(toRemove);
            this.personalGoalsSet.add(Achievement.getTemplate(486));
        }
        this.personalGoalsSet.add(Achievement.getTemplate(344));
    }
    
    private final void generatePersonalUndeadGoals() {
        this.personalGoalsSet.clear();
        this.personalGoalsSet.add(Achievement.getTemplate(338));
        this.personalGoalsSet.add(Achievement.getTemplate(340));
    }
    
    public Set<AchievementTemplate> getPersonalGoals() {
        return this.personalGoalsSet;
    }
    
    public final boolean isPersonalGoal(final AchievementTemplate template) {
        return canStillWinTheGame() && this.personalGoalsSet.contains(template);
    }
    
    public final boolean hasMetAllPersonalGoals() {
        if (!canStillWinTheGame()) {
            return false;
        }
        for (final AchievementTemplate template : this.personalGoalsSet) {
            final Achievement a = this.getAchievement(template.getNumber());
            if (a == null) {
                return false;
            }
        }
        return true;
    }
    
    public static void addAchievement(final Achievement achievement, final boolean createGoals) {
        Achievements personalAchieves = Achievements.achievements.get(achievement.getHolder());
        if (personalAchieves == null) {
            personalAchieves = new Achievements(achievement.getHolder(), createGoals);
            Achievements.achievements.put(achievement.getHolder(), personalAchieves);
        }
        personalAchieves.achievementsMap.put(achievement.getAchievement(), achievement);
    }
    
    public static Achievement[] getAchievements(final long creatureId) {
        final Achievements personalSet = Achievements.achievements.get(creatureId);
        if (personalSet == null || personalSet.achievementsMap.isEmpty()) {
            return Achievements.emptyArray;
        }
        return personalSet.achievementsMap.values().toArray(new Achievement[personalSet.achievementsMap.values().size()]);
    }
    
    public static Achievements getAchievementObject(final long creatureId) {
        Achievements personalAchieves = Achievements.achievements.get(creatureId);
        if (personalAchieves == null) {
            personalAchieves = new Achievements(creatureId, true);
            Achievements.achievements.put(creatureId, personalAchieves);
        }
        return personalAchieves;
    }
    
    public static Set<AchievementTemplate> getPersonalGoals(final long creatureId, final boolean isUndead) {
        Achievements personalAchieves = Achievements.achievements.get(creatureId);
        if (personalAchieves == null) {
            personalAchieves = new Achievements(creatureId, true);
            Achievements.achievements.put(creatureId, personalAchieves);
        }
        if (isUndead) {
            if (personalAchieves.getPersonalGoals().size() > 2) {
                personalAchieves.generatePersonalUndeadGoals();
            }
            return personalAchieves.getPersonalGoals();
        }
        return personalAchieves.getPersonalGoals();
    }
    
    private static final void awardKarma(final AchievementTemplate template, final Creature creature) {
        switch (template.getType()) {
            case 3: {
                creature.setKarma(creature.getKarma() + 100);
                creature.getCommunicator().sendSafeServerMessage("You have received 100 karma for '" + template.getRequirement() + "'.");
                break;
            }
            case 4: {
                creature.setKarma(creature.getKarma() + 500);
                creature.getCommunicator().sendSafeServerMessage("You have received 500 karma for '" + template.getRequirement() + "'.");
                break;
            }
            case 5: {
                creature.setKarma(creature.getKarma() + 1000);
                creature.getCommunicator().sendSafeServerMessage("You have received 1000 karma for '" + template.getRequirement() + "'.");
                break;
            }
        }
    }
    
    public Achievement getAchievement(final int achievement) {
        return this.achievementsMap.get(achievement);
    }
    
    private final void setWinnerEffects(final Creature p) {
        if (!canStillWinTheGame()) {
            return;
        }
        if (!p.hasFlag(6)) {
            p.setFlag(6, true);
            p.achievement(326);
            try {
                int itemTemplateId = 795 + Server.rand.nextInt(16);
                if (Server.rand.nextInt(100) == 0) {
                    itemTemplateId = 465;
                }
                final Item i = ItemFactory.createItem(itemTemplateId, 80 + Server.rand.nextInt(20), "");
                if (i.getTemplateId() == 465) {
                    i.setData1(CreatureTemplateCreator.getRandomDragonOrDrakeId());
                }
                p.getInventory().insertItem(i);
                p.addTitle(Titles.Title.Winner);
                HistoryManager.addHistory(p.getName(), "has Won The Game and receives the " + i.getName() + "!");
            }
            catch (Exception nsi) {
                Achievements.logger.log(Level.WARNING, p.getName() + " " + nsi.getMessage(), nsi);
            }
        }
    }
    
    private final void setWinnerEffectsOffline(final PlayerInfo pInf) {
        if (!pInf.isFlagSet(6)) {
            pInf.setFlag(6, true);
            triggerAchievement(pInf.wurmId, 326);
            try {
                int itemTemplateId = 795 + Server.rand.nextInt(16);
                if (Server.rand.nextInt(100) == 0) {
                    itemTemplateId = 465;
                }
                final Item i = ItemFactory.createItem(itemTemplateId, 80 + Server.rand.nextInt(20), "");
                if (i.getTemplateId() == 465) {
                    i.setData1(CreatureTemplateCreator.getRandomDragonOrDrakeId());
                }
                final long inventory = DbCreatureStatus.getInventoryIdFor(pInf.wurmId);
                i.setParentId(inventory, true);
                i.setOwnerId(pInf.wurmId);
                pInf.addTitle(Titles.Title.Winner);
                HistoryManager.addHistory(pInf.getName(), "has Won The Game and receives the " + i.getName() + "!");
            }
            catch (Exception nsi) {
                Achievements.logger.log(Level.WARNING, pInf.getName() + " " + nsi.getMessage(), nsi);
            }
        }
    }
    
    public static void triggerAchievement(final long creatureId, final int achievementId, final int counterModifier) {
        if (WurmId.getType(creatureId) != 0) {
            return;
        }
        Achievements personalAchieves = Achievements.achievements.get(creatureId);
        if (personalAchieves == null) {
            personalAchieves = new Achievements(creatureId, true);
            Achievements.achievements.put(creatureId, personalAchieves);
        }
        Achievement achieved = personalAchieves.getAchievement(achievementId);
        if (achieved == null) {
            achieved = new Achievement(achievementId, new Timestamp(System.currentTimeMillis()), creatureId, counterModifier, -1);
            PlayerJournal.achievementTriggered(creatureId, achievementId);
            achieved.create(false);
            if (!achieved.isInVisible()) {
                try {
                    final Player p = Players.getInstance().getPlayer(creatureId);
                    achieved.sendNewAchievement(p);
                    if (achievementId == 369) {
                        p.addTitle(Titles.Title.Knigt);
                    }
                    if (achievementId == 367) {
                        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(creatureId);
                        if (pinf != null) {
                            ChallengeSummary.addToScore(pinf, ChallengePointEnum.ChallengePoint.TREASURE_CHESTS.getEnumtype(), 1.0f);
                            ChallengeSummary.addToScore(pinf, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 5.0f);
                        }
                    }
                    if (personalAchieves.isPersonalGoal(achieved.getTemplate())) {
                        achieved.sendUpdatePersonalGoal(p);
                        awardKarma(achieved.getTemplate(), p);
                        if (canStillWinTheGame() && personalAchieves.hasMetAllPersonalGoals()) {
                            personalAchieves.setWinnerEffects(p);
                        }
                    }
                }
                catch (NoSuchPlayerException nsc) {
                    final PlayerInfo pInf = PlayerInfoFactory.getPlayerInfoWithWurmId(creatureId);
                    if (pInf != null) {
                        if (achievementId == 369) {
                            pInf.addTitle(Titles.Title.Knigt);
                        }
                        if (achievementId == 367) {
                            ChallengeSummary.addToScore(pInf, ChallengePointEnum.ChallengePoint.TREASURE_CHESTS.getEnumtype(), 1.0f);
                            ChallengeSummary.addToScore(pInf, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 5.0f);
                        }
                        if (personalAchieves.isPersonalGoal(achieved.getTemplate())) {
                            switch (achieved.getTemplate().getType()) {
                                case 3: {
                                    pInf.setKarma(pInf.getKarma() + 100);
                                    break;
                                }
                                case 4: {
                                    pInf.setKarma(pInf.getKarma() + 500);
                                    break;
                                }
                                case 5: {
                                    pInf.setKarma(pInf.getKarma() + 1000);
                                    break;
                                }
                            }
                            if (canStillWinTheGame() && personalAchieves.hasMetAllPersonalGoals()) {
                                personalAchieves.setWinnerEffectsOffline(pInf);
                            }
                        }
                    }
                }
            }
            triggerAchievements(creatureId, achieved, achievementId, personalAchieves, achieved.getTriggeredAchievements());
        }
        else if (!achieved.isOneTimer()) {
            final int[] triggered = achieved.setCounter(achieved.getCounter() + counterModifier);
            if (!achieved.isInVisible()) {
                try {
                    final Player p2 = Players.getInstance().getPlayer(creatureId);
                    achieved.sendUpdateAchievement(p2);
                    if (achievementId == 369) {
                        p2.addTitle(Titles.Title.Knigt);
                    }
                    if (achievementId == 367) {
                        final PlayerInfo pinf2 = PlayerInfoFactory.getPlayerInfoWithWurmId(creatureId);
                        if (pinf2 != null) {
                            ChallengeSummary.addToScore(pinf2, ChallengePointEnum.ChallengePoint.TREASURE_CHESTS.getEnumtype(), 1.0f);
                            ChallengeSummary.addToScore(pinf2, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 5.0f);
                        }
                    }
                }
                catch (NoSuchPlayerException nsc2) {
                    final PlayerInfo pInf2 = PlayerInfoFactory.getPlayerInfoWithWurmId(creatureId);
                    if (pInf2 != null) {
                        if (achievementId == 369) {
                            pInf2.addTitle(Titles.Title.Knigt);
                        }
                        if (achievementId == 367) {
                            ChallengeSummary.addToScore(pInf2, ChallengePointEnum.ChallengePoint.TREASURE_CHESTS.getEnumtype(), 1.0f);
                            ChallengeSummary.addToScore(pInf2, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 5.0f);
                        }
                    }
                }
            }
            triggerAchievements(creatureId, achieved, achievementId, personalAchieves, triggered);
        }
    }
    
    public static void triggerAchievement(final long creatureId, final int achievementId) {
        if (WurmId.getType(creatureId) != 0) {
            return;
        }
        triggerAchievement(creatureId, achievementId, 1);
    }
    
    public static void sendAchievementList(final Creature creature) {
        final Achievement[] lAchievements = getAchievements(creature.getWurmId());
        creature.getCommunicator().sendAchievementList(lAchievements);
        sendPersonalGoalsList(creature);
        if (creature.isPlayer()) {
            PlayerTutorial.sendTutorialList((Player)creature);
            PlayerJournal.sendPersonalJournal((Player)creature);
        }
    }
    
    private static void awardPremiumAchievements(final Creature creature, final int totalMonths) {
        final ArrayList<Integer> achievementsList = new ArrayList<Integer>();
        for (final Achievement a : getAchievements(creature.getWurmId())) {
            achievementsList.add(a.getAchievement());
        }
        if (totalMonths >= 1 && !achievementsList.contains(343)) {
            creature.achievement(343);
        }
        if (totalMonths >= 3 && !achievementsList.contains(344)) {
            creature.achievement(344);
        }
        if (totalMonths >= 6 && !achievementsList.contains(345)) {
            creature.achievement(345);
        }
        if (totalMonths >= 9 && !achievementsList.contains(346)) {
            creature.achievement(346);
        }
        if (totalMonths >= 13 && !achievementsList.contains(347)) {
            creature.achievement(347);
        }
        if (totalMonths >= 16 && !achievementsList.contains(348)) {
            creature.achievement(348);
        }
        if (totalMonths >= 20 && !achievementsList.contains(349)) {
            creature.achievement(349);
        }
        if (totalMonths >= 26 && !achievementsList.contains(350)) {
            creature.achievement(350);
        }
        if (totalMonths >= 36 && !achievementsList.contains(351)) {
            creature.achievement(351);
        }
        if (totalMonths >= 48 && !achievementsList.contains(352)) {
            creature.achievement(352);
        }
        if (totalMonths >= 60 && !achievementsList.contains(353)) {
            creature.achievement(353);
        }
        if (totalMonths >= 80 && !achievementsList.contains(354)) {
            creature.achievement(354);
        }
        if (totalMonths >= 120 && !achievementsList.contains(355)) {
            creature.achievement(355);
        }
        creature.setFlag(61, true);
    }
    
    public static void sendPersonalGoalsList(final Creature creature) {
        if (!canStillWinTheGame()) {
            return;
        }
        final Achievements pachievements = getAchievementObject(creature.getWurmId());
        pachievements.generatePersonalGoals(creature.getWurmId());
        final Set<AchievementTemplate> pset = getPersonalGoals(creature.getWurmId(), creature.isUndead());
        final Map<AchievementTemplate, Boolean> goals = new ConcurrentHashMap<AchievementTemplate, Boolean>();
        boolean awardTut = false;
        for (final AchievementTemplate template : pset) {
            final Achievement a = pachievements.getAchievement(template.getNumber());
            goals.put(template, a != null);
            if (a != null) {
                if (creature.hasFlag(5)) {
                    continue;
                }
                awardKarma(template, creature);
            }
            else {
                if (template.getNumber() != 141 || Servers.localServer.LOGINSERVER) {
                    continue;
                }
                awardTut = true;
            }
        }
        if (awardTut) {
            creature.achievement(141);
        }
        if (creature.isPlayer() && !creature.hasFlag(61)) {
            final PlayerInfo player = PlayerInfoFactory.getPlayerInfoWithWurmId(creature.getWurmId());
            if (player != null && player.awards != null) {
                awardPremiumAchievements(creature, player.awards.getMonthsPaidSinceReset());
            }
        }
        if (!creature.hasFlag(6) && pachievements.hasMetAllPersonalGoals() && canStillWinTheGame()) {
            pachievements.setWinnerEffects(creature);
        }
        if (!creature.hasFlag(5)) {
            creature.setFlag(5, true);
        }
        creature.getCommunicator().sendPersonalGoalsList(goals);
        if (creature.getPlayingTime() > 7200000L && creature.getPlayingTime() < 21600000L) {
            creature.getCommunicator().sendShowPersonalGoalWindow(true);
        }
    }
    
    private static void triggerAchievements(final long creatureId, final Achievement achieved, final int achievementId, final Achievements personalAchieves, final int[] triggered) {
        for (final int number : triggered) {
            if (number != achievementId) {
                final Achievement old = personalAchieves.getAchievement(number);
                if (old != null) {
                    if (!old.isOneTimer()) {
                        final int required = old.getTriggerOnCounter() * (old.getCounter() + 1);
                        if (achieved.getCounter() >= required) {
                            final int numTimes = achieved.getCounter() / old.getTriggerOnCounter() - old.getCounter();
                            if (numTimes > 0) {
                                triggerAchievement(creatureId, old.getAchievement(), numTimes);
                            }
                        }
                    }
                }
                else {
                    final AchievementTemplate template = Achievement.getTemplate(number);
                    if (template.getTriggerOnCounter() == 1 && template.getRequiredAchievements().length <= 1) {
                        Achievements.logger.log(Level.WARNING, "Achievement " + number + " has trigger on 1. Usually not good unless it's a meta achievement since it means the triggering achievement immediately gives another achievement.");
                    }
                    if ((template.getTriggerOnCounter() > 0 && achieved.getCounter() >= template.getTriggerOnCounter()) || (template.getTriggerOnCounter() < 0 && achieved.getCounter() <= template.getTriggerOnCounter())) {
                        boolean trigger = true;
                        final int[] requiredAchievements;
                        final int[] required2 = requiredAchievements = template.getRequiredAchievements();
                        for (final int req : requiredAchievements) {
                            if (req != achievementId) {
                                final Achievement existingReq = personalAchieves.getAchievement(req);
                                if (existingReq == null || existingReq.getCounter() < template.getTriggerOnCounter()) {
                                    trigger = false;
                                }
                            }
                        }
                        final int numTimes2 = achieved.getCounter() / template.getTriggerOnCounter();
                        if (trigger && numTimes2 > 0) {
                            triggerAchievement(creatureId, template.getNumber(), numTimes2);
                        }
                    }
                }
                PlayerJournal.subAchievementCounterTick(creatureId, number);
            }
            else {
                Achievements.logger.log(Level.WARNING, "Achievement " + achievementId + " has itself as trigger: " + number);
            }
        }
    }
    
    public static void loadAllAchievements() throws IOException {
        final long start = System.nanoTime();
        int loadedAchievements = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ACHIEVEMENTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp st = new Timestamp(System.currentTimeMillis());
                try {
                    st = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("ADATE")).getTime());
                }
                catch (Exception ex) {
                    Achievements.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                addAchievement(new Achievement(rs.getInt("ACHIEVEMENT"), st, rs.getLong("PLAYER"), rs.getInt("COUNTER"), rs.getInt("ID")), false);
                ++loadedAchievements;
            }
        }
        catch (SQLException sqex) {
            Achievements.logger.log(Level.WARNING, "Failed to load achievements due to " + sqex.getMessage(), sqex);
            throw new IOException("Failed to load achievements", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Achievements.logger.info("Loaded " + loadedAchievements + " achievements from the database took " + (end - start) / 1000000.0f + " ms");
        }
        if (canStillWinTheGame()) {
            generateAllPersonalGoals();
        }
    }
    
    private static void generateAllPersonalGoals() {
        final long start = System.nanoTime();
        int count = 0;
        for (final Achievements a : Achievements.achievements.values()) {
            a.generatePersonalGoals(a.getWurmId());
            ++count;
        }
        final long end = System.nanoTime();
        Achievements.logger.info("Generated " + count + " personal goals, took " + (end - start) / 1000000.0f + " ms");
    }
    
    public static void deleteAllAchievements(final long playerId) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ACHIEVEMENTS WHERE PLAYER=?");
            ps.setLong(1, playerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Achievements.logger.log(Level.WARNING, "Failed to delete achievements for " + playerId + ' ' + sqex.getMessage(), sqex);
            throw new IOException("Failed to delete achievements for " + playerId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean canStillWinTheGame() {
        return WurmCalendar.nowIsBefore(0, 1, 1, 1, 2019);
    }
    
    static {
        logger = Logger.getLogger(Achievements.class.getName());
        achievements = new ConcurrentHashMap<Long, Achievements>();
        emptyArray = new Achievement[0];
    }
}
