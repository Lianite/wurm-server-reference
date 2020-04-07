// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import org.jvnet.staxex.Base64Data;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.XMLStreamReaderEx;

final class StAXExConnector extends StAXStreamConnector
{
    private final XMLStreamReaderEx in;
    
    public StAXExConnector(final XMLStreamReaderEx in, final XmlVisitor visitor) {
        super((XMLStreamReader)in, visitor);
        this.in = in;
    }
    
    protected void handleCharacters() throws XMLStreamException, SAXException {
        if (this.predictor.expectText()) {
            final CharSequence pcdata = this.in.getPCDATA();
            if (pcdata instanceof Base64Data) {
                final Base64Data bd = (Base64Data)pcdata;
                final com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data binary = new com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data();
                if (!bd.hasData()) {
                    binary.set(bd.getDataHandler());
                }
                else {
                    binary.set(bd.get(), bd.getDataLen(), bd.getMimeType());
                }
                this.visitor.text(binary);
                this.textReported = true;
            }
            else {
                this.buffer.append(pcdata);
            }
        }
    }
}
