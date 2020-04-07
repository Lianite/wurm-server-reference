// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import java.util.Iterator;
import java.util.List;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.Header;
import java.util.Date;
import java.util.Collection;
import org.apache.http.cookie.Cookie;
import java.util.ArrayList;
import org.apache.http.cookie.CookieOrigin;
import java.net.URISyntaxException;
import org.apache.http.ProtocolException;
import java.net.URI;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.HttpHost;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.client.CookieStore;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestAddCookies implements HttpRequestInterceptor
{
    private final Log log;
    
    public RequestAddCookies() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            return;
        }
        final CookieStore cookieStore = (CookieStore)context.getAttribute("http.cookie-store");
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        final CookieSpecRegistry registry = (CookieSpecRegistry)context.getAttribute("http.cookiespec-registry");
        if (registry == null) {
            this.log.debug("CookieSpec registry not specified in HTTP context");
            return;
        }
        final HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
        if (targetHost == null) {
            this.log.debug("Target host not set in the context");
            return;
        }
        final HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
        if (conn == null) {
            this.log.debug("HTTP connection not set in the context");
            return;
        }
        final String policy = HttpClientParams.getCookiePolicy(request.getParams());
        if (this.log.isDebugEnabled()) {
            this.log.debug("CookieSpec selected: " + policy);
        }
        URI requestURI;
        if (request instanceof HttpUriRequest) {
            requestURI = ((HttpUriRequest)request).getURI();
        }
        else {
            try {
                requestURI = new URI(request.getRequestLine().getUri());
            }
            catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid request URI: " + request.getRequestLine().getUri(), ex);
            }
        }
        final String hostName = targetHost.getHostName();
        int port = targetHost.getPort();
        if (port < 0) {
            final HttpRoute route = conn.getRoute();
            if (route.getHopCount() == 1) {
                port = conn.getRemotePort();
            }
            else {
                final String scheme = targetHost.getSchemeName();
                if (scheme.equalsIgnoreCase("http")) {
                    port = 80;
                }
                else if (scheme.equalsIgnoreCase("https")) {
                    port = 443;
                }
                else {
                    port = 0;
                }
            }
        }
        final CookieOrigin cookieOrigin = new CookieOrigin(hostName, port, requestURI.getPath(), conn.isSecure());
        final CookieSpec cookieSpec = registry.getCookieSpec(policy, request.getParams());
        final List<Cookie> cookies = new ArrayList<Cookie>(cookieStore.getCookies());
        final List<Cookie> matchedCookies = new ArrayList<Cookie>();
        final Date now = new Date();
        for (final Cookie cookie : cookies) {
            if (!cookie.isExpired(now)) {
                if (!cookieSpec.match(cookie, cookieOrigin)) {
                    continue;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Cookie " + cookie + " match " + cookieOrigin);
                }
                matchedCookies.add(cookie);
            }
            else {
                if (!this.log.isDebugEnabled()) {
                    continue;
                }
                this.log.debug("Cookie " + cookie + " expired");
            }
        }
        if (!matchedCookies.isEmpty()) {
            final List<Header> headers = cookieSpec.formatCookies(matchedCookies);
            for (final Header header : headers) {
                request.addHeader(header);
            }
        }
        final int ver = cookieSpec.getVersion();
        if (ver > 0) {
            boolean needVersionHeader = false;
            for (final Cookie cookie2 : matchedCookies) {
                if (ver != cookie2.getVersion() || !(cookie2 instanceof SetCookie2)) {
                    needVersionHeader = true;
                }
            }
            if (needVersionHeader) {
                final Header header = cookieSpec.getVersionHeader();
                if (header != null) {
                    request.addHeader(header);
                }
            }
        }
        context.setAttribute("http.cookie-spec", cookieSpec);
        context.setAttribute("http.cookie-origin", cookieOrigin);
    }
}
