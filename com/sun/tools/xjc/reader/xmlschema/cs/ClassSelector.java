// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.xml.xsom.XSSchema;
import com.sun.codemodel.JJavaName;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.codemodel.JPackage;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.util.ComponentNameFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import java.io.Writer;
import com.sun.codemodel.util.JavadocEscapeWriter;
import java.io.StringWriter;
import com.sun.codemodel.JDocComment;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.xmlschema.PrefixedJClassFactoryImpl;
import com.sun.tools.xjc.util.Util;
import com.sun.xml.xsom.XSType;
import java.util.HashSet;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.codemodel.JClassContainer;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.reader.xmlschema.JClassFactory;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import com.sun.msv.util.LightStack;
import java.util.Map;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;

public class ClassSelector
{
    private final String defaultPackageName;
    public final BGMBuilder builder;
    protected final AGMFragmentBuilder agmFragmentBuilder;
    public final CodeModelClassFactory codeModelClassFactory;
    private final Map bindMap;
    private final LightStack bindQueue;
    private final ClassBinder classBinder;
    private final DOMBinder domBinder;
    private final Stack classFactories;
    private Set reportedAbstractComplexTypes;
    private static final String[] reservedClassNames;
    private static Set checkedPackageNames;
    
    public ClassSelector(final BGMBuilder _builder, final String defaultPackage) {
        this.bindMap = new HashMap();
        this.bindQueue = new LightStack();
        this.classFactories = new Stack();
        this.reportedAbstractComplexTypes = null;
        this.builder = _builder;
        this.agmFragmentBuilder = new AGMFragmentBuilder(_builder);
        this.codeModelClassFactory = new CodeModelClassFactory(this.builder.getErrorReceiver());
        this.domBinder = new DOMBinder(this);
        this.defaultPackageName = defaultPackage;
        ClassBinder c = (ClassBinder)new DefaultClassBinder(this);
        if (this.builder.getGlobalBinding().isModelGroupBinding()) {
            c = (ClassBinder)new ModelGroupBindingClassBinder(this, c);
        }
        this.classBinder = c;
        this.classFactories.push(null);
    }
    
    public final JClassFactory getClassFactory() {
        return this.classFactories.peek();
    }
    
    public final void pushClassFactory(final JClassFactory clsFctry) {
        this.classFactories.push(clsFctry);
    }
    
    public final void popClassFactory() {
        this.classFactories.pop();
    }
    
    public ClassItem bindToType(final XSComplexType ct) {
        return this._bindToClass(ct, true);
    }
    
    public TypeItem bindToType(final XSElementDecl e) {
        final TypeItem t = this.domBinder.bind(e);
        if (t != null) {
            return t;
        }
        return this._bindToClass(e, false);
    }
    
    public Expression bindToType(final XSComponent sc) {
        final Expression t = this.domBinder.bind(sc);
        if (t != null) {
            return t;
        }
        return (Expression)this._bindToClass(sc, false);
    }
    
    private ClassItem _bindToClass(final XSComponent sc, final boolean cannotBeDelayed) {
        if (!this.bindMap.containsKey(sc)) {
            if (sc instanceof XSElementDecl) {
                this.checkAbstractComplexType((XSElementDecl)sc);
            }
            boolean isGlobal = false;
            if (sc instanceof XSDeclaration) {
                isGlobal = ((XSDeclaration)sc).isGlobal();
                if (isGlobal) {
                    this.pushClassFactory((JClassFactory)new JClassFactoryImpl(this, (JClassContainer)this.getPackage(((XSDeclaration)sc).getTargetNamespace())));
                }
            }
            final ClassItem ci = sc.apply((XSFunction<ClassItem>)this.classBinder);
            if (isGlobal) {
                this.popClassFactory();
            }
            if (ci == null) {
                return null;
            }
            this.queueBuild(sc, ci);
        }
        final Binding bind = this.bindMap.get(sc);
        if (cannotBeDelayed) {
            bind.build();
        }
        return bind.ci;
    }
    
    public void executeTasks() {
        while (this.bindQueue.size() != 0) {
            ((Binding)this.bindQueue.pop()).build();
        }
    }
    
    private void checkAbstractComplexType(final XSElementDecl decl) {
        if (this.builder.inExtensionMode) {
            return;
        }
        final XSType t = decl.getType();
        if (t.isComplexType() && t.asComplexType().isAbstract()) {
            if (this.reportedAbstractComplexTypes == null) {
                this.reportedAbstractComplexTypes = new HashSet();
            }
            if (this.reportedAbstractComplexTypes.add(t)) {
                this.builder.errorReceiver.error(t.getLocator(), Messages.format("ClassSelector.AbstractComplexType", (Object)t.getName()));
                this.builder.errorReceiver.error(decl.getLocator(), Messages.format("ClassSelector.AbstractComplexType.SourceLocation"));
            }
        }
    }
    
    private boolean needValueConstructor(final XSComponent sc) {
        if (!(sc instanceof XSElementDecl)) {
            return false;
        }
        final XSElementDecl decl = (XSElementDecl)sc;
        return decl.getType().isSimpleType();
    }
    
    public void queueBuild(final XSComponent sc, final ClassItem ci) {
        final Binding b = new Binding(this, sc, ci);
        this.bindQueue.push((Object)b);
        final Object o = this.bindMap.put(sc, b);
        _assert(o == null);
    }
    
    private void build(final XSComponent sc, final ClassItem ci) {
        _assert(this.bindMap.get(sc).ci == ci);
        for (int i = 0; i < ClassSelector.reservedClassNames.length; ++i) {
            if (ci.getTypeAsDefined().name().equals(ClassSelector.reservedClassNames[i])) {
                this.builder.errorReceiver.error(sc.getLocator(), Messages.format("ClassSelector.ReservedClassName", (Object)ClassSelector.reservedClassNames[i]));
                break;
            }
        }
        this.addSchemaFragmentJavadoc(ci.getTypeAsDefined().javadoc(), sc);
        if (Util.getSystemProperty(this.getClass(), "nestedInterface") != null) {
            this.pushClassFactory(new PrefixedJClassFactoryImpl(this.builder, ci.getTypeAsDefined()));
        }
        else {
            this.pushClassFactory((JClassFactory)new JClassFactoryImpl(this, (JClassContainer)ci.getTypeAsDefined()));
        }
        ci.exp = this.builder.fieldBuilder.build(sc);
        ci.agm.exp = this.agmFragmentBuilder.build(sc, ci);
        this.popClassFactory();
        final BIProperty prop = (BIProperty)this.builder.getBindInfo(sc).get(BIProperty.NAME);
        if (prop != null) {
            prop.markAsAcknowledged();
        }
        if (ci.hasGetContentMethod) {
            ci.exp.visit((ExpressionVisitorVoid)new ClassSelector$1(this));
        }
    }
    
    private void addSchemaFragmentJavadoc(final JDocComment javadoc, final XSComponent sc) {
        final BindInfo bi = this.builder.getBindInfo(sc);
        final String doc = bi.getDocumentation();
        if (doc != null && bi.hasTitleInDocumentation()) {
            javadoc.appendComment(doc);
            javadoc.appendComment("\n");
        }
        final StringWriter out = new StringWriter();
        final SchemaWriter sw = new SchemaWriter(new JavadocEscapeWriter(out));
        sc.visit(sw);
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
        String lineNumber = Messages.format("ClassSelector.JavadocLineUnknown");
        if (loc != null && loc.getLineNumber() != -1) {
            lineNumber = String.valueOf(loc.getLineNumber());
        }
        final String componentName = sc.apply((XSFunction<String>)new ComponentNameFunction());
        javadoc.appendComment(Messages.format("ClassSelector.JavadocHeading", (Object)componentName, (Object)fileName, (Object)lineNumber));
        if (doc != null && !bi.hasTitleInDocumentation()) {
            javadoc.appendComment("\n");
            javadoc.appendComment(doc);
            javadoc.appendComment("\n");
        }
        javadoc.appendComment("\n<p>\n<pre>\n");
        javadoc.appendComment(out.getBuffer().toString());
        javadoc.appendComment("</pre>");
    }
    
    public JPackage getPackage(final String targetNamespace) {
        final XSSchema s = this.builder.schemas.getSchema(targetNamespace);
        final BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(s).get(BISchemaBinding.NAME);
        String name = null;
        if (this.defaultPackageName != null) {
            name = this.defaultPackageName;
        }
        if (name == null && sb != null && sb.getPackageName() != null) {
            name = sb.getPackageName();
        }
        if (name == null) {
            name = com.sun.tools.xjc.reader.Util.getPackageNameFromNamespaceURI(targetNamespace, this.builder.getNameConverter());
        }
        if (name == null) {
            name = "generated";
        }
        if (ClassSelector.checkedPackageNames.add(name) && !JJavaName.isJavaPackageName(name)) {
            this.builder.errorReceiver.error(s.getLocator(), Messages.format("ClassSelector.IncorrectPackageName", (Object)targetNamespace, (Object)name));
        }
        return this.builder.grammar.codeModel._package(name);
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    static {
        ClassSelector.reservedClassNames = new String[] { "ObjectFactory" };
        ClassSelector.checkedPackageNames = new HashSet();
    }
    
    private class Binding
    {
        private final XSComponent sc;
        private final ClassItem ci;
        private boolean built;
        
        Binding(final ClassSelector this$0, final XSComponent _sc, final ClassItem _ci) {
            this.this$0 = this$0;
            this.sc = _sc;
            this.ci = _ci;
        }
        
        void build() {
            if (this.built) {
                return;
            }
            this.built = true;
            ClassSelector.access$000(this.this$0, this.sc, this.ci);
            if (ClassSelector.access$100(this.this$0, this.sc)) {
                this.ci.addConstructor(new String[] { "Value" });
            }
        }
    }
}
