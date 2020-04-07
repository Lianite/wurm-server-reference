// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.Players;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import java.util.Iterator;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.Features;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.MiscConstants;

final class Flattening implements MiscConstants, SoundNames
{
    private static final Logger logger;
    private static int[][] flattenTiles;
    private static int[][] rockTiles;
    private static boolean[][] immutableTiles;
    private static boolean[][] changedTiles;
    private static int flattenSkill;
    private static double skill;
    private static int flattenRock;
    private static int flattenDone;
    private static int flattenImmutable;
    private static int flattenSlope;
    private static int needsDirt;
    private static final float DIGGING_SKILL_MULT = 3.0f;
    private static final float FLATTENING_MAX_DEPTH = -7.0f;
    private static short newFHeight;
    private static boolean raising;
    
    private static final void fillFlattenTiles(final Creature performer, final int tilex, final int tiley) {
        for (int x = -1; x < 3; ++x) {
            for (int y = -1; y < 3; ++y) {
                try {
                    Flattening.flattenTiles[x + 1][y + 1] = Server.surfaceMesh.getTile(tilex + x, tiley + y);
                    Flattening.rockTiles[x + 1][y + 1] = Tiles.decodeHeight(Server.rockMesh.getTile(tilex + x, tiley + y));
                    Flattening.immutableTiles[x + 1][y + 1] = isTileUntouchable(performer, tilex + x, tiley + y);
                }
                catch (Exception ex) {
                    Flattening.immutableTiles[x + 1][y + 1] = true;
                    Flattening.flattenTiles[x + 1][y + 1] = -100;
                }
            }
        }
    }
    
    private static final boolean isTileUntouchable(final Creature performer, final int tilex, final int tiley) {
        for (int x = 0; x >= -1; --x) {
            for (int y = 0; y >= -1; --y) {
                if (Zones.protectedTiles[tilex + x][tiley + y]) {
                    return true;
                }
                if (Zones.isWithinDuelRing(tilex, tiley, true) != null) {
                    return true;
                }
                if (Features.Feature.BLOCK_HOTA.isEnabled()) {
                    for (final FocusZone fz : FocusZone.getZonesAt(tilex, tiley)) {
                        if ((fz.isBattleCamp() || fz.isPvPHota() || fz.isNoBuild()) && fz.covers(tilex, tiley)) {
                            return true;
                        }
                    }
                }
                final VolaTile vtile = Zones.getOrCreateTile(tilex + x, tiley + y, performer.isOnSurface());
                if (vtile.getStructure() != null && performer.getPower() < 5) {
                    return true;
                }
                final Village village = vtile.getVillage();
                if (village != null) {
                    final int tile = Server.surfaceMesh.getTile(tilex + x, tiley + y);
                    if (!village.isActionAllowed((short)144, performer, false, tile, 0)) {
                        return true;
                    }
                }
                final Fence[] fences = vtile.getFencesForLevel(0);
                if (fences.length > 0) {
                    if (x == 0 && y == 0) {
                        return true;
                    }
                    if (x == -1 && y == 0) {
                        for (final Fence f : fences) {
                            if (f.isHorizontal()) {
                                return true;
                            }
                        }
                    }
                    else if (y == -1 && x == 0) {
                        for (final Fence f : fences) {
                            if (!f.isHorizontal()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    static final boolean flatten(final Creature performer, final Item source, final int tile, final int tilex, final int tiley, final float counter, final Action act) {
        return flatten(-10L, performer, source, tile, tilex, tiley, 2, 2, 4, counter, act);
    }
    
    static final boolean flattenTileBorder(final long borderId, final Creature performer, final Item source, final int tilex, final int tiley, final Tiles.TileBorderDirection dir, final float counter, final Action act) {
        final int tile = Zones.getTileIntForTile(tilex, tiley, performer.isOnSurface() ? 0 : 1);
        if (dir == Tiles.TileBorderDirection.DIR_DOWN) {
            return flatten(borderId, performer, source, tile, tilex, tiley, 1, 2, 2, counter, act);
        }
        return flatten(borderId, performer, source, tile, tilex, tiley, 2, 1, 2, counter, act);
    }
    
    private static final boolean flatten(final long borderId, final Creature performer, final Item source, final int tile, final int tilex, final int tiley, final int endX, final int endY, final int numbCorners, final float counter, final Action act) {
        boolean done = false;
        final String verb = act.getActionEntry().getVerbString().toLowerCase();
        final String action = act.getActionEntry().getActionString().toLowerCase();
        if (tilex - 2 < 0 || tilex + 2 > 1 << Constants.meshSize || tiley - 2 < 0 || tiley + 2 > 1 << Constants.meshSize) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to " + action + ".");
            done = true;
        }
        else {
            final VolaTile vt = performer.getCurrentTile();
            if (vt != null && vt.getStructure() != null && performer.getPower() < 5) {
                performer.getCommunicator().sendNormalServerMessage("You can not " + action + " from the inside.");
                return true;
            }
            final byte type = Tiles.decodeType(tile);
            if (Terraforming.isRockTile(type)) {
                performer.getCommunicator().sendNormalServerMessage("You can not dig in the solid rock.");
                return true;
            }
            if (!performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("You can not " + action + " from the inside.");
                return true;
            }
            final boolean isTooDeep = isTileTooDeep(tilex, tiley, endX, endY, numbCorners);
            if (act.getNumber() == 532 && !Terraforming.isFlat(performer.getTileX(), performer.getTileY(), performer.isOnSurface(), 0)) {
                performer.getCommunicator().sendNormalServerMessage("You need to be " + (isTooDeep ? "above" : "standing on") + " flat ground to be able to level.");
                return true;
            }
            if (!Terraforming.isNonDiggableTile(type)) {
                final boolean insta = source.isWand() && performer.getPower() >= 2;
                if ((act.currentSecond() % 10 == 0 && act.getNumber() == 150) || (act.currentSecond() % 5 == 0 && act.getNumber() != 150) || insta || counter == 1.0f) {
                    Flattening.flattenSkill = 0;
                    Flattening.flattenImmutable = 0;
                    Flattening.flattenRock = 0;
                    Flattening.flattenDone = 0;
                    Flattening.flattenSlope = 0;
                    Flattening.needsDirt = 0;
                    Flattening.skill = 0.0;
                    fillFlattenTiles(performer, tilex, tiley);
                    short maxHeight = -32768;
                    short maxHeight2 = -32768;
                    short minHeight = 32767;
                    for (int x = 1; x <= endX; ++x) {
                        for (int y = 1; y <= endY; ++y) {
                            final short ht = Tiles.decodeHeight(Flattening.flattenTiles[x][y]);
                            if (ht > maxHeight) {
                                maxHeight2 = maxHeight;
                                maxHeight = ht;
                            }
                            else if (ht > maxHeight2) {
                                maxHeight2 = ht;
                            }
                            if (ht < minHeight) {
                                minHeight = ht;
                            }
                            if (performer.getStrengthSkill() < 21.0 && Terraforming.isRoad(Tiles.decodeType(Flattening.flattenTiles[x][y]))) {
                                performer.getCommunicator().sendNormalServerMessage("You need to be stronger in order to " + action + " near roads.");
                                return true;
                            }
                        }
                    }
                    short lAverageHeight = calcAverageHeight(performer, tile, tilex, tiley, endX, endY, numbCorners, act);
                    if (isFlat(performer, lAverageHeight, endX, endY, numbCorners, act)) {
                        performer.getCommunicator().sendNormalServerMessage("Already flat!");
                        checkChangedTiles(tile, endX, endY, performer);
                        return true;
                    }
                    int ddone = 0;
                    final Skills skills = performer.getSkills();
                    Skill digging = null;
                    Skill shovel = null;
                    if (!insta) {
                        if ((!isTooDeep && !source.isDiggingtool()) || (isTooDeep && !source.isDredgingTool())) {
                            performer.getCommunicator().sendNormalServerMessage("You can't " + action + " with that.");
                            return true;
                        }
                        try {
                            digging = skills.getSkill(1009);
                        }
                        catch (Exception ex) {
                            digging = skills.learn(1009, 1.0f);
                        }
                        Label_0857: {
                            try {
                                shovel = skills.getSkill(source.getPrimarySkill());
                            }
                            catch (Exception ex) {
                                try {
                                    shovel = skills.learn(source.getPrimarySkill(), 1.0f);
                                }
                                catch (NoSuchSkillException nse) {
                                    if (performer.getPower() > 0) {
                                        break Label_0857;
                                    }
                                    Flattening.logger.log(Level.WARNING, performer.getName() + " trying to " + action + " with an item with no primary skill: " + source.getName());
                                }
                            }
                        }
                        Flattening.skill = digging.getKnowledge(0.0);
                    }
                    else {
                        Flattening.skill = 99.0;
                    }
                    if ((type == Tiles.Tile.TILE_CLAY.id || type == Tiles.Tile.TILE_TAR.id || type == Tiles.Tile.TILE_PEAT.id) && Flattening.skill < 70.0) {
                        performer.getCommunicator().sendNormalServerMessage("You just can not work how to " + action + " here it seems.");
                        return true;
                    }
                    int tickTimes = 5;
                    if (act.getNumber() == 150) {
                        tickTimes = 10;
                    }
                    if (type == Tiles.Tile.TILE_CLAY.id || type == Tiles.Tile.TILE_TAR.id || type == Tiles.Tile.TILE_PEAT.id) {
                        tickTimes = 30;
                    }
                    if (counter == 1.0f && !insta) {
                        float t;
                        if (act.getNumber() == 532) {
                            final int difmax = Math.abs(maxHeight - lAverageHeight);
                            final int difmax2 = Math.abs(maxHeight2 - lAverageHeight);
                            final int difmin = Math.abs(minHeight - lAverageHeight);
                            if (minHeight < lAverageHeight) {
                                t = Math.max(difmin, difmax) * tickTimes * 10;
                            }
                            else {
                                t = (difmax + difmax2) * tickTimes * 10;
                            }
                        }
                        else if (act.getNumber() == 865) {
                            t = (maxHeight - minHeight) * tickTimes * 10;
                        }
                        else if (act.getNumber() == 533) {
                            t = (maxHeight - minHeight) / 4 * tickTimes * 10 + 50;
                        }
                        else {
                            t = (maxHeight - minHeight) / numbCorners * tickTimes * 10 + 100;
                        }
                        if (t > 65535.0f) {
                            t = 65535.0f;
                        }
                        final int time = (int)Math.max(50.0f, t);
                        String ctype = "ground";
                        String btype = "the ground";
                        if (act.getNumber() == 533 || act.getNumber() == 865) {
                            ctype = "tile border";
                            btype = "a tile border";
                        }
                        performer.getCommunicator().sendNormalServerMessage("You start to " + action + " the " + ctype + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to " + action + " " + btype + ".", performer, 5);
                        performer.sendActionControl(action, true, time);
                        source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                    }
                    if (act.currentSecond() % tickTimes == 0 || insta) {
                        if (Zones.protectedTiles[tilex][tiley]) {
                            performer.getCommunicator().sendNormalServerMessage("Your body goes limp and you find no strength to continue here. Weird.");
                            return true;
                        }
                        if (performer.getStatus().getStamina() < 5000) {
                            performer.getCommunicator().sendNormalServerMessage("You must rest.");
                            return true;
                        }
                        if (!insta) {
                            performer.getStatus().modifyStamina(-4000.0f);
                        }
                        String sstring = "sound.work.digging1";
                        final int snd = Server.rand.nextInt(3);
                        if (snd == 0) {
                            sstring = "sound.work.digging2";
                        }
                        else if (snd == 1) {
                            sstring = "sound.work.digging3";
                        }
                        SoundPlayer.playSound(sstring, performer, 0.0f);
                        resetChangedTiles();
                        if (act.getNumber() == 533 || act.getNumber() == 865) {
                            if (checkBorderCorners(performer, tilex, tiley, tilex, tiley, 1, 1, endX, endY, (int)Math.max(3.0, Flattening.skill * 3.0), lAverageHeight, act)) {
                                ++ddone;
                            }
                            if (checkBorderCorners(performer, tilex, tiley, tilex + endX - 1, tiley + endY - 1, endX, endY, 1, 1, (int)Math.max(3.0, Flattening.skill * 3.0), lAverageHeight, act)) {
                                ++ddone;
                            }
                        }
                        else {
                            for (int xx = 1; xx <= endX; ++xx) {
                                for (int yy = 1; yy <= endY; ++yy) {
                                    if (checkFlattenCorner(performer, tilex, tiley, tilex + xx - 1, tiley + yy - 1, xx, yy, (int)Math.max(3.0, Flattening.skill * 3.0), lAverageHeight, act)) {
                                        ++ddone;
                                    }
                                }
                            }
                        }
                        checkChangedTiles(tile, tilex, tiley, performer);
                        if (ddone + Flattening.flattenSkill + Flattening.flattenImmutable + Flattening.flattenRock + Flattening.flattenSlope >= numbCorners) {
                            if (Flattening.flattenSkill > 0) {
                                performer.getCommunicator().sendNormalServerMessage("Some slope is too steep for your skill level.");
                            }
                            if (Flattening.flattenImmutable > 0) {
                                performer.getCommunicator().sendNormalServerMessage("Some corners can't be modified.");
                            }
                            if (Flattening.flattenRock > 0) {
                                performer.getCommunicator().sendNormalServerMessage("You hit the rock in a corner.");
                            }
                            if (Flattening.flattenDone > 0) {
                                performer.getCommunicator().sendNormalServerMessage("You have already flattened a corner.");
                            }
                            if (Flattening.flattenSlope > 0) {
                                performer.getCommunicator().sendNormalServerMessage("The highway would become impassable.");
                            }
                            if (ddone == numbCorners) {
                                done = true;
                                if (Flattening.flattenSkill == 0 && Flattening.flattenImmutable == 0 && Flattening.flattenSlope == 0) {
                                    checkUseDirt(tilex, tiley, endX, endY, performer, source, (int)Math.max(10.0, Flattening.skill * 3.0), lAverageHeight, act, insta && source.getAuxData() == 1);
                                }
                                if (Flattening.needsDirt > 0) {
                                    performer.getCommunicator().sendNormalServerMessage("If you carried some dirt, it would be used to fill the " + Flattening.needsDirt + " corners that need it.");
                                }
                                lAverageHeight = calcAverageHeight(performer, tile, tilex, tiley, endX, endY, numbCorners, act);
                                if (!isFlat(performer, lAverageHeight, endX, endY, numbCorners, act)) {
                                    if (Flattening.needsDirt == 0 && (act.getNumber() == 532 || act.getNumber() == 865)) {
                                        done = false;
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage("You finish " + verb + ".");
                                    }
                                }
                                else {
                                    performer.achievement(514);
                                }
                            }
                            checkChangedTiles(tile, tilex, tiley, performer);
                            if ((act.getNumber() == 533 || act.getNumber() == 865) && performer.getVisionArea() != null && borderId != -10L) {
                                performer.getVisionArea().broadCastUpdateSelectBar(borderId);
                            }
                            if (done || Flattening.flattenSkill + Flattening.flattenImmutable + Flattening.flattenRock + Flattening.flattenSlope > 0) {
                                return true;
                            }
                        }
                        if (performer.getStatus().getStamina() < 5000) {
                            performer.getCommunicator().sendNormalServerMessage("You must rest.");
                            return true;
                        }
                        if (!insta) {
                            source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                            float skilltimes = 10.0f;
                            if (act.getNumber() == 533) {
                                skilltimes = 5.0f;
                            }
                            if (act.getNumber() == 865) {
                                skilltimes = 2.5f;
                            }
                            if (act.getNumber() == 532) {
                                skilltimes = 1.5f;
                            }
                            if (shovel != null) {
                                shovel.skillCheck(30.0, source, 0.0, false, skilltimes);
                            }
                            digging.skillCheck(30.0, source, 0.0, false, skilltimes);
                        }
                    }
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("You can't " + action + " that place, as " + source.getNameWithGenus() + " just won't do.");
            }
        }
        return done;
    }
    
    public static void checkChangedTiles(final int tile, final int tilex, final int tiley, final Creature performer) {
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                final int modx = x - 1;
                final int mody = y - 1;
                if (x < 3 && y < 3 && shouldBeRock(x, y)) {
                    Flattening.changedTiles[x][y] = true;
                    Flattening.flattenTiles[x][y] = Tiles.encode(Tiles.decodeHeight(Flattening.flattenTiles[x][y]), Tiles.Tile.TILE_ROCK.id, (byte)0);
                }
                if (Flattening.changedTiles[x][y]) {
                    Flattening.changedTiles[x][y] = false;
                    Server.surfaceMesh.setTile(tilex + modx, tiley + mody, Flattening.flattenTiles[x][y]);
                    Server.setBotanizable(tilex + modx, tiley + mody, false);
                    Server.setForagable(tilex + modx, tiley + mody, false);
                    Server.isDirtHeightLower(tilex + modx, tiley + mody, Tiles.decodeHeight(Flattening.flattenTiles[x][y]));
                    try {
                        final Zone toCheckForChange = Zones.getZone(tilex + modx, tiley + mody, performer.isOnSurface());
                        toCheckForChange.changeTile(tilex + modx, tiley + mody);
                    }
                    catch (NoSuchZoneException nsz) {
                        Flattening.logger.log(Level.INFO, "no such zone?: " + (tilex + modx) + ", " + (tiley + mody), nsz);
                    }
                    performer.getMovementScheme().touchFreeMoveCounter();
                    Players.getInstance().sendChangedTile(tilex + modx, tiley + mody, performer.isOnSurface(), true);
                }
                final int lNewTile = Server.surfaceMesh.getTile(tilex + modx, tiley + mody);
                final byte type = Tiles.decodeType(lNewTile);
                final Tiles.Tile theTile = Tiles.getTile(type);
                if (theTile.isTree()) {
                    final byte data = Tiles.decodeData(lNewTile);
                    Zones.reposWildHive(tilex + modx, tiley + mody, theTile, data);
                }
            }
        }
    }
    
    private static final void resetChangedTiles() {
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                Flattening.changedTiles[x][y] = false;
            }
        }
    }
    
    private static short calcAverageHeight(final Creature performer, final int tile, final int tilex, final int tiley, final int endX, final int endY, final int numbCorners, final Action act) {
        float lAverageHeight = 0.0f;
        if (act.getNumber() == 532) {
            final int mytile = Server.surfaceMesh.getTile(performer.getTileX(), performer.getTileY());
            lAverageHeight = Tiles.decodeHeight(mytile);
        }
        else if (act.getNumber() == 865) {
            float distX = performer.getPosX() - (tilex << 2);
            float distY = performer.getPosY() - (tiley << 2);
            final double dist = Math.sqrt(distX * distX + distY * distY);
            distX = performer.getPosX() - (tilex + endX - 1 << 2);
            distY = performer.getPosY() - (tiley + endY - 1 << 2);
            final double dist2 = Math.sqrt(distX * distX + distY * distY);
            if (dist < dist2) {
                lAverageHeight = Tiles.decodeHeight(Flattening.flattenTiles[1][1]);
            }
            else {
                lAverageHeight = Tiles.decodeHeight(Flattening.flattenTiles[endX][endY]);
            }
        }
        else {
            for (int x = 1; x <= endX; ++x) {
                for (int y = 1; y <= endY; ++y) {
                    lAverageHeight += Tiles.decodeHeight(Flattening.flattenTiles[x][y]);
                }
            }
            lAverageHeight = lAverageHeight / numbCorners + 1.0f / numbCorners;
        }
        return (short)lAverageHeight;
    }
    
    private static boolean isFlat(final Creature performer, final short requiredHeight, final int endX, final int endY, final int numbCorners, final Action act) {
        int ddone = 0;
        for (int x = 1; x <= endX; ++x) {
            for (int y = 1; y <= endY; ++y) {
                if (Tiles.decodeHeight(Flattening.flattenTiles[x][y]) == requiredHeight) {
                    ++ddone;
                }
            }
        }
        if (ddone == numbCorners) {
            String ctype = "ground";
            if (act.getNumber() == 533 || act.getNumber() == 865) {
                ctype = "tile border";
            }
            performer.getCommunicator().sendNormalServerMessage("The " + ctype + " is flat here.");
            return true;
        }
        return false;
    }
    
    private static final boolean checkFlattenCorner(final Creature performer, final int initx, final int inity, final int tilex, final int tiley, final int x, final int y, final int maxDiff, final int preferredHeight, final Action act) {
        boolean raise = false;
        if (Tiles.decodeHeight(Flattening.flattenTiles[x][y]) < preferredHeight) {
            raise = true;
        }
        else if (Tiles.decodeHeight(Flattening.flattenTiles[x][y]) == preferredHeight) {
            return true;
        }
        if (raise && !mayRaiseCorner(initx, inity, Flattening.flattenTiles[x][y], x, y, maxDiff)) {
            return true;
        }
        if (!raise && !mayLowerCorner(initx, inity, Flattening.flattenTiles[x][y], Flattening.rockTiles[x][y], x, y, maxDiff)) {
            return true;
        }
        if (x == 1 && y == 1) {
            if (!changeCorner(performer, tilex, tiley, x, y, x + 1, y, maxDiff, preferredHeight, raise, act)) {
                return isCornerDone(x, y, preferredHeight);
            }
            if (changeCorner(performer, tilex, tiley, x, y, x, y + 1, maxDiff, preferredHeight, raise, act)) {
                return changeCorner(performer, tilex, tiley, x, y, x + 1, y + 1, maxDiff, preferredHeight, raise, act) || isCornerDone(x, y, preferredHeight);
            }
            return isCornerDone(x, y, preferredHeight);
        }
        else if (x == 2 && y == 1) {
            if (!changeCorner(performer, tilex, tiley, x, y, x - 1, y, maxDiff, preferredHeight, raise, act)) {
                return isCornerDone(x, y, preferredHeight);
            }
            if (changeCorner(performer, tilex, tiley, x, y, x, y + 1, maxDiff, preferredHeight, raise, act)) {
                return changeCorner(performer, tilex, tiley, x, y, x - 1, y + 1, maxDiff, preferredHeight, raise, act) || isCornerDone(x, y, preferredHeight);
            }
            return isCornerDone(x, y, preferredHeight);
        }
        else if (x == 1 && y == 2) {
            if (!changeCorner(performer, tilex, tiley, x, y, x + 1, y, maxDiff, preferredHeight, raise, act)) {
                return isCornerDone(x, y, preferredHeight);
            }
            if (changeCorner(performer, tilex, tiley, x, y, x, y - 1, maxDiff, preferredHeight, raise, act)) {
                return changeCorner(performer, tilex, tiley, x, y, x + 1, y - 1, maxDiff, preferredHeight, raise, act) || isCornerDone(x, y, preferredHeight);
            }
            return isCornerDone(x, y, preferredHeight);
        }
        else {
            if (x != 2 || y != 2) {
                return true;
            }
            if (!changeCorner(performer, tilex, tiley, x, y, x - 1, y, maxDiff, preferredHeight, raise, act)) {
                return isCornerDone(x, y, preferredHeight);
            }
            if (changeCorner(performer, tilex, tiley, x, y, x, y - 1, maxDiff, preferredHeight, raise, act)) {
                return changeCorner(performer, tilex, tiley, x, y, x - 1, y - 1, maxDiff, preferredHeight, raise, act) || isCornerDone(x, y, preferredHeight);
            }
            return isCornerDone(x, y, preferredHeight);
        }
    }
    
    private static final boolean checkBorderCorners(final Creature performer, final int initx, final int inity, final int tilex, final int tiley, final int x1, final int y1, final int x2, final int y2, final int maxDiff, final int preferredHeight, final Action act) {
        boolean raise = false;
        if (Tiles.decodeHeight(Flattening.flattenTiles[x1][y1]) < preferredHeight) {
            raise = true;
        }
        else if (Tiles.decodeHeight(Flattening.flattenTiles[x1][y1]) == preferredHeight) {
            return true;
        }
        return (raise && !mayRaiseCorner(initx, inity, Flattening.flattenTiles[x1][y1], x1, y1, maxDiff)) || (!raise && !mayLowerCorner(initx, inity, Flattening.flattenTiles[x1][y1], Flattening.rockTiles[x1][y1], x1, y1, maxDiff)) || changeCorner(performer, tilex, tiley, x1, y1, x2, y2, maxDiff, preferredHeight, raise, act) || isCornerDone(x1, y1, preferredHeight);
    }
    
    private static final boolean changeCorner(final Creature performer, final int tilex, final int tiley, final int x, final int y, final int targx, final int targy, final int maxDiff, final int preferredHeight, final boolean raise, final Action act) {
        if (raise) {
            if (Tiles.decodeHeight(Flattening.flattenTiles[targx][targy]) > preferredHeight && mayLowerCorner(tilex, tiley, Flattening.flattenTiles[targx][targy], Flattening.rockTiles[targx][targy], targx, targy, maxDiff) && !changeFlattenCorner(performer, targx, targy, maxDiff, preferredHeight, false, act)) {
                return changeFlattenCorner(performer, x, y, maxDiff, preferredHeight, true, act);
            }
        }
        else if (Tiles.decodeHeight(Flattening.flattenTiles[targx][targy]) < preferredHeight && mayRaiseCorner(tilex, tiley, Flattening.flattenTiles[targx][targy], targx, targy, maxDiff) && !changeFlattenCorner(performer, targx, targy, maxDiff, preferredHeight, true, act)) {
            return changeFlattenCorner(performer, x, y, maxDiff, preferredHeight, false, act);
        }
        return true;
    }
    
    private static final boolean mayRaiseCorner(final int tilex, final int tiley, final int tile, final int x, final int y, final int maxDiff) {
        final byte newType = Tiles.decodeType(tile);
        if (newType == Tiles.Tile.TILE_HOLE.id || Tiles.isMineDoor(newType)) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Flattening.immutableTiles[x][y]) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Terraforming.isImmutableTile(Tiles.decodeType(tile)) || Tiles.decodeType(tile) == Tiles.Tile.TILE_HOLE.id) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Terraforming.isImmutableTile(Tiles.decodeType(Flattening.flattenTiles[x][y - 1])) || Tiles.decodeType(Flattening.flattenTiles[x][y - 1]) == Tiles.Tile.TILE_HOLE.id) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Terraforming.isImmutableTile(Tiles.decodeType(Flattening.flattenTiles[x - 1][y])) || Tiles.decodeType(Flattening.flattenTiles[x - 1][y]) == Tiles.Tile.TILE_HOLE.id) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Terraforming.isImmutableTile(Tiles.decodeType(Flattening.flattenTiles[x - 1][y - 1])) || Tiles.decodeType(Flattening.flattenTiles[x - 1][y - 1]) == Tiles.Tile.TILE_HOLE.id) {
            ++Flattening.flattenImmutable;
            return false;
        }
        final int htN = Tiles.decodeHeight(Flattening.flattenTiles[x][y - 1]);
        final int htE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y]);
        final int htS = Tiles.decodeHeight(Flattening.flattenTiles[x][y + 1]);
        final int htW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y]);
        final int ht = Tiles.decodeHeight(Flattening.flattenTiles[x][y]);
        if (Math.abs(ht - htS) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(ht - htN) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(ht - htE) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(ht - htW) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        final int htNE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y - 1]);
        final int htSE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y + 1]);
        final int htSW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y + 1]);
        final int htNW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y - 1]);
        final boolean pC = Tiles.isRoadType(Flattening.flattenTiles[x][y]);
        final boolean pN = Tiles.isRoadType(Flattening.flattenTiles[x][y - 1]);
        final boolean pNW = Tiles.isRoadType(Flattening.flattenTiles[x - 1][y - 1]);
        final boolean pW = Tiles.isRoadType(Flattening.flattenTiles[x - 1][y]);
        final boolean hC = pC && MethodsHighways.onHighway(tilex + x - 1, tiley + y - 1, true);
        final boolean hN = pN && MethodsHighways.onHighway(tilex + x - 1, tiley + y - 2, true);
        final boolean hNW = pNW && MethodsHighways.onHighway(tilex + x - 2, tiley + y - 2, true);
        final boolean hW = pW && MethodsHighways.onHighway(tilex + x - 2, tiley + y - 1, true);
        final int dS = ht - htS;
        final int dN = ht - htN;
        final int dE = ht - htE;
        final int dW = ht - htW;
        if (Features.Feature.WAGONER.isEnabled()) {
            final boolean wC = hC && MethodsHighways.onWagonerCamp(tilex + x - 1, tiley + y - 1, true);
            final boolean wN = hN && MethodsHighways.onWagonerCamp(tilex + x - 1, tiley + y - 2, true);
            final boolean wNW = hNW && MethodsHighways.onWagonerCamp(tilex + x - 2, tiley + y - 2, true);
            final boolean wW = hW && MethodsHighways.onWagonerCamp(tilex + x - 2, tiley + y - 1, true);
            if ((wC || wN) && dE >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wC || wW) && dS >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wW || wNW) && dW >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wN || wNW) && dN >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            return true;
        }
        else {
            final int dNE = ht - htNE;
            final int dSE = ht - htSE;
            final int dSW = ht - htSW;
            final int dNW = ht - htNW;
            if ((hC || hN) && dE >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hC || hW) && dS >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hW || hNW) && dW >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hN || hNW) && dN >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hC && dSE >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hW && dSW >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hNW && dNW >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hN && dNE >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            return true;
        }
    }
    
    private static final boolean mayLowerCorner(final int tilex, final int tiley, final int tile, final int rocktile, final int x, final int y, final int maxDiff) {
        if (Flattening.immutableTiles[x][y]) {
            ++Flattening.flattenImmutable;
            return false;
        }
        final byte newType = Tiles.decodeType(tile);
        if (newType == Tiles.Tile.TILE_HOLE.id || newType == Tiles.Tile.TILE_CLIFF.id || Tiles.isMineDoor(newType)) {
            ++Flattening.flattenImmutable;
            return false;
        }
        if (Tiles.decodeHeight(tile) <= Tiles.decodeHeight(rocktile)) {
            ++Flattening.flattenRock;
            return false;
        }
        if (Tiles.decodeType(tile) == Tiles.Tile.TILE_HOLE.id) {
            ++Flattening.flattenImmutable;
            return false;
        }
        final int htN = Tiles.decodeHeight(Flattening.flattenTiles[x][y - 1]);
        final int htE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y]);
        final int htS = Tiles.decodeHeight(Flattening.flattenTiles[x][y + 1]);
        final int htW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y]);
        final int ht = Tiles.decodeHeight(Flattening.flattenTiles[x][y]);
        if (Math.abs(htS - ht) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(htN - ht) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(htE - ht) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        if (Math.abs(htW - ht) > maxDiff) {
            ++Flattening.flattenSkill;
            return false;
        }
        final int htNE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y - 1]);
        final int htSE = Tiles.decodeHeight(Flattening.flattenTiles[x + 1][y + 1]);
        final int htSW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y + 1]);
        final int htNW = Tiles.decodeHeight(Flattening.flattenTiles[x - 1][y - 1]);
        final boolean pC = Tiles.isRoadType(Flattening.flattenTiles[x][y]);
        final boolean pN = Tiles.isRoadType(Flattening.flattenTiles[x][y - 1]);
        final boolean pNW = Tiles.isRoadType(Flattening.flattenTiles[x - 1][y - 1]);
        final boolean pW = Tiles.isRoadType(Flattening.flattenTiles[x - 1][y]);
        final boolean hC = pC && MethodsHighways.onHighway(tilex + x - 1, tiley + y - 1, true);
        final boolean hN = pN && MethodsHighways.onHighway(tilex + x - 1, tiley + y - 2, true);
        final boolean hNW = pNW && MethodsHighways.onHighway(tilex + x - 2, tiley + y - 2, true);
        final boolean hW = pW && MethodsHighways.onHighway(tilex + x - 2, tiley + y - 1, true);
        final int dS = htS - ht;
        final int dN = htN - ht;
        final int dE = htE - ht;
        final int dW = htW - ht;
        if (Features.Feature.WAGONER.isEnabled()) {
            final boolean wC = hC && MethodsHighways.onWagonerCamp(tilex + x - 1, tiley + y - 1, true);
            final boolean wN = hN && MethodsHighways.onWagonerCamp(tilex + x - 1, tiley + y - 2, true);
            final boolean wNW = hNW && MethodsHighways.onWagonerCamp(tilex + x - 2, tiley + y - 2, true);
            final boolean wW = hW && MethodsHighways.onWagonerCamp(tilex + x - 2, tiley + y - 1, true);
            if ((wC || wN) && dE >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wC || wW) && dS >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wW || wNW) && dW >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((wN || wNW) && dN >= 0) {
                ++Flattening.flattenSlope;
                return false;
            }
            return true;
        }
        else {
            final int dNE = htNE - ht;
            final int dSE = htSE - ht;
            final int dSW = htSW - ht;
            final int dNW = htNW - ht;
            if ((hC || hN) && dE >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hC || hW) && dS >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hW || hNW) && dW >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if ((hN || hNW) && dN >= 20) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hC && dSE >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hW && dSW >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hNW && dNW >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            if (hN && dNE >= 28) {
                ++Flattening.flattenSlope;
                return false;
            }
            return true;
        }
    }
    
    public static boolean shouldBeRock(final int x, final int y) {
        int numberOfCornersAtRockHeight = 0;
        for (int xx = 0; xx <= 1; ++xx) {
            for (int yy = 0; yy <= 1; ++yy) {
                final short tileHeight = Tiles.decodeHeight(Flattening.flattenTiles[x + xx][y + yy]);
                final short rockHeight = Tiles.decodeHeight(Flattening.rockTiles[x + xx][y + yy]);
                if (tileHeight <= rockHeight) {
                    ++numberOfCornersAtRockHeight;
                }
            }
        }
        if (numberOfCornersAtRockHeight == 4) {
            final byte type = Tiles.decodeType(Flattening.flattenTiles[x][y]);
            if (!Terraforming.isRockTile(type) && !Terraforming.isImmutableTile(type)) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean isCornerDone(final int x, final int y, final int preferredHeight) {
        return Tiles.decodeHeight(Flattening.flattenTiles[x][y]) == Tiles.decodeHeight(Flattening.rockTiles[x][y]) || Tiles.decodeHeight(Flattening.flattenTiles[x][y]) == preferredHeight;
    }
    
    private static final boolean changeFlattenCorner(final Creature performer, final int x, final int y, final int maxDiff, final int preferredHeight, final boolean raise, final Action act) {
        int newTile = Flattening.flattenTiles[x][y];
        byte newType;
        final byte oldType = newType = Tiles.decodeType(newTile);
        short newHeight = Tiles.decodeHeight(newTile);
        if (raise) {
            newHeight = (short)Math.min(32767, newHeight + 1);
        }
        else {
            newHeight = (short)Math.max(-32768, newHeight - 1);
        }
        for (int a = 0; a >= -1; --a) {
            for (int b = 0; b >= -1; --b) {
                final int modx = x + a;
                final int mody = y + b;
                if (a == 0 && b == 0) {
                    Flattening.newFHeight = newHeight;
                    newType = oldType;
                    newTile = Flattening.flattenTiles[x][y];
                }
                else {
                    Flattening.newFHeight = Tiles.decodeHeight(Flattening.flattenTiles[modx][mody]);
                    newType = Tiles.decodeType(Flattening.flattenTiles[modx][mody]);
                    newTile = Flattening.flattenTiles[modx][mody];
                }
                if (Terraforming.isImmutableOrRoadTile(newType)) {
                    if (a == 0 && b == 0 && !raise) {
                        if (Flattening.immutableTiles[modx][mody]) {
                            Flattening.logger.log(Level.WARNING, "Does this really change anything? Changing " + modx + "," + mody + " from " + Tiles.decodeHeight(Flattening.flattenTiles[modx][mody]) + " to " + Flattening.newFHeight + " at  protected=" + Flattening.immutableTiles[modx][mody] + " xy=" + x + "," + y + " ab=" + a + "," + b, new Exception());
                        }
                        Flattening.changedTiles[modx][mody] = true;
                        Flattening.flattenTiles[modx][mody] = Tiles.encode(Flattening.newFHeight, newType, Tiles.decodeData(newTile));
                    }
                    else if (Tiles.isRoadType(newType)) {
                        Flattening.changedTiles[modx][mody] = true;
                        Flattening.flattenTiles[modx][mody] = Tiles.encode(Flattening.newFHeight, newType, Tiles.decodeData(newTile));
                    }
                }
                else {
                    if (newType == Tiles.Tile.TILE_SAND.id || oldType == Tiles.Tile.TILE_SAND.id) {
                        if (newType != Tiles.Tile.TILE_DIRT.id) {
                            newType = Tiles.Tile.TILE_SAND.id;
                        }
                    }
                    else {
                        newType = Tiles.Tile.TILE_DIRT.id;
                    }
                    if (oldType != newType) {
                        TileEvent.log(performer.getTileX(), performer.getTileY(), 0, performer.getWurmId(), act.getNumber());
                    }
                    Flattening.flattenTiles[modx][mody] = Tiles.encode(Flattening.newFHeight, newType, (byte)0);
                    Flattening.changedTiles[modx][mody] = true;
                }
            }
        }
        return false;
    }
    
    private static final void checkUseDirt(final int tilex, final int tiley, final int endX, final int endY, final Creature performer, final Item source, final int maxdiff, int preferredHeight, final Action act, final boolean quickLevel) {
        int higherFlatten = 0;
        int lowerFlatten = 0;
        for (int x = 1; x <= endX; ++x) {
            for (int y = 1; y <= endY; ++y) {
                final int diff = Tiles.decodeHeight(Flattening.flattenTiles[x][y]) - preferredHeight;
                if (diff >= 1) {
                    ++lowerFlatten;
                }
                else if (diff <= -1) {
                    ++higherFlatten;
                }
            }
        }
        Flattening.raising = false;
        if ((lowerFlatten >= 2 && act.getNumber() == 150) || (lowerFlatten >= 1 && act.getNumber() == 533)) {
            if (performer.getCarriedItem(26) != null || performer.getCarriedItem(298) != null) {
                ++preferredHeight;
                Flattening.raising = true;
            }
        }
        else if (higherFlatten > 0) {
            Flattening.raising = true;
        }
        if (Flattening.raising) {
            for (int x = 1; x <= endX; ++x) {
                for (int y = 1; y <= endY; ++y) {
                    final int diff = preferredHeight - Tiles.decodeHeight(Flattening.flattenTiles[x][y]);
                    if (diff >= 1 && mayRaiseCorner(tilex, tiley, Flattening.flattenTiles[x][y], x, y, maxdiff)) {
                        useDirt(performer, x, y, maxdiff, preferredHeight, quickLevel, act);
                    }
                }
            }
            if (Flattening.needsDirt == 3 && act.getNumber() != 532) {
                int dirtX = -1;
                int dirtY = -1;
                for (int x2 = 1; x2 <= endX; ++x2) {
                    for (int y2 = 1; y2 <= endY; ++y2) {
                        final int diff2 = Tiles.decodeHeight(Flattening.flattenTiles[x2][y2]) - preferredHeight + 1;
                        if (diff2 >= 1 && mayLowerCorner(tilex, tiley, Flattening.flattenTiles[x2][y2], Flattening.rockTiles[x2][y2], x2, y2, maxdiff)) {
                            if (dirtX == -1) {
                                dirtX = x2;
                                dirtY = y2;
                            }
                            else if (Server.rand.nextInt(2) == 1) {
                                dirtX = x2;
                                dirtY = y2;
                            }
                        }
                    }
                }
                if (dirtX > -1) {
                    getDirt(performer, source, dirtX, dirtY, maxdiff, preferredHeight, quickLevel, act);
                    if (Flattening.needsDirt == 3) {
                        Flattening.needsDirt = 0;
                    }
                }
            }
        }
        else if (act.getNumber() == 532) {
            int dirtX = -1;
            int dirtY = -1;
            for (int x2 = 1; x2 <= endX; ++x2) {
                for (int y2 = 1; y2 <= endY; ++y2) {
                    final int diff2 = Tiles.decodeHeight(Flattening.flattenTiles[x2][y2]) - preferredHeight;
                    if (diff2 >= 1 && mayLowerCorner(tilex, tiley, Flattening.flattenTiles[x2][y2], Flattening.rockTiles[x2][y2], x2, y2, maxdiff)) {
                        if (dirtX == -1) {
                            dirtX = x2;
                            dirtY = y2;
                        }
                        else if (Server.rand.nextInt(2) == 1) {
                            dirtX = x2;
                            dirtY = y2;
                        }
                    }
                }
            }
            if (dirtX > -1) {
                getDirt(performer, source, dirtX, dirtY, maxdiff, preferredHeight, quickLevel, act);
            }
        }
        else {
            for (int x = 1; x <= endX; ++x) {
                for (int y = 1; y <= endY; ++y) {
                    final int diff = Tiles.decodeHeight(Flattening.flattenTiles[x][y]) - preferredHeight;
                    if (diff >= 1 && mayLowerCorner(tilex, tiley, Flattening.flattenTiles[x][y], Flattening.rockTiles[x][y], x, y, maxdiff)) {
                        getDirt(performer, source, x, y, maxdiff, preferredHeight, quickLevel, act);
                    }
                }
            }
        }
    }
    
    private static final void useDirt(final Creature performer, final int x, final int y, final int maxDiff, final int preferredHeight, final boolean quickLevel, final Action act) {
        Item dirt = performer.getCarriedItem(26);
        if (dirt == null) {
            dirt = performer.getCarriedItem(298);
        }
        if (dirt != null) {
            Items.destroyItem(dirt.getWurmId());
            try {
                final Item dirtAfter = Items.getItem(dirt.getWurmId());
                if (quickLevel) {
                    performer.getCommunicator().sendNormalServerMessage("You have a ghost " + dirt.getActualName() + " in inventory.");
                    return;
                }
                performer.getCommunicator().sendNormalServerMessage("One corner could not be raised by a " + dirt.getActualName() + " you are carrying.");
                ++Flattening.needsDirt;
                return;
            }
            catch (NoSuchItemException e) {
                if (!changeFlattenCorner(performer, x, y, maxDiff, preferredHeight, true, act)) {
                    performer.getCommunicator().sendNormalServerMessage("You use a " + dirt.getActualName() + " in one corner.");
                }
                return;
            }
        }
        if (quickLevel) {
            changeFlattenCorner(performer, x, y, maxDiff, preferredHeight, true, act);
        }
        else {
            ++Flattening.needsDirt;
        }
    }
    
    private static final void getDirt(final Creature performer, final Item source, final int x, final int y, final int maxDiff, final int preferredHeight, final boolean quickLevel, final Action act) {
        try {
            final byte type = Tiles.decodeType(Flattening.flattenTiles[x][y]);
            final String dirtType = (type == Tiles.Tile.TILE_SAND.id) ? "heap of sand" : "dirt pile";
            Item ivehic = null;
            if (performer.getInventory().getNumItemsNotCoins() < 100 && performer.canCarry(ItemTemplateFactory.getInstance().getTemplate(26).getWeightGrams())) {
                try {
                    ivehic = Items.getItem(performer.getVehicle());
                }
                catch (NoSuchItemException nsi) {
                    ivehic = null;
                }
            }
            if (source.isDredgingTool() && !source.isWand()) {
                final int dirtVol = 1000;
                if (ivehic != null && ivehic.getFreeVolume() < dirtVol) {
                    if (source.getFreeVolume() < dirtVol) {
                        --Flattening.needsDirt;
                        performer.getCommunicator().sendNormalServerMessage("The " + ivehic.getName() + " and the " + source.getName() + " are both full.");
                        return;
                    }
                }
                else if (ivehic == null && source.getFreeVolume() < dirtVol) {
                    --Flattening.needsDirt;
                    performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " cannot fit anything more inside of it.");
                    return;
                }
            }
            if (performer.getInventory().getNumItemsNotCoins() < 100 && performer.canCarry(ItemTemplateFactory.getInstance().getTemplate(26).getWeightGrams())) {
                if (!changeFlattenCorner(performer, x, y, maxDiff, preferredHeight, false, act)) {
                    int template = 26;
                    if (type == Tiles.Tile.TILE_SAND.id) {
                        template = 298;
                    }
                    if (!quickLevel) {
                        final Item dirt = ItemFactory.createItem(template, Server.rand.nextFloat() * maxDiff / 3.0f, performer.getName());
                        if (source.isDredgingTool() && !source.isWand()) {
                            boolean addedToBoat = false;
                            if (performer.getVehicle() != -10L && ivehic != null && ivehic.isBoat() && ivehic.testInsertItem(dirt)) {
                                ivehic.insertItem(dirt);
                                performer.getCommunicator().sendNormalServerMessage("You put the " + dirt.getName() + " in the " + ivehic.getName() + ".");
                                addedToBoat = true;
                            }
                            if (!addedToBoat) {
                                source.insertItem(dirt, true);
                            }
                        }
                        else {
                            performer.getInventory().insertItem(dirt, true);
                        }
                        performer.getCommunicator().sendNormalServerMessage("You assemble some " + ((template == 26) ? "dirt" : "sand") + " from a corner.");
                    }
                }
            }
            else {
                --Flattening.needsDirt;
                performer.getCommunicator().sendNormalServerMessage("You are not strong enough to carry one more " + dirtType + ".");
            }
        }
        catch (NoSuchTemplateException nst) {
            Flattening.logger.log(Level.WARNING, performer.getName() + " No dirt template?", nst);
        }
        catch (FailedException fe) {
            Flattening.logger.log(Level.WARNING, performer.getName() + " failed to create dirt?", fe);
        }
    }
    
    static boolean isTileTooDeep(final int tilex, final int tiley, final int totalx, final int totaly, final int numbCorners) {
        int belowWater = 0;
        for (int x = tilex; x < tilex + totalx; ++x) {
            for (int y = tiley; y < tiley + totaly; ++y) {
                final short ht = Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y));
                if (ht <= -7.0f) {
                    ++belowWater;
                }
            }
        }
        return belowWater == numbCorners;
    }
    
    static {
        logger = Logger.getLogger(Flattening.class.getName());
        Flattening.flattenTiles = new int[4][4];
        Flattening.rockTiles = new int[4][4];
        Flattening.immutableTiles = new boolean[4][4];
        Flattening.changedTiles = new boolean[4][4];
        Flattening.flattenSkill = 0;
        Flattening.skill = 0.0;
        Flattening.flattenRock = 0;
        Flattening.flattenDone = 0;
        Flattening.flattenImmutable = 0;
        Flattening.flattenSlope = 0;
        Flattening.needsDirt = 0;
        Flattening.newFHeight = 0;
        Flattening.raising = false;
    }
}
