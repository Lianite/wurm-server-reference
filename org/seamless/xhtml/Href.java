// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import java.net.URI;

public class Href
{
    private URI uri;
    
    public Href(final URI uri) {
        this.uri = uri;
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    public static Href fromString(String string) {
        if (string == null) {
            return null;
        }
        string = string.replaceAll(" ", "%20");
        return new Href(URI.create(string));
    }
    
    public String toString() {
        return this.getURI().toString();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Href href = (Href)o;
        return this.uri.equals(href.uri);
    }
    
    public int hashCode() {
        return this.uri.hashCode();
    }
}
