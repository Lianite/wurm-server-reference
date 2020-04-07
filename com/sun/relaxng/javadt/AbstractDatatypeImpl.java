// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.relaxng.javadt;

import org.relaxng.datatype.helpers.StreamingValidatorImpl;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;

public abstract class AbstractDatatypeImpl implements Datatype
{
    public void checkValid(final String name, final ValidationContext context) throws DatatypeException {
        if (this.isValid(name, context)) {
            throw new DatatypeException();
        }
    }
    
    public DatatypeStreamingValidator createStreamingValidator(final ValidationContext context) {
        return new StreamingValidatorImpl(this, context);
    }
    
    public Object createValue(final String text, final ValidationContext context) {
        if (!this.isValid(text, context)) {
            return null;
        }
        return text.trim();
    }
    
    public boolean sameValue(final Object obj1, final Object obj2) {
        return obj1.equals(obj2);
    }
    
    public int valueHashCode(final Object obj) {
        return obj.hashCode();
    }
    
    public int getIdType() {
        return 0;
    }
    
    public boolean isContextDependent() {
        return true;
    }
}
