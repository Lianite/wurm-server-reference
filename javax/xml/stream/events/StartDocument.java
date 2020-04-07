// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream.events;

public interface StartDocument extends XMLEvent
{
    String getSystemId();
    
    String getCharacterEncodingScheme();
    
    boolean encodingSet();
    
    boolean isStandalone();
    
    boolean standaloneSet();
    
    String getVersion();
}
