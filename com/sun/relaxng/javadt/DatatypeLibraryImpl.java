// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.relaxng.javadt;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.helpers.ParameterlessDatatypeBuilder;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeLibrary;

public class DatatypeLibraryImpl implements DatatypeLibrary, DatatypeLibraryFactory
{
    public static final String NAMESPACE_URI = "http://java.sun.com/xml/ns/relaxng/java-datatypes";
    
    public DatatypeBuilder createDatatypeBuilder(final String name) throws DatatypeException {
        return new ParameterlessDatatypeBuilder(this.createDatatype(name));
    }
    
    public Datatype createDatatype(final String name) throws DatatypeException {
        if ("identifier".equals(name)) {
            return JavaIdentifierDatatype.theInstance;
        }
        if ("package".equals(name)) {
            return JavaPackageDatatype.theInstance;
        }
        throw new DatatypeException();
    }
    
    public DatatypeLibrary createDatatypeLibrary(final String namespaceUri) {
        if ("http://java.sun.com/xml/ns/relaxng/java-datatypes".equals(namespaceUri)) {
            return this;
        }
        return null;
    }
}
