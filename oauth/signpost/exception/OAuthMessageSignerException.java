// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.exception;

public class OAuthMessageSignerException extends OAuthException
{
    public OAuthMessageSignerException(final String message) {
        super(message);
    }
    
    public OAuthMessageSignerException(final Exception cause) {
        super(cause);
    }
}
