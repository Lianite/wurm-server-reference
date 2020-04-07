// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public interface DatatypeBuilder
{
    void addParameter(final String p0, final String p1, final ValidationContext p2) throws DatatypeException;
    
    Datatype createDatatype() throws DatatypeException;
}
