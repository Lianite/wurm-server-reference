// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.ElementOutline;
import java.net.URL;
import com.sun.codemodel.JResourceFile;
import com.sun.codemodel.fmt.JStaticJavaFile;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.generator.annotation.spec.XmlMimeTypeWriter;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlID;
import com.sun.tools.xjc.generator.annotation.spec.XmlJavaTypeAdapterWriter;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttachmentRef;
import com.sun.xml.bind.v2.runtime.SwaRefAdapter;
import com.sun.codemodel.JAnnotatable;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JEnumConstant;
import com.sun.tools.xjc.outline.EnumConstantOutline;
import com.sun.tools.xjc.generator.annotation.spec.XmlEnumValueWriter;
import com.sun.xml.xsom.XmlString;
import com.sun.codemodel.JJavaName;
import com.sun.tools.xjc.model.CEnumConstant;
import java.util.HashSet;
import com.sun.tools.xjc.generator.annotation.spec.XmlEnumWriter;
import com.sun.istack.NotNull;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.generator.annotation.spec.XmlAnyAttributeWriter;
import javax.xml.namespace.QName;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.generator.annotation.spec.XmlRootElementWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlSeeAlsoWriter;
import com.sun.tools.xjc.api.SpecVersion;
import com.sun.tools.xjc.generator.annotation.spec.XmlTypeWriter;
import com.sun.codemodel.JExpr;
import java.io.Serializable;
import com.sun.tools.xjc.outline.PackageOutline;
import java.util.Collection;
import java.util.Set;
import com.sun.tools.xjc.model.nav.NClass;
import java.util.TreeSet;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JExpression;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import com.sun.tools.xjc.model.CClassRef;
import java.util.Iterator;
import com.sun.tools.xjc.outline.Aspect;
import java.util.HashMap;
import com.sun.tools.xjc.AbortException;
import com.sun.codemodel.JClassContainer;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.Model;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.codemodel.JPackage;
import java.util.Map;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.outline.Outline;

public final class BeanGenerator implements Outline
{
    private final CodeModelClassFactory codeModelClassFactory;
    private final ErrorReceiver errorReceiver;
    private final Map<JPackage, PackageOutlineImpl> packageContexts;
    private final Map<CClassInfo, ClassOutlineImpl> classes;
    private final Map<CEnumLeafInfo, EnumOutline> enums;
    private final Map<Class, JClass> generatedRuntime;
    private final Model model;
    private final JCodeModel codeModel;
    private final Map<CPropertyInfo, FieldOutline> fields;
    final Map<CElementInfo, ElementOutlineImpl> elements;
    private final CClassInfoParent.Visitor<JClassContainer> exposedContainerBuilder;
    private final CClassInfoParent.Visitor<JClassContainer> implContainerBuilder;
    
    public static Outline generate(final Model model, final ErrorReceiver _errorReceiver) {
        try {
            return new BeanGenerator(model, _errorReceiver);
        }
        catch (AbortException e) {
            return null;
        }
    }
    
    private BeanGenerator(final Model _model, final ErrorReceiver _errorReceiver) {
        this.packageContexts = new HashMap<JPackage, PackageOutlineImpl>();
        this.classes = new HashMap<CClassInfo, ClassOutlineImpl>();
        this.enums = new HashMap<CEnumLeafInfo, EnumOutline>();
        this.generatedRuntime = new HashMap<Class, JClass>();
        this.fields = new HashMap<CPropertyInfo, FieldOutline>();
        this.elements = new HashMap<CElementInfo, ElementOutlineImpl>();
        this.exposedContainerBuilder = new CClassInfoParent.Visitor<JClassContainer>() {
            public JClassContainer onBean(final CClassInfo bean) {
                return BeanGenerator.this.getClazz(bean).ref;
            }
            
            public JClassContainer onElement(final CElementInfo element) {
                return BeanGenerator.this.getElement(element).implClass;
            }
            
            public JClassContainer onPackage(final JPackage pkg) {
                return BeanGenerator.this.model.strategy.getPackage(pkg, Aspect.EXPOSED);
            }
        };
        this.implContainerBuilder = new CClassInfoParent.Visitor<JClassContainer>() {
            public JClassContainer onBean(final CClassInfo bean) {
                return BeanGenerator.this.getClazz(bean).implClass;
            }
            
            public JClassContainer onElement(final CElementInfo element) {
                return BeanGenerator.this.getElement(element).implClass;
            }
            
            public JClassContainer onPackage(final JPackage pkg) {
                return BeanGenerator.this.model.strategy.getPackage(pkg, Aspect.IMPLEMENTATION);
            }
        };
        this.model = _model;
        this.codeModel = this.model.codeModel;
        this.errorReceiver = _errorReceiver;
        this.codeModelClassFactory = new CodeModelClassFactory(this.errorReceiver);
        for (final CEnumLeafInfo p : this.model.enums().values()) {
            this.enums.put(p, this.generateEnumDef(p));
        }
        final JPackage[] arr$;
        final JPackage[] packages = arr$ = this.getUsedPackages(Aspect.EXPOSED);
        for (final JPackage pkg : arr$) {
            this.getPackageContext(pkg);
        }
        for (final CClassInfo bean : this.model.beans().values()) {
            this.getClazz(bean);
        }
        for (final PackageOutlineImpl p2 : this.packageContexts.values()) {
            p2.calcDefaultValues();
        }
        final JClass OBJECT = this.codeModel.ref(Object.class);
        for (final ClassOutlineImpl cc : this.getClasses()) {
            final CClassInfo superClass = cc.target.getBaseClass();
            if (superClass != null) {
                this.model.strategy._extends(cc, this.getClazz(superClass));
            }
            else {
                final CClassRef refSuperClass = cc.target.getRefBaseClass();
                if (refSuperClass != null) {
                    cc.implClass._extends(refSuperClass.toType(this, Aspect.EXPOSED));
                }
                else {
                    if (this.model.rootClass != null && cc.implClass._extends().equals(OBJECT)) {
                        cc.implClass._extends(this.model.rootClass);
                    }
                    if (this.model.rootInterface == null) {
                        continue;
                    }
                    cc.ref._implements(this.model.rootInterface);
                }
            }
        }
        for (final ClassOutlineImpl co : this.getClasses()) {
            this.generateClassBody(co);
        }
        for (final EnumOutline eo : this.enums.values()) {
            this.generateEnumBody(eo);
        }
        for (final CElementInfo ei : this.model.getAllElements()) {
            this.getPackageContext(ei._package()).objectFactoryGenerator().populate(ei);
        }
        if (this.model.options.debugMode) {
            this.generateClassList();
        }
    }
    
    private void generateClassList() {
        try {
            final JDefinedClass jc = this.codeModel.rootPackage()._class("JAXBDebug");
            final JMethod m = jc.method(17, JAXBContext.class, "createContext");
            final JVar $classLoader = m.param(ClassLoader.class, "classLoader");
            m._throws(JAXBException.class);
            final JInvocation inv = this.codeModel.ref(JAXBContext.class).staticInvoke("newInstance");
            m.body()._return(inv);
            switch (this.model.strategy) {
                case INTF_AND_IMPL: {
                    final StringBuilder buf = new StringBuilder();
                    for (final PackageOutlineImpl po : this.packageContexts.values()) {
                        if (buf.length() > 0) {
                            buf.append(':');
                        }
                        buf.append(po._package().name());
                    }
                    inv.arg(buf.toString()).arg($classLoader);
                    break;
                }
                case BEAN_ONLY: {
                    for (final ClassOutlineImpl cc : this.getClasses()) {
                        inv.arg(cc.implRef.dotclass());
                    }
                    for (final PackageOutlineImpl po2 : this.packageContexts.values()) {
                        inv.arg(po2.objectFactory().dotclass());
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
    
    public Model getModel() {
        return this.model;
    }
    
    public JCodeModel getCodeModel() {
        return this.codeModel;
    }
    
    public JClassContainer getContainer(final CClassInfoParent parent, final Aspect aspect) {
        CClassInfoParent.Visitor<JClassContainer> v = null;
        switch (aspect) {
            case EXPOSED: {
                v = this.exposedContainerBuilder;
                break;
            }
            case IMPLEMENTATION: {
                v = this.implContainerBuilder;
                break;
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
        return parent.accept(v);
    }
    
    public final JType resolve(final CTypeRef ref, final Aspect a) {
        return ((TypeInfo<NType, C>)ref.getTarget()).getType().toType(this, a);
    }
    
    public final JPackage[] getUsedPackages(final Aspect aspect) {
        final Set<JPackage> s = new TreeSet<JPackage>();
        for (final CClassInfo bean : this.model.beans().values()) {
            final JClassContainer cont = this.getContainer(bean.parent(), aspect);
            if (cont.isPackage()) {
                s.add((JPackage)cont);
            }
        }
        for (final CElementInfo e : this.model.getElementMappings((NClass)null).values()) {
            s.add(e._package());
        }
        return s.toArray(new JPackage[s.size()]);
    }
    
    public ErrorReceiver getErrorReceiver() {
        return this.errorReceiver;
    }
    
    public CodeModelClassFactory getClassFactory() {
        return this.codeModelClassFactory;
    }
    
    public PackageOutlineImpl getPackageContext(final JPackage p) {
        PackageOutlineImpl r = this.packageContexts.get(p);
        if (r == null) {
            r = new PackageOutlineImpl(this, this.model, p);
            this.packageContexts.put(p, r);
        }
        return r;
    }
    
    private ClassOutlineImpl generateClassDef(final CClassInfo bean) {
        final ImplStructureStrategy.Result r = this.model.strategy.createClasses(this, bean);
        JClass implRef;
        if (bean.getUserSpecifiedImplClass() != null) {
            JDefinedClass usr;
            try {
                usr = this.codeModel._class(bean.getUserSpecifiedImplClass());
                usr.hide();
            }
            catch (JClassAlreadyExistsException e) {
                usr = e.getExistingClass();
            }
            usr._extends(r.implementation);
            implRef = usr;
        }
        else {
            implRef = r.implementation;
        }
        return new ClassOutlineImpl(this, bean, r.exposed, r.implementation, implRef);
    }
    
    public Collection<ClassOutlineImpl> getClasses() {
        assert this.model.beans().size() == this.classes.size();
        return this.classes.values();
    }
    
    public ClassOutlineImpl getClazz(final CClassInfo bean) {
        ClassOutlineImpl r = this.classes.get(bean);
        if (r == null) {
            this.classes.put(bean, r = this.generateClassDef(bean));
        }
        return r;
    }
    
    public ElementOutlineImpl getElement(final CElementInfo ei) {
        ElementOutlineImpl def = this.elements.get(ei);
        if (def == null && ei.hasClass()) {
            def = new ElementOutlineImpl(this, ei);
        }
        return def;
    }
    
    public EnumOutline getEnum(final CEnumLeafInfo eli) {
        return this.enums.get(eli);
    }
    
    public Collection<EnumOutline> getEnums() {
        return this.enums.values();
    }
    
    public Iterable<? extends PackageOutline> getAllPackageContexts() {
        return this.packageContexts.values();
    }
    
    public FieldOutline getField(final CPropertyInfo prop) {
        return this.fields.get(prop);
    }
    
    private void generateClassBody(final ClassOutlineImpl cc) {
        final CClassInfo target = cc.target;
        if (this.model.serializable) {
            cc.implClass._implements(Serializable.class);
            if (this.model.serialVersionUID != null) {
                cc.implClass.field(28, this.codeModel.LONG, "serialVersionUID", JExpr.lit(this.model.serialVersionUID));
            }
        }
        final String mostUsedNamespaceURI = cc._package().getMostUsedNamespaceURI();
        final XmlTypeWriter xtw = cc.implClass.annotate2(XmlTypeWriter.class);
        this.writeTypeName(cc.target.getTypeName(), xtw, mostUsedNamespaceURI);
        if (this.model.options.target.isLaterThan(SpecVersion.V2_1)) {
            final Iterator<CClassInfo> subclasses = cc.target.listSubclasses();
            if (subclasses.hasNext()) {
                final XmlSeeAlsoWriter saw = cc.implClass.annotate2(XmlSeeAlsoWriter.class);
                while (subclasses.hasNext()) {
                    final CClassInfo s = subclasses.next();
                    saw.value(this.getClazz(s).implRef);
                }
            }
        }
        if (target.isElement()) {
            final String namespaceURI = target.getElementName().getNamespaceURI();
            final String localPart = target.getElementName().getLocalPart();
            final XmlRootElementWriter xrew = cc.implClass.annotate2(XmlRootElementWriter.class);
            xrew.name(localPart);
            if (!namespaceURI.equals(mostUsedNamespaceURI)) {
                xrew.namespace(namespaceURI);
            }
        }
        if (target.isOrdered()) {
            for (final CPropertyInfo p : target.getProperties()) {
                if (!(p instanceof CAttributePropertyInfo)) {
                    xtw.propOrder(p.getName(false));
                }
            }
        }
        else {
            xtw.getAnnotationUse().paramArray("propOrder");
        }
        for (final CPropertyInfo prop : target.getProperties()) {
            this.generateFieldDecl(cc, prop);
        }
        if (target.declaresAttributeWildcard()) {
            this.generateAttributeWildcard(cc);
        }
        cc.ref.javadoc().append(target.javadoc);
        cc._package().objectFactoryGenerator().populate(cc);
    }
    
    private void writeTypeName(final QName typeName, final XmlTypeWriter xtw, final String mostUsedNamespaceURI) {
        if (typeName == null) {
            xtw.name("");
        }
        else {
            xtw.name(typeName.getLocalPart());
            final String typeNameURI = typeName.getNamespaceURI();
            if (!typeNameURI.equals(mostUsedNamespaceURI)) {
                xtw.namespace(typeNameURI);
            }
        }
    }
    
    private void generateAttributeWildcard(final ClassOutlineImpl cc) {
        final String FIELD_NAME = "otherAttributes";
        final String METHOD_SEED = this.model.getNameConverter().toClassName(FIELD_NAME);
        final JClass mapType = this.codeModel.ref(Map.class).narrow(new Class[] { QName.class, String.class });
        final JClass mapImpl = this.codeModel.ref(HashMap.class).narrow(new Class[] { QName.class, String.class });
        final JFieldVar $ref = cc.implClass.field(4, mapType, FIELD_NAME, JExpr._new(mapImpl));
        $ref.annotate2(XmlAnyAttributeWriter.class);
        final MethodWriter writer = cc.createMethodWriter();
        final JMethod $get = writer.declareMethod(mapType, "get" + METHOD_SEED);
        $get.javadoc().append("Gets a map that contains attributes that aren't bound to any typed property on this class.\n\n<p>\nthe map is keyed by the name of the attribute and \nthe value is the string value of the attribute.\n\nthe map returned by this method is live, and you can add new attribute\nby updating the map directly. Because of this design, there's no setter.\n");
        $get.javadoc().addReturn().append("always non-null");
        $get.body()._return($ref);
    }
    
    private EnumOutline generateEnumDef(final CEnumLeafInfo e) {
        final JDefinedClass type = this.getClassFactory().createClass(this.getContainer(e.parent, Aspect.EXPOSED), e.shortName, e.getLocator(), ClassType.ENUM);
        type.javadoc().append(e.javadoc);
        return new EnumOutline(e, type) {
            @NotNull
            public Outline parent() {
                return BeanGenerator.this;
            }
        };
    }
    
    private void generateEnumBody(final EnumOutline eo) {
        final JDefinedClass type = eo.clazz;
        final CEnumLeafInfo e = eo.target;
        final XmlTypeWriter xtw = type.annotate2(XmlTypeWriter.class);
        this.writeTypeName(e.getTypeName(), xtw, eo._package().getMostUsedNamespaceURI());
        final JCodeModel codeModel = this.model.codeModel;
        final JType baseExposedType = e.base.toType(this, Aspect.EXPOSED).unboxify();
        final JType baseImplType = e.base.toType(this, Aspect.IMPLEMENTATION).unboxify();
        final XmlEnumWriter xew = type.annotate2(XmlEnumWriter.class);
        xew.value(baseExposedType);
        final boolean needsValue = e.needsValueField();
        final Set<String> enumFieldNames = new HashSet<String>();
        for (final CEnumConstant mem : e.members) {
            final String constName = mem.getName();
            if (!JJavaName.isJavaIdentifier(constName)) {
                this.getErrorReceiver().error(e.getLocator(), Messages.ERR_UNUSABLE_NAME.format(mem.getLexicalValue(), constName));
            }
            if (!enumFieldNames.add(constName)) {
                this.getErrorReceiver().error(e.getLocator(), Messages.ERR_NAME_COLLISION.format(constName));
            }
            final JEnumConstant constRef = type.enumConstant(constName);
            if (needsValue) {
                constRef.arg(e.base.createConstant(this, new XmlString(mem.getLexicalValue())));
            }
            if (!mem.getLexicalValue().equals(constName)) {
                constRef.annotate2(XmlEnumValueWriter.class).value(mem.getLexicalValue());
            }
            if (mem.javadoc != null) {
                constRef.javadoc().append(mem.javadoc);
            }
            eo.constants.add(new EnumConstantOutline(mem, constRef) {});
        }
        if (needsValue) {
            final JFieldVar $value = type.field(12, baseExposedType, "value");
            type.method(1, baseExposedType, "value").body()._return($value);
            JMethod m = type.constructor(0);
            m.body().assign($value, m.param(baseImplType, "v"));
            m = type.method(17, type, "fromValue");
            final JVar $v = m.param(baseExposedType, "v");
            final JForEach fe = m.body().forEach(type, "c", type.staticInvoke("values"));
            JExpression eq;
            if (baseExposedType.isPrimitive()) {
                eq = fe.var().ref($value).eq($v);
            }
            else {
                eq = fe.var().ref($value).invoke("equals").arg($v);
            }
            fe.body()._if(eq)._then()._return(fe.var());
            final JInvocation ex = JExpr._new(codeModel.ref(IllegalArgumentException.class));
            JExpression strForm;
            if (baseExposedType.isPrimitive()) {
                strForm = codeModel.ref(String.class).staticInvoke("valueOf").arg($v);
            }
            else if (baseExposedType == codeModel.ref(String.class)) {
                strForm = $v;
            }
            else {
                strForm = $v.invoke("toString");
            }
            m.body()._throw(ex.arg(strForm));
        }
        else {
            type.method(1, String.class, "value").body()._return(JExpr.invoke("name"));
            final JMethod i = type.method(17, type, "fromValue");
            i.body()._return(JExpr.invoke("valueOf").arg(i.param(String.class, "v")));
        }
    }
    
    private FieldOutline generateFieldDecl(final ClassOutlineImpl cc, final CPropertyInfo prop) {
        FieldRenderer fr = prop.realization;
        if (fr == null) {
            fr = this.model.options.getFieldRendererFactory().getDefault();
        }
        final FieldOutline field = fr.generate(cc, prop);
        this.fields.put(prop, field);
        return field;
    }
    
    public final void generateAdapterIfNecessary(final CPropertyInfo prop, final JAnnotatable field) {
        final CAdapter adapter = prop.getAdapter();
        if (adapter != null) {
            if (adapter.getAdapterIfKnown() == SwaRefAdapter.class) {
                field.annotate(XmlAttachmentRef.class);
            }
            else {
                final XmlJavaTypeAdapterWriter xjtw = field.annotate2(XmlJavaTypeAdapterWriter.class);
                xjtw.value(((NClass)adapter.adapterType).toType((Outline)this, Aspect.EXPOSED));
            }
        }
        switch (prop.id()) {
            case ID: {
                field.annotate(XmlID.class);
                break;
            }
            case IDREF: {
                field.annotate(XmlIDREF.class);
                break;
            }
        }
        if (prop.getExpectedMimeType() != null) {
            field.annotate2(XmlMimeTypeWriter.class).value(prop.getExpectedMimeType().toString());
        }
    }
    
    public final JClass addRuntime(final Class clazz) {
        JClass g = this.generatedRuntime.get(clazz);
        if (g == null) {
            final JPackage implPkg = this.getUsedPackages(Aspect.IMPLEMENTATION)[0].subPackage("runtime");
            g = this.generateStaticClass(clazz, implPkg);
            this.generatedRuntime.put(clazz, g);
        }
        return g;
    }
    
    public JClass generateStaticClass(final Class src, final JPackage out) {
        final String shortName = this.getShortName(src.getName());
        URL res = src.getResource(shortName + ".java");
        if (res == null) {
            res = src.getResource(shortName + ".java_");
        }
        if (res == null) {
            throw new InternalError("Unable to load source code of " + src.getName() + " as a resource");
        }
        final JStaticJavaFile sjf = new JStaticJavaFile(out, shortName, res, null);
        out.addResourceFile(sjf);
        return sjf.getJClass();
    }
    
    private String getShortName(final String name) {
        return name.substring(name.lastIndexOf(46) + 1);
    }
}
