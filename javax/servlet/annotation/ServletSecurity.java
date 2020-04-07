// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

@Inherited
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ServletSecurity {
    HttpConstraint value() default @HttpConstraint;
    
    HttpMethodConstraint[] httpMethodConstraints() default {};
    
    public enum EmptyRoleSemantic
    {
        PERMIT, 
        DENY;
    }
    
    public enum TransportGuarantee
    {
        NONE, 
        CONFIDENTIAL;
    }
}
