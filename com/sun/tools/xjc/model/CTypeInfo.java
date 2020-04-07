// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.TypeInfo;

public interface CTypeInfo extends TypeInfo<NType, NClass>, CCustomizable
{
    JType toType(final Outline p0, final Aspect p1);
}
