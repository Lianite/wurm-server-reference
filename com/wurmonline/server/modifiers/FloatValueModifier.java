// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.modifiers;

public final class FloatValueModifier extends ValueModifier
{
    private float modifier;
    
    public FloatValueModifier(final float value) {
        this.modifier = 0.0f;
        this.modifier = value;
    }
    
    public FloatValueModifier(final int aType, final float value) {
        super(aType);
        this.modifier = 0.0f;
        this.modifier = value;
    }
    
    public float getModifier() {
        return this.modifier;
    }
    
    public void setModifier(final float newValue) {
        this.modifier = newValue;
    }
}
