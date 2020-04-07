// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import javax.annotation.Nonnull;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.structures.StructureSupport;
import com.wurmonline.server.structures.IFloor;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.Items;
import com.wurmonline.server.skills.Skill;
import java.io.IOException;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.Iterator;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.Servers;
import com.wurmonline.server.structures.RoofFloorEnum;
import java.util.Collections;
import java.util.ArrayList;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.NoSuchStructureException;
import java.util.logging.Level;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.shared.constants.StructureConstants;
import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class FloorBehaviour extends TileBehaviour
{
    private static final Logger logger;
    
    FloorBehaviour() {
        super((short)45);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final boolean onSurface, final Floor floor) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (!floor.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        toReturn.addAll(Actions.getDefaultTileActions());
        toReturn.addAll(super.getTileAndFloorBehavioursFor(performer, null, floor.getTileX(), floor.getTileY(), Tiles.Tile.TILE_DIRT.id));
        if (floor.getType() == StructureConstants.FloorType.OPENING) {
            if (floor.isFinished()) {
                if (floor.getFloorLevel() == performer.getFloorLevel()) {
                    toReturn.add(Actions.actionEntrys[523]);
                }
                else if (floor.getFloorLevel() == performer.getFloorLevel() + 1) {
                    toReturn.add(Actions.actionEntrys[522]);
                }
            }
            else if (floor.getFloorLevel() == performer.getFloorLevel()) {
                toReturn.add(Actions.actionEntrys[523]);
            }
        }
        final VolaTile floorTile = Zones.getOrCreateTile(floor.getTileX(), floor.getTileY(), floor.getLayer() >= 0);
        Structure structure = null;
        try {
            structure = Structures.getStructure(floor.getStructureId());
        }
        catch (NoSuchStructureException e) {
            FloorBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            return toReturn;
        }
        if (MethodsStructure.mayModifyStructure(performer, structure, floorTile, (short)177)) {
            toReturn.add(new ActionEntry((short)(-2), "Rotate", "rotating"));
            toReturn.add(new ActionEntry((short)177, "Turn clockwise", "turning"));
            toReturn.add(new ActionEntry((short)178, "Turn counterclockwise", "turning"));
        }
        return toReturn;
    }
    
    public static final List<ActionEntry> getCompletedFloorsBehaviour(final boolean andStaircases, final boolean onSurface) {
        final List<ActionEntry> plantypes = new ArrayList<ActionEntry>();
        plantypes.add(Actions.actionEntrys[508]);
        plantypes.add(Actions.actionEntrys[515]);
        if (andStaircases) {
            plantypes.add(Actions.actionEntrys[659]);
            plantypes.add(Actions.actionEntrys[704]);
            plantypes.add(Actions.actionEntrys[713]);
            plantypes.add(Actions.actionEntrys[714]);
            plantypes.add(Actions.actionEntrys[715]);
            plantypes.add(Actions.actionEntrys[705]);
            plantypes.add(Actions.actionEntrys[706]);
            plantypes.add(Actions.actionEntrys[709]);
            plantypes.add(Actions.actionEntrys[710]);
            plantypes.add(Actions.actionEntrys[711]);
            plantypes.add(Actions.actionEntrys[712]);
        }
        plantypes.add(Actions.actionEntrys[509]);
        if (onSurface) {
            plantypes.add(Actions.actionEntrys[507]);
        }
        Collections.sort(plantypes);
        final List<ActionEntry> toReturn = new ArrayList<ActionEntry>(5);
        toReturn.add(new ActionEntry((short)(-plantypes.size()), "Plan", "planning"));
        toReturn.addAll(plantypes);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final boolean onSurface, final Floor floor) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (!floor.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        toReturn.addAll(super.getTileAndFloorBehavioursFor(performer, source, floor.getTileX(), floor.getTileY(), Tiles.Tile.TILE_DIRT.id));
        if (floor.getType() == StructureConstants.FloorType.OPENING) {
            if (floor.isFinished()) {
                if (floor.getFloorLevel() == performer.getFloorLevel()) {
                    toReturn.add(Actions.actionEntrys[523]);
                }
                else if (floor.getFloorLevel() == performer.getFloorLevel() + 1) {
                    toReturn.add(Actions.actionEntrys[522]);
                }
            }
            else if (floor.getFloorLevel() == performer.getFloorLevel()) {
                toReturn.add(Actions.actionEntrys[523]);
            }
        }
        final VolaTile floorTile = Zones.getOrCreateTile(floor.getTileX(), floor.getTileY(), floor.isOnSurface());
        Structure structure = null;
        try {
            structure = Structures.getStructure(floor.getStructureId());
        }
        catch (NoSuchStructureException e) {
            FloorBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            toReturn.addAll(Actions.getDefaultItemActions());
            return toReturn;
        }
        if (MethodsStructure.mayModifyStructure(performer, structure, floorTile, (short)169)) {
            switch (floor.getFloorState()) {
                case BUILDING: {
                    toReturn.add(new ActionEntry((short)169, "Continue building", "building"));
                    break;
                }
                case PLANNING: {
                    if (floor.getType() == StructureConstants.FloorType.ROOF) {
                        final List<RoofFloorEnum> list = RoofFloorEnum.getRoofsByTool(source);
                        if (list.size() > 0) {
                            toReturn.add(new ActionEntry((short)(-list.size()), "Build", "building"));
                            for (final RoofFloorEnum en : list) {
                                toReturn.add(en.createActionEntry());
                            }
                        }
                        break;
                    }
                    final List<RoofFloorEnum> list = RoofFloorEnum.getFloorByToolAndType(source, floor.getType());
                    if (list.size() > 0) {
                        toReturn.add(new ActionEntry((short)(-list.size()), "Build", "building"));
                        for (final RoofFloorEnum en : list) {
                            toReturn.add(en.createActionEntry());
                        }
                    }
                    break;
                }
                case COMPLETED: {
                    if (floor.getType() != StructureConstants.FloorType.ROOF) {
                        toReturn.addAll(getCompletedFloorsBehaviour(true, floor.isOnSurface()));
                        break;
                    }
                    break;
                }
            }
            toReturn.add(new ActionEntry((short)(-2), "Rotate", "rotating"));
            toReturn.add(new ActionEntry((short)177, "Turn clockwise", "turning"));
            toReturn.add(new ActionEntry((short)178, "Turn counterclockwise", "turning"));
        }
        if (!source.isTraded()) {
            if (source.getTemplateId() == floor.getRepairItemTemplate()) {
                if (floor.getDamage() > 0.0f) {
                    if ((!Servers.localServer.challengeServer || performer.getEnemyPresense() <= 0) && !floor.isNoRepair()) {
                        toReturn.add(Actions.actionEntrys[193]);
                    }
                }
                else if (floor.getQualityLevel() < 100.0f && !floor.isNoImprove()) {
                    toReturn.add(Actions.actionEntrys[192]);
                }
            }
            toReturn.addAll(Actions.getDefaultItemActions());
            if (!floor.isIndestructible()) {
                if (floor.getType() == StructureConstants.FloorType.ROOF) {
                    toReturn.add(Actions.actionEntrys[525]);
                }
                else if (!MethodsHighways.onHighway(floor)) {
                    toReturn.add(Actions.actionEntrys[524]);
                }
            }
            if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                toReturn.add(Actions.actionEntrys[684]);
            }
        }
        return toReturn;
    }
    
    private boolean buildAction(final Action act, final Creature performer, final Item source, final Floor floor, final short action, final float counter) {
        switch (floor.getFloorState()) {
            case PLANNING: {
                final boolean autoAdvance = performer.getPower() >= 2 && source.getTemplateId() == 176;
                Skill craftSkill = null;
                try {
                    craftSkill = performer.getSkills().getSkill(1005);
                }
                catch (NoSuchSkillException nss) {
                    craftSkill = performer.getSkills().learn(1005, 1.0f);
                }
                final StructureConstants.FloorMaterial newMaterial = StructureConstants.FloorMaterial.fromByte((byte)(action - 20000));
                final StructureConstants.FloorMaterial oldMaterial = floor.getMaterial();
                floor.setMaterial(newMaterial);
                if (!isOkToBuild(performer, source, floor, floor.getFloorLevel(), floor.isRoof())) {
                    floor.setMaterial(oldMaterial);
                    return true;
                }
                if (!autoAdvance && !advanceNextState(performer, floor, act, true)) {
                    final String message = buildRequiredMaterialString(floor, false);
                    performer.getCommunicator().sendNormalServerMessage("You need " + message + " to start building that.");
                    floor.setMaterial(oldMaterial);
                    return true;
                }
                floor.setFloorState(StructureConstants.FloorState.BUILDING);
                final float oldql = floor.getQualityLevel();
                final float qlevel = MethodsStructure.calculateNewQualityLevel(act.getPower(), craftSkill.getKnowledge(0.0), oldql, getTotalMaterials(floor));
                floor.setQualityLevel(qlevel);
                try {
                    floor.save();
                }
                catch (IOException e) {
                    FloorBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (floor.getMaterial() == StructureConstants.FloorMaterial.STANDALONE) {
                    performer.getCommunicator().sendNormalServerMessage("You plan a " + floor.getName() + ".");
                }
                else if (floor.getType() == StructureConstants.FloorType.ROOF) {
                    performer.getCommunicator().sendNormalServerMessage("You plan a " + floor.getName() + " made of " + getMaterialDescription(floor) + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You plan a " + floor.getName() + " made of " + floor.getMaterial().getName().toLowerCase() + ".");
                }
                return floorBuilding(act, performer, source, floor, action, counter);
            }
            case BUILDING: {
                if (floorBuilding(act, performer, source, floor, action, counter)) {
                    performer.getCommunicator().sendAddFloorRoofToCreationWindow(floor, floor.getId());
                    return true;
                }
                return false;
            }
            case COMPLETED: {
                FloorBehaviour.logger.log(Level.WARNING, "FloorBehaviour buildAction on a completed floor, it should not happen?!");
                performer.getCommunicator().sendNormalServerMessage("You failed to find anything to do with that.");
                return true;
            }
            default: {
                FloorBehaviour.logger.log(Level.WARNING, "Enum value added to FloorState but not to a switch statement in method FloorBehaviour.action()");
                return false;
            }
        }
    }
    
    static boolean advanceNextState(final Creature performer, final Floor floor, final Action act, final boolean justCheckIfItemsArePresent) {
        final List<BuildMaterial> mats = getRequiredMaterialsAtState(floor);
        if (takeItemsFromCreature(performer, floor, act, mats, justCheckIfItemsArePresent)) {
            return true;
        }
        if (performer.getPower() >= 4 && !justCheckIfItemsArePresent) {
            performer.getCommunicator().sendNormalServerMessage("You magically summon some necessary materials.");
            return true;
        }
        return false;
    }
    
    static boolean takeItemsFromCreature(final Creature performer, final Floor floor, final Action act, final List<BuildMaterial> mats, final boolean justCheckIfItemsArePresent) {
        final Item[] inventoryItems = performer.getInventory().getAllItems(false);
        final Item[] bodyItems = performer.getBody().getAllItems();
        final List<Item> takeItemsOnSuccess = new ArrayList<Item>();
        for (final Item item : inventoryItems) {
            for (final BuildMaterial mat : mats) {
                if (mat.getNeededQuantity() > 0 && item.getTemplateId() == mat.getTemplateId() && item.getWeightGrams() >= mat.getWeightGrams()) {
                    takeItemsOnSuccess.add(item);
                    mat.setNeededQuantity(0);
                    break;
                }
            }
        }
        for (final Item item : bodyItems) {
            for (final BuildMaterial mat : mats) {
                if (mat.getNeededQuantity() > 0 && item.getTemplateId() == mat.getTemplateId() && item.getWeightGrams() >= mat.getWeightGrams()) {
                    takeItemsOnSuccess.add(item);
                    mat.setNeededQuantity(0);
                    break;
                }
            }
        }
        float divider = 1.0f;
        for (final BuildMaterial mat2 : mats) {
            divider += mat2.getTotalQuantityRequired();
            if (mat2.getNeededQuantity() > 0) {
                return false;
            }
        }
        float qlevel = 0.0f;
        if (!justCheckIfItemsArePresent) {
            for (final Item item : takeItemsOnSuccess) {
                act.setPower(item.getCurrentQualityLevel() / divider);
                performer.sendToLoggers("Adding " + item.getCurrentQualityLevel() + ", divider=" + divider + "=" + act.getPower());
                qlevel += item.getCurrentQualityLevel() / 21.0f;
                if (item.isCombine()) {
                    item.setWeight(item.getWeightGrams() - item.getTemplate().getWeightGrams(), true);
                }
                else {
                    Items.destroyItem(item.getWurmId());
                }
            }
        }
        act.setPower(qlevel);
        return true;
    }
    
    public static int getSkillForRoof(final StructureConstants.FloorMaterial material) {
        switch (material) {
            case WOOD: {
                return 1005;
            }
            case CLAY_BRICK: {
                return 1013;
            }
            case SLATE_SLAB: {
                return 1013;
            }
            case STONE_BRICK: {
                return 1013;
            }
            case SANDSTONE_SLAB: {
                return 1013;
            }
            case STONE_SLAB: {
                return 1013;
            }
            case MARBLE_SLAB: {
                return 1013;
            }
            case THATCH: {
                return 10092;
            }
            case METAL_IRON: {
                return 10015;
            }
            case METAL_COPPER: {
                return 10015;
            }
            case METAL_STEEL: {
                return 10015;
            }
            case METAL_SILVER: {
                return 10015;
            }
            case METAL_GOLD: {
                return 10015;
            }
            case STANDALONE: {
                return 1005;
            }
            default: {
                return 1005;
            }
        }
    }
    
    public static int getSkillForFloor(final StructureConstants.FloorMaterial material) {
        switch (material) {
            case WOOD: {
                return 1005;
            }
            case CLAY_BRICK: {
                return 10031;
            }
            case SLATE_SLAB: {
                return 10031;
            }
            case STONE_BRICK: {
                return 10031;
            }
            case SANDSTONE_SLAB: {
                return 10031;
            }
            case STONE_SLAB: {
                return 10031;
            }
            case MARBLE_SLAB: {
                return 10031;
            }
            case THATCH: {
                return 10092;
            }
            case METAL_IRON: {
                return 10015;
            }
            case METAL_COPPER: {
                return 10015;
            }
            case METAL_STEEL: {
                return 10015;
            }
            case METAL_SILVER: {
                return 10015;
            }
            case METAL_GOLD: {
                return 10015;
            }
            case STANDALONE: {
                return 1005;
            }
            default: {
                return 1005;
            }
        }
    }
    
    static byte getFinishedState(final Floor floor) {
        byte numStates = 0;
        final List<BuildMaterial> mats = getRequiredMaterialsFor(floor);
        for (final BuildMaterial mat : mats) {
            if (numStates < mat.getTotalQuantityRequired()) {
                numStates = (byte)mat.getTotalQuantityRequired();
            }
        }
        if (numStates <= 0) {
            numStates = 1;
        }
        return numStates;
    }
    
    static byte getTotalMaterials(final Floor floor) {
        int total = 0;
        final List<BuildMaterial> mats = getRequiredMaterialsFor(floor);
        for (final BuildMaterial mat : mats) {
            final int totalReq = mat.getTotalQuantityRequired();
            if (totalReq > total) {
                total = totalReq;
            }
        }
        return (byte)total;
    }
    
    public static final List<BuildMaterial> getRequiredMaterialsForRoof(final StructureConstants.FloorMaterial material) {
        final List<BuildMaterial> toReturn = new ArrayList<BuildMaterial>();
        try {
            switch (material) {
                case WOOD: {
                    toReturn.add(new BuildMaterial(790, 10));
                    toReturn.add(new BuildMaterial(218, 2));
                    break;
                }
                case STONE_BRICK: {
                    toReturn.add(new BuildMaterial(132, 10));
                    toReturn.add(new BuildMaterial(492, 10));
                    break;
                }
                case CLAY_BRICK: {
                    toReturn.add(new BuildMaterial(778, 10));
                    toReturn.add(new BuildMaterial(492, 10));
                    break;
                }
                case THATCH: {
                    toReturn.add(new BuildMaterial(756, 10));
                    toReturn.add(new BuildMaterial(444, 10));
                    break;
                }
                case SLATE_SLAB: {
                    toReturn.add(new BuildMaterial(784, 10));
                    toReturn.add(new BuildMaterial(492, 5));
                    break;
                }
                default: {
                    FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a roof but the material choice was not supported (" + material.toString() + ")");
                    break;
                }
            }
        }
        catch (NoSuchTemplateException nste) {
            FloorBehaviour.logger.log(Level.WARNING, "FloorBehaviour.getRequiredMaterialsAtState trying to use material that have a non existing template.", nste);
        }
        return toReturn;
    }
    
    public static List<BuildMaterial> getRequiredMaterialsForFloor(final StructureConstants.FloorType type, final StructureConstants.FloorMaterial material) {
        final List<BuildMaterial> toReturn = new ArrayList<BuildMaterial>();
        try {
            if (type == StructureConstants.FloorType.OPENING) {
                switch (material) {
                    case WOOD: {
                        toReturn.add(new BuildMaterial(22, 5));
                        toReturn.add(new BuildMaterial(218, 1));
                        break;
                    }
                    case STONE_BRICK: {
                        toReturn.add(new BuildMaterial(132, 5));
                        toReturn.add(new BuildMaterial(492, 5));
                        break;
                    }
                    case SANDSTONE_SLAB: {
                        toReturn.add(new BuildMaterial(1124, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case STONE_SLAB: {
                        toReturn.add(new BuildMaterial(406, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case CLAY_BRICK: {
                        toReturn.add(new BuildMaterial(776, 5));
                        toReturn.add(new BuildMaterial(492, 5));
                        break;
                    }
                    case SLATE_SLAB: {
                        toReturn.add(new BuildMaterial(771, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case MARBLE_SLAB: {
                        toReturn.add(new BuildMaterial(787, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case STANDALONE: {
                        toReturn.add(new BuildMaterial(23, 2));
                        toReturn.add(new BuildMaterial(218, 1));
                        break;
                    }
                    default: {
                        FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a floor with an opening but the material choice was not supported (" + material.toString() + ")");
                        break;
                    }
                }
            }
            else if (type == StructureConstants.FloorType.WIDE_STAIRCASE) {
                toReturn.add(new BuildMaterial(22, 30));
                toReturn.add(new BuildMaterial(217, 2));
            }
            else if (type == StructureConstants.FloorType.WIDE_STAIRCASE_RIGHT || type == StructureConstants.FloorType.WIDE_STAIRCASE_LEFT) {
                toReturn.add(new BuildMaterial(22, 30));
                toReturn.add(new BuildMaterial(23, 5));
                toReturn.add(new BuildMaterial(218, 1));
                toReturn.add(new BuildMaterial(217, 2));
            }
            else if (type == StructureConstants.FloorType.WIDE_STAIRCASE_BOTH) {
                toReturn.add(new BuildMaterial(22, 30));
                toReturn.add(new BuildMaterial(23, 10));
                toReturn.add(new BuildMaterial(218, 1));
                toReturn.add(new BuildMaterial(217, 2));
            }
            else if (type == StructureConstants.FloorType.STAIRCASE || type == StructureConstants.FloorType.RIGHT_STAIRCASE || type == StructureConstants.FloorType.LEFT_STAIRCASE) {
                switch (material) {
                    case WOOD: {
                        toReturn.add(new BuildMaterial(22, 20));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 2));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case STONE_BRICK: {
                        toReturn.add(new BuildMaterial(132, 5));
                        toReturn.add(new BuildMaterial(492, 5));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case SANDSTONE_SLAB: {
                        toReturn.add(new BuildMaterial(1124, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case STONE_SLAB: {
                        toReturn.add(new BuildMaterial(406, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case CLAY_BRICK: {
                        toReturn.add(new BuildMaterial(776, 5));
                        toReturn.add(new BuildMaterial(492, 5));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case SLATE_SLAB: {
                        toReturn.add(new BuildMaterial(771, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case MARBLE_SLAB: {
                        toReturn.add(new BuildMaterial(787, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    case STANDALONE: {
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 10));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        break;
                    }
                    default: {
                        FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a staircase with an opening but the material choice was not supported (" + material.toString() + ")");
                        break;
                    }
                }
            }
            else if (type == StructureConstants.FloorType.CLOCKWISE_STAIRCASE || type == StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE) {
                switch (material) {
                    case WOOD: {
                        toReturn.add(new BuildMaterial(22, 20));
                        toReturn.add(new BuildMaterial(218, 2));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 20));
                        break;
                    }
                    case STONE_BRICK: {
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 25));
                        toReturn.add(new BuildMaterial(492, 25));
                        break;
                    }
                    case SANDSTONE_SLAB: {
                        toReturn.add(new BuildMaterial(1124, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case STONE_SLAB: {
                        toReturn.add(new BuildMaterial(406, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case CLAY_BRICK: {
                        toReturn.add(new BuildMaterial(776, 5));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 25));
                        break;
                    }
                    case SLATE_SLAB: {
                        toReturn.add(new BuildMaterial(771, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case MARBLE_SLAB: {
                        toReturn.add(new BuildMaterial(787, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(218, 1));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    default: {
                        FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a spiral staircase but the material choice was not supported (" + material.toString() + ")");
                        break;
                    }
                }
            }
            else if (type == StructureConstants.FloorType.CLOCKWISE_STAIRCASE_WITH || type == StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE_WITH) {
                switch (material) {
                    case WOOD: {
                        toReturn.add(new BuildMaterial(22, 20));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 5));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 20));
                        break;
                    }
                    case STONE_BRICK: {
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 25));
                        toReturn.add(new BuildMaterial(492, 25));
                        break;
                    }
                    case SANDSTONE_SLAB: {
                        toReturn.add(new BuildMaterial(1124, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case STONE_SLAB: {
                        toReturn.add(new BuildMaterial(406, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case CLAY_BRICK: {
                        toReturn.add(new BuildMaterial(776, 5));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 25));
                        break;
                    }
                    case SLATE_SLAB: {
                        toReturn.add(new BuildMaterial(771, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    case MARBLE_SLAB: {
                        toReturn.add(new BuildMaterial(787, 2));
                        toReturn.add(new BuildMaterial(22, 15));
                        toReturn.add(new BuildMaterial(23, 15));
                        toReturn.add(new BuildMaterial(218, 4));
                        toReturn.add(new BuildMaterial(217, 1));
                        toReturn.add(new BuildMaterial(132, 20));
                        toReturn.add(new BuildMaterial(492, 30));
                        break;
                    }
                    default: {
                        FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a staircase with an opening but the material choice was not supported (" + material.toString() + ")");
                        break;
                    }
                }
            }
            else {
                switch (material) {
                    case WOOD: {
                        toReturn.add(new BuildMaterial(22, 10));
                        toReturn.add(new BuildMaterial(218, 2));
                        break;
                    }
                    case STONE_BRICK: {
                        toReturn.add(new BuildMaterial(132, 10));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case SANDSTONE_SLAB: {
                        toReturn.add(new BuildMaterial(1124, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case STONE_SLAB: {
                        toReturn.add(new BuildMaterial(406, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case CLAY_BRICK: {
                        toReturn.add(new BuildMaterial(776, 10));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case SLATE_SLAB: {
                        toReturn.add(new BuildMaterial(771, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    case MARBLE_SLAB: {
                        toReturn.add(new BuildMaterial(787, 2));
                        toReturn.add(new BuildMaterial(492, 10));
                        break;
                    }
                    default: {
                        FloorBehaviour.logger.log(Level.WARNING, "Someone tried to make a floor but the material choice was not supported (" + material.toString() + ")");
                        break;
                    }
                }
            }
        }
        catch (NoSuchTemplateException nste) {
            FloorBehaviour.logger.log(Level.WARNING, "FloorBehaviour.getRequiredMaterialsAtState trying to use material that have a non existing template.", nste);
        }
        return toReturn;
    }
    
    static List<BuildMaterial> getRequiredMaterialsFor(final Floor floor) {
        if (floor.getType() == StructureConstants.FloorType.ROOF) {
            return getRequiredMaterialsForRoof(floor.getMaterial());
        }
        return getRequiredMaterialsForFloor(floor.getType(), floor.getMaterial());
    }
    
    static List<BuildMaterial> getRequiredMaterialsForFloor(final Floor floor) {
        final List<BuildMaterial> toReturn = getRequiredMaterialsForFloor(floor.getType(), floor.getMaterial());
        return toReturn;
    }
    
    public static List<BuildMaterial> getRequiredMaterialsAtState(final Floor floor) {
        if (floor.getType() == StructureConstants.FloorType.ROOF) {
            return getRequiredMaterialsAtStateForRoof(floor);
        }
        return getRequiredMaterialsAtStateForFloor(floor);
    }
    
    public static List<BuildMaterial> getRequiredMaterialsAtStateForRoof(final Floor floor) {
        final List<BuildMaterial> mats = getRequiredMaterialsForRoof(floor.getMaterial());
        for (final BuildMaterial mat : mats) {
            int qty = mat.getTotalQuantityRequired();
            if (floor.getState() > 0) {
                qty -= floor.getState();
            }
            else if (qty < 0) {
                qty = 0;
            }
            mat.setNeededQuantity(qty);
        }
        return mats;
    }
    
    public static List<BuildMaterial> getRequiredMaterialsAtStateForFloor(final Floor floor) {
        final List<BuildMaterial> mats = getRequiredMaterialsForFloor(floor.getType(), floor.getMaterial());
        for (final BuildMaterial mat : mats) {
            int qty = mat.getTotalQuantityRequired();
            if (floor.getState() > 0) {
                qty -= floor.getState();
            }
            else if (qty < 0) {
                qty = 0;
            }
            mat.setNeededQuantity(qty);
        }
        return mats;
    }
    
    static final boolean isOkToBuild(final Creature performer, final Item tool, final Floor floor, final int floorLevel, final boolean roof) {
        if (tool == null) {
            performer.getCommunicator().sendNormalServerMessage("You need to activate a building tool if you want to build something.");
            return false;
        }
        if (floor == null) {
            performer.getCommunicator().sendNormalServerMessage("You fail to focus, and cannot find that floor.");
            return false;
        }
        final StructureConstants.FloorMaterial floorMaterial = floor.getMaterial();
        final String nameOfWhatIsBeingBuilt = floor.getName();
        if (!hasValidTool(floor.getMaterial(), tool)) {
            performer.getCommunicator().sendNormalServerMessage("You need to activate the correct building tool if you want to build that.");
            return false;
        }
        final Skill buildSkill = getBuildSkill(floor.getType(), floorMaterial, performer);
        if (!mayPlanAtLevel(performer, floorLevel, buildSkill, roof)) {
            return false;
        }
        if (buildSkill.getKnowledge(0.0) < getRequiredBuildSkillForFloorType(floorMaterial)) {
            if (floor.getMaterial() == StructureConstants.FloorMaterial.STANDALONE) {
                performer.getCommunicator().sendNormalServerMessage("You need higher " + buildSkill.getName() + " skill to build " + nameOfWhatIsBeingBuilt + " with " + floor.getMaterial().getName() + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You need higher " + buildSkill.getName() + " skill to build " + floor.getMaterial().getName() + " " + nameOfWhatIsBeingBuilt + ".");
            }
            return false;
        }
        return true;
    }
    
    public static final boolean mayPlanAtLevel(final Creature performer, final int floorLevel, final Skill buildSkill, final boolean roof) {
        return mayPlanAtLevel(performer, floorLevel, buildSkill, roof, true);
    }
    
    public static final boolean mayPlanAtLevel(final Creature performer, final int floorLevel, final Skill buildSkill, final boolean roof, final boolean sendMessage) {
        if (buildSkill.getKnowledge(0.0) < getRequiredBuildSkillForFloorLevel(floorLevel, roof)) {
            if (sendMessage) {
                performer.getCommunicator().sendNormalServerMessage("You need higher " + buildSkill.getName() + " skill to build at that height.");
            }
            return false;
        }
        return true;
    }
    
    private static final boolean floorBuilding(final Action act, final Creature performer, final Item source, final Floor floor, final short action, final float counter) {
        if (performer.isFighting()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot do that while in combat.");
            return true;
        }
        if (!isOkToBuild(performer, source, floor, floor.getFloorLevel(), floor.isRoof())) {
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        int time = 10;
        final boolean insta = (Servers.isThisATestServer() || performer.getPower() >= 4) && performer.getPower() > 1 && (source.getTemplateId() == 315 || source.getTemplateId() == 176);
        if (floor.isFinished()) {
            performer.getCommunicator().sendNormalServerMessage("The " + floor.getName() + " is finished already.");
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, floor.getTileX(), floor.getTileY())) {
            return true;
        }
        if (counter == 1.0f) {
            Structure structure;
            try {
                structure = Structures.getStructure(floor.getStructureId());
            }
            catch (NoSuchStructureException e) {
                FloorBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                performer.getCommunicator().sendNormalServerMessage("Your sensitive mind notices a wrongness in the fabric of space.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            if (!MethodsStructure.mayModifyStructure(performer, structure, floor.getTile(), action)) {
                performer.getCommunicator().sendNormalServerMessage("You need permission in order to make modifications to this structure.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            if (!advanceNextState(performer, floor, act, true) && !insta) {
                final String message = buildRequiredMaterialString(floor, false);
                if (floor.getType() == StructureConstants.FloorType.WIDE_STAIRCASE) {
                    performer.getCommunicator().sendNormalServerMessage("You need " + message + " to start building the " + floor.getName() + " with " + floor.getMaterial().getName().toLowerCase() + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You need " + message + " to start building the " + floor.getName() + " of " + floor.getMaterial().getName().toLowerCase() + ".");
                }
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            final Skill buildSkill = getBuildSkill(floor.getType(), floor.getMaterial(), performer);
            time = Actions.getSlowActionTime(performer, buildSkill, source, 0.0);
            act.setTimeLeft(time);
            performer.getStatus().modifyStamina(-1000.0f);
            damageTool(performer, floor, source);
            Server.getInstance().broadCastAction(performer.getName() + " continues to build a " + floor.getName() + ".", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You continue to build a " + floor.getName() + ".");
            if (!insta) {
                performer.sendActionControl("Building a " + floor.getName(), true, time);
            }
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                SoundPlayer.playSound(getBuildSound(floor), floor.getTileX(), floor.getTileY(), performer.isOnSurface(), 1.6f);
                performer.getStatus().modifyStamina(-1000.0f);
                damageTool(performer, floor, source);
            }
        }
        if (counter * 10.0f <= time && !insta) {
            return false;
        }
        final String message2 = buildRequiredMaterialString(floor, false);
        if (!advanceNextState(performer, floor, act, false) && !insta) {
            if (floor.getType() == StructureConstants.FloorType.WIDE_STAIRCASE) {
                performer.getCommunicator().sendNormalServerMessage("You need " + message2 + " to build the " + floor.getName() + " with " + floor.getMaterial().getName().toLowerCase() + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You need " + message2 + " to build the " + floor.getName() + " of " + floor.getMaterial().getName().toLowerCase() + ".");
            }
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        double bonus = 0.0;
        final Skill toolSkill = getToolSkill(floor, performer, source);
        if (toolSkill != null) {
            toolSkill.skillCheck(10.0, source, 0.0, false, counter);
            bonus = toolSkill.getKnowledge(source, 0.0) / 10.0;
        }
        final Skill buildSkill2 = getBuildSkill(floor.getType(), floor.getMaterial(), performer);
        final double check = buildSkill2.skillCheck(buildSkill2.getRealKnowledge(), source, bonus, false, counter);
        floor.buildProgress(1);
        if (WurmPermissions.mayUseGMWand(performer) && (source.getTemplateId() == 315 || source.getTemplateId() == 176) && (Servers.isThisATestServer() || performer.getPower() >= 4)) {
            if (!Servers.isThisATestServer()) {
                performer.sendToLoggers("Building floor with GM powers at [" + floor.getTile().getTileX() + "," + floor.getTile().getTileY() + "] at floor level " + floor.getFloorLevel());
            }
            floor.setFloorState(StructureConstants.FloorState.COMPLETED);
        }
        Server.getInstance().broadCastAction(performer.getName() + " attaches " + message2 + " to a " + floor.getName() + ".", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You attach " + message2 + " to a " + floor.getName() + ".");
        final float oldql = floor.getQualityLevel();
        float qlevel = MethodsStructure.calculateNewQualityLevel(act.getPower(), buildSkill2.getKnowledge(0.0), oldql, getTotalMaterials(floor));
        qlevel = Math.max(1.0f, qlevel);
        floor.setQualityLevel(qlevel);
        if (floor.getState() >= getFinishedState(floor)) {
            if (insta) {
                floor.setQualityLevel(80.0f);
            }
            floor.setFloorState(StructureConstants.FloorState.COMPLETED);
            final VolaTile floorTile = Zones.getOrCreateTile(floor.getTileX(), floor.getTileY(), floor.getLayer() >= 0);
            floorTile.updateFloor(floor);
            final String floorName = Character.toUpperCase(floor.getName().charAt(0)) + floor.getName().substring(1);
            performer.getCommunicator().sendNormalServerMessage(floorName + " completed!");
        }
        try {
            floor.save();
        }
        catch (IOException e2) {
            FloorBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        if (floor.isFinished()) {
            performer.getCommunicator().sendRemoveFromCreationWindow(floor.getId());
        }
        performer.getCommunicator().sendActionResult(true);
        return true;
    }
    
    public static final float getRequiredBuildSkillForFloorLevel(final int floorLevel, final boolean roof) {
        final int fLevel = roof ? (floorLevel - 1) : floorLevel;
        if (fLevel <= 0) {
            return 5.0f;
        }
        switch (fLevel) {
            case 1: {
                return 21.0f;
            }
            case 2: {
                return 30.0f;
            }
            case 3: {
                return 39.0f;
            }
            case 4: {
                return 47.0f;
            }
            case 5: {
                return 55.0f;
            }
            case 6: {
                return 63.0f;
            }
            case 7: {
                return 70.0f;
            }
            case 8: {
                return 77.0f;
            }
            case 9: {
                return 83.0f;
            }
            case 10: {
                return 88.0f;
            }
            case 11: {
                return 92.0f;
            }
            case 12: {
                return 95.0f;
            }
            case 13: {
                return 97.0f;
            }
            case 14: {
                return 98.0f;
            }
            case 15: {
                return 99.0f;
            }
            default: {
                return 200.0f;
            }
        }
    }
    
    public static final float getRequiredBuildSkillForFloorType(final StructureConstants.FloorMaterial floorMaterial) {
        switch (floorMaterial) {
            case WOOD:
            case STANDALONE: {
                return 5.0f;
            }
            case THATCH: {
                return 21.0f;
            }
            case STONE_BRICK: {
                return 21.0f;
            }
            case SANDSTONE_SLAB: {
                return 21.0f;
            }
            case STONE_SLAB: {
                return 21.0f;
            }
            case CLAY_BRICK: {
                return 25.0f;
            }
            case SLATE_SLAB: {
                return 30.0f;
            }
            case MARBLE_SLAB: {
                return 40.0f;
            }
            case METAL_IRON:
            case METAL_COPPER:
            case METAL_STEEL:
            case METAL_SILVER:
            case METAL_GOLD: {
                return 99.0f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static final Skill getBuildSkill(final StructureConstants.FloorType floorType, final StructureConstants.FloorMaterial floorMaterial, final Creature performer) {
        int primSkillTemplate;
        if (floorType == StructureConstants.FloorType.ROOF) {
            primSkillTemplate = getSkillForRoof(floorMaterial);
        }
        else {
            primSkillTemplate = getSkillForFloor(floorMaterial);
        }
        Skill workSkill = null;
        try {
            workSkill = performer.getSkills().getSkill(primSkillTemplate);
        }
        catch (NoSuchSkillException nss) {
            workSkill = performer.getSkills().learn(primSkillTemplate, 1.0f);
        }
        return workSkill;
    }
    
    public static boolean hasValidTool(final StructureConstants.FloorMaterial floorMaterial, final Item source) {
        if (source == null || floorMaterial == null) {
            return false;
        }
        final int tid = source.getTemplateId();
        boolean hasRightTool = false;
        switch (floorMaterial) {
            case METAL_STEEL: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case CLAY_BRICK: {
                hasRightTool = (tid == 493);
                break;
            }
            case SLATE_SLAB: {
                hasRightTool = (tid == 493);
                break;
            }
            case STONE_BRICK: {
                hasRightTool = (tid == 493);
                break;
            }
            case THATCH: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case WOOD: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case SANDSTONE_SLAB: {
                hasRightTool = (tid == 493);
                break;
            }
            case STONE_SLAB: {
                hasRightTool = (tid == 493);
                break;
            }
            case METAL_COPPER: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case METAL_IRON: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case MARBLE_SLAB: {
                hasRightTool = (tid == 493);
                break;
            }
            case METAL_GOLD: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case METAL_SILVER: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case STANDALONE: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            default: {
                FloorBehaviour.logger.log(Level.WARNING, "Enum value '" + floorMaterial.toString() + "' added to FloorMaterial but not to a switch statement in method FloorBehaviour.hasValidTool()");
                break;
            }
        }
        return tid == 315 || tid == 176 || hasRightTool;
    }
    
    public static boolean actionDestroyFloor(final Action act, final Creature performer, final Item source, final Floor floor, final short action, final float counter) {
        if (source.getTemplateId() == 824 || source.getTemplateId() == 0) {
            performer.getCommunicator().sendNormalServerMessage("You will not do any damage to the floor with that.");
            return true;
        }
        if (floor.isIndestructible()) {
            performer.getCommunicator().sendNormalServerMessage("That " + floor.getName() + " looks indestructable.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, act.getNumber(), floor.getTileX(), floor.getTileY())) {
            return true;
        }
        if (action == 524 && MethodsHighways.onHighway(floor)) {
            performer.getCommunicator().sendNormalServerMessage("That floor is protected by the highway.");
            return true;
        }
        if (floor.getFloorState() == StructureConstants.FloorState.BUILDING) {
            if ((WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) {
                floor.destroyOrRevertToPlan();
                performer.getCommunicator().sendNormalServerMessage("You remove a " + floor.getName() + " with your magic wand.");
                Server.getInstance().broadCastAction(performer.getName() + " effortlessly removes a " + floor.getName() + " with a magic wand.", performer, 3);
                return true;
            }
            return MethodsStructure.destroyFloor(action, performer, source, floor, counter);
        }
        else if (floor.getFloorState() == StructureConstants.FloorState.COMPLETED) {
            if ((WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) {
                floor.destroyOrRevertToPlan();
                performer.getCommunicator().sendNormalServerMessage("You remove a " + floor.getName() + " with your magic wand.");
                Server.getInstance().broadCastAction(performer.getName() + " effortlessly removes a " + floor.getName() + " with a magic wand.", performer, 3);
                return true;
            }
            return MethodsStructure.destroyFloor(action, performer, source, floor, counter);
        }
        else {
            if (floor.getFloorState() != StructureConstants.FloorState.PLANNING) {
                return true;
            }
            final VolaTile vtile = Zones.getOrCreateTile(floor.getTileX(), floor.getTileY(), floor.getLayer() >= 0);
            final Structure structure = vtile.getStructure();
            if (structure.wouldCreateFlyingStructureIfRemoved(floor)) {
                performer.getCommunicator().sendNormalServerMessage("Removing that would cause a collapsing section.");
                return true;
            }
            floor.destroy();
            performer.getCommunicator().sendNormalServerMessage("You remove a plan for a new floor.");
            Server.getInstance().broadCastAction(performer.getName() + " removes a plan for a new floor.", performer, 3);
            return true;
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final boolean onSurface, final Floor floor, final int encodedTile, final short action, final float counter) {
        if (action == 523 || action == 522) {
            boolean done = false;
            if (floor.getType() != StructureConstants.FloorType.OPENING || (!floor.isFinished() && action != 523)) {
                performer.getCommunicator().sendNormalServerMessage("Move a little bit closer to the ladder.", (byte)3);
                return true;
            }
            if (floor.getFloorLevel() == performer.getFloorLevel()) {
                if (action != 523) {
                    return true;
                }
            }
            else {
                if (floor.getFloorLevel() != performer.getFloorLevel() + 1) {
                    return true;
                }
                if (action != 522) {
                    return true;
                }
            }
            if (performer.getVehicle() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("You can't climb right now.");
                return true;
            }
            if (performer.getFollowers().length > 0) {
                performer.getCommunicator().sendNormalServerMessage("You stop leading.", (byte)3);
                performer.stopLeading();
            }
            if (counter == 1.0f) {
                final float qx = performer.getPosX() % 4.0f;
                final float qy = performer.getPosY() % 4.0f;
                boolean getCloser = false;
                if (performer.getTileX() != floor.getTileX() || performer.getTileY() != floor.getTileY()) {
                    performer.getCommunicator().sendNormalServerMessage("You are too far away to climb that.", (byte)3);
                    return true;
                }
                if (floor.getMaterial() == StructureConstants.FloorMaterial.STANDALONE) {
                    switch (floor.getDir()) {
                        case 0: {
                            getCloser = (qx < 1.0f || qx > 3.0f || qy < 3.0f);
                            break;
                        }
                        case 6: {
                            getCloser = (qx < 1.0f || qy < 1.0f || qy > 3.0f);
                            break;
                        }
                        case 4: {
                            getCloser = (qx < 1.0f || qx > 3.0f || qy > 1.0f);
                            break;
                        }
                        case 2: {
                            getCloser = (qx > 1.0f || qy < 1.0f || qy > 3.0f);
                            break;
                        }
                        default: {
                            getCloser = true;
                            break;
                        }
                    }
                }
                else {
                    getCloser = (qx < 1.0f || qx > 3.0f || qy < 1.0f || qy > 3.0f);
                }
                if (getCloser) {
                    performer.getCommunicator().sendNormalServerMessage("Move a little bit closer to the ladder.", (byte)3);
                    return true;
                }
                performer.sendActionControl("Climbing", true, 22);
                if (action == 523) {
                    int groundoffset = 3;
                    if (performer.getFloorLevel() - 1 == 0) {
                        final VolaTile t = performer.getCurrentTile();
                        if (t.getFloors(-10, 10).length == 0) {
                            groundoffset = 0;
                        }
                    }
                    else {
                        final VolaTile t = performer.getCurrentTile();
                        final int tfloor = (performer.getFloorLevel() - 1) * 30;
                        if (t.getFloors(tfloor - 10, tfloor + 10).length == 0) {
                            performer.getCommunicator().sendNormalServerMessage("You can't climb down there.", (byte)3);
                            return true;
                        }
                    }
                    performer.getCommunicator().setGroundOffset(groundoffset + (performer.getFloorLevel() - 1) * 30, false);
                }
                else if (action == 522) {
                    performer.getCommunicator().setGroundOffset((performer.getFloorLevel() + 1) * 30, false);
                }
            }
            else if (counter > 2.0f) {
                done = true;
            }
            return done;
        }
        else if (action == 177 || action == 178) {
            if (!floor.isNotTurnable()) {
                return MethodsStructure.rotateFloor(performer, floor, counter, act);
            }
            performer.getCommunicator().sendNormalServerMessage("Looks like that floor is stuck in place.");
            return true;
        }
        else {
            if (action == 607) {
                if (!floor.isFinished()) {
                    performer.getCommunicator().sendAddFloorRoofToCreationWindow(floor, -10L);
                }
                return true;
            }
            return super.action(act, performer, floor.getTileX(), floor.getTileY(), onSurface, Zones.getTileIntForTile(floor.getTileX(), floor.getTileY(), 0), action, counter);
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final boolean onSurface, final Floor floor, final int encodedTile, final short action, final float counter) {
        if (action == 1) {
            return this.examine(performer, floor);
        }
        if (action == 607) {
            if (!floor.isFinished()) {
                performer.getCommunicator().sendAddFloorRoofToCreationWindow(floor, -10L);
            }
            return true;
        }
        if (action == 523 || action == 522 || action == 177 || action == 178) {
            return this.action(act, performer, onSurface, floor, encodedTile, action, counter);
        }
        if (source == null) {
            return super.action(act, performer, floor.getTileX(), floor.getTileY(), onSurface, Zones.getTileIntForTile(floor.getTileX(), floor.getTileY(), 0), action, counter);
        }
        if (action == 179) {
            if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && WurmPermissions.mayUseGMWand(performer)) {
                Methods.sendSummonQuestion(performer, source, floor.getTileX(), floor.getTileY(), floor.getStructureId());
            }
            return true;
        }
        if (action == 684) {
            if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                Methods.sendItemRestrictionManagement(performer, floor, floor.getId());
            }
            else {
                FloorBehaviour.logger.log(Level.WARNING, performer.getName() + " hacking the protocol by trying to set the restrictions of " + floor + ", counter: " + counter + '!');
            }
            return true;
        }
        if (!isConstructionAction(action)) {
            return super.action(act, performer, source, floor.getTileX(), floor.getTileY(), onSurface, floor.getHeightOffset(), Zones.getTileIntForTile(floor.getTileX(), floor.getTileY(), 0), action, counter);
        }
        if (action == 524 || action == 525) {
            return actionDestroyFloor(act, performer, source, floor, action, counter);
        }
        if (action == 193) {
            return (Servers.localServer.challengeServer && performer.getEnemyPresense() > 0) || floor.isNoRepair() || MethodsStructure.repairFloor(performer, source, floor, counter, act);
        }
        if (action == 192) {
            return floor.isNoImprove() || MethodsStructure.improveFloor(performer, source, floor, counter, act);
        }
        if (action == 169) {
            if (floor.getFloorState() != StructureConstants.FloorState.BUILDING) {
                performer.getCommunicator().sendNormalServerMessage("The floor is in an invalid state to be continued.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            if (floorBuilding(act, performer, source, floor, action, counter)) {
                performer.getCommunicator().sendAddFloorRoofToCreationWindow(floor, floor.getId());
                return true;
            }
            return false;
        }
        else if (action == 508) {
            if (floor.isRoof()) {
                performer.getCommunicator().sendNormalServerMessage("You can't plan above the " + floor.getName() + ".");
                return true;
            }
            return MethodsStructure.floorPlanAbove(performer, source, floor.getTileX(), floor.getTileY(), encodedTile, performer.getLayer(), counter, act, StructureConstants.FloorType.FLOOR);
        }
        else {
            if (action == 507) {
                return MethodsStructure.floorPlanRoof(performer, source, floor.getTileX(), floor.getTileY(), encodedTile, floor.getLayer(), counter, act);
            }
            if (action == 514) {
                return MethodsStructure.floorPlanAbove(performer, source, floor.getTileX(), floor.getTileY(), encodedTile, performer.getLayer(), counter, act, StructureConstants.FloorType.DOOR);
            }
            if (action == 515) {
                if (floor.isRoof()) {
                    performer.getCommunicator().sendNormalServerMessage("You can't plan above the " + floor.getName() + ".");
                    return true;
                }
                return MethodsStructure.floorPlanAbove(performer, source, floor.getTileX(), floor.getTileY(), encodedTile, performer.getLayer(), counter, act, StructureConstants.FloorType.OPENING);
            }
            else {
                if (action != 659 && action != 704 && action != 705 && action != 706 && action != 709 && action != 710 && action != 711 && action != 712 && action != 713 && action != 714 && action != 715) {
                    return action - 20000 < 0 || this.buildAction(act, performer, source, floor, action, counter);
                }
                if (floor.isRoof()) {
                    performer.getCommunicator().sendNormalServerMessage("You can't plan above the " + floor.getName() + ".");
                    return true;
                }
                StructureConstants.FloorType ft;
                if (action == 704) {
                    ft = StructureConstants.FloorType.WIDE_STAIRCASE;
                }
                else if (action == 705) {
                    ft = StructureConstants.FloorType.RIGHT_STAIRCASE;
                }
                else if (action == 706) {
                    ft = StructureConstants.FloorType.LEFT_STAIRCASE;
                }
                else if (action == 709) {
                    ft = StructureConstants.FloorType.CLOCKWISE_STAIRCASE;
                }
                else if (action == 710) {
                    ft = StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE;
                }
                else if (action == 711) {
                    ft = StructureConstants.FloorType.CLOCKWISE_STAIRCASE_WITH;
                }
                else if (action == 712) {
                    ft = StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE_WITH;
                }
                else if (action == 713) {
                    ft = StructureConstants.FloorType.WIDE_STAIRCASE_RIGHT;
                }
                else if (action == 714) {
                    ft = StructureConstants.FloorType.WIDE_STAIRCASE_LEFT;
                }
                else if (action == 715) {
                    ft = StructureConstants.FloorType.WIDE_STAIRCASE_BOTH;
                }
                else {
                    ft = StructureConstants.FloorType.STAIRCASE;
                }
                return MethodsStructure.floorPlanAbove(performer, source, floor.getTileX(), floor.getTileY(), encodedTile, performer.getLayer(), counter, act, ft);
            }
        }
    }
    
    public static boolean isConstructionAction(final short action) {
        if (action - 20000 >= 0) {
            return true;
        }
        switch (action) {
            case 169:
            case 192:
            case 193:
            case 507:
            case 508:
            case 514:
            case 515:
            case 524:
            case 525:
            case 659:
            case 704:
            case 705:
            case 706:
            case 709:
            case 710:
            case 711:
            case 712:
            case 713:
            case 714:
            case 715: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean examine(final Creature performer, final Floor floor) {
        String materials = "";
        if (floor.getFloorState() == StructureConstants.FloorState.BUILDING) {
            materials = buildRequiredMaterialString(floor, true);
            performer.getCommunicator().sendNormalServerMessage("You see a " + floor.getName() + " under construction. The " + floor.getName() + " requires " + materials + " to be finished.");
        }
        else {
            if (floor.getFloorState() == StructureConstants.FloorState.PLANNING) {
                performer.getCommunicator().sendNormalServerMessage("You see plans for a " + floor.getName() + ".");
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("It is a normal " + floor.getName() + " made of " + getMaterialDescription(floor).toLowerCase() + ".");
        }
        sendQlString(performer, floor);
        return true;
    }
    
    private static final String getMaterialDescription(final Floor floor) {
        if (floor.getType() != StructureConstants.FloorType.ROOF) {
            return floor.getMaterial().getName();
        }
        switch (floor.getMaterial()) {
            case CLAY_BRICK: {
                return "pottery shingles";
            }
            case SLATE_SLAB: {
                return "slate shingles";
            }
            case WOOD: {
                return "wood shingles";
            }
            default: {
                return floor.getMaterial().getName().toLowerCase();
            }
        }
    }
    
    static final void sendQlString(final Creature performer, final Floor floor) {
        performer.getCommunicator().sendNormalServerMessage("QL = " + floor.getCurrentQL() + ", dam=" + floor.getDamage() + ".");
        if (performer.getPower() > 0) {
            performer.getCommunicator().sendNormalServerMessage("id: " + floor.getId() + " " + floor.getTileX() + "," + floor.getTileY() + " height: " + floor.getHeightOffset() + " " + floor.getMaterial().getName() + " " + floor.getType().getName() + " (" + floor.getFloorState().toString().toLowerCase() + ").");
        }
    }
    
    private static final String buildRequiredMaterialString(final Floor floor, final boolean detailed) {
        String description = new String();
        int numMats = 0;
        final List<BuildMaterial> billOfMaterial = getRequiredMaterialsAtState(floor);
        int maxMats = 0;
        for (final BuildMaterial mat : billOfMaterial) {
            if (mat.getNeededQuantity() > 0) {
                ++maxMats;
            }
        }
        for (final BuildMaterial mat : billOfMaterial) {
            if (mat.getNeededQuantity() > 0) {
                ++numMats;
                ItemTemplate template = null;
                try {
                    template = ItemTemplateFactory.getInstance().getTemplate(mat.getTemplateId());
                }
                catch (NoSuchTemplateException e) {
                    FloorBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (numMats > 1) {
                    if (numMats < maxMats) {
                        description += ", ";
                    }
                    else {
                        description += " and ";
                    }
                }
                if (template != null) {
                    if (detailed) {
                        description = description + mat.getNeededQuantity() + " ";
                    }
                    if (template.sizeString.length() > 0) {
                        description += template.sizeString;
                    }
                    description += ((mat.getNeededQuantity() > 1) ? template.getPlural() : template.getName());
                }
                if (description.length() != 0) {
                    continue;
                }
                description = "unknown quantities of unknown materials";
            }
        }
        if (description.length() == 0) {
            description = "no materials";
        }
        return description;
    }
    
    private static String getBuildSound(final Floor floor) {
        String soundToPlay = "";
        switch (floor.getMaterial()) {
            case CLAY_BRICK:
            case SLATE_SLAB:
            case STONE_BRICK:
            case SANDSTONE_SLAB:
            case STONE_SLAB:
            case MARBLE_SLAB: {
                soundToPlay = "sound.work.masonry";
                break;
            }
            case METAL_IRON:
            case METAL_COPPER:
            case METAL_STEEL:
            case METAL_SILVER:
            case METAL_GOLD: {
                soundToPlay = "sound.work.smithing.metal";
                break;
            }
            default: {
                soundToPlay = ((Server.rand.nextInt(2) == 0) ? "sound.work.carpentry.mallet1" : "sound.work.carpentry.mallet2");
                break;
            }
        }
        return soundToPlay;
    }
    
    private static void damageTool(final Creature performer, final Floor floor, final Item source) {
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
    
    private static Skill getToolSkill(final Floor floor, final Creature performer, final Item source) {
        Skill toolSkill = null;
        try {
            toolSkill = performer.getSkills().getSkill(source.getPrimarySkill());
        }
        catch (NoSuchSkillException nss) {
            try {
                toolSkill = performer.getSkills().learn(source.getPrimarySkill(), 1.0f);
            }
            catch (NoSuchSkillException ex) {}
        }
        return toolSkill;
    }
    
    static {
        logger = Logger.getLogger(FloorBehaviour.class.getName());
    }
}
