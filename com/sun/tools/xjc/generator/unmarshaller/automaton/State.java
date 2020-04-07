// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public final class State
{
    public boolean isListState;
    private boolean isFinalState;
    private final Set transitions;
    private State delegatedState;
    
    public State() {
        this.isListState = false;
        this.isFinalState = false;
        this.transitions = new HashSet();
    }
    
    public State getDelegatedState() {
        return this.delegatedState;
    }
    
    public void setDelegatedState(final State _delegatedState) {
        for (State s = _delegatedState; s != null; s = s.delegatedState) {
            if (s == this) {
                this.absorb(_delegatedState);
                return;
            }
        }
        if (this.isFinalState && !_delegatedState.isFinalState) {
            this.absorb(_delegatedState);
            return;
        }
        if (this.delegatedState == null) {
            this.delegatedState = _delegatedState;
            this.isListState |= this.delegatedState.isListState;
        }
        else {
            this.absorb(_delegatedState);
        }
    }
    
    public void addTransition(final Transition t) {
        this.transitions.add(t);
    }
    
    public Iterator transitions() {
        return this.transitions.iterator();
    }
    
    public Transition[] listTransitions() {
        return this.transitions.toArray(new Transition[this.transitions.size()]);
    }
    
    public void acceptForEachTransition(final TransitionVisitor visitor) {
        final Iterator itr = this.transitions.iterator();
        while (itr.hasNext()) {
            itr.next().accept(visitor);
        }
    }
    
    public void absorb(final State rhs) {
        this.transitions.addAll(rhs.transitions);
        this.isListState |= rhs.isListState;
        if (rhs.isFinalState) {
            this.markAsFinalState();
        }
        if (rhs.delegatedState != null) {
            this.setDelegatedState(rhs.delegatedState);
        }
    }
    
    public Set head() {
        final HashSet s = new HashSet();
        this.head(s, new HashSet(), true);
        return s;
    }
    
    void head(final Set result, final Set visitedStates, final boolean includeEE) {
        if (!visitedStates.add(this)) {
            return;
        }
        if (this.isFinalState && includeEE) {
            result.add(Alphabet.EverythingElse.theInstance);
        }
        for (final Transition t : this.transitions) {
            t.head(result, visitedStates, includeEE);
        }
        if (this.delegatedState != null) {
            this.delegatedState.head(result, visitedStates, includeEE);
        }
    }
    
    public boolean hasTransition() {
        return !this.transitions.isEmpty();
    }
    
    public boolean isFinalState() {
        return this.isFinalState;
    }
    
    public void markAsFinalState() {
        this.isFinalState = true;
        if (this.delegatedState != null && !this.delegatedState.isFinalState) {
            final State p = this.delegatedState;
            this.delegatedState = null;
            this.absorb(p);
        }
    }
}
