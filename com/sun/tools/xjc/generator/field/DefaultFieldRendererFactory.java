// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JCodeModel;

public class DefaultFieldRendererFactory implements FieldRendererFactory
{
    private FieldRendererFactory defaultCollectionFieldRenderer;
    
    public DefaultFieldRendererFactory(final JCodeModel codeModel) {
        this((FieldRendererFactory)new UntypedListFieldRenderer.Factory(codeModel.ref((DefaultFieldRendererFactory.class$java$util$ArrayList == null) ? (DefaultFieldRendererFactory.class$java$util$ArrayList = class$("java.util.ArrayList")) : DefaultFieldRendererFactory.class$java$util$ArrayList)));
    }
    
    public DefaultFieldRendererFactory(final FieldRendererFactory defaultCollectionFieldRenderer) {
        this.defaultCollectionFieldRenderer = defaultCollectionFieldRenderer;
    }
    
    public FieldRenderer create(final ClassContext context, final FieldUse fu) {
        if (!fu.multiplicity.isAtMostOnce()) {
            return this.defaultCollectionFieldRenderer.create(context, fu);
        }
        if (fu.isUnboxable()) {
            return (FieldRenderer)new OptionalUnboxedFieldRenderer(context, fu);
        }
        return (FieldRenderer)new SingleFieldRenderer(context, fu);
    }
}
