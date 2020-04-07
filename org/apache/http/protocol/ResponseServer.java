// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpResponseInterceptor;

@Immutable
public class ResponseServer implements HttpResponseInterceptor
{
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (!response.containsHeader("Server")) {
            final String s = (String)response.getParams().getParameter("http.origin-server");
            if (s != null) {
                response.addHeader("Server", s);
            }
        }
    }
}
