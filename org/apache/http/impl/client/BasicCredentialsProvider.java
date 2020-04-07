// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import java.util.Iterator;
import java.util.Map;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.CredentialsProvider;

@ThreadSafe
public class BasicCredentialsProvider implements CredentialsProvider
{
    private final ConcurrentHashMap<AuthScope, Credentials> credMap;
    
    public BasicCredentialsProvider() {
        this.credMap = new ConcurrentHashMap<AuthScope, Credentials>();
    }
    
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        this.credMap.put(authscope, credentials);
    }
    
    private static Credentials matchCredentials(final Map<AuthScope, Credentials> map, final AuthScope authscope) {
        Credentials creds = map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (final AuthScope current : map.keySet()) {
                final int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }
    
    public Credentials getCredentials(final AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        return matchCredentials(this.credMap, authscope);
    }
    
    public void clear() {
        this.credMap.clear();
    }
    
    public String toString() {
        return this.credMap.toString();
    }
}
