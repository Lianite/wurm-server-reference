// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class CustomDatatype extends AbstractDatatype<String>
{
    private String name;
    
    public CustomDatatype(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        return s;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") '" + this.getName() + "'";
    }
}
