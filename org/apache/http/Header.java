// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http;

public interface Header
{
    String getName();
    
    String getValue();
    
    HeaderElement[] getElements() throws ParseException;
}
