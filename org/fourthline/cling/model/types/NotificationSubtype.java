// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public enum NotificationSubtype
{
    ALIVE("ssdp:alive"), 
    UPDATE("ssdp:update"), 
    BYEBYE("ssdp:byebye"), 
    ALL("ssdp:all"), 
    DISCOVER("ssdp:discover"), 
    PROPCHANGE("upnp:propchange");
    
    private String headerString;
    
    private NotificationSubtype(final String headerString) {
        this.headerString = headerString;
    }
    
    public String getHeaderString() {
        return this.headerString;
    }
}
