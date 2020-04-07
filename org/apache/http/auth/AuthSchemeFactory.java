// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.auth;

import org.apache.http.params.HttpParams;

public interface AuthSchemeFactory
{
    AuthScheme newInstance(final HttpParams p0);
}
