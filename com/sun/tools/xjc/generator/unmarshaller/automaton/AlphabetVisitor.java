// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller.automaton;

public interface AlphabetVisitor
{
    void onEnterElement(final Alphabet.EnterElement p0);
    
    void onLeaveElement(final Alphabet.LeaveElement p0);
    
    void onEnterAttribute(final Alphabet.EnterAttribute p0);
    
    void onLeaveAttribute(final Alphabet.LeaveAttribute p0);
    
    void onInterleave(final Alphabet.Interleave p0);
    
    void onChild(final Alphabet.Child p0);
    
    void onSuper(final Alphabet.SuperClass p0);
    
    void onDispatch(final Alphabet.Dispatch p0);
    
    void onExternal(final Alphabet.External p0);
    
    void onBoundText(final Alphabet.BoundText p0);
    
    void onIgnoredText(final Alphabet.IgnoredText p0);
    
    void onEverythingElse(final Alphabet.EverythingElse p0);
}
