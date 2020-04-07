// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface XmlAttribute {
    String name() default "##default";
    
    boolean required() default false;
    
    String namespace() default "##default";
}
