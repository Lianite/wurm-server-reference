// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import oauth.signpost.http.HttpResponse;
import oauth.signpost.http.HttpRequest;

public interface OAuthProviderListener
{
    void prepareRequest(final HttpRequest p0) throws Exception;
    
    void prepareSubmission(final HttpRequest p0) throws Exception;
    
    boolean onResponseReceived(final HttpRequest p0, final HttpResponse p1) throws Exception;
}
