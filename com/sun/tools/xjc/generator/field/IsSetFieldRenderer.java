// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.generator.MethodWriter;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;

public class IsSetFieldRenderer implements FieldRenderer
{
    private final FieldRenderer core;
    private final ClassContext context;
    private final FieldUse use;
    private final boolean generateUnSetMethod;
    private final boolean generateIsSetMethod;
    
    public static FieldRendererFactory createFactory(final FieldRendererFactory core, final boolean generateUnSetMethod, final boolean generateIsSetMethod) {
        return (FieldRendererFactory)new IsSetFieldRenderer$1(core, generateUnSetMethod, generateIsSetMethod);
    }
    
    public static FieldRendererFactory createFactory(final FieldRendererFactory core) {
        return createFactory(core, true, true);
    }
    
    public IsSetFieldRenderer(final ClassContext _context, final FieldUse _use, final FieldRenderer _core, final boolean generateUnSetMethod, final boolean generateIsSetMethod) {
        this.core = _core;
        this.context = _context;
        this.use = _use;
        this.generateUnSetMethod = generateUnSetMethod;
        this.generateIsSetMethod = generateIsSetMethod;
    }
    
    public void generate() {
        this.core.generate();
        final MethodWriter writer = this.context.createMethodWriter();
        final JCodeModel codeModel = this.context.parent.getCodeModel();
        if (this.generateIsSetMethod) {
            final JExpression hasSetValue = this.core.hasSetValue();
            if (hasSetValue == null) {}
            writer.declareMethod((JType)codeModel.BOOLEAN, "isSet" + this.use.name).body()._return(hasSetValue);
        }
        if (this.generateUnSetMethod) {
            this.core.unsetValues(writer.declareMethod((JType)codeModel.VOID, "unset" + this.use.name).body());
        }
    }
    
    public JBlock getOnSetEventHandler() {
        return this.core.getOnSetEventHandler();
    }
    
    public void unsetValues(final JBlock body) {
        this.core.unsetValues(body);
    }
    
    public void toArray(final JBlock block, final JExpression $array) {
        this.core.toArray(block, $array);
    }
    
    public JExpression hasSetValue() {
        return this.core.hasSetValue();
    }
    
    public JExpression getValue() {
        return this.core.getValue();
    }
    
    public JClass getValueType() {
        return this.core.getValueType();
    }
    
    public FieldUse getFieldUse() {
        return this.core.getFieldUse();
    }
    
    public void setter(final JBlock body, final JExpression newValue) {
        this.core.setter(body, newValue);
    }
    
    public JExpression ifCountEqual(final int i) {
        return this.core.ifCountEqual(i);
    }
    
    public JExpression ifCountGte(final int i) {
        return this.core.ifCountGte(i);
    }
    
    public JExpression ifCountLte(final int i) {
        return this.core.ifCountLte(i);
    }
    
    public JExpression count() {
        return this.core.count();
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        return this.core.createMarshaller(block, uniqueId);
    }
}
