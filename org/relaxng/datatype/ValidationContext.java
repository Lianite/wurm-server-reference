// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public interface ValidationContext
{
    String resolveNamespacePrefix(final String p0);
    
    String getBaseUri();
    
    boolean isUnparsedEntity(final String p0);
    
    boolean isNotation(final String p0);
}
