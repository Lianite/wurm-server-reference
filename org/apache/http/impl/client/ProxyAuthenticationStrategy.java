// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.Header;
import java.util.Queue;
import org.apache.http.HttpResponse;
import java.util.Map;
import org.apache.http.protocol.HttpContext;
import org.apache.http.auth.AuthScheme;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;

@Immutable
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public ProxyAuthenticationStrategy() {
        super(407, "Proxy-Authenticate", "http.auth.proxy-scheme-pref");
    }
}
