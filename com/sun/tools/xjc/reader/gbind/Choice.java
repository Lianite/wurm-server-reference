// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

public final class Choice extends Expression
{
    private final Expression lhs;
    private final Expression rhs;
    private final boolean isNullable;
    
    public Choice(final Expression lhs, final Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.isNullable = (lhs.isNullable() || rhs.isNullable());
    }
    
    boolean isNullable() {
        return this.isNullable;
    }
    
    ElementSet lastSet() {
        return ElementSets.union(this.lhs.lastSet(), this.rhs.lastSet());
    }
    
    void buildDAG(final ElementSet incoming) {
        this.lhs.buildDAG(incoming);
        this.rhs.buildDAG(incoming);
    }
    
    public String toString() {
        return '(' + this.lhs.toString() + '|' + this.rhs.toString() + ')';
    }
}
