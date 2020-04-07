// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class ReadOnlyAdapter<OnTheWire, InMemory> extends XmlAdapter<OnTheWire, InMemory>
{
    public final OnTheWire marshal(final InMemory onTheWire) {
        return null;
    }
}
