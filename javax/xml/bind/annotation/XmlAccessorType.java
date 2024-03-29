// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
public @interface XmlAccessorType {
    XmlAccessType value() default XmlAccessType.PUBLIC_MEMBER;
}
