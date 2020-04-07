// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JClass;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.tools.xjc.generator.util.BlockReference;

public class XsiNilFieldRenderer extends AbstractFieldRendererWithVar
{
    public static final FieldRendererFactory theFactory;
    private BlockReference onSetEvent;
    
    public XsiNilFieldRenderer(final ClassContext context, final FieldUse fu) {
        super(context, fu);
    }
    
    protected JFieldVar generateField() {
        return this.generateField(this.codeModel.BOOLEAN);
    }
    
    public void generateAccessors() {
        final JMethod $get = this.writer.declareMethod((JType)this.codeModel.BOOLEAN, "is" + this.fu.name);
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        $get.body()._return(this.ref());
        final JMethod $set = this.writer.declareMethod((JType)this.codeModel.VOID, "set" + this.fu.name);
        final JVar $value = this.writer.addParameter((JType)this.codeModel.BOOLEAN, "value");
        final JBlock body = $set.body();
        body.assign(this.ref(), $value);
        this.writer.javadoc().appendComment("Passing <code>true</code> will generate xsi:nil in the XML output");
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        this.onSetEvent = (BlockReference)new XsiNilFieldRenderer$2(this, body, $value);
    }
    
    public JBlock getOnSetEventHandler() {
        return this.onSetEvent.get(true);
    }
    
    public void setter(final JBlock block, final JExpression newValue) {
        block.assign(this.ref(), newValue);
    }
    
    public void toArray(final JBlock block, final JExpression $array) {
        block.assign($array.component(JExpr.lit(0)), this.ref());
    }
    
    public void unsetValues(final JBlock body) {
        throw new JAXBAssertionError();
    }
    
    public JExpression hasSetValue() {
        return null;
    }
    
    public JExpression getValue() {
        return this.codeModel.BOOLEAN.wrap(this.ref());
    }
    
    public JClass getValueType() {
        return this.codeModel.BOOLEAN.getWrapperClass();
    }
    
    public JExpression ifCountEqual(final int i) {
        switch (i) {
            case 0: {
                return this.ref().not();
            }
            case 1: {
                return this.ref();
            }
            default: {
                return JExpr.FALSE;
            }
        }
    }
    
    public JExpression ifCountGte(final int i) {
        if (i == 1) {
            return this.ref();
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountLte(final int i) {
        if (i == 0) {
            return this.ref().not();
        }
        return JExpr.TRUE;
    }
    
    public JExpression count() {
        return JOp.cond(this.ref(), JExpr.lit(1), JExpr.lit(0));
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        return (FieldMarshallerGenerator)new XsiNilFieldRenderer$3(this);
    }
    
    static {
        XsiNilFieldRenderer.theFactory = (FieldRendererFactory)new XsiNilFieldRenderer$1();
    }
}
