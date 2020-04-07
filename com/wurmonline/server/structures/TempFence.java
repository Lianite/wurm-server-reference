// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.Items;
import java.io.IOException;
import com.wurmonline.shared.constants.StructureStateEnum;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.items.Item;

public final class TempFence extends Fence
{
    private Item fenceItem;
    
    public TempFence(final StructureConstantsEnum aType, final int aTileX, final int aTileY, final int aHeightOffset, final Item item, final Tiles.TileBorderDirection aDir, final int aZoneId, final int aLayer) {
        super(aType, aTileX, aTileY, aHeightOffset, item.getQualityLevel(), aDir, aZoneId, aLayer);
        this.fenceItem = item;
        this.state = StructureStateEnum.FINISHED;
    }
    
    @Override
    public void setZoneId(final int zid) {
        this.zoneId = zid;
    }
    
    @Override
    public void save() throws IOException {
    }
    
    @Override
    void load() throws IOException {
    }
    
    @Override
    public float getQualityLevel() {
        return this.fenceItem.getQualityLevel();
    }
    
    @Override
    public float getOriginalQualityLevel() {
        return this.fenceItem.getOriginalQualityLevel();
    }
    
    @Override
    public float getDamage() {
        return this.fenceItem.getDamage();
    }
    
    @Override
    public boolean setDamage(final float newDam) {
        return this.fenceItem.setDamage(this.fenceItem.getDamage() + newDam);
    }
    
    @Override
    public boolean isTemporary() {
        return true;
    }
    
    @Override
    public boolean setQualityLevel(final float newQl) {
        return this.fenceItem.setQualityLevel(newQl);
    }
    
    @Override
    public void improveOrigQualityLevel(final float newQl) {
        this.fenceItem.setOriginalQualityLevel(newQl);
    }
    
    @Override
    public void delete() {
        Items.destroyItem(this.fenceItem.getWurmId());
    }
    
    @Override
    public void setLastUsed(final long aLastUsed) {
        this.fenceItem.setLastMaintained(aLastUsed);
    }
    
    @Override
    public final long getTempId() {
        return this.fenceItem.getWurmId();
    }
    
    @Override
    public void savePermissions() {
    }
    
    @Override
    boolean changeColor(final int aNewcolor) {
        return false;
    }
}
