// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary.visitor;

import org.relaxng.datatype.Datatype;
import org.kohsuke.rngom.nc.NameClass;
import org.kohsuke.rngom.binary.Pattern;

public class PatternWalker implements PatternVisitor
{
    public void visitEmpty() {
    }
    
    public void visitNotAllowed() {
    }
    
    public void visitError() {
    }
    
    public void visitGroup(final Pattern p1, final Pattern p2) {
        this.visitBinary(p1, p2);
    }
    
    protected void visitBinary(final Pattern p1, final Pattern p2) {
        p1.accept(this);
        p2.accept(this);
    }
    
    public void visitInterleave(final Pattern p1, final Pattern p2) {
        this.visitBinary(p1, p2);
    }
    
    public void visitChoice(final Pattern p1, final Pattern p2) {
        this.visitBinary(p1, p2);
    }
    
    public void visitOneOrMore(final Pattern p) {
        p.accept(this);
    }
    
    public void visitElement(final NameClass nc, final Pattern content) {
        content.accept(this);
    }
    
    public void visitAttribute(final NameClass ns, final Pattern value) {
        value.accept(this);
    }
    
    public void visitData(final Datatype dt) {
    }
    
    public void visitDataExcept(final Datatype dt, final Pattern except) {
    }
    
    public void visitValue(final Datatype dt, final Object obj) {
    }
    
    public void visitText() {
    }
    
    public void visitList(final Pattern p) {
        p.accept(this);
    }
    
    public void visitAfter(final Pattern p1, final Pattern p2) {
    }
}
