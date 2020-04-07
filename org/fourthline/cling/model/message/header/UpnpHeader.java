// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class UpnpHeader<T>
{
    private static final Logger log;
    private T value;
    
    public void setValue(final T value) {
        this.value = value;
    }
    
    public T getValue() {
        return this.value;
    }
    
    public abstract void setString(final String p0) throws InvalidHeaderException;
    
    public abstract String getString();
    
    public static UpnpHeader newInstance(final Type type, final String headerValue) {
        UpnpHeader upnpHeader = null;
        for (int i = 0; i < type.getHeaderTypes().length && upnpHeader == null; ++i) {
            final Class<? extends UpnpHeader> headerClass = type.getHeaderTypes()[i];
            try {
                UpnpHeader.log.finest("Trying to parse '" + type + "' with class: " + headerClass.getSimpleName());
                upnpHeader = (UpnpHeader)headerClass.newInstance();
                if (headerValue != null) {
                    upnpHeader.setString(headerValue);
                }
            }
            catch (InvalidHeaderException ex) {
                UpnpHeader.log.finest("Invalid header value for tested type: " + headerClass.getSimpleName() + " - " + ex.getMessage());
                upnpHeader = null;
            }
            catch (Exception ex2) {
                UpnpHeader.log.severe("Error instantiating header of type '" + type + "' with value: " + headerValue);
                UpnpHeader.log.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(ex2));
            }
        }
        return upnpHeader;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") '" + this.getValue() + "'";
    }
    
    static {
        log = Logger.getLogger(UpnpHeader.class.getName());
    }
    
    public enum Type
    {
        USN("USN", (Class<? extends UpnpHeader>[])new Class[] { USNRootDeviceHeader.class, DeviceUSNHeader.class, ServiceUSNHeader.class, UDNHeader.class }), 
        NT("NT", (Class<? extends UpnpHeader>[])new Class[] { RootDeviceHeader.class, UDADeviceTypeHeader.class, UDAServiceTypeHeader.class, DeviceTypeHeader.class, ServiceTypeHeader.class, UDNHeader.class, NTEventHeader.class }), 
        NTS("NTS", (Class<? extends UpnpHeader>[])new Class[] { NTSHeader.class }), 
        HOST("HOST", (Class<? extends UpnpHeader>[])new Class[] { HostHeader.class }), 
        SERVER("SERVER", (Class<? extends UpnpHeader>[])new Class[] { ServerHeader.class }), 
        LOCATION("LOCATION", (Class<? extends UpnpHeader>[])new Class[] { LocationHeader.class }), 
        MAX_AGE("CACHE-CONTROL", (Class<? extends UpnpHeader>[])new Class[] { MaxAgeHeader.class }), 
        USER_AGENT("USER-AGENT", (Class<? extends UpnpHeader>[])new Class[] { UserAgentHeader.class }), 
        CONTENT_TYPE("CONTENT-TYPE", (Class<? extends UpnpHeader>[])new Class[] { ContentTypeHeader.class }), 
        MAN("MAN", (Class<? extends UpnpHeader>[])new Class[] { MANHeader.class }), 
        MX("MX", (Class<? extends UpnpHeader>[])new Class[] { MXHeader.class }), 
        ST("ST", (Class<? extends UpnpHeader>[])new Class[] { STAllHeader.class, RootDeviceHeader.class, UDADeviceTypeHeader.class, UDAServiceTypeHeader.class, DeviceTypeHeader.class, ServiceTypeHeader.class, UDNHeader.class }), 
        EXT("EXT", (Class<? extends UpnpHeader>[])new Class[] { EXTHeader.class }), 
        SOAPACTION("SOAPACTION", (Class<? extends UpnpHeader>[])new Class[] { SoapActionHeader.class }), 
        TIMEOUT("TIMEOUT", (Class<? extends UpnpHeader>[])new Class[] { TimeoutHeader.class }), 
        CALLBACK("CALLBACK", (Class<? extends UpnpHeader>[])new Class[] { CallbackHeader.class }), 
        SID("SID", (Class<? extends UpnpHeader>[])new Class[] { SubscriptionIdHeader.class }), 
        SEQ("SEQ", (Class<? extends UpnpHeader>[])new Class[] { EventSequenceHeader.class }), 
        RANGE("RANGE", (Class<? extends UpnpHeader>[])new Class[] { RangeHeader.class }), 
        CONTENT_RANGE("CONTENT-RANGE", (Class<? extends UpnpHeader>[])new Class[] { ContentRangeHeader.class }), 
        PRAGMA("PRAGMA", (Class<? extends UpnpHeader>[])new Class[] { PragmaHeader.class }), 
        EXT_IFACE_MAC("X-CLING-IFACE-MAC", (Class<? extends UpnpHeader>[])new Class[] { InterfaceMacHeader.class }), 
        EXT_AV_CLIENT_INFO("X-AV-CLIENT-INFO", (Class<? extends UpnpHeader>[])new Class[] { AVClientInfoHeader.class });
        
        private static Map<String, Type> byName;
        private String httpName;
        private Class<? extends UpnpHeader>[] headerTypes;
        
        private Type(final String httpName, final Class<? extends UpnpHeader>[] headerClass) {
            this.httpName = httpName;
            this.headerTypes = headerClass;
        }
        
        public String getHttpName() {
            return this.httpName;
        }
        
        public Class<? extends UpnpHeader>[] getHeaderTypes() {
            return this.headerTypes;
        }
        
        public boolean isValidHeaderType(final Class<? extends UpnpHeader> clazz) {
            for (final Class<? extends UpnpHeader> permissibleType : this.getHeaderTypes()) {
                if (permissibleType.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }
        
        public static Type getByHttpName(final String httpName) {
            if (httpName == null) {
                return null;
            }
            return Type.byName.get(httpName.toUpperCase(Locale.ROOT));
        }
        
        static {
            Type.byName = new HashMap<String, Type>() {
                {
                    for (final Type t : Type.values()) {
                        this.put(t.getHttpName(), t);
                    }
                }
            };
        }
    }
}
