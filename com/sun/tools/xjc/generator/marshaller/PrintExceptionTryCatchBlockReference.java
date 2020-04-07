// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JTryBlock;
import com.sun.tools.xjc.generator.util.BlockReference;

class PrintExceptionTryCatchBlockReference implements BlockReference
{
    private final BlockReference parent;
    private final Context context;
    private JTryBlock block;
    
    PrintExceptionTryCatchBlockReference(final Context _context) {
        this.block = null;
        this.context = _context;
        this.parent = this.context.getCurrentBlock();
    }
    
    public JBlock get(final boolean create) {
        if (!create && this.block == null) {
            return null;
        }
        if (this.block == null) {
            this.block = this.parent.get(true)._try();
            final JCodeModel codeModel = this.context.codeModel;
            final JCatchBlock $catch = this.block._catch(codeModel.ref((PrintExceptionTryCatchBlockReference.class$java$lang$Exception == null) ? (PrintExceptionTryCatchBlockReference.class$java$lang$Exception = class$("java.lang.Exception")) : PrintExceptionTryCatchBlockReference.class$java$lang$Exception));
            $catch.body().staticInvoke(this.context.getRuntime((PrintExceptionTryCatchBlockReference.class$com$sun$tools$xjc$runtime$Util == null) ? (PrintExceptionTryCatchBlockReference.class$com$sun$tools$xjc$runtime$Util = class$("com.sun.tools.xjc.runtime.Util")) : PrintExceptionTryCatchBlockReference.class$com$sun$tools$xjc$runtime$Util), "handlePrintConversionException").arg(JExpr._this()).arg($catch.param("e")).arg(this.context.$serializer);
        }
        return this.block.body();
    }
}
