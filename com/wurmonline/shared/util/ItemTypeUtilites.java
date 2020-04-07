// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

public final class ItemTypeUtilites
{
    public static final int ITEMTYPE_ITEM = 0;
    public static final int ITEMTYPE_WOUND = 1;
    public static final int ITEMTYPE_BODYPART = 2;
    public static final int ITEMTYPE_CONTAINER = 3;
    public static final int ITEMTYPE_NODROP = 4;
    public static final int ITEMTYPE_IS_TWO_HANDER = 5;
    public static final int ITEMTYPE_INVENTORY_GROUP = 6;
    public static final int ITEMTYPE_SHOWSLOPES = 7;
    public static final int ITEMTYPE_TOOLBELT_IGNORE_CONTENTS = 8;
    
    public static short calcProfile(final boolean wound, final boolean bodypart, final boolean container, final boolean nodrop, final boolean twoHanded, final boolean inventoryGroup, final boolean showSlopes, final boolean toolbeltIgnoreContents) {
        short toReturn = 0;
        if (wound) {
            toReturn += 2;
        }
        if (bodypart) {
            toReturn += 4;
        }
        if (container) {
            toReturn += 8;
        }
        if (nodrop) {
            toReturn += 16;
        }
        if (twoHanded) {
            toReturn += 32;
        }
        if (inventoryGroup) {
            toReturn += 64;
        }
        if (showSlopes) {
            toReturn += 128;
        }
        if (toolbeltIgnoreContents) {
            toReturn += 256;
        }
        return toReturn;
    }
    
    public static boolean isWound(final short profile) {
        return (profile >> 1 & 0x1) == 0x1;
    }
    
    public static boolean isBodypart(final short profile) {
        return (profile >> 2 & 0x1) == 0x1;
    }
    
    public static boolean isContainer(final short profile) {
        return (profile >> 3 & 0x1) == 0x1;
    }
    
    public static boolean isNodrop(final short profile) {
        return (profile >> 4 & 0x1) == 0x1;
    }
    
    public static boolean isTwoHanded(final short profile) {
        return (profile >> 5 & 0x1) == 0x1;
    }
    
    public static boolean isInventoryGroup(final short profile) {
        return (profile >> 6 & 0x1) == 0x1;
    }
    
    public static boolean doesShowSlopes(final short profile) {
        return (profile >> 7 & 0x1) == 0x1;
    }
    
    public static boolean toolbeltIgnoreContents(final short profile) {
        return (profile >> 8 & 0x1) == 0x1;
    }
}
