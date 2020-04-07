// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthSchemeFactory;

@Immutable
public class SPNegoSchemeFactory implements AuthSchemeFactory
{
    private final boolean stripPort;
    
    public SPNegoSchemeFactory(final boolean stripPort) {
        this.stripPort = stripPort;
    }
    
    public SPNegoSchemeFactory() {
        this(false);
    }
    
    public AuthScheme newInstance(final HttpParams params) {
        return new SPNegoScheme(this.stripPort);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
}
