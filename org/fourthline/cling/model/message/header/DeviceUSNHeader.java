// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.NamedDeviceType;

public class DeviceUSNHeader extends UpnpHeader<NamedDeviceType>
{
    public DeviceUSNHeader() {
    }
    
    public DeviceUSNHeader(final UDN udn, final DeviceType deviceType) {
        this.setValue(new NamedDeviceType(udn, deviceType));
    }
    
    public DeviceUSNHeader(final NamedDeviceType value) {
        this.setValue(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(NamedDeviceType.valueOf(s));
        }
        catch (Exception ex) {
            throw new InvalidHeaderException("Invalid device USN header value, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
