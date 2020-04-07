// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary.visitor;

import org.relaxng.datatype.Datatype;
import org.kohsuke.rngom.nc.NameClass;
import org.kohsuke.rngom.binary.Pattern;

public interface PatternVisitor
{
    void visitEmpty();
    
    void visitNotAllowed();
    
    void visitError();
    
    void visitAfter(final Pattern p0, final Pattern p1);
    
    void visitGroup(final Pattern p0, final Pattern p1);
    
    void visitInterleave(final Pattern p0, final Pattern p1);
    
    void visitChoice(final Pattern p0, final Pattern p1);
    
    void visitOneOrMore(final Pattern p0);
    
    void visitElement(final NameClass p0, final Pattern p1);
    
    void visitAttribute(final NameClass p0, final Pattern p1);
    
    void visitData(final Datatype p0);
    
    void visitDataExcept(final Datatype p0, final Pattern p1);
    
    void visitValue(final Datatype p0, final Object p1);
    
    void visitText();
    
    void visitList(final Pattern p0);
}
