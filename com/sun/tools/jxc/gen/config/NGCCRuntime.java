// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.SAXParseException;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.Stack;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;

public class NGCCRuntime implements ContentHandler, NGCCEventSource
{
    private Locator locator;
    private final Stack attStack;
    private AttributesImpl currentAtts;
    private StringBuffer text;
    private NGCCEventReceiver currentHandler;
    static final String IMPOSSIBLE = "\u0000";
    private ContentHandler redirect;
    private int redirectionDepth;
    private final ArrayList namespaces;
    private int nsEffectivePtr;
    private final Stack nsEffectiveStack;
    private int indent;
    private boolean needIndent;
    
    public NGCCRuntime() {
        this.attStack = new Stack();
        this.text = new StringBuffer();
        this.redirect = null;
        this.redirectionDepth = 0;
        this.namespaces = new ArrayList();
        this.nsEffectivePtr = 0;
        this.nsEffectiveStack = new Stack();
        this.indent = 0;
        this.needIndent = true;
        this.reset();
    }
    
    public void setRootHandler(final NGCCHandler rootHandler) {
        if (this.currentHandler != null) {
            throw new IllegalStateException();
        }
        this.currentHandler = rootHandler;
    }
    
    public void reset() {
        this.attStack.clear();
        this.currentAtts = null;
        this.currentHandler = null;
        this.indent = 0;
        this.locator = null;
        this.namespaces.clear();
        this.needIndent = true;
        this.redirect = null;
        this.redirectionDepth = 0;
        this.text = new StringBuffer();
        this.attStack.push(new AttributesImpl());
    }
    
    public void setDocumentLocator(final Locator _loc) {
        this.locator = _loc;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public Attributes getCurrentAttributes() {
        return this.currentAtts;
    }
    
    public int replace(final NGCCEventReceiver o, final NGCCEventReceiver n) {
        if (o != this.currentHandler) {
            throw new IllegalStateException();
        }
        this.currentHandler = n;
        return 0;
    }
    
    private void processPendingText(final boolean ignorable) throws SAXException {
        if (!ignorable || this.text.toString().trim().length() != 0) {
            this.currentHandler.text(this.text.toString());
        }
        if (this.text.length() > 1024) {
            this.text = new StringBuffer();
        }
        else {
            this.text.setLength(0);
        }
    }
    
    public void processList(final String str) throws SAXException {
        final StringTokenizer t = new StringTokenizer(str, " \t\r\n");
        while (t.hasMoreTokens()) {
            this.currentHandler.text(t.nextToken());
        }
    }
    
    public void startElement(String uri, String localname, String qname, final Attributes atts) throws SAXException {
        uri = uri.intern();
        localname = localname.intern();
        qname = qname.intern();
        if (this.redirect != null) {
            this.redirect.startElement(uri, localname, qname, atts);
            ++this.redirectionDepth;
        }
        else {
            this.processPendingText(true);
            this.currentHandler.enterElement(uri, localname, qname, atts);
        }
    }
    
    public void onEnterElementConsumed(final String uri, final String localName, final String qname, final Attributes atts) throws SAXException {
        this.attStack.push(this.currentAtts = new AttributesImpl(atts));
        this.nsEffectiveStack.push(new Integer(this.nsEffectivePtr));
        this.nsEffectivePtr = this.namespaces.size();
    }
    
    public void onLeaveElementConsumed(final String uri, final String localName, final String qname) throws SAXException {
        this.attStack.pop();
        if (this.attStack.isEmpty()) {
            this.currentAtts = null;
        }
        else {
            this.currentAtts = this.attStack.peek();
        }
        this.nsEffectivePtr = this.nsEffectiveStack.pop();
    }
    
    public void endElement(String uri, String localname, String qname) throws SAXException {
        uri = uri.intern();
        localname = localname.intern();
        qname = qname.intern();
        if (this.redirect != null) {
            this.redirect.endElement(uri, localname, qname);
            --this.redirectionDepth;
            if (this.redirectionDepth != 0) {
                return;
            }
            for (int i = 0; i < this.namespaces.size(); i += 2) {
                this.redirect.endPrefixMapping(this.namespaces.get(i));
            }
            this.redirect.endDocument();
            this.redirect = null;
        }
        this.processPendingText(false);
        this.currentHandler.leaveElement(uri, localname, qname);
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.redirect != null) {
            this.redirect.characters(ch, start, length);
        }
        else {
            this.text.append(ch, start, length);
        }
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.redirect != null) {
            this.redirect.ignorableWhitespace(ch, start, length);
        }
        else {
            this.text.append(ch, start, length);
        }
    }
    
    public int getAttributeIndex(final String uri, final String localname) {
        return this.currentAtts.getIndex(uri, localname);
    }
    
    public void consumeAttribute(final int index) throws SAXException {
        final String uri = this.currentAtts.getURI(index).intern();
        final String local = this.currentAtts.getLocalName(index).intern();
        final String qname = this.currentAtts.getQName(index).intern();
        final String value = this.currentAtts.getValue(index);
        this.currentAtts.removeAttribute(index);
        this.currentHandler.enterAttribute(uri, local, qname);
        this.currentHandler.text(value);
        this.currentHandler.leaveAttribute(uri, local, qname);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.redirect != null) {
            this.redirect.startPrefixMapping(prefix, uri);
        }
        else {
            this.namespaces.add(prefix);
            this.namespaces.add(uri);
        }
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.redirect != null) {
            this.redirect.endPrefixMapping(prefix);
        }
        else {
            this.namespaces.remove(this.namespaces.size() - 1);
            this.namespaces.remove(this.namespaces.size() - 1);
        }
    }
    
    public void skippedEntity(final String name) throws SAXException {
        if (this.redirect != null) {
            this.redirect.skippedEntity(name);
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.redirect != null) {
            this.redirect.processingInstruction(target, data);
        }
    }
    
    public void endDocument() throws SAXException {
        this.currentHandler.leaveElement("\u0000", "\u0000", "\u0000");
        this.reset();
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void sendEnterAttribute(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this.currentHandler.enterAttribute(uri, local, qname);
    }
    
    public void sendEnterElement(final int threadId, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this.currentHandler.enterElement(uri, local, qname, atts);
    }
    
    public void sendLeaveAttribute(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this.currentHandler.leaveAttribute(uri, local, qname);
    }
    
    public void sendLeaveElement(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this.currentHandler.leaveElement(uri, local, qname);
    }
    
    public void sendText(final int threadId, final String value) throws SAXException {
        this.currentHandler.text(value);
    }
    
    public void redirectSubtree(final ContentHandler child, final String uri, final String local, final String qname) throws SAXException {
        (this.redirect = child).setDocumentLocator(this.locator);
        this.redirect.startDocument();
        for (int i = 0; i < this.namespaces.size(); i += 2) {
            this.redirect.startPrefixMapping(this.namespaces.get(i), this.namespaces.get(i + 1));
        }
        this.redirect.startElement(uri, local, qname, this.currentAtts);
        this.redirectionDepth = 1;
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        for (int i = this.nsEffectivePtr - 2; i >= 0; i -= 2) {
            if (this.namespaces.get(i).equals(prefix)) {
                return this.namespaces.get(i + 1);
            }
        }
        if (prefix.equals("")) {
            return "";
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        return null;
    }
    
    protected void unexpectedX(final String token) throws SAXException {
        throw new SAXParseException(MessageFormat.format("Unexpected {0} appears at line {1} column {2}", token, new Integer(this.getLocator().getLineNumber()), new Integer(this.getLocator().getColumnNumber())), this.getLocator());
    }
    
    private void printIndent() {
        for (int i = 0; i < this.indent; ++i) {
            System.out.print("  ");
        }
    }
    
    public void trace(final String s) {
        if (this.needIndent) {
            this.needIndent = false;
            this.printIndent();
        }
        System.out.print(s);
    }
    
    public void traceln(final String s) {
        this.trace(s);
        this.trace("\n");
        this.needIndent = true;
    }
}
