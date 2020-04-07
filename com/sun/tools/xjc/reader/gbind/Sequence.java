// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

public final class Sequence extends Expression
{
    private final Expression lhs;
    private final Expression rhs;
    private final boolean isNullable;
    private ElementSet lastSet;
    
    public Sequence(final Expression lhs, final Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.isNullable = (lhs.isNullable() && rhs.isNullable());
    }
    
    ElementSet lastSet() {
        if (this.lastSet == null) {
            if (this.rhs.isNullable()) {
                this.lastSet = ElementSets.union(this.lhs.lastSet(), this.rhs.lastSet());
            }
            else {
                this.lastSet = this.rhs.lastSet();
            }
        }
        return this.lastSet;
    }
    
    boolean isNullable() {
        return this.isNullable;
    }
    
    void buildDAG(final ElementSet incoming) {
        this.lhs.buildDAG(incoming);
        if (this.lhs.isNullable()) {
            this.rhs.buildDAG(ElementSets.union(incoming, this.lhs.lastSet()));
        }
        else {
            this.rhs.buildDAG(this.lhs.lastSet());
        }
    }
    
    public String toString() {
        return '(' + this.lhs.toString() + ',' + this.rhs.toString() + ')';
    }
}
