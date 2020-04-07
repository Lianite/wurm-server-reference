// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.Element;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.codemodel.JType;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnum;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIClass;
import com.sun.xml.xsom.XSComponent;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.model.nav.NClass;

public final class CClassRef extends AbstractCElement implements NClass, CClass
{
    private final String fullyQualifiedClassName;
    private JClass clazz;
    
    public CClassRef(final Model model, final XSComponent source, final BIClass decl, final CCustomizations customizations) {
        super(model, source, decl.getLocation(), customizations);
        this.fullyQualifiedClassName = decl.getExistingClassRef();
        assert this.fullyQualifiedClassName != null;
    }
    
    public CClassRef(final Model model, final XSComponent source, final BIEnum decl, final CCustomizations customizations) {
        super(model, source, decl.getLocation(), customizations);
        this.fullyQualifiedClassName = decl.ref;
        assert this.fullyQualifiedClassName != null;
    }
    
    public void setAbstract() {
    }
    
    public boolean isAbstract() {
        return false;
    }
    
    public NType getType() {
        return this;
    }
    
    public JClass toType(final Outline o, final Aspect aspect) {
        if (this.clazz == null) {
            this.clazz = o.getCodeModel().ref(this.fullyQualifiedClassName);
        }
        return this.clazz;
    }
    
    public String fullName() {
        return this.fullyQualifiedClassName;
    }
    
    public QName getTypeName() {
        return null;
    }
    
    @Deprecated
    public CNonElement getInfo() {
        return this;
    }
    
    public CElement getSubstitutionHead() {
        return null;
    }
    
    public CClassInfo getScope() {
        return null;
    }
    
    public QName getElementName() {
        return null;
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public boolean isSimpleType() {
        return false;
    }
}
