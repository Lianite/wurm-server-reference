// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import com.wurmonline.server.behaviours.FloorBehaviour;
import com.wurmonline.server.behaviours.BuildMaterial;
import java.util.ArrayList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.shared.constants.StructureConstants;

public enum RoofFloorEnum
{
    WOOD_SHINGLE_ROOF(StructureConstants.FloorType.ROOF, "Wood shingle", false, StructureConstants.FloorMaterial.WOOD, (short)60), 
    SLATE_SHINGLE_ROOF(StructureConstants.FloorType.ROOF, "Slate shingle", false, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    CLAY_SHIGLE_ROOF(StructureConstants.FloorType.ROOF, "Pottery shingle", false, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    TATCHED_ROOF(StructureConstants.FloorType.ROOF, "Thatched", false, StructureConstants.FloorMaterial.THATCH, (short)60), 
    WOODEN_PLANK_FLOOR(StructureConstants.FloorType.FLOOR, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    WOODEN_PLANK_OPENING(StructureConstants.FloorType.OPENING, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    CLAY_FLOOR(StructureConstants.FloorType.FLOOR, "Pottery brick", true, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    CLAY_OPENING(StructureConstants.FloorType.OPENING, "Pottery brick", true, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    SANDSTONE_SLAB_FLOOR(StructureConstants.FloorType.FLOOR, "Sandstone slab", true, StructureConstants.FloorMaterial.SANDSTONE_SLAB, (short)60), 
    SANDSTONE_SLAB_OPENING(StructureConstants.FloorType.OPENING, "Sandstone slab", true, StructureConstants.FloorMaterial.SANDSTONE_SLAB, (short)60), 
    STONE_SLAB_FLOOR(StructureConstants.FloorType.FLOOR, "Stone slab", true, StructureConstants.FloorMaterial.STONE_SLAB, (short)60), 
    STONE_SLAB_OPENING(StructureConstants.FloorType.OPENING, "Stone slab", true, StructureConstants.FloorMaterial.STONE_SLAB, (short)60), 
    STONE_BRICK_FLOOR(StructureConstants.FloorType.FLOOR, "Stone brick", true, StructureConstants.FloorMaterial.STONE_BRICK, (short)60), 
    STONE_BRICK_OPENING(StructureConstants.FloorType.OPENING, "Stone brick", true, StructureConstants.FloorMaterial.STONE_BRICK, (short)60), 
    SLATE_SLAB_FLOOR(StructureConstants.FloorType.FLOOR, "Slate slab", true, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    SLATE_SLAB_OPENING(StructureConstants.FloorType.OPENING, "Slate slab", true, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    MARBLE_FLOOR(StructureConstants.FloorType.FLOOR, "Marble slab", true, StructureConstants.FloorMaterial.MARBLE_SLAB, (short)60), 
    MARBLE_OPENING(StructureConstants.FloorType.OPENING, "Marble slab", true, StructureConstants.FloorMaterial.MARBLE_SLAB, (short)60), 
    WOODEN_PLANK_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    CLAY_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Pottery brick", true, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    SANDSTONE_SLAB_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Sandstone slab", true, StructureConstants.FloorMaterial.SANDSTONE_SLAB, (short)60), 
    STONE_SLAB_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Stone slab", true, StructureConstants.FloorMaterial.STONE_SLAB, (short)60), 
    STONE_BRICK_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Stone brick", true, StructureConstants.FloorMaterial.STONE_BRICK, (short)60), 
    SLATE_SLAB_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Slate slab", true, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    MARBLE_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Marble slab", true, StructureConstants.FloorMaterial.MARBLE_SLAB, (short)60), 
    STANDALONE_STAIRCASE(StructureConstants.FloorType.STAIRCASE, "Standalone", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WIDE_STAIRCASE(StructureConstants.FloorType.WIDE_STAIRCASE, "No banisters", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WIDE_STAIRCASE_RIGHT(StructureConstants.FloorType.WIDE_STAIRCASE_RIGHT, "Right banisters", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WIDE_STAIRCASE_LEFT(StructureConstants.FloorType.WIDE_STAIRCASE_LEFT, "Left banisters", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WIDE_STAIRCASE_BOTH(StructureConstants.FloorType.WIDE_STAIRCASE_BOTH, "Both banisters", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WOODEN_PLANK_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    CLAY_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Pottery brick", true, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    SANDSTONE_SLAB_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Sandstone slab", true, StructureConstants.FloorMaterial.SANDSTONE_SLAB, (short)60), 
    STONE_SLAB_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Stone slab", true, StructureConstants.FloorMaterial.STONE_SLAB, (short)60), 
    STONE_BRICK_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Stone brick", true, StructureConstants.FloorMaterial.STONE_BRICK, (short)60), 
    SLATE_SLAB_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Slate slab", true, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    MARBLE_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Marble slab", true, StructureConstants.FloorMaterial.MARBLE_SLAB, (short)60), 
    STANDALONE_RIGHT_STAIRCASE(StructureConstants.FloorType.RIGHT_STAIRCASE, "Standalone", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WOODEN_PLANK_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    CLAY_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Pottery brick", true, StructureConstants.FloorMaterial.CLAY_BRICK, (short)60), 
    SANDSTONE_SLAB_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Sandstone slab", true, StructureConstants.FloorMaterial.SANDSTONE_SLAB, (short)60), 
    STONE_SLAB_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Stone slab", true, StructureConstants.FloorMaterial.STONE_SLAB, (short)60), 
    STONE_BRICK_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Stone brick", true, StructureConstants.FloorMaterial.STONE_BRICK, (short)60), 
    SLATE_SLAB_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Slate slab", true, StructureConstants.FloorMaterial.SLATE_SLAB, (short)60), 
    MARBLE_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Marble slab", true, StructureConstants.FloorMaterial.MARBLE_SLAB, (short)60), 
    STANDALONE_LEFT_STAIRCASE(StructureConstants.FloorType.LEFT_STAIRCASE, "Standalone", true, StructureConstants.FloorMaterial.STANDALONE, (short)60), 
    WOODEN_PLANK_CLOCKWISE_STAIRCASE(StructureConstants.FloorType.CLOCKWISE_STAIRCASE, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    WOODEN_PLANK_ANTICLOCKWISE_STAIRCASE(StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    WOODEN_PLANK_CLOCKWISE_STAIRCASE_WITH(StructureConstants.FloorType.CLOCKWISE_STAIRCASE_WITH, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    WOODEN_PLANK_ANTICLOCKWISE_STAIRCASE_WITH(StructureConstants.FloorType.ANTICLOCKWISE_STAIRCASE_WITH, "Wooden plank", true, StructureConstants.FloorMaterial.WOOD, (short)60), 
    UNKNOWN(StructureConstants.FloorType.OPENING, "Unkown", true, StructureConstants.FloorMaterial.WOOD, (short)60);
    
    private final StructureConstants.FloorType type;
    private final boolean isFloor;
    private final StructureConstants.FloorMaterial material;
    private final short actionId;
    private final String name;
    private final String actionString;
    private final short icon;
    private static int[] emptyArr;
    
    private RoofFloorEnum(final StructureConstants.FloorType type, final String name, final boolean isFloor, final StructureConstants.FloorMaterial material, final short icon) {
        this.type = type;
        this.isFloor = isFloor;
        this.material = material;
        this.actionId = (short)(20000 + this.material.getCode());
        this.name = StringUtil.format("%s %s", name, this.type.getName());
        this.actionString = StringUtil.format("%s %s", "Building", StringUtil.toLowerCase(this.name));
        this.icon = icon;
    }
    
    public final ActionEntry createActionEntry() {
        return ActionEntry.createEntry(this.actionId, this.name, this.actionString, RoofFloorEnum.emptyArr);
    }
    
    public final StructureConstants.FloorType getType() {
        return this.type;
    }
    
    public final StructureConstants.FloorMaterial getMaterial() {
        return this.material;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final boolean isFloor() {
        return this.isFloor;
    }
    
    public final short getIcon() {
        return this.icon;
    }
    
    public final short getActionId() {
        return this.actionId;
    }
    
    public final boolean isValidTool(final Item tool) {
        if (tool == null) {
            return false;
        }
        if (tool.getTemplateId() == 176 || tool.getTemplateId() == 315) {
            return true;
        }
        final int[] validToolsForMaterial;
        final int[] valid = validToolsForMaterial = getValidToolsForMaterial(this.material);
        for (final int v : validToolsForMaterial) {
            if (v == tool.getTemplateId()) {
                return true;
            }
        }
        return false;
    }
    
    public static final List<RoofFloorEnum> getRoofsByTool(final Item tool) {
        final List<RoofFloorEnum> list = new ArrayList<RoofFloorEnum>();
        if (tool == null) {
            return list;
        }
        for (final RoofFloorEnum en : values()) {
            if (!en.isFloor() && en.isValidTool(tool) && en != RoofFloorEnum.UNKNOWN) {
                list.add(en);
            }
        }
        return list;
    }
    
    public static final RoofFloorEnum getByFloorType(final Floor floor) {
        for (final RoofFloorEnum en : values()) {
            if (en.getType() == floor.getType() && en.getMaterial() == floor.getMaterial()) {
                return en;
            }
        }
        return RoofFloorEnum.UNKNOWN;
    }
    
    public static List<BuildMaterial> getMaterialsNeeded(final Floor floor) {
        final List<BuildMaterial> billOfMaterial = FloorBehaviour.getRequiredMaterialsAtState(floor);
        final List<BuildMaterial> needed = new ArrayList<BuildMaterial>();
        for (final BuildMaterial mat : billOfMaterial) {
            if (mat.getNeededQuantity() > 0) {
                needed.add(mat);
            }
        }
        return needed;
    }
    
    public List<BuildMaterial> getTotalMaterialsNeeded() {
        List<BuildMaterial> billOfMaterial;
        if (this.type == StructureConstants.FloorType.ROOF) {
            billOfMaterial = FloorBehaviour.getRequiredMaterialsForRoof(this.material);
        }
        else {
            billOfMaterial = FloorBehaviour.getRequiredMaterialsForFloor(this.type, this.material);
        }
        return billOfMaterial;
    }
    
    public static final boolean canBuildFloorRoof(final Floor floor, final RoofFloorEnum en, final Creature performer) {
        final Skill skill = FloorBehaviour.getBuildSkill(en.getType(), en.getMaterial(), performer);
        return skill != null && skill.getKnowledge(0.0) >= FloorBehaviour.getRequiredBuildSkillForFloorType(en.getMaterial()) && FloorBehaviour.mayPlanAtLevel(performer, floor.getFloorLevel(), skill, floor.isRoof(), false) && FloorBehaviour.getRequiredBuildSkillForFloorLevel(floor.getFloorLevel(), floor.isRoof()) <= skill.getKnowledge(0.0);
    }
    
    public final int getNeededSkillNumber() {
        if (this.type == StructureConstants.FloorType.ROOF) {
            return FloorBehaviour.getSkillForRoof(this.material);
        }
        return FloorBehaviour.getSkillForFloor(this.material);
    }
    
    public static final Floor getFloorOrRoofFromId(final long floorId) {
        final int x = Tiles.decodeTileX(floorId);
        final int y = Tiles.decodeTileY(floorId);
        final int heightOffset = Tiles.decodeHeightOffset(floorId);
        final byte layer = Tiles.decodeLayer(floorId);
        final Floor[] floors = Zones.getFloorsAtTile(x, y, heightOffset, heightOffset, layer);
        if (floors != null && floors.length > 0) {
            return floors[0];
        }
        return null;
    }
    
    public static final List<RoofFloorEnum> getFloorByToolAndType(final Item tool, final StructureConstants.FloorType fType) {
        final List<RoofFloorEnum> list = new ArrayList<RoofFloorEnum>();
        if (tool == null) {
            return list;
        }
        for (final RoofFloorEnum en : values()) {
            if (en.isFloor() && en != RoofFloorEnum.UNKNOWN && en.getType() == fType && en.isValidTool(tool)) {
                list.add(en);
            }
        }
        return list;
    }
    
    public static final RoofFloorEnum getFloorRoofByTypeAndMaterial(final StructureConstants.FloorType type, final StructureConstants.FloorMaterial material) {
        for (final RoofFloorEnum en : values()) {
            if (en.getType() == type && en.getMaterial() == material) {
                return en;
            }
        }
        return RoofFloorEnum.UNKNOWN;
    }
    
    public static final int[] getValidToolsForMaterial(final StructureConstants.FloorMaterial material) {
        switch (material) {
            case METAL_STEEL: {
                return new int[] { 62, 63 };
            }
            case CLAY_BRICK: {
                return new int[] { 493 };
            }
            case SLATE_SLAB: {
                return new int[] { 493 };
            }
            case STONE_BRICK: {
                return new int[] { 493 };
            }
            case THATCH: {
                return new int[] { 62, 63 };
            }
            case WOOD: {
                return new int[] { 62, 63 };
            }
            case STONE_SLAB: {
                return new int[] { 493 };
            }
            case METAL_COPPER: {
                return new int[] { 62, 63 };
            }
            case METAL_IRON: {
                return new int[] { 62, 63 };
            }
            case SANDSTONE_SLAB: {
                return new int[] { 493 };
            }
            case MARBLE_SLAB: {
                return new int[] { 493 };
            }
            case METAL_GOLD: {
                return new int[] { 62, 63 };
            }
            case METAL_SILVER: {
                return new int[] { 62, 63 };
            }
            case STANDALONE: {
                return new int[] { 62, 63 };
            }
            default: {
                return new int[0];
            }
        }
    }
    
    static {
        RoofFloorEnum.emptyArr = new int[0];
    }
}
