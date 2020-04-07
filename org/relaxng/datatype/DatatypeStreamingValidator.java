// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public interface DatatypeStreamingValidator
{
    void addCharacters(final char[] p0, final int p1, final int p2);
    
    boolean isValid();
    
    void checkValid() throws DatatypeException;
}
