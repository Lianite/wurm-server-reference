// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface NotationDeclaration extends XMLEvent
{
    String getName();
    
    String getPublicId();
    
    String getSystemId();
}
