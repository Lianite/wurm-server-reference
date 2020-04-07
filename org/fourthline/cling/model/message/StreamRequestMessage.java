// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.net.URL;
import java.net.URI;

public class StreamRequestMessage extends UpnpMessage<UpnpRequest>
{
    protected Connection connection;
    
    public StreamRequestMessage(final StreamRequestMessage source) {
        super(source);
        this.connection = source.getConnection();
    }
    
    public StreamRequestMessage(final UpnpRequest operation) {
        super(operation);
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URI uri) {
        super(new UpnpRequest(method, uri));
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URL url) {
        super(new UpnpRequest(method, url));
    }
    
    public StreamRequestMessage(final UpnpRequest operation, final String body) {
        super(operation, BodyType.STRING, body);
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URI uri, final String body) {
        super(new UpnpRequest(method, uri), BodyType.STRING, body);
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URL url, final String body) {
        super(new UpnpRequest(method, url), BodyType.STRING, body);
    }
    
    public StreamRequestMessage(final UpnpRequest operation, final byte[] body) {
        super(operation, BodyType.BYTES, body);
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URI uri, final byte[] body) {
        super(new UpnpRequest(method, uri), BodyType.BYTES, body);
    }
    
    public StreamRequestMessage(final UpnpRequest.Method method, final URL url, final byte[] body) {
        super(new UpnpRequest(method, url), BodyType.BYTES, body);
    }
    
    public URI getUri() {
        return this.getOperation().getURI();
    }
    
    public void setUri(final URI uri) {
        this.getOperation().setUri(uri);
    }
    
    public void setConnection(final Connection connection) {
        this.connection = connection;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
}
