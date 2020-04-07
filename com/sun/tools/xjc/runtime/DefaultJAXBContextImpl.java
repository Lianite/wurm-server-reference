// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.PropertyException;
import com.sun.xml.bind.Messages;
import javax.xml.bind.Validator;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.DatatypeConverter;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.msv.grammar.Grammar;
import javax.xml.bind.JAXBContext;

public class DefaultJAXBContextImpl extends JAXBContext
{
    private GrammarInfo gi;
    private Grammar grammar;
    
    public DefaultJAXBContextImpl(final String contextPath, final ClassLoader classLoader) throws JAXBException {
        this(GrammarInfoFacade.createGrammarInfoFacade(contextPath, classLoader));
        DatatypeConverter.setDatatypeConverter(DatatypeConverterImpl.theInstance);
    }
    
    public DefaultJAXBContextImpl(final GrammarInfo gi) {
        this.gi = null;
        this.grammar = null;
        this.gi = gi;
    }
    
    public GrammarInfo getGrammarInfo() {
        return this.gi;
    }
    
    public synchronized Grammar getGrammar() throws JAXBException {
        if (this.grammar == null) {
            this.grammar = this.gi.getGrammar();
        }
        return this.grammar;
    }
    
    public Marshaller createMarshaller() throws JAXBException {
        if (MetaVariable.M) {
            return (Marshaller)new MarshallerImpl(this);
        }
        throw new UnsupportedOperationException("When generating this code, the compiler option was specified not to generate the marshaller");
    }
    
    public Unmarshaller createUnmarshaller() throws JAXBException {
        if (MetaVariable.U) {
            return (Unmarshaller)new UnmarshallerImpl(this, this.gi);
        }
        throw new UnsupportedOperationException("When generating this code, the compiler option was specified not to generate the unmarshaller");
    }
    
    public Validator createValidator() throws JAXBException {
        if (MetaVariable.V) {
            return (Validator)new ValidatorImpl(this);
        }
        throw new UnsupportedOperationException("When generating this code, the compiler option was specified not to generate the validator");
    }
    
    public Object newInstance(final Class javaContentInterface) throws JAXBException {
        if (javaContentInterface == null) {
            throw new JAXBException(Messages.format("DefaultJAXBContextImpl.CINotNull"));
        }
        try {
            final Class c = this.gi.getDefaultImplementation(javaContentInterface);
            if (c == null) {
                throw new JAXBException(Messages.format("ImplementationRegistry.MissingInterface", (Object)javaContentInterface));
            }
            return c.newInstance();
        }
        catch (Exception e) {
            throw new JAXBException(e);
        }
    }
    
    public void setProperty(final String name, final Object value) throws PropertyException {
        throw new PropertyException(name, value);
    }
    
    public Object getProperty(final String name) throws PropertyException {
        throw new PropertyException(name);
    }
}
