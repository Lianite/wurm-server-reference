// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import java.util.Collections;
import java.util.Arrays;
import org.apache.http.client.AuthCache;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScheme;
import java.util.Iterator;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.auth.AuthSchemeRegistry;
import java.util.LinkedList;
import org.apache.http.auth.AuthOption;
import java.util.Queue;
import java.util.Locale;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.FormattedHeader;
import java.util.HashMap;
import org.apache.http.Header;
import java.util.Map;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.AuthenticationStrategy;

@Immutable
class AuthenticationStrategyImpl implements AuthenticationStrategy
{
    private final Log log;
    private static final List<String> DEFAULT_SCHEME_PRIORITY;
    private final int challengeCode;
    private final String headerName;
    private final String prefParamName;
    
    AuthenticationStrategyImpl(final int challengeCode, final String headerName, final String prefParamName) {
        this.log = LogFactory.getLog(this.getClass());
        this.challengeCode = challengeCode;
        this.headerName = headerName;
        this.prefParamName = prefParamName;
    }
    
    public boolean isAuthenticationRequested(final HttpHost authhost, final HttpResponse response, final HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final int status = response.getStatusLine().getStatusCode();
        return status == this.challengeCode;
    }
    
    public Map<String, Header> getChallenges(final HttpHost authhost, final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final Header[] headers = response.getHeaders(this.headerName);
        final Map<String, Header> map = new HashMap<String, Header>(headers.length);
        for (final Header header : headers) {
            CharArrayBuffer buffer;
            int pos;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader)header).getBuffer();
                pos = ((FormattedHeader)header).getValuePos();
            }
            else {
                final String s = header.getValue();
                if (s == null) {
                    throw new MalformedChallengeException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
                ++pos;
            }
            final int beginIndex = pos;
            while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
                ++pos;
            }
            final int endIndex = pos;
            final String s2 = buffer.substring(beginIndex, endIndex);
            map.put(s2.toLowerCase(Locale.US), header);
        }
        return map;
    }
    
    public Queue<AuthOption> select(final Map<String, Header> challenges, final HttpHost authhost, final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        if (challenges == null) {
            throw new IllegalArgumentException("Map of auth challenges may not be null");
        }
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        final Queue<AuthOption> options = new LinkedList<AuthOption>();
        final AuthSchemeRegistry registry = (AuthSchemeRegistry)context.getAttribute("http.authscheme-registry");
        if (registry == null) {
            this.log.debug("Auth scheme registry not set in the context");
            return options;
        }
        final CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
            return options;
        }
        List<String> authPrefs = (List<String>)response.getParams().getParameter(this.prefParamName);
        if (authPrefs == null) {
            authPrefs = AuthenticationStrategyImpl.DEFAULT_SCHEME_PRIORITY;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
        }
        for (final String id : authPrefs) {
            final Header challenge = challenges.get(id.toLowerCase(Locale.US));
            if (challenge != null) {
                try {
                    final AuthScheme authScheme = registry.getAuthScheme(id, response.getParams());
                    authScheme.processChallenge(challenge);
                    final AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
                    final Credentials credentials = credsProvider.getCredentials(authScope);
                    if (credentials == null) {
                        continue;
                    }
                    options.add(new AuthOption(authScheme, credentials));
                }
                catch (IllegalStateException e) {
                    if (!this.log.isWarnEnabled()) {
                        continue;
                    }
                    this.log.warn("Authentication scheme " + id + " not supported");
                }
            }
            else {
                if (!this.log.isDebugEnabled()) {
                    continue;
                }
                this.log.debug("Challenge for " + id + " authentication scheme not available");
            }
        }
        return options;
    }
    
    public void authSucceeded(final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (authScheme == null) {
            throw new IllegalArgumentException("Auth scheme may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        if (this.isCachable(authScheme)) {
            AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute("http.auth.auth-cache", authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.put(authhost, authScheme);
        }
    }
    
    protected boolean isCachable(final AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        final String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest");
    }
    
    public void authFailed(final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        final AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
        if (authCache != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Clearing cached auth scheme for " + authhost);
            }
            authCache.remove(authhost);
        }
    }
    
    static {
        DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList((List<? extends String>)Arrays.asList("negotiate", "Kerberos", "NTLM", "Digest", "Basic"));
    }
}
