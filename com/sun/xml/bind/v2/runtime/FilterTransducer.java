// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.istack.NotNull;
import com.sun.xml.bind.api.AccessorException;

public abstract class FilterTransducer<T> implements Transducer<T>
{
    protected final Transducer<T> core;
    
    protected FilterTransducer(final Transducer<T> core) {
        this.core = core;
    }
    
    public final boolean isDefault() {
        return false;
    }
    
    public boolean useNamespace() {
        return this.core.useNamespace();
    }
    
    public void declareNamespace(final T o, final XMLSerializer w) throws AccessorException {
        this.core.declareNamespace(o, w);
    }
    
    @NotNull
    public CharSequence print(@NotNull final T o) throws AccessorException {
        return this.core.print(o);
    }
    
    public T parse(final CharSequence lexical) throws AccessorException, SAXException {
        return this.core.parse(lexical);
    }
    
    public void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.core.writeText(w, o, fieldName);
    }
    
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.core.writeLeafElement(w, tagName, o, fieldName);
    }
    
    public QName getTypeName(final T instance) {
        return null;
    }
}
