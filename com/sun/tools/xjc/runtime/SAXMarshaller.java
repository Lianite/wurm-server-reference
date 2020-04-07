// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.xml.bind.JAXBAssertionError;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.serializer.AbortSerializationException;
import java.util.Iterator;
import java.util.Collection;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.NotIdentifiableEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import com.sun.xml.bind.marshaller.Messages;
import com.sun.xml.bind.marshaller.IdentifiableObject;
import com.sun.xml.bind.serializer.Util;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.HashSet;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.bind.JAXBObject;
import java.util.Set;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SAXMarshaller implements XMLSerializer
{
    private final AttributesImpl attributes;
    private final ContentHandler writer;
    private final MarshallerImpl owner;
    private final Set idReferencedObjects;
    private final Set objectsWithId;
    private JAXBObject currentTarget;
    private final NamespaceContextImpl nsContext;
    private String[] elementStack;
    private int elementLen;
    private final PrefixCallback startPrefixCallback;
    private final PrefixCallback endPrefixCallback;
    private final StringBuffer textBuf;
    private String attNamespaceUri;
    private String attLocalName;
    private static NamespacePrefixMapper defaultNamespacePrefixMapper;
    
    public SAXMarshaller(final ContentHandler _writer, final NamespacePrefixMapper prefixMapper, final MarshallerImpl _owner) {
        this.attributes = new AttributesImpl();
        this.idReferencedObjects = new HashSet();
        this.objectsWithId = new HashSet();
        this.elementStack = new String[16];
        this.elementLen = 0;
        this.startPrefixCallback = (PrefixCallback)new SAXMarshaller$1(this);
        this.endPrefixCallback = (PrefixCallback)new SAXMarshaller$2(this);
        this.textBuf = new StringBuffer();
        this.writer = _writer;
        this.owner = _owner;
        this.nsContext = new NamespaceContextImpl((prefixMapper != null) ? prefixMapper : SAXMarshaller.defaultNamespacePrefixMapper);
    }
    
    public NamespaceContext2 getNamespaceContext() {
        return (NamespaceContext2)this.nsContext;
    }
    
    private void pushElement(final String uri, final String local) {
        if (this.elementStack.length == this.elementLen) {
            final String[] buf = new String[this.elementStack.length * 2];
            System.arraycopy(this.elementStack, 0, buf, 0, this.elementStack.length);
            this.elementStack = buf;
        }
        this.elementStack[this.elementLen++] = uri;
        this.elementStack[this.elementLen++] = local;
    }
    
    private void popElement() {
        this.elementLen -= 2;
    }
    
    private String getCurrentElementUri() {
        return this.elementStack[this.elementLen - 2];
    }
    
    private String getCurrentElementLocal() {
        return this.elementStack[this.elementLen - 1];
    }
    
    public void startElement(final String uri, final String local) throws SAXException {
        boolean isRoot = false;
        String suggestion = null;
        if (this.elementLen == 0) {
            isRoot = true;
            suggestion = "";
        }
        this.writePendingText();
        this.nsContext.startElement();
        this.pushElement(uri, local);
        this.nsContext.declareNamespace(uri, suggestion, false);
        if (isRoot) {
            final String[] uris = this.nsContext.getNamespacePrefixMapper().getPreDeclaredNamespaceUris();
            if (uris != null) {
                for (int i = 0; i < uris.length; ++i) {
                    if (uris[i] != null) {
                        this.nsContext.declareNamespace(uris[i], (String)null, false);
                    }
                }
            }
        }
    }
    
    public void endNamespaceDecls() throws SAXException {
        this.nsContext.endNamespaceDecls();
    }
    
    public void endAttributes() throws SAXException {
        final String uri = this.getCurrentElementUri();
        final String local = this.getCurrentElementLocal();
        final String prefix = this.nsContext.getPrefix(uri);
        _assert(prefix != null);
        String qname;
        if (prefix.length() != 0) {
            qname = prefix + ':' + local;
        }
        else {
            qname = local;
        }
        this.nsContext.iterateDeclaredPrefixes(this.startPrefixCallback);
        this.writer.startElement(uri, local, qname, this.attributes);
        this.attributes.clear();
        this.textBuf.setLength(0);
    }
    
    public void endElement() throws SAXException {
        this.writePendingText();
        final String uri = this.getCurrentElementUri();
        final String local = this.getCurrentElementLocal();
        final String prefix = this.nsContext.getPrefix(uri);
        _assert(prefix != null);
        String qname;
        if (prefix.length() != 0) {
            qname = prefix + ':' + local;
        }
        else {
            qname = local;
        }
        this.writer.endElement(uri, local, qname);
        this.nsContext.iterateDeclaredPrefixes(this.endPrefixCallback);
        this.popElement();
        this.textBuf.setLength(0);
        this.nsContext.endElement();
    }
    
    public void text(final String text, final String fieldName) throws SAXException {
        if (text == null) {
            this.reportError(Util.createMissingObjectError((Object)this.currentTarget, fieldName));
            return;
        }
        if (this.textBuf.length() != 0) {
            this.textBuf.append(' ');
        }
        this.textBuf.append(text);
    }
    
    private void writePendingText() throws SAXException {
        final int len = this.textBuf.length();
        if (len != 0) {
            this.writer.characters(this.textBuf.toString().toCharArray(), 0, len);
        }
    }
    
    public void startAttribute(final String uri, final String local) {
        this.textBuf.setLength(0);
        this.attNamespaceUri = uri;
        this.attLocalName = local;
    }
    
    public void endAttribute() {
        String qname;
        if (this.attNamespaceUri.length() == 0) {
            qname = this.attLocalName;
        }
        else {
            qname = this.nsContext.declareNamespace(this.attNamespaceUri, (String)null, true) + ':' + this.attLocalName;
        }
        this.attributes.addAttribute(this.attNamespaceUri, this.attLocalName, qname, "CDATA", this.textBuf.toString());
    }
    
    public String onID(final IdentifiableObject owner, final String value) throws SAXException {
        this.objectsWithId.add(owner);
        return value;
    }
    
    public String onIDREF(final IdentifiableObject obj) throws SAXException {
        this.idReferencedObjects.add(obj);
        final String id = obj.____jaxb____getId();
        if (id == null) {
            this.reportError(new NotIdentifiableEventImpl(1, Messages.format("SAXMarshaller.NotIdentifiable"), new ValidationEventLocatorImpl(obj)));
        }
        return id;
    }
    
    void reconcileID() throws AbortSerializationException {
        this.idReferencedObjects.removeAll(this.objectsWithId);
        for (final IdentifiableObject o : this.idReferencedObjects) {
            this.reportError(new NotIdentifiableEventImpl(1, Messages.format("SAXMarshaller.DanglingIDREF", o.____jaxb____getId()), new ValidationEventLocatorImpl(o)));
        }
        this.idReferencedObjects.clear();
        this.objectsWithId.clear();
    }
    
    public void childAsBody(final JAXBObject o, final String fieldName) throws SAXException {
        if (o == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        final JAXBObject oldTarget = this.currentTarget;
        this.currentTarget = o;
        this.owner.context.getGrammarInfo().castToXMLSerializable((Object)o).serializeBody((XMLSerializer)this);
        this.currentTarget = oldTarget;
    }
    
    public void childAsAttributes(final JAXBObject o, final String fieldName) throws SAXException {
        if (o == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        final JAXBObject oldTarget = this.currentTarget;
        this.currentTarget = o;
        this.owner.context.getGrammarInfo().castToXMLSerializable((Object)o).serializeAttributes((XMLSerializer)this);
        this.currentTarget = oldTarget;
    }
    
    public void childAsURIs(final JAXBObject o, final String fieldName) throws SAXException {
        if (o == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        final JAXBObject oldTarget = this.currentTarget;
        this.currentTarget = o;
        this.owner.context.getGrammarInfo().castToXMLSerializable((Object)o).serializeURIs((XMLSerializer)this);
        this.currentTarget = oldTarget;
    }
    
    public void reportError(final ValidationEvent ve) throws AbortSerializationException {
        ValidationEventHandler handler;
        try {
            handler = this.owner.getEventHandler();
        }
        catch (JAXBException e) {
            throw new AbortSerializationException((Exception)e);
        }
        if (handler.handleEvent(ve)) {
            return;
        }
        if (ve.getLinkedException() instanceof Exception) {
            throw new AbortSerializationException((Exception)ve.getLinkedException());
        }
        throw new AbortSerializationException(ve.getMessage());
    }
    
    public void reportMissingObjectError(final String fieldName) throws SAXException {
        this.reportError(Util.createMissingObjectError((Object)this.currentTarget, fieldName));
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    static {
        SAXMarshaller.defaultNamespacePrefixMapper = (NamespacePrefixMapper)new SAXMarshaller$3();
    }
}
