// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.Locale;

public class BooleanDatatype extends AbstractDatatype<Boolean>
{
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Boolean.TYPE || Boolean.class.isAssignableFrom(type);
    }
    
    @Override
    public Boolean valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        if (s.equals("1") || s.toUpperCase(Locale.ROOT).equals("YES") || s.toUpperCase(Locale.ROOT).equals("TRUE")) {
            return true;
        }
        if (s.equals("0") || s.toUpperCase(Locale.ROOT).equals("NO") || s.toUpperCase(Locale.ROOT).equals("FALSE")) {
            return false;
        }
        throw new InvalidValueException("Invalid boolean value string: " + s);
    }
    
    @Override
    public String getString(final Boolean value) throws InvalidValueException {
        if (value == null) {
            return "";
        }
        return value ? "1" : "0";
    }
}
