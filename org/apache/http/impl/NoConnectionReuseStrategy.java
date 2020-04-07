// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl;

import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.ConnectionReuseStrategy;

@Immutable
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        return false;
    }
}
