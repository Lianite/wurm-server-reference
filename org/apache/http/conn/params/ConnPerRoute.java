// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;

public interface ConnPerRoute
{
    int getMaxForRoute(final HttpRoute p0);
}
