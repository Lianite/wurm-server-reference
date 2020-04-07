// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface ProcessingInstruction extends XMLEvent
{
    String getTarget();
    
    String getData();
}
