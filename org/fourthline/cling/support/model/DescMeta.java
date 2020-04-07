// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.net.URI;

public class DescMeta<M>
{
    protected String id;
    protected String type;
    protected URI nameSpace;
    protected M metadata;
    
    public DescMeta() {
    }
    
    public DescMeta(final String id, final String type, final URI nameSpace, final M metadata) {
        this.id = id;
        this.type = type;
        this.nameSpace = nameSpace;
        this.metadata = metadata;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public URI getNameSpace() {
        return this.nameSpace;
    }
    
    public void setNameSpace(final URI nameSpace) {
        this.nameSpace = nameSpace;
    }
    
    public M getMetadata() {
        return this.metadata;
    }
    
    public void setMetadata(final M metadata) {
        this.metadata = metadata;
    }
    
    public Document createMetadataDocument() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            final Element rootElement = d.createElementNS("urn:fourthline-org:cling:support:content-directory-desc-1-0", "desc-wrapper");
            d.appendChild(rootElement);
            return d;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
