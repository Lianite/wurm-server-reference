// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface Namespace extends Attribute
{
    String getPrefix();
    
    String getNamespaceURI();
    
    boolean isDefaultNamespaceDeclaration();
}
