// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import java.util.Map;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import java.io.Serializable;

public interface OAuthProvider extends Serializable
{
    String retrieveRequestToken(final OAuthConsumer p0, final String p1, final String... p2) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException;
    
    void retrieveAccessToken(final OAuthConsumer p0, final String p1, final String... p2) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException;
    
    HttpParameters getResponseParameters();
    
    void setResponseParameters(final HttpParameters p0);
    
    @Deprecated
    void setRequestHeader(final String p0, final String p1);
    
    @Deprecated
    Map<String, String> getRequestHeaders();
    
    void setOAuth10a(final boolean p0);
    
    boolean isOAuth10a();
    
    String getRequestTokenEndpointUrl();
    
    String getAccessTokenEndpointUrl();
    
    String getAuthorizationWebsiteUrl();
    
    void setListener(final OAuthProviderListener p0);
    
    void removeListener(final OAuthProviderListener p0);
}
