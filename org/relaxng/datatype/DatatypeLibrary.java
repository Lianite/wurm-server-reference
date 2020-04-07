// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public interface DatatypeLibrary
{
    DatatypeBuilder createDatatypeBuilder(final String p0) throws DatatypeException;
    
    Datatype createDatatype(final String p0) throws DatatypeException;
}
