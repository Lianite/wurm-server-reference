// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

public final class Den
{
    private int templateId;
    private final int tilex;
    private final int tiley;
    private final boolean surfaced;
    
    Den(final int creatureTemplateId, final int tileX, final int tileY, final boolean _surfaced) {
        this.templateId = creatureTemplateId;
        this.tilex = tileX;
        this.tiley = tileY;
        this.surfaced = _surfaced;
    }
    
    public int getTemplateId() {
        return this.templateId;
    }
    
    void setTemplateId(final int aTemplateId) {
        this.templateId = aTemplateId;
    }
    
    public int getTilex() {
        return this.tilex;
    }
    
    public int getTiley() {
        return this.tiley;
    }
    
    public boolean isSurfaced() {
        return this.surfaced;
    }
    
    @Override
    public String toString() {
        return "Den [CreatureTemplate: " + this.templateId + ", Tile: " + this.tilex + ", " + this.tiley + ", surfaced: " + this.surfaced + ']';
    }
}
