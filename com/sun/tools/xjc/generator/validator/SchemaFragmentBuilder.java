// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.validator;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ElementExp;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.JavaItemVisitor;
import com.sun.msv.grammar.ExpressionCloner;

class SchemaFragmentBuilder extends ExpressionCloner implements JavaItemVisitor
{
    private boolean inAttribute;
    private boolean inSuperClass;
    private Expression anyAttributes;
    
    public SchemaFragmentBuilder(final ExpressionPool pool) {
        super(pool);
        this.inAttribute = false;
        this.inSuperClass = false;
        this.anyAttributes = this.pool.createZeroOrMore(this.pool.createAttribute(NameClass.ALL));
    }
    
    public Expression onRef(final ReferenceExp exp) {
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onOther(final OtherExp exp) {
        if (exp instanceof JavaItem) {
            return (Expression)((JavaItem)exp).visitJI((JavaItemVisitor)this);
        }
        return exp.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Expression onAttribute(final AttributeExp exp) {
        if (this.inAttribute) {
            throw new JAXBAssertionError();
        }
        this.inAttribute = true;
        try {
            return (Expression)new AttributeExp(exp.nameClass, exp.exp.visit((ExpressionVisitorExpression)this));
        }
        finally {
            this.inAttribute = false;
        }
    }
    
    public Expression onElement(final ElementExp exp) {
        return (Expression)this.createElement((NameClassAndExpression)exp);
    }
    
    public ElementPattern createElement(final NameClassAndExpression exp) {
        return new ElementPattern(exp.getNameClass(), exp.getContentModel().visit((ExpressionVisitorExpression)this));
    }
    
    public Object onPrimitive(final PrimitiveItem pi) {
        return pi.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Object onField(final FieldItem fi) {
        return fi.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Object onIgnore(final IgnoreItem ii) {
        return ii.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Object onInterface(final InterfaceItem ii) {
        return ii.exp.visit((ExpressionVisitorExpression)this);
    }
    
    public Object onSuper(final SuperClassItem si) {
        this.inSuperClass = true;
        try {
            return si.exp.visit((ExpressionVisitorExpression)this);
        }
        finally {
            this.inSuperClass = false;
        }
    }
    
    public Object onExternal(final ExternalItem ei) {
        return ei.createValidationFragment();
    }
    
    public Object onClass(final ClassItem ii) {
        if (this.inSuperClass) {
            this.inSuperClass = false;
            try {
                return ii.exp.visit((ExpressionVisitorExpression)this);
            }
            finally {
                this.inSuperClass = true;
            }
        }
        if (this.inAttribute) {
            return this.pool.createValue((XSDatatype)StringType.theInstance, (Object)("\u0000" + ii.getType().fullName()).intern());
        }
        return new ElementPattern((NameClass)new SimpleNameClass("http://java.sun.com/jaxb/xjc/dummy-elements", ii.getType().fullName().intern()), this.anyAttributes);
    }
}
