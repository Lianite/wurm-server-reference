// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import java.util.List;
import javax.xml.namespace.QName;

public interface Mapping
{
    QName getElement();
    
    TypeAndAnnotation getType();
    
    List<? extends Property> getWrapperStyleDrilldown();
}
