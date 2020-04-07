// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import java.net.URI;

public class UDAServiceTypeHeader extends ServiceTypeHeader
{
    public UDAServiceTypeHeader() {
    }
    
    public UDAServiceTypeHeader(final URI uri) {
        super(uri);
    }
    
    public UDAServiceTypeHeader(final UDAServiceType value) {
        super(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            ((UpnpHeader<UDAServiceType>)this).setValue(UDAServiceType.valueOf(s));
        }
        catch (Exception ex) {
            throw new InvalidHeaderException("Invalid UDA service type header value, " + ex.getMessage());
        }
    }
}
