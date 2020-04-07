// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import org.xml.sax.SAXException;
import com.sun.xml.bind.DatatypeConverterImpl;

public class XsiNilLoader extends ProxyLoader
{
    private final Loader defaultLoader;
    
    public XsiNilLoader(final Loader defaultLoader) {
        this.defaultLoader = defaultLoader;
        assert defaultLoader != null;
    }
    
    protected Loader selectLoader(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final int idx = ea.atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "nil");
        if (idx != -1) {
            final String value = ea.atts.getValue(idx);
            if (DatatypeConverterImpl._parseBoolean(value)) {
                this.onNil(state);
                return Discarder.INSTANCE;
            }
        }
        return this.defaultLoader;
    }
    
    protected void onNil(final UnmarshallingContext.State state) throws SAXException {
    }
    
    public static final class Single extends XsiNilLoader
    {
        private final Accessor acc;
        
        public Single(final Loader l, final Accessor acc) {
            super(l);
            this.acc = acc;
        }
        
        protected void onNil(final UnmarshallingContext.State state) throws SAXException {
            try {
                this.acc.set(state.prev.target, null);
            }
            catch (AccessorException e) {
                Loader.handleGenericException(e, true);
            }
        }
    }
    
    public static final class Array extends XsiNilLoader
    {
        public Array(final Loader core) {
            super(core);
        }
        
        protected void onNil(final UnmarshallingContext.State state) {
            state.target = null;
        }
    }
}
