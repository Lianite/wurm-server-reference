// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JVar;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.codemodel.JCodeModel;

public abstract class MethodWriter
{
    protected final JCodeModel codeModel;
    
    protected MethodWriter(final ClassOutline context) {
        this.codeModel = context.parent().getCodeModel();
    }
    
    public abstract JMethod declareMethod(final JType p0, final String p1);
    
    public final JMethod declareMethod(final Class returnType, final String methodName) {
        return this.declareMethod(this.codeModel.ref(returnType), methodName);
    }
    
    public abstract JDocComment javadoc();
    
    public abstract JVar addParameter(final JType p0, final String p1);
    
    public final JVar addParameter(final Class type, final String name) {
        return this.addParameter(this.codeModel.ref(type), name);
    }
}
