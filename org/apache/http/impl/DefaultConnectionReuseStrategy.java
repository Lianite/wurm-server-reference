// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl;

import org.apache.http.message.BasicTokenIterator;
import org.apache.http.TokenIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.ConnectionReuseStrategy;

@Immutable
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null.");
        }
        final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        final Header teh = response.getFirstHeader("Transfer-Encoding");
        if (teh != null) {
            if (!"chunked".equalsIgnoreCase(teh.getValue())) {
                return false;
            }
        }
        else if (this.canResponseHaveBody(response)) {
            final Header[] clhs = response.getHeaders("Content-Length");
            if (clhs.length != 1) {
                return false;
            }
            final Header clh = clhs[0];
            try {
                final int contentLen = Integer.parseInt(clh.getValue());
                if (contentLen < 0) {
                    return false;
                }
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        HeaderIterator hit = response.headerIterator("Connection");
        if (!hit.hasNext()) {
            hit = response.headerIterator("Proxy-Connection");
        }
        if (hit.hasNext()) {
            try {
                final TokenIterator ti = this.createTokenIterator(hit);
                boolean keepalive = false;
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                    if (!"Keep-Alive".equalsIgnoreCase(token)) {
                        continue;
                    }
                    keepalive = true;
                }
                if (keepalive) {
                    return true;
                }
            }
            catch (ParseException px) {
                return false;
            }
        }
        return !ver.lessEquals(HttpVersion.HTTP_1_0);
    }
    
    protected TokenIterator createTokenIterator(final HeaderIterator hit) {
        return new BasicTokenIterator(hit);
    }
    
    private boolean canResponseHaveBody(final HttpResponse response) {
        final int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }
}
