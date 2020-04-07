// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.logging.Level;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Zones;
import java.util.Random;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Point;
import java.util.ArrayList;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.MiscConstants;

public final class Fish implements MiscConstants
{
    static final short POND = 20000;
    static final short LAKE = 7500;
    public static final short DEEP_SEA = 5000;
    public static final short DEEP_SEA_DEPTH = -250;
    private static MeshIO mesh;
    private static final Logger logger;
    
    public static short getFishFor(final Creature performer, final short waterSize, final short effect, final int tilex, final int tiley, final Item rod) {
        final int rand = Server.rand.nextInt(80 + ((rod != null) ? (rod.getRarity() * 10) : 0)) + effect / 2;
        int toReturn = 164;
        if (waterSize == 20000) {
            if (rand < 60) {
                toReturn = 162;
            }
            else if (rand < 90) {
                toReturn = 163;
            }
        }
        else if (waterSize == 7500) {
            if (rand < 40) {
                toReturn = 162;
            }
            else if (rand < 60) {
                toReturn = 163;
            }
            else if (rand < 70) {
                toReturn = 165;
            }
            else if (rand < 80) {
                toReturn = 157;
            }
            else if (rand < 90) {
                toReturn = 160;
            }
        }
        else if (waterSize == 5000) {
            toReturn = 161;
            if (rand < 20) {
                toReturn = 162;
            }
            else if (rand < 40) {
                toReturn = 159;
            }
            else if (rand < 55) {
                toReturn = 163;
            }
            else if (rand < 60) {
                toReturn = 165;
            }
            else if (rand < 65) {
                toReturn = 157;
            }
            else if (rand < 75) {
                toReturn = 160;
            }
            else if (rand < 80) {
                toReturn = 164;
            }
            else if (rand < 90) {
                toReturn = 158;
            }
            else {
                final short fish = checkForSpecialFish(performer, tilex, tiley);
                if (fish != 0) {
                    return fish;
                }
            }
        }
        return (short)toReturn;
    }
    
    public static short checkForSpecialFish(final Creature performer, final int tilex, final int tiley) {
        final ArrayList<Integer> canCatch = getSpecialFishList(performer, tilex, tiley);
        if (canCatch.isEmpty()) {
            return 0;
        }
        if (canCatch.size() == 1) {
            return (short)(Object)canCatch.get(0);
        }
        final int randomfish = Server.rand.nextInt(canCatch.size());
        return (short)(Object)canCatch.get(randomfish);
    }
    
    public static ArrayList<Integer> getSpecialFishList(final Creature performer, final int tilex, final int tiley) {
        final ArrayList<Integer> canCatch = new ArrayList<Integer>();
        final Point[] rareSpots;
        final Point[] points = rareSpots = getRareSpots(tilex, tiley);
        for (final Point point : rareSpots) {
            if (performer.isWithinTileDistanceTo(point.getX(), point.getY(), 0, 5)) {
                canCatch.add(point.getH());
            }
        }
        return canCatch;
    }
    
    public static Point[] getRareSpots(final int tilex, final int tiley) {
        final ArrayList<Point> rareSpots = new ArrayList<Point>();
        final int season = WurmCalendar.getSeasonNumber();
        final int zoneX = tilex / 100 * 100;
        final int zoneY = tiley / 100 * 100;
        Point tp = getFishSpot(zoneX, zoneY, 572, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 569, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 570, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 574, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 573, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 571, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        tp = getFishSpot(zoneX, zoneY, 575, season);
        if (isFishSpotValid(tp)) {
            rareSpots.add(tp);
        }
        return rareSpots.toArray(new Point[rareSpots.size()]);
    }
    
    public static Point getFishSpot(final int zoneX, final int zoneY, final int fishtype, final int season) {
        final Random r = new Random(fishtype + Servers.localServer.id * 10 + season * 25);
        final int rx = zoneX + r.nextInt(100);
        final int ry = zoneY + r.nextInt(100);
        return new Point(rx, ry, fishtype);
    }
    
    private static boolean isFishSpotValid(final Point point) {
        final int tile = Server.surfaceMesh.getTile(Zones.safeTileX(point.getX()), Zones.safeTileY(point.getY()));
        return Tiles.decodeHeight(tile) < -250;
    }
    
    static float getDifficultyFor(final int fish) {
        float toReturn = 0.0f;
        if (fish == 158) {
            toReturn = 50.0f;
        }
        else if (fish == 164) {
            toReturn = 60.0f;
        }
        else if (fish == 160) {
            toReturn = 40.0f;
        }
        else if (fish == 159) {
            toReturn = 10.0f;
        }
        else if (fish == 163) {
            toReturn = 8.0f;
        }
        else if (fish == 157) {
            toReturn = 50.0f;
        }
        else if (fish == 162) {
            toReturn = 2.0f;
        }
        else if (fish == 161) {
            toReturn = 80.0f;
        }
        else if (fish == 165) {
            toReturn = 30.0f;
        }
        else if (fish == 569) {
            toReturn = 90.0f;
        }
        else if (fish == 570) {
            toReturn = 86.0f;
        }
        else if (fish == 571) {
            toReturn = 88.0f;
        }
        else if (fish == 572) {
            toReturn = 84.0f;
        }
        else if (fish == 573) {
            toReturn = 89.0f;
        }
        else if (fish == 574) {
            toReturn = 85.0f;
        }
        else if (fish == 575) {
            toReturn = 87.0f;
        }
        return toReturn;
    }
    
    static float getDamModFor(final Item f) {
        final float toreturn = getDamModFor(f.getTemplateId()) * Math.max(0.1f, f.getWeightGrams() / 3000);
        return toreturn;
    }
    
    static int getDamModFor(final int type) {
        int mod = 1;
        if (type == 158) {
            mod = 2;
        }
        else if (type == 164) {
            mod = 3;
        }
        else if (type == 160) {
            mod = 4;
        }
        else if (type == 159) {
            mod = 1;
        }
        else if (type == 163) {
            mod = 1;
        }
        else if (type == 157) {
            mod = 4;
        }
        else if (type == 162) {
            mod = 1;
        }
        else if (type == 161) {
            mod = 5;
        }
        else if (type == 165) {
            mod = 2;
        }
        else if (type == 569) {
            mod = 2;
        }
        else if (type == 570) {
            mod = 2;
        }
        else if (type == 571) {
            mod = 1;
        }
        else if (type == 572) {
            mod = 1;
        }
        else if (type == 573) {
            mod = 1;
        }
        else if (type == 574) {
            mod = 1;
        }
        else if (type == 575) {
            mod = 2;
        }
        return mod;
    }
    
    public static double getMeatFor(final Item fish) {
        final double ql = fish.getCurrentQualityLevel() / 100.0f;
        final double weight = fish.getTemplate().getWeightGrams() * ql;
        return weight;
    }
    
    static boolean fish(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final Action act) {
        boolean done = false;
        if (!Terraforming.isTileUnderWater(tile, tilex, tiley, performer.isOnSurface())) {
            performer.getCommunicator().sendNormalServerMessage("The water is too shallow to fish.");
            done = true;
        }
        if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("A huge shadow moves beneath the waves, and you reel in the line in panic.");
            done = true;
        }
        if (source.getTemplateId() == 23 || source.getTemplateId() == 780) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " will do a poor job of catching fish.");
            done = true;
        }
        if (source.getTemplateId() != 94 && source.getTemplateId() != 152 && source.getTemplateId() != 176) {
            return true;
        }
        if (!done) {
            done = false;
            final int time = 1800;
            if (counter == 1.0f) {
                final int perfTileX = performer.getTileX();
                final int perfTileY = performer.getTileY();
                Fish.mesh = Server.surfaceMesh;
                if (!performer.isOnSurface()) {
                    Fish.mesh = Server.caveMesh;
                }
                final int perfTile = Fish.mesh.getTile(perfTileX, perfTileY);
                final short ph = Tiles.decodeHeight(perfTile);
                if (performer.getBridgeId() == -10L && ph < -10 && performer.getVehicle() == -10L) {
                    done = true;
                    performer.getCommunicator().sendNormalServerMessage("You can't swim and fish at the same time.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You throw out the line and start fishing.");
                    Server.getInstance().broadCastAction(performer.getName() + " throws out a line and starts fishing.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[160].getVerbString(), true, 1800);
                    final short timeToBite = (short)(100 + Server.rand.nextInt(1000));
                    try {
                        performer.getCurrentAction().setTimeLeft(timeToBite);
                    }
                    catch (NoSuchActionException nsa) {
                        Fish.logger.log(Level.INFO, "Strange, this action doesn't exist.");
                    }
                }
            }
            else {
                int number = 10;
                try {
                    number = performer.getCurrentAction().getTimeLeft();
                }
                catch (NoSuchActionException nsa2) {
                    Fish.logger.log(Level.INFO, "Strange, this action doesn't exist.");
                }
                if (number < 2000) {
                    if (act.currentSecond() * 10 > number || source.getTemplateId() == 176) {
                        short waterFound = 0;
                        short waterType = 20000;
                        short maxDepth = 0;
                        if (performer.isOnSurface()) {
                            int x = 0;
                            int y = 1;
                            while (y++ < 10) {
                                if (tiley - y > 0) {
                                    final int t = Server.surfaceMesh.getTile(tilex, tiley - y);
                                    final short h = Tiles.decodeHeight(t);
                                    if (h > -3) {
                                        continue;
                                    }
                                    if (h < maxDepth) {
                                        maxDepth = h;
                                    }
                                    ++waterFound;
                                }
                                else {
                                    ++waterFound;
                                }
                            }
                            y = 1;
                            while (y++ < 10) {
                                if (tiley + y < Zones.worldTileSizeY) {
                                    final int t = Server.surfaceMesh.getTile(tilex, tiley + y);
                                    final short h = Tiles.decodeHeight(t);
                                    if (h > -3) {
                                        continue;
                                    }
                                    if (h < maxDepth) {
                                        maxDepth = h;
                                    }
                                    ++waterFound;
                                }
                                else {
                                    ++waterFound;
                                }
                            }
                            y = 0;
                            x = 1;
                            while (x++ < 10) {
                                if (tilex + x > Zones.worldTileSizeX) {
                                    final int t = Server.surfaceMesh.getTile(tilex + x, tiley);
                                    final short h = Tiles.decodeHeight(t);
                                    if (h > -3) {
                                        continue;
                                    }
                                    if (h < maxDepth) {
                                        maxDepth = h;
                                    }
                                    ++waterFound;
                                }
                                else {
                                    ++waterFound;
                                }
                            }
                            x = 1;
                            while (x++ < 10) {
                                if (tilex - x > 0) {
                                    final int t = Server.surfaceMesh.getTile(tilex - x, tiley);
                                    final short h = Tiles.decodeHeight(t);
                                    if (h > -3) {
                                        continue;
                                    }
                                    if (h < maxDepth) {
                                        maxDepth = h;
                                    }
                                    ++waterFound;
                                }
                                else {
                                    ++waterFound;
                                }
                            }
                        }
                        if (waterFound > 1) {
                            if (waterFound < 9) {
                                waterType = 20000;
                            }
                            else if (waterFound > 20 && maxDepth < -250) {
                                waterType = 5000;
                            }
                            else {
                                waterType = 7500;
                            }
                        }
                        try {
                            performer.getCurrentAction().setTimeLeft(waterType);
                        }
                        catch (NoSuchActionException nsa3) {
                            Fish.logger.log(Level.INFO, "Strange, this action doesn't exist.");
                        }
                    }
                    else if (act.justTickedSecond() && act.currentSecond() % 2 == 0) {
                        if (performer.getInventory().getNumItemsNotCoins() >= 100) {
                            performer.getCommunicator().sendNormalServerMessage("You wouldn't be able to carry the fish. Drop something first.");
                            return true;
                        }
                        if (!performer.canCarry(1000)) {
                            performer.getCommunicator().sendNormalServerMessage("You are too heavily loaded. Drop something first.");
                            return true;
                        }
                        final Skills skills = performer.getSkills();
                        Skill fishing;
                        try {
                            fishing = skills.getSkill(10033);
                        }
                        catch (NoSuchSkillException nss) {
                            fishing = skills.learn(10033, 1.0f);
                        }
                        fishing.skillCheck(50.0, source, 0.0, false, 2.0f);
                    }
                }
                else if (act.justTickedSecond() && act.currentSecond() % 2 == 0) {
                    if (performer.getInventory().getNumItemsNotCoins() >= 100) {
                        performer.getCommunicator().sendNormalServerMessage("You wouldn't be able to carry the fish. Drop something first.");
                        return true;
                    }
                    final int waterType2 = number;
                    short fishOnHook = 0;
                    Item fish = null;
                    final Skills skills2 = performer.getSkills();
                    Skill fishing2 = null;
                    try {
                        fishing2 = skills2.getSkill(10033);
                    }
                    catch (NoSuchSkillException nss2) {
                        fishing2 = skills2.learn(10033, 1.0f);
                    }
                    double bonus = 0.0;
                    if (source.getTemplateId() == 152) {
                        bonus -= 20.0;
                    }
                    if (waterType2 != 0) {
                        if (waterType2 >= 20000) {
                            fishOnHook = (short)(waterType2 - 20000);
                        }
                        else if (waterType2 >= 7500) {
                            fishOnHook = (short)(waterType2 - 7500);
                        }
                        else if (waterType2 >= 5000) {
                            fishOnHook = (short)(waterType2 - 5000);
                        }
                        if (fishOnHook == 0) {
                            final double result = fishing2.skillCheck(waterType2 / 250.0f, source, bonus, false, 1.0f);
                            if (result > 0.0) {
                                final int perfTileX2 = performer.getTileX();
                                final int perfTileY2 = performer.getTileY();
                                fishOnHook = getFishFor(performer, (short)waterType2, (short)result, perfTileX2, perfTileY2, source);
                                try {
                                    if (act.getRarity() != 0) {
                                        performer.playPersonalSound("sound.fx.drumroll");
                                    }
                                    final double fishresult = 10.0 + 0.9 * Math.max(Server.rand.nextFloat() * 10.0f, fishing2.skillCheck(getDifficultyFor(fishOnHook), source, bonus, true, 1.0f));
                                    fish = ItemFactory.createItem(fishOnHook, (float)fishresult, (byte)2, act.getRarity(), null);
                                    final int weight = (int)getMeatFor(fish);
                                    if (weight <= 50) {
                                        performer.getCommunicator().sendNormalServerMessage("You almost catch something but it escapes.");
                                        Server.getInstance().broadCastAction(performer.getName() + "'s line twitches but nothing is on it.", performer, 5);
                                        done = true;
                                        Items.decay(fish.getWurmId(), fish.getDbStrings());
                                    }
                                    else {
                                        fish.setSizes(weight);
                                        fish.setWeight(weight, false);
                                        performer.getCommunicator().sendNormalServerMessage("Something bites.");
                                        Server.getInstance().broadCastAction("Something bites on " + performer.getName() + "'s hook.", performer, 5);
                                        performer.getCurrentAction().setDestroyedItem(fish);
                                    }
                                }
                                catch (NoSuchTemplateException nse) {
                                    Fish.logger.log(Level.INFO, "Failed to create item with id " + fishOnHook + " for performer " + performer.getName(), nse);
                                }
                                catch (FailedException fe) {
                                    Fish.logger.log(Level.INFO, "Failed to create item with id " + fishOnHook + " for performer " + performer.getName(), fe);
                                }
                                catch (NoSuchActionException nsa4) {
                                    Fish.logger.log(Level.INFO, "no action found for performer");
                                }
                            }
                            try {
                                performer.getCurrentAction().setTimeLeft((short)(waterType2 + fishOnHook));
                            }
                            catch (NoSuchActionException nsa5) {
                                Fish.logger.log(Level.INFO, "Strange, this action doesn't exist.");
                            }
                        }
                        else {
                            try {
                                fish = performer.getCurrentAction().getDestroyedItem();
                            }
                            catch (NoSuchActionException nsa6) {
                                Fish.logger.log(Level.INFO, "no action found, should not happen");
                            }
                            final float difficulty = getDifficultyFor(fishOnHook);
                            double result2 = fishing2.skillCheck(difficulty, source, bonus, false, 1.0f);
                            if ((act.currentSecond() % 5 == 0 || source.getTemplateId() == 176) && act.justTickedSecond()) {
                                if (result2 > 0.0) {
                                    done = true;
                                    try {
                                        performer.getCurrentAction().setDestroyedItem(null);
                                    }
                                    catch (NoSuchActionException nsa7) {
                                        Fish.logger.log(Level.INFO, "no action found, should not happen");
                                    }
                                    if (source.getTemplateId() != 176) {
                                        source.setDamage(source.getDamage() + Math.min((100.0f - source.getCurrentQualityLevel()) / 50.0f, Math.max(0.1f, source.getDamageModifier(false, true) / (10.0f * Math.max(10.0f, source.getQualityLevel()))) * getDamModFor(fish)));
                                    }
                                    performer.getInventory().insertItem(fish, true);
                                    performer.achievement(126);
                                    final int weight2 = (int)getMeatFor(fish);
                                    if (weight2 > 3000) {
                                        performer.achievement(542);
                                    }
                                    if (weight2 > 10000) {
                                        performer.achievement(585);
                                    }
                                    if (weight2 > 175000) {
                                        performer.achievement(297);
                                    }
                                    performer.getCommunicator().sendNormalServerMessage("You catch " + fish.getNameWithGenus() + ".");
                                    Server.getInstance().broadCastAction(performer.getName() + " catches " + fish.getNameWithGenus() + ".", performer, 5);
                                    fishOnHook = 0;
                                    if (fish.getTemplateId() == 570 || fish.getTemplateId() == 571) {
                                        result2 = fishing2.skillCheck(difficulty, source, bonus, false, 1.0f);
                                        if (result2 < 0.0 && performer.getPower() <= 1) {
                                            try {
                                                performer.getCommunicator().sendNormalServerMessage("The " + fish.getName() + " bites you!");
                                                Server.getInstance().broadCastAction(performer.getName() + " is bit by the " + fish.getName() + "!", performer, 5);
                                                performer.addWoundOfType(null, (byte)3, 0, true, 1.0f, false, Math.max(3000, fish.getWeightGrams() / 10), 0.0f, 3.0f, false, false);
                                            }
                                            catch (Exception ex) {
                                                Fish.logger.log(Level.WARNING, performer.getName() + ": " + ex.getMessage(), ex);
                                            }
                                        }
                                    }
                                }
                                else if (fish.getWeightGrams() > 500) {
                                    if (result2 < -80.0 && !source.isWand() && Server.rand.nextInt(Math.max(1, 30 - (int)fishing2.getKnowledge(0.0))) == 0) {
                                        done = true;
                                        performer.getCommunicator().sendNormalServerMessage("The line snaps, and the fish escapes!");
                                        Server.getInstance().broadCastAction(performer.getName() + "s line snaps and the fish escapes.", performer, 5);
                                        fishOnHook = 0;
                                        source.setTemplateId(780);
                                        source.setWeight(1000, false);
                                    }
                                    else if (result2 < -60.0) {
                                        performer.getCommunicator().sendNormalServerMessage("The fish pulls very hard on the line, making the rod creak.");
                                        final float damMod = getDamModFor(fish);
                                        source.setDamage(source.getDamage() + Math.max(0.1f, Math.min((100.0f - source.getCurrentQualityLevel()) / 100.0f, source.getDamageModifier() / (10.0f * Math.max(10.0f, source.getQualityLevel()))) * damMod));
                                    }
                                }
                                else {
                                    final int mess = Server.rand.nextInt(4);
                                    if (mess == 0) {
                                        performer.getCommunicator().sendNormalServerMessage("The fish pulls hard on the line, bending the rod.");
                                        Server.getInstance().broadCastAction("The fish pulls hard on " + performer.getName() + "'s rod and bends it.", performer, 5);
                                    }
                                    else if (mess == 1) {
                                        performer.getCommunicator().sendNormalServerMessage("The fish seems tired and you pull in the line a bit.");
                                        Server.getInstance().broadCastAction(performer.getName() + " pulls in the line a bit.", performer, 5);
                                    }
                                    else if (mess == 2) {
                                        performer.getCommunicator().sendNormalServerMessage("The water splashes as the fish fights in the water.");
                                        Server.getInstance().broadCastAction("The water splashes as " + performer.getName() + "'s fish fights in the water", performer, 5);
                                    }
                                }
                            }
                        }
                    }
                    else if (act.currentSecond() > 1 && act.currentSecond() % 60 == 0) {
                        fishing2.skillCheck(50.0, source, 0.0, false, 1.0f);
                    }
                    if (act.currentSecond() > 180) {
                        done = true;
                        if (fishOnHook == 0) {
                            performer.getCommunicator().sendNormalServerMessage("You pull in the line and stop fishing.");
                            Server.getInstance().broadCastAction(performer.getName() + " pulls in the line and stops fishing.", performer, 5);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The fish breaks loose and swims away.");
                            Server.getInstance().broadCastAction(performer.getName() + "'s fish breaks loose and swims away.", performer, 5);
                        }
                    }
                }
            }
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(Fish.class.getName());
    }
}
