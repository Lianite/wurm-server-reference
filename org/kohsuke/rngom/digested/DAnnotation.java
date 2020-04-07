// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.xml.sax.Locator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.Map;

public class DAnnotation
{
    static final DAnnotation EMPTY;
    final Map<QName, Attribute> attributes;
    final List<Element> contents;
    
    public DAnnotation() {
        this.attributes = new HashMap<QName, Attribute>();
        this.contents = new ArrayList<Element>();
    }
    
    public Attribute getAttribute(final String nsUri, final String localName) {
        return this.getAttribute(new QName(nsUri, localName));
    }
    
    public Attribute getAttribute(final QName n) {
        return this.attributes.get(n);
    }
    
    public Map<QName, Attribute> getAttributes() {
        return Collections.unmodifiableMap((Map<? extends QName, ? extends Attribute>)this.attributes);
    }
    
    public List<Element> getChildren() {
        return Collections.unmodifiableList((List<? extends Element>)this.contents);
    }
    
    static {
        EMPTY = new DAnnotation();
    }
    
    public static class Attribute
    {
        private final String ns;
        private final String localName;
        private final String prefix;
        private String value;
        private Locator loc;
        
        public Attribute(final String ns, final String localName, final String prefix) {
            this.ns = ns;
            this.localName = localName;
            this.prefix = prefix;
        }
        
        public Attribute(final String ns, final String localName, final String prefix, final String value, final Locator loc) {
            this.ns = ns;
            this.localName = localName;
            this.prefix = prefix;
            this.value = value;
            this.loc = loc;
        }
        
        public String getNs() {
            return this.ns;
        }
        
        public String getLocalName() {
            return this.localName;
        }
        
        public String getPrefix() {
            return this.prefix;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public Locator getLoc() {
            return this.loc;
        }
    }
}
