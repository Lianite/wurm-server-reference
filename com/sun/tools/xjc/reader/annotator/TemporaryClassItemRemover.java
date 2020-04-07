// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import java.util.ArrayList;
import java.util.List;
import com.sun.tools.xjc.grammar.BGMWalker;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.tools.xjc.util.Util;
import java.util.Iterator;
import java.util.Set;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.ClassCandidateItem;
import java.util.Collection;
import java.util.HashSet;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

class TemporaryClassItemRemover
{
    private static final PrintStream debug;
    
    public static void remove(final AnnotatedGrammar grammar) {
        final ClassItem[] items = grammar.getClasses();
        final Pass1 p1 = new Pass1((TemporaryClassItemRemover$1)null);
        grammar.visit((ExpressionVisitorVoid)p1);
        for (int i = 0; i < items.length; ++i) {
            p1.processIfUnvisited(items[i]);
        }
        final Set cs = new HashSet(p1.allCandidates);
        cs.removeAll(p1.notRemovableClasses);
        if (TemporaryClassItemRemover.debug != null) {
            for (final ClassCandidateItem ci : cs) {
                if (!p1.reachableClasses.contains(ci)) {
                    TemporaryClassItemRemover.debug.println(displayName(ci) + " : this is unreachable");
                }
            }
        }
        cs.retainAll(p1.reachableClasses);
        if (TemporaryClassItemRemover.debug != null) {
            for (final ClassCandidateItem ci : cs) {
                TemporaryClassItemRemover.debug.println(" " + displayName(ci) + " will be removed");
            }
        }
        final Pass2 p2 = new Pass2(grammar.getPool(), cs);
        grammar.visit((ExpressionVisitorExpression)p2);
        for (int j = 0; j < items.length; ++j) {
            items[j].visit((ExpressionVisitorExpression)p2);
        }
    }
    
    private static final String displayName(final ClassCandidateItem cci) {
        return cci.name + '@' + Integer.toHexString(cci.hashCode());
    }
    
    static {
        TemporaryClassItemRemover.debug = ((Util.getSystemProperty((TemporaryClassItemRemover.class$com$sun$tools$xjc$reader$annotator$TemporaryClassItemRemover == null) ? (TemporaryClassItemRemover.class$com$sun$tools$xjc$reader$annotator$TemporaryClassItemRemover = class$("com.sun.tools.xjc.reader.annotator.TemporaryClassItemRemover")) : TemporaryClassItemRemover.class$com$sun$tools$xjc$reader$annotator$TemporaryClassItemRemover, "debug") != null) ? System.out : null);
    }
    
    private static class Pass2 extends ExpressionCloner
    {
        private final Set rejectedCandidates;
        private final Set visitedExps;
        
        Pass2(final ExpressionPool pool, final Set _rejected) {
            super(pool);
            this.visitedExps = new HashSet();
            this.rejectedCandidates = _rejected;
        }
        
        public Expression onAttribute(final AttributeExp exp) {
            return this.pool.createAttribute(exp.getNameClass(), exp.exp.visit((ExpressionVisitorExpression)this));
        }
        
        public Expression onElement(final ElementExp exp) {
            if (this.visitedExps.add(exp)) {
                exp.contentModel = exp.contentModel.visit((ExpressionVisitorExpression)this);
            }
            return (Expression)exp;
        }
        
        public Expression onOther(final OtherExp exp) {
            if (!(exp instanceof ClassCandidateItem)) {
                if (this.visitedExps.add(exp)) {
                    exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
                }
                return (Expression)exp;
            }
            if (this.rejectedCandidates.contains(exp)) {
                return exp.exp.visit((ExpressionVisitorExpression)this);
            }
            if (this.visitedExps.add(exp)) {
                exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            }
            return (Expression)((ClassCandidateItem)exp).toClassItem();
        }
        
        public Expression onRef(final ReferenceExp exp) {
            if (this.visitedExps.add(exp)) {
                exp.exp = exp.exp.visit((ExpressionVisitorExpression)this);
            }
            return (Expression)exp;
        }
    }
    
    private static class Pass1 extends BGMWalker
    {
        private final Set checkedClasses;
        public final Set reachableClasses;
        public final Set notRemovableClasses;
        public final Set allCandidates;
        private List childItems;
        private boolean hasNonSimpleName;
        private OtherExp parentItem;
        
        private Pass1() {
            this.checkedClasses = new HashSet();
            this.reachableClasses = new HashSet();
            this.notRemovableClasses = new HashSet();
            this.allCandidates = new HashSet();
            this.childItems = new ArrayList();
            this.parentItem = null;
        }
        
        public void processIfUnvisited(final ClassItem ci) {
            if (!this.checkedClasses.contains(ci)) {
                if (TemporaryClassItemRemover.debug != null) {
                    TemporaryClassItemRemover.debug.println("processIf(" + ci.name + ")");
                }
                ci.visit((ExpressionVisitorVoid)this);
            }
        }
        
        public Object onIgnore(final IgnoreItem item) {
            return null;
        }
        
        public Object onField(final FieldItem item) {
            item.exp.visit((ExpressionVisitorVoid)this);
            return null;
        }
        
        public Object onSuper(final SuperClassItem item) {
            this.updateAndVisit(item);
            return null;
        }
        
        public Object onInterface(final InterfaceItem item) {
            this.updateAndVisit(item);
            return null;
        }
        
        private void updateAndVisit(final JavaItem item) {
            this.childItems.add(item);
            final OtherExp old = this.parentItem;
            this.parentItem = item;
            item.exp.visit((ExpressionVisitorVoid)this);
            this.parentItem = old;
        }
        
        public Object onExternal(final ExternalItem item) {
            this.childItems.add(item);
            return null;
        }
        
        public Object onPrimitive(final PrimitiveItem item) {
            this.childItems.add(item);
            return null;
        }
        
        private Result collectChildItems(final OtherExp exp) {
            final List oldChildItems = this.childItems;
            this.childItems = new ArrayList();
            final OtherExp oldParent = this.parentItem;
            this.parentItem = exp;
            final boolean oldHNSN = this.hasNonSimpleName;
            this.hasNonSimpleName = false;
            exp.exp.visit((ExpressionVisitorVoid)this);
            final Result result = new Result(this.childItems, this.hasNonSimpleName);
            this.childItems = oldChildItems;
            this.parentItem = oldParent;
            this.hasNonSimpleName = oldHNSN;
            return result;
        }
        
        public Object onClass(final ClassItem item) {
            if (TemporaryClassItemRemover.debug != null) {
                TemporaryClassItemRemover.debug.println("processing " + item.name);
            }
            this.childItems.add(item);
            if (!this.checkedClasses.add(item)) {
                return null;
            }
            this.collectChildItems(item);
            return null;
        }
        
        public void onOther(final OtherExp exp) {
            if (!(exp instanceof ClassCandidateItem)) {
                super.onOther(exp);
                return;
            }
            final ClassCandidateItem cci = (ClassCandidateItem)exp;
            if (TemporaryClassItemRemover.debug != null) {
                TemporaryClassItemRemover.debug.println("processing " + TemporaryClassItemRemover.access$200(cci));
            }
            this.childItems.add(cci);
            this.allCandidates.add(cci);
            if (this.parentItem != null) {
                if (!this.reachableClasses.add(cci) && cci.exp instanceof NameClassAndExpression && this.notRemovableClasses.add(cci) && TemporaryClassItemRemover.debug != null) {
                    TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : referenced more than once");
                }
            }
            else {
                this.notRemovableClasses.add(cci);
                if (TemporaryClassItemRemover.debug != null) {
                    TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : can be a root class");
                }
            }
            if ((this.parentItem instanceof SuperClassItem || this.parentItem instanceof InterfaceItem) && this.notRemovableClasses.add(cci) && TemporaryClassItemRemover.debug != null) {
                TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : referenced by a superClass/interfaceItem");
            }
            if (!this.checkedClasses.add(cci)) {
                return;
            }
            final Result r = this.collectChildItems(cci);
            if (r.hasNonSimpleName && this.notRemovableClasses.add(cci) && TemporaryClassItemRemover.debug != null) {
                TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : this class has non-simple element/attribute");
            }
            if (r.childItems.size() == 0) {
                if (this.notRemovableClasses.add(cci) && TemporaryClassItemRemover.debug != null) {
                    TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : this class has no child item");
                }
            }
            else if (r.childItems.size() > 1 && this.notRemovableClasses.add(cci) && TemporaryClassItemRemover.debug != null) {
                TemporaryClassItemRemover.debug.println(TemporaryClassItemRemover.access$200(cci) + " : this class has multiple fields");
            }
        }
        
        public void onAttribute(final AttributeExp exp) {
            if (!(exp.nameClass instanceof SimpleNameClass)) {
                this.hasNonSimpleName = true;
            }
            exp.exp.visit((ExpressionVisitorVoid)this);
        }
        
        public void onElement(final ElementExp exp) {
            if (!(exp.getNameClass() instanceof SimpleNameClass)) {
                this.hasNonSimpleName = true;
            }
            exp.contentModel.visit((ExpressionVisitorVoid)this);
        }
        
        private static class Result
        {
            public final List childItems;
            public final boolean hasNonSimpleName;
            
            public Result(final List _ci, final boolean _has) {
                this.childItems = _ci;
                this.hasNonSimpleName = _has;
            }
        }
    }
}
