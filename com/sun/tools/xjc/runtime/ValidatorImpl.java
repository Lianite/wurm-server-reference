// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.PropertyException;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationException;
import com.sun.xml.bind.validator.Messages;
import javax.xml.bind.DatatypeConverter;
import com.sun.xml.bind.DatatypeConverterImpl;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Validator;

public class ValidatorImpl implements Validator
{
    private ValidationEventHandler eventHandler;
    final DefaultJAXBContextImpl jaxbContext;
    
    public ValidatorImpl(final DefaultJAXBContextImpl c) {
        this.eventHandler = new DefaultValidationEventHandler();
        DatatypeConverter.setDatatypeConverter(DatatypeConverterImpl.theInstance);
        this.jaxbContext = c;
    }
    
    public boolean validateRoot(final Object o) throws ValidationException {
        if (o == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"rootObj"));
        }
        return this.validate(o, true);
    }
    
    public boolean validate(final Object o) throws ValidationException {
        if (o == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"subrootObj"));
        }
        return this.validate(o, false);
    }
    
    private boolean validate(final Object o, final boolean validateId) throws ValidationException {
        try {
            final ValidatableObject vo = this.jaxbContext.getGrammarInfo().castToValidatableObject(o);
            if (vo == null) {
                throw new ValidationException(Messages.format("Validator.NotValidatable"));
            }
            final EventInterceptor ei = new EventInterceptor(this.eventHandler);
            final ValidationContext context = new ValidationContext(this.jaxbContext, (ValidationEventHandler)ei, validateId);
            context.validate(vo);
            context.reconcileIDs();
            return !ei.hadError();
        }
        catch (SAXException e) {
            final Exception nested = e.getException();
            if (nested != null) {
                throw new ValidationException(nested);
            }
            throw new ValidationException(e);
        }
    }
    
    public ValidationEventHandler getEventHandler() {
        return this.eventHandler;
    }
    
    public void setEventHandler(final ValidationEventHandler handler) {
        if (handler == null) {
            this.eventHandler = new DefaultValidationEventHandler();
        }
        else {
            this.eventHandler = handler;
        }
    }
    
    public void setProperty(final String name, final Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"name"));
        }
        throw new PropertyException(name, value);
    }
    
    public Object getProperty(final String name) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"name"));
        }
        throw new PropertyException(name);
    }
    
    private static class EventInterceptor implements ValidationEventHandler
    {
        private boolean hadError;
        private final ValidationEventHandler next;
        
        EventInterceptor(final ValidationEventHandler _next) {
            this.hadError = false;
            this.next = _next;
        }
        
        public boolean hadError() {
            return this.hadError;
        }
        
        public boolean handleEvent(final ValidationEvent e) {
            this.hadError = true;
            boolean result;
            if (this.next != null) {
                try {
                    result = this.next.handleEvent(e);
                }
                catch (RuntimeException re) {
                    result = false;
                }
            }
            else {
                result = false;
            }
            return result;
        }
    }
}
