// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.Iterator;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;

public class ServletSecurityElement extends HttpConstraintElement
{
    private Collection<String> methodNames;
    private Collection<HttpMethodConstraintElement> methodConstraints;
    
    public ServletSecurityElement() {
        this.methodConstraints = new HashSet<HttpMethodConstraintElement>();
        this.methodNames = (Collection<String>)Collections.emptySet();
    }
    
    public ServletSecurityElement(final HttpConstraintElement constraint) {
        super(constraint.getEmptyRoleSemantic(), constraint.getTransportGuarantee(), constraint.getRolesAllowed());
        this.methodConstraints = new HashSet<HttpMethodConstraintElement>();
        this.methodNames = (Collection<String>)Collections.emptySet();
    }
    
    public ServletSecurityElement(final Collection<HttpMethodConstraintElement> methodConstraints) {
        this.methodConstraints = ((methodConstraints == null) ? new HashSet<HttpMethodConstraintElement>() : methodConstraints);
        this.methodNames = this.checkMethodNames(this.methodConstraints);
    }
    
    public ServletSecurityElement(final HttpConstraintElement constraint, final Collection<HttpMethodConstraintElement> methodConstraints) {
        super(constraint.getEmptyRoleSemantic(), constraint.getTransportGuarantee(), constraint.getRolesAllowed());
        this.methodConstraints = ((methodConstraints == null) ? new HashSet<HttpMethodConstraintElement>() : methodConstraints);
        this.methodNames = this.checkMethodNames(this.methodConstraints);
    }
    
    public ServletSecurityElement(final ServletSecurity annotation) {
        super(annotation.value().value(), annotation.value().transportGuarantee(), annotation.value().rolesAllowed());
        this.methodConstraints = new HashSet<HttpMethodConstraintElement>();
        for (final HttpMethodConstraint constraint : annotation.httpMethodConstraints()) {
            this.methodConstraints.add(new HttpMethodConstraintElement(constraint.value(), new HttpConstraintElement(constraint.emptyRoleSemantic(), constraint.transportGuarantee(), constraint.rolesAllowed())));
        }
        this.methodNames = this.checkMethodNames(this.methodConstraints);
    }
    
    public Collection<HttpMethodConstraintElement> getHttpMethodConstraints() {
        return Collections.unmodifiableCollection((Collection<? extends HttpMethodConstraintElement>)this.methodConstraints);
    }
    
    public Collection<String> getMethodNames() {
        return Collections.unmodifiableCollection((Collection<? extends String>)this.methodNames);
    }
    
    private Collection<String> checkMethodNames(final Collection<HttpMethodConstraintElement> methodConstraints) {
        final Collection<String> methodNames = new HashSet<String>();
        for (final HttpMethodConstraintElement methodConstraint : methodConstraints) {
            final String methodName = methodConstraint.getMethodName();
            if (!methodNames.add(methodName)) {
                throw new IllegalArgumentException("Duplicate HTTP method name: " + methodName);
            }
        }
        return methodNames;
    }
}
