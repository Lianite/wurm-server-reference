// 
// Decompiled by Procyon v0.5.30
// 

package org.intellij.lang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE })
@Language("RegExp")
public @interface RegExp {
    String prefix() default "";
    
    String suffix() default "";
}
