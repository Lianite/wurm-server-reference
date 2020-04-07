// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import java.util.ArrayList;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import java.util.Comparator;
import java.util.TreeMap;
import com.sun.tools.xjc.generator.unmarshaller.automaton.OrderComparator;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import java.util.HashMap;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Automaton;
import java.util.Map;

class TransitionTable
{
    private final Map table;
    private static final Entry[] empty;
    
    TransitionTable(final Automaton a) {
        this.table = new HashMap();
        final Iterator itr = a.states();
        while (itr.hasNext()) {
            final State state = itr.next();
            final TreeMap tm = new TreeMap(OrderComparator.theInstance);
            final Iterator jtr = state.transitions();
            while (jtr.hasNext()) {
                final Transition t = jtr.next();
                tm.put(t.alphabet, t);
            }
            final ArrayList r = new ArrayList();
            for (final Map.Entry e : tm.entrySet()) {
                this.buildList(r, e.getKey(), e.getValue());
            }
            if (state.isFinalState()) {
                r.add(new Entry(Alphabet.EverythingElse.theInstance, Transition.REVERT_TO_PARENT, (TransitionTable$1)null));
            }
            final Set alphabetsSeen = new HashSet();
            int i = 0;
            while (i < r.size()) {
                if (!alphabetsSeen.add(r.get(i).alphabet)) {
                    r.remove(i);
                }
                else {
                    ++i;
                }
            }
            this.table.put(state, r.toArray(new Entry[r.size()]));
        }
    }
    
    private void buildList(final ArrayList r, final Alphabet alphabet, final Transition transition) {
        if (alphabet.isReference()) {
            final Iterator itr = alphabet.asReference().head(true).iterator();
            while (itr.hasNext()) {
                this.buildList(r, itr.next(), transition);
            }
        }
        else {
            r.add(new Entry(alphabet, transition, (TransitionTable$1)null));
        }
    }
    
    public Entry[] list(final State s) {
        final Entry[] r = this.table.get(s);
        if (r == null) {
            return TransitionTable.empty;
        }
        return r;
    }
    
    static {
        TransitionTable.empty = new Entry[0];
    }
    
    static class Entry
    {
        final Alphabet alphabet;
        final Transition transition;
        
        private Entry(final Alphabet _alphabet, final Transition _transition) {
            this.alphabet = _alphabet;
            this.transition = _transition;
        }
    }
}
