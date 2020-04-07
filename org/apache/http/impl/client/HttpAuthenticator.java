// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.auth.AuthOption;
import java.util.Queue;
import java.util.Map;
import org.apache.http.auth.MalformedChallengeException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.protocol.HttpContext;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class HttpAuthenticator
{
    private final Log log;
    
    public HttpAuthenticator(final Log log) {
        this.log = ((log != null) ? log : LogFactory.getLog(this.getClass()));
    }
    
    public HttpAuthenticator() {
        this(null);
    }
    
    public boolean isAuthenticationRequested(final HttpHost host, final HttpResponse response, final AuthenticationStrategy authStrategy, final AuthState authState, final HttpContext context) {
        if (authStrategy.isAuthenticationRequested(host, response, context)) {
            if (authState.getState() == AuthProtocolState.SUCCESS) {
                authStrategy.authFailed(host, authState.getAuthScheme(), context);
            }
            this.log.debug("Authentication required");
            return true;
        }
        switch (authState.getState()) {
            case CHALLENGED:
            case HANDSHAKE: {
                this.log.debug("Authentication succeeded");
                authState.setState(AuthProtocolState.SUCCESS);
                authStrategy.authSucceeded(host, authState.getAuthScheme(), context);
                break;
            }
            case SUCCESS: {
                break;
            }
            default: {
                authState.setState(AuthProtocolState.UNCHALLENGED);
                break;
            }
        }
        return false;
    }
    
    public boolean authenticate(final HttpHost host, final HttpResponse response, final AuthenticationStrategy authStrategy, final AuthState authState, final HttpContext context) {
        try {
            if (this.log.isDebugEnabled()) {
                this.log.debug(host.toHostString() + " requested authentication");
            }
            final Map<String, Header> challenges = authStrategy.getChallenges(host, response, context);
            if (challenges.isEmpty()) {
                this.log.debug("Response contains no authentication challenges");
                return false;
            }
            final AuthScheme authScheme = authState.getAuthScheme();
            switch (authState.getState()) {
                case FAILURE: {
                    return false;
                }
                case SUCCESS: {
                    authState.reset();
                    break;
                }
                case CHALLENGED:
                case HANDSHAKE: {
                    if (authScheme == null) {
                        this.log.debug("Auth scheme is null");
                        authStrategy.authFailed(host, null, context);
                        authState.reset();
                        authState.setState(AuthProtocolState.FAILURE);
                        return false;
                    }
                }
                case UNCHALLENGED: {
                    if (authScheme == null) {
                        break;
                    }
                    final String id = authScheme.getSchemeName();
                    final Header challenge = challenges.get(id.toLowerCase(Locale.US));
                    if (challenge == null) {
                        authState.reset();
                        break;
                    }
                    this.log.debug("Authorization challenge processed");
                    authScheme.processChallenge(challenge);
                    if (authScheme.isComplete()) {
                        this.log.debug("Authentication failed");
                        authStrategy.authFailed(host, authState.getAuthScheme(), context);
                        authState.reset();
                        authState.setState(AuthProtocolState.FAILURE);
                        return false;
                    }
                    authState.setState(AuthProtocolState.HANDSHAKE);
                    return true;
                }
            }
            final Queue<AuthOption> authOptions = authStrategy.select(challenges, host, response, context);
            if (authOptions != null && !authOptions.isEmpty()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Selected authentication options: " + authOptions);
                }
                authState.setState(AuthProtocolState.CHALLENGED);
                authState.update(authOptions);
                return true;
            }
            return false;
        }
        catch (MalformedChallengeException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn("Malformed challenge: " + ex.getMessage());
            }
            authState.reset();
            return false;
        }
    }
}
