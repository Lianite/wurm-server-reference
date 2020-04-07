// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.protocol;

import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.Cookie;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HeaderIterator;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpResponseInterceptor;

@Immutable
public class ResponseProcessCookies implements HttpResponseInterceptor
{
    private final Log log;
    
    public ResponseProcessCookies() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        final CookieSpec cookieSpec = (CookieSpec)context.getAttribute("http.cookie-spec");
        if (cookieSpec == null) {
            this.log.debug("Cookie spec not specified in HTTP context");
            return;
        }
        final CookieStore cookieStore = (CookieStore)context.getAttribute("http.cookie-store");
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        final CookieOrigin cookieOrigin = (CookieOrigin)context.getAttribute("http.cookie-origin");
        if (cookieOrigin == null) {
            this.log.debug("Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it = response.headerIterator("Set-Cookie");
        this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it = response.headerIterator("Set-Cookie2");
            this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        }
    }
    
    private void processCookies(final HeaderIterator iterator, final CookieSpec cookieSpec, final CookieOrigin cookieOrigin, final CookieStore cookieStore) {
        while (iterator.hasNext()) {
            final Header header = iterator.nextHeader();
            try {
                final List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (final Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) {
                            continue;
                        }
                        this.log.debug("Cookie accepted: \"" + cookie + "\". ");
                    }
                    catch (MalformedCookieException ex) {
                        if (!this.log.isWarnEnabled()) {
                            continue;
                        }
                        this.log.warn("Cookie rejected: \"" + cookie + "\". " + ex.getMessage());
                    }
                }
            }
            catch (MalformedCookieException ex2) {
                if (!this.log.isWarnEnabled()) {
                    continue;
                }
                this.log.warn("Invalid cookie header: \"" + header + "\". " + ex2.getMessage());
            }
        }
    }
}
