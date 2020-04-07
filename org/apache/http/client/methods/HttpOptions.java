// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.methods;

import org.apache.http.HeaderElement;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpResponse;
import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class HttpOptions extends HttpRequestBase
{
    public static final String METHOD_NAME = "OPTIONS";
    
    public HttpOptions() {
    }
    
    public HttpOptions(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpOptions(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    public String getMethod() {
        return "OPTIONS";
    }
    
    public Set<String> getAllowedMethods(final HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        final HeaderIterator it = response.headerIterator("Allow");
        final Set<String> methods = new HashSet<String>();
        while (it.hasNext()) {
            final Header header = it.nextHeader();
            final HeaderElement[] arr$;
            final HeaderElement[] elements = arr$ = header.getElements();
            for (final HeaderElement element : arr$) {
                methods.add(element.getName());
            }
        }
        return methods;
    }
}
