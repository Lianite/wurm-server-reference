// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public final class Graph implements Iterable<ConnectedComponent>
{
    private final Element source;
    private final Element sink;
    private final List<ConnectedComponent> ccs;
    
    public Graph(final Expression body) {
        this.source = new SourceNode();
        this.sink = new SinkNode();
        this.ccs = new ArrayList<ConnectedComponent>();
        final Expression whole = new Sequence(new Sequence(this.source, body), this.sink);
        whole.buildDAG(ElementSet.EMPTY_SET);
        this.source.assignDfsPostOrder(this.sink);
        this.source.buildStronglyConnectedComponents(this.ccs);
        final Set<Element> visited = new HashSet<Element>();
        for (final ConnectedComponent cc : this.ccs) {
            visited.clear();
            if (this.source.checkCutSet(cc, visited)) {
                cc.isRequired = true;
            }
        }
    }
    
    public Iterator<ConnectedComponent> iterator() {
        return this.ccs.iterator();
    }
    
    public String toString() {
        return this.ccs.toString();
    }
}
