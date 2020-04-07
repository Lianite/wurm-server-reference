// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.codemodel.JBlock;

public interface FieldRenderer
{
    void generate();
    
    JBlock getOnSetEventHandler();
    
    FieldUse getFieldUse();
    
    void setter(final JBlock p0, final JExpression p1);
    
    void toArray(final JBlock p0, final JExpression p1);
    
    void unsetValues(final JBlock p0);
    
    JExpression hasSetValue();
    
    JExpression getValue();
    
    JClass getValueType();
    
    JExpression ifCountEqual(final int p0);
    
    JExpression ifCountGte(final int p0);
    
    JExpression ifCountLte(final int p0);
    
    JExpression count();
    
    FieldMarshallerGenerator createMarshaller(final JBlock p0, final String p1);
}
