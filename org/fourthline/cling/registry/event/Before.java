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

@Qualifier
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
