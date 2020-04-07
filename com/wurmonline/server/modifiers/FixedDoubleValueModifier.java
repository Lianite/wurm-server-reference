// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.modifiers;

public final class FixedDoubleValueModifier extends DoubleValueModifier
{
    public FixedDoubleValueModifier(final double aValue) {
        super(aValue);
    }
    
    public FixedDoubleValueModifier(final int aType, final double aValue) {
        super(aType, aValue);
    }
    
    @Override
    public void setModifier(final double aNewValue) {
        assert false : "Do not call FixedDoubleValueModifier.setModifier()";
        throw new IllegalArgumentException("Do not call FixedDoubleValueModifier.setModifier(). The modifier cannot be changed.");
    }
}
