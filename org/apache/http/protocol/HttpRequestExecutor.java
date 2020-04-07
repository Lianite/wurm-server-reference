// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpVersion;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;

@Immutable
public class HttpRequestExecutor
{
    protected boolean canResponseHaveBody(final HttpRequest request, final HttpResponse response) {
        if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            return false;
        }
        final int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }
    
    public HttpResponse execute(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws IOException, HttpException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("Client connection may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        try {
            HttpResponse response = this.doSendRequest(request, conn, context);
            if (response == null) {
                response = this.doReceiveResponse(request, conn, context);
            }
            return response;
        }
        catch (IOException ex) {
            closeConnection(conn);
            throw ex;
        }
        catch (HttpException ex2) {
            closeConnection(conn);
            throw ex2;
        }
        catch (RuntimeException ex3) {
            closeConnection(conn);
            throw ex3;
        }
    }
    
    private static final void closeConnection(final HttpClientConnection conn) {
        try {
            conn.close();
        }
        catch (IOException ex) {}
    }
    
    public void preProcess(final HttpRequest request, final HttpProcessor processor, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("HTTP processor may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        context.setAttribute("http.request", request);
        processor.process(request, context);
    }
    
    protected HttpResponse doSendRequest(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws IOException, HttpException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("HTTP connection may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        HttpResponse response = null;
        context.setAttribute("http.connection", conn);
        context.setAttribute("http.request_sent", Boolean.FALSE);
        conn.sendRequestHeader(request);
        if (request instanceof HttpEntityEnclosingRequest) {
            boolean sendentity = true;
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            if (((HttpEntityEnclosingRequest)request).expectContinue() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                conn.flush();
                final int tms = request.getParams().getIntParameter("http.protocol.wait-for-continue", 2000);
                if (conn.isResponseAvailable(tms)) {
                    response = conn.receiveResponseHeader();
                    if (this.canResponseHaveBody(request, response)) {
                        conn.receiveResponseEntity(response);
                    }
                    final int status = response.getStatusLine().getStatusCode();
                    if (status < 200) {
                        if (status != 100) {
                            throw new ProtocolException("Unexpected response: " + response.getStatusLine());
                        }
                        response = null;
                    }
                    else {
                        sendentity = false;
                    }
                }
            }
            if (sendentity) {
                conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
            }
        }
        conn.flush();
        context.setAttribute("http.request_sent", Boolean.TRUE);
        return response;
    }
    
    protected HttpResponse doReceiveResponse(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("HTTP connection may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        HttpResponse response = null;
        for (int statuscode = 0; response == null || statuscode < 200; statuscode = response.getStatusLine().getStatusCode()) {
            response = conn.receiveResponseHeader();
            if (this.canResponseHaveBody(request, response)) {
                conn.receiveResponseEntity(response);
            }
        }
        return response;
    }
    
    public void postProcess(final HttpResponse response, final HttpProcessor processor, final HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("HTTP processor may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        context.setAttribute("http.response", response);
        processor.process(response, context);
    }
}
