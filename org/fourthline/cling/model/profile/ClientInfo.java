// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.message.header.UserAgentHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpHeaders;

public class ClientInfo
{
    protected final UpnpHeaders requestHeaders;
    
    public ClientInfo() {
        this(new UpnpHeaders());
    }
    
    public ClientInfo(final UpnpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
    
    public UpnpHeaders getRequestHeaders() {
        return this.requestHeaders;
    }
    
    public String getRequestUserAgent() {
        return this.getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.USER_AGENT);
    }
    
    public void setRequestUserAgent(final String userAgent) {
        this.getRequestHeaders().add(UpnpHeader.Type.USER_AGENT, new UserAgentHeader(userAgent));
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") User-Agent: " + this.getRequestUserAgent();
    }
}
