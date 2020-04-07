// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UpnpInputArgument {
    String name();
    
    String[] aliases() default {};
    
    String stateVariable() default "";
}
