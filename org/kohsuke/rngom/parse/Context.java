// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse;

import java.util.Enumeration;
import org.relaxng.datatype.ValidationContext;

public interface Context extends ValidationContext
{
    Enumeration prefixes();
    
    Context copy();
}
