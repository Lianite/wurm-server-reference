// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Features;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.Point;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class Locates
{
    private static final Logger logger;
    
    static void locateSpring(final Creature performer, final Item pendulum, final Skill primSkill) {
        final int[] closest = Zones.getClosestSpring(performer.getTileX(), performer.getTileY(), (int)(10.0f * getMaterialPendulumModifier(pendulum.getMaterial())));
        final int max = Math.max(closest[0], closest[1]);
        final double knowl = primSkill.getKnowledge(pendulum.getCurrentQualityLevel());
        final float difficulty = Server.rand.nextFloat() * (max + 3) * 30.0f;
        double result = Server.rand.nextFloat() * knowl * 10.0;
        result -= difficulty;
        Server.getInstance().broadCastAction(performer.getName() + " lets out a mild sigh as " + performer.getHeSheItString() + " starts breathing again.", performer, 5);
        if (closest[0] == -1) {
            performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " seems dead.");
        }
        else if (result > 0.0) {
            if (max < 1) {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " now swings frantically! There is something here!");
            }
            else if (max < 2) {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " swings rapidly back and forth! You are close to a water source!");
            }
            else if (max < 3) {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " is swinging in a circle, there is probably a water source in the ground nearby.");
            }
            else if (max < 5) {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " is starting to move, indicating a flow of energy somewhere near.");
            }
            else if (result > 30.0) {
                performer.getCommunicator().sendNormalServerMessage("You think you detect some faint tugs in the " + pendulum.getName() + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " seems dead.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " seems dead.");
        }
    }
    
    static void useLocateItem(final Creature performer, final Item pendulum, final Skill primSkill) {
        if (pendulum.getSpellLocChampBonus() > 0.0f) {
            locateChamp(performer, pendulum, primSkill);
        }
        else if (pendulum.getSpellLocEnemyBonus() > 0.0f) {
            locateEnemy(performer, pendulum, primSkill);
        }
        else if (pendulum.getSpellLocFishBonus() > 0.0f) {
            if (Servers.isThisATestServer() && performer.isOnSurface()) {
                performer.getCommunicator().sendAlertServerMessage("New fishing...");
                locateFish(performer, pendulum, primSkill, true);
                performer.getCommunicator().sendAlertServerMessage("Old fishing...");
                locateFish(performer, pendulum, primSkill, false);
            }
            else {
                locateFish(performer, pendulum, primSkill, true);
            }
        }
    }
    
    static void locateChamp(final Creature performer, final Item pendulum, final Skill primSkill) {
        final int x = performer.getTileX();
        final int y = performer.getTileY();
        final int dist = (int)(pendulum.getSpellLocChampBonus() / 100.0f * Zones.worldTileSizeX / 32.0f * getMaterialPendulumModifier(pendulum.getMaterial()));
        final Creature firstChamp = findFirstCreature(x, y, dist, performer.isOnSurface(), true, performer);
        if (firstChamp != null) {
            final int dx = Math.abs(x - firstChamp.getTileX());
            final int dy = Math.abs(y - firstChamp.getTileY());
            final int maxd = (int)Math.sqrt(dx * dx + dy * dy);
            if (primSkill.skillCheck(maxd / 10.0f, pendulum, 0.0, false, 5.0f) > 0.0) {
                final int dir = MethodsCreatures.getDir(performer, firstChamp.getTileX(), firstChamp.getTileY());
                final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                final String toReturn = EndGameItems.getDistanceString(maxd, firstChamp.getName(), direction, false);
                performer.getCommunicator().sendNormalServerMessage(toReturn);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
            }
        }
        else if (primSkill.skillCheck(10.0, pendulum, 0.0, false, 5.0f) > 0.0) {
            performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " doesn't seem to move.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
        }
    }
    
    static void locateEnemy(final Creature performer, final Item pendulum, final Skill primSkill) {
        final int x = performer.getTileX();
        final int y = performer.getTileY();
        final int dist = (int)(pendulum.getSpellLocEnemyBonus() * getMaterialPendulumModifier(pendulum.getMaterial()));
        final Creature firstEnemy = findFirstCreature(x, y, dist, performer.isOnSurface(), false, performer);
        if (firstEnemy != null) {
            final int dx = Math.abs(x - firstEnemy.getTileX());
            final int dy = Math.abs(y - firstEnemy.getTileY());
            final int maxd = (int)Math.sqrt(dx * dx + dy * dy);
            if (primSkill.skillCheck(maxd / 10.0f, pendulum, 0.0, false, 5.0f) > 0.0) {
                final int dir = MethodsCreatures.getDir(performer, firstEnemy.getTileX(), firstEnemy.getTileY());
                final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                final String toReturn = EndGameItems.getDistanceString(maxd, "enemy", direction, false);
                performer.getCommunicator().sendNormalServerMessage(toReturn);
                locateTraitor(performer, pendulum, primSkill);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
            }
        }
        else if (primSkill.skillCheck(10.0, pendulum, 0.0, false, 5.0f) > 0.0) {
            if (!locateTraitor(performer, pendulum, primSkill)) {
                performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " doesn't seem to move.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
        }
    }
    
    static boolean locateTraitor(final Creature performer, final Item pendulum, final Skill primSkill) {
        final Creature[] possibleTraitors = EpicServerStatus.getCurrentTraitors();
        if (possibleTraitors != null) {
            final int maxDist = (int)(pendulum.getSpellLocEnemyBonus() / 100.0f * Zones.worldTileSizeX / 16.0f * getMaterialPendulumModifier(pendulum.getMaterial()));
            for (final Creature c : possibleTraitors) {
                if (performer.isWithinDistanceTo(c, maxDist)) {
                    final int dx = Math.abs(performer.getTileX() - c.getTileX());
                    final int dy = Math.abs(performer.getTileY() - c.getTileY());
                    final int maxd = (int)Math.sqrt(dx * dx + dy * dy);
                    final int dir = MethodsCreatures.getDir(performer, c.getTileX(), c.getTileY());
                    final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                    final String toReturn = EndGameItems.getDistanceString(maxd, c.getName(), direction, false);
                    performer.getCommunicator().sendNormalServerMessage(toReturn);
                    return true;
                }
            }
        }
        return false;
    }
    
    static void locateFish(final Creature performer, final Item pendulum, final Skill primSkill, final boolean newFishing) {
        if (!performer.isOnSurface()) {
            performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " does not move.");
            return;
        }
        final int maxDist = (int)(pendulum.getSpellLocFishBonus() / 10.0f * getMaterialPendulumModifier(pendulum.getMaterial()));
        Point[] points;
        if (newFishing) {
            final int season = WurmCalendar.getSeasonNumber();
            points = MethodsFishing.getSpecialSpots(performer.getTileX(), performer.getTileY(), season);
        }
        else {
            points = Fish.getRareSpots(performer.getTileX(), performer.getTileY());
        }
        boolean found = false;
        for (final Point point : points) {
            if (performer.isWithinTileDistanceTo(point.getX(), point.getY(), 0, maxDist + 5)) {
                sendFishFound(point.getX(), point.getY(), point.getH(), performer, primSkill, pendulum);
                found = true;
            }
        }
        if (!found) {
            performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
        }
    }
    
    private static final void sendFishFound(final int targx, final int targy, final int fish, final Creature performer, final Skill primSkill, final Item pendulum) {
        final int x = performer.getTileX();
        final int y = performer.getTileY();
        if (fish > 0) {
            final int dx = Math.max(0, Math.abs(x - targx) - 5);
            final int dy = Math.max(0, Math.abs(y - targy) - 5);
            final int maxd = (int)Math.sqrt(dx * dx + dy * dy);
            final double skillCheck = primSkill.skillCheck(maxd, pendulum, 0.0, false, 5.0f);
            if (skillCheck > 0.0) {
                final int dir = MethodsCreatures.getDir(performer, targx, targy);
                final String direction = MethodsCreatures.getLocationStringFor(performer.getStatus().getRotation(), dir, "you");
                String spot = "fishing spot";
                if (skillCheck > 75.0) {
                    final String name = ItemTemplateFactory.getInstance().getTemplateName(fish);
                    if (name.length() > 0) {
                        spot = name + " fishing spot";
                    }
                }
                String loc = "";
                if (performer.getPower() >= 2) {
                    loc = " (" + targx + "," + targy + ")";
                }
                final String toReturn = EndGameItems.getDistanceString(maxd, spot + loc, direction, false);
                performer.getCommunicator().sendNormalServerMessage(toReturn);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You feel there is something there but cannot determine what.");
            }
        }
        else if (primSkill.skillCheck(10.0, pendulum, 0.0, false, 5.0f) > 0.0) {
            performer.getCommunicator().sendNormalServerMessage("The " + pendulum.getName() + " doesn't seem to move.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You fail to make sense of the " + pendulum.getName() + ".");
        }
    }
    
    static final Creature findFirstCreature(final int x, final int y, final int maxdist, final boolean surfaced, final boolean champ, final Creature performer) {
        for (int tdist = 0; tdist <= maxdist; ++tdist) {
            if (tdist == 0) {
                final Creature c = getCreatureOnTile(x, y, tdist, surfaced, champ, performer);
                if (c != null) {
                    return c;
                }
            }
            else {
                final Creature c = findCreatureOnRow(x, y, tdist, surfaced, champ, performer);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }
    
    private static final Creature findCreatureOnRow(final int x, final int y, final int dist, final boolean surfaced, final boolean champ, final Creature performer) {
        for (int tx = x; tx < x + dist; ++tx) {
            if (tx < Zones.worldTileSizeX && tx > 0) {
                final Creature toReturn = getCreatureOnTile(tx, y - dist, dist, surfaced, champ, performer);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        }
        for (int ty = y - dist; ty < y; ++ty) {
            if (ty < Zones.worldTileSizeY && ty > 0) {
                final Creature toreturn = getCreatureOnTile(x + dist, dist, ty, surfaced, champ, performer);
                if (toreturn != null) {
                    return toreturn;
                }
            }
        }
        for (int ty = y; ty <= y + dist; ++ty) {
            if (ty < Zones.worldTileSizeY && ty > 0) {
                final Creature toreturn = getCreatureOnTile(x + dist, dist, ty, surfaced, champ, performer);
                if (toreturn != null) {
                    return toreturn;
                }
            }
        }
        for (int tx = x; tx < x + dist; ++tx) {
            if (tx < Zones.worldTileSizeX && tx > 0) {
                final Creature toReturn = getCreatureOnTile(tx, y + dist, dist, surfaced, champ, performer);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        }
        for (int ty = y - dist; ty < y; ++ty) {
            if (ty < Zones.worldTileSizeY && ty > 0) {
                final Creature toreturn = getCreatureOnTile(x - dist, dist, ty, surfaced, champ, performer);
                if (toreturn != null) {
                    return toreturn;
                }
            }
        }
        for (int tx = x - dist; tx < x; ++tx) {
            if (tx < Zones.worldTileSizeX && tx > 0) {
                final Creature toReturn = getCreatureOnTile(tx, y + dist, dist, surfaced, champ, performer);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        }
        for (int ty = y; ty < y + dist; ++ty) {
            if (ty < Zones.worldTileSizeY && ty > 0) {
                final Creature toreturn = getCreatureOnTile(x - dist, ty, dist, surfaced, champ, performer);
                if (toreturn != null) {
                    return toreturn;
                }
            }
        }
        for (int tx = x - dist; tx < x; ++tx) {
            if (tx < Zones.worldTileSizeX && tx > 0) {
                final Creature toReturn = getCreatureOnTile(tx, y - dist, dist, surfaced, champ, performer);
                if (toReturn != null) {
                    return toReturn;
                }
            }
        }
        return null;
    }
    
    static final Creature getCreatureOnTile(final int x, final int y, final int dist, final boolean surfaced, final boolean champ, final Creature performer) {
        final VolaTile t = Zones.getTileOrNull(x, y, surfaced);
        if (t != null) {
            final Creature[] creatures;
            final Creature[] crets = creatures = t.getCreatures();
            for (final Creature c : creatures) {
                if (champ && (c.getStatus().isChampion() || c.isUnique())) {
                    return c;
                }
                Label_0212: {
                    if (!champ && c.isPlayer()) {
                        boolean found = c.getAttitude(performer) == 2;
                        if (!found) {
                            found = (performer.getCitizenVillage() != null && performer.getCitizenVillage().isEnemy(c));
                        }
                        if (found) {
                            if (!c.isStealth() || dist <= 25) {
                                final float nolocateEnchantPower = c.getNoLocateItemBonus(false);
                                if (nolocateEnchantPower > 0.0f) {
                                    final int maxDistance = 100;
                                    final int distReduction = (int)(nolocateEnchantPower / 2.0f);
                                    if (dist > maxDistance - distReduction) {
                                        break Label_0212;
                                    }
                                }
                                if (c.getBonusForSpellEffect((byte)29) <= 0.0f) {
                                    return c;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private static float getMaterialPendulumModifier(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 56: {
                    return 1.15f;
                }
                case 30: {
                    return 1.025f;
                }
                case 31: {
                    return 1.05f;
                }
                case 10: {
                    return 0.95f;
                }
                case 57: {
                    return 1.2f;
                }
                case 7: {
                    return 1.1f;
                }
                case 12: {
                    return 0.9f;
                }
                case 67: {
                    return 1.25f;
                }
                case 8: {
                    return 1.05f;
                }
                case 9: {
                    return 1.025f;
                }
                case 34: {
                    return 0.95f;
                }
                case 13: {
                    return 0.95f;
                }
                case 96: {
                    return 1.075f;
                }
            }
        }
        return 1.0f;
    }
    
    static {
        logger = Logger.getLogger(Locates.class.getName());
    }
}
