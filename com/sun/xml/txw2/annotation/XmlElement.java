// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface XmlElement {
    String value() default "";
    
    String ns() default "##default";
}
