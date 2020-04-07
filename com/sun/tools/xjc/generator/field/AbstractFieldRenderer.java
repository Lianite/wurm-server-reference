// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.MethodWriter;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JCodeModel;

abstract class AbstractFieldRenderer implements FieldRenderer
{
    protected final JCodeModel codeModel;
    protected final ClassContext context;
    protected final FieldUse fu;
    protected final MethodWriter writer;
    
    protected AbstractFieldRenderer(final ClassContext _context, final FieldUse _fu) {
        this.context = _context;
        this.fu = _fu;
        this.codeModel = _context.parent.getCodeModel();
        this.writer = this.context.createMethodWriter();
    }
    
    public final FieldUse getFieldUse() {
        return this.fu;
    }
    
    protected final JFieldVar generateField(final JType type) {
        return this.context.implClass.field(2, type, "_" + this.fu.name);
    }
    
    protected final void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
