// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDN;

public class ServiceReference
{
    public static final String DELIMITER = "/";
    private final UDN udn;
    private final ServiceId serviceId;
    
    public ServiceReference(final String s) {
        final String[] split = s.split("/");
        if (split.length == 2) {
            this.udn = UDN.valueOf(split[0]);
            this.serviceId = ServiceId.valueOf(split[1]);
        }
        else {
            this.udn = null;
            this.serviceId = null;
        }
    }
    
    public ServiceReference(final UDN udn, final ServiceId serviceId) {
        this.udn = udn;
        this.serviceId = serviceId;
    }
    
    public UDN getUdn() {
        return this.udn;
    }
    
    public ServiceId getServiceId() {
        return this.serviceId;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ServiceReference that = (ServiceReference)o;
        return this.serviceId.equals(that.serviceId) && this.udn.equals(that.udn);
    }
    
    @Override
    public int hashCode() {
        int result = this.udn.hashCode();
        result = 31 * result + this.serviceId.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return (this.udn == null || this.serviceId == null) ? "" : (this.udn.toString() + "/" + this.serviceId.toString());
    }
}
