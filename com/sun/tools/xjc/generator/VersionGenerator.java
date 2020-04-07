// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JCodeModel;

final class VersionGenerator
{
    private final JCodeModel codeModel;
    private final GeneratorContext context;
    private final JPackage targetPackage;
    public final JDefinedClass versionClass;
    
    VersionGenerator(final GeneratorContext _context, final AnnotatedGrammar _grammar, final JPackage _pkg) {
        this.context = _context;
        this.codeModel = _grammar.codeModel;
        this.targetPackage = _pkg;
        this.versionClass = this.context.getClassFactory().createClass(this.targetPackage, "JAXBVersion", null);
        this.generate();
    }
    
    private void generate() {
        this.versionClass.field(25, this.codeModel.ref((VersionGenerator.class$java$lang$String == null) ? (VersionGenerator.class$java$lang$String = class$("java.lang.String")) : VersionGenerator.class$java$lang$String), "version", JExpr.lit(Messages.format("VersionGenerator.versionField")));
    }
    
    void generateVersionReference(final JDefinedClass impl) {
        impl.field(25, this.codeModel.ref((VersionGenerator.class$java$lang$Class == null) ? (VersionGenerator.class$java$lang$Class = class$("java.lang.Class")) : VersionGenerator.class$java$lang$Class), "version", this.versionClass.dotclass());
    }
    
    void generateVersionReference(final ClassContext cc) {
        this.generateVersionReference(cc.implClass);
    }
}
