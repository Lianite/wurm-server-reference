// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl;

import org.apache.http.MethodNotSupportedException;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpRequestFactory;

@Immutable
public class DefaultHttpRequestFactory implements HttpRequestFactory
{
    private static final String[] RFC2616_COMMON_METHODS;
    private static final String[] RFC2616_ENTITY_ENC_METHODS;
    private static final String[] RFC2616_SPECIAL_METHODS;
    
    private static boolean isOneOf(final String[] methods, final String method) {
        for (int i = 0; i < methods.length; ++i) {
            if (methods[i].equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    public HttpRequest newHttpRequest(final RequestLine requestline) throws MethodNotSupportedException {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        final String method = requestline.getMethod();
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException {
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    static {
        RFC2616_COMMON_METHODS = new String[] { "GET" };
        RFC2616_ENTITY_ENC_METHODS = new String[] { "POST", "PUT" };
        RFC2616_SPECIAL_METHODS = new String[] { "HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT" };
    }
}
