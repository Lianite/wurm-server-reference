// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.params;

public final class HttpConnectionParams implements CoreConnectionPNames
{
    public static int getSoTimeout(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getIntParameter("http.socket.timeout", 0);
    }
    
    public static void setSoTimeout(final HttpParams params, final int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter("http.socket.timeout", timeout);
    }
    
    public static boolean getSoReuseaddr(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getBooleanParameter("http.socket.reuseaddr", false);
    }
    
    public static void setSoReuseaddr(final HttpParams params, final boolean reuseaddr) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter("http.socket.reuseaddr", reuseaddr);
    }
    
    public static boolean getTcpNoDelay(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getBooleanParameter("http.tcp.nodelay", true);
    }
    
    public static void setTcpNoDelay(final HttpParams params, final boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter("http.tcp.nodelay", value);
    }
    
    public static int getSocketBufferSize(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getIntParameter("http.socket.buffer-size", -1);
    }
    
    public static void setSocketBufferSize(final HttpParams params, final int size) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter("http.socket.buffer-size", size);
    }
    
    public static int getLinger(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getIntParameter("http.socket.linger", -1);
    }
    
    public static void setLinger(final HttpParams params, final int value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter("http.socket.linger", value);
    }
    
    public static int getConnectionTimeout(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getIntParameter("http.connection.timeout", 0);
    }
    
    public static void setConnectionTimeout(final HttpParams params, final int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter("http.connection.timeout", timeout);
    }
    
    public static boolean isStaleCheckingEnabled(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getBooleanParameter("http.connection.stalecheck", true);
    }
    
    public static void setStaleCheckingEnabled(final HttpParams params, final boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter("http.connection.stalecheck", value);
    }
    
    public static boolean getSoKeepalive(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return params.getBooleanParameter("http.socket.keepalive", false);
    }
    
    public static void setSoKeepalive(final HttpParams params, final boolean enableKeepalive) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter("http.socket.keepalive", enableKeepalive);
    }
}
