// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import org.seamless.util.io.Base64Coder;

public class Base64Datatype extends AbstractDatatype<byte[]>
{
    public Class<byte[]> getValueType() {
        return byte[].class;
    }
    
    @Override
    public byte[] valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return Base64Coder.decode(s);
        }
        catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public String getString(final byte[] value) throws InvalidValueException {
        if (value == null) {
            return "";
        }
        try {
            return new String(Base64Coder.encode(value), "UTF-8");
        }
        catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }
}
