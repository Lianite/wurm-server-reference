// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype.helpers;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;

public final class ParameterlessDatatypeBuilder implements DatatypeBuilder
{
    private final Datatype baseType;
    
    public ParameterlessDatatypeBuilder(final Datatype baseType) {
        this.baseType = baseType;
    }
    
    public void addParameter(final String s, final String s2, final ValidationContext validationContext) throws DatatypeException {
        throw new DatatypeException();
    }
    
    public Datatype createDatatype() throws DatatypeException {
        return this.baseType;
    }
}
