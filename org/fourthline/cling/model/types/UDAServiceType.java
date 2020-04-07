// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDAServiceType extends ServiceType
{
    public static final String DEFAULT_NAMESPACE = "schemas-upnp-org";
    public static final Pattern PATTERN;
    
    public UDAServiceType(final String type) {
        this(type, 1);
    }
    
    public UDAServiceType(final String type, final int version) {
        super("schemas-upnp-org", type, version);
    }
    
    public static UDAServiceType valueOf(final String s) throws InvalidValueException {
        final Matcher matcher = UDAServiceType.PATTERN.matcher(s);
        try {
            if (matcher.matches()) {
                return new UDAServiceType(matcher.group(1), Integer.valueOf(matcher.group(2)));
            }
        }
        catch (RuntimeException e) {
            throw new InvalidValueException(String.format("Can't parse UDA service type string (namespace/type/version) '%s': %s", s, e.toString()));
        }
        throw new InvalidValueException("Can't parse UDA service type string (namespace/type/version): " + s);
    }
    
    static {
        PATTERN = Pattern.compile("urn:schemas-upnp-org:service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    }
}
