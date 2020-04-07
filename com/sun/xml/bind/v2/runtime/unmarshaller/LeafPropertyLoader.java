// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

public class LeafPropertyLoader extends Loader
{
    private final TransducedAccessor xacc;
    
    public LeafPropertyLoader(final TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }
    
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            this.xacc.parse(state.prev.target, text);
        }
        catch (AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (RuntimeException e2) {
            Loader.handleParseConversionException(state, e2);
        }
    }
}
