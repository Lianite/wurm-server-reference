// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.auth.params;

import org.apache.http.protocol.HTTP;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.Immutable;

@Immutable
public final class AuthParams
{
    public static String getCredentialCharset(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String charset = (String)params.getParameter("http.auth.credential-charset");
        if (charset == null) {
            charset = HTTP.DEF_PROTOCOL_CHARSET.name();
        }
        return charset;
    }
    
    public static void setCredentialCharset(final HttpParams params, final String charset) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter("http.auth.credential-charset", charset);
    }
}
