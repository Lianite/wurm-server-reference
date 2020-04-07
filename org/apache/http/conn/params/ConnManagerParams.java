// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public final class ConnManagerParams implements ConnManagerPNames
{
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE;
    
    public static long getTimeout(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getLongParameter("http.conn-manager.timeout", 0L);
    }
    
    public static void setTimeout(final HttpParams params, final long timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setLongParameter("http.conn-manager.timeout", timeout);
    }
    
    public static void setMaxConnectionsPerRoute(final HttpParams params, final ConnPerRoute connPerRoute) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        params.setParameter("http.conn-manager.max-per-route", connPerRoute);
    }
    
    public static ConnPerRoute getMaxConnectionsPerRoute(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        ConnPerRoute connPerRoute = (ConnPerRoute)params.getParameter("http.conn-manager.max-per-route");
        if (connPerRoute == null) {
            connPerRoute = ConnManagerParams.DEFAULT_CONN_PER_ROUTE;
        }
        return connPerRoute;
    }
    
    public static void setMaxTotalConnections(final HttpParams params, final int maxTotalConnections) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        params.setIntParameter("http.conn-manager.max-total", maxTotalConnections);
    }
    
    public static int getMaxTotalConnections(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        return params.getIntParameter("http.conn-manager.max-total", 20);
    }
    
    static {
        DEFAULT_CONN_PER_ROUTE = new ConnPerRoute() {
            public int getMaxForRoute(final HttpRoute route) {
                return 2;
            }
        };
    }
}
