// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDN;

public class UDNHeader extends UpnpHeader<UDN>
{
    public UDNHeader() {
    }
    
    public UDNHeader(final UDN udn) {
        this.setValue(udn);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (!s.startsWith("uuid:")) {
            throw new InvalidHeaderException("Invalid UDA header value, must start with 'uuid:': " + s);
        }
        if (s.contains("::urn")) {
            throw new InvalidHeaderException("Invalid UDA header value, must not contain '::urn': " + s);
        }
        final UDN udn = new UDN(s.substring("uuid:".length()));
        this.setValue(udn);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
