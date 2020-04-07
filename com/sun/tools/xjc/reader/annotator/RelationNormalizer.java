// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.tools.xjc.grammar.util.AnnotationRemover;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.xml.bind.JAXBAssertionError;
import java.util.Set;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.ClassItem;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.tools.xjc.grammar.FieldItem;
import java.util.HashSet;
import com.sun.tools.xjc.grammar.util.FieldMultiplicityCounter;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.ExpressionPool;

public final class RelationNormalizer
{
    private final AnnotatorController controller;
    private final ExpressionPool pool;
    private final AnnotatedGrammar grammar;
    
    private RelationNormalizer(final AnnotatorController _controller, final AnnotatedGrammar _grammar) {
        this.controller = _controller;
        this.pool = _grammar.getPool();
        this.grammar = _grammar;
    }
    
    public static void normalize(final AnnotatedGrammar grammar, final AnnotatorController controller) {
        final RelationNormalizer n = new RelationNormalizer(controller, grammar);
        final ClassItem[] classItems = grammar.getClasses();
        final InterfaceItem[] interfaceItems = grammar.getInterfaces();
        final RelationNormalizer relationNormalizer = n;
        relationNormalizer.getClass();
        final Pass1 pass1 = new Pass1(relationNormalizer, (RelationNormalizer$1)null);
        for (int i = 0; i < classItems.length; ++i) {
            pass1.process(classItems[i]);
        }
        for (int i = 0; i < interfaceItems.length; ++i) {
            interfaceItems[i].visit((ExpressionVisitorExpression)pass1);
        }
        for (int i = 0; i < classItems.length; ++i) {
            final FieldUse[] fieldUses = classItems[i].getDeclaredFieldUses();
            for (int j = 0; j < fieldUses.length; ++j) {
                fieldUses[j].multiplicity = FieldMultiplicityCounter.count(classItems[i].exp, fieldUses[j]);
                final Set possibleTypes = new HashSet();
                final FieldItem[] fields = fieldUses[j].items.toArray(new FieldItem[0]);
                for (int k = 0; k < fields.length; ++k) {
                    possibleTypes.add(fields[k].getType(grammar.codeModel));
                }
                fieldUses[j].type = TypeUtil.getCommonBaseType(grammar.codeModel, possibleTypes);
                if (fieldUses[j].isDelegated() && !fieldUses[j].multiplicity.isAtMostOnce()) {
                    controller.reportError(new Locator[] { classItems[i].locator }, Messages.format("Normalizer.DelegationMultiplicityMustBe1", (Object)fieldUses[j].name));
                }
            }
        }
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    private static boolean isInterface(final JType t) {
        return !t.isPrimitive() && ((JClass)t).isInterface();
    }
    
    private static boolean isClass(final Object exp) {
        return exp instanceof ClassItem;
    }
    
    private static boolean isSuperClass(final Object exp) {
        return exp instanceof SuperClassItem;
    }
    
    private static boolean isInterface(final Object exp) {
        return exp instanceof InterfaceItem;
    }
    
    private static boolean isField(final Object exp) {
        return exp instanceof FieldItem;
    }
    
    private static boolean isPrimitive(final Object exp) {
        return exp instanceof PrimitiveItem;
    }
    
    private static boolean isIgnore(final Object exp) {
        return exp instanceof IgnoreItem;
    }
    
    private class Pass1 implements ExpressionVisitorExpression
    {
        private final Set visitedClasses;
        private JavaItem parentItem;
        private Multiplicity multiplicity;
        
        private Pass1(final RelationNormalizer this$0) {
            this.this$0 = this$0;
            this.visitedClasses = new HashSet();
            this.parentItem = null;
            this.multiplicity = null;
        }
        
        public Expression onAttribute(final AttributeExp exp) {
            final Expression newContent = exp.exp.visit((ExpressionVisitorExpression)this);
            if (newContent != exp.exp) {
                return this.this$0.pool.createAttribute(exp.getNameClass(), newContent);
            }
            return (Expression)exp;
        }
        
        public Expression onElement(final ElementExp exp) {
            final Expression body = exp.contentModel.visit((ExpressionVisitorExpression)this);
            if (body == exp.contentModel) {
                return (Expression)exp;
            }
            return (Expression)new ElementPattern(exp.getNameClass(), body);
        }
        
        public Expression onMixed(final MixedExp exp) {
            return this.this$0.pool.createMixed(exp.exp.visit((ExpressionVisitorExpression)this));
        }
        
        public Expression onList(final ListExp exp) {
            return this.this$0.pool.createList(exp.exp.visit((ExpressionVisitorExpression)this));
        }
        
        public Expression onConcur(final ConcurExp exp) {
            throw new Error("concur is not supported");
        }
        
        public Expression onChoice(final ChoiceExp exp) {
            final Expression lhs = exp.exp1.visit((ExpressionVisitorExpression)this);
            final Multiplicity lhc = this.multiplicity;
            final Expression rhs = exp.exp2.visit((ExpressionVisitorExpression)this);
            final Multiplicity rhc = this.multiplicity;
            this.multiplicity = Multiplicity.choice(lhc, rhc);
            return this.this$0.pool.createChoice(lhs, rhs);
        }
        
        public Expression onSequence(final SequenceExp exp) {
            final Expression lhs = exp.exp1.visit((ExpressionVisitorExpression)this);
            final Multiplicity lhc = this.multiplicity;
            final Expression rhs = exp.exp2.visit((ExpressionVisitorExpression)this);
            final Multiplicity rhc = this.multiplicity;
            this.multiplicity = Multiplicity.group(lhc, rhc);
            return this.this$0.pool.createSequence(lhs, rhs);
        }
        
        public Expression onInterleave(final InterleaveExp exp) {
            final Expression lhs = exp.exp1.visit((ExpressionVisitorExpression)this);
            final Multiplicity lhc = this.multiplicity;
            final Expression rhs = exp.exp2.visit((ExpressionVisitorExpression)this);
            final Multiplicity rhc = this.multiplicity;
            this.multiplicity = Multiplicity.group(lhc, rhc);
            return this.this$0.pool.createInterleave(lhs, rhs);
        }
        
        public Expression onOneOrMore(final OneOrMoreExp exp) {
            final Expression p = this.this$0.pool.createOneOrMore(exp.exp.visit((ExpressionVisitorExpression)this));
            this.multiplicity = Multiplicity.oneOrMore(this.multiplicity);
            return p;
        }
        
        public Expression onEpsilon() {
            this.multiplicity = Multiplicity.zero;
            return Expression.epsilon;
        }
        
        public Expression onNullSet() {
            this.multiplicity = Multiplicity.zero;
            return Expression.nullSet;
        }
        
        public Expression onAnyString() {
            throw new Error();
        }
        
        public Expression onData(final DataExp exp) {
            this.multiplicity = Multiplicity.zero;
            return (Expression)exp;
        }
        
        public Expression onValue(final ValueExp exp) {
            this.multiplicity = Multiplicity.zero;
            return (Expression)exp;
        }
        
        public Expression onRef(final ReferenceExp exp) {
            return exp.exp.visit((ExpressionVisitorExpression)this);
        }
        
        public void process(final ClassItem ci) {
            if (!this.visitedClasses.add(ci)) {
                return;
            }
            final JavaItem oldParent = this.parentItem;
            this.parentItem = ci;
            ci.exp.visit((ExpressionVisitorExpression)this);
            this.parentItem = oldParent;
            if (ci.getSuperClass() != null) {
                this.process(ci.getSuperClass());
            }
            ci.removeDuplicateFieldUses();
            final FieldUse[] fus = ci.getDeclaredFieldUses();
            for (int i = 0; i < fus.length; ++i) {
                if (fus[i].isDelegated()) {
                    if (fus[i].items.size() != 1) {
                        throw new JAXBAssertionError();
                    }
                    final FieldItem fi = fus[i].items.iterator().next();
                    final JType t = fi.getType(this.this$0.grammar.codeModel);
                    if (t == this.this$0.grammar.codeModel.ref((RelationNormalizer.class$java$lang$Object == null) ? (RelationNormalizer.class$java$lang$Object = RelationNormalizer.class$("java.lang.Object")) : RelationNormalizer.class$java$lang$Object)) {
                        fus[i].disableDelegation();
                    }
                    else {
                        if (!RelationNormalizer.access$300(t)) {
                            this.this$0.controller.reportError(new Locator[] { ci.locator }, Messages.format("Normalizer.DelegationMustBeInterface", (Object)ci.name, (Object)t.fullName()));
                        }
                        ci.getTypeAsDefined()._implements((JClass)t);
                    }
                }
            }
        }
        
        public Expression onOther(final OtherExp exp) {
            if (!(exp instanceof JavaItem)) {
                exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
                return (Expression)exp;
            }
            if (RelationNormalizer.access$500((Object)this.parentItem)) {
                return exp.exp.visit((ExpressionVisitorExpression)this);
            }
            if (exp instanceof IgnoreItem) {
                exp.exp = AnnotationRemover.remove(exp.exp, this.this$0.pool);
                this.multiplicity = Multiplicity.zero;
                return (Expression)exp;
            }
            final JavaItem oldParent = this.parentItem;
            if (exp instanceof JavaItem) {
                this.sanityCheck(this.parentItem, (JavaItem)exp);
                if (RelationNormalizer.access$600((Object)this.parentItem) && RelationNormalizer.access$700((Object)exp)) {
                    final FieldItem fi = (FieldItem)exp;
                    ((ClassItem)this.parentItem).getOrCreateFieldUse(fi.name).items.add(fi);
                }
                if (RelationNormalizer.access$700((Object)this.parentItem) && exp instanceof TypeItem) {
                    final TypeItem ti = (TypeItem)exp;
                    final FieldItem fi2 = (FieldItem)this.parentItem;
                    try {
                        fi2.addType(ti);
                    }
                    catch (FieldItem.BadTypeException e) {
                        this.this$0.controller.reportError(new Locator[] { fi2.locator }, Messages.format("Normalizer.ConflictBetweenUserTypeAndActualType", (Object)fi2.name, (Object)e.getUserSpecifiedType().name(), (Object)ti.getType().name()));
                    }
                }
                if (exp instanceof ClassItem || !this.visitedClasses.add(exp)) {
                    this.multiplicity = this.getJavaItemMultiplicity(exp);
                    return (Expression)exp;
                }
                this.parentItem = (JavaItem)exp;
            }
            if (exp instanceof ExternalItem) {
                exp.exp = AnnotationRemover.remove(exp.exp, this.this$0.pool);
            }
            else {
                exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            }
            this.parentItem = oldParent;
            if (RelationNormalizer.access$800((Object)exp)) {
                final SuperClassItem sci = (SuperClassItem)exp;
                if (sci.definition == null) {
                    this.this$0.controller.reportError(new Expression[] { exp }, Messages.format("Normalizer.MissingSuperClassBody"));
                }
                else if (!this.multiplicity.isUnique()) {
                    this.this$0.controller.reportError(new Expression[] { exp, sci.definition }, Messages.format("Normalizer.BadSuperClassBodyMultiplicity", new Object[] { sci.definition.name }));
                }
            }
            if (RelationNormalizer.access$700((Object)exp)) {
                final FieldItem fi = (FieldItem)exp;
                if (fi.multiplicity == null) {
                    fi.multiplicity = this.multiplicity;
                }
                else {
                    RelationNormalizer.access$900(fi.multiplicity.includes(this.multiplicity));
                }
                if (!fi.hasTypes()) {
                    this.this$0.controller.reportError(new Locator[] { fi.locator }, Messages.format("Normalizer.EmptyProperty", new Object[] { fi.name }));
                    try {
                        fi.addType(this.this$0.grammar.createPrimitiveItem((Transducer)new IdentityTransducer(this.this$0.grammar.codeModel), (DatabindableDatatype)StringType.theInstance, this.this$0.pool.createData((XSDatatype)StringType.theInstance), fi.locator));
                    }
                    catch (FieldItem.BadTypeException ex) {}
                }
            }
            if (RelationNormalizer.access$1000((Object)exp)) {
                final InterfaceItem ii = (InterfaceItem)exp;
                if (!this.multiplicity.isAtMostOnce()) {
                    System.out.println(ExpressionPrinter.printContentModel(exp.exp));
                    this.this$0.controller.reportError(new Expression[] { ii }, Messages.format("Normalizer.BadInterfaceToClassMultiplicity", new Object[] { ii.name }));
                }
                return (Expression)exp;
            }
            this.multiplicity = this.getJavaItemMultiplicity(exp);
            return (Expression)exp;
        }
        
        private Multiplicity getJavaItemMultiplicity(final OtherExp item) {
            if (item instanceof IgnoreItem) {
                return Multiplicity.zero;
            }
            return Multiplicity.one;
        }
        
        private void sanityCheck(final JavaItem parent, final JavaItem child) {
            if (RelationNormalizer.access$800((Object)parent) && !RelationNormalizer.access$600((Object)child)) {
                this.this$0.controller.reportError(new Expression[] { parent, child }, Messages.format("Normalizer.BadSuperClassUse"));
                return;
            }
            if (RelationNormalizer.access$1100((Object)parent)) {
                throw new Error("internal error: use of primitive-" + child + " relation.");
            }
            if ((RelationNormalizer.access$700((Object)parent) && (RelationNormalizer.access$800((Object)child) || RelationNormalizer.access$700((Object)child))) || (RelationNormalizer.access$1000((Object)parent) && (RelationNormalizer.access$800((Object)child) || RelationNormalizer.access$700((Object)child) || RelationNormalizer.access$1100((Object)child)))) {
                this.this$0.controller.reportError(new Expression[] { parent, child }, Messages.format("Normalizer.BadItemUse", (Object)parent, (Object)child));
                return;
            }
            if (RelationNormalizer.access$600((Object)parent) && child instanceof TypeItem) {
                throw new Error("internal error. C-C/C-I/C-P relation " + ((ClassItem)parent).getTypeAsDefined().name() + " " + child.toString() + " " + ExpressionPrinter.printContentModel((Expression)parent));
            }
        }
    }
}
