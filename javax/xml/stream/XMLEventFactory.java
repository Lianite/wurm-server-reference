// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public abstract class XMLEventFactory
{
    public static XMLEventFactory newInstance() throws FactoryConfigurationError {
        return (XMLEventFactory)FactoryFinder.find("javax.xml.stream.XMLEventFactory", "com.bea.xml.stream.EventFactory");
    }
    
    public static XMLEventFactory newInstance(final String factoryId, final ClassLoader classLoader) throws FactoryConfigurationError {
        return (XMLEventFactory)FactoryFinder.find(factoryId, "com.bea.xml.stream.EventFactory", classLoader);
    }
    
    public abstract void setLocation(final Location p0);
    
    public abstract Attribute createAttribute(final String p0, final String p1, final String p2, final String p3);
    
    public abstract Attribute createAttribute(final String p0, final String p1);
    
    public abstract Attribute createAttribute(final QName p0, final String p1);
    
    public abstract Namespace createNamespace(final String p0);
    
    public abstract Namespace createNamespace(final String p0, final String p1);
    
    public abstract StartElement createStartElement(final QName p0, final Iterator p1, final Iterator p2);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2, final Iterator p3, final Iterator p4);
    
    public abstract StartElement createStartElement(final String p0, final String p1, final String p2, final Iterator p3, final Iterator p4, final NamespaceContext p5);
    
    public abstract EndElement createEndElement(final QName p0, final Iterator p1);
    
    public abstract EndElement createEndElement(final String p0, final String p1, final String p2);
    
    public abstract EndElement createEndElement(final String p0, final String p1, final String p2, final Iterator p3);
    
    public abstract Characters createCharacters(final String p0);
    
    public abstract Characters createCData(final String p0);
    
    public abstract Characters createSpace(final String p0);
    
    public abstract Characters createIgnorableSpace(final String p0);
    
    public abstract StartDocument createStartDocument();
    
    public abstract StartDocument createStartDocument(final String p0, final String p1, final boolean p2);
    
    public abstract StartDocument createStartDocument(final String p0, final String p1);
    
    public abstract StartDocument createStartDocument(final String p0);
    
    public abstract EndDocument createEndDocument();
    
    public abstract EntityReference createEntityReference(final String p0, final EntityDeclaration p1);
    
    public abstract Comment createComment(final String p0);
    
    public abstract ProcessingInstruction createProcessingInstruction(final String p0, final String p1);
    
    public abstract DTD createDTD(final String p0);
}
