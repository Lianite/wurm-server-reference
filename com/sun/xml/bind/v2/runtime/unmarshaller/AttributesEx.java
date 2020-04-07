// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;

public interface AttributesEx extends Attributes
{
    CharSequence getData(final int p0);
    
    CharSequence getData(final String p0, final String p1);
}
