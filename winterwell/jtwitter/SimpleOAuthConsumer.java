// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import java.net.HttpURLConnection;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.AbstractOAuthConsumer;

class SimpleOAuthConsumer extends AbstractOAuthConsumer
{
    private static final long serialVersionUID = 1L;
    
    public SimpleOAuthConsumer(final String consumerKey, final String consumerSecret) {
        super(consumerKey, consumerSecret);
    }
    
    @Override
    protected HttpRequest wrap(final Object request) {
        if (request instanceof HttpRequest) {
            return (HttpRequest)request;
        }
        return new HttpURLConnectionRequestAdapter((HttpURLConnection)request);
    }
}
