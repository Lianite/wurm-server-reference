// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter extends XmlAdapter<Object, Object>
{
    public Object unmarshal(final Object v) {
        return v;
    }
    
    public Object marshal(final Object v) {
        return v;
    }
}
