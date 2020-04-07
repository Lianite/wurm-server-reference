// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

public final class OneOrMore extends Expression
{
    private final Expression child;
    
    public OneOrMore(final Expression child) {
        this.child = child;
    }
    
    ElementSet lastSet() {
        return this.child.lastSet();
    }
    
    boolean isNullable() {
        return this.child.isNullable();
    }
    
    void buildDAG(final ElementSet incoming) {
        this.child.buildDAG(ElementSets.union(incoming, this.child.lastSet()));
    }
    
    public String toString() {
        return this.child.toString() + '+';
    }
}
