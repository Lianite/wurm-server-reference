// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

import java.util.Collections;
import java.util.Iterator;

interface ElementSet extends Iterable<Element>
{
    public static final ElementSet EMPTY_SET = new ElementSet() {
        public void addNext(final Element element) {
        }
        
        public boolean contains(final ElementSet element) {
            return this == element;
        }
        
        public Iterator<Element> iterator() {
            return Collections.emptySet().iterator();
        }
    };
    
    void addNext(final Element p0);
    
    boolean contains(final ElementSet p0);
}
