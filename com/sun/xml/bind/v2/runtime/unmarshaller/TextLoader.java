// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.Transducer;

public class TextLoader extends Loader
{
    private final Transducer xducer;
    
    public TextLoader(final Transducer xducer) {
        super(true);
        this.xducer = xducer;
    }
    
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            state.target = this.xducer.parse(text);
        }
        catch (AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (RuntimeException e2) {
            Loader.handleParseConversionException(state, e2);
        }
    }
}
