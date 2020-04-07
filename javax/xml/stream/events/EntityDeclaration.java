// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface EntityDeclaration extends XMLEvent
{
    String getPublicId();
    
    String getSystemId();
    
    String getName();
    
    String getNotationName();
    
    String getReplacementText();
    
    String getBaseURI();
}
