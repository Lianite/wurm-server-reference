// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class CascadingDatatypeLibraryFactory implements DatatypeLibraryFactory
{
    private final DatatypeLibraryFactory factory1;
    private final DatatypeLibraryFactory factory2;
    
    public CascadingDatatypeLibraryFactory(final DatatypeLibraryFactory factory1, final DatatypeLibraryFactory factory2) {
        this.factory1 = factory1;
        this.factory2 = factory2;
    }
    
    public DatatypeLibrary createDatatypeLibrary(final String namespaceURI) {
        DatatypeLibrary lib = this.factory1.createDatatypeLibrary(namespaceURI);
        if (lib == null) {
            lib = this.factory2.createDatatypeLibrary(namespaceURI);
        }
        return lib;
    }
}
