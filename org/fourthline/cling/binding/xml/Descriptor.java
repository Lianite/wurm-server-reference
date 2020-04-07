// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.w3c.dom.Node;

public abstract class Descriptor
{
    public interface Service
    {
        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:service-1-0";
        
        public enum ELEMENT
        {
            scpd, 
            specVersion, 
            major, 
            minor, 
            actionList, 
            action, 
            name, 
            argumentList, 
            argument, 
            direction, 
            relatedStateVariable, 
            retval, 
            serviceStateTable, 
            stateVariable, 
            dataType, 
            defaultValue, 
            allowedValueList, 
            allowedValue, 
            allowedValueRange, 
            minimum, 
            maximum, 
            step;
            
            public static ELEMENT valueOrNullOf(final String s) {
                try {
                    return valueOf(s);
                }
                catch (IllegalArgumentException ex) {
                    return null;
                }
            }
            
            public boolean equals(final Node node) {
                return this.toString().equals(node.getLocalName());
            }
        }
        
        public enum ATTRIBUTE
        {
            sendEvents;
        }
    }
    
    public interface Device
    {
        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:device-1-0";
        public static final String DLNA_NAMESPACE_URI = "urn:schemas-dlna-org:device-1-0";
        public static final String DLNA_PREFIX = "dlna";
        public static final String SEC_NAMESPACE_URI = "http://www.sec.co.kr/dlna";
        public static final String SEC_PREFIX = "sec";
        
        public enum ELEMENT
        {
            root, 
            specVersion, 
            major, 
            minor, 
            URLBase, 
            device, 
            UDN, 
            X_DLNADOC, 
            X_DLNACAP, 
            ProductCap, 
            X_ProductCap, 
            deviceType, 
            friendlyName, 
            manufacturer, 
            manufacturerURL, 
            modelDescription, 
            modelName, 
            modelNumber, 
            modelURL, 
            presentationURL, 
            UPC, 
            serialNumber, 
            iconList, 
            icon, 
            width, 
            height, 
            depth, 
            url, 
            mimetype, 
            serviceList, 
            service, 
            serviceType, 
            serviceId, 
            SCPDURL, 
            controlURL, 
            eventSubURL, 
            deviceList;
            
            public static ELEMENT valueOrNullOf(final String s) {
                try {
                    return valueOf(s);
                }
                catch (IllegalArgumentException ex) {
                    return null;
                }
            }
            
            public boolean equals(final Node node) {
                return this.toString().equals(node.getLocalName());
            }
        }
    }
}
