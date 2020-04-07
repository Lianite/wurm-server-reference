// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.auth;

import org.apache.http.util.LangUtils;
import java.security.Principal;
import java.util.Locale;
import org.apache.http.annotation.Immutable;
import java.io.Serializable;

@Immutable
public class NTCredentials implements Credentials, Serializable
{
    private static final long serialVersionUID = -7385699315228907265L;
    private final NTUserPrincipal principal;
    private final String password;
    private final String workstation;
    
    public NTCredentials(final String usernamePassword) {
        if (usernamePassword == null) {
            throw new IllegalArgumentException("Username:password string may not be null");
        }
        final int atColon = usernamePassword.indexOf(58);
        String username;
        if (atColon >= 0) {
            username = usernamePassword.substring(0, atColon);
            this.password = usernamePassword.substring(atColon + 1);
        }
        else {
            username = usernamePassword;
            this.password = null;
        }
        final int atSlash = username.indexOf(47);
        if (atSlash >= 0) {
            this.principal = new NTUserPrincipal(username.substring(0, atSlash).toUpperCase(Locale.ENGLISH), username.substring(atSlash + 1));
        }
        else {
            this.principal = new NTUserPrincipal(null, username.substring(atSlash + 1));
        }
        this.workstation = null;
    }
    
    public NTCredentials(final String userName, final String password, final String workstation, final String domain) {
        if (userName == null) {
            throw new IllegalArgumentException("User name may not be null");
        }
        this.principal = new NTUserPrincipal(domain, userName);
        this.password = password;
        if (workstation != null) {
            this.workstation = workstation.toUpperCase(Locale.ENGLISH);
        }
        else {
            this.workstation = null;
        }
    }
    
    public Principal getUserPrincipal() {
        return this.principal;
    }
    
    public String getUserName() {
        return this.principal.getUsername();
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getDomain() {
        return this.principal.getDomain();
    }
    
    public String getWorkstation() {
        return this.workstation;
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.principal);
        hash = LangUtils.hashCode(hash, this.workstation);
        return hash;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NTCredentials) {
            final NTCredentials that = (NTCredentials)o;
            if (LangUtils.equals(this.principal, that.principal) && LangUtils.equals(this.workstation, that.workstation)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.principal);
        buffer.append("][workstation: ");
        buffer.append(this.workstation);
        buffer.append("]");
        return buffer.toString();
    }
}
