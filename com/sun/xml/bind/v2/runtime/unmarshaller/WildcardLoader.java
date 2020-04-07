// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.bind.annotation.DomHandler;
import com.sun.xml.bind.v2.model.core.WildcardMode;

public final class WildcardLoader extends ProxyLoader
{
    private final DomLoader dom;
    private final WildcardMode mode;
    
    public WildcardLoader(final DomHandler dom, final WildcardMode mode) {
        this.dom = new DomLoader(dom);
        this.mode = mode;
    }
    
    protected Loader selectLoader(final UnmarshallingContext.State state, final TagName tag) throws SAXException {
        final UnmarshallingContext context = state.getContext();
        if (this.mode.allowTypedObject) {
            final Loader l = context.selectRootLoader(state, tag);
            if (l != null) {
                return l;
            }
        }
        if (this.mode.allowDom) {
            return this.dom;
        }
        return Discarder.INSTANCE;
    }
}
