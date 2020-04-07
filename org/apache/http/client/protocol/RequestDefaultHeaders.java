// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import java.util.Iterator;
import org.apache.http.Header;
import java.util.Collection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestDefaultHeaders implements HttpRequestInterceptor
{
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            return;
        }
        final Collection<Header> defHeaders = (Collection<Header>)request.getParams().getParameter("http.default-headers");
        if (defHeaders != null) {
            for (final Header defHeader : defHeaders) {
                request.addHeader(defHeader);
            }
        }
    }
}
