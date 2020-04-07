// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import java.util.Comparator;

public final class OrderComparator implements Comparator
{
    public static final Comparator theInstance;
    
    public int compare(final Object o1, final Object o2) {
        final Alphabet a1 = (Alphabet)o1;
        final Alphabet a2 = (Alphabet)o2;
        return a2.order - a1.order;
    }
    
    static {
        OrderComparator.theInstance = (Comparator)new OrderComparator();
    }
}
