// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

import com.wurmonline.shared.util.StringUtilities;

public final class BushData
{
    public static boolean isBush(final int treeId) {
        return true;
    }
    
    public static boolean isTree(final int treeId) {
        return false;
    }
    
    public static String getHelpSubject(final byte type, final boolean infected) {
        return "Terrain:" + getTypeName(type).replace(' ', '_');
    }
    
    public static boolean isValidBush(final int treeId) {
        return treeId < BushType.getLength();
    }
    
    public static int getType(final byte data) {
        return data & 0xF;
    }
    
    public static String getTypeName(final byte data) {
        return BushType.fromTileData(getType(data)).getName();
    }
    
    public enum BushType
    {
        LAVENDER(0, (byte)46, 4, 142, 148, 154, 1.0f, 1.0f, 0.0f, "model.bush.lavendel", 0, 0, true), 
        ROSE(1, (byte)47, 5, 143, 149, 155, 2.0f, 1.0f, 0.0f, "model.bush.rose", 1, 0, true), 
        THORN(2, (byte)48, 15, 144, 150, 156, 0.5f, 0.5f, 0.0f, "model.bush.thorn", 2, 0, false), 
        GRAPE(3, (byte)49, 5, 145, 151, 157, 1.4f, 1.2f, 0.0f, "model.bush.grape", 3, 0, true), 
        CAMELLIA(4, (byte)50, 3, 146, 152, 158, 1.6f, 1.25f, 0.0f, "model.bush.camellia", 0, 1, true), 
        OLEANDER(5, (byte)51, 2, 147, 153, 159, 1.55f, 1.45f, 0.0f, "model.bush.oleander", 1, 1, true), 
        HAZELNUT(6, (byte)71, 2, 160, 161, 162, 1.7f, 1.32f, 0.0f, "model.bush.hazelnut", 2, 1, true), 
        RASPBERRY(7, (byte)90, 2, 166, 167, 168, 1.7f, 1.32f, 0.0f, "model.bush.raspberry", 3, 1, true), 
        BLUEBERRY(8, (byte)91, 2, 169, 170, 171, 1.7f, 1.32f, 0.0f, "model.bush.blueberry", 0, 2, true), 
        LINGONBERRY(9, (byte)92, 2, 172, 172, 172, 1.7f, 1.32f, 0.0f, "model.bush.lingonberry", 1, 2, true);
        
        private final int typeId;
        private final byte materialId;
        private final int woodDifficulty;
        private final byte normalBush;
        private final byte myceliumBush;
        private final byte enchantedBush;
        private final float width;
        private final float height;
        private final float radius;
        private final String modelName;
        private final int posX;
        private final int posY;
        private final boolean canBearFruit;
        private static final BushType[] types;
        
        private BushType(final int id, final byte material, final int woodDifficulty, final int normalBush, final int myceliumBush, final int enchantedBush, final float width, final float height, final float radius, final String modelName, final int posX, final int posY, final boolean canBearFruit) {
            this.typeId = id;
            this.materialId = material;
            this.woodDifficulty = woodDifficulty;
            this.normalBush = (byte)normalBush;
            this.myceliumBush = (byte)myceliumBush;
            this.enchantedBush = (byte)enchantedBush;
            this.width = width;
            this.height = height;
            this.radius = radius;
            this.modelName = modelName;
            this.posX = posX;
            this.posY = posY;
            this.canBearFruit = canBearFruit;
        }
        
        public int getTypeId() {
            return this.typeId;
        }
        
        public String getName() {
            final String name = fromInt(this.typeId).toString() + " bush";
            return StringUtilities.raiseFirstLetter(name);
        }
        
        public byte getMaterial() {
            return this.materialId;
        }
        
        public byte asNormalBush() {
            return this.normalBush;
        }
        
        public byte asMyceliumBush() {
            return this.myceliumBush;
        }
        
        public byte asEnchantedBush() {
            return this.enchantedBush;
        }
        
        public int getDifficulty() {
            return this.woodDifficulty;
        }
        
        public float getWidth() {
            return this.width;
        }
        
        public float getHeight() {
            return this.height;
        }
        
        public float getRadius() {
            return this.radius;
        }
        
        String getModelName() {
            return this.modelName;
        }
        
        public String getModelResourceName(final int treeAge) {
            if (treeAge < 4) {
                return this.getModelName() + ".young";
            }
            if (treeAge == 15) {
                return this.getModelName() + ".shrivelled";
            }
            return this.getModelName();
        }
        
        public int getTexturPosX() {
            return this.posX;
        }
        
        public int getTexturPosY() {
            return this.posY;
        }
        
        public boolean canBearFruit() {
            return this.canBearFruit;
        }
        
        public static final int getLength() {
            return BushType.types.length;
        }
        
        public static BushType fromTileData(final int tileData) {
            return fromInt(tileData & 0xF);
        }
        
        public static BushType fromInt(final int i) {
            if (i >= getLength()) {
                return BushType.types[0];
            }
            return BushType.types[i & 0xFF];
        }
        
        public static BushType decodeTileData(final int tileData) {
            return fromInt(tileData & 0xF);
        }
        
        public static int encodeTileData(final int tage, int ttype) {
            ttype = Math.min(ttype, BushType.types.length - 1);
            ttype = Math.max(ttype, 0);
            return (tage & 0xF) << 4 | (ttype & 0xF);
        }
        
        static {
            types = values();
        }
    }
}
