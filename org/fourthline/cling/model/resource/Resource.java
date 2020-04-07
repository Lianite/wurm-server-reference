// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.ExpirationDetails;
import java.util.List;
import java.net.URISyntaxException;
import java.net.URI;

public class Resource<M>
{
    private URI pathQuery;
    private M model;
    
    public Resource(final URI pathQuery, final M model) {
        try {
            this.pathQuery = new URI(null, null, pathQuery.getPath(), pathQuery.getQuery(), null);
        }
        catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        this.model = model;
        if (model == null) {
            throw new IllegalArgumentException("Model instance must not be null");
        }
    }
    
    public URI getPathQuery() {
        return this.pathQuery;
    }
    
    public M getModel() {
        return this.model;
    }
    
    public boolean matches(final URI pathQuery) {
        return pathQuery.equals(this.getPathQuery());
    }
    
    public void maintain(final List<Runnable> pendingExecutions, final ExpirationDetails expirationDetails) {
    }
    
    public void shutdown() {
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Resource resource = (Resource)o;
        return this.getPathQuery().equals(resource.getPathQuery());
    }
    
    @Override
    public int hashCode() {
        return this.getPathQuery().hashCode();
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") URI: " + this.getPathQuery();
    }
}
