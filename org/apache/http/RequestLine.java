// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http;

public interface RequestLine
{
    String getMethod();
    
    ProtocolVersion getProtocolVersion();
    
    String getUri();
}
