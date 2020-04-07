// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

public enum BuildingTypesEnum
{
    HOUSE("structure.wall.house"), 
    ALLFENCES("structure.wall.fence"), 
    FLOOR("structure.floor"), 
    ROOF("structure.roof"), 
    STAIRCASE("structure.staircase");
    
    public final String modelString;
    
    private BuildingTypesEnum(final String _modelString) {
        this.modelString = _modelString;
    }
    
    public final String getModelString() {
        return "model." + this.modelString;
    }
    
    public final String getTextureString() {
        return "img.texture." + this.modelString;
    }
}
