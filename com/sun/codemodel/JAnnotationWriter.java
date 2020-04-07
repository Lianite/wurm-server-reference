// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.lang.annotation.Annotation;

public interface JAnnotationWriter<A extends Annotation>
{
    JAnnotationUse getAnnotationUse();
    
    Class<A> getAnnotationType();
}
