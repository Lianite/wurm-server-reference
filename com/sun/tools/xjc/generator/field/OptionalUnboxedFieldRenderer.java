// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JConditional;
import com.sun.tools.xjc.grammar.DefaultValue;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;

public class OptionalUnboxedFieldRenderer extends AbstractFieldRendererWithVar
{
    private JVar $has_flag;
    private JBlock onSetEvent;
    
    public OptionalUnboxedFieldRenderer(final ClassContext context, final FieldUse fu) {
        super(context, fu);
    }
    
    protected JFieldVar generateField() {
        this.$has_flag = this.context.implClass.field(2, this.codeModel.BOOLEAN, "has_" + this.fu.name);
        return this.generateField(this.fu.type);
    }
    
    public JClass getValueType() {
        return ((JPrimitiveType)this.fu.type).getWrapperClass();
    }
    
    public JExpression getValue() {
        return ((JPrimitiveType)this.fu.type).wrap(this.ref());
    }
    
    public void generateAccessors() {
        final JMethod $get = this.writer.declareMethod(this.fu.type, ((this.fu.type == this.codeModel.BOOLEAN) ? "is" : "get") + this.fu.name);
        String javadoc = this.fu.getJavadoc();
        if (javadoc.length() == 0) {
            javadoc = Messages.format("SingleFieldRenderer.DefaultGetterJavadoc", NameConverter.standard.toVariableName(this.fu.name));
        }
        this.writer.javadoc().appendComment(javadoc);
        final DefaultValue[] defaultValues = this.fu.getDefaultValues();
        if (defaultValues == null) {
            $get.body()._return(this.ref());
        }
        else {
            this._assert(defaultValues.length == 1);
            final JConditional cond = $get.body()._if(this.$has_flag.not());
            cond._then()._return(defaultValues[0].generateConstant());
            cond._else()._return(this.ref());
        }
        final JMethod $set = this.writer.declareMethod((JType)this.codeModel.VOID, "set" + this.fu.name);
        final JVar $value = this.writer.addParameter(this.fu.type, "value");
        final JBlock body = $set.body();
        body.assign(this.ref(), $value);
        body.assign(this.$has_flag, JExpr.TRUE);
        this.onSetEvent = body;
        javadoc = this.fu.getJavadoc();
        if (javadoc.length() == 0) {
            javadoc = Messages.format("SingleFieldRenderer.DefaultSetterJavadoc", NameConverter.standard.toVariableName(this.fu.name));
        }
        this.writer.javadoc().appendComment(javadoc);
    }
    
    public void toArray(final JBlock block, final JExpression $array) {
        block.assign($array.component(JExpr.lit(0)), this.ref());
    }
    
    public void unsetValues(final JBlock body) {
        body.assign(this.$has_flag, JExpr.FALSE);
    }
    
    public JExpression hasSetValue() {
        return this.$has_flag;
    }
    
    public JBlock getOnSetEventHandler() {
        return this.onSetEvent;
    }
    
    public JExpression ifCountEqual(final int i) {
        switch (i) {
            case 0: {
                return this.$has_flag.not();
            }
            case 1: {
                return this.$has_flag;
            }
            default: {
                return JExpr.FALSE;
            }
        }
    }
    
    public JExpression ifCountGte(final int i) {
        if (i == 1) {
            return this.$has_flag;
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountLte(final int i) {
        if (i == 0) {
            return this.$has_flag.not();
        }
        return JExpr.TRUE;
    }
    
    public JExpression count() {
        return JOp.cond(this.$has_flag, JExpr.lit(1), JExpr.lit(0));
    }
    
    public void setter(final JBlock block, final JExpression newValue) {
        block.assign(this.ref(), newValue);
        block.assign(this.$has_flag, JExpr.TRUE);
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        return (FieldMarshallerGenerator)new OptionalUnboxedFieldRenderer$1(this);
    }
}
