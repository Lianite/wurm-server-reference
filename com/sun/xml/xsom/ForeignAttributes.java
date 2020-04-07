// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import org.xml.sax.Locator;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Attributes;

public interface ForeignAttributes extends Attributes
{
    ValidationContext getContext();
    
    Locator getLocator();
}
