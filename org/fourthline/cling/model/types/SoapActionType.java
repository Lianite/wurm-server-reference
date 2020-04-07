// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import org.fourthline.cling.model.ModelUtil;
import java.util.regex.Pattern;

public class SoapActionType
{
    public static final String MAGIC_CONTROL_NS = "schemas-upnp-org";
    public static final String MAGIC_CONTROL_TYPE = "control-1-0";
    public static final Pattern PATTERN_MAGIC_CONTROL;
    public static final Pattern PATTERN;
    private String namespace;
    private String type;
    private String actionName;
    private Integer version;
    
    public SoapActionType(final ServiceType serviceType, final String actionName) {
        this(serviceType.getNamespace(), serviceType.getType(), serviceType.getVersion(), actionName);
    }
    
    public SoapActionType(final String namespace, final String type, final Integer version, final String actionName) {
        this.namespace = namespace;
        this.type = type;
        this.version = version;
        this.actionName = actionName;
        if (actionName != null && !ModelUtil.isValidUDAName(actionName)) {
            throw new IllegalArgumentException("Action name contains illegal characters: " + actionName);
        }
    }
    
    public String getActionName() {
        return this.actionName;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Integer getVersion() {
        return this.version;
    }
    
    public static SoapActionType valueOf(final String s) throws InvalidValueException {
        final Matcher magicControlMatcher = SoapActionType.PATTERN_MAGIC_CONTROL.matcher(s);
        try {
            if (magicControlMatcher.matches()) {
                return new SoapActionType("schemas-upnp-org", "control-1-0", null, magicControlMatcher.group(1));
            }
            final Matcher matcher = SoapActionType.PATTERN.matcher(s);
            if (matcher.matches()) {
                return new SoapActionType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)), matcher.group(4));
            }
        }
        catch (RuntimeException e) {
            throw new InvalidValueException(String.format("Can't parse action type string (namespace/type/version#actionName) '%s': %s", s, e.toString()));
        }
        throw new InvalidValueException("Can't parse action type string (namespace/type/version#actionName): " + s);
    }
    
    public ServiceType getServiceType() {
        if (this.version == null) {
            return null;
        }
        return new ServiceType(this.namespace, this.type, this.version);
    }
    
    @Override
    public String toString() {
        return this.getTypeString() + "#" + this.getActionName();
    }
    
    public String getTypeString() {
        if (this.version == null) {
            return "urn:" + this.getNamespace() + ":" + this.getType();
        }
        return "urn:" + this.getNamespace() + ":service:" + this.getType() + ":" + this.getVersion();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof SoapActionType)) {
            return false;
        }
        final SoapActionType that = (SoapActionType)o;
        if (!this.actionName.equals(that.actionName)) {
            return false;
        }
        if (!this.namespace.equals(that.namespace)) {
            return false;
        }
        if (!this.type.equals(that.type)) {
            return false;
        }
        if (this.version != null) {
            if (this.version.equals(that.version)) {
                return true;
            }
        }
        else if (that.version == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.actionName.hashCode();
        result = 31 * result + ((this.version != null) ? this.version.hashCode() : 0);
        return result;
    }
    
    static {
        PATTERN_MAGIC_CONTROL = Pattern.compile("urn:schemas-upnp-org:control-1-0#([a-zA-Z0-9^-_\\p{L}\\p{N}]{1}[a-zA-Z0-9^-_\\.\\\\p{L}\\\\p{N}\\p{Mc}\\p{Sk}]*)");
        PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+)#([a-zA-Z0-9^-_\\p{L}\\p{N}]{1}[a-zA-Z0-9^-_\\.\\\\p{L}\\\\p{N}\\p{Mc}\\p{Sk}]*)");
    }
}
