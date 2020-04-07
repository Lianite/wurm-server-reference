// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

final class ExternalEntity extends EntityDecl
{
    String systemId;
    String publicId;
    String notation;
    
    public ExternalEntity(final InputEntity in) {
    }
    
    public InputSource getInputSource(final EntityResolver r) throws IOException, SAXException {
        InputSource retval = r.resolveEntity(this.publicId, this.systemId);
        if (retval == null) {
            retval = Resolver.createInputSource(new URL(this.systemId), false);
        }
        return retval;
    }
}
