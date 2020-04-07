// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http;

import org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy
{
    boolean keepAlive(final HttpResponse p0, final HttpContext p1);
}
