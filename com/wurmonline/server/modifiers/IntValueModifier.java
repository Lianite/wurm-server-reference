// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.modifiers;

public final class IntValueModifier extends ValueModifier
{
    private int modifier;
    
    public IntValueModifier(final int value) {
        this.modifier = 0;
        this.modifier = value;
    }
    
    public IntValueModifier(final int aType, final int value) {
        super(aType);
        this.modifier = 0;
        this.modifier = value;
    }
    
    public int getModifier() {
        return this.modifier;
    }
    
    public void setModifier(final int newValue) {
        this.modifier = newValue;
    }
}
