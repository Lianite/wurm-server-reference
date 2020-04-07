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
public class TargetAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public TargetAuthenticationStrategy() {
        super(401, "WWW-Authenticate", "http.auth.target-scheme-pref");
    }
}
