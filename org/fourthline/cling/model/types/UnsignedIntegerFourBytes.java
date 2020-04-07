// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public final class UnsignedIntegerFourBytes extends UnsignedVariableInteger
{
    public UnsignedIntegerFourBytes(final long value) throws NumberFormatException {
        super(value);
    }
    
    public UnsignedIntegerFourBytes(final String s) throws NumberFormatException {
        super(s);
    }
    
    @Override
    public Bits getBits() {
        return Bits.THIRTYTWO;
    }
}
