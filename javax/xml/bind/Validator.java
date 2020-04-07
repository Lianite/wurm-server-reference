// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

public interface Validator
{
    void setEventHandler(final ValidationEventHandler p0) throws JAXBException;
    
    ValidationEventHandler getEventHandler() throws JAXBException;
    
    boolean validate(final Object p0) throws JAXBException;
    
    boolean validateRoot(final Object p0) throws JAXBException;
    
    void setProperty(final String p0, final Object p1) throws PropertyException;
    
    Object getProperty(final String p0) throws PropertyException;
}
