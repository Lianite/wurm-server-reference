// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

public final class GrassData
{
    private static String getFlowerName(final FlowerType flowerType) {
        switch (flowerType) {
            case NONE: {
                return "";
            }
            case FLOWER_1: {
                return "Yellow flowers";
            }
            case FLOWER_2: {
                return "Orange-red flowers";
            }
            case FLOWER_3: {
                return "Purple flowers";
            }
            case FLOWER_4: {
                return "White flowers";
            }
            case FLOWER_5: {
                return "Blue flowers";
            }
            case FLOWER_6: {
                return "Greenish-yellow flowers";
            }
            case FLOWER_7: {
                return "White-dotted flowers";
            }
            default: {
                return "Unknown grass";
            }
        }
    }
    
    public static String getModelResourceName(final FlowerType flowerType) {
        switch (flowerType) {
            default: {
                return "model.flower.unknown";
            }
        }
    }
    
    public static String getHelpSubject(final int type) {
        return "Terrain:" + GrassType.values()[type].name().replace(' ', '_');
    }
    
    public static int getFlowerType(final byte data) {
        return FlowerType.decodeTileData(data).getType() & 0xFFFF;
    }
    
    public static String getFlowerTypeName(final byte data) {
        return getFlowerName(FlowerType.decodeTileData(data));
    }
    
    public static byte encodeGrassTileData(final GrowthStage growthStage, final GrassType grassType, final FlowerType flowerType) {
        return (byte)(growthStage.getEncodedData() | grassType.getEncodedData() | flowerType.getEncodedData());
    }
    
    public static byte encodeGrassTileData(final GrowthStage growthStage, final FlowerType flowerType) {
        return (byte)(growthStage.getEncodedData() | flowerType.getEncodedData());
    }
    
    public static String getHover(final byte data) {
        return GrassType.decodeTileData(data).getName();
    }
    
    public static int getGrowthRateFor(final GrassType grassType, final GrowthSeason season) {
        return grassType.getGrowthRateInSeason(season);
    }
    
    public enum GrowthSeason
    {
        WINTER, 
        SPRING, 
        SUMMER, 
        AUTUMN;
    }
    
    public enum GrowthStage
    {
        SHORT((byte)0), 
        MEDIUM((byte)1), 
        TALL((byte)2), 
        WILD((byte)3);
        
        private byte code;
        private static final int NUMBER_OF_STAGES;
        private static final GrowthStage[] stages;
        
        private GrowthStage(final byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
        
        public byte getEncodedData() {
            return (byte)(this.code << 6 & 0xC0);
        }
        
        public static GrowthStage fromInt(final int i) {
            return GrowthStage.stages[i];
        }
        
        public static GrowthStage decodeTileData(final int tileData) {
            return fromInt(tileData >> 6 & 0x3);
        }
        
        public static GrowthStage decodeTreeData(final int tileData) {
            final int len = Math.max((tileData & 0x3) - 1, 0);
            return fromInt(len);
        }
        
        public static short getYield(final GrowthStage growthStage) {
            short yield = 0;
            switch (growthStage) {
                case SHORT: {
                    yield = 0;
                    break;
                }
                case MEDIUM: {
                    yield = 1;
                    break;
                }
                case TALL: {
                    yield = 2;
                    break;
                }
                case WILD: {
                    yield = 3;
                    break;
                }
                default: {
                    yield = 0;
                    break;
                }
            }
            return yield;
        }
        
        public GrowthStage getNextStage() {
            int num = this.ordinal();
            num = Math.min(num + 1, GrowthStage.NUMBER_OF_STAGES - 1);
            return fromInt(num);
        }
        
        public final boolean isMax() {
            return this.ordinal() >= GrowthStage.NUMBER_OF_STAGES - 1;
        }
        
        public GrowthStage getPreviousStage() {
            int num = this.ordinal();
            num = Math.max(num - 1, 0);
            return fromInt(num);
        }
        
        static {
            NUMBER_OF_STAGES = values().length;
            stages = values();
        }
    }
    
    public enum GrowthTreeStage
    {
        LAWN((byte)0), 
        SHORT((byte)1), 
        MEDIUM((byte)2), 
        TALL((byte)3);
        
        private byte code;
        private static final int NUMBER_OF_STAGES;
        private static final GrowthTreeStage[] stages;
        
        private GrowthTreeStage(final byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
        
        public byte getEncodedData() {
            return (byte)(this.code & 0x3);
        }
        
        public static GrowthTreeStage fromInt(final int i) {
            return GrowthTreeStage.stages[i];
        }
        
        public static GrowthTreeStage decodeTileData(final int tileData) {
            return fromInt(tileData & 0x3);
        }
        
        public static short getYield(final GrowthTreeStage growthStage) {
            short yield = 0;
            switch (growthStage) {
                case SHORT: {
                    yield = 0;
                    break;
                }
                case MEDIUM: {
                    yield = 1;
                    break;
                }
                case TALL: {
                    yield = 2;
                    break;
                }
                default: {
                    yield = 0;
                    break;
                }
            }
            return yield;
        }
        
        public GrowthTreeStage getNextStage() {
            int num = this.ordinal();
            num = Math.min(num + 1, GrowthTreeStage.NUMBER_OF_STAGES - 1);
            return fromInt(num);
        }
        
        public final boolean isMax() {
            return this.ordinal() >= GrowthTreeStage.NUMBER_OF_STAGES - 1;
        }
        
        public GrowthTreeStage getPreviousStage() {
            int num = this.ordinal();
            num = Math.max(num - 1, 1);
            return fromInt(num);
        }
        
        static {
            NUMBER_OF_STAGES = values().length;
            stages = values();
        }
    }
    
    public enum GrassType
    {
        GRASS((byte)0), 
        REED((byte)1), 
        KELP((byte)2), 
        UNUSED((byte)3);
        
        private byte type;
        private static final GrassType[] types;
        
        private GrassType(final byte type) {
            this.type = type;
        }
        
        public byte getType() {
            return this.type;
        }
        
        public byte getEncodedData() {
            return (byte)(this.type << 4 & 0x30);
        }
        
        public static GrassType fromInt(final int i) {
            return GrassType.types[i];
        }
        
        public static GrassType decodeTileData(final int tile) {
            return fromInt(tile >> 4 & 0x3);
        }
        
        public String getName() {
            switch (this) {
                case GRASS: {
                    return "Grass";
                }
                case KELP: {
                    return "Kelp";
                }
                case REED: {
                    return "Reed";
                }
                default: {
                    return "Unknown";
                }
            }
        }
        
        public int getGrowthRateInSeason(final GrowthSeason season) {
            switch (season) {
                case WINTER: {
                    return 15;
                }
                case SUMMER: {
                    return 40;
                }
                case AUTUMN: {
                    return 30;
                }
                case SPRING: {
                    return 20;
                }
                default: {
                    return 5;
                }
            }
        }
        
        static {
            types = values();
        }
    }
    
    public enum FlowerType
    {
        NONE((byte)0), 
        FLOWER_1((byte)1), 
        FLOWER_2((byte)2), 
        FLOWER_3((byte)3), 
        FLOWER_4((byte)4), 
        FLOWER_5((byte)5), 
        FLOWER_6((byte)6), 
        FLOWER_7((byte)7), 
        FLOWER_8((byte)8), 
        FLOWER_9((byte)9), 
        FLOWER_10((byte)10), 
        FLOWER_11((byte)11), 
        FLOWER_12((byte)12), 
        FLOWER_13((byte)13), 
        FLOWER_14((byte)14), 
        FLOWER_15((byte)15);
        
        private byte type;
        private static final FlowerType[] types;
        
        private FlowerType(final byte type) {
            this.type = type;
        }
        
        public byte getType() {
            return this.type;
        }
        
        public byte getEncodedData() {
            return (byte)(this.type & 0xFF);
        }
        
        public static FlowerType fromInt(final int i) {
            return FlowerType.types[i];
        }
        
        public static FlowerType decodeTileData(final int tileData) {
            return fromInt(tileData & 0xF);
        }
        
        public String getDescription() {
            return getFlowerName(this);
        }
        
        static {
            types = values();
        }
    }
}
