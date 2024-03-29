// 
// Decompiled by Procyon v0.5.30
// 

package org.jetbrains.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface Contract {
    String value() default "";
    
    boolean pure() default false;
    
    String mutates() default "";
}
