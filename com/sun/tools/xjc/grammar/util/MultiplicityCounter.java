// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitor;

public abstract class MultiplicityCounter implements ExpressionVisitor
{
    public static final MultiplicityCounter javaItemCounter;
    
    protected abstract Multiplicity isChild(final Expression p0);
    
    public Object onSequence(final SequenceExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return Multiplicity.group((Multiplicity)exp.exp1.visit((ExpressionVisitor)this), (Multiplicity)exp.exp2.visit((ExpressionVisitor)this));
    }
    
    public Object onInterleave(final InterleaveExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return Multiplicity.group((Multiplicity)exp.exp1.visit((ExpressionVisitor)this), (Multiplicity)exp.exp2.visit((ExpressionVisitor)this));
    }
    
    public Object onChoice(final ChoiceExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return Multiplicity.choice((Multiplicity)exp.exp1.visit((ExpressionVisitor)this), (Multiplicity)exp.exp2.visit((ExpressionVisitor)this));
    }
    
    public Object onOneOrMore(final OneOrMoreExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return Multiplicity.oneOrMore((Multiplicity)exp.exp.visit((ExpressionVisitor)this));
    }
    
    public Object onMixed(final MixedExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.exp.visit((ExpressionVisitor)this);
    }
    
    public Object onList(final ListExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.exp.visit((ExpressionVisitor)this);
    }
    
    public Object onEpsilon() {
        Multiplicity m = this.isChild(Expression.epsilon);
        if (m == null) {
            m = Multiplicity.zero;
        }
        return m;
    }
    
    public Object onAnyString() {
        Multiplicity m = this.isChild(Expression.anyString);
        if (m == null) {
            m = Multiplicity.zero;
        }
        return m;
    }
    
    public Object onData(final DataExp exp) {
        Multiplicity m = this.isChild((Expression)exp);
        if (m == null) {
            m = Multiplicity.zero;
        }
        return m;
    }
    
    public Object onValue(final ValueExp exp) {
        Multiplicity m = this.isChild((Expression)exp);
        if (m == null) {
            m = Multiplicity.zero;
        }
        return m;
    }
    
    public Object onElement(final ElementExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.contentModel.visit((ExpressionVisitor)this);
    }
    
    public Object onAttribute(final AttributeExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.exp.visit((ExpressionVisitor)this);
    }
    
    public Object onRef(final ReferenceExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.exp.visit((ExpressionVisitor)this);
    }
    
    public Object onOther(final OtherExp exp) {
        final Multiplicity m = this.isChild((Expression)exp);
        if (m != null) {
            return m;
        }
        return exp.exp.visit((ExpressionVisitor)this);
    }
    
    public Object onConcur(final ConcurExp exp) {
        throw new Error();
    }
    
    public Object onNullSet() {
        throw new Error();
    }
    
    static {
        MultiplicityCounter.javaItemCounter = (MultiplicityCounter)new MultiplicityCounter$1();
    }
}
