// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

import org.xml.sax.ContentHandler;

public interface UnmarshallerHandler extends ContentHandler
{
    Object getResult() throws JAXBException, IllegalStateException;
}
