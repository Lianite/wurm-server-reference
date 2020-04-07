// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.validator;

import com.sun.msv.grammar.ExpressionVisitorExpression;
import java.io.ObjectOutputStream;
import com.sun.codemodel.JFieldVar;
import com.sun.msv.grammar.Expression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.util.Util;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.io.Writer;
import java.io.StringWriter;
import org.xml.sax.SAXException;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.Grammar;
import org.xml.sax.DocumentHandler;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.msv.writer.relaxng.RELAXNGWriter;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

public class ValidatorGenerator
{
    private static final PrintStream debug;
    
    public static void generate(final AnnotatedGrammar grammar, final GeneratorContext context, final Options opt) {
        final JCodeModel codeModel = grammar.codeModel;
        final ClassItem[] cis = grammar.getClasses();
        for (int i = 0; i < cis.length; ++i) {
            final ClassItem ci = cis[i];
            final JDefinedClass cls = context.getClassContext(ci).implClass;
            cls._implements(context.getRuntime((ValidatorGenerator.class$com$sun$tools$xjc$runtime$ValidatableObject == null) ? (ValidatorGenerator.class$com$sun$tools$xjc$runtime$ValidatableObject = class$("com.sun.tools.xjc.runtime.ValidatableObject")) : ValidatorGenerator.class$com$sun$tools$xjc$runtime$ValidatableObject));
            final JMethod method = cls.method(1, (ValidatorGenerator.class$java$lang$Class == null) ? (ValidatorGenerator.class$java$lang$Class = class$("java.lang.Class")) : ValidatorGenerator.class$java$lang$Class, "getPrimaryInterface");
            method.body()._return(((JClass)ci.getType()).dotclass());
            final ExpressionPool pool = new ExpressionPool();
            final Expression fragment = createSchemaFragment(ci, pool);
            if (opt.debugMode && opt.verbose) {
                System.out.println(ci.getType().fullName());
                System.out.println(ExpressionPrinter.printFragment(fragment));
                System.out.println();
            }
            if (ValidatorGenerator.debug != null) {
                ValidatorGenerator.debug.println("---- schema fragment for " + ci.name + " ----");
                try {
                    final TREXGrammar g = new TREXGrammar(pool);
                    g.exp = fragment;
                    final RELAXNGWriter w = new RELAXNGWriter();
                    final OutputFormat format = new OutputFormat("xml", null, true);
                    format.setIndent(1);
                    w.setDocumentHandler((DocumentHandler)new XMLSerializer(ValidatorGenerator.debug, format));
                    w.write((Grammar)g);
                }
                catch (SAXException e) {
                    e.printStackTrace();
                    throw new JAXBAssertionError();
                }
            }
            StringWriter sw = new StringWriter();
            saveFragmentTo(fragment, pool, (OutputStream)new StringOutputStream((Writer)sw));
            String deserializeMethodName = "deserialize";
            if (sw.getBuffer().length() > 32768) {
                sw = new StringWriter();
                try {
                    saveFragmentTo(fragment, pool, (OutputStream)new GZIPOutputStream((OutputStream)new StringOutputStream((Writer)sw)));
                    deserializeMethodName = "deserializeCompressed";
                }
                catch (IOException e2) {
                    throw new InternalError(e2.getMessage());
                }
            }
            final JFieldVar $schemaFragment = cls.field(20, (ValidatorGenerator.class$com$sun$msv$grammar$Grammar == null) ? (ValidatorGenerator.class$com$sun$msv$grammar$Grammar = class$("com.sun.msv.grammar.Grammar")) : ValidatorGenerator.class$com$sun$msv$grammar$Grammar, "schemaFragment");
            JExpression encodedFragment;
            if (Util.getSystemProperty((ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator == null) ? (ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator = class$("com.sun.tools.xjc.generator.validator.ValidatorGenerator")) : ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator, "noSplit") != null) {
                encodedFragment = JExpr.lit(sw.toString());
            }
            else {
                final int len = sw.getBuffer().length();
                final StringBuffer buf = new StringBuffer(len);
                for (int j = 0; j < len; j += 60) {
                    buf.append('\n');
                    if (j != 0) {
                        buf.append('+');
                    }
                    else {
                        buf.append(' ');
                    }
                    buf.append(JExpr.quotify('\"', sw.getBuffer().substring(j, Math.min(j + 60, len))));
                }
                encodedFragment = JExpr.direct(buf.toString());
            }
            final JMethod m = cls.method(1, (ValidatorGenerator.class$com$sun$msv$verifier$DocumentDeclaration == null) ? (ValidatorGenerator.class$com$sun$msv$verifier$DocumentDeclaration = class$("com.sun.msv.verifier.DocumentDeclaration")) : ValidatorGenerator.class$com$sun$msv$verifier$DocumentDeclaration, "createRawValidator");
            m.body()._if($schemaFragment.eq(JExpr._null()))._then().assign($schemaFragment, codeModel.ref((ValidatorGenerator.class$com$sun$xml$bind$validator$SchemaDeserializer == null) ? (ValidatorGenerator.class$com$sun$xml$bind$validator$SchemaDeserializer = class$("com.sun.xml.bind.validator.SchemaDeserializer")) : ValidatorGenerator.class$com$sun$xml$bind$validator$SchemaDeserializer).staticInvoke(deserializeMethodName).arg(encodedFragment));
            m.body()._return(JExpr._new(codeModel.ref((ValidatorGenerator.class$com$sun$msv$verifier$regexp$REDocumentDeclaration == null) ? (ValidatorGenerator.class$com$sun$msv$verifier$regexp$REDocumentDeclaration = class$("com.sun.msv.verifier.regexp.REDocumentDeclaration")) : ValidatorGenerator.class$com$sun$msv$verifier$regexp$REDocumentDeclaration)).arg($schemaFragment));
        }
    }
    
    private static void saveFragmentTo(final Expression fragment, final ExpressionPool pool, final OutputStream os) {
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(fragment);
            oos.writeObject(pool);
            oos.close();
        }
        catch (IOException e) {
            throw new JAXBAssertionError((Throwable)e);
        }
    }
    
    private static Expression createSchemaFragment(final ClassItem ci, final ExpressionPool pool) {
        Expression exp;
        if (ci.agm.exp == null) {
            exp = ci.exp;
        }
        else {
            exp = ci.agm.exp;
        }
        exp = exp.visit((ExpressionVisitorExpression)new SchemaFragmentBuilder(new ExpressionPool()));
        return exp.visit((ExpressionVisitorExpression)new ValidatorGenerator$1(pool));
    }
    
    static {
        ValidatorGenerator.debug = ((Util.getSystemProperty((ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator == null) ? (ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator = class$("com.sun.tools.xjc.generator.validator.ValidatorGenerator")) : ValidatorGenerator.class$com$sun$tools$xjc$generator$validator$ValidatorGenerator, "debug") != null) ? System.out : null);
    }
}
