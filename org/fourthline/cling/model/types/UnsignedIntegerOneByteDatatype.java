// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class UnsignedIntegerOneByteDatatype extends AbstractDatatype<UnsignedIntegerOneByte>
{
    @Override
    public UnsignedIntegerOneByte valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return new UnsignedIntegerOneByte(s);
        }
        catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number or not in range: " + s, ex);
        }
    }
}
