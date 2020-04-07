// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.items.ItemTemplate;
import java.util.Set;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.HashSet;
import com.wurmonline.server.structures.BridgePart;
import java.util.Iterator;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.ArrayList;
import java.util.List;

public class BuildStageMaterials
{
    private final List<BuildMaterial> bms;
    private final String name;
    private int stageNo;
    
    public BuildStageMaterials(final String newName) {
        this.stageNo = -1;
        this.name = newName;
        this.bms = new ArrayList<BuildMaterial>();
    }
    
    public void add(final int templateId, final int qty) throws NoSuchTemplateException {
        this.bms.add(new BuildMaterial(templateId, qty));
    }
    
    public void setStageNumber(final int numb) {
        this.stageNo = numb;
    }
    
    public String getStageNumber() {
        if (this.stageNo >= 0) {
            return "Stage " + this.stageNo + " ";
        }
        return "";
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getStageName() {
        return this.getStageNumber() + this.name;
    }
    
    public List<BuildMaterial> getBuildMaterials() {
        return this.bms;
    }
    
    public void setNoneNeeded() {
        for (final BuildMaterial mat : this.bms) {
            mat.setNeededQuantity(0);
        }
    }
    
    public void setMaxNeeded() {
        for (final BuildMaterial mat : this.bms) {
            mat.setNeededQuantity(mat.getTotalQuantityRequired());
        }
    }
    
    public void reduceNeededBy(final int qty) {
        for (final BuildMaterial mat : this.bms) {
            int newQty = mat.getTotalQuantityRequired() - qty;
            if (newQty < 0) {
                newQty = 0;
            }
            mat.setNeededQuantity(newQty);
        }
    }
    
    public boolean isStageComplete(final BridgePart bridgePart) {
        for (final BuildMaterial mat : this.bms) {
            if (mat.getTotalQuantityRequired() > bridgePart.getMaterialCount()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isStageComplete() {
        for (final BuildMaterial bm : this.getBuildMaterials()) {
            if (bm.getNeededQuantity() > 0) {
                return false;
            }
        }
        return true;
    }
    
    public List<BuildMaterial> getRequiredMaterials() {
        final List<BuildMaterial> mats = new ArrayList<BuildMaterial>();
        for (final BuildMaterial mat : this.bms) {
            if (mat.getNeededQuantity() > 0) {
                mats.add(mat);
            }
        }
        return mats;
    }
    
    public String getRequiredMaterialString(final boolean detailed) {
        final Set<String> mats = new HashSet<String>();
        for (final BuildMaterial mat : this.bms) {
            if (mat.getNeededQuantity() > 0) {
                try {
                    String description = "";
                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(mat.getTemplateId());
                    if (template != null) {
                        if (detailed) {
                            description = description + mat.getNeededQuantity() + " ";
                        }
                        if (template.sizeString.length() > 0) {
                            description += template.sizeString;
                        }
                        description += ((mat.getNeededQuantity() > 1) ? template.getPlural() : template.getName());
                    }
                    if (description.length() == 0) {
                        description = "unknown quantities of unknown materials";
                    }
                    mats.add(description);
                }
                catch (NoSuchTemplateException ex) {}
            }
        }
        String description2 = "";
        int cnt = 0;
        for (final String s : mats) {
            if (++cnt == mats.size() && mats.size() > 1) {
                description2 += " and ";
            }
            else if (cnt > 1) {
                description2 += ", ";
            }
            description2 += s;
        }
        if (description2.length() == 0) {
            description2 = "no materials";
        }
        return description2;
    }
    
    public int getTotalQuantityRequired() {
        int count = 0;
        for (final BuildMaterial bm : this.bms) {
            count += bm.getTotalQuantityRequired();
        }
        return count;
    }
    
    public int getTotalQuantityDone() {
        int count = 0;
        for (final BuildMaterial bm : this.bms) {
            count += bm.getTotalQuantityRequired() - bm.getNeededQuantity();
        }
        return count;
    }
}
