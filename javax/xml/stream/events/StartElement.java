// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import javax.xml.namespace.QName;

public interface StartElement extends XMLEvent
{
    QName getName();
    
    Iterator getAttributes();
    
    Iterator getNamespaces();
    
    Attribute getAttributeByName(final QName p0);
    
    NamespaceContext getNamespaceContext();
    
    String getNamespaceURI(final String p0);
}
