// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import org.xml.sax.Locator;
import com.sun.msv.grammar.xmlschema.OccurrenceExp;
import com.sun.msv.grammar.OtherExp;
import java.util.Collection;
import com.sun.tools.xjc.util.SubList;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.InterleaveExp;
import java.util.HashMap;
import java.util.ArrayList;
import com.sun.tools.xjc.grammar.ClassItem;
import java.util.Set;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import java.util.HashSet;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.util.Map;
import java.util.List;
import com.sun.tools.xjc.grammar.BGMWalker;

public class FieldCollisionChecker extends BGMWalker
{
    private final AnnotatorController controller;
    private List fields;
    private final Map class2fields;
    private int sl;
    private int sr;
    
    public static void check(final AnnotatedGrammar grammar, final AnnotatorController controller) {
        final FieldCollisionChecker checker = new FieldCollisionChecker(controller);
        final Set baseClasses = new HashSet();
        final ClassItem[] cls = grammar.getClasses();
        for (int i = 0; i < cls.length; ++i) {
            baseClasses.add(cls[i].getSuperClass());
        }
        for (int i = 0; i < cls.length; ++i) {
            if (!baseClasses.contains(cls[i])) {
                checker.reset();
                cls[i].visit((ExpressionVisitorVoid)checker);
            }
        }
    }
    
    private FieldCollisionChecker(final AnnotatorController _controller) {
        this.fields = new ArrayList();
        this.class2fields = new HashMap();
        this.controller = _controller;
    }
    
    private void reset() {
        this.fields = new ArrayList();
        final int n = -1;
        this.sr = n;
        this.sl = n;
    }
    
    public void onInterleave(final InterleaveExp exp) {
        this.check((BinaryExp)exp);
    }
    
    public void onSequence(final SequenceExp exp) {
        this.check((BinaryExp)exp);
    }
    
    private void check(final BinaryExp exp) {
        final int l = this.fields.size();
        exp.exp1.visit((ExpressionVisitorVoid)this);
        final int r = this.fields.size();
        exp.exp2.visit((ExpressionVisitorVoid)this);
        this.compare(l, r, r, this.fields.size());
    }
    
    public void onChoice(final ChoiceExp exp) {
        final int l = this.fields.size();
        exp.exp1.visit((ExpressionVisitorVoid)this);
        final int r = this.fields.size();
        exp.exp2.visit((ExpressionVisitorVoid)this);
        if (l <= this.sl && this.sr <= r) {
            this.compare(this.sl, this.sr, r, this.fields.size());
        }
        else if (r <= this.sl && this.sr <= this.fields.size()) {
            this.compare(l, r, this.sl, this.sr);
        }
    }
    
    public Object onSuper(final SuperClassItem sci) {
        this.sl = this.fields.size();
        sci.definition.visit((ExpressionVisitorVoid)this);
        this.sr = this.fields.size();
        return null;
    }
    
    public Object onField(final FieldItem item) {
        this.fields.add(item);
        if (item.name.equals("Class")) {
            this.error(item.locator, "FieldCollisionChecker.ReservedWordCollision", item.name);
        }
        return null;
    }
    
    public Object onClass(final ClassItem item) {
        final List subList = this.class2fields.get(item);
        if (subList == null) {
            final int s = this.fields.size();
            super.onClass(item);
            final int e = this.fields.size();
            this.class2fields.put(item, new SubList(this.fields, s, e));
        }
        else {
            this.fields.addAll(subList);
        }
        return null;
    }
    
    public void onOther(final OtherExp exp) {
        if (exp instanceof OccurrenceExp) {
            ((OccurrenceExp)exp).itemExp.visit((ExpressionVisitorVoid)this);
        }
        else {
            super.onOther(exp);
        }
    }
    
    private void compare(final int ls, final int le, final int rs, final int re) {
        for (int l = ls; l < le; ++l) {
            final FieldItem left = this.fields.get(l);
            for (int r = rs; r < re; ++r) {
                final FieldItem right = this.fields.get(r);
                if (left.name.equals(right.name) && (!left.collisionExpected || !right.collisionExpected)) {
                    Locator locator;
                    if (left.locator != null) {
                        locator = left.locator;
                    }
                    else {
                        locator = right.locator;
                    }
                    this.error(locator, "FieldCollisionChecker.PropertyNameCollision", left.name);
                    if (left.locator != null && right.locator != null) {
                        this.error(right.locator, "FieldCollisionChecker.PropertyNameCollision.Source", left.name);
                    }
                }
            }
        }
    }
    
    private void error(final Locator loc, final String prop, final Object arg) {
        this.controller.getErrorReceiver().error(loc, Messages.format(prop, arg));
    }
}
