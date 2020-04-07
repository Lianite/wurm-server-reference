// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.outline.FieldOutline;
import java.util.Iterator;
import java.util.Collection;
import javax.xml.bind.JAXBException;
import com.sun.tools.xjc.model.Constructor;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JAnnotatable;
import com.sun.tools.xjc.model.CPropertyInfo;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementDeclWriter;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.xml.bind.v2.TODO;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.generator.annotation.spec.XmlRegistryWriter;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CElementInfo;
import java.util.Map;
import com.sun.codemodel.JFieldVar;
import javax.xml.namespace.QName;
import java.util.HashMap;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.Model;

abstract class ObjectFactoryGeneratorImpl extends ObjectFactoryGenerator
{
    private final BeanGenerator outline;
    private final Model model;
    private final JCodeModel codeModel;
    private final JClass classRef;
    private final JDefinedClass objectFactory;
    private final HashMap<QName, JFieldVar> qnameMap;
    private final Map<String, CElementInfo> elementFactoryNames;
    private final Map<String, ClassOutlineImpl> valueFactoryNames;
    
    public JDefinedClass getObjectFactory() {
        return this.objectFactory;
    }
    
    public ObjectFactoryGeneratorImpl(final BeanGenerator outline, final Model model, final JPackage targetPackage) {
        this.qnameMap = new HashMap<QName, JFieldVar>();
        this.elementFactoryNames = new HashMap<String, CElementInfo>();
        this.valueFactoryNames = new HashMap<String, ClassOutlineImpl>();
        this.outline = outline;
        this.model = model;
        this.codeModel = this.model.codeModel;
        this.classRef = this.codeModel.ref(Class.class);
        (this.objectFactory = this.outline.getClassFactory().createClass(targetPackage, "ObjectFactory", null)).annotate2(XmlRegistryWriter.class);
        final JMethod m1 = this.objectFactory.constructor(1);
        m1.javadoc().append("Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: " + targetPackage.name());
        this.objectFactory.javadoc().append("This object contains factory methods for each \nJava content interface and Java element interface \ngenerated in the " + targetPackage.name() + " package. \n" + "<p>An ObjectFactory allows you to programatically \n" + "construct new instances of the Java representation \n" + "for XML content. The Java representation of XML \n" + "content can consist of schema derived interfaces \n" + "and classes representing the binding of schema \n" + "type definitions, element declarations and model \n" + "groups.  Factory methods for each of these are \n" + "provided in this class.");
    }
    
    protected final void populate(final CElementInfo ei, final Aspect impl, final Aspect exposed) {
        final JType exposedElementType = ei.toType(this.outline, exposed);
        final JType exposedType = ei.getContentInMemoryType().toType(this.outline, exposed);
        final JType implType = ei.getContentInMemoryType().toType(this.outline, impl);
        final String namespaceURI = ei.getElementName().getNamespaceURI();
        final String localPart = ei.getElementName().getLocalPart();
        JClass scope = null;
        if (ei.getScope() != null) {
            scope = this.outline.getClazz(ei.getScope()).implClass;
        }
        if (ei.isAbstract()) {
            TODO.checkSpec();
        }
        final CElementInfo existing = this.elementFactoryNames.put(ei.getSqueezedName(), ei);
        if (existing != null) {
            this.outline.getErrorReceiver().error(existing.getLocator(), Messages.OBJECT_FACTORY_CONFLICT.format(ei.getSqueezedName()));
            this.outline.getErrorReceiver().error(ei.getLocator(), Messages.OBJECT_FACTORY_CONFLICT_RELATED.format(new Object[0]));
            return;
        }
        final JMethod m = this.objectFactory.method(1, exposedElementType, "create" + ei.getSqueezedName());
        final JVar $value = m.param(exposedType, "value");
        JExpression declaredType;
        if (implType.boxify().isParameterized() || !exposedType.equals(implType)) {
            declaredType = JExpr.cast(this.classRef, implType.boxify().dotclass());
        }
        else {
            declaredType = implType.boxify().dotclass();
        }
        final JExpression scopeClass = (scope == null) ? JExpr._null() : scope.dotclass();
        final JInvocation exp = JExpr._new(exposedElementType);
        if (!ei.hasClass()) {
            exp.arg(this.getQNameInvocation(ei));
            exp.arg(declaredType);
            exp.arg(scopeClass);
        }
        if (implType == exposedType) {
            exp.arg($value);
        }
        else {
            exp.arg(JExpr.cast(implType, $value));
        }
        m.body()._return(exp);
        m.javadoc().append("Create an instance of ").append(exposedElementType).append("}");
        final XmlElementDeclWriter xemw = m.annotate2(XmlElementDeclWriter.class);
        xemw.namespace(namespaceURI).name(localPart);
        if (scope != null) {
            xemw.scope(scope);
        }
        if (ei.getSubstitutionHead() != null) {
            final QName n = ei.getSubstitutionHead().getElementName();
            xemw.substitutionHeadNamespace(n.getNamespaceURI());
            xemw.substitutionHeadName(n.getLocalPart());
        }
        if (ei.getDefaultValue() != null) {
            xemw.defaultValue(ei.getDefaultValue());
        }
        if (ei.getProperty().inlineBinaryData()) {
            m.annotate(XmlInlineBinaryData.class);
        }
        this.outline.generateAdapterIfNecessary(ei.getProperty(), m);
    }
    
    private JExpression getQNameInvocation(final CElementInfo ei) {
        final QName name = ei.getElementName();
        if (this.qnameMap.containsKey(name)) {
            return this.qnameMap.get(name);
        }
        if (this.qnameMap.size() > 1024) {
            return this.createQName(name);
        }
        final JFieldVar qnameField = this.objectFactory.field(28, QName.class, '_' + ei.getSqueezedName() + "_QNAME", this.createQName(name));
        this.qnameMap.put(name, qnameField);
        return qnameField;
    }
    
    private JInvocation createQName(final QName name) {
        return JExpr._new(this.codeModel.ref(QName.class)).arg(name.getNamespaceURI()).arg(name.getLocalPart());
    }
    
    protected final void populate(final ClassOutlineImpl cc, final JClass sigType) {
        if (!cc.target.isAbstract()) {
            final JMethod m = this.objectFactory.method(1, sigType, "create" + cc.target.getSqueezedName());
            m.body()._return(JExpr._new(cc.implRef));
            m.javadoc().append("Create an instance of ").append(cc.ref);
        }
        final Collection<? extends Constructor> consl = cc.target.getConstructors();
        if (consl.size() != 0) {
            cc.implClass.constructor(1);
        }
        final String name = cc.target.getSqueezedName();
        final ClassOutlineImpl existing = this.valueFactoryNames.put(name, cc);
        if (existing != null) {
            this.outline.getErrorReceiver().error(existing.target.getLocator(), Messages.OBJECT_FACTORY_CONFLICT.format(name));
            this.outline.getErrorReceiver().error(cc.target.getLocator(), Messages.OBJECT_FACTORY_CONFLICT_RELATED.format(new Object[0]));
            return;
        }
        for (final Constructor cons : consl) {
            final JMethod i = this.objectFactory.method(1, cc.ref, "create" + cc.target.getSqueezedName());
            final JInvocation inv = JExpr._new(cc.implRef);
            i.body()._return(inv);
            i.javadoc().append("Create an instance of ").append(cc.ref).addThrows(JAXBException.class).append("if an error occurs");
            final JMethod c = cc.implClass.constructor(1);
            for (String fieldName : cons.fields) {
                final CPropertyInfo field = cc.target.getProperty(fieldName);
                if (field == null) {
                    this.outline.getErrorReceiver().error(cc.target.getLocator(), Messages.ILLEGAL_CONSTRUCTOR_PARAM.format(fieldName));
                }
                else {
                    fieldName = camelize(fieldName);
                    final FieldOutline fo = this.outline.getField(field);
                    final FieldAccessor accessor = fo.create(JExpr._this());
                    inv.arg(i.param(fo.getRawType(), fieldName));
                    final JVar $var = c.param(fo.getRawType(), fieldName);
                    accessor.fromRawValue(c.body(), '_' + fieldName, $var);
                }
            }
        }
    }
    
    private static String camelize(final String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
