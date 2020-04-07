// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

public class UpnpRequest extends UpnpOperation
{
    private Method method;
    private URI uri;
    
    public UpnpRequest(final Method method) {
        this.method = method;
    }
    
    public UpnpRequest(final Method method, final URI uri) {
        this.method = method;
        this.uri = uri;
    }
    
    public UpnpRequest(final Method method, final URL url) {
        this.method = method;
        try {
            if (url != null) {
                this.uri = url.toURI();
            }
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public String getHttpMethodName() {
        return this.method.getHttpName();
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    public void setUri(final URI uri) {
        this.uri = uri;
    }
    
    @Override
    public String toString() {
        return this.getHttpMethodName() + ((this.getURI() != null) ? (" " + this.getURI()) : "");
    }
    
    public enum Method
    {
        GET("GET"), 
        POST("POST"), 
        NOTIFY("NOTIFY"), 
        MSEARCH("M-SEARCH"), 
        SUBSCRIBE("SUBSCRIBE"), 
        UNSUBSCRIBE("UNSUBSCRIBE"), 
        UNKNOWN("UNKNOWN");
        
        private static Map<String, Method> byName;
        private String httpName;
        
        private Method(final String httpName) {
            this.httpName = httpName;
        }
        
        public String getHttpName() {
            return this.httpName;
        }
        
        public static Method getByHttpName(final String httpName) {
            if (httpName == null) {
                return Method.UNKNOWN;
            }
            final Method m = Method.byName.get(httpName.toUpperCase(Locale.ROOT));
            return (m != null) ? m : Method.UNKNOWN;
        }
        
        static {
            Method.byName = new HashMap<String, Method>() {
                {
                    for (final Method m : Method.values()) {
                        this.put(m.getHttpName(), m);
                    }
                }
            };
        }
    }
}
