// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.basic;

import java.net.HttpURLConnection;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.AbstractOAuthConsumer;

public class DefaultOAuthConsumer extends AbstractOAuthConsumer
{
    private static final long serialVersionUID = 1L;
    
    public DefaultOAuthConsumer(final String consumerKey, final String consumerSecret) {
        super(consumerKey, consumerSecret);
    }
    
    protected HttpRequest wrap(final Object request) {
        if (!(request instanceof HttpURLConnection)) {
            throw new IllegalArgumentException("The default consumer expects requests of type java.net.HttpURLConnection");
        }
        return new HttpURLConnectionRequestAdapter((HttpURLConnection)request);
    }
}
