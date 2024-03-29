// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestUserAgent implements HttpRequestInterceptor
{
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (!request.containsHeader("User-Agent")) {
            final String useragent = HttpProtocolParams.getUserAgent(request.getParams());
            if (useragent != null) {
                request.addHeader("User-Agent", useragent);
            }
        }
    }
}
