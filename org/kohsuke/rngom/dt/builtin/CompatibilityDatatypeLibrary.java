// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt.builtin;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeLibrary;

class CompatibilityDatatypeLibrary implements DatatypeLibrary
{
    private final DatatypeLibraryFactory factory;
    private DatatypeLibrary xsdDatatypeLibrary;
    
    CompatibilityDatatypeLibrary(final DatatypeLibraryFactory factory) {
        this.xsdDatatypeLibrary = null;
        this.factory = factory;
    }
    
    public DatatypeBuilder createDatatypeBuilder(final String type) throws DatatypeException {
        if (type.equals("ID") || type.equals("IDREF") || type.equals("IDREFS")) {
            if (this.xsdDatatypeLibrary == null) {
                this.xsdDatatypeLibrary = this.factory.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
                if (this.xsdDatatypeLibrary == null) {
                    throw new DatatypeException();
                }
            }
            return this.xsdDatatypeLibrary.createDatatypeBuilder(type);
        }
        throw new DatatypeException();
    }
    
    public Datatype createDatatype(final String type) throws DatatypeException {
        return this.createDatatypeBuilder(type).createDatatype();
    }
}
