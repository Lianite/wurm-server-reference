// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.xml.bind.serializer.AbortSerializationException;
import javax.xml.bind.helpers.ValidationEventImpl;
import java.util.Iterator;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.NotIdentifiableEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import java.util.Map;
import org.xml.sax.SAXException;
import com.sun.xml.bind.validator.Messages;
import com.sun.xml.bind.ProxyGroup;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import javax.xml.bind.ValidationEventHandler;
import java.util.HashMap;
import java.util.HashSet;

class ValidationContext
{
    final DefaultJAXBContextImpl jaxbContext;
    private final IdentityHashSet validatedObjects;
    private final NamespaceContextImpl nsContext;
    private final boolean validateID;
    private final HashSet IDs;
    private final HashMap IDREFs;
    private final ValidationEventHandler eventHandler;
    
    ValidationContext(final DefaultJAXBContextImpl _context, final ValidationEventHandler _eventHandler, final boolean validateID) {
        this.validatedObjects = new IdentityHashSet();
        this.nsContext = new NamespaceContextImpl((NamespacePrefixMapper)null);
        this.IDs = new HashSet();
        this.IDREFs = new HashMap();
        this.jaxbContext = _context;
        this.eventHandler = _eventHandler;
        this.validateID = validateID;
    }
    
    public void validate(final ValidatableObject vo) throws SAXException {
        if (this.validatedObjects.add(ProxyGroup.unwrap((Object)vo))) {
            MSVValidator.validate(this.jaxbContext, this, vo);
        }
        else {
            this.reportEvent(vo, Messages.format("ValidationContext.CycleDetected"));
        }
    }
    
    public NamespaceContextImpl getNamespaceContext() {
        return this.nsContext;
    }
    
    public String onID(final XMLSerializable owner, final String value) throws SAXException {
        if (!this.validateID) {
            return value;
        }
        if (!this.IDs.add(value)) {
            this.reportEvent(this.jaxbContext.getGrammarInfo().castToValidatableObject((Object)owner), Messages.format("ValidationContext.DuplicateId", (Object)value));
        }
        return value;
    }
    
    public String onIDREF(final XMLSerializable referer, final String value) throws SAXException {
        if (!this.validateID) {
            return value;
        }
        if (this.IDs.contains(value)) {
            return value;
        }
        this.IDREFs.put(value, referer);
        return value;
    }
    
    protected void reconcileIDs() throws SAXException {
        if (!this.validateID) {
            return;
        }
        for (final Map.Entry e : this.IDREFs.entrySet()) {
            if (this.IDs.contains(e.getKey())) {
                continue;
            }
            final ValidatableObject source = e.getValue();
            this.reportEvent(source, new NotIdentifiableEventImpl(1, Messages.format("ValidationContext.IdNotFound", e.getKey()), new ValidationEventLocatorImpl(source)));
        }
        this.IDREFs.clear();
    }
    
    public void reportEvent(final ValidatableObject source, final String formattedMessage) throws AbortSerializationException {
        this.reportEvent(source, new ValidationEventImpl(1, formattedMessage, new ValidationEventLocatorImpl(source)));
    }
    
    public void reportEvent(final ValidatableObject source, final Exception nestedException) throws AbortSerializationException {
        this.reportEvent(source, new ValidationEventImpl(1, nestedException.toString(), new ValidationEventLocatorImpl(source), nestedException));
    }
    
    public void reportEvent(final ValidatableObject source, final ValidationEvent event) throws AbortSerializationException {
        boolean r;
        try {
            r = this.eventHandler.handleEvent(event);
        }
        catch (RuntimeException re) {
            r = false;
        }
        if (!r) {
            throw new AbortSerializationException(event.getMessage());
        }
    }
}
