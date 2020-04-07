// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import java.util.NoSuchElementException;
import org.apache.http.Header;
import java.util.List;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.HeaderIterator;

@NotThreadSafe
public class BasicListHeaderIterator implements HeaderIterator
{
    protected final List<Header> allHeaders;
    protected int currentIndex;
    protected int lastIndex;
    protected String headerName;
    
    public BasicListHeaderIterator(final List<Header> headers, final String name) {
        if (headers == null) {
            throw new IllegalArgumentException("Header list must not be null.");
        }
        this.allHeaders = headers;
        this.headerName = name;
        this.currentIndex = this.findNext(-1);
        this.lastIndex = -1;
    }
    
    protected int findNext(int from) {
        if (from < -1) {
            return -1;
        }
        int to;
        boolean found;
        for (to = this.allHeaders.size() - 1, found = false; !found && from < to; ++from, found = this.filterHeader(from)) {}
        return found ? from : -1;
    }
    
    protected boolean filterHeader(final int index) {
        if (this.headerName == null) {
            return true;
        }
        final String name = this.allHeaders.get(index).getName();
        return this.headerName.equalsIgnoreCase(name);
    }
    
    public boolean hasNext() {
        return this.currentIndex >= 0;
    }
    
    public Header nextHeader() throws NoSuchElementException {
        final int current = this.currentIndex;
        if (current < 0) {
            throw new NoSuchElementException("Iteration already finished.");
        }
        this.lastIndex = current;
        this.currentIndex = this.findNext(current);
        return this.allHeaders.get(current);
    }
    
    public final Object next() throws NoSuchElementException {
        return this.nextHeader();
    }
    
    public void remove() throws UnsupportedOperationException {
        if (this.lastIndex < 0) {
            throw new IllegalStateException("No header to remove.");
        }
        this.allHeaders.remove(this.lastIndex);
        this.lastIndex = -1;
        --this.currentIndex;
    }
}
