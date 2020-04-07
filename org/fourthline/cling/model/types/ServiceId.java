// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ServiceId
{
    private static final Logger log;
    public static final String UNKNOWN = "UNKNOWN";
    public static final Pattern PATTERN;
    public static final Pattern BROKEN_PATTERN;
    private String namespace;
    private String id;
    
    public ServiceId(final String namespace, final String id) {
        if (namespace != null && !namespace.matches("[a-zA-Z0-9\\-\\.]+")) {
            throw new IllegalArgumentException("Service ID namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (id != null && !id.matches("[a-zA-Z_0-9\\-:\\.]{1,64}")) {
            throw new IllegalArgumentException("Service ID suffix too long (64) or contains illegal characters");
        }
        this.id = id;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public String getId() {
        return this.id;
    }
    
    public static ServiceId valueOf(final String s) throws InvalidValueException {
        ServiceId serviceId = null;
        try {
            serviceId = UDAServiceId.valueOf(s);
        }
        catch (Exception ex) {}
        if (serviceId != null) {
            return serviceId;
        }
        Matcher matcher = ServiceId.PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 2) {
            return new ServiceId(matcher.group(1), matcher.group(2));
        }
        matcher = ServiceId.BROKEN_PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 2) {
            return new ServiceId(matcher.group(1), matcher.group(2));
        }
        matcher = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:").matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            ServiceId.log.warning("UPnP specification violation, no service ID token, defaulting to UNKNOWN: " + s);
            return new ServiceId(matcher.group(1), "UNKNOWN");
        }
        final String[] tokens = s.split("[:]");
        if (tokens.length == 4) {
            ServiceId.log.warning("UPnP specification violation, trying a simple colon-split of: " + s);
            return new ServiceId(tokens[1], tokens[3]);
        }
        throw new InvalidValueException("Can't parse service ID string (namespace/id): " + s);
    }
    
    @Override
    public String toString() {
        return "urn:" + this.getNamespace() + ":serviceId:" + this.getId();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceId)) {
            return false;
        }
        final ServiceId serviceId = (ServiceId)o;
        return this.id.equals(serviceId.id) && this.namespace.equals(serviceId.namespace);
    }
    
    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.id.hashCode();
        return result;
    }
    
    static {
        log = Logger.getLogger(ServiceId.class.getName());
        PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:([a-zA-Z_0-9\\-:\\.]{1,64})");
        BROKEN_PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-:\\.]{1,64})");
    }
}
