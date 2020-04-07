// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.HostPort;

public class HostHeader extends UpnpHeader<HostPort>
{
    int port;
    String group;
    
    public HostHeader() {
        this.port = 1900;
        this.group = "239.255.255.250";
        this.setValue(new HostPort(this.group, this.port));
    }
    
    public HostHeader(final int port) {
        this.port = 1900;
        this.group = "239.255.255.250";
        this.setValue(new HostPort(this.group, port));
    }
    
    public HostHeader(final String host, final int port) {
        this.port = 1900;
        this.group = "239.255.255.250";
        this.setValue(new HostPort(host, port));
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.contains(":")) {
            try {
                this.port = Integer.valueOf(s.substring(s.indexOf(":") + 1));
                this.group = s.substring(0, s.indexOf(":"));
                this.setValue(new HostPort(this.group, this.port));
                return;
            }
            catch (NumberFormatException ex) {
                throw new InvalidHeaderException("Invalid HOST header value, can't parse port: " + s + " - " + ex.getMessage());
            }
        }
        this.group = s;
        this.setValue(new HostPort(this.group, this.port));
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
