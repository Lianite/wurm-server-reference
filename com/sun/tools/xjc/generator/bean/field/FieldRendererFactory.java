// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JClass;

public class FieldRendererFactory
{
    private final FieldRenderer DEFAULT;
    private static final FieldRenderer ARRAY;
    private static final FieldRenderer REQUIRED_UNBOXED;
    private static final FieldRenderer SINGLE;
    private static final FieldRenderer SINGLE_PRIMITIVE_ACCESS;
    
    public FieldRendererFactory() {
        this.DEFAULT = new DefaultFieldRenderer(this);
    }
    
    public FieldRenderer getDefault() {
        return this.DEFAULT;
    }
    
    public FieldRenderer getArray() {
        return FieldRendererFactory.ARRAY;
    }
    
    public FieldRenderer getRequiredUnboxed() {
        return FieldRendererFactory.REQUIRED_UNBOXED;
    }
    
    public FieldRenderer getSingle() {
        return FieldRendererFactory.SINGLE;
    }
    
    public FieldRenderer getSinglePrimitiveAccess() {
        return FieldRendererFactory.SINGLE_PRIMITIVE_ACCESS;
    }
    
    public FieldRenderer getList(final JClass coreList) {
        return new UntypedListFieldRenderer(coreList);
    }
    
    public FieldRenderer getConst(final FieldRenderer fallback) {
        return new ConstFieldRenderer(fallback);
    }
    
    static {
        ARRAY = new GenericFieldRenderer(ArrayField.class);
        REQUIRED_UNBOXED = new GenericFieldRenderer(UnboxedField.class);
        SINGLE = new GenericFieldRenderer(SingleField.class);
        SINGLE_PRIMITIVE_ACCESS = new GenericFieldRenderer(SinglePrimitiveAccessField.class);
    }
}
