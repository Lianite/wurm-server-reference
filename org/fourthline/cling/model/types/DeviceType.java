// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class DeviceType
{
    private static final Logger log;
    public static final String UNKNOWN = "UNKNOWN";
    public static final Pattern PATTERN;
    private String namespace;
    private String type;
    private int version;
    
    public DeviceType(final String namespace, final String type) {
        this(namespace, type, 1);
    }
    
    public DeviceType(final String namespace, final String type, final int version) {
        this.version = 1;
        if (namespace != null && !namespace.matches("[a-zA-Z0-9\\-\\.]+")) {
            throw new IllegalArgumentException("Device type namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (type != null && !type.matches("[a-zA-Z_0-9\\-]{1,64}")) {
            throw new IllegalArgumentException("Device type suffix too long (64) or contains illegal characters");
        }
        this.type = type;
        this.version = version;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public String getType() {
        return this.type;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public static DeviceType valueOf(String s) throws InvalidValueException {
        DeviceType deviceType = null;
        s = s.replaceAll("\\s", "");
        try {
            deviceType = UDADeviceType.valueOf(s);
        }
        catch (Exception ex) {}
        if (deviceType != null) {
            return deviceType;
        }
        try {
            Matcher matcher = DeviceType.PATTERN.matcher(s);
            if (matcher.matches()) {
                return new DeviceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }
            matcher = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):device::([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 2) {
                DeviceType.log.warning("UPnP specification violation, no device type token, defaulting to UNKNOWN: " + s);
                return new DeviceType(matcher.group(1), "UNKNOWN", Integer.valueOf(matcher.group(2)));
            }
            matcher = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):device:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                final String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                DeviceType.log.warning("UPnP specification violation, replacing invalid device type token '" + matcher.group(2) + "' with: " + cleanToken);
                return new DeviceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }
        }
        catch (RuntimeException e) {
            throw new InvalidValueException(String.format("Can't parse device type string (namespace/type/version) '%s': %s", s, e.toString()));
        }
        throw new InvalidValueException("Can't parse device type string (namespace/type/version): " + s);
    }
    
    public boolean implementsVersion(final DeviceType that) {
        return this.namespace.equals(that.namespace) && this.type.equals(that.type) && this.version >= that.version;
    }
    
    public String getDisplayString() {
        return this.getType();
    }
    
    @Override
    public String toString() {
        return "urn:" + this.getNamespace() + ":device:" + this.getType() + ":" + this.getVersion();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DeviceType)) {
            return false;
        }
        final DeviceType that = (DeviceType)o;
        return this.version == that.version && this.namespace.equals(that.namespace) && this.type.equals(that.type);
    }
    
    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.version;
        return result;
    }
    
    static {
        log = Logger.getLogger(DeviceType.class.getName());
        PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):device:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    }
}
