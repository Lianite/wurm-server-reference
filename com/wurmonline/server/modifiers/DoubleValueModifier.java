// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.modifiers;

import java.util.Iterator;

public class DoubleValueModifier extends ValueModifier
{
    private double modifier;
    
    public DoubleValueModifier(final double value) {
        this.modifier = 0.0;
        this.modifier = value;
    }
    
    public DoubleValueModifier(final int aType, final double value) {
        super(aType);
        this.modifier = 0.0;
        this.modifier = value;
    }
    
    public double getModifier() {
        return this.modifier;
    }
    
    public void setModifier(final double newValue) {
        this.modifier = newValue;
        if (this.getListeners() != null) {
            for (final ValueModifiedListener list : this.getListeners()) {
                list.valueChanged(this.modifier, newValue);
            }
        }
    }
}
