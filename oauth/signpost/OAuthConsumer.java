// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.SigningStrategy;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.OAuthMessageSigner;
import java.io.Serializable;

public interface OAuthConsumer extends Serializable
{
    void setMessageSigner(final OAuthMessageSigner p0);
    
    void setAdditionalParameters(final HttpParameters p0);
    
    void setSigningStrategy(final SigningStrategy p0);
    
    void setSendEmptyTokens(final boolean p0);
    
    HttpRequest sign(final HttpRequest p0) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
    
    HttpRequest sign(final Object p0) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
    
    String sign(final String p0) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
    
    void setTokenWithSecret(final String p0, final String p1);
    
    String getToken();
    
    String getTokenSecret();
    
    String getConsumerKey();
    
    String getConsumerSecret();
    
    HttpParameters getRequestParameters();
}
