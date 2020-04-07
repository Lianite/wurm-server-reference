// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class DoubleDatatype extends AbstractDatatype<Double>
{
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Double.TYPE || Double.class.isAssignableFrom(type);
    }
    
    @Override
    public Double valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }
}
