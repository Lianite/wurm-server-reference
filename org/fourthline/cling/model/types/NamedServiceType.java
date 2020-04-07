// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class NamedServiceType
{
    private UDN udn;
    private ServiceType serviceType;
    
    public NamedServiceType(final UDN udn, final ServiceType serviceType) {
        this.udn = udn;
        this.serviceType = serviceType;
    }
    
    public UDN getUdn() {
        return this.udn;
    }
    
    public ServiceType getServiceType() {
        return this.serviceType;
    }
    
    public static NamedServiceType valueOf(final String s) throws InvalidValueException {
        final String[] strings = s.split("::");
        if (strings.length != 2) {
            throw new InvalidValueException("Can't parse UDN::ServiceType from: " + s);
        }
        UDN udn;
        try {
            udn = UDN.valueOf(strings[0]);
        }
        catch (Exception ex) {
            throw new InvalidValueException("Can't parse UDN: " + strings[0]);
        }
        final ServiceType serviceType = ServiceType.valueOf(strings[1]);
        return new NamedServiceType(udn, serviceType);
    }
    
    @Override
    public String toString() {
        return this.getUdn().toString() + "::" + this.getServiceType().toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof NamedServiceType)) {
            return false;
        }
        final NamedServiceType that = (NamedServiceType)o;
        return this.serviceType.equals(that.serviceType) && this.udn.equals(that.udn);
    }
    
    @Override
    public int hashCode() {
        int result = this.udn.hashCode();
        result = 31 * result + this.serviceType.hashCode();
        return result;
    }
}
