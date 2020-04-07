// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

public enum CreationWindowCategory
{
    NONE(0, ""), 
    EPIC(1, "Epic"), 
    ARMOUR(2, "Armour"), 
    CONTAINERS(3, "Containers"), 
    FOOD(4, "Food"), 
    LOCKS(5, "Locks"), 
    MAGIC(6, "Magic"), 
    POTTERY(7, "Pottery"), 
    DECORATIONS(8, "Decorations"), 
    TOOLS(9, "Tools"), 
    SHIELDS(10, "Shields"), 
    WEAPONS(11, "Weapons"), 
    MISCELLANEOUS(12, "Miscellaneous");
    
    private final byte id;
    private final String name;
    private static final CreationWindowCategory[] types;
    
    private CreationWindowCategory(final int id, final String name) {
        this.id = (byte)id;
        this.name = name;
    }
    
    public byte getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static CreationWindowCategory creationWindowCategoryFromId(final byte aId) {
        for (int i = 0; i < CreationWindowCategory.types.length; ++i) {
            if (aId == CreationWindowCategory.types[i].getId()) {
                return CreationWindowCategory.types[i];
            }
        }
        return CreationWindowCategory.NONE;
    }
    
    public static byte idFromName(final String aName) {
        for (int i = 0; i < CreationWindowCategory.types.length; ++i) {
            if (aName.equals(CreationWindowCategory.types[i].getName())) {
                return CreationWindowCategory.types[i].getId();
            }
        }
        return CreationWindowCategory.NONE.getId();
    }
    
    static {
        types = values();
    }
}
