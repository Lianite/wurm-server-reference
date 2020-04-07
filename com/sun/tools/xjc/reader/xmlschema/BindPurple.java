// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.tools.xjc.model.CClass;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.CDefaultValue;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;

public class BindPurple extends ColorBinder
{
    public void attGroupDecl(final XSAttGroupDecl xsAttGroupDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void attributeDecl(final XSAttributeDecl xsAttributeDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void attributeUse(final XSAttributeUse use) {
        final boolean hasFixedValue = use.getFixedValue() != null;
        final BIProperty pc = BIProperty.getCustomization(use);
        final boolean toConstant = pc.isConstantProperty() && hasFixedValue;
        final TypeUse attType = this.bindAttDecl(use.getDecl());
        final CPropertyInfo prop = pc.createAttributeProperty(use, attType);
        if (toConstant) {
            prop.defaultValue = CDefaultValue.create(attType, use.getFixedValue());
            prop.realization = this.builder.fieldRendererFactory.getConst(prop.realization);
        }
        else if (!attType.isCollection()) {
            if (use.getDefaultValue() != null) {
                prop.defaultValue = CDefaultValue.create(attType, use.getDefaultValue());
            }
            else if (use.getFixedValue() != null) {
                prop.defaultValue = CDefaultValue.create(attType, use.getFixedValue());
            }
        }
        this.getCurrentBean().addProperty(prop);
    }
    
    private TypeUse bindAttDecl(final XSAttributeDecl decl) {
        final SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        stb.refererStack.push(decl);
        try {
            return stb.build(decl.getType());
        }
        finally {
            stb.refererStack.pop();
        }
    }
    
    public void complexType(final XSComplexType ct) {
        final CClass ctBean = this.selector.bindToType(ct, null, false);
        if (this.getCurrentBean() != ctBean) {
            this.getCurrentBean().setBaseClass(ctBean);
        }
    }
    
    public void wildcard(final XSWildcard xsWildcard) {
        this.getCurrentBean().hasAttributeWildcard(true);
    }
    
    public void modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void modelGroup(final XSModelGroup xsModelGroup) {
        throw new UnsupportedOperationException();
    }
    
    public void elementDecl(final XSElementDecl xsElementDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void simpleType(final XSSimpleType type) {
        this.createSimpleTypeProperty(type, "Value");
    }
    
    public void particle(final XSParticle xsParticle) {
        throw new UnsupportedOperationException();
    }
    
    public void empty(final XSContentType ct) {
    }
}
