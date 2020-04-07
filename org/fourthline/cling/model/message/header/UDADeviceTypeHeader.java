// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.DeviceType;
import java.net.URI;

public class UDADeviceTypeHeader extends DeviceTypeHeader
{
    public UDADeviceTypeHeader() {
    }
    
    public UDADeviceTypeHeader(final URI uri) {
        super(uri);
    }
    
    public UDADeviceTypeHeader(final DeviceType value) {
        super(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            ((UpnpHeader<UDADeviceType>)this).setValue(UDADeviceType.valueOf(s));
        }
        catch (Exception ex) {
            throw new InvalidHeaderException("Invalid UDA device type header value, " + ex.getMessage());
        }
    }
}
