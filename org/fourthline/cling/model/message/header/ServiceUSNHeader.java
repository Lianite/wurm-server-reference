// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.NamedServiceType;

public class ServiceUSNHeader extends UpnpHeader<NamedServiceType>
{
    public ServiceUSNHeader() {
    }
    
    public ServiceUSNHeader(final UDN udn, final ServiceType serviceType) {
        this.setValue(new NamedServiceType(udn, serviceType));
    }
    
    public ServiceUSNHeader(final NamedServiceType value) {
        this.setValue(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(NamedServiceType.valueOf(s));
        }
        catch (Exception ex) {
            throw new InvalidHeaderException("Invalid service USN header value, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
