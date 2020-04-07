// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.Iterator;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import java.util.logging.Level;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Server;
import com.wurmonline.server.Constants;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class ShardBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    private static final int TYPE_MULT = 17;
    private static final int TYPE_OFFSET = 17;
    private static final int QL_MULT = 18;
    private static final int QL_OFFSET = -3;
    
    ShardBehaviour() {
        super((short)46);
    }
    
    List<ActionEntry> getShardBehaviours(final Creature performer, @Nullable final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (target != null && (target.isShard() || target.isOre())) {
            toReturn.add(Actions.actionEntrys[536]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getShardBehaviours(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getShardBehaviours(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        return this.performShardAction(act, performer, null, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        return this.performShardAction(act, performer, source, target, action, counter);
    }
    
    boolean performShardAction(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter) {
        if (action == 536) {
            boolean done = false;
            if (target != null && (target.isShard() || target.isOre())) {
                if (target.getOwnerId() != performer.getWurmId()) {
                    performer.getCommunicator().sendSafeServerMessage("You need to carry the " + target.getName() + " in order to analyse it.");
                    return true;
                }
                int tilex = target.getDataX();
                final int tiley = target.getDataY();
                final String targetType = target.isOre() ? "ore" : "shard";
                final Skills skills = performer.getSkills();
                Skill prospecting = null;
                try {
                    prospecting = skills.getSkill(10032);
                }
                catch (Exception ex) {
                    prospecting = skills.learn(10032, 1.0f);
                }
                if (prospecting.getKnowledge(0.0) <= 20.0) {
                    performer.getCommunicator().sendNormalServerMessage("You are unable to work out how to analyse the " + targetType + ".");
                    return true;
                }
                if (counter == 1.0f) {
                    if (tilex > 0) {
                        target.setDataXY(-tilex, tiley);
                    }
                }
                else {
                    tilex = -tilex;
                }
                if (tilex == -1) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " looks too old for a decent analysis and therefore you decide not to analyse it.");
                    return true;
                }
                if (tilex <= 0 || tiley <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("It looks like someone has tampered with the " + target.getName() + " and therefore you decide not to analyse it.");
                    return true;
                }
                if (tilex < 1 || tilex > 1 << Constants.meshSize || tiley < 1 || tiley > 1 << Constants.meshSize) {
                    performer.getCommunicator().sendNormalServerMessage("You are unable to determine the origin of the " + target.getName() + ", analysis would be futile.");
                    return true;
                }
                if (counter == 1.0f) {
                    String sstring = "sound.work.prospecting1";
                    final int x = Server.rand.nextInt(3);
                    if (x == 0) {
                        sstring = "sound.work.prospecting2";
                    }
                    else if (x == 1) {
                        sstring = "sound.work.prospecting3";
                    }
                    SoundPlayer.playSound(sstring, performer.getTileX(), performer.getTileY(), performer.isOnSurface(), 1.0f);
                    final int maxRadius = calcMaxRadius(prospecting.getKnowledge(0.0));
                    final float time = calcTickTime(performer, prospecting);
                    act.setNextTick(time);
                    act.setTickCount(1);
                    final float totalTime = time * maxRadius;
                    try {
                        performer.getCurrentAction().setTimeLeft((int)totalTime);
                    }
                    catch (NoSuchActionException nsa) {
                        ShardBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                    }
                    performer.getCommunicator().sendNormalServerMessage("You start to analyse the " + targetType + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts analysing the " + targetType + ".", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[536].getVerbString(), true, (int)totalTime);
                    performer.getCommunicator().sendNormalServerMessage(target.examine(performer));
                }
                if (counter * 10.0f >= act.getNextTick()) {
                    final int radius = act.getTickCount();
                    final int currentSkill = (int)prospecting.getKnowledge(0.0);
                    final int maxRadius = calcMaxRadius(currentSkill);
                    final int skillTypeOffset = currentSkill - (radius * 17 - 17);
                    final int skillQLOffset = currentSkill - (radius * 18 + 3);
                    act.incTickCount();
                    act.incNextTick(calcTickTime(performer, prospecting));
                    performer.getStatus().modifyStamina(-1500 * radius);
                    prospecting.skillCheck(target.getCurrentQualityLevel(), null, 0.0, false, counter / radius);
                    if (radius >= maxRadius) {
                        done = true;
                    }
                    final LinkedList<String> list = new LinkedList<String>();
                    for (int x2 = -radius; x2 <= radius; ++x2) {
                        for (int y = -radius; y <= radius; ++y) {
                            if (x2 == -radius || x2 == radius || y == -radius || y == radius) {
                                String dir = "";
                                if (performer.getBestCompass() != null) {
                                    if (y < 0) {
                                        dir = "north";
                                    }
                                    else if (y > 0) {
                                        dir = "south";
                                    }
                                    String we = "";
                                    if (x2 < 0) {
                                        we = "west";
                                    }
                                    else if (x2 > 0) {
                                        we = "east";
                                    }
                                    if (dir.length() > 0) {
                                        if (we.length() > 0) {
                                            if (Math.abs(x2) == Math.abs(y)) {
                                                dir += we;
                                            }
                                            else if (Math.abs(x2) < Math.abs(y)) {
                                                dir = we + " of " + dir;
                                            }
                                            else {
                                                dir = dir + " of " + we;
                                            }
                                        }
                                    }
                                    else {
                                        dir = we;
                                    }
                                    dir = " (" + dir + ")";
                                }
                                int resource = Server.getCaveResource(tilex + x2, tiley + y);
                                if (resource == 65535) {
                                    resource = Server.rand.nextInt(10000);
                                    Server.setCaveResource(tilex + x2, tiley + y, resource);
                                }
                                final String foundString = checkTile(performer, tilex + x2, tiley + y, radius, skillTypeOffset, skillQLOffset);
                                add2List(list, foundString, dir);
                                if (prospecting.getKnowledge(0.0) > 40.0) {
                                    final byte type = Tiles.decodeType(Server.caveMesh.getTile(tilex + x2, tiley + y));
                                    if (type != Tiles.Tile.TILE_CAVE_WALL_ROCKSALT.id) {
                                        TileRockBehaviour.rockRandom.setSeed((tilex + x2 + (tiley + y) * Zones.worldTileSizeY) * 102533L);
                                        if (TileRockBehaviour.rockRandom.nextInt(100) == 0) {
                                            final String foundSalt = checkSaltFlint(performer, tilex + x2, tiley + y, radius, skillTypeOffset, true);
                                            add2List(list, foundSalt, dir);
                                        }
                                    }
                                }
                                TileRockBehaviour.rockRandom.setSeed((tilex + x2 + (tiley + y) * Zones.worldTileSizeY) * 6883L);
                                if (TileRockBehaviour.rockRandom.nextInt(200) == 0) {
                                    final String foundFlint = checkSaltFlint(performer, tilex + x2, tiley + y, radius, skillTypeOffset, false);
                                    add2List(list, foundFlint, dir);
                                }
                            }
                        }
                    }
                    outputList(performer, list, radius);
                    if (!done) {
                        furtherStudy(performer, radius + 1);
                    }
                }
                if (done) {
                    performer.getCommunicator().sendNormalServerMessage("You finish analysing the " + targetType + ".");
                }
            }
            return done;
        }
        if (source == null) {
            return super.action(act, performer, target, action, counter);
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private static void add2List(final LinkedList<String> list, final String foundString, final String dir) {
        if (foundString.length() > 0 && !list.contains(foundString + dir)) {
            if (Server.rand.nextBoolean()) {
                list.addFirst(foundString + dir);
            }
            else {
                list.addLast(foundString + dir);
            }
        }
    }
    
    private static String checkTile(final Creature performer, final int tilex, final int tiley, final int radius, final int skillTypeOffset, final int skillQLOffset) {
        String findString = "";
        final int itemTemplate = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)));
        if (itemTemplate != 146) {
            try {
                final int itemSkillOffset = itemToSkillOffset(itemTemplate);
                final ItemTemplate t = ItemTemplateFactory.getInstance().getTemplate(itemTemplate);
                findString = "something, but cannot quite make it out";
                if (skillTypeOffset > itemSkillOffset) {
                    findString = t.getProspectName();
                    TileBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                    final int m = 100;
                    final int max = Math.min(100, 20 + TileBehaviour.r.nextInt(80));
                    findString = TileBehaviour.getShardQlDescription(max) + " " + findString;
                }
                if (radius == 6) {
                    return "an " + radToString(radius) + "trace of " + findString;
                }
                return "a " + radToString(radius) + "trace of " + findString;
            }
            catch (NoSuchTemplateException nst) {
                ShardBehaviour.logger.log(Level.WARNING, performer.getName() + " - " + nst.getMessage() + ": " + itemTemplate + " at " + tilex + ", " + tiley, nst);
            }
        }
        return findString;
    }
    
    private static String checkSaltFlint(final Creature performer, final int tilex, final int tiley, final int radius, final int skillTypeOffset, final boolean salt) {
        String findString = "";
        final int itemTemplate = salt ? 349 : 446;
        try {
            final int itemSkillOffset = salt ? 5 : 2;
            final ItemTemplate t = ItemTemplateFactory.getInstance().getTemplate(itemTemplate);
            findString = "something, but cannot quite make it out";
            if (skillTypeOffset > itemSkillOffset) {
                findString = t.getName();
            }
            if (radius == 6) {
                return "an " + radToString(radius) + "trace of " + findString;
            }
            return "a " + radToString(radius) + "trace of " + findString;
        }
        catch (NoSuchTemplateException nst) {
            ShardBehaviour.logger.log(Level.WARNING, performer.getName() + " - " + nst.getMessage() + ": " + itemTemplate + " at " + tilex + ", " + tiley, nst);
            return findString;
        }
    }
    
    private static String radToString(final int radius) {
        switch (radius) {
            case 2: {
                return "slight ";
            }
            case 3: {
                return "faint ";
            }
            case 4: {
                return "minuscule ";
            }
            case 5: {
                return "vague ";
            }
            case 6: {
                return "indistinct ";
            }
            default: {
                return "";
            }
        }
    }
    
    private static void furtherStudy(final Creature performer, final int radius) {
        switch (radius) {
            case 2: {
                performer.getCommunicator().sendNormalServerMessage("You take a closer look.");
                break;
            }
            case 3: {
                performer.getCommunicator().sendNormalServerMessage("You study it a bit more.");
                break;
            }
            case 4: {
                performer.getCommunicator().sendNormalServerMessage("You study it real hard.");
                break;
            }
            case 5: {
                performer.getCommunicator().sendNormalServerMessage("You peer at it.");
                break;
            }
            default: {
                performer.getCommunicator().sendNormalServerMessage("You go cross-eyed studying it.");
                break;
            }
        }
    }
    
    public static int calcMaxRadius(final double currentSkill) {
        return (int)(currentSkill + 17.0) / 17;
    }
    
    private static float calcTickTime(final Creature performer, final Skill prospecting) {
        return Actions.getQuickActionTime(performer, prospecting, null, 0.0) / 3 * 2;
    }
    
    private static void outputList(final Creature performer, final LinkedList<String> list, final int radius) {
        if (list.isEmpty()) {
            final int x = Server.rand.nextInt(3);
            if (x == 0) {
                performer.getCommunicator().sendNormalServerMessage("You do not notice any unusual " + radToString(radius) + "traces.");
            }
            else if (x == 1) {
                performer.getCommunicator().sendNormalServerMessage("You cannot see anything unusual.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot see any unusual " + radToString(radius) + "traces of anything.");
            }
        }
        else {
            final Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                final int x2 = Server.rand.nextInt(3);
                if (x2 == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You spot " + it.next() + ".");
                }
                else if (x2 == 1) {
                    performer.getCommunicator().sendNormalServerMessage("You notice " + it.next() + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You see " + it.next() + ".");
                }
            }
        }
    }
    
    private static int itemToSkillOffset(final int itemTemplate) {
        switch (itemTemplate) {
            case 38: {
                return 0;
            }
            case 207: {
                return 1;
            }
            case 42: {
                return 2;
            }
            case 41: {
                return 3;
            }
            case 43: {
                return 4;
            }
            case 770: {
                return 6;
            }
            case 40: {
                return 7;
            }
            case 785: {
                return 8;
            }
            case 39: {
                return 9;
            }
            case 697: {
                return 10;
            }
            case 693: {
                return 11;
            }
            case 1116: {
                return 12;
            }
            default: {
                return 0;
            }
        }
    }
    
    static {
        logger = Logger.getLogger(MethodsItems.class.getName());
    }
}
