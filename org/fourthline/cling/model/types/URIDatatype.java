// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.net.URISyntaxException;
import java.net.URI;

public class URIDatatype extends AbstractDatatype<URI>
{
    @Override
    public URI valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return new URI(s);
        }
        catch (URISyntaxException ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }
}
