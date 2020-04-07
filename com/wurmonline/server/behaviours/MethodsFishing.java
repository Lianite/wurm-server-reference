// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.awt.Color;
import com.wurmonline.shared.constants.FishingEnums;
import java.util.Iterator;
import com.wurmonline.server.items.ContainerRestriction;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.Items;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Point4f;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import javax.annotation.Nullable;
import java.util.ArrayList;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.WaterType;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Point;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.creatures.ai.scripts.FishAI;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class MethodsFishing implements MiscConstants
{
    private static final Logger logger;
    
    static boolean fish(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final Action act) {
        if (!Terraforming.isTileUnderWater(tile, tilex, tiley, performer.isOnSurface())) {
            performer.getCommunicator().sendNormalServerMessage("The water is too shallow to fish.");
            return true;
        }
        final Item[] fishingItems = source.getFishingItems();
        final Item fishingReel = fishingItems[0];
        final Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        final Item fishingHook = fishingItems[3];
        final Item fishingBait = fishingItems[4];
        final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
        int timeleft = 1800;
        int defaultTimer = 1800;
        byte startCommand = 0;
        if (source.getTemplateId() == 1343) {
            startCommand = 40;
            if (performer.getVehicle() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("You cannot use a fishing net whilst on a vehicle.");
                return true;
            }
        }
        else if (source.getTemplateId() == 705 || source.getTemplateId() == 707) {
            startCommand = 20;
            if (performer.getVehicle() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("You cannot use a spear to fish whilst on a vehicle.");
                if (counter != 1.0f) {
                    sendSpearStop(performer, act);
                }
                return true;
            }
        }
        else {
            startCommand = 0;
            boolean failed = false;
            if (source.getTemplateId() == 1344) {
                if (act.getCounterAsFloat() < act.getFailSecond()) {
                    if (fishingLine == null || fishingHook == null) {
                        performer.getCommunicator().sendNormalServerMessage("Fishing pole needs a line with a fishing hook to be able to catch fish.");
                        failed = true;
                    }
                    if (fishingFloat == null) {
                        performer.getCommunicator().sendNormalServerMessage("Fishing pole needs a float for you to be able to see when a fish can be hooked.");
                        failed = true;
                    }
                }
                if (failed) {
                    act.setFailSecond(act.getCounterAsFloat());
                }
            }
            else {
                if (act.getCounterAsFloat() < act.getFailSecond()) {
                    if (fishingReel == null || fishingLine == null || fishingHook == null) {
                        performer.getCommunicator().sendNormalServerMessage("Fishing rod needs a reel, line and a fishing hook to be able to catch fish.");
                        failed = true;
                    }
                    if (fishingFloat == null) {
                        performer.getCommunicator().sendNormalServerMessage("Fishing rod needs a float for you to be able to see when a fish can be hooked.");
                        failed = true;
                    }
                }
                if (failed) {
                    act.setFailSecond(act.getCounterAsFloat());
                }
            }
            if (failed) {
                if (counter == 1.0f || act.getCreature() == null) {
                    sendFishStop(performer, act);
                    return true;
                }
                final byte currentPhase = (byte)act.getTickCount();
                if (currentPhase != 15) {
                    processFishMovedOn(performer, act, 2.2f);
                    act.setTickCount(15);
                }
            }
        }
        if (counter == 1.0f) {
            if (fishingTestsFailed(performer, act.getPosX(), act.getPosY())) {
                return true;
            }
            switch (startCommand) {
                case 40: {
                    if (!source.isEmpty(false)) {
                        performer.getCommunicator().sendNormalServerMessage("The net is too unwieldly for you to cast, please empty it first!");
                        return true;
                    }
                    performer.getCommunicator().sendNormalServerMessage("You throw out the net and start slowly pulling it in, hoping to catch some small fish.");
                    Server.getInstance().broadCastAction(performer.getName() + " throws out a net and starts fishing.", performer, 5);
                    timeleft = 50 + Server.rand.nextInt(250);
                    defaultTimer = 600;
                    act.setTickCount(40);
                    break;
                }
                case 20: {
                    performer.getCommunicator().sendNormalServerMessage("You stand still to wait for a passing fish, are they like buses?");
                    Server.getInstance().broadCastAction(performer.getName() + " stands still looking at the water.", performer, 5);
                    performer.getCommunicator().sendSpearStart();
                    timeleft = 100 + Server.rand.nextInt(250) - source.getRarity() * 10;
                    act.setTickCount(21);
                    break;
                }
                case 0: {
                    final String ss = (performer.getVehicle() == -10L) ? "stand" : "sit";
                    performer.getCommunicator().sendNormalServerMessage("You " + ss + " still and get ready to cast.");
                    Server.getInstance().broadCastAction(performer.getName() + " gets ready to cast.", performer, 5);
                    final float[] radi = getMinMaxRadius(source, fishingLine);
                    final float minRadius = radi[0];
                    final float maxRadius = radi[1];
                    final byte rodType = getRodType(source, fishingReel, fishingLine);
                    final byte rodMaterial = source.getMaterial();
                    final FishEnums.ReelType reelType = FishEnums.ReelType.fromItem(fishingReel);
                    final byte reelMaterial = (byte)((fishingReel == null) ? 0 : fishingReel.getMaterial());
                    final FishEnums.FloatType floatType = FishEnums.FloatType.fromItem(fishingFloat);
                    final FishEnums.HookType hookType = FishEnums.HookType.fromItem(fishingHook);
                    final byte hookMaterial = fishingHook.getMaterial();
                    final FishEnums.BaitType baitType = FishEnums.BaitType.fromItem(fishingBait);
                    performer.getCommunicator().sendFishStart(minRadius, maxRadius, rodType, rodMaterial, reelType.getTypeId(), reelMaterial, floatType.getTypeId(), baitType.getTypeId());
                    remember(source, reelType, reelMaterial, floatType, hookType, hookMaterial, baitType);
                    timeleft = (defaultTimer = 300);
                    act.setTickCount(0);
                    break;
                }
            }
            act.setTimeLeft(defaultTimer);
            act.setData(timeleft);
            performer.sendActionControl(Actions.actionEntrys[160].getVerbString(), true, defaultTimer);
            return false;
        }
        else {
            final byte currentPhase2 = (byte)act.getTickCount();
            if (canTimeOut(act) && act.getSecond() * 10 > act.getTimeLeft()) {
                Label_1056: {
                    switch (startCommand) {
                        case 40: {
                            performer.getCommunicator().sendNormalServerMessage("You finish pulling in the net.");
                            break;
                        }
                        case 20: {
                            performer.getCommunicator().sendNormalServerMessage("You speared nothing!");
                            sendSpearStop(performer, act);
                            break;
                        }
                        case 0: {
                            switch (currentPhase2) {
                                case 0: {
                                    performer.getCommunicator().sendNormalServerMessage("You never cast your line, so caught nothing.");
                                    return sendFishStop(performer, act);
                                }
                                default: {
                                    performer.getCommunicator().sendNormalServerMessage("You did not catch anything!");
                                    sendFishStop(performer, act);
                                    break Label_1056;
                                }
                            }
                            break;
                        }
                    }
                }
                return true;
            }
            if (currentPhase2 == 17) {
                final Creature fish = act.getCreature();
                if (performer.isWithinDistanceTo(fish, 1.0f)) {
                    act.setTickCount(12);
                }
                else if (!performer.isWithinDistanceTo(fish, 10.0f)) {
                    act.setTickCount(13);
                }
            }
            timeleft = (int)act.getData();
            if (act.getSecond() * 10 > timeleft || isInstant(currentPhase2)) {
                switch (startCommand) {
                    case 40: {
                        if (processNetPhase(performer, currentPhase2, source, act, tilex, tiley, fishing)) {
                            return true;
                        }
                        break;
                    }
                    case 20: {
                        if (processSpearPhase(performer, currentPhase2, source, act, fishing)) {
                            return true;
                        }
                        break;
                    }
                    case 0: {
                        if (processFishPhase(performer, currentPhase2, source, act, fishing)) {
                            return true;
                        }
                        break;
                    }
                }
            }
            if (act.justTickedSecond()) {
                if (act.getSecond() % 2 == 0 && fishingTestsFailed(performer, act.getPosX(), act.getPosY())) {
                    switch (startCommand) {
                        case 40: {
                            return true;
                        }
                        case 20: {
                            sendSpearStop(performer, act);
                            return true;
                        }
                        case 0: {
                            sendFishStop(performer, act);
                            return true;
                        }
                        default: {
                            return true;
                        }
                    }
                }
                else {
                    final Creature fish = act.getCreature();
                    if (fish != null && startCommand == 20) {
                        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
                        if (fish.getPosX() == faid.getTargetPosX() && fish.getPosY() == faid.getTargetPosY()) {
                            testMessage(performer, "Fish out of range? Swam away!", "");
                            act.setTickCount(28);
                        }
                    }
                }
            }
            return false;
        }
    }
    
    private static boolean isInstant(final byte currentPhase) {
        switch (currentPhase) {
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 13:
            case 22:
            case 23:
            case 24:
            case 27:
            case 28: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean canTimeOut(final Action act) {
        final Creature fish = act.getCreature();
        return fish == null;
    }
    
    private static void remember(final Item rod, final FishEnums.ReelType reelType, final byte reelMaterial, final FishEnums.FloatType floatType, final FishEnums.HookType hookType, final byte hookMaterial, final FishEnums.BaitType baitType) {
        final int types = (reelType.getTypeId() << 12) + (floatType.getTypeId() << 8) + (hookType.getTypeId() << 4) + baitType.getTypeId();
        final int materials = (reelMaterial << 8) + hookMaterial;
        rod.setData(types, materials);
    }
    
    public static void playerOutOfRange(final Creature performer, final Action act) {
        final Item source = act.getSubject();
        final int stid = (source == null) ? 0 : source.getTemplateId();
        switch (stid) {
            case 705:
            case 707: {
                sendSpearStop(performer, act);
                break;
            }
            case 1344:
            case 1346: {
                sendFishStop(performer, act);
                break;
            }
        }
    }
    
    private static boolean processNetPhase(final Creature performer, final byte currentPhase, final Item net, final Action act, final int tilex, final int tiley, final Skill fishing) {
        final float posx = (tilex << 2) + 2;
        final float posy = (tiley << 2) + 2;
        final FishRow fr = caughtFish(performer, posx, posy, net);
        if (fr != null) {
            final int weight = makeDeadFish(performer, act, fishing, fr.getFishTypeId(), net, net);
            final FishEnums.FishData fd = FishEnums.FishData.fromInt(fr.getFishTypeId());
            final float damMod = fd.getDamageMod() * Math.max(0.1f, weight / 3000.0f);
            final float additionalDamage = additionalDamage(net, damMod * 10.0f, false);
            if (additionalDamage > 0.0f) {
                final float newDam = net.getDamage() + additionalDamage;
                if (newDam >= 100.0f && !net.isEmpty(false)) {
                    performer.getCommunicator().sendNormalServerMessage("As the " + net.getName() + " disintegrates, the fish inside all swim away");
                    destroyContents(net);
                }
                net.setDamage(newDam);
            }
        }
        final int moreTime = 50 + Server.rand.nextInt(250);
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        return false;
    }
    
    private static boolean processSpearPhase(final Creature performer, final byte currentPhase, final Item spear, final Action act, final Skill fishing) {
        switch (currentPhase) {
            case 27: {
                return processSpearCancel(performer, act);
            }
            case 21: {
                return processSpearMove(performer, act, spear, fishing);
            }
            case 28: {
                return processSpearSwamAway(performer, act, spear, 50);
            }
            case 24: {
                testMessage(performer, "You launch the spear at nothing!", "");
                act.setTickCount(21);
                return false;
            }
            case 23: {
                return processSpearMissed(performer, act, spear);
            }
            case 22: {
                return processSpearHit(performer, act, fishing, spear);
            }
            case 26: {
                final Creature fish = act.getCreature();
                testMessage(performer, "You launch the spear at " + fish.getName() + ".", " @fx:" + (int)fish.getPosX() + " fy:" + (int)fish.getPosY());
                performer.getCommunicator().sendSpearStrike(fish.getPosX(), fish.getPosY());
                return processSpearStrike(performer, act, fishing, spear, fish.getPosX(), fish.getPosY());
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean processFishPhase(final Creature performer, final byte currentPhase, final Item rod, final Action act, final Skill fishing) {
        switch (currentPhase) {
            case 10: {
                return processFishCancel(performer, act);
            }
            case 1: {
                return processFishBite(performer, act, rod, fishing);
            }
            case 18: {
                return processFishPause(performer, act);
            }
            case 16: {
                if (processFishMove(performer, act, fishing, rod)) {
                    sendFishStop(performer, act);
                    return true;
                }
                return false;
            }
            case 2: {
                return processFishMovedOn(performer, act);
            }
            case 19: {
                return processFishMovingOn(performer, act);
            }
            case 14: {
                return processFishSwamAway(performer, act, rod, 50);
            }
            case 3: {
                return processFishHooked(performer, act, fishing, rod);
            }
            case 11: {
                return processFishStrike(performer, act, fishing, rod);
            }
            case 17: {
                if (processFishPull(performer, act, fishing, rod, false)) {
                    final Creature fish = act.getCreature();
                    final String fname = (fish == null) ? "fish" : fish.getName();
                    performer.getCommunicator().sendNormalServerMessage("The " + fname + " swims off.");
                    sendFishStop(performer, act);
                    return true;
                }
                return false;
            }
            case 12: {
                return processFishCaught(performer, act, fishing, rod);
            }
            case 4: {
                return sendFishStop(performer, act);
            }
            case 5: {
                performer.getCommunicator().sendNormalServerMessage("You hooked nothing while reeling in your line.");
                return sendFishStop(performer, act);
            }
            case 13: {
                final Creature fish = act.getCreature();
                final String fname = (fish == null) ? "fish" : fish.getName();
                performer.getCommunicator().sendNormalServerMessage("The " + fname + " managed to jump the fishing hook!");
                return sendFishStop(performer, act);
            }
            case 6:
            case 7: {
                return processFishLineSnapped(performer, act, rod);
            }
            case 15: {
                return sendFishStop(performer, act);
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean fishingTestsFailed(final Creature performer, final float posx, final float posy) {
        final int tilex = (int)posx >> 2;
        final int tiley = (int)posy >> 2;
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("A huge shadow moves beneath the waves, and you reel in the line in panic.");
            return true;
        }
        if (performer.getInventory().getNumItemsNotCoins() >= 100) {
            performer.getCommunicator().sendNormalServerMessage("You wouldn't be able to carry the fish. Drop something first.");
            return true;
        }
        if (!performer.canCarry(1000)) {
            performer.getCommunicator().sendNormalServerMessage("You are too heavily loaded. Drop something first.");
            return true;
        }
        final int depth = FishEnums.getWaterDepth(performer.getPosX(), performer.getPosY(), performer.isOnSurface());
        if (performer.getBridgeId() == -10L && depth > 10 && performer.getVehicle() == -10L) {
            performer.getCommunicator().sendNormalServerMessage("You can't swim and fish at the same time.");
            return true;
        }
        return false;
    }
    
    static boolean destroyFishCreature(final Action act) {
        final Creature fish = act.getCreature();
        act.setCreature(null);
        if (fish != null) {
            fish.destroy();
        }
        return true;
    }
    
    private static boolean isFishSpotValid(final Point point) {
        if (WaterType.getWaterType(Zones.safeTileX(point.getX()), Zones.safeTileY(point.getY()), true) == 4) {
            float ht = 0.0f;
            try {
                ht = Zones.calculateHeight(point.getX(), point.getY(), true) * 10.0f;
            }
            catch (NoSuchZoneException ex) {}
            if (ht < -100.0f) {
                return true;
            }
        }
        return false;
    }
    
    public static Point[] getSpecialSpots(final int tilex, final int tiley, final int season) {
        final ArrayList<Point> specialSpots = new ArrayList<Point>();
        final int zoneX = tilex / 128;
        final int zoneY = tiley / 128;
        for (final FishEnums.FishData fd : FishEnums.FishData.values()) {
            if (fd.isSpecialFish()) {
                final Point tp = fd.getSpecialSpot(zoneX, zoneY, season);
                if ((tilex == 0 && tiley == 0) || isFishSpotValid(tp)) {
                    specialSpots.add(tp);
                }
            }
        }
        return specialSpots.toArray(new Point[specialSpots.size()]);
    }
    
    public static FishRow[] getFishTable(final Creature performer, final float posX, final float posY, final Item rod) {
        final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
        final float knowledge = (float)fishing.getKnowledge();
        final Item[] fishingItems = rod.getFishingItems();
        final Item fishingReel = fishingItems[0];
        final Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        final Item fishingHook = fishingItems[3];
        final Item fishingBait = fishingItems[4];
        float totalChances = 0.0f;
        final FishRow[] fishTable = new FishRow[FishEnums.FishData.getLength()];
        for (final FishEnums.FishData fd : FishEnums.FishData.values()) {
            fishTable[fd.getTypeId()] = new FishRow(fd.getTypeId(), fd.getName());
            if (fd.getTypeId() != FishEnums.FishData.NONE.getTypeId() && fd.getTypeId() != FishEnums.FishData.CLAM.getTypeId()) {
                final float fishChance = fd.getChance(knowledge, rod, fishingReel, fishingLine, fishingFloat, fishingHook, fishingBait, posX, posY, performer.isOnSurface());
                fishTable[fd.getTypeId()].setChance(fishChance);
                totalChances += fishChance;
            }
        }
        if (totalChances > 99.0f) {
            final float percentageFactor = totalChances / 99.0f;
            totalChances = 0.0f;
            for (final FishEnums.FishData fd2 : FishEnums.FishData.values()) {
                final float chance = fishTable[fd2.getTypeId()].getChance();
                if (chance > 0.0f) {
                    fishTable[fd2.getTypeId()].setChance(chance / percentageFactor);
                }
                totalChances += fishTable[fd2.getTypeId()].getChance();
            }
        }
        if (totalChances < 100.0f) {
            fishTable[FishEnums.FishData.CLAM.getTypeId()].setChance(Math.min(2.0f, 100.0f - totalChances));
        }
        return fishTable;
    }
    
    public static boolean showFishTable(final Creature performer, final Item source, final int tileX, final int tileY, final float counter, final Action act) {
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            time = 250;
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start looking around for what fish might be in this area.");
            performer.sendActionControl(act.getActionString(), true, time);
        }
        else if (act.justTickedSecond() && act.getSecond() == 5) {
            if (source.getTemplateId() == 1344) {
                final Item[] fishingItems = source.getFishingItems();
                if (fishingItems[1] == null) {
                    performer.getCommunicator().sendNormalServerMessage("Your pole is missing a line, float and fishing hook!");
                    return true;
                }
                if (fishingItems[2] == null) {
                    if (fishingItems[3] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your pole is missing a float and fishing hook!");
                        return true;
                    }
                    performer.getCommunicator().sendNormalServerMessage("Your pole is missing a float!");
                    return true;
                }
                else {
                    if (fishingItems[3] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your pole is missing a fishing hook!");
                        return true;
                    }
                    if (fishingItems[4] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your pole looks all set, but you think catching something could be easier using bait.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Your pole looks all set.");
                    }
                }
            }
            else if (source.getTemplateId() == 1346) {
                final Item[] fishingItems = source.getFishingItems();
                if (fishingItems[0] == null) {
                    performer.getCommunicator().sendNormalServerMessage("Your rod is missing a reel, line, float and fishing hook!");
                    return true;
                }
                if (fishingItems[1] == null) {
                    performer.getCommunicator().sendNormalServerMessage("Your rod is missing a line, float and fishing hook!");
                    return true;
                }
                if (fishingItems[2] == null) {
                    if (fishingItems[3] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your rod is missing a float and fishing hook!");
                        return true;
                    }
                    performer.getCommunicator().sendNormalServerMessage("Your rod is missing a float!");
                    return true;
                }
                else {
                    if (fishingItems[3] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your rod is missing a fishing hook!");
                        return true;
                    }
                    if (fishingItems[4] == null) {
                        performer.getCommunicator().sendNormalServerMessage("Your rod looks all set, but you think catching something could be easier using bait.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Your rod looks all set.");
                    }
                }
            }
        }
        final double fishingSkill = performer.getSkills().getSkillOrLearn(10033).getKnowledge(0.0);
        final float posx = (tileX << 2) + 2;
        final float posy = (tileY << 2) + 2;
        final String waterType = WaterType.getWaterTypeString(tileX, tileY, performer.isOnSurface()).toLowerCase();
        final int waterDepth = FishEnums.getWaterDepth(posx, posy, performer.isOnSurface());
        if (act.justTickedSecond() && act.getSecond() == 10) {
            if (fishingSkill < 10.0) {
                performer.getCommunicator().sendNormalServerMessage("You're not too sure what type of water you're standing in, perhaps with a bit more fishing knowledge you'll understand better.");
            }
            else if (fishingSkill < 40.0) {
                performer.getCommunicator().sendNormalServerMessage("You believe this area of water to be " + waterType + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You believe this area of water to be " + waterType + " around a depth of " + waterDepth / 10 + "m.");
            }
        }
        else if (act.justTickedSecond() && act.getSecond() == 15) {
            final FishRow[] fishChances = getFishTable(performer, posx, posy, source);
            FishRow topChance = null;
            for (final FishRow fishChance : fishChances) {
                if (!FishEnums.FishData.fromName(fishChance.getName()).isSpecialFish()) {
                    final float chance = fishChance.getChance();
                    if (chance > 0.0f && (topChance == null || topChance.getChance() < chance)) {
                        topChance = fishChance;
                    }
                }
            }
            if (topChance == null) {
                performer.getCommunicator().sendNormalServerMessage("You can't find anything that you'll catch in this area with the " + source.getName() + ".");
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("The most common fish around here that you think you might catch with the " + source.getName() + " is a " + topChance.getName() + ".");
        }
        else if (act.justTickedSecond() && act.getSecond() == 25) {
            final FishRow[] fishChances = getFishTable(performer, posx, posy, source);
            FishRow topChance = null;
            final ArrayList<FishRow> otherChances = new ArrayList<FishRow>();
            for (final FishRow fishChance2 : fishChances) {
                if (!FishEnums.FishData.fromName(fishChance2.getName()).isSpecialFish()) {
                    final float chance2 = fishChance2.getChance();
                    if (chance2 > 0.0f) {
                        if (topChance == null || topChance.getChance() < chance2) {
                            topChance = fishChance2;
                        }
                        if (chance2 > (100.0 - fishingSkill) / 5.0 + 5.0) {
                            otherChances.add(fishChance2);
                        }
                    }
                }
            }
            otherChances.remove(topChance);
            if (otherChances.isEmpty() || fishingSkill < 20.0) {
                performer.getCommunicator().sendNormalServerMessage("You're not sure of what other fish you might find here.");
            }
            else {
                String allOthers = "";
                for (int i = 0; i < otherChances.size(); ++i) {
                    if (i > 0 && i == otherChances.size() - 1) {
                        allOthers += " and ";
                    }
                    else if (i > 0) {
                        allOthers += ", ";
                    }
                    allOthers += otherChances.get(i).getName();
                }
                performer.getCommunicator().sendNormalServerMessage("You also think the " + source.getName() + " will be useful to catch " + allOthers + " in this area.");
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    private static FishRow caughtFish(final Creature performer, final float posX, final float posY, final Item rod) {
        final FishRow[] fishChances = getFishTable(performer, posX, posY, rod);
        final float rno = Server.rand.nextFloat() * 100.0f;
        float runningTotal = 0.0f;
        for (final FishRow fishChance : fishChances) {
            if (fishChance.getChance() > 0.0f) {
                runningTotal += fishChance.getChance();
                if (runningTotal > rno) {
                    return fishChance;
                }
            }
        }
        return null;
    }
    
    public static boolean processSpearStrike(final Creature performer, final Action act, final Skill fishing, final Item spear, final float posX, final float posY) {
        final Creature fish = act.getCreature();
        if (fish == null) {
            act.setTickCount(24);
            return false;
        }
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final float dx = Math.abs(fish.getStatus().getPositionX() - posX);
        final float dy = Math.abs(fish.getStatus().getPositionY() - posY);
        final float dd = (float)Math.sqrt(dx * dx + dy * dy);
        testMessage(performer, "Distance away was " + dd + " fish:" + fish.getStatus().getPositionX() + "," + fish.getStatus().getPositionY() + " strike:" + posX + "," + posY, "");
        performer.sendSpearStrike(posX, posY);
        if (dd > 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You missed the " + faid.getNameWithSize() + "!");
            Server.getInstance().broadCastAction(performer.getName() + " attempted to spear a fish and failed!", performer, 5);
            act.setTickCount(23);
            return false;
        }
        final float result = getDifficulty(performer, act, performer.getPosX(), performer.getPosY(), fishing, spear, null, null, null, null, null, 0.0f, 100.0f);
        if (result <= 0.0f) {
            if (result < -80.0f && performer.isWithinDistanceTo(posX, posY, 0.0f, 2.0f)) {
                final Skill bodyStr = performer.getSkills().getSkillOrLearn(102);
                final byte foot = (byte)(Server.rand.nextBoolean() ? 15 : 16);
                performer.getCommunicator().sendNormalServerMessage("You miss the " + fish.getName() + " and hit your own foot!");
                Server.getInstance().broadCastAction(performer.getName() + " spears their own foot.. how silly!", performer, 5);
                performer.addWoundOfType(null, (byte)2, foot, false, 1.0f, true, 400.0 * bodyStr.getKnowledge(0.0), 0.0f, 0.0f, false, false);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You missed the " + fish.getName() + "!");
                Server.getInstance().broadCastAction(performer.getName() + " attempted to spear a fish and failed!", performer, 5);
            }
            act.setTickCount(23);
        }
        else {
            act.setTickCount(22);
            performer.getCommunicator().sendNormalServerMessage("You managed to spear the " + fish.getName() + "!");
            Server.getInstance().broadCastAction(performer.getName() + " managed to spear a fish!", performer, 5);
            performer.achievement(559);
        }
        return false;
    }
    
    public static boolean processFishStrike(final Creature performer, final Action act, final Skill fishing, final Item rod) {
        final Creature fish = act.getCreature();
        if (fish == null) {
            act.setTickCount(5);
            return false;
        }
        if (rod == null) {
            act.setTickCount(7);
            return false;
        }
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        String extra = "";
        if (act.getTickCount() != 18) {
            if (act.getTickCount() != 4 && act.getTickCount() != 2 && act.getTickCount() != 19) {
                if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
                    extra = " (Test only) Cmd:" + fromCommand((byte)act.getTickCount());
                }
                performer.getCommunicator().sendNormalServerMessage("You scare off the " + faid.getNameWithSize() + " before it starts feeding." + extra);
                processFishMovedOn(performer, act, 2.0f);
                act.setTickCount(4);
                performer.getCommunicator().sendFishSubCommand((byte)4, -1L);
            }
            return false;
        }
        final Item[] fishingItems = rod.getFishingItems();
        final Item fishingReel = fishingItems[0];
        final Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        final Item fishingHook = fishingItems[3];
        final Item fishingBait = fishingItems[4];
        final float result = getDifficulty(performer, act, performer.getPosX(), performer.getPosY(), fishing, rod, fishingReel, fishingLine, fishingFloat, fishingHook, fishingBait, 0.0f, 40.0f);
        if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
            extra = " (Test only) Res:" + result;
        }
        act.setTickCount(3);
        return false;
    }
    
    private static float getDifficulty(final Creature performer, final Action act, final float posx, final float posy, final Skill fishing, final Item source, final Item reel, final Item line, final Item fishingFloat, final Item hook, final Item bait, final float bonus, final float times) {
        final Creature fish = act.getCreature();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        float difficulty = faid.getDifficulty();
        if (difficulty == -10.0f) {
            final FishEnums.FishData fd = FishEnums.FishData.fromInt(faid.getFishTypeId());
            final float knowledge = (float)fishing.getKnowledge();
            difficulty = fd.getDifficulty(knowledge, posx, posy, performer.isOnSurface(), source, reel, line, fishingFloat, hook, bait);
            faid.setDifficulty(difficulty);
            testMessage(performer, "", fd.getName() + " Dif:" + difficulty);
        }
        final double result = fishing.skillCheck(difficulty, source, bonus, false, times);
        return (float)result;
    }
    
    private static boolean processSpearCancel(final Creature performer, final Action act) {
        performer.getCommunicator().sendNormalServerMessage("You have cancelled your spearing!");
        return sendSpearStop(performer, act);
    }
    
    private static boolean processSpearMove(final Creature performer, final Action act, final Item spear, final Skill fishing) {
        if (act.getCreature() != null) {
            act.setTickCount(28);
            return false;
        }
        if (makeFish(performer, act, spear, fishing, (byte)20)) {
            return true;
        }
        act.setTickCount(21);
        return false;
    }
    
    private static boolean processSpearSwamAway(final Creature performer, final Action act, final Item spear, final int delay) {
        performer.getCommunicator().sendNormalServerMessage("The " + act.getCreature().getName() + " swims off.");
        destroyFishCreature(act);
        final int moreTime = delay + Server.rand.nextInt(250) - spear.getRarity() * 10;
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        act.setTickCount(21);
        return false;
    }
    
    private static boolean processSpearMissed(final Creature performer, final Action act, final Item spear) {
        final Creature fish = act.getCreature();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        faid.setRaceAway(true);
        final int moreTime = (int)(faid.getTimeToTarget() / 2.0f);
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        act.setTickCount(21);
        final FishEnums.FishData fd = faid.getFishData();
        final float fdam = fd.getDamageMod();
        final float damMod = fdam * Math.max(0.1f, faid.getWeight() / 3000);
        final float additionalDamage = additionalDamage(spear, damMod * 10.0f, true);
        return additionalDamage > 0.0f && spear.setDamage(spear.getDamage() + additionalDamage);
    }
    
    private static boolean processSpearHit(final Creature performer, final Action act, final Skill fishing, final Item spear) {
        final Creature fish = act.getCreature();
        if (fish == null) {
            return true;
        }
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final byte fishTypeId = faid.getFishTypeId();
        performer.getCommunicator().sendSpearHit(fishTypeId, fish.getWurmId());
        makeDeadFish(performer, act, fishing, fishTypeId, spear, performer.getInventory());
        destroyFishCreature(act);
        final FishEnums.FishData fd = faid.getFishData();
        final float fdam = fd.getDamageMod();
        final float damMod = fdam * Math.max(0.1f, faid.getWeight() / 3000);
        final float additionalDamage = additionalDamage(spear, damMod * 8.0f, true);
        return additionalDamage > 0.0f && spear.setDamage(spear.getDamage() + additionalDamage);
    }
    
    private static boolean sendSpearStop(final Creature performer, final Action act) {
        destroyFishCreature(act);
        performer.getCommunicator().sendFishSubCommand((byte)29, -1L);
        return true;
    }
    
    private static boolean makeFish(final Creature performer, final Action act, final Item source, final Skill fishing, final byte startCmd) {
        final FishRow fr = caughtFish(performer, act.getPosX(), act.getPosY(), source);
        if (fr == null) {
            final int moreTime = 100 + Server.rand.nextInt(250);
            final int timeleft = act.getSecond() * 10 + moreTime;
            act.setData(timeleft);
            testMessage(performer, "No Fish!", " Next attempt in approx:" + moreTime / 10 + "s");
            return false;
        }
        float speedMod = 1.0f;
        final float rot = performer.getStatus().getRotation();
        float angle;
        if (performer.getVehicle() == -10L) {
            final int ang = Server.rand.nextInt(40);
            if (Server.rand.nextBoolean()) {
                angle = Creature.normalizeAngle(rot - 130.0f + ang);
            }
            else {
                angle = Creature.normalizeAngle(rot + 130.0f - ang);
            }
        }
        else {
            angle = Server.rand.nextInt(360);
        }
        Point4f mid;
        Point4f start;
        Point4f end;
        if (startCmd == 20) {
            final float dist = 0.05f + Server.rand.nextFloat();
            mid = calcSpot(act.getPosX(), act.getPosY(), performer.getStatus().getRotation(), dist);
            start = calcSpot(mid.getPosX(), mid.getPosY(), angle, 10.0f);
            end = calcSpot(start.getPosX(), start.getPosY(), start.getRot(), 20.0f);
            try {
                if (Zones.calculateHeight(start.getPosX(), start.getPosY(), performer.isOnSurface()) > 0.0f) {
                    final Point4f temp = start;
                    start = end;
                    end = temp;
                }
            }
            catch (NoSuchZoneException e) {
                MethodsFishing.logger.log(Level.WARNING, e.getMessage(), e);
            }
            speedMod = 0.5f;
        }
        else {
            mid = new Point4f(act.getPosX(), act.getPosY(), 0.0f, Creature.normalizeAngle(rot + 180.0f));
            start = calcSpot(mid.getPosX(), mid.getPosY(), angle, 10.0f);
            end = new Point4f(mid.getPosX(), mid.getPosY(), 0.0f, start.getRot());
            try {
                if (Zones.calculateHeight(start.getPosX(), start.getPosY(), performer.isOnSurface()) > 0.0f) {
                    final Point4f temp2 = start;
                    start = end;
                    end = temp2;
                }
            }
            catch (NoSuchZoneException e2) {
                MethodsFishing.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
            speedMod = 1.2f;
        }
        final double ql = getQL(performer, source, fishing, fr.getFishTypeId(), act.getPosX(), act.getPosY(), true);
        final Creature fish = makeFishCreature(performer, start, fr.getName(), ql, fr.getFishTypeId(), speedMod, end);
        if (fish == null) {
            performer.getCommunicator().sendNormalServerMessage("You jump as you see a ghost of a fish, and stop fishing.");
            return true;
        }
        act.setCreature(fish);
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final int moreTime2 = (int)faid.getTimeToTarget();
        final int timeleft2 = act.getSecond() * 10 + moreTime2;
        act.setData(timeleft2);
        if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
            if (performer.getPower() >= 2) {
                testMessage(performer, "", performer.getName() + " @px:" + (int)performer.getPosX() + ",py:" + (int)performer.getPosY() + ",pr:" + (int)performer.getStatus().getRotation());
            }
            if (startCmd == 20) {
                addMarker(performer, fish.getName() + " mid", mid);
            }
            addMarker(performer, fish.getName() + " start", start);
            addMarker(performer, fish.getName() + " end", end);
        }
        return false;
    }
    
    @Nullable
    public static Creature makeFishCreature(final Creature performer, final Point4f start, final String fishName, final double ql, final byte fishTypeId, final float speedMod, final Point4f end) {
        try {
            final Creature fish = Creature.doNew(119, start.getPosX(), start.getPosY(), start.getRot(), performer.getLayer(), fishName, (byte)(Server.rand.nextBoolean() ? 0 : 1), (byte)0);
            fish.setVisible(false);
            final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
            faid.setFishTypeId(fishTypeId);
            faid.setQL(ql);
            faid.setMovementSpeedModifier(speedMod);
            faid.setTargetPos(end.getPosX(), end.getPosY());
            fish.setVisible(true);
            return fish;
        }
        catch (Exception e) {
            MethodsFishing.logger.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
    
    public static void addMarker(final Creature performer, final String string, final Point4f loc) {
        if (!Servers.isThisATestServer()) {
            return;
        }
        String sloc = "";
        if (performer.getPower() >= 2) {
            final int depth = FishEnums.getWaterDepth(loc.getPosX(), loc.getPosY(), performer.isOnSurface());
            sloc = " @x:" + (int)loc.getPosX() + ", y:" + (int)loc.getPosY() + ", r:" + (int)loc.getRot() + ", d:" + depth;
            testMessage(performer, string, sloc);
        }
        if (performer.getPower() >= 5 && performer.hasFlag(51)) {
            final VolaTile vtile = Zones.getOrCreateTile(loc.getTileX(), loc.getTileY(), performer.isOnSurface());
            try {
                final Item marker = ItemFactory.createItem(344, 1.0f, null);
                marker.setSizes(10, 10, 100);
                marker.setPosXY(loc.getPosX(), loc.getPosY());
                marker.setRotation(loc.getRot());
                marker.setDescription(string + sloc);
                vtile.addItem(marker, false, false);
            }
            catch (FailedException e) {
                MethodsFishing.logger.log(Level.WARNING, e.getMessage(), e);
            }
            catch (NoSuchTemplateException e2) {
                MethodsFishing.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
        }
    }
    
    public static Point4f calcSpot(final float posx, final float posy, final float rot, final float dist) {
        final float r = rot * 3.1415927f / 180.0f;
        final float s = (float)Math.sin(r);
        final float c = (float)Math.cos(r);
        final float xo = s * dist;
        final float yo = c * -dist;
        final float newx = posx + xo;
        final float newy = posy + yo;
        final float angle = rot + 180.0f;
        return new Point4f(newx, newy, 0.0f, Creature.normalizeAngle(angle));
    }
    
    private static int makeDeadFish(final Creature performer, final Action act, final Skill fishing, final byte fishTypeId, final Item source, final Item container) {
        final Creature fish = act.getCreature();
        FishEnums.FishData fd;
        double ql;
        int weight;
        if (fish == null) {
            if (source.getTemplateId() != 1343) {
                return 0;
            }
            fd = FishEnums.FishData.fromInt(fishTypeId);
            ql = getQL(performer, source, fishing, fishTypeId, act.getPosX(), act.getPosY(), false);
            final ItemTemplate it = fd.getTemplate();
            if (it == null) {
                testMessage(performer, "", fd.getName() + " no template!");
                return 0;
            }
            weight = (int)(it.getWeightGrams() * (ql / 100.0));
        }
        else {
            final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
            fd = faid.getFishData();
            ql = faid.getQL();
            weight = faid.getWeight();
        }
        if (weight == 0) {
            performer.getCommunicator().sendNormalServerMessage("You fumbled when trying to move the fish to " + container.getName() + ", and it got away.");
            return 0;
        }
        testMessage(performer, "", fd.getTemplate().getName() + ":" + weight + "g");
        if (weight >= fd.getMinWeight()) {
            try {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                final Item deadFish = ItemFactory.createItem(fd.getTemplateId(), (float)ql, (byte)2, act.getRarity(), performer.getName());
                deadFish.setSizes(weight);
                deadFish.setWeight(weight, false);
                final boolean inserted = container.insertItem(deadFish);
                if (inserted) {
                    if (source.getTemplateId() == 1343) {
                        performer.getCommunicator().sendNormalServerMessage("You catch " + deadFish.getNameWithGenus() + " in the " + source.getName() + ".");
                    }
                    if (fd.getTypeId() == FishEnums.FishData.CLAM.getTypeId()) {
                        performer.getCommunicator().sendNormalServerMessage("You will need to pry open the clam with a knife.");
                    }
                    performer.achievement(126);
                    if (weight > 3000) {
                        performer.achievement(542);
                    }
                    if (weight > 10000) {
                        performer.achievement(585);
                    }
                    if (weight > 175000) {
                        performer.achievement(297);
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You fumbled when trying to move the " + deadFish.getName() + " to " + container.getName() + ", and it got away.");
                    Items.destroyItem(deadFish.getWurmId());
                }
                act.setRarity(performer.getRarity());
            }
            catch (FailedException e) {
                MethodsFishing.logger.log(Level.WARNING, e.getMessage(), e);
            }
            catch (NoSuchTemplateException e2) {
                MethodsFishing.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
            return weight;
        }
        if (source.getTemplateId() == 1343) {
            performer.getCommunicator().sendNormalServerMessage("The " + fd.getTemplate().getName() + "  was so small, it swam through the net.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The " + fd.getTemplate().getName() + "  was so small, you threw it back in.");
            Server.getInstance().broadCastAction(performer.getName() + " throws the tiddler " + fd.getTemplate().getName() + " back in.", performer, 5);
        }
        return 0;
    }
    
    private static float additionalDamage(final Item item, final float damMod, final boolean flexible) {
        if (item == null) {
            return 0.0f;
        }
        final float typeMod = item.isWood() ? 2.0f : (item.isMetal() ? 1.0f : 0.5f);
        final float newDam = typeMod * item.getDamageModifier(false, flexible) / (10.0f * Math.max(10.0f, item.getQualityLevel()));
        final float qlMod = (100.0f - item.getCurrentQualityLevel()) / 50.0f;
        final float extraDam = Math.min(qlMod, Math.max(0.1f, newDam) * damMod);
        return extraDam;
    }
    
    private static double getQL(final Creature performer, final Item source, final Skill fishing, final byte fishTypeId, final float posx, final float posy, final boolean noSkill) {
        final Item[] fishingItems = source.getFishingItems();
        final Item fishingReel = fishingItems[0];
        final Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        final Item fishingHook = fishingItems[3];
        final Item fishingBait = fishingItems[4];
        final FishEnums.FishData fd = FishEnums.FishData.fromInt(fishTypeId);
        final float knowledge = (float)fishing.getKnowledge();
        final float difficulty = fd.getDifficulty(knowledge, posx, posy, performer.isOnSurface(), source, fishingReel, fishingLine, fishingFloat, fishingHook, fishingBait);
        final double power = fishing.skillCheck(difficulty, null, 0.0, noSkill, 10.0f);
        final double ql = 10.0 + 0.9 * Math.max(Server.rand.nextFloat() * 10.0f, power);
        return ql;
    }
    
    private static boolean autoCast(final Creature performer, final Action act, final Item rod) {
        final Item[] fishingItems = rod.getFishingItems();
        final Item line = fishingItems[1];
        final float[] radi = getMinMaxRadius(rod, line);
        final float minRadius = radi[0];
        final float maxRadius = radi[1];
        final float half = (maxRadius + minRadius) / 2.0f;
        final float rot = performer.getStatus().getRotation();
        final float r = rot * 3.1415927f / 180.0f;
        final float s = (float)Math.sin(r);
        final float c = (float)Math.cos(r);
        final float xo = s * half;
        final float yo = c * -half;
        final float castX = performer.getPosX() + xo;
        final float castY = performer.getPosY() + yo;
        final int tilex = (int)castX >> 2;
        final int tiley = (int)castY >> 2;
        int tile;
        if (performer.isOnSurface()) {
            tile = Server.surfaceMesh.getTile(tilex, tiley);
        }
        else {
            tile = Server.caveMesh.getTile(tilex, tiley);
        }
        if (!Terraforming.isTileUnderWater(tile, tilex, tiley, performer.isOnSurface())) {
            performer.getCommunicator().sendNormalServerMessage("The water is too shallow to fish.");
            testMessage(performer, "", performer.getTileX() + "=" + tilex + "," + performer.getTileY() + "=" + tiley);
            return true;
        }
        testMessage(performer, "Auto cast as you were too lazy to cast!", "");
        Server.getInstance().broadCastAction(performer.getName() + " casts and starts fishing.", performer, 5);
        act.setTickCount(9);
        return processFishCasted(performer, act, castX, castY, rod, false);
    }
    
    private static boolean processFishCasted(final Creature performer, final Action act, final float castX, final float castY, final Item rod, final boolean manualMode) {
        act.setPosX(castX);
        act.setPosY(castY);
        final int defaultTimer = 1800;
        act.setTimeLeft(1800);
        performer.sendActionControl(Actions.actionEntrys[160].getVerbString(), true, 1800);
        final Item[] fishingItems = rod.getFishingItems();
        final Item floatItem = fishingItems[2];
        performer.sendFishingLine(castX, castY, FishEnums.FloatType.fromItem(floatItem).getTypeId());
        act.setTickCount(16);
        final int moreTime = getNextFishDelay(rod, 100, 600);
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
            final Point4f cast = new Point4f(castX, castY, 0.0f, 0.0f);
            addMarker(performer, (manualMode ? "Manual" : "Auto") + " cast (" + moreTime / 10 + "s)", cast);
        }
        return false;
    }
    
    private static boolean processFishMove(final Creature performer, final Action act, final Skill fishing, final Item rod) {
        if (act.getCreature() != null) {
            act.setTickCount(14);
            return false;
        }
        if (makeFish(performer, act, rod, fishing, (byte)0)) {
            return true;
        }
        if (act.getCreature() == null) {
            return false;
        }
        final Creature fish = act.getCreature();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final int moreTime = (int)faid.getTimeToTarget();
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        act.setTickCount(1);
        return false;
    }
    
    private static boolean processFishMovedOn(final Creature performer, final Action act) {
        performer.getCommunicator().sendFishSubCommand((byte)2, -1L);
        processFishMovedOn(performer, act, 1.2f);
        act.setTickCount(19);
        return false;
    }
    
    private static boolean processFishMovingOn(final Creature performer, final Action act) {
        act.setTickCount(16);
        return false;
    }
    
    private static boolean processFishMovedOn(final Creature performer, final Action act, final float speed) {
        final Creature fish = act.getCreature();
        testMessage(performer, fish.getName() + " Swims off.", "");
        performer.getCommunicator().sendNormalServerMessage("The " + fish.getName() + " swims off into the distance.");
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final Point4f end = calcSpot(fish.getPosX(), fish.getPosY(), performer.getStatus().getRotation(), 10.0f);
        faid.setMovementSpeedModifier(faid.getMovementSpeedModifier() * speed);
        faid.setTargetPos(end.getPosX(), end.getPosY());
        final int moreTime = (int)faid.getTimeToTarget();
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        return false;
    }
    
    private static boolean processFishSwamAway(final Creature performer, final Action act, final Item rod, final int delay) {
        destroyFishCreature(act);
        final int moreTime = getNextFishDelay(rod, delay, 200);
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        act.setTickCount(16);
        return false;
    }
    
    private static int getNextFishDelay(final Item rod, final int delay, final int rnd) {
        final Item[] fishingItems = rod.getFishingItems();
        final Item bait = fishingItems[4];
        final int bonus = (bait == null) ? 0 : (bait.getRarity() * 10);
        final Item hook = fishingItems[3];
        final float fmod = (hook == null) ? 1.0f : hook.getMaterialFragrantModifier();
        return (int)((delay + Server.rand.nextInt(rnd) - bonus) * fmod);
    }
    
    private static boolean processFishCancel(final Creature performer, final Action act) {
        performer.getCommunicator().sendNormalServerMessage("You have cancelled your fishing!");
        return sendFishStop(performer, act);
    }
    
    private static boolean processFishBite(final Creature performer, final Action act, final Item rod, final Skill fishing) {
        final Creature fish = act.getCreature();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
            final String fpos = (performer.getPower() >= 2) ? (" fx:" + (int)fish.getPosX() + ",fy:" + (int)fish.getPosY() + " cx:" + (int)act.getPosX() + ",cy:" + (int)act.getPosY()) : "";
            testMessage(performer, fish.getName() + " takes a bite!", fpos);
        }
        final Item[] fishingItems = rod.getFishingItems();
        final Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        Item fishingHook = fishingItems[3];
        Item fishingBait = fishingItems[4];
        final FishEnums.FishData fd = faid.getFishData();
        final float damMod = fd.getDamageMod() * Math.max(0.1f, faid.getWeight() / 3000);
        float additionalDamage = additionalDamage(fishingFloat, damMod * 5.0f, false);
        float newDamage = fishingFloat.getDamage() + additionalDamage;
        if (newDamage > 100.0f) {}
        if (additionalDamage > 0.0f) {
            fishingFloat.setDamage(newDamage);
        }
        additionalDamage = additionalDamage(fishingHook, damMod * 10.0f, false);
        newDamage = fishingHook.getDamage() + additionalDamage;
        if (newDamage > 100.0f) {
            destroyContents(fishingHook);
        }
        if (additionalDamage > 0.0f) {
            fishingHook.setDamage(newDamage);
        }
        fishingHook = fishingLine.getFishingHook();
        if (fishingHook != null) {
            fishingBait = fishingHook.getFishingBait();
            if (fishingBait != null) {
                final float dam = getBaitDamage(fd, fishingBait);
                if (fishingBait.setDamage(fishingBait.getDamage() + dam)) {}
            }
        }
        final Item[] newFishingItems = rod.getFishingItems();
        final Item newFishingBait = newFishingItems[4];
        final int bonus = (newFishingBait == null) ? 0 : (newFishingBait.getRarity() * 10);
        final int skillBonus = (int)(fishing.getKnowledge(0.0) / 3.0);
        final int moreTime = 35 + skillBonus + Server.rand.nextInt(20) + bonus;
        final int timeleft = act.getSecond() * 10 + moreTime;
        act.setData(timeleft);
        act.setTickCount(18);
        performer.getCommunicator().sendFishBite(faid.getFishTypeId(), fish.getWurmId(), -1L);
        performer.getCommunicator().sendNormalServerMessage("You feel something nibble on the line.");
        return false;
    }
    
    private static float getBaitDamage(final FishEnums.FishData fd, final Item bait) {
        final float crumbles = FishEnums.BaitType.fromItem(bait).getCrumbleFactor();
        final float dif = fd.getTemplateDifficulty();
        final float base = dif + Server.rand.nextFloat() * 20.0f;
        final float newDam = (base + 10.0f) / crumbles;
        return Math.max(20.0f, newDam);
    }
    
    private static float getRodDamageModifier(final Item rod) {
        final Item reel = rod.getFishingReel();
        if (reel == null) {
            return 5.0f;
        }
        switch (reel.getTemplateId()) {
            case 1372: {
                return 4.5f;
            }
            case 1373: {
                return 4.0f;
            }
            case 1374: {
                return 3.5f;
            }
            case 1375: {
                return 2.5f;
            }
            default: {
                return 5.0f;
            }
        }
    }
    
    private static float getReelDamageModifier(final Item reel) {
        if (reel == null) {
            return 0.0f;
        }
        switch (reel.getTemplateId()) {
            case 1372: {
                return 0.1f;
            }
            case 1373: {
                return 0.07f;
            }
            case 1374: {
                return 0.05f;
            }
            case 1375: {
                return 0.02f;
            }
            default: {
                return 0.1f;
            }
        }
    }
    
    private static boolean autoReplace(final Creature performer, final int templateId, final byte material, final Item targetContainer) {
        final Item tacklebox = performer.getBestTackleBox();
        if (tacklebox != null) {
            final Item compartment = getBoxCompartment(tacklebox, templateId);
            if (compartment != null) {
                final Item[] contents = compartment.getItemsAsArray();
                if (contents.length > 0) {
                    for (final Item item : contents) {
                        if (item.getTemplateId() == templateId && (material == 0 || item.getMaterial() == material)) {
                            targetContainer.insertItem(item);
                            item.sendUpdate();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean doAutoReplace(final Creature performer, final Action act) {
        final Item rod = act.getSubject();
        if (rod == null) {
            return true;
        }
        final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
        final boolean hasTacklebox = performer.getBestTackleBox() != null;
        final float knowledge = (float)fishing.getKnowledge(0.0);
        boolean replaced = false;
        Item reel = rod.getFishingReel();
        if (rod.getTemplateId() == 1346 && reel == null && hasTacklebox) {
            if (knowledge >= 90.0f) {
                final FishEnums.ReelType reelType = FishEnums.ReelType.fromInt(rod.getData1() >> 12 & 0xF);
                final byte reelMaterial = (byte)(rod.getData2() >> 8 & 0xFF);
                replaced = autoReplace(performer, reelType.getTemplateId(), reelMaterial, rod);
                if (replaced) {
                    reel = rod.getFishingReel();
                    performer.getCommunicator().sendNormalServerMessage("You managed to put another " + reel.getName() + " in the " + rod.getName() + "!");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("No replacement reel found!");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot remember what the reel was!");
            }
            if (!replaced) {
                performer.getCommunicator().sendNormalServerMessage("You are missing reel, line, float, fishing hook and bait!");
                return true;
            }
        }
        final Item lineParent = (rod.getTemplateId() == 1346) ? reel : rod;
        replaced = false;
        Item line = lineParent.getFishingLine();
        if (line == null && hasTacklebox) {
            if (knowledge > 70.0f || reel != null) {
                final int lineTemplateId = FishEnums.ReelType.fromItem(reel).getAssociatedLineTemplateId();
                replaced = autoReplace(performer, lineTemplateId, (byte)0, lineParent);
                if (replaced) {
                    line = lineParent.getFishingLine();
                    performer.getCommunicator().sendNormalServerMessage("You managed to put another " + line.getName() + " in the " + lineParent.getName() + "!");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("No replacement line found!");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot remember what the line was!");
            }
            if (!replaced) {
                performer.getCommunicator().sendNormalServerMessage("You are missing line, float, fishing hook and bait!");
                return true;
            }
        }
        Item afloat = line.getFishingFloat();
        if (afloat == null && hasTacklebox) {
            if (knowledge > 50.0f) {
                final FishEnums.FloatType floatType = FishEnums.FloatType.fromInt(rod.getData1() >> 8 & 0xF);
                replaced = autoReplace(performer, floatType.getTemplateId(), (byte)0, line);
                if (replaced) {
                    afloat = line.getFishingFloat();
                    final String floatName = afloat.getName();
                    performer.getCommunicator().sendNormalServerMessage("You managed to put another " + floatName + " on the " + line.getName() + "!");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("No replacement float found!");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot remember what the float was!");
            }
        }
        Item hook = line.getFishingHook();
        if (hook == null && hasTacklebox) {
            if (knowledge > 30.0f) {
                final FishEnums.HookType hookType = FishEnums.HookType.fromInt(rod.getData1() >> 4 & 0xF);
                final byte hookMaterial = (byte)(rod.getData2() & 0xFF);
                replaced = autoReplace(performer, hookType.getTemplateId(), hookMaterial, line);
                if (replaced) {
                    hook = line.getFishingHook();
                    performer.getCommunicator().sendNormalServerMessage("You managed to put another " + hook.getName() + " on the " + line.getName() + "!");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("No replacement fishing hook found!");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot remember what the fishing hook was!");
            }
        }
        Item bait = null;
        if (hook != null) {
            bait = hook.getFishingBait();
            if (bait == null && hasTacklebox) {
                if (knowledge > 10.0f) {
                    final FishEnums.BaitType baitType = FishEnums.BaitType.fromInt(rod.getData1() & 0xF);
                    replaced = autoReplace(performer, baitType.getTemplateId(), (byte)0, hook);
                    if (replaced) {
                        bait = hook.getFishingBait();
                        performer.getCommunicator().sendNormalServerMessage("You managed to put another " + bait.getName() + " on the " + hook.getName() + "!");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("No replacement bait found!");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You cannot remember what the bait was!");
                }
            }
        }
        if (afloat == null && hook == null) {
            performer.getCommunicator().sendNormalServerMessage("You are missing a float, fishing hook and bait!");
            return true;
        }
        if (afloat == null && bait == null) {
            performer.getCommunicator().sendNormalServerMessage("You are missing a float and bait!");
            return true;
        }
        if (afloat == null) {
            performer.getCommunicator().sendNormalServerMessage("You are missing a float!");
            return true;
        }
        if (hook == null) {
            performer.getCommunicator().sendNormalServerMessage("You are missing a fishing hook and bait!");
            return true;
        }
        if (bait == null) {
            performer.getCommunicator().sendNormalServerMessage("You are missing a bait!");
            return true;
        }
        return true;
    }
    
    @Nullable
    private static Item getBoxCompartment(final Item tacklebox, final int templateId) {
        for (final Item compartment : tacklebox.getItems()) {
            for (final ContainerRestriction cRest : compartment.getTemplate().getContainerRestrictions()) {
                if (cRest.contains(templateId)) {
                    return compartment;
                }
            }
        }
        return null;
    }
    
    private static boolean processFishPause(final Creature performer, final Action act) {
        act.setTickCount(2);
        return false;
    }
    
    private static boolean processFishHooked(final Creature performer, final Action act, final Skill fishing, final Item rod) {
        if (act.getCreature() == null) {
            act.setTickCount(5);
        }
        else {
            final Creature fish = act.getCreature();
            final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
            performer.getCommunicator().sendFishSubCommand((byte)3, -1L);
            performer.sendFishHooked(faid.getFishTypeId(), fish.getWurmId());
            if (processFishPull(performer, act, fishing, rod, true)) {
                sendFishStop(performer, act);
                return true;
            }
        }
        return false;
    }
    
    private static boolean processFishPull(final Creature performer, final Action act, final Skill fishing, final Item rod, final boolean initial) {
        final Item[] fishingItems = rod.getFishingItems();
        Item fishingReel = fishingItems[0];
        Item fishingLine = fishingItems[1];
        final Item fishingFloat = fishingItems[2];
        final Item fishingHook = fishingItems[3];
        final Item fishingBait = fishingItems[4];
        final Creature fish = act.getCreature();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        final boolean isClam = faid.getFishTypeId() == FishEnums.FishData.CLAM.getTypeId();
        if (initial) {
            performer.getCommunicator().sendNormalServerMessage("You hooked " + faid.getNameWithGenusAndSize() + "!");
            Server.getInstance().broadCastAction(performer.getName() + " hooks " + faid.getNameWithGenusAndSize() + ".", performer, 5);
        }
        final float fstr = faid.getBodyStrength();
        final float pstr = (float)performer.getSkills().getSkillOrLearn(102).getKnowledge(0.0);
        final float strBonus = Math.max(1.0f, pstr - fstr);
        final float stamBonus = faid.getBodyStamina() - 20.0f;
        final float bonus = (strBonus + stamBonus) / 2.0f;
        float adjusted;
        final float result = adjusted = getDifficulty(performer, act, act.getPosX(), act.getPosY(), fishing, rod, fishingReel, fishingLine, fishingFloat, fishingHook, fishingBait, bonus, 10.0f);
        if (fishingReel != null) {
            adjusted += fishingReel.getRarity() * fishingReel.getRarity();
        }
        if (adjusted < 0.0f && !isClam) {
            if (result <= -(90 + rod.getRarity() * 3)) {
                act.setTickCount(7);
            }
            else if (result <= -(70 + fishingLine.getRarity() * 3)) {
                act.setTickCount(6);
            }
            else if (result <= -(50 + fishingHook.getRarity() * 3)) {
                act.setTickCount(13);
            }
            else {
                if (!initial) {
                    if (result <= -30.0f) {
                        performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " pulls hard on the line!");
                    }
                    else if (result <= -15.0f) {
                        performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " pulls somewhat on the line!");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " pulls a bit on the line!");
                    }
                }
                testMessage(performer, faid.getNameWithSize() + " moving away", " Result:" + result);
                faid.decBodyStamina(strBonus / 2.0f + 2.0f);
                final double lNewrot = Math.atan2(performer.getPosY() - fish.getPosY(), performer.getPosX() - fish.getPosX());
                final float rot = (float)(lNewrot * 57.29577951308232) - 90.0f;
                final float angle = rot + result * 2.0f + 90.0f;
                final float dist = Math.min(2.0f, strBonus / 10.0f);
                final Point4f end = calcSpot(fish.getPosX(), fish.getPosY(), Creature.normalizeAngle(angle), dist);
                final float speedMod = dist / 2.0f;
                faid.setMovementSpeedModifier(speedMod);
                faid.setTargetPos(end.getPosX(), end.getPosY());
                final int moreTime = (int)faid.getTimeToTarget();
                final int timeleft = act.getSecond() * 10 + moreTime;
                act.setData(timeleft);
                act.setTickCount(17);
            }
        }
        else {
            if (!initial && !isClam) {
                if (result > 80.0f) {
                    performer.getCommunicator().sendNormalServerMessage("You manage to easily reel in the " + faid.getNameWithSize() + "!");
                }
                else if (result > 50.0f) {
                    performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " stands no chance!");
                }
                else if (result > 25.0f) {
                    performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " takes a rest, so you reel it in a bit!");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The " + faid.getNameWithSize() + " starts to get tired and you manage to reel it in a bit!");
                }
            }
            final double lNewrot2 = Math.atan2(performer.getPosY() - fish.getPosY(), performer.getPosX() - fish.getPosX());
            final float rot2 = (float)(lNewrot2 * 57.29577951308232);
            float speedMod2;
            Point4f end2;
            if (isClam) {
                performer.getCommunicator().sendNormalServerMessage("You quickly reel in the " + faid.getNameWithSize() + "!");
                speedMod2 = 2.0f;
                end2 = new Point4f(performer.getPosX(), performer.getPosY(), 0.0f, Creature.normalizeAngle(rot2 + 180.0f));
            }
            else {
                testMessage(performer, faid.getNameWithSize() + " moving closer", " Result:" + result);
                faid.decBodyStamina(1.0f);
                final float angle2 = rot2 + result + 40.0f;
                final float dist2 = Math.min(2.5f, strBonus / 15.0f + Math.max(0.0f, result - 50.0f) / 15.0f);
                speedMod2 = 1.0f;
                end2 = calcSpot(fish.getPosX(), fish.getPosY(), Creature.normalizeAngle(angle2), dist2);
            }
            faid.setMovementSpeedModifier(speedMod2);
            faid.setTargetPos(end2.getPosX(), end2.getPosY());
            final int moreTime2 = (int)faid.getTimeToTarget();
            final int timeleft2 = act.getSecond() * 10 + moreTime2;
            act.setData(timeleft2);
            act.setTickCount(17);
        }
        final FishEnums.FishData fd = faid.getFishData();
        final float fdam = fd.getDamageMod();
        final float damMod = (float)(fdam * Math.max(0.10000000149011612, (faid.getWeight() > 6000) ? Math.pow(faid.getWeight() / 1000, 0.6) : ((double)(faid.getWeight() / 3000))));
        float additionalDamage = additionalDamage(rod, damMod * getRodDamageModifier(rod), true);
        float newDamage = rod.getDamage() + additionalDamage;
        if (newDamage > 100.0f) {
            destroyContents(rod);
            return rod.setDamage(newDamage);
        }
        if (additionalDamage > 0.0f) {
            rod.setDamage(newDamage);
        }
        if (rod.getTemplateId() == 1344) {
            fishingLine = rod.getFishingLine();
        }
        else {
            fishingReel = rod.getFishingReel();
            additionalDamage = additionalDamage(fishingReel, damMod * getReelDamageModifier(fishingReel), true);
            newDamage = fishingReel.getDamage() + additionalDamage;
            if (newDamage > 100.0f) {
                destroyContents(fishingReel);
                return fishingReel.setDamage(newDamage);
            }
            if (additionalDamage > 0.0f) {
                fishingReel.setDamage(newDamage);
            }
            fishingLine = fishingReel.getFishingLine();
        }
        additionalDamage = additionalDamage(fishingLine, damMod * 0.3f, true);
        newDamage = fishingLine.getDamage() + additionalDamage;
        if (newDamage > 100.0f) {
            destroyContents(fishingLine);
            return fishingLine.setDamage(newDamage);
        }
        if (additionalDamage > 0.0f) {
            fishingLine.setDamage(newDamage);
        }
        return false;
    }
    
    private static boolean processFishCaught(final Creature performer, final Action act, final Skill fishing, final Item rod) {
        final Creature fish = act.getCreature();
        performer.getCommunicator().sendFishSubCommand((byte)12, -1L);
        performer.sendFishingStopped();
        final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
        performer.getCommunicator().sendNormalServerMessage("You catch " + faid.getNameWithGenusAndSize());
        Server.getInstance().broadCastAction(performer.getName() + " lands " + faid.getNameWithGenusAndSize() + ".", performer, 5);
        makeDeadFish(performer, act, fishing, faid.getFishTypeId(), rod, performer.getInventory());
        destroyFishCreature(act);
        return doAutoReplace(performer, act);
    }
    
    private static boolean processFishLineSnapped(final Creature performer, final Action act, final Item rod) {
        performer.getCommunicator().sendNormalServerMessage("Your line snapped!!");
        final Item[] fishingItems = rod.getFishingItems();
        final Item fishingReel = fishingItems[0];
        final Item fishingLine = fishingItems[1];
        destroyContents(fishingLine);
        final byte currentPhase = (byte)act.getTickCount();
        boolean brokeRod = false;
        if (currentPhase == 7) {
            final Creature fish = act.getCreature();
            final FishAI.FishAIData faid = (FishAI.FishAIData)fish.getCreatureAIData();
            final FishEnums.FishData fd = faid.getFishData();
            final int weight = faid.getWeight();
            final float fdam = fd.getDamageMod() * 5.0f;
            final float damMod = fdam * Math.max(0.1f, weight / 3000);
            float additionalDamage = additionalDamage(rod, damMod * getRodDamageModifier(rod) * 2.0f, true);
            float newDamage = rod.getDamage() + additionalDamage;
            if (newDamage > 100.0f) {
                destroyContents(rod);
            }
            if (additionalDamage > 0.0f) {
                brokeRod = rod.setDamage(newDamage);
            }
            if (!brokeRod && fishingReel != null) {
                additionalDamage = additionalDamage(fishingReel, damMod * getReelDamageModifier(fishingReel) * 2.0f, true);
                newDamage = fishingReel.getDamage() + additionalDamage;
                if (newDamage > 100.0f) {
                    destroyContents(fishingReel);
                }
                if (additionalDamage > 0.0f) {
                    fishingReel.setDamage(newDamage);
                }
            }
        }
        performer.getCommunicator().sendFishSubCommand((byte)6, -1L);
        processFishMovedOn(performer, act, 2.2f);
        act.setTickCount(15);
        return false;
    }
    
    private static void destroyContents(final Item container) {
        for (final Item item : container.getItemsAsArray()) {
            if (!item.isEmpty(false)) {
                destroyContents(item);
            }
            item.setDamage(100.0f);
        }
    }
    
    private static boolean sendFishStop(final Creature performer, final Action act) {
        destroyFishCreature(act);
        performer.getCommunicator().sendFishSubCommand((byte)15, -1L);
        performer.sendFishingStopped();
        return doAutoReplace(performer, act);
    }
    
    public static void fromClient(final Creature performer, final byte subCommand, final float posX, final float posY) {
        try {
            final Action act = performer.getCurrentAction();
            if (act.getNumber() != 160) {
                testMessage(performer, "not fishing? ", "Action:" + act.getNumber());
                MethodsFishing.logger.log(Level.WARNING, "not fishing! " + act.getNumber());
                return;
            }
            final byte phase = (byte)act.getTickCount();
            switch (subCommand) {
                case 9: {
                    if (phase != 0) {
                        testMessage(performer, "Incorrect fishing subcommand", " (" + fromCommand(subCommand) + ") for phase (" + fromCommand(phase) + ")");
                        MethodsFishing.logger.log(Level.WARNING, "Incorrect fishing subcommand (" + fromCommand(subCommand) + ") for phase (" + fromCommand(phase) + ")");
                        return;
                    }
                    final Item rod = act.getSubject();
                    if (rod == null) {
                        testMessage(performer, "", "Subject missing in action");
                        MethodsFishing.logger.log(Level.WARNING, "Subject missing in action");
                        break;
                    }
                    performer.getCommunicator().sendNormalServerMessage("You cast the line and start fishing.");
                    Server.getInstance().broadCastAction(performer.getName() + " casts and starts fishing.", performer, 5);
                    processFishCasted(performer, act, posX, posY, rod, true);
                    break;
                }
                case 10: {
                    act.setTickCount(subCommand);
                    break;
                }
                case 11: {
                    final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
                    final Item rod2 = act.getSubject();
                    if (rod2 == null) {
                        testMessage(performer, "", "Subject missing in action");
                        MethodsFishing.logger.log(Level.WARNING, "Subject missing in action");
                        break;
                    }
                    if (canStrike(phase)) {
                        processFishStrike(performer, act, fishing, rod2);
                        break;
                    }
                    break;
                }
                case 27: {
                    act.setTickCount(subCommand);
                    break;
                }
                case 26: {
                    if (phase != 21) {
                        testMessage(performer, "Incorrect fishing subcommand", " (" + fromCommand(subCommand) + ") for phase (" + fromCommand(phase) + ")");
                        MethodsFishing.logger.log(Level.WARNING, "Incorrect fishing subcommand (" + fromCommand(subCommand) + ") for phase (" + fromCommand(phase) + ")");
                        return;
                    }
                    act.setTickCount(subCommand);
                    final Skill fishing = performer.getSkills().getSkillOrLearn(10033);
                    final Item spear = act.getSubject();
                    if (spear == null) {
                        testMessage(performer, "", "Subject missing in action");
                        MethodsFishing.logger.log(Level.WARNING, "Subject missing in action");
                        break;
                    }
                    processSpearStrike(performer, act, fishing, spear, posX, posY);
                    break;
                }
                default: {
                    testMessage(performer, "Bad fishing subcommand!", " (" + fromCommand(subCommand) + ")");
                    MethodsFishing.logger.log(Level.WARNING, "Bad fishing subcommand! " + fromCommand(subCommand) + " (" + subCommand + ")");
                }
            }
        }
        catch (NoSuchActionException e) {
            testMessage(performer, "", "No current action, should be FISH.");
            MethodsFishing.logger.log(Level.WARNING, "No current action, should be FISH:" + e.getMessage(), e);
        }
    }
    
    private static boolean canStrike(final byte phase) {
        switch (phase) {
            case 3:
            case 17: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    private static void testMessage(final Creature performer, final String message, final String powerMessage) {
        if (Servers.isThisATestServer() && (performer.getPower() > 1 || performer.hasFlag(51))) {
            if (performer.getPower() >= 2) {
                performer.getCommunicator().sendNormalServerMessage("(test only) " + message + powerMessage);
            }
            else if (message.length() > 0) {
                performer.getCommunicator().sendNormalServerMessage("(test only) " + message);
            }
        }
    }
    
    public static String fromCommand(final byte command) {
        switch (command) {
            case 0: {
                return "FISH_START";
            }
            case 1: {
                return "FISH_BITE";
            }
            case 2: {
                return "FISH_MOVED_ON";
            }
            case 19: {
                return "FISH_MOVING_ON";
            }
            case 3: {
                return "FISH_HOOKED";
            }
            case 4: {
                return "FISH_MISSED";
            }
            case 5: {
                return "FISH_NO_FISH";
            }
            case 6: {
                return "FISH_LINE_SNAPPED";
            }
            case 7: {
                return "FISH_ROD_BROKE";
            }
            case 8: {
                return "FISH_TIME_OUT";
            }
            case 9: {
                return "FISH_CASTED";
            }
            case 10: {
                return "FISH_CANCEL";
            }
            case 11: {
                return "FISH_STRIKE";
            }
            case 12: {
                return "FISH_CAUGHT";
            }
            case 13: {
                return "FISH_GOT_AWAY";
            }
            case 14: {
                return "FISH_SWAM_AWAY";
            }
            case 15: {
                return "FISH_STOP";
            }
            case 16: {
                return "FISH_MOVE";
            }
            case 17: {
                return "FISH_PULL";
            }
            case 18: {
                return "FISH_PAUSE";
            }
            case 20: {
                return "SPEAR_START";
            }
            case 21: {
                return "SPEAR_MOVE";
            }
            case 22: {
                return "SPEAR_HIT";
            }
            case 23: {
                return "SPEAR_MISSED";
            }
            case 24: {
                return "SPEAR_NO_FISH";
            }
            case 25: {
                return "SPEAR_TIME_OUT";
            }
            case 26: {
                return "SPEAR_STRIKE";
            }
            case 27: {
                return "SPEAR_CANCEL";
            }
            case 28: {
                return "SPEAR_SWAM_AWAY";
            }
            case 29: {
                return "SPEAR_STOP";
            }
            case 40: {
                return "NET_START";
            }
            case 45: {
                return "SHOW_FISH_SPOTS";
            }
            default: {
                return "Unknown (" + command + ")";
            }
        }
    }
    
    private static byte getRodType(final Item rod, @Nullable final Item reel, @Nullable final Item line) {
        if (rod.getTemplateId() == 1344) {
            return FishingEnums.RodType.FISHING_POLE.getTypeId();
        }
        if (rod.getTemplateId() != 1346 || reel == null) {
            return -1;
        }
        if (line == null) {
            switch (reel.getTemplateId()) {
                case 1372: {
                    return FishingEnums.RodType.FISHING_ROD_BASIC.getTypeId();
                }
                case 1373: {
                    return FishingEnums.RodType.FISHING_ROD_FINE.getTypeId();
                }
                case 1374: {
                    return FishingEnums.RodType.FISHING_ROD_DEEP_WATER.getTypeId();
                }
                case 1375: {
                    return FishingEnums.RodType.FISHING_ROD_DEEP_SEA.getTypeId();
                }
                default: {
                    return -1;
                }
            }
        }
        else {
            switch (reel.getTemplateId()) {
                case 1372: {
                    return FishingEnums.RodType.FISHING_ROD_BASIC_WITH_LINE.getTypeId();
                }
                case 1373: {
                    return FishingEnums.RodType.FISHING_ROD_FINE_WITH_LINE.getTypeId();
                }
                case 1374: {
                    return FishingEnums.RodType.FISHING_ROD_DEEP_WATER_WITH_LINE.getTypeId();
                }
                case 1375: {
                    return FishingEnums.RodType.FISHING_ROD_DEEP_SEA_WITH_LINE.getTypeId();
                }
                default: {
                    return -1;
                }
            }
        }
    }
    
    private static float[] getMinMaxRadius(final Item rod, final Item line) {
        final float min = (rod.getTemplateId() == 1344) ? 2.0f : 4.0f;
        final float linelength = getSingleLineLength(line);
        final float max = Math.min((linelength - min) / 2.0f, 8.0f) + min;
        return new float[] { min, max };
    }
    
    public static int getLineLength(final Item line) {
        final int lineTemplateWeight = line.getTemplate().getWeightGrams();
        final int lineWeight = line.getWeightGrams();
        final float comb = lineWeight / lineTemplateWeight;
        final int slen = getSingleLineLength(line);
        final int tlen = (int)(comb * slen);
        return tlen;
    }
    
    public static int getSingleLineLength(final Item line) {
        switch (line.getTemplateId()) {
            case 1347: {
                return 10;
            }
            case 1348: {
                return 12;
            }
            case 1349: {
                return 14;
            }
            case 1350: {
                return 16;
            }
            case 1351: {
                return 18;
            }
            default: {
                return 10;
            }
        }
    }
    
    public static Color getBgColour(final int season) {
        final int[] bgRed = { 181, 34, 183, 192 };
        final int[] bgGreen = { 230, 177, 141, 192 };
        final int[] bgBlue = { 29, 0, 76, 192 };
        return new Color(bgRed[season], bgGreen[season], bgBlue[season]);
    }
    
    public static Point getSeasonOffset(final int season) {
        final int[] offsetXs = { 0, 128, 0, 128 };
        final int[] offsetYs = { 0, 0, 128, 128 };
        return new Point(offsetXs[season], offsetYs[season]);
    }
    
    public static Color getFishColour(final int templateId) {
        int red = 0;
        int green = 0;
        int blue = 0;
        switch (templateId) {
            case 1336: {
                red = 255;
            }
            case 569: {
                red = 255;
                green = 255;
                break;
            }
            case 570: {
                red = 255;
                green = 127;
                break;
            }
            case 574: {
                green = 255;
                break;
            }
            case 573: {
                green = 255;
                blue = 255;
                break;
            }
            case 571: {
                red = 127;
                blue = 255;
                break;
            }
            case 575: {
                blue = 255;
                break;
            }
        }
        return new Color(red, green, blue);
    }
    
    static {
        logger = Logger.getLogger(MethodsFishing.class.getName());
    }
    
    public static class FishRow
    {
        private final byte fishTypeId;
        private final String name;
        private float chance;
        
        public FishRow(final int fishTypeId, final String name) {
            this.chance = 0.0f;
            this.fishTypeId = (byte)fishTypeId;
            this.name = name;
        }
        
        public byte getFishTypeId() {
            return this.fishTypeId;
        }
        
        public String getName() {
            return this.name;
        }
        
        public float getChance() {
            return this.chance;
        }
        
        public void setChance(final float chance) {
            this.chance = chance;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("FishData [");
            buf.append("Name: ").append(this.name);
            buf.append(", Id: ").append(this.fishTypeId);
            buf.append(", Chance: ").append(this.chance);
            buf.append("]");
            return buf.toString();
        }
    }
}
