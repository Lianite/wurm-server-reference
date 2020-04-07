// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import com.sun.xml.bind.util.AttributesImpl;

public final class AttributesExImpl extends AttributesImpl implements AttributesEx
{
    public CharSequence getData(final int idx) {
        return this.getValue(idx);
    }
    
    public CharSequence getData(final String nsUri, final String localName) {
        return this.getValue(nsUri, localName);
    }
}
