// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class UDAServiceId extends ServiceId
{
    private static Logger log;
    public static final String DEFAULT_NAMESPACE = "upnp-org";
    public static final String BROKEN_DEFAULT_NAMESPACE = "schemas-upnp-org";
    public static final Pattern PATTERN;
    public static final Pattern BROKEN_PATTERN;
    
    public UDAServiceId(final String id) {
        super("upnp-org", id);
    }
    
    public static UDAServiceId valueOf(final String s) throws InvalidValueException {
        Matcher matcher = UDAServiceId.PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            return new UDAServiceId(matcher.group(1));
        }
        matcher = UDAServiceId.BROKEN_PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            return new UDAServiceId(matcher.group(1));
        }
        matcher = Pattern.compile("urn:upnp-orgerviceId:urnchemas-upnp-orgervice:([a-zA-Z_0-9\\-:\\.]{1,64})").matcher(s);
        if (matcher.matches()) {
            UDAServiceId.log.warning("UPnP specification violation, recovering from Eyecon garbage: " + s);
            return new UDAServiceId(matcher.group(1));
        }
        if ("ContentDirectory".equals(s) || "ConnectionManager".equals(s) || "RenderingControl".equals(s) || "AVTransport".equals(s)) {
            UDAServiceId.log.warning("UPnP specification violation, fixing broken Service ID: " + s);
            return new UDAServiceId(s);
        }
        throw new InvalidValueException("Can't parse UDA service ID string (upnp-org/id): " + s);
    }
    
    static {
        UDAServiceId.log = Logger.getLogger(UDAServiceId.class.getName());
        PATTERN = Pattern.compile("urn:upnp-org:serviceId:([a-zA-Z_0-9\\-:\\.]{1,64})");
        BROKEN_PATTERN = Pattern.compile("urn:schemas-upnp-org:service:([a-zA-Z_0-9\\-:\\.]{1,64})");
    }
}
