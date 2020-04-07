// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import javax.servlet.annotation.ServletSecurity;

public class HttpConstraintElement
{
    private ServletSecurity.EmptyRoleSemantic emptyRoleSemantic;
    private ServletSecurity.TransportGuarantee transportGuarantee;
    private String[] rolesAllowed;
    
    public HttpConstraintElement() {
        this(ServletSecurity.EmptyRoleSemantic.PERMIT);
    }
    
    public HttpConstraintElement(final ServletSecurity.EmptyRoleSemantic semantic) {
        this(semantic, ServletSecurity.TransportGuarantee.NONE, new String[0]);
    }
    
    public HttpConstraintElement(final ServletSecurity.TransportGuarantee guarantee, final String... roleNames) {
        this(ServletSecurity.EmptyRoleSemantic.PERMIT, guarantee, roleNames);
    }
    
    public HttpConstraintElement(final ServletSecurity.EmptyRoleSemantic semantic, final ServletSecurity.TransportGuarantee guarantee, final String... roleNames) {
        if (semantic == ServletSecurity.EmptyRoleSemantic.DENY && roleNames.length > 0) {
            throw new IllegalArgumentException("Deny semantic with rolesAllowed");
        }
        this.emptyRoleSemantic = semantic;
        this.transportGuarantee = guarantee;
        this.rolesAllowed = roleNames;
    }
    
    public ServletSecurity.EmptyRoleSemantic getEmptyRoleSemantic() {
        return this.emptyRoleSemantic;
    }
    
    public ServletSecurity.TransportGuarantee getTransportGuarantee() {
        return this.transportGuarantee;
    }
    
    public String[] getRolesAllowed() {
        return this.rolesAllowed;
    }
}
