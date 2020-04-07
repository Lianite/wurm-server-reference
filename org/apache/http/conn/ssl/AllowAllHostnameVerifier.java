// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.ssl;

import org.apache.http.annotation.Immutable;

@Immutable
public class AllowAllHostnameVerifier extends AbstractVerifier
{
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) {
    }
    
    public final String toString() {
        return "ALLOW_ALL";
    }
}
