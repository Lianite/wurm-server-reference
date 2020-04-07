// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Collection;

public interface JavaCompiler
{
    J2SJAXBModel bind(final Collection<Reference> p0, final Map<QName, Reference> p1, final String p2, final AnnotationProcessorEnvironment p3);
}
