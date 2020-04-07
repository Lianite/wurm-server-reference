// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;

public interface GeneratorContext
{
    AnnotatedGrammar getGrammar();
    
    JCodeModel getCodeModel();
    
    LookupTableBuilder getLookupTableBuilder();
    
    JClass getRuntime(final Class p0);
    
    FieldRenderer getField(final FieldUse p0);
    
    PackageContext getPackageContext(final JPackage p0);
    
    ClassContext getClassContext(final ClassItem p0);
    
    PackageContext[] getAllPackageContexts();
    
    CodeModelClassFactory getClassFactory();
    
    ErrorReceiver getErrorReceiver();
}
