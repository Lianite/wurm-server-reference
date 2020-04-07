// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class FloatDatatype extends AbstractDatatype<Float>
{
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Float.TYPE || Float.class.isAssignableFrom(type);
    }
    
    @Override
    public Float valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return Float.parseFloat(s.trim());
        }
        catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }
}
