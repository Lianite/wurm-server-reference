// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public final class UnsignedIntegerOneByte extends UnsignedVariableInteger
{
    public UnsignedIntegerOneByte(final long value) throws NumberFormatException {
        super(value);
    }
    
    public UnsignedIntegerOneByte(final String s) throws NumberFormatException {
        super(s);
    }
    
    @Override
    public Bits getBits() {
        return Bits.EIGHT;
    }
}
