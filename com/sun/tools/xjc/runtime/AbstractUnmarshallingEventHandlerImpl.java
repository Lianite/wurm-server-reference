// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.Element;
import javax.xml.bind.ParseConversionEvent;
import javax.xml.bind.helpers.ParseConversionEventImpl;
import com.sun.xml.bind.JAXBAssertionError;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import com.sun.xml.bind.unmarshaller.Messages;
import java.util.StringTokenizer;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public abstract class AbstractUnmarshallingEventHandlerImpl implements UnmarshallingEventHandler
{
    public final UnmarshallingContext context;
    private final String stateTextTypes;
    public int state;
    
    public AbstractUnmarshallingEventHandlerImpl(final UnmarshallingContext _ctxt, final String _stateTextTypes) {
        this.context = _ctxt;
        this.stateTextTypes = _stateTextTypes;
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.unexpectedEnterElement(uri, local, qname, atts);
    }
    
    public void leaveElement(final String uri, final String local, final String qname) throws SAXException {
        this.unexpectedLeaveElement(uri, local, qname);
    }
    
    public final void text(final String text) throws SAXException {
        if (this.isListState()) {
            final StringTokenizer tokens = new StringTokenizer(text);
            if (tokens.countTokens() == 1) {
                this.handleText(text);
            }
            else {
                while (tokens.hasMoreTokens()) {
                    this.context.getCurrentHandler().text(tokens.nextToken());
                }
            }
        }
        else {
            this.handleText(text);
        }
    }
    
    protected void handleText(final String s) throws SAXException {
        this.unexpectedText(s);
    }
    
    public void enterAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.unexpectedEnterAttribute(uri, local, qname);
    }
    
    public void leaveAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.unexpectedLeaveAttribute(uri, local, qname);
    }
    
    public void leaveChild(final int nextState) throws SAXException {
        this.state = nextState;
    }
    
    protected final boolean isListState() {
        return this.stateTextTypes.charAt(this.state) == 'L';
    }
    
    protected void handleUnexpectedTextException(final String text, final RuntimeException e) throws SAXException {
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedText", text), e, true);
    }
    
    protected void handleGenericException(final Exception e) throws SAXException {
        this.reportError(e.getMessage(), e, false);
    }
    
    protected final void dump() {
        System.err.println("state is :" + this.state);
    }
    
    private void reportError(final String msg, final boolean canRecover) throws SAXException {
        this.reportError(msg, null, canRecover);
    }
    
    private void reportError(final String msg, final Exception nested, final boolean canRecover) throws SAXException {
        this.context.handleEvent((ValidationEvent)new ValidationEventImpl(canRecover ? 1 : 2, msg, new ValidationEventLocatorImpl(this.context.getLocator()), nested), canRecover);
    }
    
    protected final void unexpectedEnterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedEnterElement", uri, local), true);
        this.context.pushContentHandler((UnmarshallingEventHandler)new Discarder(this.context), this.state);
        this.context.getCurrentHandler().enterElement(uri, local, qname, atts);
    }
    
    protected final void unexpectedLeaveElement(final String uri, final String local, final String qname) throws SAXException {
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedLeaveElement", uri, local), false);
    }
    
    protected final void unexpectedEnterAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedEnterAttribute", uri, local), false);
    }
    
    protected final void unexpectedLeaveAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedLeaveAttribute", uri, local), false);
    }
    
    protected final void unexpectedText(String str) throws SAXException {
        str = str.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim();
        this.reportError(Messages.format("ContentHandlerEx.UnexpectedText", str), true);
    }
    
    protected final void unexpectedLeaveChild() throws SAXException {
        this.dump();
        throw new JAXBAssertionError(Messages.format("ContentHandlerEx.UnexpectedLeaveChild"));
    }
    
    protected void handleParseConversionException(final Exception e) throws SAXException {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        final ParseConversionEvent pce = new ParseConversionEventImpl(1, e.getMessage(), new ValidationEventLocatorImpl(this.context.getLocator()), e);
        this.context.handleEvent((ValidationEvent)pce, true);
    }
    
    private UnmarshallingEventHandler spawnChild(final Class clazz, final int memento) {
        UnmarshallableObject child;
        try {
            child = clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }
        catch (IllegalAccessException e2) {
            throw new IllegalAccessError(e2.getMessage());
        }
        final UnmarshallingEventHandler handler = child.createUnmarshaller(this.context);
        this.context.pushContentHandler(handler, memento);
        return handler;
    }
    
    protected final Object spawnChildFromEnterElement(final Class clazz, final int memento, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        final UnmarshallingEventHandler ueh = this.spawnChild(clazz, memento);
        ueh.enterElement(uri, local, qname, atts);
        return ueh.owner();
    }
    
    protected final Object spawnChildFromEnterAttribute(final Class clazz, final int memento, final String uri, final String local, final String qname) throws SAXException {
        final UnmarshallingEventHandler ueh = this.spawnChild(clazz, memento);
        ueh.enterAttribute(uri, local, qname);
        return ueh.owner();
    }
    
    protected final Object spawnChildFromText(final Class clazz, final int memento, final String value) throws SAXException {
        final UnmarshallingEventHandler ueh = this.spawnChild(clazz, memento);
        ueh.text(value);
        return ueh.owner();
    }
    
    protected final Object spawnChildFromLeaveElement(final Class clazz, final int memento, final String uri, final String local, final String qname) throws SAXException {
        final UnmarshallingEventHandler ueh = this.spawnChild(clazz, memento);
        ueh.leaveElement(uri, local, qname);
        return ueh.owner();
    }
    
    protected final Object spawnChildFromLeaveAttribute(final Class clazz, final int memento, final String uri, final String local, final String qname) throws SAXException {
        final UnmarshallingEventHandler ueh = this.spawnChild(clazz, memento);
        ueh.leaveAttribute(uri, local, qname);
        return ueh.owner();
    }
    
    protected final Element spawnWildcard(final int memento, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        final UnmarshallingEventHandler ueh = this.context.getGrammarInfo().createUnmarshaller(uri, local, this.context);
        if (ueh != null) {
            this.context.pushContentHandler(ueh, memento);
            ueh.enterElement(uri, local, qname, atts);
            return (Element)ueh.owner();
        }
        this.context.pushContentHandler((UnmarshallingEventHandler)new Discarder(this.context), memento);
        this.context.getCurrentHandler().enterElement(uri, local, qname, atts);
        return null;
    }
    
    protected final void spawnHandlerFromEnterElement(final UnmarshallingEventHandler unm, final int memento, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.enterElement(uri, local, qname, atts);
    }
    
    protected final void spawnHandlerFromEnterAttribute(final UnmarshallingEventHandler unm, final int memento, final String uri, final String local, final String qname) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.enterAttribute(uri, local, qname);
    }
    
    protected final void spawnHandlerFromFromText(final UnmarshallingEventHandler unm, final int memento, final String value) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.text(value);
    }
    
    protected final void spawnHandlerFromLeaveElement(final UnmarshallingEventHandler unm, final int memento, final String uri, final String local, final String qname) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.leaveElement(uri, local, qname);
    }
    
    protected final void spawnHandlerFromLeaveAttribute(final UnmarshallingEventHandler unm, final int memento, final String uri, final String local, final String qname) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.leaveAttribute(uri, local, qname);
    }
    
    protected final void spawnHandlerFromText(final UnmarshallingEventHandler unm, final int memento, final String text) throws SAXException {
        this.context.pushContentHandler(unm, memento);
        unm.text(text);
    }
    
    protected final void revertToParentFromEnterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.context.popContentHandler();
        this.context.getCurrentHandler().enterElement(uri, local, qname, atts);
    }
    
    protected final void revertToParentFromLeaveElement(final String uri, final String local, final String qname) throws SAXException {
        this.context.popContentHandler();
        this.context.getCurrentHandler().leaveElement(uri, local, qname);
    }
    
    protected final void revertToParentFromEnterAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.context.popContentHandler();
        this.context.getCurrentHandler().enterAttribute(uri, local, qname);
    }
    
    protected final void revertToParentFromLeaveAttribute(final String uri, final String local, final String qname) throws SAXException {
        this.context.popContentHandler();
        this.context.getCurrentHandler().leaveAttribute(uri, local, qname);
    }
    
    protected final void revertToParentFromText(final String value) throws SAXException {
        this.context.popContentHandler();
        this.context.getCurrentHandler().text(value);
    }
}
