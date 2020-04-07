// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

public class MutableAllowedValueRange
{
    public Long minimum;
    public Long maximum;
    public Long step;
    
    public MutableAllowedValueRange() {
        this.minimum = 0L;
        this.maximum = Long.MAX_VALUE;
        this.step = 1L;
    }
}
