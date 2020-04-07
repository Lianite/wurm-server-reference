// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSComplexType;

interface CTBuilder
{
    boolean isApplicable(final XSComplexType p0);
    
    Expression build(final XSComplexType p0);
}
