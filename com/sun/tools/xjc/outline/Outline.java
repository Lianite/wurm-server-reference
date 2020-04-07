// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.codemodel.JClassContainer;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CClassInfo;
import java.util.Collection;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.Model;

public interface Outline
{
    Model getModel();
    
    JCodeModel getCodeModel();
    
    FieldOutline getField(final CPropertyInfo p0);
    
    PackageOutline getPackageContext(final JPackage p0);
    
    Collection<? extends ClassOutline> getClasses();
    
    ClassOutline getClazz(final CClassInfo p0);
    
    ElementOutline getElement(final CElementInfo p0);
    
    EnumOutline getEnum(final CEnumLeafInfo p0);
    
    Collection<EnumOutline> getEnums();
    
    Iterable<? extends PackageOutline> getAllPackageContexts();
    
    CodeModelClassFactory getClassFactory();
    
    ErrorReceiver getErrorReceiver();
    
    JClassContainer getContainer(final CClassInfoParent p0, final Aspect p1);
    
    JType resolve(final CTypeRef p0, final Aspect p1);
    
    JClass addRuntime(final Class p0);
}
