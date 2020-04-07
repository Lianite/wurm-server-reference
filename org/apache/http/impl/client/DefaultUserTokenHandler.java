// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScheme;
import javax.net.ssl.SSLSession;
import java.security.Principal;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.auth.AuthState;
import org.apache.http.protocol.HttpContext;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.UserTokenHandler;

@Immutable
public class DefaultUserTokenHandler implements UserTokenHandler
{
    public Object getUserToken(final HttpContext context) {
        Principal userPrincipal = null;
        final AuthState targetAuthState = (AuthState)context.getAttribute("http.auth.target-scope");
        if (targetAuthState != null) {
            userPrincipal = getAuthPrincipal(targetAuthState);
            if (userPrincipal == null) {
                final AuthState proxyAuthState = (AuthState)context.getAttribute("http.auth.proxy-scope");
                userPrincipal = getAuthPrincipal(proxyAuthState);
            }
        }
        if (userPrincipal == null) {
            final HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
            if (conn.isOpen()) {
                final SSLSession sslsession = conn.getSSLSession();
                if (sslsession != null) {
                    userPrincipal = sslsession.getLocalPrincipal();
                }
            }
        }
        return userPrincipal;
    }
    
    private static Principal getAuthPrincipal(final AuthState authState) {
        final AuthScheme scheme = authState.getAuthScheme();
        if (scheme != null && scheme.isComplete() && scheme.isConnectionBased()) {
            final Credentials creds = authState.getCredentials();
            if (creds != null) {
                return creds.getUserPrincipal();
            }
        }
        return null;
    }
}
