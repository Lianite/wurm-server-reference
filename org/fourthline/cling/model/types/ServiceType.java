// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ServiceType
{
    private static final Logger log;
    public static final Pattern PATTERN;
    public static final Pattern BROKEN_PATTERN;
    private String namespace;
    private String type;
    private int version;
    
    public ServiceType(final String namespace, final String type) {
        this(namespace, type, 1);
    }
    
    public ServiceType(final String namespace, final String type, final int version) {
        this.version = 1;
        if (namespace != null && !namespace.matches("[a-zA-Z0-9\\-\\.]+")) {
            throw new IllegalArgumentException("Service type namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (type != null && !type.matches("[a-zA-Z_0-9\\-]{1,64}")) {
            throw new IllegalArgumentException("Service type suffix too long (64) or contains illegal characters");
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
    
    public static ServiceType valueOf(String s) throws InvalidValueException {
        if (s == null) {
            throw new InvalidValueException("Can't parse null string");
        }
        ServiceType serviceType = null;
        s = s.replaceAll("\\s", "");
        try {
            serviceType = UDAServiceType.valueOf(s);
        }
        catch (Exception ex) {}
        if (serviceType != null) {
            return serviceType;
        }
        try {
            Matcher matcher = ServiceType.PATTERN.matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }
            matcher = ServiceType.BROKEN_PATTERN.matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }
            matcher = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                final String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                ServiceType.log.warning("UPnP specification violation, replacing invalid service type token '" + matcher.group(2) + "' with: " + cleanToken);
                return new ServiceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }
            matcher = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                final String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                ServiceType.log.warning("UPnP specification violation, replacing invalid service type token '" + matcher.group(2) + "' with: " + cleanToken);
                return new ServiceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }
        }
        catch (RuntimeException e) {
            throw new InvalidValueException(String.format("Can't parse service type string (namespace/type/version) '%s': %s", s, e.toString()));
        }
        throw new InvalidValueException("Can't parse service type string (namespace/type/version): " + s);
    }
    
    public boolean implementsVersion(final ServiceType that) {
        return that != null && this.namespace.equals(that.namespace) && this.type.equals(that.type) && this.version >= that.version;
    }
    
    public String toFriendlyString() {
        return this.getNamespace() + ":" + this.getType() + ":" + this.getVersion();
    }
    
    @Override
    public String toString() {
        return "urn:" + this.getNamespace() + ":service:" + this.getType() + ":" + this.getVersion();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceType)) {
            return false;
        }
        final ServiceType that = (ServiceType)o;
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
        log = Logger.getLogger(ServiceType.class.getName());
        PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
        BROKEN_PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    }
}
