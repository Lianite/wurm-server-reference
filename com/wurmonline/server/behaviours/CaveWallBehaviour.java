// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.questions.OreQuestion;
import com.wurmonline.server.Players;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.RuneUtilities;
import java.util.Random;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.Servers;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.logging.Level;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import java.util.Collections;
import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class CaveWallBehaviour extends TileBehaviour
{
    private static final Logger logger;
    
    CaveWallBehaviour() {
        super((short)38);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, tilex, tiley, onSurface, tile));
        final byte type = Tiles.decodeType(tile);
        if (performer.getDeity() != null && performer.getDeity().isMountainGod()) {
            Methods.addActionIfAbsent(toReturn, Actions.actionEntrys[141]);
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || isPartlyClad(type)) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, subject, tilex, tiley, onSurface, tile));
        final byte type = Tiles.decodeType(tile);
        if (type != Tiles.Tile.TILE_LAVA.id) {
            if (subject.isMiningtool()) {
                if (!isClad(type)) {
                    toReturn.add(new ActionEntry((short)(-4), "Mining", "Mining options"));
                    toReturn.add(Actions.actionEntrys[145]);
                    toReturn.add(Actions.actionEntrys[147]);
                    toReturn.add(Actions.actionEntrys[146]);
                    toReturn.add(Actions.actionEntrys[156]);
                }
            }
            else if (subject.getTemplateId() == 429 && type == Tiles.Tile.TILE_CAVE_WALL.id) {
                toReturn.add(Actions.actionEntrys[229]);
            }
            else if (subject.getTemplateId() == 525) {
                toReturn.add(Actions.actionEntrys[94]);
            }
            else if (subject.getTemplateId() == 526 || subject.getTemplateId() == 667) {
                if (Tiles.isOreCave(Tiles.decodeType(tile))) {
                    toReturn.add(Actions.actionEntrys[118]);
                }
            }
            else if (subject.getTemplateId() == 668) {
                toReturn.add(Actions.actionEntrys[78]);
            }
            if (performer.getDeity() != null && performer.getDeity().isMountainGod()) {
                Methods.addActionIfAbsent(toReturn, Actions.actionEntrys[141]);
            }
            if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id && (subject.getTemplateId() == 493 || (subject.isWand() && performer.getPower() >= 4))) {
                final List<ActionEntry> reinforce = new LinkedList<ActionEntry>();
                reinforce.add(Actions.actionEntrys[856]);
                reinforce.add(Actions.actionEntrys[857]);
                reinforce.add(Actions.actionEntrys[858]);
                reinforce.add(Actions.actionEntrys[859]);
                reinforce.add(Actions.actionEntrys[860]);
                reinforce.add(Actions.actionEntrys[861]);
                if (subject.isWand() && performer.getPower() >= 4) {
                    reinforce.add(Actions.actionEntrys[862]);
                }
                Collections.sort(reinforce);
                toReturn.add(new ActionEntry((short)(-reinforce.size()), "Clad", "Clad options"));
                toReturn.addAll(reinforce);
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id && (subject.getTemplateId() == 63 || subject.getTemplateId() == 62)) {
                toReturn.add(Actions.actionEntrys[862]);
            }
            else if (subject.getTemplateId() == 493 || (subject.isWand() && performer.getPower() >= 4)) {
                if (type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)856, "Continue cladding", "cladding"));
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)857, "Continue cladding", "cladding"));
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)858, "Continue cladding", "cladding"));
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)859, "Continue cladding", "cladding"));
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)860, "Continue cladding", "cladding"));
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)861, "Continue cladding", "cladding"));
                }
                else if (subject.isWand() && performer.getPower() >= 4 && type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id) {
                    toReturn.add(new ActionEntry((short)862, "Continue cladding", "cladding"));
                }
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id && (subject.getTemplateId() == 63 || subject.getTemplateId() == 62)) {
                toReturn.add(new ActionEntry((short)862, "Continue cladding", "cladding"));
            }
            if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id && (subject.getTemplateId() == 130 || (subject.isWand() && performer.getPower() >= 4))) {
                toReturn.add(Actions.actionEntrys[847]);
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_RENDERED_REINFORCED.id && (subject.getTemplateId() == 1115 || (subject.isWand() && performer.getPower() >= 4))) {
                toReturn.add(new ActionEntry((short)847, "Remove render", "removing"));
            }
            else if (isClad(type) && (subject.getTemplateId() == 1115 || (subject.isWand() && performer.getPower() >= 4))) {
                toReturn.add(new ActionEntry((short)78, "Remove cladding", "removing"));
            }
            if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || isPartlyClad(type)) {
                toReturn.add(Actions.actionEntrys[607]);
            }
        }
        return toReturn;
    }
    
    public static boolean isClad(final byte type) {
        return type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_SLATE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_POTTERY_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_ROUNDED_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_SANDSTONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_RENDERED_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_MARBLE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id;
    }
    
    public static boolean isPartlyClad(final byte type) {
        return type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final int dir, final short action, final float counter) {
        boolean done = true;
        final byte type = Tiles.decodeType(tile);
        if (action == 1) {
            final Communicator comm = performer.getCommunicator();
            if (Tiles.isOreCave(type)) {
                if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_COPPER.id) {
                    comm.sendNormalServerMessage("A vein of pure copper emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_GOLD.id) {
                    comm.sendNormalServerMessage("A vein of pure gold emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_SILVER.id) {
                    comm.sendNormalServerMessage("A vein of pure silver emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_TIN.id) {
                    comm.sendNormalServerMessage("A vein of pure tin emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_IRON.id) {
                    comm.sendNormalServerMessage("A vein of pure iron emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_ZINC.id) {
                    comm.sendNormalServerMessage("A vein of pure zinc emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_LEAD.id) {
                    comm.sendNormalServerMessage("A vein of pure lead emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_ADAMANTINE.id) {
                    comm.sendNormalServerMessage("A vein of black adamantine emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL.id) {
                    comm.sendNormalServerMessage("A vein of shiny glimmersteel emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_MARBLE.id) {
                    comm.sendNormalServerMessage("A vein of an interlocking mosaic of carbonate crystals, otherwise known as marble, emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_SLATE.id) {
                    comm.sendNormalServerMessage("A vein of fine-grained, foliated, homogeneous metamorphic rock, or slate as we know it, emerges here.");
                }
                else if (type == Tiles.Tile.TILE_CAVE_WALL_SANDSTONE.id) {
                    comm.sendNormalServerMessage("A vein of sand-sized minerals or rock grains, composed of quartz or feldspar, because these are the most common minerals in the Wurm's crust.");
                }
                else {
                    comm.sendNormalServerMessage("Unknown vein!");
                }
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams and metal bands.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and planks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and stone bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_SLATE_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and slate bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_ROUNDED_STONE_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and rounded stone bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_POTTERY_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and pottery bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_SANDSTONE_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and sandstone bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_RENDERED_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and stone bricks that have been rendered.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_MARBLE_REINFORCED.id) {
                comm.sendNormalServerMessage("The cave wall has been reinforced with thick wooden beams, metal bands and marble bricks.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_LAVA.id) {
                comm.sendNormalServerMessage("Thick, slow flowing lava blocks your way.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_ROCKSALT.id) {
                comm.sendNormalServerMessage("A vein of crystalized sediment formed over time by evaporation.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_SANDSTONE.id) {
                comm.sendNormalServerMessage("A vein of clastic sedimentary rock composed mainly of sand-sized minerals or rock grains.");
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "plank", false));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "stone brick", true));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "slate brick", true));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "rounded stone brick", true));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "pottery brick", true));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "sandstone brick", true));
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id) {
                comm.sendNormalServerMessage(this.partlyCladExamine(tilex, tiley, "marble brick", true));
            }
            else {
                comm.sendNormalServerMessage("You see dark dungeons.");
            }
        }
        else if (action == 109) {
            performer.getCommunicator().sendNormalServerMessage("You cannot track there.");
        }
        else if (action == 141 && performer.getDeity() != null && performer.getDeity().isMountainGod()) {
            done = MethodsReligion.pray(act, performer, counter);
        }
        else if (action == 607) {
            final long target = Tiles.getTileId(tilex, tiley, 0, false);
            performer.getCommunicator().sendAddCaveWallToCreationWindow(target, type, -10L);
        }
        else {
            done = super.action(act, performer, tilex, tiley, onSurface, tile, action, counter);
        }
        return done;
    }
    
    private String partlyCladExamine(final int tilex, final int tiley, final String brickName, final boolean needsMortar) {
        final int resource = Server.getCaveResource(tilex, tiley);
        final int needs = resource >>> 8;
        final String requires = "  It requires " + needs + " more " + brickName + ((needs > 1) ? "s" : "") + (needsMortar ? " and the same number of mortar" : ((needs == 10) ? " and one large nail" : "")) + ".";
        return "The cave wall has been reinforced with thick wooden beams, metal bands and partly clad with " + brickName + "." + requires;
    }
    
    public static final Blocker checkForBlockersBetween(final TilePos pos, final int tileSurfaceSide, final int floorLevel) {
        final VolaTile posTile = Zones.getTileOrNull(pos, false);
        if (posTile != null) {
            final Wall[] walls2;
            final Wall[] walls = walls2 = posTile.getWalls();
            for (final Wall wall : walls2) {
                if (wall.getFloorLevel() == floorLevel && !wall.isDoor() && isBlockerBlockingSurfaceSide(pos, wall, tileSurfaceSide)) {
                    return wall;
                }
            }
            final Fence[] fences2;
            final Fence[] fences = fences2 = posTile.getFences();
            for (final Fence fence : fences2) {
                if (fence.getFloorLevel() == floorLevel && !fence.isDoor() && isBlockerBlockingSurfaceSide(pos, fence, tileSurfaceSide)) {
                    return fence;
                }
            }
        }
        return null;
    }
    
    public static final boolean isBlockerBlockingSurfaceSide(final TilePos pos, final Blocker blocker, final int surfaceSide) {
        switch (surfaceSide) {
            case 2: {
                return blocker.isOnWestBorder(pos);
            }
            case 5: {
                return blocker.isOnSouthBorder(pos);
            }
            case 3: {
                return blocker.isOnNorthBorder(pos);
            }
            case 4: {
                return blocker.isOnEastBorder(pos);
            }
            default: {
                return false;
            }
        }
    }
    
    public static final Blocker isCaveWallBlocked(final TilePos checkedTile, final int actionSurface, final int floorLevel) {
        TilePos vtile = null;
        switch (actionSurface) {
            case 2: {
                vtile = checkedTile.East();
                break;
            }
            case 5: {
                vtile = checkedTile.North();
                break;
            }
            case 3: {
                vtile = checkedTile.South();
                break;
            }
            case 4: {
                vtile = checkedTile.West();
                break;
            }
            default: {
                return null;
            }
        }
        final Blocker blocker = checkForBlockersBetween(vtile, actionSurface, floorLevel);
        if (blocker != null) {
            return blocker;
        }
        int newSurface = 0;
        switch (actionSurface) {
            case 2: {
                newSurface = 4;
                break;
            }
            case 5: {
                newSurface = 3;
                break;
            }
            case 3: {
                newSurface = 5;
                break;
            }
            case 4: {
                newSurface = 2;
                break;
            }
            default: {
                return null;
            }
        }
        final Blocker blocker2 = checkForBlockersBetween(checkedTile, newSurface, floorLevel);
        if (blocker2 != null) {
            return blocker2;
        }
        return null;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final int dir, final short action, final float counter) {
        boolean done = true;
        final byte type = Tiles.decodeType(tile);
        if (source.isMiningtool() && !isClad(type) && (action == 145 || action == 147 || action == 146)) {
            if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep to mine.", (byte)3);
                return true;
            }
            if (Zones.isTileProtected(tilex, tiley)) {
                performer.getCommunicator().sendNormalServerMessage("This tile is protected by the gods. You can not mine here.", (byte)3);
                return true;
            }
            boolean ok = true;
            if (type != Tiles.Tile.TILE_CAVE_WALL.id && type != Tiles.Tile.TILE_CAVE_WALL_ROCKSALT.id && !Tiles.isOreCave(type)) {
                ok = false;
                if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    if (performer.getPower() >= 2) {
                        CaveWallBehaviour.logger.log(Level.WARNING, performer.getName() + " removed reinforced cave wall at " + tile + " with GM powers.");
                        ok = true;
                    }
                    else {
                        final VolaTile vt = Zones.getOrCreateTile(tilex, tiley, onSurface);
                        final Village vill = vt.getVillage();
                        if (vill == null) {
                            ok = false;
                        }
                        else {
                            if (!Methods.isActionAllowed(performer, (short)229, false, tilex, tiley, tile, 2) || !Methods.isActionAllowed(performer, (short)145, false, tilex, tiley, tile, 2)) {
                                return true;
                            }
                            ok = true;
                        }
                    }
                }
                else if (!Methods.isActionAllowed(performer, (short)145, false, tilex, tiley, tile, 2)) {
                    return true;
                }
                if (!ok) {
                    performer.getCommunicator().sendNormalServerMessage("The rock is too hard to mine.", (byte)3);
                    return true;
                }
            }
            if (performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("You are too far away to mine there.", (byte)3);
                return true;
            }
            final int digTilex = performer.getTileX();
            final int digTiley = performer.getTileY();
            if (dir == 1) {
                performer.getCommunicator().sendNormalServerMessage("The rock is too hard to mine.", (byte)3);
                CaveWallBehaviour.logger.log(Level.WARNING, performer.getName() + " Tried to mine the roof of a cave wall.");
                return true;
            }
            final Blocker blocker = isCaveWallBlocked(TilePos.fromXY(tilex, tiley), dir, performer.getFloorLevel());
            if (blocker != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + blocker.getName() + " is in the way.", (byte)3);
                if (performer.getPower() > 0) {
                    performer.getCommunicator().sendNormalServerMessage(blocker.getName() + " is at " + blocker.getPositionX() + ", " + blocker.getPositionY() + " horiz=" + blocker.isHorizontal() + " wurmid=" + blocker.getId());
                }
                return done;
            }
            final short h = Tiles.decodeHeight(tile);
            if (h >= -25 || h == -100) {
                done = false;
                final Skills skills = performer.getSkills();
                Skill mining = null;
                Skill tool = null;
                final boolean insta = performer.getPower() >= 2 && source.isWand();
                try {
                    mining = skills.getSkill(1008);
                }
                catch (Exception ex2) {
                    mining = skills.learn(1008, 1.0f);
                }
                try {
                    tool = skills.getSkill(source.getPrimarySkill());
                }
                catch (Exception ex2) {
                    try {
                        tool = skills.learn(source.getPrimarySkill(), 1.0f);
                    }
                    catch (NoSuchSkillException nse) {
                        CaveWallBehaviour.logger.log(Level.WARNING, performer.getName() + " trying to mine with an item with no primary skill: " + source.getName());
                    }
                }
                int time = 0;
                if (counter == 1.0f) {
                    if (!Methods.isActionAllowed(performer, action, false, tilex, tiley, tile, dir)) {
                        return true;
                    }
                    final VolaTile dropTile = Zones.getTileOrNull(performer.getTilePos(), onSurface);
                    if (dropTile != null && dropTile.getNumberOfItems(performer.getFloorLevel()) > 99) {
                        performer.getCommunicator().sendNormalServerMessage("There is no space to mine here. Clear the area first.", (byte)3);
                        return true;
                    }
                    time = Actions.getVariableActionTime(performer, mining, source, 0.0, 250, 25, 8000);
                    try {
                        performer.getCurrentAction().setTimeLeft(time);
                    }
                    catch (NoSuchActionException nsa) {
                        CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                    }
                    String dirstring = "";
                    if (action == 147) {
                        dirstring = " down";
                    }
                    else if (action == 146) {
                        dirstring = " up";
                    }
                    Server.getInstance().broadCastAction(performer.getName() + " starts mining" + dirstring + ".", performer, 5);
                    performer.getCommunicator().sendNormalServerMessage("You start to mine" + dirstring + ".");
                    performer.sendActionControl(Actions.actionEntrys[145].getVerbString() + " " + dirstring, true, time);
                    source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else {
                    try {
                        time = performer.getCurrentAction().getTimeLeft();
                    }
                    catch (NoSuchActionException nsa2) {
                        CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa2);
                    }
                    if (counter * 10.0f <= time && !insta) {
                        if (act.currentSecond() % 5 == 0 || (act.currentSecond() == 3 && time < 50)) {
                            String sstring = "sound.work.mining1";
                            final int x = Server.rand.nextInt(3);
                            if (x == 0) {
                                sstring = "sound.work.mining2";
                            }
                            else if (x == 1) {
                                sstring = "sound.work.mining3";
                            }
                            SoundPlayer.playSound(sstring, tilex, tiley, performer.isOnSurface(), 1.0f);
                            source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                            performer.getStatus().modifyStamina(-7000.0f);
                        }
                    }
                    else {
                        if (act.getRarity() != 0) {
                            performer.playPersonalSound("sound.fx.drumroll");
                        }
                        final VolaTile dropTile = Zones.getTileOrNull(performer.getTilePos(), onSurface);
                        if (dropTile != null && dropTile.getNumberOfItems(performer.getFloorLevel()) > 99) {
                            performer.getCommunicator().sendNormalServerMessage("There is no space to mine here. Clear the area first.", (byte)3);
                            return true;
                        }
                        double bonus = 0.0;
                        double power = 0.0;
                        done = true;
                        int resource = Server.getCaveResource(tilex, tiley);
                        if (resource == 65535) {
                            resource = Server.rand.nextInt(10000);
                        }
                        final int itemTemplateCreated = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(tile));
                        if (resource > 50 && (itemTemplateCreated == 693 || itemTemplateCreated == 697)) {
                            resource = 50;
                        }
                        final float diff = TileRockBehaviour.getDifficultyForTile(Tiles.decodeType(tile));
                        if (resource > 0) {
                            --resource;
                        }
                        Server.setCaveResource(tilex, tiley, resource);
                        boolean dryrun = false;
                        byte state = Zones.getMiningState(tilex, tiley);
                        boolean createItem = true;
                        final int randint = Server.rand.nextInt(10);
                        final boolean normal = itemTemplateCreated == 146 || itemTemplateCreated == 1238;
                        if (normal && state > Servers.localServer.getTunnelingHits() && state <= Servers.localServer.getTunnelingHits() + 5 + randint && !TileRockBehaviour.isInsideTunnelOk(tilex, tiley, tile, action, dir, performer, false)) {
                            dryrun = true;
                            createItem = false;
                        }
                        if ((normal && state > Servers.localServer.getTunnelingHits() + randint) || (!normal && resource <= 0) || insta) {
                            if (!TileRockBehaviour.createInsideTunnel(tilex, tiley, tile, performer, action, dir, false, act)) {
                                dryrun = true;
                                if (!normal || Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                                    TileEvent.log(tilex, tiley, -1, performer.getWurmId(), action);
                                    if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                                        performer.getCommunicator().sendNormalServerMessage("You manage to remove the reinforcement.");
                                    }
                                    else {
                                        performer.getCommunicator().sendNormalServerMessage("The mine is now depleted.", (byte)3);
                                    }
                                    Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(tile)));
                                    TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
                                }
                                createItem = false;
                            }
                            else {
                                if (itemTemplateCreated == 1238 || itemTemplateCreated == 1116) {
                                    Zones.setMiningState(tilex, tiley, (byte)(-1), false);
                                }
                                else {
                                    Zones.setMiningState(tilex, tiley, (byte)0, false);
                                    Zones.deleteMiningTile(tilex, tiley);
                                }
                                performer.getCommunicator().sendNormalServerMessage("The wall breaks!");
                                final Random rockRandom = new Random();
                                rockRandom.setSeed((tilex + tiley * Zones.worldTileSizeY) * TileRockBehaviour.SOURCE_PRIME);
                                if (rockRandom.nextInt(TileRockBehaviour.sourceFactor) == 0) {
                                    TileRockBehaviour.SOURCE_PRIME += Server.rand.nextInt(10000);
                                }
                            }
                        }
                        else if (normal) {
                            if (state < 56) {
                                ++state;
                                if (state >= Servers.localServer.getTunnelingHits()) {
                                    performer.getCommunicator().sendNormalServerMessage("The wall will break soon.");
                                }
                            }
                            Zones.setMiningState(tilex, tiley, state, false);
                        }
                        else if (resource < 5) {
                            performer.getCommunicator().sendNormalServerMessage("The wall will break soon.");
                        }
                        final boolean useNewSystem = false;
                        if (tool != null) {
                            bonus = tool.skillCheck(diff, source, 0.0, dryrun, counter) / 5.0;
                        }
                        power = Math.max(1.0, mining.skillCheck(diff, source, bonus, dryrun, counter));
                        if (performer.getTutorialLevel() == 10 && !performer.skippedTutorial()) {
                            performer.missionFinished(true, true);
                        }
                        if (createItem) {
                            try {
                                final double imbueEnhancement = 1.0 + 0.23047 * source.getSkillSpellImprovement(1008) / 100.0;
                                if (mining.getKnowledge(0.0) * imbueEnhancement < power) {
                                    power = mining.getKnowledge(0.0) * imbueEnhancement;
                                }
                                CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                                int m = TileRockBehaviour.MAX_QL;
                                if (normal || itemTemplateCreated == 38) {
                                    m = 100;
                                }
                                int max = (int)Math.min(m, (20 + CaveWallBehaviour.r.nextInt(80)) * imbueEnhancement);
                                if (state == -1) {
                                    max = 99;
                                }
                                power = Math.min(power, max);
                                if (source.isCrude()) {
                                    power = 1.0;
                                }
                                float modifier = 1.0f;
                                if (source.getSpellEffects() != null) {
                                    modifier *= source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                                }
                                final float orePower = GeneralUtilities.calcOreRareQuality(power * modifier, act.getRarity(), source.getRarity());
                                final Item newItem = ItemFactory.createItem(itemTemplateCreated, orePower, act.getRarity(), null);
                                if (itemTemplateCreated == 39) {
                                    performer.achievement(372);
                                }
                                newItem.setLastOwnerId(performer.getWurmId());
                                newItem.setDataXY(tilex, tiley);
                                newItem.putItemInfrontof(performer, 0.0f);
                                performer.getCommunicator().sendNormalServerMessage("You mine some " + newItem.getName() + ".");
                                Server.getInstance().broadCastAction(performer.getName() + " mines some " + newItem.getName() + ".", performer, 5);
                                TileRockBehaviour.createGem(tilex, tiley, digTilex, digTiley, performer, power, false, act);
                                if (performer.getDeity() != null && performer.getDeity().number == 2) {
                                    performer.maybeModifyAlignment(0.5f);
                                }
                                if (itemTemplateCreated == 38) {
                                    performer.achievement(516);
                                    PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.MINE_IRON);
                                }
                                else if (itemTemplateCreated == 43) {
                                    performer.achievement(527);
                                }
                                else if (itemTemplateCreated == 207) {
                                    performer.achievement(528);
                                }
                                if (newItem.isMetal() && newItem.getCurrentQualityLevel() >= 80.0f) {
                                    performer.achievement(603);
                                }
                            }
                            catch (Exception ex) {
                                CaveWallBehaviour.logger.log(Level.WARNING, "Factory failed to produce item", ex);
                            }
                        }
                    }
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep to mine.", (byte)3);
            }
        }
        else if (source.isMiningtool() && action == 156) {
            if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep to prospect.", (byte)3);
                return true;
            }
            final Blocker blocker2 = isCaveWallBlocked(TilePos.fromXY(tilex, tiley), dir, performer.getFloorLevel());
            if (blocker2 != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + blocker2.getName() + " is in the way.", (byte)3);
                return done;
            }
            final Skills skills2 = performer.getSkills();
            Skill prospecting = null;
            done = false;
            try {
                prospecting = skills2.getSkill(10032);
            }
            catch (Exception ex3) {
                prospecting = skills2.learn(10032, 1.0f);
            }
            int time2 = 0;
            if (counter == 1.0f) {
                String sstring2 = "sound.work.prospecting1";
                final int x2 = Server.rand.nextInt(3);
                if (x2 == 0) {
                    sstring2 = "sound.work.prospecting2";
                }
                else if (x2 == 1) {
                    sstring2 = "sound.work.prospecting3";
                }
                SoundPlayer.playSound(sstring2, tilex, tiley, performer.isOnSurface(), 1.0f);
                time2 = (int)Math.max(30.0, 100.0 - prospecting.getKnowledge(source, 0.0));
                try {
                    performer.getCurrentAction().setTimeLeft(time2);
                }
                catch (NoSuchActionException nsa3) {
                    CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa3);
                }
                performer.getCommunicator().sendNormalServerMessage("You start to gather fragments of the rock.");
                Server.getInstance().broadCastAction(performer.getName() + " starts gathering fragments of the rock.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[156].getVerbString(), true, time2);
            }
            else {
                try {
                    time2 = performer.getCurrentAction().getTimeLeft();
                }
                catch (NoSuchActionException nsa4) {
                    CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa4);
                }
            }
            if (counter * 10.0f > time2) {
                performer.getStatus().modifyStamina(-3000.0f);
                prospecting.skillCheck(1.0, source, 0.0, false, counter);
                source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                done = true;
                int resource2 = Server.getCaveResource(tilex, tiley);
                if (resource2 == 65535) {
                    resource2 = Server.rand.nextInt(10000);
                    Server.setCaveResource(tilex, tiley, resource2);
                }
                final int itemTemplate = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(tile));
                try {
                    final ItemTemplate t = ItemTemplateFactory.getInstance().getTemplate(itemTemplate);
                    if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || isClad(type)) {
                        performer.getCommunicator().sendNormalServerMessage("This wall is reinforced and may not be mined.");
                    }
                    else if (type == Tiles.Tile.TILE_CAVE_WALL_ROCKSALT.id) {
                        performer.getCommunicator().sendNormalServerMessage("You would mine " + t.getName() + " here.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You would mine " + MaterialUtilities.getMaterialString(t.getMaterial()) + " " + t.getName() + " here.");
                    }
                }
                catch (NoSuchTemplateException nst) {
                    CaveWallBehaviour.logger.log(Level.WARNING, performer.getName() + " - " + nst.getMessage() + ": " + itemTemplate + " at " + tilex + ", " + tiley);
                }
                if (type != Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id && !isClad(type)) {
                    if (prospecting.getKnowledge(0.0) > 20.0) {
                        CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                        int i = TileRockBehaviour.MAX_QL;
                        if (itemTemplate == 146 || itemTemplate == 1238 || itemTemplate == 1116 || itemTemplate == 38) {
                            i = 100;
                        }
                        int max2 = Math.min(i, 20 + CaveWallBehaviour.r.nextInt(80));
                        final byte state2 = Zones.getMiningState(tilex, tiley);
                        if (state2 == -1) {
                            max2 = 99;
                        }
                        if (prospecting.getKnowledge(0.0) > 80.0) {
                            performer.getCommunicator().sendNormalServerMessage("It has a max quality of " + max2 + ".");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("It is of " + TileBehaviour.getShardQlDescription(max2) + ".");
                        }
                    }
                    if (prospecting.getKnowledge(0.0) > 60.0 && itemTemplate != 146 && itemTemplate != 1238) {
                        if (prospecting.getKnowledge(0.0) > 90.0) {
                            performer.getCommunicator().sendNormalServerMessage("You will be able to mine here " + Server.getCaveResource(tilex, tiley) + " more times.");
                        }
                        else {
                            String numString = "There is plenty of ore left.";
                            if (resource2 > 10000) {
                                numString = "There is plenty of ore left.";
                            }
                            else if (resource2 > 5000) {
                                numString = "Only a few weeks mining remain here.";
                            }
                            else if (resource2 > 3000) {
                                numString = "The ore is starting to deplete.";
                            }
                            else if (resource2 > 1000) {
                                numString = "You should start to prospect for another vein of this ore.";
                            }
                            else if (resource2 > 100) {
                                numString = "The ore will run out soon.";
                            }
                            else {
                                numString = "The ore will run out any hour.";
                            }
                            performer.getCommunicator().sendNormalServerMessage(numString);
                        }
                    }
                    if (prospecting.getKnowledge(0.0) > 40.0) {
                        CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 102533L);
                        final boolean saltExists = CaveWallBehaviour.r.nextInt(100) == 0;
                        if (saltExists) {
                            performer.getCommunicator().sendNormalServerMessage("You will find salt here!");
                        }
                    }
                    if (prospecting.getKnowledge(0.0) > 20.0) {
                        CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 6883L);
                        if (CaveWallBehaviour.r.nextInt(200) == 0) {
                            performer.getCommunicator().sendNormalServerMessage("You will find flint here!");
                        }
                    }
                }
            }
        }
        else if (source.getTemplateId() == 429 && action == 229) {
            if (Tiles.decodeType(tile) != Tiles.Tile.TILE_CAVE_WALL.id) {
                if (Tiles.isOreCave(Tiles.decodeType(tile))) {
                    performer.getCommunicator().sendNormalServerMessage("You can not reinforce ore veins.", (byte)3);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The wall does not need reinforcing.", (byte)3);
                }
                return true;
            }
            if (!Methods.isActionAllowed(performer, (short)229, false, tilex, tiley, tile, 2) || !Methods.isActionAllowed(performer, (short)145, false, tilex, tiley, tile, 2)) {
                return true;
            }
            if (!GeneralUtilities.isValidTileLocation(tilex, tiley)) {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep here.", (byte)3);
                return true;
            }
            done = false;
            final Skills skills3 = performer.getSkills();
            Skill mining2 = null;
            final boolean insta2 = performer.getPower() > 3;
            try {
                mining2 = skills3.getSkill(1008);
            }
            catch (Exception ex3) {
                mining2 = skills3.learn(1008, 1.0f);
            }
            int time2 = 0;
            if (counter == 1.0f) {
                SoundPlayer.playSound("sound.work.masonry", tilex, tiley, performer.isOnSurface(), 1.0f);
                time2 = Math.min(250, Actions.getStandardActionTime(performer, mining2, source, 0.0));
                try {
                    performer.getCurrentAction().setTimeLeft(time2);
                }
                catch (NoSuchActionException nsa4) {
                    CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa4);
                }
                performer.getCommunicator().sendNormalServerMessage("You start to reinforce the rock wall.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to reinforce the rock wall.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[229].getVerbString(), true, time2);
            }
            else {
                try {
                    time2 = performer.getCurrentAction().getTimeLeft();
                }
                catch (NoSuchActionException nsa4) {
                    CaveWallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa4);
                }
            }
            if (counter * 10.0f > time2 || insta2) {
                mining2.skillCheck(20.0, source, 0.0, false, counter);
                done = true;
                performer.getCommunicator().sendNormalServerMessage("You reinforce the wall.");
                Server.getInstance().broadCastAction(performer.getName() + " reinforces the rock wall.", performer, 5);
                Items.destroyItem(source.getWurmId());
                Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id, Tiles.decodeData(tile)));
                Zones.setMiningState(tilex, tiley, (byte)(-1), false);
                Players.getInstance().sendChangedTile(tilex, tiley, false, true);
            }
            return done;
        }
        else if (action == 78 && source.getTemplateId() == 668) {
            done = true;
            if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL.id) {
                final OreQuestion ctq = new OreQuestion(performer, tilex, tiley, source);
                ctq.sendQuestion();
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("This rod only works on normal cave walls.");
            }
        }
        else if (action == 118 && (source.getTemplateId() == 526 || source.getTemplateId() == 667)) {
            done = true;
            if (source.getTemplateId() == 526) {
                performer.getCommunicator().sendNormalServerMessage("You draw a circle in the air in front of you with " + source.getNameWithGenus() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " draws a circle in the air in front of " + performer.getHimHerItString() + " with " + source.getNameWithGenus() + ".", performer, 5);
                if (Tiles.isOreCave(Tiles.decodeType(tile))) {
                    if (source.getAuxData() > 0) {
                        final int digTilex2 = (int)performer.getStatus().getPositionX() + 2 >> 2;
                        final int digTiley2 = (int)performer.getStatus().getPositionY() + 2 >> 2;
                        if (digTilex2 > tilex + 1 || digTilex2 < tilex || digTiley2 > tiley + 1 || digTiley2 < tiley) {
                            performer.getCommunicator().sendNormalServerMessage("You are too far away to use the wand.");
                            return true;
                        }
                        final int itemTemplateCreated2 = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(tile));
                        CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                        int j = TileRockBehaviour.MAX_QL;
                        if (itemTemplateCreated2 == 146 || itemTemplateCreated2 == 38) {
                            j = 100;
                        }
                        int max3 = Math.min(j, 20 + CaveWallBehaviour.r.nextInt(80));
                        final byte state3 = Zones.getMiningState(tilex, tiley);
                        if (state3 == -1) {
                            max3 = 99;
                        }
                        float power2 = max3;
                        if (source.isCrude()) {
                            power2 = 1.0f;
                        }
                        int resource3 = Server.getCaveResource(tilex, tiley);
                        if (resource3 == 65535) {
                            resource3 = Server.rand.nextInt(10000);
                        }
                        if (resource3 > 10) {
                            Server.setCaveResource(tilex, tiley, Math.max(1, resource3 - 10));
                            for (int x3 = 0; x3 < 10; ++x3) {
                                try {
                                    final Item newItem2 = ItemFactory.createItem(itemTemplateCreated2, power2, performer.getStatus().getPositionX(), performer.getStatus().getPositionY(), Server.rand.nextFloat() * 360.0f, false, act.getRarity(), -10L, null);
                                    newItem2.setLastOwnerId(performer.getWurmId());
                                    newItem2.setDataXY(tilex, tiley);
                                }
                                catch (Exception ex2) {
                                    CaveWallBehaviour.logger.log(Level.WARNING, "Factory failed to produce item", ex2);
                                }
                            }
                            TileRockBehaviour.createGem(digTilex2, digTiley2, performer, power2, false, act);
                            source.setAuxData((byte)(source.getAuxData() - 1));
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " fails you.");
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                    }
                }
            }
            else if (Tiles.isOreCave(Tiles.decodeType(tile))) {
                int resource4 = Server.getCaveResource(tilex, tiley);
                if (resource4 == 65535) {
                    resource4 = Server.rand.nextInt(10000);
                }
                if (resource4 > 10) {
                    Server.setCaveResource(tilex, tiley, Math.max(1, resource4 - 10));
                }
                final byte state4 = Zones.getMiningState(tilex, tiley);
                final int itemTemplateCreated2 = TileRockBehaviour.getItemTemplateForTile(Tiles.decodeType(tile));
                CaveWallBehaviour.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 789221L);
                int j = TileRockBehaviour.MAX_QL;
                if (itemTemplateCreated2 == 146 || itemTemplateCreated2 == 38) {
                    j = 100;
                }
                int max3 = Math.min(j, 20 + CaveWallBehaviour.r.nextInt(80));
                if (state4 == -1) {
                    max3 = 99;
                }
                performer.getCommunicator().sendNormalServerMessage("You ding the " + source.getName() + " on the wall. From the sounds you realize that the ore here has a max quality of " + max3 + ". You will be able to mine here " + Server.getCaveResource(tilex, tiley) + " more times.");
                Items.destroyItem(source.getWurmId());
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("Nothing happens as you ding the " + source.getName() + " on the wall.");
            }
        }
        else if (action == 856) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 132, Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id);
            }
        }
        else if (action == 857) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 1123, Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_SLATE_REINFORCED.id);
            }
        }
        else if (action == 858) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 776, Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_POTTERY_REINFORCED.id);
            }
        }
        else if (action == 859) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 1122, Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_ROUNDED_STONE_REINFORCED.id);
            }
        }
        else if (action == 860) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 1121, Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_SANDSTONE_REINFORCED.id);
            }
        }
        else if (action == 861) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id) && (source.getTemplateId() == 493 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 786, Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_MARBLE_REINFORCED.id);
            }
        }
        else if (action == 862) {
            if ((type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id || type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id) && (source.getTemplateId() == 63 || source.getTemplateId() == 62 || (source.isWand() && performer.getPower() >= 4))) {
                done = clad(act, performer, source, tilex, tiley, tile, action, counter, 22, Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id);
            }
        }
        else if (action == 78 && isClad(type) && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
            done = removeCladding(act, performer, source, tilex, tiley, tile, action, counter);
        }
        else if (action == 78 && type == Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id && (source.getTemplateId() == 219 || (source.isWand() && performer.getPower() >= 4))) {
            done = removeCladding(act, performer, source, tilex, tiley, tile, action, counter);
        }
        else if (action == 847) {
            if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id && (source.getTemplateId() == 130 || (source.isWand() && performer.getPower() >= 4))) {
                done = toggleRenderWall(act, performer, source, tilex, tiley, tile, action, counter);
            }
            else if (type == Tiles.Tile.TILE_CAVE_WALL_RENDERED_REINFORCED.id && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
                done = toggleRenderWall(act, performer, source, tilex, tiley, tile, action, counter);
            }
        }
        else if (action == 141 && performer.getDeity() != null && performer.getDeity().isMountainGod()) {
            done = MethodsReligion.pray(act, performer, counter);
        }
        else if (action == 109) {
            performer.getCommunicator().sendNormalServerMessage("You cannot track there.");
        }
        else if (action == 607) {
            final long target = Tiles.getTileId(tilex, tiley, 0, false);
            performer.getCommunicator().sendAddCaveWallToCreationWindow(target, type, -10L);
        }
        else {
            done = super.action(act, performer, source, tilex, tiley, onSurface, heightOffset, tile, action, counter);
        }
        return done;
    }
    
    static boolean toggleRenderWall(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final short action, final float counter) {
        final boolean insta = source.isWand() && performer.getPower() >= 4;
        final byte type = Tiles.decodeType(tile);
        if (!Methods.isActionAllowed(performer, (short)229, tilex, tiley) || !Methods.isActionAllowed(performer, (short)116, tilex, tiley)) {
            return true;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id && !insta && source.getWeightGrams() < 10000) {
            performer.getCommunicator().sendNormalServerMessage("It takes 10kg of " + source.getName() + " to render the wall.");
            return true;
        }
        int time = 40;
        String render;
        String renders;
        String walltype;
        if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id) {
            render = "render";
            renders = "renders";
            walltype = "stone reinforced wall";
        }
        else {
            render = "remove the render from";
            renders = "removes the render from";
            walltype = "rendered reinforced wall";
        }
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl("Rendering wall", true, time);
            performer.getCommunicator().sendNormalServerMessage("You start to " + render + " the " + walltype + ".");
            Server.getInstance().broadCastAction(StringUtil.format("%s starts to " + render + " the " + walltype + ".", performer.getName()), performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time || insta) {
            performer.getCommunicator().sendNormalServerMessage("You " + render + " the " + walltype + ".");
            Server.getInstance().broadCastAction(StringUtil.format("%s " + renders + " the %s.", performer.getName(), walltype), performer, 5);
            if (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id && !insta) {
                source.setWeight(source.getWeightGrams() - 10000, true);
            }
            final byte newWallType = (type == Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id) ? Tiles.Tile.TILE_CAVE_WALL_RENDERED_REINFORCED.id : Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id;
            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newWallType, Tiles.decodeData(tile)));
            TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
            return true;
        }
        return false;
    }
    
    static boolean removeCladding(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final short action, final float counter) {
        final boolean insta = source.isWand() && performer.getPower() >= 4;
        final byte type = Tiles.decodeType(tile);
        if (!Methods.isActionAllowed(performer, (short)229, tilex, tiley) || !Methods.isActionAllowed(performer, (short)145, tilex, tiley)) {
            return true;
        }
        int time = 400;
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl("Removing cladding", true, time);
            performer.getCommunicator().sendNormalServerMessage("You start to remove the cladding from the " + Tiles.getTile(type).getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to remove the cladding from the " + Tiles.getTile(type).getName() + ".", performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (!insta && act.currentSecond() % 5 == 0) {
            SoundPlayer.playSound((Server.rand.nextInt(2) == 0) ? "sound.work.carpentry.mallet1" : "sound.work.carpentry.mallet2", tilex, tiley, performer.isOnSurface(), 1.6f);
            performer.getStatus().modifyStamina(-10000.0f);
            source.setDamage(source.getDamage() + 0.001f * source.getDamageModifier());
        }
        if (counter * 10.0f > time || insta) {
            performer.getCommunicator().sendNormalServerMessage("You remove the cladding from the " + Tiles.getTile(type).getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " removes the chadding from the " + Tiles.getTile(type).getName() + ".", performer, 5);
            final byte newWallType = Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id;
            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newWallType, Tiles.decodeData(tile)));
            TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
            return true;
        }
        return false;
    }
    
    static boolean clad(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final short action, final float counter, final int brick, final byte partwalltype, final byte finishedWallType) {
        final boolean insta = source.isWand() && performer.getPower() >= 4;
        final byte tiletype = Tiles.decodeType(tile);
        if (!Methods.isActionAllowed(performer, (short)229, tilex, tiley) || !Methods.isActionAllowed(performer, (short)116, tilex, tiley)) {
            return true;
        }
        final int skillTemplate = (brick == 22) ? 1005 : 1013;
        final Skill skill = performer.getSkills().getSkillOrLearn(skillTemplate);
        Skill toolskill = null;
        try {
            toolskill = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
        }
        catch (NoSuchSkillException ex) {}
        if (skillTemplate == 1013 && skill.getKnowledge(0.0) < 30.0) {
            performer.getCommunicator().sendNormalServerMessage("You need at least 30 masonry to clad reinforced walls with stone.");
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        final int resource = Server.getCaveResource(tilex, tiley);
        int needs = resource >>> 8;
        if (tiletype == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
            needs = 10;
            Server.setCaveResource(tilex, tiley, (needs << 8) + (resource & 0xF));
            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), partwalltype, Tiles.decodeData(tile)));
            TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
            final long target = Tiles.getTileId(tilex, tiley, 0, false);
            performer.getCommunicator().sendAddCaveWallToCreationWindow(target, partwalltype, target);
        }
        final int[] templatesNeeded = getTemplatesNeeded(tilex, tiley, partwalltype);
        if (!insta && !hasTemplateItems(performer, templatesNeeded)) {
            return true;
        }
        int time = 40;
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl("Cladding reinforced wall", true, time);
            performer.getCommunicator().sendNormalServerMessage("You start to clad the reinforced wall.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to clad the reinforced wall.", performer, 5);
        }
        else {
            time = act.getTimeLeft();
        }
        if (act.currentSecond() % 5 == 0) {
            if (source.getTemplateId() == 493) {
                SoundPlayer.playSound("sound.work.masonry", tilex, tiley, performer.isOnSurface(), 1.6f);
            }
            else {
                SoundPlayer.playSound((Server.rand.nextInt(2) == 0) ? "sound.work.carpentry.mallet1" : "sound.work.carpentry.mallet2", tilex, tiley, performer.isOnSurface(), 1.6f);
            }
            performer.getStatus().modifyStamina(-10000.0f);
            if (source.getTemplateId() == 63) {
                source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
            }
            else if (source.getTemplateId() == 62) {
                source.setDamage(source.getDamage() + 3.0E-4f * source.getDamageModifier());
            }
            else if (source.getTemplateId() == 493) {
                source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
            }
        }
        if (counter * 10.0f <= time && !insta) {
            return false;
        }
        if (!insta && !depleteTemplateItems(performer, templatesNeeded, act)) {
            return true;
        }
        if (insta) {
            performer.getCommunicator().sendNormalServerMessage("You use the wand and summon the required materials and add those to the wall.");
            act.setPower(50.0f);
            if (needs > 1) {
                needs = 1;
            }
            else {
                needs = 0;
            }
        }
        else {
            --needs;
        }
        Server.setCaveResource(tilex, tiley, (needs << 8) + (resource & 0xF));
        if (!insta) {
            double bonus = 0.0;
            if (toolskill != null) {
                toolskill.skillCheck(10.0, source, 0.0, false, counter);
                bonus = toolskill.getKnowledge(source, 0.0) / 10.0;
            }
            skill.skillCheck(10.0, source, bonus, false, counter);
        }
        final long target2 = Tiles.getTileId(tilex, tiley, 0, false);
        if (needs <= 0) {
            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), finishedWallType, Tiles.decodeData(tile)));
            TileRockBehaviour.sendCaveTile(tilex, tiley, 0, 0);
            performer.getCommunicator().sendRemoveFromCreationWindow(target2);
        }
        else {
            performer.getCommunicator().sendAddCaveWallToCreationWindow(target2, partwalltype, target2);
        }
        return true;
    }
    
    static final boolean hasTemplateItems(final Creature performer, final int[] neededTemplates) {
        boolean found = true;
        final Item[] inventoryItems = performer.getInventory().getAllItems(false);
        final Item[] bodyItems = performer.getBody().getAllItems();
        final int[] foundTemplates = getNotInitializedIntArray(neededTemplates.length);
        for (int x = 0; x < inventoryItems.length; ++x) {
            for (int n = 0; n < neededTemplates.length; ++n) {
                if (inventoryItems[x].getTemplateId() == neededTemplates[n]) {
                    final int neededTemplateWeightGrams = getItemTemplateWeightInGrams(inventoryItems[x].getTemplateId());
                    if (neededTemplateWeightGrams <= inventoryItems[x].getWeightGrams()) {
                        foundTemplates[n] = neededTemplates[n];
                    }
                }
            }
        }
        for (int f = 0; f < foundTemplates.length; ++f) {
            if (foundTemplates[f] == -1) {
                found = false;
            }
        }
        if (!found) {
            for (int x = 0; x < bodyItems.length; ++x) {
                for (int n = 0; n < neededTemplates.length; ++n) {
                    if (bodyItems[x].getTemplateId() == neededTemplates[n]) {
                        final int neededTemplateWeightGrams = getItemTemplateWeightInGrams(bodyItems[x].getTemplateId());
                        if (neededTemplateWeightGrams <= bodyItems[x].getWeightGrams()) {
                            foundTemplates[n] = neededTemplates[n];
                        }
                    }
                }
            }
            found = true;
            for (int f = 0; f < foundTemplates.length; ++f) {
                if (foundTemplates[f] == -1) {
                    found = false;
                }
            }
        }
        if (!found) {
            for (int n2 = 0; n2 < foundTemplates.length; ++n2) {
                if (foundTemplates[n2] == -1) {
                    try {
                        if (neededTemplates[n2] == 217) {
                            performer.getCommunicator().sendNormalServerMessage("You need large iron nails.");
                        }
                        else {
                            final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(neededTemplates[n2]);
                            if (template.isCombine()) {
                                performer.getCommunicator().sendNormalServerMessage("You need " + template.getName() + ".");
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage("You need " + template.getNameWithGenus() + ".");
                            }
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        CaveWallBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    static final boolean depleteTemplateItems(final Creature performer, final int[] neededTemplates, final Action act) {
        final Item[] inventoryItems = performer.getInventory().getAllItems(false);
        final Item[] bodyItems = performer.getBody().getAllItems();
        float qlevel = 0.0f;
        final int[] foundTemplates = getNotInitializedIntArray(neededTemplates.length);
        final Item[] depleteItems = new Item[neededTemplates.length];
        for (int i = 0; i < neededTemplates.length; ++i) {
            if (foundTemplates[i] == -1) {
                for (int j = 0; j < inventoryItems.length; ++j) {
                    if (inventoryItems[j].getTemplateId() == neededTemplates[i]) {
                        final int neededTemplateWeightGrams = getItemTemplateWeightInGrams(inventoryItems[j].getTemplateId());
                        if (neededTemplateWeightGrams <= inventoryItems[j].getWeightGrams()) {
                            depleteItems[i] = inventoryItems[j];
                            foundTemplates[i] = neededTemplates[i];
                            break;
                        }
                    }
                }
            }
        }
        boolean allInitialized = true;
        for (int k = 0; k < foundTemplates.length; ++k) {
            if (foundTemplates[k] == -1) {
                allInitialized = false;
                break;
            }
        }
        if (!allInitialized) {
            for (int k = 0; k < neededTemplates.length; ++k) {
                if (foundTemplates[k] == -1) {
                    for (int l = 0; l < bodyItems.length; ++l) {
                        if (bodyItems[l].getTemplateId() == neededTemplates[k]) {
                            final int neededTemplateWeightGrams2 = getItemTemplateWeightInGrams(bodyItems[l].getTemplateId());
                            if (neededTemplateWeightGrams2 <= bodyItems[l].getWeightGrams()) {
                                depleteItems[k] = bodyItems[l];
                                foundTemplates[k] = neededTemplates[k];
                                break;
                            }
                        }
                    }
                }
            }
            allInitialized = true;
            for (int k = 0; k < foundTemplates.length; ++k) {
                if (foundTemplates[k] == -1) {
                    allInitialized = false;
                    break;
                }
            }
        }
        if (!allInitialized) {
            for (int k = 0; k < foundTemplates.length; ++k) {
                if (foundTemplates[k] == -1) {
                    try {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(neededTemplates[k]);
                        performer.getCommunicator().sendNormalServerMessage("You did not have enough " + template.getPlural() + ".");
                    }
                    catch (NoSuchTemplateException nst) {
                        CaveWallBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
            return false;
        }
        final StringBuilder buf = new StringBuilder();
        for (int m = 0; m < depleteItems.length; ++m) {
            try {
                final ItemTemplate template2 = ItemTemplateFactory.getInstance().getTemplate(neededTemplates[m]);
                if (m > 0) {
                    buf.append(" and ");
                }
                buf.append(template2.getNameWithGenus());
            }
            catch (NoSuchTemplateException nst2) {
                CaveWallBehaviour.logger.log(Level.WARNING, nst2.getMessage(), nst2);
            }
            if (depleteItems[m].isCombine()) {
                depleteItems[m].setWeight(depleteItems[m].getWeightGrams() - depleteItems[m].getTemplate().getWeightGrams(), true);
            }
            else {
                Items.destroyItem(depleteItems[m].getWurmId());
            }
            qlevel += depleteItems[m].getCurrentQualityLevel() / 21.0f;
        }
        performer.getCommunicator().sendNormalServerMessage("You use " + buf.toString() + ".");
        act.setPower(qlevel);
        return true;
    }
    
    static final int[] getNotInitializedIntArray(final int len) {
        final int[] notInitializedArray = new int[len];
        for (int x = 0; x < notInitializedArray.length; ++x) {
            notInitializedArray[x] = -1;
        }
        return notInitializedArray;
    }
    
    static final int getItemTemplateWeightInGrams(final int itemTemplateId) {
        int neededTemplateWeightGrams = 0;
        try {
            neededTemplateWeightGrams = ItemTemplateFactory.getInstance().getTemplate(itemTemplateId).getWeightGrams();
        }
        catch (NoSuchTemplateException nst) {
            switch (itemTemplateId) {
                case 22: {
                    neededTemplateWeightGrams = 2000;
                    break;
                }
                case 217: {
                    neededTemplateWeightGrams = 300;
                    break;
                }
                case 492: {
                    neededTemplateWeightGrams = 2000;
                    break;
                }
                case 132: {
                    neededTemplateWeightGrams = 150000;
                    break;
                }
                default: {
                    neededTemplateWeightGrams = 150000;
                    break;
                }
            }
        }
        return neededTemplateWeightGrams;
    }
    
    public static boolean isCorrectTool(final byte type, final Creature performer, @Nullable final Item tool) {
        if (tool == null) {
            return false;
        }
        final List<Integer> tools = getToolsForType(type, performer);
        for (final Integer t : tools) {
            if (t == tool.getTemplateId()) {
                return true;
            }
        }
        return false;
    }
    
    public static List<Integer> getToolsForType(final byte type, @Nullable final Creature performer) {
        final List<Integer> list = new ArrayList<Integer>();
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id) {
            list.add(62);
            list.add(63);
            if (performer != null) {
                if (performer.getPower() >= 2 && Servers.isThisATestServer()) {
                    list.add(315);
                }
                if (WurmPermissions.mayUseGMWand(performer)) {
                    list.add(176);
                }
            }
        }
        else {
            list.add(493);
            if (performer != null && WurmPermissions.mayUseGMWand(performer)) {
                list.add(176);
            }
        }
        return list;
    }
    
    public static short actionFromWallType(final byte type) {
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id) {
            return 856;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id) {
            return 857;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id) {
            return 858;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id) {
            return 859;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id) {
            return 860;
        }
        if (type == Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id) {
            return 861;
        }
        return 862;
    }
    
    public static int[] getTemplatesNeeded(final int tilex, final int tiley, final byte type) {
        final int resource = Server.getCaveResource(tilex, tiley);
        final int needs = resource >>> 8;
        final int brick = getBrickTypeNeeded(type);
        int[] templatesNeeded;
        if (brick == 22) {
            if (needs == 10) {
                templatesNeeded = new int[] { 22, 217 };
            }
            else {
                templatesNeeded = new int[] { 22 };
            }
        }
        else {
            templatesNeeded = new int[] { brick, 492 };
        }
        return templatesNeeded;
    }
    
    public static int[] getMaterialsNeeded(final int tilex, final int tiley, final byte type) {
        final int resource = Server.getCaveResource(tilex, tiley);
        final int needs = resource >>> 8;
        final int brick = getBrickTypeNeeded(type);
        int[] materialsNeeded;
        if (brick == 22) {
            if (needs == 10) {
                materialsNeeded = new int[] { 22, 10, 217, 1 };
            }
            else {
                materialsNeeded = new int[] { 22, needs };
            }
        }
        else {
            materialsNeeded = new int[] { brick, needs, 492, needs };
        }
        return materialsNeeded;
    }
    
    public static int getBrickTypeNeeded(final byte type) {
        switch (type & 0xFF) {
            case 239: {
                return 132;
            }
            case 240: {
                return 1123;
            }
            case 242: {
                return 1122;
            }
            case 241: {
                return 776;
            }
            case 243: {
                return 1121;
            }
            case 244: {
                return 786;
            }
            case 245: {
                return 22;
            }
            default: {
                return 132;
            }
        }
    }
    
    public static byte[] getMaterialsFromToolType(final Creature performer, final Item tool) {
        switch (tool.getTemplateId()) {
            case 62:
            case 63: {
                return new byte[] { Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id };
            }
            case 493: {
                return new byte[] { Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id };
            }
            case 176: {
                return new byte[] { Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id };
            }
            case 315: {
                if (performer.getPower() >= 2 && Servers.isThisATestServer()) {
                    return new byte[] { Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id, Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id };
                }
                return new byte[0];
            }
            default: {
                return new byte[0];
            }
        }
    }
    
    public static final boolean canCladWall(final byte type, final Creature performer) {
        final int skillNumber = (type == Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id) ? 1005 : 1013;
        final Skill skill = performer.getSkills().getSkillOrLearn(skillNumber);
        return skill != null && (skillNumber != 1013 || skill.getKnowledge(0.0) >= 30.0);
    }
    
    public static final int getSkillNumberNeededForCladding(final short action) {
        if (action == 862) {
            return 1005;
        }
        return 1013;
    }
    
    public static final int[] getCorrectToolsForCladding(final short action) {
        if (action == 862) {
            return new int[] { 62, 63 };
        }
        return new int[] { 493 };
    }
    
    public static byte getPartReinforcedWallFromAction(final short action) {
        switch (action) {
            case 856: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_STONE_REINFORCED.id;
            }
            case 857: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_SLATE_REINFORCED.id;
            }
            case 858: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_POTTERY_REINFORCED.id;
            }
            case 859: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_ROUNDED_STONE_REINFORCED.id;
            }
            case 860: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_SANDSTONE_REINFORCED.id;
            }
            case 861: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_MARBLE_REINFORCED.id;
            }
            case 862: {
                return Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id;
            }
            default: {
                CaveWallBehaviour.logger.log(Level.WARNING, "Part reinforced wall for action " + action + " is not found!", new Exception());
                return Tiles.Tile.TILE_CAVE_WALL_PART_WOOD_REINFORCED.id;
            }
        }
    }
    
    public static byte getReinforcedWallFromAction(final short action) {
        switch (action) {
            case 856: {
                return Tiles.Tile.TILE_CAVE_WALL_STONE_REINFORCED.id;
            }
            case 857: {
                return Tiles.Tile.TILE_CAVE_WALL_SLATE_REINFORCED.id;
            }
            case 858: {
                return Tiles.Tile.TILE_CAVE_WALL_POTTERY_REINFORCED.id;
            }
            case 859: {
                return Tiles.Tile.TILE_CAVE_WALL_ROUNDED_STONE_REINFORCED.id;
            }
            case 860: {
                return Tiles.Tile.TILE_CAVE_WALL_SANDSTONE_REINFORCED.id;
            }
            case 861: {
                return Tiles.Tile.TILE_CAVE_WALL_MARBLE_REINFORCED.id;
            }
            case 862: {
                return Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id;
            }
            default: {
                CaveWallBehaviour.logger.log(Level.WARNING, "Reinforced wall for action " + action + " is not found!", new Exception());
                return Tiles.Tile.TILE_CAVE_WALL_WOOD_REINFORCED.id;
            }
        }
    }
    
    public static int[] getMaterialsNeededTotal(final short action) {
        switch (action) {
            case 856: {
                return new int[] { 132, 10, 492, 10 };
            }
            case 857: {
                return new int[] { 1123, 10, 492, 10 };
            }
            case 858: {
                return new int[] { 776, 10, 492, 10 };
            }
            case 859: {
                return new int[] { 1122, 10, 492, 10 };
            }
            case 860: {
                return new int[] { 1121, 10, 492, 10 };
            }
            case 861: {
                return new int[] { 786, 10, 492, 10 };
            }
            case 862: {
                return new int[] { 22, 10, 217, 1 };
            }
            default: {
                CaveWallBehaviour.logger.log(Level.WARNING, "Materials for reinforced wall for action " + action + " is not found!", new Exception());
                return new int[0];
            }
        }
    }
    
    static {
        logger = Logger.getLogger(CaveWallBehaviour.class.getName());
    }
}
