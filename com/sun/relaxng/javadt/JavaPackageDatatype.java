// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.relaxng.javadt;

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;

public class JavaPackageDatatype extends AbstractDatatypeImpl
{
    public static final Datatype theInstance;
    
    static {
        JavaPackageDatatype.theInstance = (Datatype)new JavaPackageDatatype();
    }
    
    public boolean isValid(final String token, final ValidationContext context) {
        return Name.isJavaPackageName(token.trim());
    }
}
