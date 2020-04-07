// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthSchemeFactory;

@Immutable
public class NTLMSchemeFactory implements AuthSchemeFactory
{
    public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme(new NTLMEngineImpl());
    }
}
