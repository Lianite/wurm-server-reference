// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public interface StructureConstants
{
    public static final byte STRUCTURE_HOUSE = 0;
    public static final byte STRUCTURE_BRIDGE = 1;
    
    public enum FloorMaterial
    {
        WOOD((byte)0, "Wood", "wood"), 
        STONE_BRICK((byte)1, "Stone brick", "stone_brick"), 
        SANDSTONE_SLAB((byte)2, "Sandstone slab", "sandstone_slab"), 
        SLATE_SLAB((byte)3, "Slate slab", "slate_slab"), 
        THATCH((byte)4, "Thatch", "thatch"), 
        METAL_IRON((byte)5, "Iron", "metal_iron"), 
        METAL_STEEL((byte)6, "Steel", "metal_steel"), 
        METAL_COPPER((byte)7, "Copper", "metal_copper"), 
        CLAY_BRICK((byte)8, "Clay brick", "clay_brick"), 
        METAL_GOLD((byte)9, "Gold", "metal_gold"), 
        METAL_SILVER((byte)10, "Silver", "metal_silver"), 
        MARBLE_SLAB((byte)11, "Marble slab", "marble_slab"), 
        STANDALONE((byte)12, "Standalone", "standalone"), 
        STONE_SLAB((byte)13, "Stone slab", "stone_slab");
        
        private byte material;
        private String name;
        private String modelName;
        private static final FloorMaterial[] types;
        
        private FloorMaterial(final byte newMaterial, final String newName, final String newModelName) {
            this.material = newMaterial;
            this.name = newName;
            this.modelName = newModelName;
        }
        
        public byte getCode() {
            return this.material;
        }
        
        public static FloorMaterial fromByte(final byte typeByte) {
            for (int i = 0; i < FloorMaterial.types.length; ++i) {
                if (typeByte == FloorMaterial.types[i].getCode()) {
                    return FloorMaterial.types[i];
                }
            }
            return null;
        }
        
        public final String getName() {
            return this.name;
        }
        
        public final String getModelName() {
            return this.modelName;
        }
        
        public static final String getTextureName(final FloorType type, final FloorMaterial material) {
            return FloorMappings.getMapping(type, material);
        }
        
        static {
            types = values();
        }
    }
    
    public enum FloorType
    {
        UNKNOWN((byte)100, false, "unknown"), 
        FLOOR((byte)10, false, "floor"), 
        DOOR((byte)11, false, "hatch"), 
        OPENING((byte)12, false, "opening"), 
        ROOF((byte)13, false, "roof"), 
        SOLID((byte)14, false, "solid"), 
        STAIRCASE((byte)15, true, "staircase"), 
        WIDE_STAIRCASE((byte)16, true, "staircase, wide"), 
        RIGHT_STAIRCASE((byte)17, true, "staircase, right"), 
        LEFT_STAIRCASE((byte)18, true, "staircase, left"), 
        WIDE_STAIRCASE_RIGHT((byte)19, true, "staircase, wide with right banisters"), 
        WIDE_STAIRCASE_LEFT((byte)20, true, "staircase, wide with left banisters"), 
        WIDE_STAIRCASE_BOTH((byte)21, true, "staircase, wide with both banisters"), 
        CLOCKWISE_STAIRCASE((byte)22, true, "staircase, clockwise spiral"), 
        ANTICLOCKWISE_STAIRCASE((byte)23, true, "staircase, counter clockwise spiral"), 
        CLOCKWISE_STAIRCASE_WITH((byte)24, true, "staircase, clockwise spiral with banisters"), 
        ANTICLOCKWISE_STAIRCASE_WITH((byte)25, true, "staircase, counter clockwise spiral with banisters");
        
        private byte type;
        private String name;
        private boolean isStair;
        private static final FloorType[] types;
        
        private FloorType(final byte newType, final boolean newIsStair, final String newName) {
            this.type = newType;
            this.name = newName;
            this.isStair = newIsStair;
        }
        
        public byte getCode() {
            return this.type;
        }
        
        public boolean isStair() {
            return this.isStair;
        }
        
        public static FloorType fromByte(final byte typeByte) {
            for (int i = 0; i < FloorType.types.length; ++i) {
                if (typeByte == FloorType.types[i].getCode()) {
                    return FloorType.types[i];
                }
            }
            return FloorType.UNKNOWN;
        }
        
        public final String getName() {
            return this.name;
        }
        
        public static final String getModelName(final FloorType type, final FloorMaterial material, final FloorState state) {
            if (type == FloorType.STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.CLOCKWISE_STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.clockwise.none.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.clockwise.none.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.clockwise.none." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.CLOCKWISE_STAIRCASE_WITH) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.clockwise.with.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.clockwise.with.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.clockwise.with." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.ANTICLOCKWISE_STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.anticlockwise.none.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.anticlockwise.none.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.anticlockwise.none." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.ANTICLOCKWISE_STAIRCASE_WITH) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.anticlockwise.with.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.anticlockwise.with.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.anticlockwise.with." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.WIDE_STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.wide.none.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.wide.none.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.wide.none." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.WIDE_STAIRCASE_LEFT) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.wide.left.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.wide.left.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.wide.left." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.WIDE_STAIRCASE_RIGHT) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.wide.right.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.wide.right.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.wide.right." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.WIDE_STAIRCASE_BOTH) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.wide.both.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.wide.both.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.wide.both." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.RIGHT_STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.right.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.right.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.right." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else if (type == FloorType.LEFT_STAIRCASE) {
                if (state == FloorState.PLANNING) {
                    return "model.structure.staircase.left.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.staircase.left.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.staircase.left." + material.toString().toLowerCase(Locale.ENGLISH);
            }
            else {
                if (type != FloorType.OPENING) {
                    String modelName;
                    if (state == FloorState.PLANNING) {
                        modelName = "model.structure.floor.plan";
                    }
                    else if (state == FloorState.BUILDING) {
                        modelName = "model.structure.floor.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                    }
                    else if (type == FloorType.ROOF) {
                        modelName = "model.structure.roof." + material.toString().toLowerCase(Locale.ENGLISH);
                    }
                    else {
                        modelName = "model.structure.floor." + material.toString().toLowerCase(Locale.ENGLISH);
                    }
                    if (type == FloorType.UNKNOWN) {
                        modelName = "model.structure.floor.plan";
                    }
                    return modelName;
                }
                if (state == FloorState.PLANNING) {
                    return "model.structure.floor.opening.plan";
                }
                if (state == FloorState.BUILDING) {
                    return "model.structure.floor.opening.plan." + material.toString().toLowerCase(Locale.ENGLISH);
                }
                return "model.structure.floor.opening." + material.toString().toLowerCase(Locale.ENGLISH);
            }
        }
        
        public static final int getIconId(final FloorType type, final FloorMaterial material, final FloorState state) {
            if (state == FloorState.PLANNING || state == FloorState.BUILDING) {
                return 60;
            }
            if (type == FloorType.ROOF) {
                return getRoofIconId(material);
            }
            return getFloorIconId(material);
        }
        
        private static int getFloorIconId(final FloorMaterial material) {
            int returnId = 60;
            switch (material) {
                case WOOD: {
                    returnId = 60;
                    break;
                }
                case STONE_BRICK: {
                    returnId = 60;
                    break;
                }
                case CLAY_BRICK: {
                    returnId = 60;
                    break;
                }
                case SLATE_SLAB: {
                    returnId = 60;
                    break;
                }
                case STONE_SLAB: {
                    returnId = 60;
                    break;
                }
                case THATCH: {
                    returnId = 60;
                    break;
                }
                case METAL_IRON: {
                    returnId = 60;
                    break;
                }
                case METAL_STEEL: {
                    returnId = 60;
                    break;
                }
                case METAL_COPPER: {
                    returnId = 60;
                    break;
                }
                case METAL_GOLD: {
                    returnId = 60;
                    break;
                }
                case METAL_SILVER: {
                    returnId = 60;
                    break;
                }
                case SANDSTONE_SLAB: {
                    returnId = 60;
                    break;
                }
                case MARBLE_SLAB: {
                    returnId = 60;
                    break;
                }
                case STANDALONE: {
                    returnId = 60;
                    break;
                }
                default: {
                    returnId = 60;
                    break;
                }
            }
            return returnId;
        }
        
        private static int getRoofIconId(final FloorMaterial material) {
            int returnId = 60;
            switch (material) {
                case WOOD: {
                    returnId = 60;
                    break;
                }
                case STONE_BRICK: {
                    returnId = 60;
                    break;
                }
                case CLAY_BRICK: {
                    returnId = 60;
                    break;
                }
                case SLATE_SLAB: {
                    returnId = 60;
                    break;
                }
                case STONE_SLAB: {
                    returnId = 60;
                    break;
                }
                case THATCH: {
                    returnId = 60;
                    break;
                }
                case METAL_IRON: {
                    returnId = 60;
                    break;
                }
                case METAL_STEEL: {
                    returnId = 60;
                    break;
                }
                case METAL_COPPER: {
                    returnId = 60;
                    break;
                }
                case METAL_GOLD: {
                    returnId = 60;
                    break;
                }
                case METAL_SILVER: {
                    returnId = 60;
                    break;
                }
                case MARBLE_SLAB: {
                    returnId = 60;
                    break;
                }
                case SANDSTONE_SLAB: {
                    returnId = 60;
                    break;
                }
                default: {
                    returnId = 60;
                    break;
                }
            }
            return returnId;
        }
        
        static {
            types = values();
        }
    }
    
    public enum FloorState
    {
        PLANNING((byte)(-1)), 
        BUILDING((byte)0), 
        COMPLETED((byte)127);
        
        private byte state;
        private static final FloorState[] types;
        
        private FloorState(final byte newState) {
            this.state = newState;
        }
        
        public byte getCode() {
            return this.state;
        }
        
        public static FloorState fromByte(final byte floorStateByte) {
            for (int i = 0; i < FloorState.types.length; ++i) {
                if (floorStateByte == FloorState.types[i].getCode()) {
                    return FloorState.types[i];
                }
            }
            return FloorState.BUILDING;
        }
        
        static {
            types = values();
        }
    }
    
    public static class Pair<K, V>
    {
        private final K key;
        private final V value;
        
        public Pair(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        public final K getKey() {
            return this.key;
        }
        
        public final V getValue() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Pair)) {
                return false;
            }
            final Pair mapping = (Pair)o;
            return this.key.equals(mapping.getKey()) && this.value.equals(mapping.getValue());
        }
    }
    
    public static final class FloorMappings
    {
        public static final Map<Pair<FloorType, FloorMaterial>, String> mappings;
        
        public static final String getMapping(final FloorType t, final FloorMaterial m) {
            final Pair<FloorType, FloorMaterial> p = new Pair<FloorType, FloorMaterial>(t, m);
            return FloorMappings.mappings.get(p);
        }
        
        static {
            mappings = new HashMap<Pair<FloorType, FloorMaterial>, String>();
            for (final FloorType t : FloorType.values()) {
                for (final FloorMaterial m : FloorMaterial.values()) {
                    final String mapping = "img.texture.floor." + t.toString().toLowerCase() + "." + m.toString().toLowerCase();
                    final Pair<FloorType, FloorMaterial> p = new Pair<FloorType, FloorMaterial>(t, m);
                    FloorMappings.mappings.put(p, mapping);
                }
            }
        }
    }
}
