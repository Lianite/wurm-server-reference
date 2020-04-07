// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt.builtin;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeLibrary;

public class BuiltinDatatypeLibrary implements DatatypeLibrary
{
    private final DatatypeLibraryFactory factory;
    private DatatypeLibrary xsdDatatypeLibrary;
    
    BuiltinDatatypeLibrary(final DatatypeLibraryFactory factory) {
        this.xsdDatatypeLibrary = null;
        this.factory = factory;
    }
    
    public DatatypeBuilder createDatatypeBuilder(final String type) throws DatatypeException {
        this.xsdDatatypeLibrary = this.factory.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
        if (this.xsdDatatypeLibrary == null) {
            throw new DatatypeException();
        }
        if (type.equals("string") || type.equals("token")) {
            return new BuiltinDatatypeBuilder(this.xsdDatatypeLibrary.createDatatype(type));
        }
        throw new DatatypeException();
    }
    
    public Datatype createDatatype(final String type) throws DatatypeException {
        return this.createDatatypeBuilder(type).createDatatype();
    }
}
