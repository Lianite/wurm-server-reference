// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface EntityReference extends XMLEvent
{
    EntityDeclaration getDeclaration();
    
    String getName();
}
