// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.auth;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.Header;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;

public class SPNegoScheme extends GGSSchemeBase
{
    private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
    
    public SPNegoScheme(final boolean stripPort) {
        super(stripPort);
    }
    
    public SPNegoScheme() {
        super(false);
    }
    
    public String getSchemeName() {
        return "Negotiate";
    }
    
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return super.authenticate(credentials, request, context);
    }
    
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return this.generateGSSToken(input, new Oid("1.3.6.1.5.5.2"), authServer);
    }
    
    public String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        return null;
    }
    
    public String getRealm() {
        return null;
    }
    
    public boolean isConnectionBased() {
        return true;
    }
}
