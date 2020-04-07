// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.runtime;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.ElementInfo;

public interface RuntimeElementInfo extends ElementInfo<Type, Class>, RuntimeElement
{
    RuntimeClassInfo getScope();
    
    RuntimeElementPropertyInfo getProperty();
    
    Class<? extends JAXBElement> getType();
    
    RuntimeNonElement getContentType();
}
