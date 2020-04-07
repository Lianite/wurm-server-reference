// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public interface DPatternVisitor<V>
{
    V onAttribute(final DAttributePattern p0);
    
    V onChoice(final DChoicePattern p0);
    
    V onData(final DDataPattern p0);
    
    V onElement(final DElementPattern p0);
    
    V onEmpty(final DEmptyPattern p0);
    
    V onGrammar(final DGrammarPattern p0);
    
    V onGroup(final DGroupPattern p0);
    
    V onInterleave(final DInterleavePattern p0);
    
    V onList(final DListPattern p0);
    
    V onMixed(final DMixedPattern p0);
    
    V onNotAllowed(final DNotAllowedPattern p0);
    
    V onOneOrMore(final DOneOrMorePattern p0);
    
    V onOptional(final DOptionalPattern p0);
    
    V onRef(final DRefPattern p0);
    
    V onText(final DTextPattern p0);
    
    V onValue(final DValuePattern p0);
    
    V onZeroOrMore(final DZeroOrMorePattern p0);
}
