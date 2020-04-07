// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind.annotation.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
public @interface XmlJavaTypeAdapter {
    Class<? extends XmlAdapter> value();
    
    Class type() default DEFAULT.class;
    
    public static final class DEFAULT
    {
    }
}
