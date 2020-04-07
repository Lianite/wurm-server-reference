// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

public class VolumeDBRange
{
    private Integer minValue;
    private Integer maxValue;
    
    public VolumeDBRange(final Integer minValue, final Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public Integer getMinValue() {
        return this.minValue;
    }
    
    public Integer getMaxValue() {
        return this.maxValue;
    }
}
