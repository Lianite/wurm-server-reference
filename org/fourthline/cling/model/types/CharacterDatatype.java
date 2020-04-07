// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class CharacterDatatype extends AbstractDatatype<Character>
{
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return type == Character.TYPE || Character.class.isAssignableFrom(type);
    }
    
    @Override
    public Character valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        return s.charAt(0);
    }
}
