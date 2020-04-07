// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.relaxng.javadt;

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;

public class JavaIdentifierDatatype extends AbstractDatatypeImpl
{
    public static final Datatype theInstance;
    
    static {
        JavaIdentifierDatatype.theInstance = (Datatype)new JavaIdentifierDatatype();
    }
    
    public boolean isValid(final String token, final ValidationContext context) {
        return Name.isJavaIdentifier(token.trim());
    }
}
