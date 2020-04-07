// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.xml.xsom.XSAttContainer;
import java.util.Collection;
import java.util.Collections;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSComplexType;

final class ChoiceContentComplexTypeBuilder extends CTBuilder
{
    public boolean isApplicable(final XSComplexType ct) {
        if (!this.bgmBuilder.getGlobalBinding().isChoiceContentPropertyEnabled()) {
            return false;
        }
        if (ct.getBaseType() != this.schemas.getAnyType()) {
            return false;
        }
        final XSParticle p = ct.getContentType().asParticle();
        if (p == null) {
            return false;
        }
        final XSModelGroup mg = this.getTopLevelModelGroup(p);
        return mg.getCompositor() == XSModelGroup.CHOICE && !p.isRepeated();
    }
    
    private XSModelGroup getTopLevelModelGroup(final XSParticle p) {
        XSModelGroup mg = p.getTerm().asModelGroup();
        if (p.getTerm().isModelGroupDecl()) {
            mg = p.getTerm().asModelGroupDecl().getModelGroup();
        }
        return mg;
    }
    
    public void build(final XSComplexType ct) {
        final XSParticle p = ct.getContentType().asParticle();
        this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
        this.bgmBuilder.getParticleBinder().build(p, Collections.singleton(p));
        this.green.attContainer(ct);
    }
}
