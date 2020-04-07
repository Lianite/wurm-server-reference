// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.util.Locale;

public class RootDeviceHeader extends UpnpHeader<String>
{
    public RootDeviceHeader() {
        this.setValue("upnp:rootdevice");
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (!s.toLowerCase(Locale.ROOT).equals(((UpnpHeader<Object>)this).getValue())) {
            throw new InvalidHeaderException("Invalid root device NT header value: " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue();
    }
}
