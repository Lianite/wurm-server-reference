// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JClass;

public class XsiTypeFieldRenderer extends SingleFieldRenderer
{
    private final JClass defaultObject;
    
    public XsiTypeFieldRenderer(final ClassContext context, final FieldUse fu, final JClass _defaultObject) {
        super(context, fu);
        this.defaultObject = _defaultObject;
    }
    
    protected JFieldVar generateField() {
        return this.context.implClass.field(2, this.fu.type, "_" + this.fu.name, JExpr._new(this.defaultObject));
    }
    
    public JExpression ifCountEqual(final int i) {
        if (i == 1) {
            return JExpr.TRUE;
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountGte(final int i) {
        if (i <= 1) {
            return JExpr.TRUE;
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountLte(final int i) {
        if (i == 0) {
            return JExpr.FALSE;
        }
        return JExpr.TRUE;
    }
    
    public JExpression count() {
        return JExpr.lit(1);
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        return (FieldMarshallerGenerator)new XsiTypeFieldRenderer$1(this);
    }
    
    public static final class Factory implements FieldRendererFactory
    {
        private final ClassItem defaultObjectType;
        
        public Factory(final ClassItem _defaultObjectType) {
            this.defaultObjectType = _defaultObjectType;
        }
        
        public FieldRenderer create(final ClassContext context, final FieldUse fu) {
            return (FieldRenderer)new XsiTypeFieldRenderer(context, fu, context.parent.getClassContext(this.defaultObjectType).implRef);
        }
    }
}
