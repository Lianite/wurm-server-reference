// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationSource
{
     <A extends Annotation> A readAnnotation(final Class<A> p0);
    
    boolean hasAnnotation(final Class<? extends Annotation> p0);
}
