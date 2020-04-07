// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import oauth.signpost.http.HttpResponse;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import java.util.HashMap;
import java.util.Map;
import oauth.signpost.http.HttpParameters;

public abstract class AbstractOAuthProvider implements OAuthProvider
{
    private static final long serialVersionUID = 1L;
    private String requestTokenEndpointUrl;
    private String accessTokenEndpointUrl;
    private String authorizationWebsiteUrl;
    private HttpParameters responseParameters;
    private Map<String, String> defaultHeaders;
    private boolean isOAuth10a;
    private transient OAuthProviderListener listener;
    
    public AbstractOAuthProvider(final String requestTokenEndpointUrl, final String accessTokenEndpointUrl, final String authorizationWebsiteUrl) {
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
        this.responseParameters = new HttpParameters();
        this.defaultHeaders = new HashMap<String, String>();
    }
    
    public synchronized String retrieveRequestToken(final OAuthConsumer consumer, final String callbackUrl, final String... customOAuthParams) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        consumer.setTokenWithSecret(null, null);
        final HttpParameters params = new HttpParameters();
        params.putAll(customOAuthParams, true);
        params.put("oauth_callback", callbackUrl, true);
        this.retrieveToken(consumer, this.requestTokenEndpointUrl, params);
        final String callbackConfirmed = this.responseParameters.getFirst("oauth_callback_confirmed");
        this.responseParameters.remove((Object)"oauth_callback_confirmed");
        this.isOAuth10a = Boolean.TRUE.toString().equals(callbackConfirmed);
        if (this.isOAuth10a) {
            return OAuth.addQueryParameters(this.authorizationWebsiteUrl, "oauth_token", consumer.getToken());
        }
        return OAuth.addQueryParameters(this.authorizationWebsiteUrl, "oauth_token", consumer.getToken(), "oauth_callback", callbackUrl);
    }
    
    public synchronized void retrieveAccessToken(final OAuthConsumer consumer, final String oauthVerifier, final String... customOAuthParams) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException("Authorized request token or token secret not set. Did you retrieve an authorized request token before?");
        }
        final HttpParameters params = new HttpParameters();
        params.putAll(customOAuthParams, true);
        if (this.isOAuth10a && oauthVerifier != null) {
            params.put("oauth_verifier", oauthVerifier, true);
        }
        this.retrieveToken(consumer, this.accessTokenEndpointUrl, params);
    }
    
    protected void retrieveToken(final OAuthConsumer consumer, final String endpointUrl, final HttpParameters customOAuthParams) throws OAuthMessageSignerException, OAuthCommunicationException, OAuthNotAuthorizedException, OAuthExpectationFailedException {
        final Map<String, String> defaultHeaders = this.getRequestHeaders();
        if (consumer.getConsumerKey() == null || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException("Consumer key or secret not set");
        }
        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = this.createRequest(endpointUrl);
            for (final String header : defaultHeaders.keySet()) {
                request.setHeader(header, defaultHeaders.get(header));
            }
            if (customOAuthParams != null && !customOAuthParams.isEmpty()) {
                consumer.setAdditionalParameters(customOAuthParams);
            }
            if (this.listener != null) {
                this.listener.prepareRequest(request);
            }
            consumer.sign(request);
            if (this.listener != null) {
                this.listener.prepareSubmission(request);
            }
            response = this.sendRequest(request);
            final int statusCode = response.getStatusCode();
            boolean requestHandled = false;
            if (this.listener != null) {
                requestHandled = this.listener.onResponseReceived(request, response);
            }
            if (requestHandled) {
                return;
            }
            if (statusCode >= 300) {
                this.handleUnexpectedResponse(statusCode, response);
            }
            final HttpParameters responseParams = OAuth.decodeForm(response.getContent());
            final String token = responseParams.getFirst("oauth_token");
            final String secret = responseParams.getFirst("oauth_token_secret");
            responseParams.remove((Object)"oauth_token");
            responseParams.remove((Object)"oauth_token_secret");
            this.setResponseParameters(responseParams);
            if (token == null || secret == null) {
                throw new OAuthExpectationFailedException("Request token or token secret not set in server reply. The service provider you use is probably buggy.");
            }
            consumer.setTokenWithSecret(token, secret);
        }
        catch (OAuthNotAuthorizedException e) {
            throw e;
        }
        catch (OAuthExpectationFailedException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new OAuthCommunicationException(e3);
        }
        finally {
            try {
                this.closeConnection(request, response);
            }
            catch (Exception e4) {
                throw new OAuthCommunicationException(e4);
            }
        }
    }
    
    protected void handleUnexpectedResponse(final int statusCode, final HttpResponse response) throws Exception {
        if (response == null) {
            return;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
        final StringBuilder responseBody = new StringBuilder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            responseBody.append(line);
        }
        switch (statusCode) {
            case 401: {
                throw new OAuthNotAuthorizedException(responseBody.toString());
            }
            default: {
                throw new OAuthCommunicationException("Service provider responded in error: " + statusCode + " (" + response.getReasonPhrase() + ")", responseBody.toString());
            }
        }
    }
    
    protected abstract HttpRequest createRequest(final String p0) throws Exception;
    
    protected abstract HttpResponse sendRequest(final HttpRequest p0) throws Exception;
    
    protected void closeConnection(final HttpRequest request, final HttpResponse response) throws Exception {
    }
    
    public HttpParameters getResponseParameters() {
        return this.responseParameters;
    }
    
    protected String getResponseParameter(final String key) {
        return this.responseParameters.getFirst(key);
    }
    
    public void setResponseParameters(final HttpParameters parameters) {
        this.responseParameters = parameters;
    }
    
    public void setOAuth10a(final boolean isOAuth10aProvider) {
        this.isOAuth10a = isOAuth10aProvider;
    }
    
    public boolean isOAuth10a() {
        return this.isOAuth10a;
    }
    
    public String getRequestTokenEndpointUrl() {
        return this.requestTokenEndpointUrl;
    }
    
    public String getAccessTokenEndpointUrl() {
        return this.accessTokenEndpointUrl;
    }
    
    public String getAuthorizationWebsiteUrl() {
        return this.authorizationWebsiteUrl;
    }
    
    public void setRequestHeader(final String header, final String value) {
        this.defaultHeaders.put(header, value);
    }
    
    public Map<String, String> getRequestHeaders() {
        return this.defaultHeaders;
    }
    
    public void setListener(final OAuthProviderListener listener) {
        this.listener = listener;
    }
    
    public void removeListener(final OAuthProviderListener listener) {
        this.listener = null;
    }
}
