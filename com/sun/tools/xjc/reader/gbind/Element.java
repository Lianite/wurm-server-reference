// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Element extends Expression implements ElementSet
{
    final Set<Element> foreEdges;
    final Set<Element> backEdges;
    Element prevPostOrder;
    private ConnectedComponent cc;
    
    protected Element() {
        this.foreEdges = new LinkedHashSet<Element>();
        this.backEdges = new LinkedHashSet<Element>();
    }
    
    ElementSet lastSet() {
        return this;
    }
    
    boolean isNullable() {
        return false;
    }
    
    boolean isSource() {
        return false;
    }
    
    boolean isSink() {
        return false;
    }
    
    void buildDAG(final ElementSet incoming) {
        incoming.addNext(this);
    }
    
    public void addNext(final Element element) {
        this.foreEdges.add(element);
        element.backEdges.add(this);
    }
    
    public boolean contains(final ElementSet rhs) {
        return this == rhs || rhs == ElementSet.EMPTY_SET;
    }
    
    public Iterator<Element> iterator() {
        return Collections.singleton(this).iterator();
    }
    
    Element assignDfsPostOrder(Element prev) {
        if (this.prevPostOrder != null) {
            return prev;
        }
        this.prevPostOrder = this;
        for (final Element next : this.foreEdges) {
            prev = next.assignDfsPostOrder(prev);
        }
        this.prevPostOrder = prev;
        return this;
    }
    
    public void buildStronglyConnectedComponents(final List<ConnectedComponent> ccs) {
        for (Element cur = this; cur != cur.prevPostOrder; cur = cur.prevPostOrder) {
            if (!cur.belongsToSCC()) {
                final ConnectedComponent cc = new ConnectedComponent();
                ccs.add(cc);
                cur.formConnectedComponent(cc);
            }
        }
    }
    
    private boolean belongsToSCC() {
        return this.cc != null || this.isSource() || this.isSink();
    }
    
    private void formConnectedComponent(final ConnectedComponent group) {
        if (this.belongsToSCC()) {
            return;
        }
        (this.cc = group).add(this);
        for (final Element prev : this.backEdges) {
            prev.formConnectedComponent(group);
        }
    }
    
    public boolean hasSelfLoop() {
        assert this.foreEdges.contains(this) == this.backEdges.contains(this);
        return this.foreEdges.contains(this);
    }
    
    final boolean checkCutSet(final ConnectedComponent cc, final Set<Element> visited) {
        assert this.belongsToSCC();
        if (this.isSink()) {
            return false;
        }
        if (!visited.add(this)) {
            return true;
        }
        if (this.cc == cc) {
            return true;
        }
        for (final Element next : this.foreEdges) {
            if (!next.checkCutSet(cc, visited)) {
                return false;
            }
        }
        return true;
    }
}
