// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import java.util.List;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.Header;
import java.util.Map;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public class DefaultProxyAuthenticationHandler extends AbstractAuthenticationHandler
{
    public boolean isAuthenticationRequested(final HttpResponse response, final HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final int status = response.getStatusLine().getStatusCode();
        return status == 407;
    }
    
    public Map<String, Header> getChallenges(final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final Header[] headers = response.getHeaders("Proxy-Authenticate");
        return this.parseChallenges(headers);
    }
    
    protected List<String> getAuthPreferences(final HttpResponse response, final HttpContext context) {
        final List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.proxy-scheme-pref");
        if (authpref != null) {
            return authpref;
        }
        return super.getAuthPreferences(response, context);
    }
}
