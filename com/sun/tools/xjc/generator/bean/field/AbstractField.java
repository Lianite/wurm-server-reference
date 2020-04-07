// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import java.util.ArrayList;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.generator.annotation.spec.XmlAttributeWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementWriter;
import javax.xml.bind.annotation.XmlNsForm;
import java.util.List;
import com.sun.tools.xjc.model.CTypeRef;
import javax.xml.bind.annotation.XmlList;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.model.nav.NClass;
import java.util.Iterator;
import java.util.Collection;
import javax.xml.bind.annotation.W3CDomHandler;
import com.sun.tools.xjc.generator.annotation.spec.XmlAnyElementWriter;
import javax.xml.bind.annotation.XmlMixed;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementRefsWriter;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementRefWriter;
import com.sun.xml.bind.v2.TODO;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import com.sun.tools.xjc.generator.annotation.spec.XmlSchemaTypeWriter;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.codemodel.JAnnotatable;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementsWriter;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.outline.FieldOutline;

abstract class AbstractField implements FieldOutline
{
    protected final ClassOutlineImpl outline;
    protected final CPropertyInfo prop;
    protected final JCodeModel codeModel;
    protected final JType implType;
    protected final JType exposedType;
    private XmlElementsWriter xesw;
    
    protected AbstractField(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        this.xesw = null;
        this.outline = outline;
        this.prop = prop;
        this.codeModel = outline.parent().getCodeModel();
        this.implType = this.getType(Aspect.IMPLEMENTATION);
        this.exposedType = this.getType(Aspect.EXPOSED);
    }
    
    public final ClassOutline parent() {
        return this.outline;
    }
    
    public final CPropertyInfo getPropertyInfo() {
        return this.prop;
    }
    
    protected void annotate(final JAnnotatable field) {
        assert field != null;
        if (this.prop instanceof CAttributePropertyInfo) {
            this.annotateAttribute(field);
        }
        else if (this.prop instanceof CElementPropertyInfo) {
            this.annotateElement(field);
        }
        else if (this.prop instanceof CValuePropertyInfo) {
            field.annotate(XmlValue.class);
        }
        else if (this.prop instanceof CReferencePropertyInfo) {
            this.annotateReference(field);
        }
        this.outline.parent().generateAdapterIfNecessary(this.prop, field);
        final QName st = this.prop.getSchemaType();
        if (st != null) {
            field.annotate2(XmlSchemaTypeWriter.class).name(st.getLocalPart()).namespace(st.getNamespaceURI());
        }
        if (this.prop.inlineBinaryData()) {
            field.annotate(XmlInlineBinaryData.class);
        }
    }
    
    private void annotateReference(final JAnnotatable field) {
        final CReferencePropertyInfo rp = (CReferencePropertyInfo)this.prop;
        TODO.prototype();
        final Collection<CElement> elements = rp.getElements();
        if (elements.size() == 1) {
            final XmlElementRefWriter refw = field.annotate2(XmlElementRefWriter.class);
            final CElement e = elements.iterator().next();
            refw.name(e.getElementName().getLocalPart()).namespace(e.getElementName().getNamespaceURI()).type(((TypeInfo<NType, C>)e).getType().toType(this.outline.parent(), Aspect.IMPLEMENTATION));
        }
        else if (elements.size() > 1) {
            final XmlElementRefsWriter refsw = field.annotate2(XmlElementRefsWriter.class);
            for (final CElement e2 : elements) {
                final XmlElementRefWriter refw = refsw.value();
                refw.name(e2.getElementName().getLocalPart()).namespace(e2.getElementName().getNamespaceURI()).type(((TypeInfo<NType, C>)e2).getType().toType(this.outline.parent(), Aspect.IMPLEMENTATION));
            }
        }
        if (rp.isMixed()) {
            field.annotate(XmlMixed.class);
        }
        final NClass dh = rp.getDOMHandler();
        if (dh != null) {
            final XmlAnyElementWriter xaew = field.annotate2(XmlAnyElementWriter.class);
            xaew.lax(rp.getWildcard().allowTypedObject);
            final JClass value = dh.toType((Outline)this.outline.parent(), Aspect.IMPLEMENTATION);
            if (!value.equals(this.codeModel.ref(W3CDomHandler.class))) {
                xaew.value(value);
            }
        }
    }
    
    private void annotateElement(final JAnnotatable field) {
        final CElementPropertyInfo ep = (CElementPropertyInfo)this.prop;
        final List<CTypeRef> types = ep.getTypes();
        if (ep.isValueList()) {
            field.annotate(XmlList.class);
        }
        assert ep.getXmlName() == null;
        if (types.size() == 1) {
            final CTypeRef t = types.get(0);
            this.writeXmlElementAnnotation(field, t, this.resolve(t, Aspect.IMPLEMENTATION), false);
        }
        else {
            for (final CTypeRef t2 : types) {
                this.writeXmlElementAnnotation(field, t2, this.resolve(t2, Aspect.IMPLEMENTATION), true);
            }
            this.xesw = null;
        }
    }
    
    private void writeXmlElementAnnotation(final JAnnotatable field, final CTypeRef ctype, JType jtype, final boolean checkWrapper) {
        XmlElementWriter xew = null;
        final XmlNsForm formDefault = this.parent()._package().getElementFormDefault();
        final String propName = this.prop.getName(false);
        String enclosingTypeNS;
        if (this.parent().target.getTypeName() == null) {
            enclosingTypeNS = this.parent()._package().getMostUsedNamespaceURI();
        }
        else {
            enclosingTypeNS = this.parent().target.getTypeName().getNamespaceURI();
        }
        final String generatedName = ctype.getTagName().getLocalPart();
        if (!generatedName.equals(propName)) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.name(generatedName);
        }
        final String generatedNS = ctype.getTagName().getNamespaceURI();
        if ((formDefault == XmlNsForm.QUALIFIED && !generatedNS.equals(enclosingTypeNS)) || (formDefault == XmlNsForm.UNQUALIFIED && !generatedNS.equals(""))) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.namespace(generatedNS);
        }
        final CElementPropertyInfo ep = (CElementPropertyInfo)this.prop;
        if (ep.isRequired() && this.exposedType.isReference()) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.required(true);
        }
        if (ep.isRequired() && !this.prop.isCollection()) {
            jtype = jtype.unboxify();
        }
        if (!jtype.equals(this.exposedType) || (this.parent().parent().getModel().options.runtime14 && this.prop.isCollection())) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.type(jtype);
        }
        final String defaultValue = ctype.getDefaultValue();
        if (defaultValue != null) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.defaultValue(defaultValue);
        }
        if (ctype.isNillable()) {
            if (xew == null) {
                xew = this.getXew(checkWrapper, field);
            }
            xew.nillable(true);
        }
    }
    
    private XmlElementWriter getXew(final boolean checkWrapper, final JAnnotatable field) {
        XmlElementWriter xew;
        if (checkWrapper) {
            if (this.xesw == null) {
                this.xesw = field.annotate2(XmlElementsWriter.class);
            }
            xew = this.xesw.value();
        }
        else {
            xew = field.annotate2(XmlElementWriter.class);
        }
        return xew;
    }
    
    private void annotateAttribute(final JAnnotatable field) {
        final CAttributePropertyInfo ap = (CAttributePropertyInfo)this.prop;
        final QName attName = ap.getXmlName();
        final XmlAttributeWriter xaw = field.annotate2(XmlAttributeWriter.class);
        final String generatedName = attName.getLocalPart();
        final String generatedNS = attName.getNamespaceURI();
        if (!generatedName.equals(ap.getName(false))) {
            xaw.name(generatedName);
        }
        if (!generatedNS.equals("")) {
            xaw.namespace(generatedNS);
        }
        if (ap.isRequired()) {
            xaw.required(true);
        }
    }
    
    protected final JFieldVar generateField(final JType type) {
        return this.outline.implClass.field(2, type, this.prop.getName(false));
    }
    
    protected final JExpression castToImplType(final JExpression exp) {
        if (this.implType == this.exposedType) {
            return exp;
        }
        return JExpr.cast(this.implType, exp);
    }
    
    protected JType getType(final Aspect aspect) {
        if (this.prop.getAdapter() != null) {
            return ((NType)this.prop.getAdapter().customType).toType(this.outline.parent(), aspect);
        }
        final class TypeList extends ArrayList<JType>
        {
            void add(final CTypeInfo t) {
                this.add(((TypeInfo<NType, C>)t).getType().toType(AbstractField.this.outline.parent(), aspect));
                if (t instanceof CElementInfo) {
                    this.add(((CElementInfo)t).getSubstitutionMembers());
                }
            }
            
            void add(final Collection<? extends CTypeInfo> col) {
                for (final CTypeInfo typeInfo : col) {
                    this.add(typeInfo);
                }
            }
        }
        final TypeList r = new TypeList();
        r.add(this.prop.ref());
        JType t;
        if (this.prop.baseType != null) {
            t = this.prop.baseType;
        }
        else {
            t = TypeUtil.getCommonBaseType(this.codeModel, r);
        }
        if (this.prop.isUnboxable()) {
            t = t.unboxify();
        }
        return t;
    }
    
    protected final List<Object> listPossibleTypes(final CPropertyInfo prop) {
        final List<Object> r = new ArrayList<Object>();
        for (final CTypeInfo tt : prop.ref()) {
            final JType t = ((TypeInfo<NType, C>)tt).getType().toType(this.outline.parent(), Aspect.EXPOSED);
            if (t.isPrimitive() || t.isArray()) {
                r.add(t.fullName());
            }
            else {
                r.add(t);
                r.add("\n");
            }
        }
        return r;
    }
    
    private JType resolve(final CTypeRef typeRef, final Aspect a) {
        return this.outline.parent().resolve(typeRef, a);
    }
    
    protected abstract class Accessor implements FieldAccessor
    {
        protected final JExpression $target;
        
        protected Accessor(final JExpression $target) {
            this.$target = $target;
        }
        
        public final FieldOutline owner() {
            return AbstractField.this;
        }
        
        public final CPropertyInfo getPropertyInfo() {
            return AbstractField.this.prop;
        }
    }
}
