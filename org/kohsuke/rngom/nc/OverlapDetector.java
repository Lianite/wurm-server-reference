// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

class OverlapDetector implements NameClassVisitor<Void>
{
    private NameClass nc1;
    private NameClass nc2;
    private boolean overlaps;
    static final String IMPOSSIBLE = "\u0000";
    
    private OverlapDetector(final NameClass nc1, final NameClass nc2) {
        this.overlaps = false;
        this.nc1 = nc1;
        this.nc2 = nc2;
        nc1.accept((NameClassVisitor<Object>)this);
        nc2.accept((NameClassVisitor<Object>)this);
    }
    
    private void probe(final QName name) {
        if (this.nc1.contains(name) && this.nc2.contains(name)) {
            this.overlaps = true;
        }
    }
    
    public Void visitChoice(final NameClass nc1, final NameClass nc2) {
        nc1.accept((NameClassVisitor<Object>)this);
        nc2.accept((NameClassVisitor<Object>)this);
        return null;
    }
    
    public Void visitNsName(final String ns) {
        this.probe(new QName(ns, "\u0000"));
        return null;
    }
    
    public Void visitNsNameExcept(final String ns, final NameClass ex) {
        this.probe(new QName(ns, "\u0000"));
        ex.accept((NameClassVisitor<Object>)this);
        return null;
    }
    
    public Void visitAnyName() {
        this.probe(new QName("\u0000", "\u0000"));
        return null;
    }
    
    public Void visitAnyNameExcept(final NameClass ex) {
        this.probe(new QName("\u0000", "\u0000"));
        ex.accept((NameClassVisitor<Object>)this);
        return null;
    }
    
    public Void visitName(final QName name) {
        this.probe(name);
        return null;
    }
    
    public Void visitNull() {
        return null;
    }
    
    static boolean overlap(final NameClass nc1, final NameClass nc2) {
        if (nc2 instanceof SimpleNameClass) {
            final SimpleNameClass snc = (SimpleNameClass)nc2;
            return nc1.contains(snc.name);
        }
        if (nc1 instanceof SimpleNameClass) {
            final SimpleNameClass snc = (SimpleNameClass)nc1;
            return nc2.contains(snc.name);
        }
        return new OverlapDetector(nc1, nc2).overlaps;
    }
}
