// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.xsom.XmlString;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Outline;

public abstract class CDefaultValue
{
    public abstract JExpression compute(final Outline p0);
    
    public static CDefaultValue create(final TypeUse typeUse, final XmlString defaultValue) {
        return new CDefaultValue() {
            public JExpression compute(final Outline outline) {
                return typeUse.createConstant(outline, defaultValue);
            }
        };
    }
}
