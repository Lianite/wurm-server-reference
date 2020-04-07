// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.output;

import javax.xml.stream.XMLStreamException;
import com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.XMLStreamWriterEx;

public final class StAXExStreamWriterOutput extends XMLStreamWriterOutput
{
    private final XMLStreamWriterEx out;
    
    public StAXExStreamWriterOutput(final XMLStreamWriterEx out) {
        super((XMLStreamWriter)out);
        this.out = out;
    }
    
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        if (!(value instanceof Base64Data)) {
            this.out.writeCharacters(value.toString());
        }
        else {
            final Base64Data v = (Base64Data)value;
            this.out.writeBinary(v.getDataHandler());
        }
    }
}
