// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import javax.xml.bind.annotation.XmlNsForm;
import java.util.Set;
import com.sun.tools.xjc.generator.bean.ObjectFactoryGenerator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

public interface PackageOutline
{
    JPackage _package();
    
    JDefinedClass objectFactory();
    
    ObjectFactoryGenerator objectFactoryGenerator();
    
    Set<? extends ClassOutline> getClasses();
    
    String getMostUsedNamespaceURI();
    
    XmlNsForm getElementFormDefault();
}
