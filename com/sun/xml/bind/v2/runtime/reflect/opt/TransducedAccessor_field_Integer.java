// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.Name;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Integer extends DefaultTransducedAccessor
{
    public String print(final Object o) {
        return DatatypeConverterImpl._printInt(((Bean)o).f_int);
    }
    
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).f_int = DatatypeConverterImpl._parseInt(lexical);
    }
    
    public boolean hasValue(final Object o) {
        return true;
    }
    
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final Object o, final String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
        w.leafElement(tagName, ((Bean)o).f_int, fieldName);
    }
}
