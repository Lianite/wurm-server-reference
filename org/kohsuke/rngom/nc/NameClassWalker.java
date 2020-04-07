// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public class NameClassWalker implements NameClassVisitor<Void>
{
    public Void visitChoice(final NameClass nc1, final NameClass nc2) {
        nc1.accept((NameClassVisitor<Object>)this);
        return nc2.accept((NameClassVisitor<Void>)this);
    }
    
    public Void visitNsName(final String ns) {
        return null;
    }
    
    public Void visitNsNameExcept(final String ns, final NameClass nc) {
        return nc.accept((NameClassVisitor<Void>)this);
    }
    
    public Void visitAnyName() {
        return null;
    }
    
    public Void visitAnyNameExcept(final NameClass nc) {
        return nc.accept((NameClassVisitor<Void>)this);
    }
    
    public Void visitName(final QName name) {
        return null;
    }
    
    public Void visitNull() {
        return null;
    }
}
