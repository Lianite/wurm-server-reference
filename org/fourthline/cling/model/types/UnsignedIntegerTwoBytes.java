// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public final class UnsignedIntegerTwoBytes extends UnsignedVariableInteger
{
    public UnsignedIntegerTwoBytes(final long value) throws NumberFormatException {
        super(value);
    }
    
    public UnsignedIntegerTwoBytes(final String s) throws NumberFormatException {
        super(s);
    }
    
    @Override
    public Bits getBits() {
        return Bits.SIXTEEN;
    }
}
