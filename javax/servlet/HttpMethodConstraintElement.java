// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

public class HttpMethodConstraintElement extends HttpConstraintElement
{
    private String methodName;
    
    public HttpMethodConstraintElement(final String methodName) {
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("invalid HTTP method name");
        }
        this.methodName = methodName;
    }
    
    public HttpMethodConstraintElement(final String methodName, final HttpConstraintElement constraint) {
        super(constraint.getEmptyRoleSemantic(), constraint.getTransportGuarantee(), constraint.getRolesAllowed());
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("invalid HTTP method name");
        }
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
}
