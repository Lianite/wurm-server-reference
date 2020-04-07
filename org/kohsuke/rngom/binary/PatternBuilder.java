// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

public class PatternBuilder
{
    private final EmptyPattern empty;
    protected final NotAllowedPattern notAllowed;
    protected final PatternInterner interner;
    
    public PatternBuilder() {
        this.empty = new EmptyPattern();
        this.notAllowed = new NotAllowedPattern();
        this.interner = new PatternInterner();
    }
    
    public PatternBuilder(final PatternBuilder parent) {
        this.empty = parent.empty;
        this.notAllowed = parent.notAllowed;
        this.interner = new PatternInterner(parent.interner);
    }
    
    Pattern makeEmpty() {
        return this.empty;
    }
    
    Pattern makeNotAllowed() {
        return this.notAllowed;
    }
    
    Pattern makeGroup(final Pattern p1, final Pattern p2) {
        if (p1 == this.empty) {
            return p2;
        }
        if (p2 == this.empty) {
            return p1;
        }
        if (p1 == this.notAllowed || p2 == this.notAllowed) {
            return this.notAllowed;
        }
        final Pattern p3 = new GroupPattern(p1, p2);
        return this.interner.intern(p3);
    }
    
    Pattern makeInterleave(final Pattern p1, final Pattern p2) {
        if (p1 == this.empty) {
            return p2;
        }
        if (p2 == this.empty) {
            return p1;
        }
        if (p1 == this.notAllowed || p2 == this.notAllowed) {
            return this.notAllowed;
        }
        final Pattern p3 = new InterleavePattern(p1, p2);
        return this.interner.intern(p3);
    }
    
    Pattern makeChoice(final Pattern p1, final Pattern p2) {
        if (p1 == this.empty && p2.isNullable()) {
            return p2;
        }
        if (p2 == this.empty && p1.isNullable()) {
            return p1;
        }
        final Pattern p3 = new ChoicePattern(p1, p2);
        return this.interner.intern(p3);
    }
    
    Pattern makeOneOrMore(final Pattern p) {
        if (p == this.empty || p == this.notAllowed || p instanceof OneOrMorePattern) {
            return p;
        }
        final Pattern p2 = new OneOrMorePattern(p);
        return this.interner.intern(p2);
    }
    
    Pattern makeOptional(final Pattern p) {
        return this.makeChoice(p, this.empty);
    }
    
    Pattern makeZeroOrMore(final Pattern p) {
        return this.makeOptional(this.makeOneOrMore(p));
    }
}
