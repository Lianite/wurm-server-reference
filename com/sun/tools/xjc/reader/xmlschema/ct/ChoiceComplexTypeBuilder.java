// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSComplexType;

public class ChoiceComplexTypeBuilder extends AbstractCTBuilder
{
    public ChoiceComplexTypeBuilder(final ComplexTypeFieldBuilder _builder) {
        super(_builder);
    }
    
    public boolean isApplicable(final XSComplexType ct) {
        if (!this.bgmBuilder.getGlobalBinding().isModelGroupBinding()) {
            return false;
        }
        if (ct.getBaseType() != this.bgmBuilder.schemas.getAnyType()) {
            return false;
        }
        final XSParticle p = ct.getContentType().asParticle();
        if (p == null) {
            return false;
        }
        final XSModelGroup mg = this.getTopLevelModelGroup(p);
        return mg.getCompositor() == XSModelGroup.CHOICE && p.getMaxOccurs() <= 1 && p.getMaxOccurs() != -1;
    }
    
    private XSModelGroup getTopLevelModelGroup(final XSParticle p) {
        XSModelGroup mg = p.getTerm().asModelGroup();
        if (p.getTerm().isModelGroupDecl()) {
            mg = p.getTerm().asModelGroupDecl().getModelGroup();
        }
        return mg;
    }
    
    public Expression build(final XSComplexType ct) {
        final XSModelGroup choice = this.getTopLevelModelGroup(ct.getContentType().asParticle());
        final Expression body = this.bgmBuilder.fieldBuilder.build(choice);
        return this.pool.createSequence(this.bgmBuilder.fieldBuilder.attributeContainer(ct), body);
    }
}
