// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.tools.xjc.reader.gbind.Element;
import java.util.Iterator;
import com.sun.tools.xjc.reader.gbind.Expression;
import com.sun.tools.xjc.reader.gbind.ConnectedComponent;
import com.sun.tools.xjc.reader.gbind.Graph;
import java.util.Collection;
import com.sun.xml.xsom.XSParticle;

final class ExpressionParticleBinder extends ParticleBinder
{
    public void build(final XSParticle p, final Collection<XSParticle> forcedProps) {
        final Expression tree = ExpressionBuilder.createTree(p);
        final Graph g = new Graph(tree);
        for (final ConnectedComponent cc : g) {
            this.buildProperty(cc);
        }
    }
    
    private void buildProperty(final ConnectedComponent cc) {
        final StringBuilder propName = new StringBuilder();
        int nameTokenCount = 0;
        final RawTypeSetBuilder rtsb = new RawTypeSetBuilder();
        for (final Element e : cc) {
            final GElement ge = (GElement)e;
            if (nameTokenCount < 3) {
                if (nameTokenCount != 0) {
                    propName.append("And");
                }
                propName.append(this.makeJavaName(cc.isCollection(), ge.getPropertyNameSeed()));
                ++nameTokenCount;
            }
            if (e instanceof GElementImpl) {
                final GElementImpl ei = (GElementImpl)e;
                rtsb.elementDecl(ei.decl);
            }
            else if (e instanceof GWildcardElement) {
                final GWildcardElement w = (GWildcardElement)e;
                rtsb.getRefs().add(new RawTypeSetBuilder.WildcardRef(w.isStrict() ? WildcardMode.STRICT : WildcardMode.SKIP));
            }
            else {
                assert false : e;
                continue;
            }
        }
        Multiplicity m = Multiplicity.ONE;
        if (cc.isCollection()) {
            m = m.makeRepeated();
        }
        if (!cc.isRequired()) {
            m = m.makeOptional();
        }
        final RawTypeSet rts = new RawTypeSet(rtsb.getRefs(), m);
        final XSParticle p = this.findSourceParticle(cc);
        final BIProperty cust = BIProperty.getCustomization(p);
        final CPropertyInfo prop = cust.createElementOrReferenceProperty(propName.toString(), false, p, rts);
        this.getCurrentBean().addProperty(prop);
    }
    
    private XSParticle findSourceParticle(final ConnectedComponent cc) {
        XSParticle first = null;
        for (final Element e : cc) {
            final GElement ge = (GElement)e;
            for (final XSParticle p : ge.particles) {
                if (first == null) {
                    first = p;
                }
                if (this.getLocalPropCustomization(p) != null) {
                    return p;
                }
            }
        }
        return first;
    }
    
    public boolean checkFallback(final XSParticle p) {
        return false;
    }
}
