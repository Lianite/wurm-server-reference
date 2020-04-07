// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.net.URI;
import org.fourthline.cling.model.types.DeviceType;

public class DeviceTypeHeader extends UpnpHeader<DeviceType>
{
    public DeviceTypeHeader() {
    }
    
    public DeviceTypeHeader(final URI uri) {
        this.setString(uri.toString());
    }
    
    public DeviceTypeHeader(final DeviceType value) {
        this.setValue(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(DeviceType.valueOf(s));
        }
        catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid device type header value, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
