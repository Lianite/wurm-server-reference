// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import java.util.HashSet;
import java.util.Set;

public final class Transition
{
    public static final Transition REVERT_TO_PARENT;
    public final Alphabet alphabet;
    public final State to;
    
    public Transition(final Alphabet _alphabet, final State _to) {
        this.alphabet = _alphabet;
        this.to = _to;
    }
    
    public Set head(final State sourceState) {
        final HashSet s = new HashSet();
        final HashSet visited = new HashSet();
        visited.add(sourceState);
        this.head(s, visited, true);
        return s;
    }
    
    void head(final Set result, final Set visitedStates, final boolean includeEE) {
        result.add(this.alphabet);
        if (!(this.alphabet instanceof Alphabet.Reference)) {
            return;
        }
        final Alphabet.Reference ref = this.alphabet.asReference();
        if (ref.isNullable()) {
            this.to.head(result, visitedStates, includeEE);
        }
    }
    
    public void accept(final TransitionVisitor visitor) {
        this.alphabet.accept(visitor, this);
    }
    
    static {
        Transition.REVERT_TO_PARENT = new Transition((Alphabet)null, (State)null);
    }
}
