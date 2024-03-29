// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.HttpRequestInterceptor;

@ThreadSafe
public class RequestDate implements HttpRequestInterceptor
{
    private static final HttpDateGenerator DATE_GENERATOR;
    
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null.");
        }
        if (request instanceof HttpEntityEnclosingRequest && !request.containsHeader("Date")) {
            final String httpdate = RequestDate.DATE_GENERATOR.getCurrentDate();
            request.setHeader("Date", httpdate);
        }
    }
    
    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }
}
