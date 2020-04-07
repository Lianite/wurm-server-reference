// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;

public class BuildMaterial
{
    private final int templateId;
    private final int weightGrams;
    private final int totalQuantityRequired;
    private int neededQuantity;
    
    public BuildMaterial(final int tid, final int quantity) throws NoSuchTemplateException {
        final int qty = (quantity < 0) ? 0 : quantity;
        this.templateId = tid;
        this.weightGrams = ItemTemplateFactory.getInstance().getTemplate(tid).getWeightGrams();
        this.totalQuantityRequired = qty;
        this.neededQuantity = qty;
    }
    
    public int getTemplateId() {
        return this.templateId;
    }
    
    int getTotalQuantityRequired() {
        return this.totalQuantityRequired;
    }
    
    int getWeightGrams() {
        return this.weightGrams;
    }
    
    @Override
    public String toString() {
        String toReturn = "";
        try {
            toReturn = "" + this.weightGrams / ItemTemplateFactory.getInstance().getTemplate(this.templateId).getWeightGrams() + " " + ItemTemplateFactory.getInstance().getTemplate(this.templateId).getName();
        }
        catch (NoSuchTemplateException ex) {}
        return toReturn;
    }
    
    public void setNeededQuantity(final int qty) {
        this.neededQuantity = ((qty < 0) ? 0 : qty);
    }
    
    public int getNeededQuantity() {
        return this.neededQuantity;
    }
}
