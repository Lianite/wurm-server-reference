// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.reader.Ring;
import com.sun.xml.xsom.visitor.XSVisitor;

abstract class ColorBinder extends BindingComponent implements XSVisitor
{
    protected final BGMBuilder builder;
    protected final ClassSelector selector;
    
    ColorBinder() {
        this.builder = Ring.get(BGMBuilder.class);
        this.selector = this.getClassSelector();
    }
    
    protected final CClassInfo getCurrentBean() {
        return this.selector.getCurrentBean();
    }
    
    protected final XSComponent getCurrentRoot() {
        return this.selector.getCurrentRoot();
    }
    
    protected final void createSimpleTypeProperty(final XSSimpleType type, final String propName) {
        final BIProperty prop = BIProperty.getCustomization(type);
        final SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        final CPropertyInfo p = prop.createValueProperty(propName, false, type, stb.buildDef(type), BGMBuilder.getName(type));
        this.getCurrentBean().addProperty(p);
    }
    
    public final void annotation(final XSAnnotation xsAnnotation) {
        throw new IllegalStateException();
    }
    
    public final void schema(final XSSchema xsSchema) {
        throw new IllegalStateException();
    }
    
    public final void facet(final XSFacet xsFacet) {
        throw new IllegalStateException();
    }
    
    public final void notation(final XSNotation xsNotation) {
        throw new IllegalStateException();
    }
    
    public final void identityConstraint(final XSIdentityConstraint xsIdentityConstraint) {
        throw new IllegalStateException();
    }
    
    public final void xpath(final XSXPath xsxPath) {
        throw new IllegalStateException();
    }
}
