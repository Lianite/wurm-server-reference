// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JBlock;
import java.util.List;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JMethod;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

public class SingleField extends AbstractFieldWithVar
{
    protected SingleField(final ClassOutlineImpl context, final CPropertyInfo prop) {
        this(context, prop, false);
    }
    
    protected SingleField(final ClassOutlineImpl context, final CPropertyInfo prop, final boolean forcePrimitiveAccess) {
        super(context, prop);
        assert !this.exposedType.isPrimitive() && !this.implType.isPrimitive();
        this.createField();
        final MethodWriter writer = context.createMethodWriter();
        final NameConverter nc = context.parent().getModel().getNameConverter();
        JExpression defaultValue = null;
        if (prop.defaultValue != null) {
            defaultValue = prop.defaultValue.compute(this.outline.parent());
        }
        JType getterType;
        if (defaultValue != null || forcePrimitiveAccess) {
            getterType = this.exposedType.unboxify();
        }
        else {
            getterType = this.exposedType;
        }
        final JMethod $get = writer.declareMethod(getterType, this.getGetterMethod());
        String javadoc = prop.javadoc;
        if (javadoc.length() == 0) {
            javadoc = Messages.DEFAULT_GETTER_JAVADOC.format(nc.toVariableName(prop.getName(true)));
        }
        writer.javadoc().append(javadoc);
        if (defaultValue == null) {
            $get.body()._return(this.ref());
        }
        else {
            final JConditional cond = $get.body()._if(this.ref().eq(JExpr._null()));
            cond._then()._return(defaultValue);
            cond._else()._return(this.ref());
        }
        final List<Object> possibleTypes = this.listPossibleTypes(prop);
        writer.javadoc().addReturn().append("possible object is\n").append(possibleTypes);
        final JMethod $set = writer.declareMethod(this.codeModel.VOID, "set" + prop.getName(true));
        JType setterType = this.exposedType;
        if (forcePrimitiveAccess) {
            setterType = setterType.unboxify();
        }
        final JVar $value = writer.addParameter(setterType, "value");
        final JBlock body = $set.body();
        body.assign(JExpr._this().ref(this.ref()), this.castToImplType($value));
        writer.javadoc().append(Messages.DEFAULT_SETTER_JAVADOC.format(nc.toVariableName(prop.getName(true))));
        writer.javadoc().addParam($value).append("allowed object is\n").append(possibleTypes);
    }
    
    public final JType getFieldType() {
        return this.implType;
    }
    
    public FieldAccessor create(final JExpression targetObject) {
        return new Accessor(targetObject);
    }
    
    protected class Accessor extends AbstractFieldWithVar.Accessor
    {
        protected Accessor(final JExpression $target) {
            super($target);
        }
        
        public void unsetValues(final JBlock body) {
            body.assign(this.$ref, JExpr._null());
        }
        
        public JExpression hasSetValue() {
            return this.$ref.ne(JExpr._null());
        }
    }
}
