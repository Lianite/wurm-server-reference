// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public final class ConnectedComponent implements Iterable<Element>
{
    private final List<Element> elements;
    boolean isRequired;
    
    public ConnectedComponent() {
        this.elements = new ArrayList<Element>();
    }
    
    public final boolean isCollection() {
        assert !this.elements.isEmpty();
        if (this.elements.size() > 1) {
            return true;
        }
        final Element n = this.elements.get(0);
        return n.hasSelfLoop();
    }
    
    public final boolean isRequired() {
        return this.isRequired;
    }
    
    void add(final Element e) {
        assert !this.elements.contains(e);
        this.elements.add(e);
    }
    
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }
    
    public String toString() {
        String s = this.elements.toString();
        if (this.isRequired()) {
            s += '!';
        }
        if (this.isCollection()) {
            s += '*';
        }
        return s;
    }
}
