// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.net.URI;
import org.fourthline.cling.model.types.ServiceType;

public class ServiceTypeHeader extends UpnpHeader<ServiceType>
{
    public ServiceTypeHeader() {
    }
    
    public ServiceTypeHeader(final URI uri) {
        this.setString(uri.toString());
    }
    
    public ServiceTypeHeader(final ServiceType value) {
        this.setValue(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(ServiceType.valueOf(s));
        }
        catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid service type header value, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
