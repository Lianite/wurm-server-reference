// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

final class ItemDemand
{
    private final int templateId;
    private float demand;
    
    ItemDemand(final int itemTemplateId, final float dem) {
        this.templateId = itemTemplateId;
        this.demand = dem;
    }
    
    int getTemplateId() {
        return this.templateId;
    }
    
    float getDemand() {
        return this.demand;
    }
    
    void setDemand(final float aDemand) {
        this.demand = aDemand;
    }
}
