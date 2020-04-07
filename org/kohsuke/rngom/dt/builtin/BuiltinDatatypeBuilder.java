// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.dt.builtin;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.kohsuke.rngom.util.Localizer;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;

class BuiltinDatatypeBuilder implements DatatypeBuilder
{
    private final Datatype dt;
    private static final Localizer localizer;
    
    BuiltinDatatypeBuilder(final Datatype dt) {
        this.dt = dt;
    }
    
    public void addParameter(final String name, final String value, final ValidationContext context) throws DatatypeException {
        throw new DatatypeException(BuiltinDatatypeBuilder.localizer.message("builtin_param"));
    }
    
    public Datatype createDatatype() {
        return this.dt;
    }
    
    static {
        localizer = new Localizer(BuiltinDatatypeBuilder.class);
    }
}
