// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

import com.sun.msv.grammar.ExpressionPool;
import java.util.Iterator;
import com.sun.xml.bind.JAXBAssertionError;
import java.util.HashMap;
import java.util.Map;
import com.sun.tools.xjc.generator.ClassContext;

public final class Automaton
{
    private final ClassContext owner;
    private State initial;
    private Boolean nullable;
    private final Map states;
    private int iota;
    
    public Automaton(final ClassContext _owner) {
        this.nullable = null;
        this.states = new HashMap();
        this.iota = 0;
        this.owner = _owner;
    }
    
    public void setInitialState(final State _initialState) {
        if (this.initial != null) {
            throw new JAXBAssertionError();
        }
        this.initial = _initialState;
        new StateEnumerator(this, (Automaton$1)null).visit(this.initial);
    }
    
    public State getInitialState() {
        return this.initial;
    }
    
    public int getStateNumber(final State s) {
        return this.states.get(s);
    }
    
    public int getStateSize() {
        return this.states.size();
    }
    
    public Iterator states() {
        return this.states.keySet().iterator();
    }
    
    public ClassContext getOwner() {
        return this.owner;
    }
    
    public boolean isNullable() {
        if (this.nullable == null) {
            final ExpressionPool pool = new ExpressionPool();
            if (this.owner.target.exp.getExpandedExp(pool).isEpsilonReducible()) {
                this.nullable = Boolean.TRUE;
            }
            else {
                this.nullable = Boolean.FALSE;
            }
        }
        return this.nullable;
    }
    
    private class StateEnumerator extends AbstractTransitionVisitorImpl
    {
        private StateEnumerator(final Automaton this$0) {
            this.this$0 = this$0;
        }
        
        protected void visit(final State s) {
            if (s == null || this.this$0.states.containsKey(s)) {
                return;
            }
            this.this$0.states.put(s, new Integer(this.this$0.iota++));
            s.acceptForEachTransition((TransitionVisitor)this);
            this.visit(s.getDelegatedState());
        }
        
        protected void onAlphabet(final Alphabet a, final State to) {
            this.visit(to);
        }
        
        public void onInterleave(final Alphabet.Interleave a, final State to) {
            for (int i = 0; i < a.branches.length; ++i) {
                this.visit(a.branches[i].initialState);
            }
            this.visit(to);
        }
    }
}
