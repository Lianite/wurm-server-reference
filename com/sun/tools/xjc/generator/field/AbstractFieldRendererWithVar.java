// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JFieldVar;

abstract class AbstractFieldRendererWithVar extends AbstractFieldRenderer
{
    private JFieldVar field;
    
    protected AbstractFieldRendererWithVar(final ClassContext _context, final FieldUse _fu) {
        super(_context, _fu);
    }
    
    public final void generate() {
        this.field = this.generateField();
        this.generateAccessors();
    }
    
    public JFieldVar ref() {
        return this.field;
    }
    
    protected JFieldVar generateField() {
        return this.generateField(this.getValueType());
    }
    
    public abstract void generateAccessors();
}
