// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.lang.annotation.Annotation;

public interface JAnnotatable
{
    JAnnotationUse annotate(final JClass p0);
    
    JAnnotationUse annotate(final Class<? extends Annotation> p0);
    
     <W extends JAnnotationWriter> W annotate2(final Class<W> p0);
}
