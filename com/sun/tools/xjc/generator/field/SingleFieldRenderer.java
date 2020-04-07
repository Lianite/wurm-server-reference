// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JConditional;
import com.sun.tools.xjc.grammar.DefaultValue;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.JavadocBuilder;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JBlock;

public class SingleFieldRenderer extends AbstractFieldRendererWithVar
{
    private JBlock onSetEvent;
    
    public SingleFieldRenderer(final ClassContext context, final FieldUse fu) {
        super(context, fu);
        this._assert(!fu.type.isPrimitive());
    }
    
    protected JFieldVar generateField() {
        return this.generateField(this.fu.type);
    }
    
    public JClass getValueType() {
        return (JClass)this.fu.type;
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
            final JConditional cond = $get.body()._if(this.ref().eq(JExpr._null()));
            cond._then()._return(defaultValues[0].generateConstant());
            cond._else()._return(this.ref());
        }
        this.writer.javadoc().addReturn("possible object is\n" + JavadocBuilder.listPossibleTypes(this.fu));
        final JMethod $set = this.writer.declareMethod((JType)this.codeModel.VOID, "set" + this.fu.name);
        final JVar $value = this.writer.addParameter(this.fu.type, "value");
        final JBlock body = $set.body();
        body.assign(this.ref(), $value);
        this.onSetEvent = body;
        javadoc = this.fu.getJavadoc();
        if (javadoc.length() == 0) {
            javadoc = Messages.format("SingleFieldRenderer.DefaultSetterJavadoc", NameConverter.standard.toVariableName(this.fu.name));
        }
        this.writer.javadoc().appendComment(javadoc);
        this.writer.javadoc().addParam($value, "allowed object is\n" + JavadocBuilder.listPossibleTypes(this.fu));
    }
    
    public JBlock getOnSetEventHandler() {
        return this.onSetEvent;
    }
    
    public void setter(final JBlock block, final JExpression newValue) {
        block.assign(this.ref(), newValue);
    }
    
    public void toArray(final JBlock block, final JExpression $array) {
        block.assign($array.component(JExpr.lit(0)), this.ref());
    }
    
    public void unsetValues(final JBlock body) {
        body.assign(this.ref(), JExpr._null());
    }
    
    public JExpression hasSetValue() {
        return this.ref().ne(JExpr._null());
    }
    
    public JExpression getValue() {
        return this.ref();
    }
    
    public JExpression ifCountEqual(final int i) {
        switch (i) {
            case 0: {
                return this.ref().eq(JExpr._null());
            }
            case 1: {
                return this.ref().ne(JExpr._null());
            }
            default: {
                return JExpr.FALSE;
            }
        }
    }
    
    public JExpression ifCountGte(final int i) {
        if (i == 1) {
            return this.ref().ne(JExpr._null());
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountLte(final int i) {
        if (i == 0) {
            return this.ref().eq(JExpr._null());
        }
        return JExpr.TRUE;
    }
    
    public JExpression count() {
        return JOp.cond(this.ref().ne(JExpr._null()), JExpr.lit(1), JExpr.lit(0));
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        return (FieldMarshallerGenerator)new SingleFieldRenderer$1(this);
    }
}
