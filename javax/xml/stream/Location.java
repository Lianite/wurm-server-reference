// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

public interface Location
{
    int getLineNumber();
    
    int getColumnNumber();
    
    int getCharacterOffset();
    
    String getPublicId();
    
    String getSystemId();
}
