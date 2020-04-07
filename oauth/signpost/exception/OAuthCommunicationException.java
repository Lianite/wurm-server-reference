// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.exception;

public class OAuthCommunicationException extends OAuthException
{
    private String responseBody;
    
    public OAuthCommunicationException(final Exception cause) {
        super("Communication with the service provider failed: " + cause.getLocalizedMessage(), cause);
    }
    
    public OAuthCommunicationException(final String message, final String responseBody) {
        super(message);
        this.responseBody = responseBody;
    }
    
    public String getResponseBody() {
        return this.responseBody;
    }
}
