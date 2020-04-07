// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.xml;

import org.xml.sax.SAXException;
import java.util.Hashtable;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.DTDHandler;

public abstract class DtdContext implements DTDHandler, ValidationContext
{
    private final Hashtable notationTable;
    private final Hashtable unparsedEntityTable;
    
    public DtdContext() {
        this.notationTable = new Hashtable();
        this.unparsedEntityTable = new Hashtable();
    }
    
    public DtdContext(final DtdContext dc) {
        this.notationTable = dc.notationTable;
        this.unparsedEntityTable = dc.unparsedEntityTable;
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        this.notationTable.put(name, name);
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        this.unparsedEntityTable.put(name, name);
    }
    
    public boolean isNotation(final String notationName) {
        return this.notationTable.get(notationName) != null;
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        return this.unparsedEntityTable.get(entityName) != null;
    }
    
    public void clearDtdContext() {
        this.notationTable.clear();
        this.unparsedEntityTable.clear();
    }
}
