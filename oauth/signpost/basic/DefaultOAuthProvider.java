// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.basic;

import oauth.signpost.http.HttpResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.AbstractOAuthProvider;

public class DefaultOAuthProvider extends AbstractOAuthProvider
{
    private static final long serialVersionUID = 1L;
    
    public DefaultOAuthProvider(final String requestTokenEndpointUrl, final String accessTokenEndpointUrl, final String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
    }
    
    protected HttpRequest createRequest(final String endpointUrl) throws MalformedURLException, IOException {
        final HttpURLConnection connection = (HttpURLConnection)new URL(endpointUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Length", "0");
        return new HttpURLConnectionRequestAdapter(connection);
    }
    
    protected HttpResponse sendRequest(final HttpRequest request) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)request.unwrap();
        connection.connect();
        return new HttpURLConnectionResponseAdapter(connection);
    }
    
    protected void closeConnection(final HttpRequest request, final HttpResponse response) {
        final HttpURLConnection connection = (HttpURLConnection)request.unwrap();
        if (connection != null) {
            connection.disconnect();
        }
    }
}
