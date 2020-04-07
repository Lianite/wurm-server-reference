// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

public class AVClientInfoHeader extends UpnpHeader<String>
{
    public AVClientInfoHeader() {
    }
    
    public AVClientInfoHeader(final String s) {
        this.setValue(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        this.setValue(s);
    }
    
    @Override
    public String getString() {
        return this.getValue();
    }
}
