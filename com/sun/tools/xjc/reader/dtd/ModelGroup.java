// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

final class ModelGroup extends Term
{
    Kind kind;
    private final List<Term> terms;
    
    ModelGroup() {
        this.terms = new ArrayList<Term>();
    }
    
    void normalize(final List<Block> r, final boolean optional) {
        switch (this.kind) {
            case SEQUENCE: {
                for (final Term t : this.terms) {
                    t.normalize(r, optional);
                }
            }
            case CHOICE: {
                final Block b = new Block(this.isOptional() || optional, this.isRepeated());
                this.addAllElements(b);
                r.add(b);
            }
            default: {}
        }
    }
    
    void addAllElements(final Block b) {
        for (final Term t : this.terms) {
            t.addAllElements(b);
        }
    }
    
    boolean isOptional() {
        switch (this.kind) {
            case SEQUENCE: {
                for (final Term t : this.terms) {
                    if (!t.isOptional()) {
                        return false;
                    }
                }
                return true;
            }
            case CHOICE: {
                for (final Term t : this.terms) {
                    if (t.isOptional()) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    boolean isRepeated() {
        switch (this.kind) {
            case SEQUENCE: {
                return true;
            }
            case CHOICE: {
                for (final Term t : this.terms) {
                    if (t.isRepeated()) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    void setKind(final short connectorType) {
        Kind k = null;
        switch (connectorType) {
            case 1: {
                k = Kind.SEQUENCE;
                break;
            }
            case 0: {
                k = Kind.CHOICE;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        assert k == this.kind;
        this.kind = k;
    }
    
    void addTerm(final Term t) {
        if (t instanceof ModelGroup) {
            final ModelGroup mg = (ModelGroup)t;
            if (mg.kind == this.kind) {
                this.terms.addAll(mg.terms);
                return;
            }
        }
        this.terms.add(t);
    }
    
    Term wrapUp() {
        switch (this.terms.size()) {
            case 0: {
                return ModelGroup.EMPTY;
            }
            case 1: {
                assert this.kind == null;
                return this.terms.get(0);
            }
            default: {
                assert this.kind != null;
                return this;
            }
        }
    }
    
    enum Kind
    {
        CHOICE, 
        SEQUENCE;
    }
}
