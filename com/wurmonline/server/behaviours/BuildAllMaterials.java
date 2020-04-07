// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class BuildAllMaterials
{
    private final List<BuildStageMaterials> bsms;
    
    public BuildAllMaterials() {
        this.bsms = new ArrayList<BuildStageMaterials>();
    }
    
    public void add(final BuildStageMaterials bms) {
        this.bsms.add(bms);
    }
    
    public List<BuildStageMaterials> getBuildStageMaterials() {
        return this.bsms;
    }
    
    public BuildStageMaterials getBuildStageMaterials(final byte stage) {
        return this.bsms.get(Math.max(0, stage));
    }
    
    public int getStageCount() {
        return this.bsms.size();
    }
    
    public List<BuildMaterial> getCurrentRequiredMaterials() {
        final Iterator<BuildStageMaterials> iterator = this.bsms.iterator();
        if (iterator.hasNext()) {
            final BuildStageMaterials bsm = iterator.next();
            return bsm.getRequiredMaterials();
        }
        return new ArrayList<BuildMaterial>();
    }
    
    public String getStageCountAsString() {
        switch (this.bsms.size()) {
            case 1: {
                return "one";
            }
            case 2: {
                return "two";
            }
            case 3: {
                return "three";
            }
            case 4: {
                return "four";
            }
            case 5: {
                return "five";
            }
            case 6: {
                return "six";
            }
            case 7: {
                return "seven";
            }
            default: {
                return "" + this.bsms.size();
            }
        }
    }
    
    public void setNeeded(final byte currentStage, final int done) {
        for (int stage = 0; stage < this.bsms.size(); ++stage) {
            if (currentStage > stage) {
                this.getBuildStageMaterials((byte)stage).setNoneNeeded();
            }
            else if (currentStage == stage) {
                this.getBuildStageMaterials((byte)stage).reduceNeededBy(done);
            }
            else {
                this.getBuildStageMaterials((byte)stage).setMaxNeeded();
            }
        }
    }
    
    public List<BuildMaterial> getTotalMaterialsNeeded() {
        final BuildStageMaterials all = this.getTotalMaterialsRequired();
        return all.getBuildMaterials();
    }
    
    private BuildStageMaterials getTotalMaterialsRequired() {
        final Map<Integer, Integer> mats = new HashMap<Integer, Integer>();
        for (final BuildStageMaterials bsm : this.bsms) {
            for (final BuildMaterial bm : bsm.getBuildMaterials()) {
                int qty = bm.getNeededQuantity();
                if (qty > 0) {
                    final Integer key = bm.getTemplateId();
                    if (mats.containsKey(key)) {
                        qty += mats.get(key);
                    }
                    mats.put(key, qty);
                }
            }
        }
        final BuildStageMaterials all = new BuildStageMaterials("All");
        for (final Map.Entry<Integer, Integer> entry : mats.entrySet()) {
            try {
                all.add(entry.getKey(), entry.getValue());
            }
            catch (NoSuchTemplateException ex) {}
        }
        return all;
    }
    
    public BuildAllMaterials getRemainingMaterialsNeeded() {
        final BuildAllMaterials toReturn = new BuildAllMaterials();
        for (final BuildStageMaterials bsm : this.bsms) {
            if (!bsm.isStageComplete()) {
                toReturn.add(bsm);
            }
        }
        return toReturn;
    }
    
    public String getRequiredMaterialString(final boolean detailed) {
        final BuildStageMaterials all = this.getTotalMaterialsRequired();
        return all.getRequiredMaterialString(detailed);
    }
    
    public int getTotalQuantityRequired() {
        int count = 0;
        for (final BuildStageMaterials bsm : this.bsms) {
            count += bsm.getTotalQuantityRequired();
        }
        return count;
    }
    
    public int getTotalQuantityDone() {
        int count = 0;
        for (final BuildStageMaterials bsm : this.bsms) {
            count += bsm.getTotalQuantityDone();
        }
        return count;
    }
}
