// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Long extends DefaultTransducedAccessor
{
    public String print(final Object o) {
        return DatatypeConverterImpl._printLong(((Bean)o).get_long());
    }
    
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).set_long(DatatypeConverterImpl._parseLong(lexical));
    }
    
    public boolean hasValue(final Object o) {
        return true;
    }
}
