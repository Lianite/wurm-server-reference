// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt.builtin;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class BuiltinDatatypeLibraryFactory implements DatatypeLibraryFactory
{
    private final DatatypeLibrary builtinDatatypeLibrary;
    private final DatatypeLibrary compatibilityDatatypeLibrary;
    private final DatatypeLibraryFactory core;
    
    public BuiltinDatatypeLibraryFactory(final DatatypeLibraryFactory coreFactory) {
        this.builtinDatatypeLibrary = new BuiltinDatatypeLibrary(coreFactory);
        this.compatibilityDatatypeLibrary = new CompatibilityDatatypeLibrary(coreFactory);
        this.core = coreFactory;
    }
    
    public DatatypeLibrary createDatatypeLibrary(final String uri) {
        if (uri.equals("")) {
            return this.builtinDatatypeLibrary;
        }
        if (uri.equals("http://relaxng.org/ns/compatibility/datatypes/1.0")) {
            return this.compatibilityDatatypeLibrary;
        }
        return this.core.createDatatypeLibrary(uri);
    }
}
