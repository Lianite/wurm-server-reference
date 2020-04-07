// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSParticle;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.xml.xsom.visitor.XSTermFunction;

public final class MultiplicityCounter implements XSTermFunction<Multiplicity>
{
    public static final MultiplicityCounter theInstance;
    
    public Multiplicity particle(final XSParticle p) {
        final Multiplicity m = p.getTerm().apply((XSTermFunction<Multiplicity>)this);
        Integer max;
        if (m.max == null || p.getMaxOccurs() == -1) {
            max = null;
        }
        else {
            max = p.getMaxOccurs();
        }
        return Multiplicity.multiply(m, Multiplicity.create(p.getMinOccurs(), max));
    }
    
    public Multiplicity wildcard(final XSWildcard wc) {
        return Multiplicity.ONE;
    }
    
    public Multiplicity modelGroupDecl(final XSModelGroupDecl decl) {
        return this.modelGroup(decl.getModelGroup());
    }
    
    public Multiplicity modelGroup(final XSModelGroup group) {
        final boolean isChoice = group.getCompositor() == XSModelGroup.CHOICE;
        Multiplicity r = Multiplicity.ZERO;
        for (final XSParticle p : group.getChildren()) {
            final Multiplicity m = this.particle(p);
            if (r == null) {
                r = m;
            }
            else if (isChoice) {
                r = Multiplicity.choice(r, m);
            }
            else {
                r = Multiplicity.group(r, m);
            }
        }
        return r;
    }
    
    public Multiplicity elementDecl(final XSElementDecl decl) {
        return Multiplicity.ONE;
    }
    
    static {
        theInstance = new MultiplicityCounter();
    }
}
