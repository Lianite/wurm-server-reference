// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import org.kohsuke.rngom.digested.DOptionalPattern;
import org.kohsuke.rngom.digested.DZeroOrMorePattern;
import org.kohsuke.rngom.digested.DOneOrMorePattern;
import org.kohsuke.rngom.digested.DMixedPattern;
import org.kohsuke.rngom.digested.DListPattern;
import org.kohsuke.rngom.digested.DAttributePattern;
import org.kohsuke.rngom.digested.DChoicePattern;
import org.kohsuke.rngom.digested.DRefPattern;
import java.util.HashSet;
import org.kohsuke.rngom.digested.DDefine;
import java.util.Set;
import java.util.Stack;
import org.kohsuke.rngom.digested.DPatternWalker;

final class TypePatternBinder extends DPatternWalker
{
    private boolean canInherit;
    private final Stack<Boolean> stack;
    private final Set<DDefine> cannotBeInherited;
    
    TypePatternBinder() {
        this.stack = new Stack<Boolean>();
        this.cannotBeInherited = new HashSet<DDefine>();
    }
    
    void reset() {
        this.canInherit = true;
        this.stack.clear();
    }
    
    public Void onRef(final DRefPattern p) {
        if (!this.canInherit) {
            this.cannotBeInherited.add(p.getTarget());
        }
        else {
            this.canInherit = false;
        }
        return null;
    }
    
    public Void onChoice(final DChoicePattern p) {
        this.push(false);
        super.onChoice(p);
        this.pop();
        return null;
    }
    
    public Void onAttribute(final DAttributePattern p) {
        this.push(false);
        super.onAttribute(p);
        this.pop();
        return null;
    }
    
    public Void onList(final DListPattern p) {
        this.push(false);
        super.onList(p);
        this.pop();
        return null;
    }
    
    public Void onMixed(final DMixedPattern p) {
        this.push(false);
        super.onMixed(p);
        this.pop();
        return null;
    }
    
    public Void onOneOrMore(final DOneOrMorePattern p) {
        this.push(false);
        super.onOneOrMore(p);
        this.pop();
        return null;
    }
    
    public Void onZeroOrMore(final DZeroOrMorePattern p) {
        this.push(false);
        super.onZeroOrMore(p);
        this.pop();
        return null;
    }
    
    public Void onOptional(final DOptionalPattern p) {
        this.push(false);
        super.onOptional(p);
        this.pop();
        return null;
    }
    
    private void push(final boolean v) {
        this.stack.push(this.canInherit);
        this.canInherit = v;
    }
    
    private void pop() {
        this.canInherit = this.stack.pop();
    }
}
