// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DPatternWalker implements DPatternVisitor<Void>
{
    public Void onAttribute(final DAttributePattern p) {
        return this.onXmlToken(p);
    }
    
    protected Void onXmlToken(final DXmlTokenPattern p) {
        return this.onUnary(p);
    }
    
    public Void onChoice(final DChoicePattern p) {
        return this.onContainer(p);
    }
    
    protected Void onContainer(final DContainerPattern p) {
        for (DPattern c = p.firstChild(); c != null; c = c.next) {
            c.accept((DPatternVisitor<Object>)this);
        }
        return null;
    }
    
    public Void onData(final DDataPattern p) {
        return null;
    }
    
    public Void onElement(final DElementPattern p) {
        return this.onXmlToken(p);
    }
    
    public Void onEmpty(final DEmptyPattern p) {
        return null;
    }
    
    public Void onGrammar(final DGrammarPattern p) {
        return p.getStart().accept((DPatternVisitor<Void>)this);
    }
    
    public Void onGroup(final DGroupPattern p) {
        return this.onContainer(p);
    }
    
    public Void onInterleave(final DInterleavePattern p) {
        return this.onContainer(p);
    }
    
    public Void onList(final DListPattern p) {
        return this.onUnary(p);
    }
    
    public Void onMixed(final DMixedPattern p) {
        return this.onUnary(p);
    }
    
    public Void onNotAllowed(final DNotAllowedPattern p) {
        return null;
    }
    
    public Void onOneOrMore(final DOneOrMorePattern p) {
        return this.onUnary(p);
    }
    
    public Void onOptional(final DOptionalPattern p) {
        return this.onUnary(p);
    }
    
    public Void onRef(final DRefPattern p) {
        return p.getTarget().getPattern().accept((DPatternVisitor<Void>)this);
    }
    
    public Void onText(final DTextPattern p) {
        return null;
    }
    
    public Void onValue(final DValuePattern p) {
        return null;
    }
    
    public Void onZeroOrMore(final DZeroOrMorePattern p) {
        return this.onUnary(p);
    }
    
    protected Void onUnary(final DUnaryPattern p) {
        return p.getChild().accept((DPatternVisitor<Void>)this);
    }
}
