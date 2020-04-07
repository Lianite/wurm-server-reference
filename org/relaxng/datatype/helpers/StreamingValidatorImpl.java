// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype.helpers;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeStreamingValidator;

public final class StreamingValidatorImpl implements DatatypeStreamingValidator
{
    private final StringBuffer buffer;
    private final Datatype baseType;
    private final ValidationContext context;
    
    public void addCharacters(final char[] array, final int n, final int n2) {
        this.buffer.append(array, n, n2);
    }
    
    public boolean isValid() {
        return this.baseType.isValid(this.buffer.toString(), this.context);
    }
    
    public void checkValid() throws DatatypeException {
        this.baseType.checkValid(this.buffer.toString(), this.context);
    }
    
    public StreamingValidatorImpl(final Datatype baseType, final ValidationContext context) {
        this.buffer = new StringBuffer();
        this.baseType = baseType;
        this.context = context;
    }
}
