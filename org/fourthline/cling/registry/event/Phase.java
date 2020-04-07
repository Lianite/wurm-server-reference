// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry.event;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import javax.enterprise.util.AnnotationLiteral;

public interface Phase
{
    public static final AnnotationLiteral<Alive> ALIVE = new AnnotationLiteral<Alive>() {};
    public static final AnnotationLiteral<Complete> COMPLETE = new AnnotationLiteral<Complete>() {};
    public static final AnnotationLiteral<Byebye> BYEBYE = new AnnotationLiteral<Byebye>() {};
    public static final AnnotationLiteral<Updated> UPDATED = new AnnotationLiteral<Updated>() {};
    
    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Updated {
    }
    
    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Byebye {
    }
    
    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Complete {
    }
    
    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Alive {
    }
}
