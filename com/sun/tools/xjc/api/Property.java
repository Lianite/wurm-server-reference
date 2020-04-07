// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import javax.xml.namespace.QName;
import com.sun.codemodel.JType;

public interface Property
{
    String name();
    
    JType type();
    
    QName elementName();
}
