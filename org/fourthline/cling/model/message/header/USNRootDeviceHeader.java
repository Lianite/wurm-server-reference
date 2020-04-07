// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDN;

public class USNRootDeviceHeader extends UpnpHeader<UDN>
{
    public static final String ROOT_DEVICE_SUFFIX = "::upnp:rootdevice";
    
    public USNRootDeviceHeader() {
    }
    
    public USNRootDeviceHeader(final UDN udn) {
        this.setValue(udn);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (!s.startsWith("uuid:") || !s.endsWith("::upnp:rootdevice")) {
            throw new InvalidHeaderException("Invalid root device USN header value, must start with 'uuid:' and end with '::upnp:rootdevice' but is '" + s + "'");
        }
        final UDN udn = new UDN(s.substring("uuid:".length(), s.length() - "::upnp:rootdevice".length()));
        this.setValue(udn);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString() + "::upnp:rootdevice";
    }
}
