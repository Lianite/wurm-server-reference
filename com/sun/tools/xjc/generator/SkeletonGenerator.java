// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import java.text.ParseException;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;
import com.sun.tools.xjc.generator.field.DefaultFieldRendererFactory;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import java.util.Iterator;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JDefinedClass;
import org.xml.sax.Locator;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import java.net.URL;
import com.sun.codemodel.fmt.JStaticJavaFile;
import java.io.IOException;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JResourceFile;
import com.sun.codemodel.fmt.JStaticFile;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.cls.ImplStructureStrategy;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.generator.cls.PararellStructureStrategy;
import java.util.HashMap;
import com.sun.tools.xjc.AbortException;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.util.Map;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.util.CodeModelClassFactory;

public class SkeletonGenerator implements GeneratorContext
{
    private final CodeModelClassFactory codeModelClassFactory;
    private final ErrorReceiver errorReceiver;
    private final Options opts;
    private final Map packageContexts;
    private final Map classContexts;
    private final AnnotatedGrammar grammar;
    private final JCodeModel codeModel;
    private final Map runtimeClasses;
    private final Map fields;
    private final LookupTableBuilder lookupTableBuilder;
    
    public static GeneratorContext generate(final AnnotatedGrammar grammar, final Options opt, final ErrorReceiver _errorReceiver) {
        try {
            return (GeneratorContext)new SkeletonGenerator(grammar, opt, _errorReceiver);
        }
        catch (AbortException e) {
            return null;
        }
    }
    
    private SkeletonGenerator(final AnnotatedGrammar _grammar, final Options opt, final ErrorReceiver _errorReceiver) {
        this.packageContexts = new HashMap();
        this.classContexts = new HashMap();
        this.runtimeClasses = new HashMap();
        this.fields = new HashMap();
        this.grammar = _grammar;
        this.opts = opt;
        this.codeModel = this.grammar.codeModel;
        this.errorReceiver = _errorReceiver;
        this.codeModelClassFactory = new CodeModelClassFactory(this.errorReceiver);
        this.populateTransducers(this.grammar);
        this.generateStaticRuntime();
        final JPackage[] packages = this.grammar.getUsedPackages();
        if (packages.length != 0) {
            this.lookupTableBuilder = (LookupTableBuilder)new LookupTableCache((LookupTableBuilder)new LookupTableInterner((LookupTableBuilder)new LookupTableFactory(packages[0].subPackage("impl"))));
        }
        else {
            this.lookupTableBuilder = null;
        }
        for (int i = 0; i < packages.length; ++i) {
            final JPackage pkg = packages[i];
            this.packageContexts.put(pkg, new PackageContext((GeneratorContext)this, this.grammar, opt, pkg));
        }
        final ClassItem[] items = this.grammar.getClasses();
        final ImplStructureStrategy strategy = (ImplStructureStrategy)new PararellStructureStrategy(this.codeModelClassFactory);
        for (int j = 0; j < items.length; ++j) {
            this.classContexts.put(items[j], new ClassContext((GeneratorContext)this, strategy, items[j]));
        }
        for (int j = 0; j < items.length; ++j) {
            this.generateClass(this.getClassContext(items[j]));
        }
        for (int j = 0; j < items.length; ++j) {
            final ClassContext cc = this.getClassContext(items[j]);
            final ClassItem superClass = cc.target.getSuperClass();
            if (superClass != null) {
                cc.implClass._extends(this.getClassContext(superClass).implRef);
            }
            else if (this.grammar.rootClass != null) {
                cc.implClass._extends(this.grammar.rootClass);
            }
            final FieldUse[] fus = items[j].getDeclaredFieldUses();
            for (int k = 0; k < fus.length; ++k) {
                if (fus[k].isDelegated()) {
                    this.generateDelegation(items[j].locator, cc.implClass, (JClass)fus[k].type, this.getField(fus[k]));
                }
            }
        }
    }
    
    public AnnotatedGrammar getGrammar() {
        return this.grammar;
    }
    
    public JCodeModel getCodeModel() {
        return this.codeModel;
    }
    
    public LookupTableBuilder getLookupTableBuilder() {
        return this.lookupTableBuilder;
    }
    
    private JPackage getRuntimePackage() {
        if (this.opts.runtimePackage != null) {
            return this.codeModel._package(this.opts.runtimePackage);
        }
        final JPackage[] pkgs = this.grammar.getUsedPackages();
        if (pkgs.length == 0) {
            return null;
        }
        JPackage pkg = pkgs[0];
        if (pkg.name().startsWith("org.w3") && pkgs.length > 1) {
            pkg = this.grammar.getUsedPackages()[1];
        }
        return pkg.subPackage("impl.runtime");
    }
    
    private void generateStaticRuntime() {
        if (!this.opts.generateRuntime) {
            return;
        }
        final JPackage pkg = this.getRuntimePackage();
        final String prefix = "com/sun/tools/xjc/runtime/";
        if (pkg == null) {
            return;
        }
        final BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("com/sun/tools/xjc/runtime/filelist")));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                final String name = line.substring(12);
                final boolean forU = line.charAt(2) == 'x';
                final boolean forW = line.charAt(4) == 'x';
                final boolean forM = line.charAt(6) == 'x';
                final boolean forV = line.charAt(8) == 'x';
                final boolean must = line.charAt(10) == 'x';
                if (!must && (!forU || !this.opts.generateUnmarshallingCode) && (!forW || !this.opts.generateValidatingUnmarshallingCode) && (!forM || !this.opts.generateMarshallingCode) && (!forV || !this.opts.generateValidationCode)) {
                    continue;
                }
                if (name.endsWith(".java")) {
                    final String className = name.substring(0, name.length() - 5);
                    final Class cls = Class.forName("com/sun/tools/xjc/runtime/".replace('/', '.') + className);
                    this.addRuntime(cls);
                }
                else {
                    final JStaticFile s = new JStaticFile("com/sun/tools/xjc/runtime/" + name);
                    pkg.addResourceFile(s);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new JAXBAssertionError();
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
            throw new JAXBAssertionError();
        }
    }
    
    public JClass getRuntime(final Class clazz) {
        final JClass r = this.runtimeClasses.get(clazz);
        if (r != null) {
            return r;
        }
        return this.addRuntime(clazz);
    }
    
    private JClass addRuntime(final Class runtimeClass) {
        final JPackage pkg = this.getRuntimePackage();
        final String shortName = this.getShortName(runtimeClass.getName());
        if (!pkg.hasResourceFile(shortName + ".java")) {
            final URL res = runtimeClass.getResource(shortName + ".java");
            if (res == null) {
                throw new JAXBAssertionError("Unable to load source code of " + runtimeClass.getName() + " as a resource");
            }
            final JStaticJavaFile sjf = new JStaticJavaFile(pkg, shortName, res, (JStaticJavaFile.LineFilter)new PreProcessor(this, (SkeletonGenerator$1)null));
            if (this.opts.generateRuntime) {
                pkg.addResourceFile(sjf);
            }
            this.runtimeClasses.put(runtimeClass, sjf.getJClass());
        }
        return this.getRuntime(runtimeClass);
    }
    
    private String getShortName(final String name) {
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    public ErrorReceiver getErrorReceiver() {
        return this.errorReceiver;
    }
    
    public CodeModelClassFactory getClassFactory() {
        return this.codeModelClassFactory;
    }
    
    public PackageContext getPackageContext(final JPackage p) {
        return this.packageContexts.get(p);
    }
    
    public ClassContext getClassContext(final ClassItem ci) {
        return this.classContexts.get(ci);
    }
    
    public PackageContext[] getAllPackageContexts() {
        return (PackageContext[])this.packageContexts.values().toArray(new PackageContext[this.packageContexts.size()]);
    }
    
    public FieldRenderer getField(final FieldUse fu) {
        return this.fields.get(fu);
    }
    
    private void generateClass(final ClassContext cc) {
        if (this.grammar.serialVersionUID != null) {
            cc.implClass._implements((SkeletonGenerator.class$java$io$Serializable == null) ? (SkeletonGenerator.class$java$io$Serializable = class$("java.io.Serializable")) : SkeletonGenerator.class$java$io$Serializable);
            cc.implClass.field(28, this.codeModel.LONG, "serialVersionUID", JExpr.lit(this.grammar.serialVersionUID));
        }
        if (cc.target.exp instanceof NameClassAndExpression) {
            final XmlNameStoreAlgorithm nsa = XmlNameStoreAlgorithm.get((NameClassAndExpression)cc.target.exp);
            nsa.populate(cc);
            if (cc.target.exp instanceof ElementExp) {
                cc.implClass._implements((SkeletonGenerator.class$com$sun$xml$bind$RIElement == null) ? (SkeletonGenerator.class$com$sun$xml$bind$RIElement = class$("com.sun.xml.bind.RIElement")) : SkeletonGenerator.class$com$sun$xml$bind$RIElement);
                cc.implClass.method(1, (SkeletonGenerator.class$java$lang$String == null) ? (SkeletonGenerator.class$java$lang$String = class$("java.lang.String")) : SkeletonGenerator.class$java$lang$String, "____jaxb_ri____getNamespaceURI").body()._return(nsa.getNamespaceURI());
                cc.implClass.method(1, (SkeletonGenerator.class$java$lang$String == null) ? (SkeletonGenerator.class$java$lang$String = class$("java.lang.String")) : SkeletonGenerator.class$java$lang$String, "____jaxb_ri____getLocalName").body()._return(nsa.getLocalPart());
            }
        }
        cc.implClass._implements((SkeletonGenerator.class$com$sun$xml$bind$JAXBObject == null) ? (SkeletonGenerator.class$com$sun$xml$bind$JAXBObject = class$("com.sun.xml.bind.JAXBObject")) : SkeletonGenerator.class$com$sun$xml$bind$JAXBObject);
        final FieldUse[] fus = cc.target.getDeclaredFieldUses();
        for (int j = 0; j < fus.length; ++j) {
            this.generateFieldDecl(cc, fus[j]);
        }
        if (cc.target.hasGetContentMethod) {
            this.generateChoiceContentField(cc);
        }
        cc._package.objectFactoryGenerator.populate(cc);
        cc._package.versionGenerator.generateVersionReference(cc);
    }
    
    private void generateChoiceContentField(final ClassContext cc) {
        final FieldUse[] fus = cc.target.getDeclaredFieldUses();
        final JType[] types = new JType[fus.length];
        for (int i = 0; i < fus.length; ++i) {
            final FieldRenderer fr = this.getField(fus[i]);
            types[i] = fr.getValueType();
        }
        final JType returnType = TypeUtil.getCommonBaseType(this.codeModel, types);
        final MethodWriter helper = cc.createMethodWriter();
        final JMethod $get = helper.declareMethod(returnType, "getContent");
        for (int j = 0; j < fus.length; ++j) {
            final FieldRenderer fr2 = this.getField(fus[j]);
            final JBlock then = $get.body()._if(fr2.hasSetValue())._then();
            then._return(fr2.getValue());
        }
        $get.body()._return(JExpr._null());
        final JMethod $isSet = helper.declareMethod((JType)this.codeModel.BOOLEAN, "isSetContent");
        JExpression exp = JExpr.FALSE;
        for (int k = 0; k < fus.length; ++k) {
            exp = exp.cor(this.getField(fus[k]).hasSetValue());
        }
        $isSet.body()._return(exp);
        final JMethod $unset = helper.declareMethod((JType)this.codeModel.VOID, "unsetContent");
        for (int l = 0; l < fus.length; ++l) {
            this.getField(fus[l]).unsetValues($unset.body());
        }
        for (int l = 0; l < fus.length; ++l) {
            final FieldRenderer fr3 = this.getField(fus[l]);
            for (int m = 0; m < fus.length; ++m) {
                if (l != m) {
                    final FieldRenderer fr4 = this.getField(fus[m]);
                    fr4.unsetValues(fr3.getOnSetEventHandler());
                }
            }
        }
    }
    
    private void generateDelegation(final Locator errorSource, final JDefinedClass impl, final JClass _intf, final FieldRenderer fr) {
        final JDefinedClass intf = (JDefinedClass)_intf;
        Iterator itr = intf._implements();
        while (itr.hasNext()) {
            this.generateDelegation(errorSource, impl, itr.next(), fr);
        }
        itr = intf.methods();
        while (itr.hasNext()) {
            final JMethod m = itr.next();
            if (impl.getMethod(m.name(), m.listParamTypes()) != null) {
                this.errorReceiver.error(errorSource, Messages.format("SkeletonGenerator.MethodCollision", m.name(), impl.fullName(), intf.fullName()));
            }
            final JMethod n = impl.method(1, m.type(), m.name());
            final JVar[] mp = m.listParams();
            final JInvocation inv = fr.getValue().invoke(m);
            if (m.type() == this.codeModel.VOID) {
                n.body().add(inv);
            }
            else {
                n.body()._return(inv);
            }
            for (int j = 0; j < mp.length; ++j) {
                inv.arg(n.param(mp[j].type(), mp[j].name()));
            }
        }
    }
    
    private void populateTransducers(final AnnotatedGrammar grammar) {
        final PrimitiveItem[] pis = grammar.getPrimitives();
        for (int i = 0; i < pis.length; ++i) {
            pis[i].xducer.populate(grammar, (GeneratorContext)this);
        }
    }
    
    private FieldRenderer generateFieldDecl(final ClassContext cc, final FieldUse fu) {
        FieldRendererFactory frf = fu.getRealization();
        if (frf == null) {
            frf = (FieldRendererFactory)new DefaultFieldRendererFactory(this.codeModel);
        }
        final FieldRenderer field = frf.create(cc, fu);
        field.generate();
        this.fields.put(fu, field);
        return field;
    }
    
    private class PreProcessor extends PreProcessingLineFilter
    {
        private PreProcessor(final SkeletonGenerator this$0) {
            this.this$0 = this$0;
        }
        
        protected boolean getVar(final char variableName) throws ParseException {
            switch (variableName) {
                case 'U': {
                    return this.this$0.opts.generateUnmarshallingCode;
                }
                case 'V': {
                    return this.this$0.opts.generateValidationCode;
                }
                case 'M': {
                    return this.this$0.opts.generateMarshallingCode;
                }
                case 'W': {
                    return this.this$0.opts.generateValidatingUnmarshallingCode;
                }
                default: {
                    throw new ParseException("undefined variable " + variableName, -1);
                }
            }
        }
    }
}
