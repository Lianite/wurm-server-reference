// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.tools.xjc.grammar.InterfaceItem;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.util.MultiplicityCounter;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.ReferenceExp;
import java.util.Stack;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.tools.xjc.util.Util;
import java.util.HashMap;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.util.Map;
import java.io.PrintStream;

class FieldItemAnnotation
{
    private static PrintStream debug;
    private final AnnotatorController controller;
    private final Map annotatedRefs;
    
    public static void annotate(final AnnotatedGrammar g, final AnnotatorController controller) {
        final FieldItemAnnotation ann = new FieldItemAnnotation(controller);
        final ClassItem[] classes = g.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            if (FieldItemAnnotation.debug != null) {
                FieldItemAnnotation.debug.println(" adding field item for " + classes[i].getTypeAsDefined().name());
            }
            final ClassItem classItem = classes[i];
            final Expression exp = classes[i].exp;
            final FieldItemAnnotation fieldItemAnnotation = ann;
            fieldItemAnnotation.getClass();
            classItem.exp = exp.visit((ExpressionVisitorExpression)new Annotator(fieldItemAnnotation, g, classes[i], (FieldItemAnnotation$1)null));
        }
    }
    
    private FieldItemAnnotation(final AnnotatorController _controller) {
        this.annotatedRefs = new HashMap();
        this.controller = _controller;
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new Error();
        }
    }
    
    static {
        FieldItemAnnotation.debug = ((Util.getSystemProperty((FieldItemAnnotation.class$com$sun$tools$xjc$reader$annotator$FieldItemAnnotation == null) ? (FieldItemAnnotation.class$com$sun$tools$xjc$reader$annotator$FieldItemAnnotation = class$("com.sun.tools.xjc.reader.annotator.FieldItemAnnotation")) : FieldItemAnnotation.class$com$sun$tools$xjc$reader$annotator$FieldItemAnnotation, "debug") != null) ? System.err : null);
    }
    
    private class Annotator extends ExpressionCloner
    {
        private final Stack names;
        private final ClassItem owner;
        
        private Annotator(final FieldItemAnnotation this$0, final AnnotatedGrammar g, final ClassItem owner) {
            super(g.getPool());
            this.this$0 = this$0;
            this.names = new Stack();
            this.owner = owner;
        }
        
        public Expression onRef(final ReferenceExp exp) {
            Expression r = this.this$0.annotatedRefs.get(exp);
            if (r != null) {
                return r;
            }
            boolean pushed = false;
            if (exp.name != null && this.owner.exp != exp && !(exp.exp instanceof ClassItem)) {
                this.names.push(exp.name);
                pushed = true;
            }
            r = exp.exp.visit((ExpressionVisitorExpression)this);
            if (pushed) {
                this.this$0.annotatedRefs.put(exp, r);
            }
            r.visit((ExpressionVisitorVoid)new FieldItemAnnotation$1(this));
            if (pushed) {
                this.names.pop();
            }
            return r;
        }
        
        public Expression onOther(final OtherExp exp) {
            if (exp instanceof TypeItem) {
                return (Expression)new FieldItem(this.decideName((Expression)exp), (Expression)exp, ((TypeItem)exp).locator);
            }
            if (exp instanceof IgnoreItem || exp instanceof SuperClassItem || exp instanceof FieldItem) {
                return (Expression)exp;
            }
            FieldItemAnnotation.access$200(!(exp instanceof JavaItem));
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            return (Expression)exp;
        }
        
        public Expression onAttribute(final AttributeExp exp) {
            final Expression body = this.visitXMLItemContent((NameClassAndExpression)exp);
            if (body == exp.exp) {
                return (Expression)exp;
            }
            return this.pool.createAttribute(exp.nameClass, body);
        }
        
        public Expression onElement(final ElementExp exp) {
            final Expression body = this.visitXMLItemContent((NameClassAndExpression)exp);
            if (body == exp.contentModel) {
                return (Expression)exp;
            }
            return (Expression)new ElementPattern(exp.getNameClass(), body);
        }
        
        private Expression visitXMLItemContent(final NameClassAndExpression exp) {
            String name = null;
            final NameClass nc = exp.getNameClass();
            if (nc instanceof SimpleNameClass) {
                name = ((SimpleNameClass)nc).localName;
            }
            if (exp == this.owner.exp) {
                name = null;
            }
            if (name != null) {
                this.names.push(name);
            }
            final Expression body = exp.getContentModel().visit((ExpressionVisitorExpression)this);
            if (name != null) {
                this.names.pop();
            }
            return body;
        }
        
        public Expression onChoice(final ChoiceExp exp) {
            final Expression[] b = exp.getChildren();
            final boolean[] fieldlessBranch = new boolean[b.length];
            int numLiveBranch = 0;
            boolean bBranchWithField = false;
            final boolean[] bBranchWithPrimitive = { false };
            if (FieldItemAnnotation.debug != null) {
                FieldItemAnnotation.debug.println("Processing Choice: " + ExpressionPrinter.printContentModel((Expression)exp));
                FieldItemAnnotation.debug.println("checking each branch");
            }
            for (int i = 0; i < b.length; ++i) {
                final boolean[] hasChildFieldItem = { false };
                final Multiplicity m = Multiplicity.calc(b[i], (MultiplicityCounter)new FieldItemAnnotation.FieldItemAnnotation$2(this, hasChildFieldItem, bBranchWithPrimitive));
                if (FieldItemAnnotation.debug != null) {
                    FieldItemAnnotation.debug.println("  Branch: " + ExpressionPrinter.printContentModel(b[i]));
                    FieldItemAnnotation.debug.println("    multiplicity:" + m + "  hasChildFieldItem:" + hasChildFieldItem[0]);
                }
                if (!m.isZero()) {
                    ++numLiveBranch;
                    if (!hasChildFieldItem[0]) {
                        fieldlessBranch[i] = true;
                    }
                    else {
                        bBranchWithField = true;
                        b[i] = b[i].visit((ExpressionVisitorExpression)this);
                    }
                }
            }
            if (numLiveBranch <= 1) {
                for (int i = 0; i < b.length; ++i) {
                    if (fieldlessBranch[i]) {
                        b[i] = b[i].visit((ExpressionVisitorExpression)this);
                    }
                }
                Expression expression = Expression.nullSet;
                for (int j = 0; j < b.length; ++j) {
                    expression = this.pool.createChoice(expression, b[j]);
                }
                return expression;
            }
            final String fieldName = this.decideName((Expression)exp);
            Expression r = Expression.nullSet;
            for (int k = 0; k < b.length; ++k) {
                if (bBranchWithField && fieldlessBranch[k]) {
                    b[k] = (Expression)new FieldItem(fieldName, b[k], null);
                }
                r = this.pool.createChoice(r, b[k]);
            }
            if (!bBranchWithField) {
                r = (Expression)new FieldItem(fieldName, r, null);
            }
            return r;
        }
        
        private String decideName(final Expression hint) {
            String name = null;
            if (!this.names.isEmpty()) {
                name = this.names.peek();
            }
            if (name == null && hint != null) {
                if (hint instanceof NameClassAndExpression) {
                    final NameClass nc = ((NameClassAndExpression)hint).getNameClass();
                    if (nc instanceof SimpleNameClass) {
                        name = ((SimpleNameClass)nc).localName;
                    }
                }
                if (hint instanceof ClassItem) {
                    name = ((ClassItem)hint).name;
                }
                if (hint instanceof InterfaceItem) {
                    name = ((InterfaceItem)hint).name;
                }
            }
            if (name == null) {
                name = "Content";
            }
            return this.this$0.controller.getNameConverter().toPropertyName(name);
        }
    }
}
