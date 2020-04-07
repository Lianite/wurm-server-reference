// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.exception;

public abstract class OAuthException extends Exception
{
    public OAuthException(final String message) {
        super(message);
    }
    
    public OAuthException(final Throwable cause) {
        super(cause);
    }
    
    public OAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
