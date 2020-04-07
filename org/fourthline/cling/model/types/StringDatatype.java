// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class StringDatatype extends AbstractDatatype<String>
{
    @Override
    public String valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        return s;
    }
}
