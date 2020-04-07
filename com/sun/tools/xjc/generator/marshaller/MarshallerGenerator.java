// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JConditional;
import java.util.Map;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import java.util.HashMap;
import com.sun.codemodel.JType;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;

public class MarshallerGenerator
{
    private final AnnotatedGrammar grammar;
    private final GeneratorContext context;
    
    public static void generate(final AnnotatedGrammar grammar, final GeneratorContext context, final Options opt) {
        new MarshallerGenerator(grammar, context, opt);
    }
    
    private MarshallerGenerator(final AnnotatedGrammar _grammar, final GeneratorContext _context, final Options _opt) {
        this.grammar = _grammar;
        this.context = _context;
        final ClassItem[] cs = this.grammar.getClasses();
        for (int i = 0; i < cs.length; ++i) {
            this.generate(this.context.getClassContext(cs[i]));
        }
    }
    
    private void generate(final ClassContext cc) {
        cc.implClass._implements(this.context.getRuntime((MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializable == null) ? (MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializable = class$("com.sun.tools.xjc.runtime.XMLSerializable")) : MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializable));
        this.generateMethodSkeleton(cc, "serializeBody").bodyPass.build(cc.target.exp);
        this.generateMethodSkeleton(cc, "serializeAttributes").attPass.build(cc.target.exp);
        this.generateMethodSkeleton(cc, "serializeURIs").uriPass.build(cc.target.exp);
        this.processID(cc);
    }
    
    private void processID(final ClassContext cc) {
        cc.target.exp.visit((ExpressionVisitorVoid)new MarshallerGenerator$1(this, cc));
    }
    
    private Context generateMethodSkeleton(final ClassContext cc, final String methodName) {
        final JMethod p = cc.implClass.method(1, this.grammar.codeModel.VOID, methodName);
        final JVar $serializer = p.param(this.context.getRuntime((MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializer == null) ? (MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializer = class$("com.sun.tools.xjc.runtime.XMLSerializer")) : MarshallerGenerator.class$com$sun$tools$xjc$runtime$XMLSerializer), "context");
        p._throws((MarshallerGenerator.class$org$xml$sax$SAXException == null) ? (MarshallerGenerator.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : MarshallerGenerator.class$org$xml$sax$SAXException);
        final JBlock body = p.body();
        final FieldUse[] uses = cc.target.getDeclaredFieldUses();
        final Map fieldMarshallers = new HashMap();
        for (int i = 0; i < uses.length; ++i) {
            fieldMarshallers.put(uses[i], this.context.getField(uses[i]).createMarshaller(body, Integer.toString(i + 1)));
            if (uses[i].multiplicity.isUnique() && uses[i].type.isPrimitive()) {
                final JExpression hasSetValue = this.context.getField(uses[i]).hasSetValue();
                if (hasSetValue != null) {
                    final JConditional cond = body._if(hasSetValue.not());
                    cond._then().invoke($serializer, "reportError").arg(this.grammar.codeModel.ref((MarshallerGenerator.class$com$sun$xml$bind$serializer$Util == null) ? (MarshallerGenerator.class$com$sun$xml$bind$serializer$Util = class$("com.sun.xml.bind.serializer.Util")) : MarshallerGenerator.class$com$sun$xml$bind$serializer$Util).staticInvoke("createMissingObjectError").arg(JExpr._this()).arg(JExpr.lit(uses[i].name)));
                }
            }
        }
        return new Context(this.context, this.grammar.getPool(), cc.target, body, $serializer, fieldMarshallers);
    }
}
