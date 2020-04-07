// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.LocalScoping;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JPackage;
import org.xml.sax.Locator;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import java.io.Writer;
import com.sun.codemodel.util.JavadocEscapeWriter;
import java.io.StringWriter;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.util.ComponentNameFunction;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.xml.xsom.XSType;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.xml.xsom.XSSchemaSet;
import java.util.HashSet;
import java.util.HashMap;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CClassInfo;
import java.util.Set;
import java.util.Stack;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.xml.xsom.XSComponent;
import java.util.Map;

public final class ClassSelector extends BindingComponent
{
    private final BGMBuilder builder;
    private final Map<XSComponent, Binding> bindMap;
    final Map<XSComponent, CElementInfo> boundElements;
    private final Stack<Binding> bindQueue;
    private final Set<CClassInfo> built;
    private final ClassBinder classBinder;
    private final Stack<CClassInfoParent> classScopes;
    private XSComponent currentRoot;
    private CClassInfo currentBean;
    private static final String[] reservedClassNames;
    private static Set<String> checkedPackageNames;
    
    public ClassSelector() {
        this.builder = Ring.get(BGMBuilder.class);
        this.bindMap = new HashMap<XSComponent, Binding>();
        this.boundElements = new HashMap<XSComponent, CElementInfo>();
        this.bindQueue = new Stack<Binding>();
        this.built = new HashSet<CClassInfo>();
        this.classScopes = new Stack<CClassInfoParent>();
        Ring.add(ClassBinder.class, this.classBinder = new Abstractifier(new DefaultClassBinder()));
        this.classScopes.push(null);
        final XSComplexType anyType = Ring.get(XSSchemaSet.class).getComplexType("http://www.w3.org/2001/XMLSchema", "anyType");
        this.bindMap.put(anyType, new Binding(anyType, CBuiltinLeafInfo.ANYTYPE));
    }
    
    public final CClassInfoParent getClassScope() {
        assert !this.classScopes.isEmpty();
        return this.classScopes.peek();
    }
    
    public final void pushClassScope(final CClassInfoParent clsFctry) {
        assert clsFctry != null;
        this.classScopes.push(clsFctry);
    }
    
    public final void popClassScope() {
        this.classScopes.pop();
    }
    
    public XSComponent getCurrentRoot() {
        return this.currentRoot;
    }
    
    public CClassInfo getCurrentBean() {
        return this.currentBean;
    }
    
    public final CElement isBound(final XSElementDecl x, final XSComponent referer) {
        final CElementInfo r = this.boundElements.get(x);
        if (r != null) {
            return r;
        }
        return this.bindToType(x, referer);
    }
    
    public CTypeInfo bindToType(final XSComponent sc, final XSComponent referer) {
        return this._bindToClass(sc, referer, false);
    }
    
    public CElement bindToType(final XSElementDecl e, final XSComponent referer) {
        return (CElement)this._bindToClass(e, referer, false);
    }
    
    public CClass bindToType(final XSComplexType t, final XSComponent referer, final boolean cannotBeDelayed) {
        return (CClass)this._bindToClass(t, referer, cannotBeDelayed);
    }
    
    public TypeUse bindToType(final XSType t, final XSComponent referer) {
        if (t instanceof XSSimpleType) {
            return Ring.get(SimpleTypeBuilder.class).build((XSSimpleType)t);
        }
        return (CNonElement)this._bindToClass(t, referer, false);
    }
    
    CTypeInfo _bindToClass(@NotNull final XSComponent sc, final XSComponent referer, final boolean cannotBeDelayed) {
        if (!this.bindMap.containsKey(sc)) {
            boolean isGlobal = false;
            if (sc instanceof XSDeclaration) {
                isGlobal = ((XSDeclaration)sc).isGlobal();
                if (isGlobal) {
                    this.pushClassScope(new CClassInfoParent.Package(this.getPackage(((XSDeclaration)sc).getTargetNamespace())));
                }
            }
            final CElement bean = sc.apply((XSFunction<CElement>)this.classBinder);
            if (isGlobal) {
                this.popClassScope();
            }
            if (bean == null) {
                return null;
            }
            if (bean instanceof CClassInfo) {
                final XSSchema os = sc.getOwnerSchema();
                final BISchemaBinding sb = this.builder.getBindInfo(os).get(BISchemaBinding.class);
                if (sb != null && !sb.map) {
                    this.getErrorReporter().error(sc.getLocator(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS", sc.apply((XSFunction<Object>)new ComponentNameFunction()));
                    this.getErrorReporter().error(sb.getLocation(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS_MAP_FALSE", os.getTargetNamespace());
                    if (referer != null) {
                        this.getErrorReporter().error(referer.getLocator(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS_REFERER", referer.apply((XSFunction<Object>)new ComponentNameFunction()));
                    }
                }
            }
            this.queueBuild(sc, bean);
        }
        final Binding bind = this.bindMap.get(sc);
        if (cannotBeDelayed) {
            bind.build();
        }
        return bind.bean;
    }
    
    public void executeTasks() {
        while (this.bindQueue.size() != 0) {
            this.bindQueue.pop().build();
        }
    }
    
    private boolean needValueConstructor(final XSComponent sc) {
        if (!(sc instanceof XSElementDecl)) {
            return false;
        }
        final XSElementDecl decl = (XSElementDecl)sc;
        return decl.getType().isSimpleType();
    }
    
    public void queueBuild(final XSComponent sc, final CElement bean) {
        final Binding b = new Binding(sc, bean);
        this.bindQueue.push(b);
        final Binding old = this.bindMap.put(sc, b);
        assert old.bean == bean;
    }
    
    private void addSchemaFragmentJavadoc(final CClassInfo bean, final XSComponent sc) {
        final String doc = this.builder.getBindInfo(sc).getDocumentation();
        if (doc != null) {
            this.append(bean, doc);
        }
        final Locator loc = sc.getLocator();
        String fileName = null;
        if (loc != null) {
            fileName = loc.getPublicId();
            if (fileName == null) {
                fileName = loc.getSystemId();
            }
        }
        if (fileName == null) {
            fileName = "";
        }
        String lineNumber = Messages.format("ClassSelector.JavadocLineUnknown", new Object[0]);
        if (loc != null && loc.getLineNumber() != -1) {
            lineNumber = String.valueOf(loc.getLineNumber());
        }
        final String componentName = sc.apply((XSFunction<String>)new ComponentNameFunction());
        final String jdoc = Messages.format("ClassSelector.JavadocHeading", componentName, fileName, lineNumber);
        this.append(bean, jdoc);
        final StringWriter out = new StringWriter();
        out.write("<pre>\n");
        final SchemaWriter sw = new SchemaWriter(new JavadocEscapeWriter(out));
        sc.visit(sw);
        out.write("</pre>");
        this.append(bean, out.toString());
    }
    
    private void append(final CClassInfo bean, final String doc) {
        if (bean.javadoc == null) {
            bean.javadoc = doc + '\n';
        }
        else {
            bean.javadoc = bean.javadoc + '\n' + doc + '\n';
        }
    }
    
    public JPackage getPackage(final String targetNamespace) {
        final XSSchema s = Ring.get(XSSchemaSet.class).getSchema(targetNamespace);
        final BISchemaBinding sb = this.builder.getBindInfo(s).get(BISchemaBinding.class);
        if (sb != null) {
            sb.markAsAcknowledged();
        }
        String name = null;
        if (this.builder.defaultPackage1 != null) {
            name = this.builder.defaultPackage1;
        }
        if (name == null && sb != null && sb.getPackageName() != null) {
            name = sb.getPackageName();
        }
        if (name == null && this.builder.defaultPackage2 != null) {
            name = this.builder.defaultPackage2;
        }
        if (name == null) {
            name = this.builder.getNameConverter().toPackageName(targetNamespace);
        }
        if (name == null) {
            name = "generated";
        }
        if (ClassSelector.checkedPackageNames.add(name) && !JJavaName.isJavaPackageName(name)) {
            this.getErrorReporter().error(s.getLocator(), "ClassSelector.IncorrectPackageName", targetNamespace, name);
        }
        return Ring.get(JCodeModel.class)._package(name);
    }
    
    static {
        reservedClassNames = new String[] { "ObjectFactory" };
        ClassSelector.checkedPackageNames = new HashSet<String>();
    }
    
    private final class Binding
    {
        private final XSComponent sc;
        private final CTypeInfo bean;
        
        public Binding(final XSComponent sc, final CTypeInfo bean) {
            this.sc = sc;
            this.bean = bean;
        }
        
        void build() {
            if (!(this.bean instanceof CClassInfo)) {
                return;
            }
            final CClassInfo bean = (CClassInfo)this.bean;
            if (!ClassSelector.this.built.add(bean)) {
                return;
            }
            for (final String reservedClassName : ClassSelector.reservedClassNames) {
                if (bean.getName().equals(reservedClassName)) {
                    ClassSelector.this.getErrorReporter().error(this.sc.getLocator(), "ClassSelector.ReservedClassName", reservedClassName);
                    break;
                }
            }
            if (ClassSelector.this.needValueConstructor(this.sc)) {
                bean.addConstructor("value");
            }
            if (bean.javadoc == null) {
                ClassSelector.this.addSchemaFragmentJavadoc(bean, this.sc);
            }
            if (ClassSelector.this.builder.getGlobalBinding().getFlattenClasses() == LocalScoping.NESTED) {
                ClassSelector.this.pushClassScope(bean);
            }
            else {
                ClassSelector.this.pushClassScope(bean.parent());
            }
            final XSComponent oldRoot = ClassSelector.this.currentRoot;
            final CClassInfo oldBean = ClassSelector.this.currentBean;
            ClassSelector.this.currentRoot = this.sc;
            ClassSelector.this.currentBean = bean;
            this.sc.visit(Ring.get(BindRed.class));
            ClassSelector.this.currentBean = oldBean;
            ClassSelector.this.currentRoot = oldRoot;
            ClassSelector.this.popClassScope();
            final BIProperty prop = ClassSelector.this.builder.getBindInfo(this.sc).get(BIProperty.class);
            if (prop != null) {
                prop.markAsAcknowledged();
            }
        }
    }
}
