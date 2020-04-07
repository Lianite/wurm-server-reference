// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.ServiceReference;

public class PeerManagerHeader extends DLNAHeader<ServiceReference>
{
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                final ServiceReference serviceReference = new ServiceReference(s);
                if (serviceReference.getUdn() != null && serviceReference.getServiceId() != null) {
                    this.setValue(serviceReference);
                    return;
                }
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid PeerManager header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
