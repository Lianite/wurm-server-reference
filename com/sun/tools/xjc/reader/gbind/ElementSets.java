// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;

public final class ElementSets
{
    public static ElementSet union(final ElementSet lhs, final ElementSet rhs) {
        if (lhs.contains(rhs)) {
            return lhs;
        }
        if (lhs == ElementSet.EMPTY_SET) {
            return rhs;
        }
        if (rhs == ElementSet.EMPTY_SET) {
            return lhs;
        }
        return new MultiValueSet(lhs, rhs);
    }
    
    private static final class MultiValueSet extends LinkedHashSet<Element> implements ElementSet
    {
        public MultiValueSet(final ElementSet lhs, final ElementSet rhs) {
            this.addAll(lhs);
            this.addAll(rhs);
            assert this.size() > 1;
        }
        
        private void addAll(final ElementSet lhs) {
            if (lhs instanceof MultiValueSet) {
                super.addAll((Collection<? extends Element>)lhs);
            }
            else {
                for (final Element e : lhs) {
                    this.add(e);
                }
            }
        }
        
        public boolean contains(final ElementSet rhs) {
            return super.contains(rhs) || rhs == ElementSet.EMPTY_SET;
        }
        
        public void addNext(final Element element) {
            for (final Element e : this) {
                e.addNext(element);
            }
        }
    }
}
