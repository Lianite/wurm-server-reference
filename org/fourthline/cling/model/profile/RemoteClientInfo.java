// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.profile;

import org.seamless.http.RequestInfo;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.UserAgentHeader;
import java.net.InetAddress;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.Connection;

public class RemoteClientInfo extends ClientInfo
{
    protected final Connection connection;
    protected final UpnpHeaders extraResponseHeaders;
    
    public RemoteClientInfo() {
        this((StreamRequestMessage)null);
    }
    
    public RemoteClientInfo(final StreamRequestMessage requestMessage) {
        this((requestMessage != null) ? requestMessage.getConnection() : null, (requestMessage != null) ? requestMessage.getHeaders() : new UpnpHeaders());
    }
    
    public RemoteClientInfo(final Connection connection, final UpnpHeaders requestHeaders) {
        super(requestHeaders);
        this.extraResponseHeaders = new UpnpHeaders();
        this.connection = connection;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public boolean isRequestCancelled() {
        return !this.getConnection().isOpen();
    }
    
    public void throwIfRequestCancelled() throws InterruptedException {
        if (this.isRequestCancelled()) {
            throw new InterruptedException("Client's request cancelled");
        }
    }
    
    public InetAddress getRemoteAddress() {
        return this.getConnection().getRemoteAddress();
    }
    
    public InetAddress getLocalAddress() {
        return this.getConnection().getLocalAddress();
    }
    
    public UpnpHeaders getExtraResponseHeaders() {
        return this.extraResponseHeaders;
    }
    
    public void setResponseUserAgent(final String userAgent) {
        this.setResponseUserAgent(new UserAgentHeader(userAgent));
    }
    
    public void setResponseUserAgent(final UserAgentHeader userAgentHeader) {
        this.getExtraResponseHeaders().add(UpnpHeader.Type.USER_AGENT, userAgentHeader);
    }
    
    public boolean isWMPRequest() {
        return RequestInfo.isWMPRequest(this.getRequestUserAgent());
    }
    
    public boolean isXbox360Request() {
        return RequestInfo.isXbox360Request(this.getRequestUserAgent(), this.getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.SERVER));
    }
    
    public boolean isPS3Request() {
        return RequestInfo.isPS3Request(this.getRequestUserAgent(), this.getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.EXT_AV_CLIENT_INFO));
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") Remote Address: " + this.getRemoteAddress();
    }
}
