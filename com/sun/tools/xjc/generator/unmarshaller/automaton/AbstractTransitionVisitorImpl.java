// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

public class AbstractTransitionVisitorImpl implements TransitionVisitor
{
    public void onEnterElement(final Alphabet.EnterElement a, final State to) {
        this.onNamed((Alphabet.Named)a, to);
    }
    
    public void onLeaveElement(final Alphabet.LeaveElement a, final State to) {
        this.onNamed((Alphabet.Named)a, to);
    }
    
    public void onEnterAttribute(final Alphabet.EnterAttribute a, final State to) {
        this.onNamed((Alphabet.Named)a, to);
    }
    
    public void onLeaveAttribute(final Alphabet.LeaveAttribute a, final State to) {
        this.onNamed((Alphabet.Named)a, to);
    }
    
    protected void onNamed(final Alphabet.Named a, final State to) {
        this.onAlphabet((Alphabet)a, to);
    }
    
    public void onInterleave(final Alphabet.Interleave a, final State to) {
        this.onRef((Alphabet.Reference)a, to);
    }
    
    public void onChild(final Alphabet.Child a, final State to) {
        this.onRef((Alphabet.Reference)a, to);
    }
    
    public void onDispatch(final Alphabet.Dispatch a, final State to) {
        this.onAlphabet((Alphabet)a, to);
    }
    
    public void onSuper(final Alphabet.SuperClass a, final State to) {
        this.onRef((Alphabet.Reference)a, to);
    }
    
    public void onExternal(final Alphabet.External a, final State to) {
        this.onRef((Alphabet.Reference)a, to);
    }
    
    protected void onRef(final Alphabet.Reference a, final State to) {
        this.onAlphabet((Alphabet)a, to);
    }
    
    public void onBoundText(final Alphabet.BoundText a, final State to) {
        this.onText((Alphabet.Text)a, to);
    }
    
    public void onIgnoredText(final Alphabet.IgnoredText a, final State to) {
        this.onText((Alphabet.Text)a, to);
    }
    
    protected void onText(final Alphabet.Text a, final State to) {
        this.onAlphabet((Alphabet)a, to);
    }
    
    public void onEverythingElse(final Alphabet.EverythingElse a, final State to) {
        this.onAlphabet((Alphabet)a, to);
    }
    
    protected void onAlphabet(final Alphabet a, final State to) {
    }
}
