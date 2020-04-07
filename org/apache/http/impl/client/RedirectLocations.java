// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.net.URI;
import java.util.Set;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class RedirectLocations
{
    private final Set<URI> unique;
    private final List<URI> all;
    
    public RedirectLocations() {
        this.unique = new HashSet<URI>();
        this.all = new ArrayList<URI>();
    }
    
    public boolean contains(final URI uri) {
        return this.unique.contains(uri);
    }
    
    public void add(final URI uri) {
        this.unique.add(uri);
        this.all.add(uri);
    }
    
    public boolean remove(final URI uri) {
        final boolean removed = this.unique.remove(uri);
        if (removed) {
            final Iterator<URI> it = this.all.iterator();
            while (it.hasNext()) {
                final URI current = it.next();
                if (current.equals(uri)) {
                    it.remove();
                }
            }
        }
        return removed;
    }
    
    public List<URI> getAll() {
        return new ArrayList<URI>(this.all);
    }
}
