// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JForLoop;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.codemodel.JInvocation;
import java.util.Iterator;
import com.sun.tools.xjc.grammar.Constructor;
import com.sun.msv.grammar.Grammar;
import com.sun.codemodel.JMethod;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.msv.writer.relaxng.RELAXNGWriter;
import java.io.IOException;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.fmt.JSerializedObject;
import com.sun.codemodel.JType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JResourceFile;
import com.sun.codemodel.fmt.JPropertyFile;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.util.Util;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.Options;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

final class ObjectFactoryGenerator
{
    private static final PrintStream debug;
    private final GeneratorContext context;
    private final AnnotatedGrammar grammar;
    private final JCodeModel codeModel;
    private final Options opt;
    private final JPackage targetPackage;
    private final JVar $grammarInfo;
    private final JVar $rootTagMap;
    private final DefaultImplementationMapGenerator defImplMapGenerator;
    private final JDefinedClass objectFactory;
    
    public JVar getGrammarInfo() {
        return this.$grammarInfo;
    }
    
    public JDefinedClass getObjectFactory() {
        return this.objectFactory;
    }
    
    public JVar getRootTagMap() {
        return this.$rootTagMap;
    }
    
    ObjectFactoryGenerator(final GeneratorContext _context, final AnnotatedGrammar _grammar, final Options _opt, final JPackage _pkg) {
        this.context = _context;
        this.grammar = _grammar;
        this.opt = _opt;
        this.codeModel = this.grammar.codeModel;
        this.targetPackage = _pkg;
        this.objectFactory = this.context.getClassFactory().createClass(this.targetPackage, "ObjectFactory", null);
        this.defImplMapGenerator = new DefaultImplementationMapGenerator(this, Util.calculateInitialHashMapCapacity(this.countClassItems(), 0.75f));
        this.$rootTagMap = this.objectFactory.field(20, (ObjectFactoryGenerator.class$java$util$HashMap == null) ? (ObjectFactoryGenerator.class$java$util$HashMap = class$("java.util.HashMap")) : ObjectFactoryGenerator.class$java$util$HashMap, "rootTagMap", JExpr._new(this.objectFactory.owner().ref((ObjectFactoryGenerator.class$java$util$HashMap == null) ? (ObjectFactoryGenerator.class$java$util$HashMap = class$("java.util.HashMap")) : ObjectFactoryGenerator.class$java$util$HashMap)));
        this.objectFactory._extends(this.context.getRuntime((ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl == null) ? (ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl = class$("com.sun.tools.xjc.runtime.DefaultJAXBContextImpl")) : ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl));
        final JPropertyFile jaxbProperties = new JPropertyFile("jaxb.properties");
        this.targetPackage.addResourceFile(jaxbProperties);
        jaxbProperties.add("javax.xml.bind.context.factory", ((ObjectFactoryGenerator.class$com$sun$xml$bind$ContextFactory_1_0_1 == null) ? (ObjectFactoryGenerator.class$com$sun$xml$bind$ContextFactory_1_0_1 = class$("com.sun.xml.bind.ContextFactory_1_0_1")) : ObjectFactoryGenerator.class$com$sun$xml$bind$ContextFactory_1_0_1).getName());
        jaxbProperties.add("com.sun.xml.bind.jaxbContextImpl", this.context.getRuntime((ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl == null) ? (ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl = class$("com.sun.tools.xjc.runtime.DefaultJAXBContextImpl")) : ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$DefaultJAXBContextImpl).fullName());
        if (this.opt.debugMode && !this.targetPackage.isUnnamed()) {
            try {
                this.codeModel._package("")._class("ObjectFactory")._extends(this.objectFactory);
            }
            catch (JClassAlreadyExistsException ex) {}
        }
        this.$grammarInfo = this.objectFactory.field(25, this.context.getRuntime((ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfo == null) ? (ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfo = class$("com.sun.tools.xjc.runtime.GrammarInfo")) : ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfo), "grammarInfo", JExpr._new(this.context.getRuntime((ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfoImpl == null) ? (ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfoImpl = class$("com.sun.tools.xjc.runtime.GrammarInfoImpl")) : ObjectFactoryGenerator.class$com$sun$tools$xjc$runtime$GrammarInfoImpl)).arg(this.$rootTagMap).arg(this.defImplMapGenerator.$map).arg(this.objectFactory.dotclass()));
        final JMethod m1 = this.objectFactory.constructor(1);
        m1.body().invoke("super").arg(this.$grammarInfo);
        m1.javadoc().setComment("Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: " + this.targetPackage.name());
        final JMethod m2 = this.objectFactory.method(1, this.codeModel.ref((ObjectFactoryGenerator.class$java$lang$Object == null) ? (ObjectFactoryGenerator.class$java$lang$Object = class$("java.lang.Object")) : ObjectFactoryGenerator.class$java$lang$Object), "newInstance")._throws((ObjectFactoryGenerator.class$javax$xml$bind$JAXBException == null) ? (ObjectFactoryGenerator.class$javax$xml$bind$JAXBException = class$("javax.xml.bind.JAXBException")) : ObjectFactoryGenerator.class$javax$xml$bind$JAXBException);
        m2.param((ObjectFactoryGenerator.class$java$lang$Class == null) ? (ObjectFactoryGenerator.class$java$lang$Class = class$("java.lang.Class")) : ObjectFactoryGenerator.class$java$lang$Class, "javaContentInterface");
        m2.body()._return(JExpr.invoke(JExpr._super(), "newInstance").arg(JExpr.ref("javaContentInterface")));
        m2.javadoc().setComment("Create an instance of the specified Java content interface.").addParam("javaContentInterface", "the Class object of the javacontent interface to instantiate").addReturn("a new instance").addThrows("JAXBException", "if an error occurs");
        final JMethod m3 = this.objectFactory.method(1, this.codeModel.ref((ObjectFactoryGenerator.class$java$lang$Object == null) ? (ObjectFactoryGenerator.class$java$lang$Object = class$("java.lang.Object")) : ObjectFactoryGenerator.class$java$lang$Object), "getProperty")._throws((ObjectFactoryGenerator.class$javax$xml$bind$PropertyException == null) ? (ObjectFactoryGenerator.class$javax$xml$bind$PropertyException = class$("javax.xml.bind.PropertyException")) : ObjectFactoryGenerator.class$javax$xml$bind$PropertyException);
        JVar $name = m3.param((ObjectFactoryGenerator.class$java$lang$String == null) ? (ObjectFactoryGenerator.class$java$lang$String = class$("java.lang.String")) : ObjectFactoryGenerator.class$java$lang$String, "name");
        m3.body()._return(JExpr._super().invoke("getProperty").arg($name));
        m3.javadoc().setComment("Get the specified property. This method can only be\nused to get provider specific properties.\nAttempting to get an undefined property will result\nin a PropertyException being thrown.").addParam("name", "the name of the property to retrieve").addReturn("the value of the requested property").addThrows("PropertyException", "when there is an error retrieving the given property or value");
        final JMethod m4 = this.objectFactory.method(1, this.codeModel.VOID, "setProperty")._throws((ObjectFactoryGenerator.class$javax$xml$bind$PropertyException == null) ? (ObjectFactoryGenerator.class$javax$xml$bind$PropertyException = class$("javax.xml.bind.PropertyException")) : ObjectFactoryGenerator.class$javax$xml$bind$PropertyException);
        $name = m4.param((ObjectFactoryGenerator.class$java$lang$String == null) ? (ObjectFactoryGenerator.class$java$lang$String = class$("java.lang.String")) : ObjectFactoryGenerator.class$java$lang$String, "name");
        final JVar $value = m4.param((ObjectFactoryGenerator.class$java$lang$Object == null) ? (ObjectFactoryGenerator.class$java$lang$Object = class$("java.lang.Object")) : ObjectFactoryGenerator.class$java$lang$Object, "value");
        m4.body().invoke(JExpr._super(), "setProperty").arg($name).arg($value);
        m4.javadoc().setComment("Set the specified property. This method can only be\nused to set provider specific properties.\nAttempting to set an undefined property will result\nin a PropertyException being thrown.").addParam("name", "the name of the property to retrieve").addParam("value", "the value of the property to be set").addThrows("PropertyException", "when there is an error processing the given property or value");
        final Grammar purifiedGrammar = AGMBuilder.remove(this.grammar);
        try {
            this.targetPackage.addResourceFile(new JSerializedObject("bgm.ser", purifiedGrammar));
        }
        catch (IOException e) {
            throw new JAXBAssertionError((Throwable)e);
        }
        if (ObjectFactoryGenerator.debug != null) {
            ObjectFactoryGenerator.debug.println("---- schema ----");
            try {
                final RELAXNGWriter w = new RELAXNGWriter();
                final OutputFormat format = new OutputFormat("xml", null, true);
                format.setIndent(1);
                w.setDocumentHandler((DocumentHandler)new XMLSerializer(ObjectFactoryGenerator.debug, format));
                w.write(purifiedGrammar);
            }
            catch (SAXException e2) {
                throw new JAXBAssertionError((Throwable)e2);
            }
        }
        this.objectFactory.javadoc().appendComment("This object contains factory methods for each \nJava content interface and Java element interface \ngenerated in the " + this.targetPackage.name() + " package. \n" + "<p>An ObjectFactory allows you to programatically \n" + "construct new instances of the Java representation \n" + "for XML content. The Java representation of XML \n" + "content can consist of schema derived interfaces \n" + "and classes representing the binding of schema \n" + "type definitions, element declarations and model \n" + "groups.  Factory methods for each of these are \n" + "provided in this class.");
    }
    
    void populate(final ClassContext cc) {
        final JMethod m = this.objectFactory.method(1, cc.ref, "create" + this.getPartlyQualifiedName(cc.ref))._throws((ObjectFactoryGenerator.class$javax$xml$bind$JAXBException == null) ? (ObjectFactoryGenerator.class$javax$xml$bind$JAXBException = class$("javax.xml.bind.JAXBException")) : ObjectFactoryGenerator.class$javax$xml$bind$JAXBException);
        m.body()._return(JExpr._new(cc.implRef));
        m.javadoc().appendComment("Create an instance of " + this.getPartlyQualifiedName(cc.ref)).addThrows("JAXBException", "if an error occurs");
        final Iterator itr = cc.target.iterateConstructors();
        if (itr.hasNext()) {
            cc.implClass.constructor(1);
        }
        while (itr.hasNext()) {
            final Constructor cons = itr.next();
            final JMethod i = this.objectFactory.method(1, cc.ref, "create" + this.getPartlyQualifiedName(cc.ref));
            final JInvocation inv = JExpr._new(cc.implRef);
            i.body()._return(inv);
            i._throws(this.codeModel.ref((ObjectFactoryGenerator.class$javax$xml$bind$JAXBException == null) ? (ObjectFactoryGenerator.class$javax$xml$bind$JAXBException = class$("javax.xml.bind.JAXBException")) : ObjectFactoryGenerator.class$javax$xml$bind$JAXBException));
            i.javadoc().appendComment("Create an instance of " + this.getPartlyQualifiedName(cc.ref)).addThrows("JAXBException", "if an error occurs");
            final JMethod c = cc.implClass.constructor(1);
            for (int j = 0; j < cons.fields.length; ++j) {
                String fieldName = cons.fields[j];
                final FieldUse field = cc.target.getField(fieldName);
                if (field == null) {
                    throw new UnsupportedOperationException("illegal constructor param name: " + fieldName);
                }
                fieldName = camelize(fieldName);
                final FieldRenderer renderer = this.context.getField(field);
                JVar $fvar;
                if (field.multiplicity.isAtMostOnce()) {
                    $fvar = i.param(field.type, fieldName);
                    final JVar $var = c.param(field.type, fieldName);
                    renderer.setter(c.body(), (JExpression)$var);
                }
                else {
                    $fvar = i.param(field.type.array(), fieldName);
                    final JVar $var = c.param(field.type.array(), fieldName);
                    final JForLoop forLoop = c.body()._for();
                    final JVar $i = forLoop.init(this.codeModel.INT, "___i", JExpr.lit(0));
                    forLoop.test($i.lt($var.ref("length")));
                    forLoop.update($i.incr());
                    renderer.setter(forLoop.body(), (JExpression)$var.component($i));
                }
                inv.arg($fvar);
            }
        }
        this.defImplMapGenerator.add(cc.ref, cc.implRef);
    }
    
    private String getPartlyQualifiedName(final JDefinedClass cls) {
        if (cls.parentContainer() instanceof JPackage) {
            return cls.name();
        }
        return this.getPartlyQualifiedName((JDefinedClass)cls.parentContainer()) + cls.name();
    }
    
    private static String camelize(final String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    
    private int countClassItems() {
        final ClassItem[] classItems = this.grammar.getClasses();
        int count = 0;
        for (int i = 0; i < classItems.length; ++i) {
            if (classItems[i].getTypeAsDefined()._package() == this.targetPackage) {
                ++count;
            }
        }
        return count;
    }
    
    static {
        ObjectFactoryGenerator.debug = ((Util.getSystemProperty((ObjectFactoryGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator == null) ? (ObjectFactoryGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator = class$("com.sun.tools.xjc.generator.ObjectFactoryGenerator")) : ObjectFactoryGenerator.class$com$sun$tools$xjc$generator$ObjectFactoryGenerator, "debug") != null) ? System.out : null);
    }
    
    private class DefaultImplementationMapGenerator extends StaticMapGenerator
    {
        DefaultImplementationMapGenerator(final ObjectFactoryGenerator this$0, final int initialCapacity) {
            super((JVar)this$0.objectFactory.field(20, (ObjectFactoryGenerator.class$java$util$HashMap == null) ? (ObjectFactoryGenerator.class$java$util$HashMap = ObjectFactoryGenerator.class$("java.util.HashMap")) : ObjectFactoryGenerator.class$java$util$HashMap, "defaultImplementations", JExpr._new(this$0.codeModel.ref((ObjectFactoryGenerator.class$java$util$HashMap == null) ? (ObjectFactoryGenerator.class$java$util$HashMap = ObjectFactoryGenerator.class$("java.util.HashMap")) : ObjectFactoryGenerator.class$java$util$HashMap)).arg(JExpr.lit(initialCapacity)).arg(JExpr.lit(0.75f))), this$0.objectFactory.init());
            this.this$0 = this$0;
        }
        
        public void add(final JDefinedClass _interface, final JClass _implementation) {
            super.add(_interface.dotclass(), JExpr.lit(_implementation.fullName()));
        }
        
        protected JMethod createNewMethod(final int uniqueId) {
            return this.this$0.objectFactory.method(20, this.this$0.codeModel.VOID, "__$$init$$" + uniqueId);
        }
    }
}
