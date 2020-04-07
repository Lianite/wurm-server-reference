// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

public class EXTHeader extends UpnpHeader<String>
{
    public static final String DEFAULT_VALUE = "";
    
    public EXTHeader() {
        this.setValue("");
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s != null && s.length() > 0) {
            throw new InvalidHeaderException("Invalid EXT header, it has no value: " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue();
    }
}
