// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.util.MultiplicityCounter;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.codemodel.JPackage;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.tools.xjc.grammar.ClassCandidateItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.ClassItem;
import java.util.HashSet;
import java.util.Set;
import com.sun.tools.xjc.grammar.BGMWalker;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

class ChoiceAnnotator
{
    private static PrintStream debug;
    private final AnnotatedGrammar grammar;
    private final CodeModelClassFactory classFactory;
    
    public static void annotate(final AnnotatedGrammar g, final AnnotatorController _controller) {
        final ChoiceAnnotator choiceAnnotator;
        final ChoiceAnnotator ann = choiceAnnotator = new ChoiceAnnotator(g, _controller);
        choiceAnnotator.getClass();
        g.visit((ExpressionVisitorVoid)new Finder(choiceAnnotator, (ChoiceAnnotator$1)null));
    }
    
    private ChoiceAnnotator(final AnnotatedGrammar g, final AnnotatorController _controller) {
        this.grammar = g;
        this.classFactory = new CodeModelClassFactory(_controller.getErrorReceiver());
    }
    
    static {
        ChoiceAnnotator.debug = null;
    }
    
    private class Finder extends BGMWalker
    {
        private final Set visited;
        
        private Finder(final ChoiceAnnotator this$0) {
            this.this$0 = this$0;
            this.visited = new HashSet();
        }
        
        public Object onClass(final ClassItem item) {
            if (this.visited.add(item)) {
                item.exp = item.exp.visit((ExpressionVisitorExpression)new Annotator(this.this$0, item, (ChoiceAnnotator$1)null));
                super.onClass(item);
            }
            return null;
        }
        
        public void onOther(final OtherExp exp) {
            if (exp instanceof ClassCandidateItem) {
                if (!this.visited.add(exp)) {
                    return;
                }
                final ClassCandidateItem cci = (ClassCandidateItem)exp;
                cci.exp = cci.exp.visit((ExpressionVisitorExpression)new Annotator(this.this$0, cci, (ChoiceAnnotator$1)null));
            }
            super.onOther(exp);
        }
    }
    
    private class Annotator extends ExpressionCloner
    {
        private final JPackage _package;
        private final String className;
        private int iota;
        
        private Annotator(final ChoiceAnnotator this$0, final ClassItem owner) {
            super(this$0.grammar.getPool());
            this.this$0 = this$0;
            this.iota = 0;
            this._package = owner.getTypeAsDefined()._package();
            this.className = owner.getType().name();
        }
        
        private Annotator(final ChoiceAnnotator this$0, final ClassCandidateItem owner) {
            super(this$0.grammar.getPool());
            this.this$0 = this$0;
            this.iota = 0;
            this._package = owner.targetPackage;
            this.className = owner.name;
        }
        
        public Expression onRef(final ReferenceExp exp) {
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            return (Expression)exp;
        }
        
        public Expression onOther(final OtherExp exp) {
            if (exp instanceof PrimitiveItem || exp instanceof InterfaceItem || exp instanceof ClassItem || exp instanceof IgnoreItem || exp instanceof SuperClassItem || exp instanceof FieldItem || exp instanceof ClassCandidateItem) {
                return (Expression)exp;
            }
            exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            return (Expression)exp;
        }
        
        public Expression onAttribute(final AttributeExp exp) {
            final Expression body = exp.exp.visit((ExpressionVisitorExpression)this);
            if (body == exp.exp) {
                return (Expression)exp;
            }
            return this.pool.createAttribute(exp.nameClass, body);
        }
        
        public Expression onElement(final ElementExp exp) {
            final Expression body = exp.contentModel.visit((ExpressionVisitorExpression)this);
            if (body == exp.contentModel) {
                return (Expression)exp;
            }
            return (Expression)new ElementPattern(exp.getNameClass(), body);
        }
        
        public Expression onChoice(final ChoiceExp exp) {
            final Expression[] b = exp.getChildren();
            final boolean[] complexBranch = new boolean[b.length];
            final boolean[] fieldlessBranch = new boolean[b.length];
            int numLiveBranch = 0;
            boolean bBranchWithField = false;
            final boolean[] bBranchWithPrimitive = { false };
            if (ChoiceAnnotator.debug != null) {
                ChoiceAnnotator.debug.println("Processing Choice: " + ExpressionPrinter.printContentModel((Expression)exp));
                ChoiceAnnotator.debug.println("checking each branch");
            }
            for (int i = 0; i < b.length; ++i) {
                final boolean[] hasChildFieldItem = { false };
                final int _i = i;
                final Multiplicity m = Multiplicity.calc(b[i], (MultiplicityCounter)new ChoiceAnnotator$1(this, complexBranch, _i, hasChildFieldItem, bBranchWithPrimitive));
                if (ChoiceAnnotator.debug != null) {
                    ChoiceAnnotator.debug.println("  Branch: " + ExpressionPrinter.printContentModel(b[i]));
                    ChoiceAnnotator.debug.println("    multiplicity:" + m + "  hasChildFieldItem:" + hasChildFieldItem[0]);
                }
                if (!m.isZero()) {
                    ++numLiveBranch;
                    if (!m.isAtMostOnce()) {
                        complexBranch[i] = true;
                    }
                    else if (!hasChildFieldItem[0]) {
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
                    if (fieldlessBranch[i] || complexBranch[i]) {
                        b[i] = b[i].visit((ExpressionVisitorExpression)this);
                    }
                }
                Expression expression = Expression.nullSet;
                for (int j = 0; j < b.length; ++j) {
                    expression = this.pool.createChoice(expression, b[j]);
                }
                return expression;
            }
            for (int i = 0; i < b.length; ++i) {
                if (complexBranch[i]) {
                    if (ChoiceAnnotator.debug != null) {
                        ChoiceAnnotator.debug.println("  Insert a wrapper class on: " + ExpressionPrinter.printContentModel((Expression)exp));
                    }
                    b[i] = (Expression)new ClassCandidateItem(this.this$0.classFactory, this.this$0.grammar, this._package, this.className + "Subordinate" + ++this.iota, null, b[i].visit((ExpressionVisitorExpression)this));
                }
            }
            Expression r = Expression.nullSet;
            for (int j = 0; j < b.length; ++j) {
                r = this.pool.createChoice(r, b[j]);
            }
            if (!bBranchWithField && !bBranchWithPrimitive[0]) {
                final JPackage pkg = this._package;
                String intfName = "I" + this.className + "Content";
                if (pkg.isDefined(intfName)) {
                    int cnt;
                    for (cnt = 2; pkg.isDefined(intfName + cnt); ++cnt) {}
                    intfName += cnt;
                }
                if (ChoiceAnnotator.debug != null) {
                    ChoiceAnnotator.debug.println("  Wrap it by an interface iem: " + intfName);
                    ChoiceAnnotator.debug.println("  " + ExpressionPrinter.printContentModel(r));
                }
                r = (Expression)this.this$0.grammar.createInterfaceItem(this.this$0.classFactory.createInterface(pkg, intfName, null), r, null);
            }
            return r;
        }
    }
}
