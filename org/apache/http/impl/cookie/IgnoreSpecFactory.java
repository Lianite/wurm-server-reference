// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpecFactory;

@Immutable
public class IgnoreSpecFactory implements CookieSpecFactory
{
    public CookieSpec newInstance(final HttpParams params) {
        return new IgnoreSpec();
    }
}
