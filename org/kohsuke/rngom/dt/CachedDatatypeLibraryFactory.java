// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class CachedDatatypeLibraryFactory implements DatatypeLibraryFactory
{
    private String lastUri;
    private DatatypeLibrary lastLib;
    private final DatatypeLibraryFactory core;
    
    public CachedDatatypeLibraryFactory(final DatatypeLibraryFactory core) {
        this.core = core;
    }
    
    public DatatypeLibrary createDatatypeLibrary(final String namespaceURI) {
        if (this.lastUri == namespaceURI) {
            return this.lastLib;
        }
        this.lastUri = namespaceURI;
        return this.lastLib = this.core.createDatatypeLibrary(namespaceURI);
    }
}
