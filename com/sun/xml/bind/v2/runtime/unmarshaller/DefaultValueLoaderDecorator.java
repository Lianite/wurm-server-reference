// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public final class DefaultValueLoaderDecorator extends Loader
{
    private final Loader l;
    private final String defaultValue;
    
    public DefaultValueLoaderDecorator(final Loader l, final String defaultValue) {
        this.l = l;
        this.defaultValue = defaultValue;
    }
    
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        if (state.elementDefaultValue == null) {
            state.elementDefaultValue = this.defaultValue;
        }
        state.loader = this.l;
        this.l.startElement(state, ea);
    }
}
