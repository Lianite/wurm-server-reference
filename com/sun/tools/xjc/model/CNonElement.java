// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.NonElement;

public interface CNonElement extends NonElement<NType, NClass>, TypeUse, CTypeInfo
{
    @Deprecated
    CNonElement getInfo();
    
    @Deprecated
    boolean isCollection();
    
    @Deprecated
    CAdapter getAdapterUse();
}
