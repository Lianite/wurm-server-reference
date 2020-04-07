// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class ShortDatatype extends AbstractDatatype<Short>
{
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Short.TYPE || Short.class.isAssignableFrom(type);
    }
    
    @Override
    public Short valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            final Short value = Short.parseShort(s.trim());
            if (!this.isValid(value)) {
                throw new InvalidValueException("Not a valid short: " + s);
            }
            return value;
        }
        catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }
}
