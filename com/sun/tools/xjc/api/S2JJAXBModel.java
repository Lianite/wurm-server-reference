// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Plugin;
import java.util.Collection;
import com.sun.codemodel.JClass;
import java.util.List;
import javax.xml.namespace.QName;

public interface S2JJAXBModel extends JAXBModel
{
    Mapping get(final QName p0);
    
    List<JClass> getAllObjectFactories();
    
    Collection<? extends Mapping> getMappings();
    
    TypeAndAnnotation getJavaType(final QName p0);
    
    JCodeModel generateCode(final Plugin[] p0, final ErrorListener p1);
}
