// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface UpnpService {
    UpnpServiceId serviceId();
    
    UpnpServiceType serviceType();
    
    boolean supportsQueryStateVariables() default true;
    
    Class[] stringConvertibleTypes() default {};
}
