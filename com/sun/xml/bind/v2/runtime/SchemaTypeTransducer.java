// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.bind.api.AccessorException;
import javax.xml.namespace.QName;

public class SchemaTypeTransducer<V> extends FilterTransducer<V>
{
    private final QName schemaType;
    
    public SchemaTypeTransducer(final Transducer<V> core, final QName schemaType) {
        super(core);
        this.schemaType = schemaType;
    }
    
    public CharSequence print(final V o) throws AccessorException {
        final XMLSerializer w = XMLSerializer.getInstance();
        final QName old = w.setSchemaType(this.schemaType);
        try {
            return this.core.print(o);
        }
        finally {
            w.setSchemaType(old);
        }
    }
    
    public void writeText(final XMLSerializer w, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final QName old = w.setSchemaType(this.schemaType);
        try {
            this.core.writeText(w, o, fieldName);
        }
        finally {
            w.setSchemaType(old);
        }
    }
    
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final QName old = w.setSchemaType(this.schemaType);
        try {
            this.core.writeLeafElement(w, tagName, o, fieldName);
        }
        finally {
            w.setSchemaType(old);
        }
    }
}
