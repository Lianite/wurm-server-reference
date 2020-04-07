// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpVersion;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestExpectContinue implements HttpRequestInterceptor
{
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null && entity.getContentLength() != 0L) {
                final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
                if (HttpProtocolParams.useExpectContinue(request.getParams()) && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                    request.addHeader("Expect", "100-continue");
                }
            }
        }
    }
}
