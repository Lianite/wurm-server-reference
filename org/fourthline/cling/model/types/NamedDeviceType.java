// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class NamedDeviceType
{
    private UDN udn;
    private DeviceType deviceType;
    
    public NamedDeviceType(final UDN udn, final DeviceType deviceType) {
        this.udn = udn;
        this.deviceType = deviceType;
    }
    
    public UDN getUdn() {
        return this.udn;
    }
    
    public DeviceType getDeviceType() {
        return this.deviceType;
    }
    
    public static NamedDeviceType valueOf(final String s) throws InvalidValueException {
        final String[] strings = s.split("::");
        if (strings.length != 2) {
            throw new InvalidValueException("Can't parse UDN::DeviceType from: " + s);
        }
        UDN udn;
        try {
            udn = UDN.valueOf(strings[0]);
        }
        catch (Exception ex) {
            throw new InvalidValueException("Can't parse UDN: " + strings[0]);
        }
        final DeviceType deviceType = DeviceType.valueOf(strings[1]);
        return new NamedDeviceType(udn, deviceType);
    }
    
    @Override
    public String toString() {
        return this.getUdn().toString() + "::" + this.getDeviceType().toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof NamedDeviceType)) {
            return false;
        }
        final NamedDeviceType that = (NamedDeviceType)o;
        return this.deviceType.equals(that.deviceType) && this.udn.equals(that.udn);
    }
    
    @Override
    public int hashCode() {
        int result = this.udn.hashCode();
        result = 31 * result + this.deviceType.hashCode();
        return result;
    }
}
