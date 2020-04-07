// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

public interface TransitionVisitor
{
    void onEnterElement(final Alphabet.EnterElement p0, final State p1);
    
    void onLeaveElement(final Alphabet.LeaveElement p0, final State p1);
    
    void onEnterAttribute(final Alphabet.EnterAttribute p0, final State p1);
    
    void onLeaveAttribute(final Alphabet.LeaveAttribute p0, final State p1);
    
    void onInterleave(final Alphabet.Interleave p0, final State p1);
    
    void onChild(final Alphabet.Child p0, final State p1);
    
    void onDispatch(final Alphabet.Dispatch p0, final State p1);
    
    void onSuper(final Alphabet.SuperClass p0, final State p1);
    
    void onExternal(final Alphabet.External p0, final State p1);
    
    void onBoundText(final Alphabet.BoundText p0, final State p1);
    
    void onIgnoredText(final Alphabet.IgnoredText p0, final State p1);
    
    void onEverythingElse(final Alphabet.EverythingElse p0, final State p1);
}
