// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.xml.sax;

import org.kohsuke.rngom.util.Uri;
import org.xml.sax.Locator;

public class XmlBaseHandler
{
    private int depth;
    private Locator loc;
    private Entry stack;
    
    public XmlBaseHandler() {
        this.depth = 0;
        this.stack = null;
    }
    
    public void setLocator(final Locator loc) {
        this.loc = loc;
    }
    
    public void startElement() {
        ++this.depth;
    }
    
    public void endElement() {
        if (this.stack != null && this.stack.depth == this.depth) {
            this.stack = this.stack.parent;
        }
        --this.depth;
    }
    
    public void xmlBaseAttribute(final String value) {
        final Entry entry = new Entry();
        entry.parent = this.stack;
        (this.stack = entry).attValue = Uri.escapeDisallowedChars(value);
        entry.systemId = this.getSystemId();
        entry.depth = this.depth;
    }
    
    private String getSystemId() {
        return (this.loc == null) ? null : this.loc.getSystemId();
    }
    
    public String getBaseUri() {
        return getBaseUri1(this.getSystemId(), this.stack);
    }
    
    private static String getBaseUri1(String baseUri, final Entry stack) {
        if (stack == null || (baseUri != null && !baseUri.equals(stack.systemId))) {
            return baseUri;
        }
        baseUri = stack.attValue;
        if (Uri.isAbsolute(baseUri)) {
            return baseUri;
        }
        return Uri.resolve(getBaseUri1(stack.systemId, stack.parent), baseUri);
    }
    
    private static class Entry
    {
        private Entry parent;
        private String attValue;
        private String systemId;
        private int depth;
    }
}
