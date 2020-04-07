// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.ArrayList;

public class ContainerRestriction
{
    private final boolean onlyOneOf;
    private ArrayList<Integer> itemTemplateIds;
    private String emptySlotName;
    
    public ContainerRestriction(final boolean onlyOneOf, final int... itemTemplateId) {
        this.emptySlotName = null;
        this.onlyOneOf = onlyOneOf;
        this.itemTemplateIds = new ArrayList<Integer>();
        for (final int i : itemTemplateId) {
            this.itemTemplateIds.add(i);
        }
    }
    
    public ContainerRestriction(final boolean onlyOneOf, final String emptySlotName, final int... itemTemplateId) {
        this(onlyOneOf, itemTemplateId);
        this.setEmptySlotName(emptySlotName);
    }
    
    public boolean canInsertItem(final Item[] existing, final Item toInsert) {
        if (!this.itemTemplateIds.contains(toInsert.getTemplateId())) {
            return false;
        }
        if (this.onlyOneOf) {
            for (final Item i : existing) {
                if (this.itemTemplateIds.contains(i.getTemplateId())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void setEmptySlotName(final String name) {
        this.emptySlotName = name;
    }
    
    public String getEmptySlotName() {
        if (this.emptySlotName != null) {
            return this.emptySlotName;
        }
        return "empty " + ItemTemplateFactory.getInstance().getTemplateName(this.getEmptySlotTemplateId()) + " slot";
    }
    
    public int getEmptySlotTemplateId() {
        return this.itemTemplateIds.get(0);
    }
    
    public boolean contains(final int id) {
        return this.itemTemplateIds.contains(id);
    }
    
    public boolean doesItemOverrideSlot(final Item toInsert) {
        return this.itemTemplateIds.contains(toInsert.getTemplateId());
    }
}
