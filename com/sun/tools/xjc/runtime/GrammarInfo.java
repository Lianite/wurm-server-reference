// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.JAXBException;
import com.sun.msv.grammar.Grammar;

public interface GrammarInfo
{
    UnmarshallingEventHandler createUnmarshaller(final String p0, final String p1, final UnmarshallingContext p2);
    
    Class getRootElement(final String p0, final String p1);
    
    String[] getProbePoints();
    
    boolean recognize(final String p0, final String p1);
    
    Class getDefaultImplementation(final Class p0);
    
    Grammar getGrammar() throws JAXBException;
    
    XMLSerializable castToXMLSerializable(final Object p0);
    
    ValidatableObject castToValidatableObject(final Object p0);
}
