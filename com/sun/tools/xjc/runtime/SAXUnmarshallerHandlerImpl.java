// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.helpers.LocatorImpl;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.JAXBAssertionError;
import javax.xml.bind.ValidationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.bind.UnmarshalException;
import org.xml.sax.SAXParseException;
import com.sun.xml.bind.unmarshaller.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.bind.unmarshaller.Tracer;
import org.xml.sax.Locator;
import java.util.Hashtable;
import com.sun.xml.bind.util.AttributesImpl;

public class SAXUnmarshallerHandlerImpl implements SAXUnmarshallerHandler, UnmarshallingContext
{
    private boolean isUnmarshalInProgress;
    private final GrammarInfo grammarInfo;
    private Object result;
    private UnmarshallingEventHandler[] handlers;
    private int[] mementos;
    private int handlerLen;
    private StringBuffer buffer;
    private String[] nsBind;
    private int nsLen;
    private int[] idxStack;
    private AttributesImpl[] attStack;
    private int elementDepth;
    private int stackTop;
    private boolean[] collectText;
    private Runnable[] patchers;
    private int patchersLen;
    private Hashtable idmap;
    private Locator locator;
    private static final Locator DUMMY_LOCATOR;
    private final UnmarshallerImpl parent;
    private boolean aborted;
    private Tracer tracer;
    
    public SAXUnmarshallerHandlerImpl(final UnmarshallerImpl _parent, final GrammarInfo _gi) {
        this.isUnmarshalInProgress = true;
        this.handlers = new UnmarshallingEventHandler[16];
        this.mementos = new int[16];
        this.handlerLen = 0;
        this.buffer = new StringBuffer();
        this.nsBind = new String[16];
        this.nsLen = 0;
        this.idxStack = new int[16];
        this.attStack = new AttributesImpl[16];
        this.collectText = new boolean[16];
        this.patchers = null;
        this.patchersLen = 0;
        this.idmap = null;
        this.locator = SAXUnmarshallerHandlerImpl.DUMMY_LOCATOR;
        this.aborted = false;
        this.parent = _parent;
        this.grammarInfo = _gi;
        this.startPrefixMapping("", "");
    }
    
    public GrammarInfo getGrammarInfo() {
        return this.grammarInfo;
    }
    
    private final boolean shouldCollectText() {
        return this.collectText[this.stackTop];
    }
    
    public void startDocument() throws SAXException {
        this.result = null;
        this.handlerLen = 0;
        this.patchers = null;
        this.patchersLen = 0;
        this.aborted = false;
        this.isUnmarshalInProgress = true;
        this.stackTop = 0;
        this.elementDepth = 1;
    }
    
    public void endDocument() throws SAXException {
        this.runPatchers();
        this.isUnmarshalInProgress = false;
    }
    
    public void startElement(String uri, String local, String qname, final Attributes atts) throws SAXException {
        if (uri == null) {
            uri = "";
        }
        if (local == null || local.length() == 0) {
            local = qname;
        }
        if (qname == null || qname.length() == 0) {
            qname = local;
        }
        if (this.result == null) {
            final UnmarshallingEventHandler unmarshaller = this.grammarInfo.createUnmarshaller(uri, local, (UnmarshallingContext)this);
            if (unmarshaller == null) {
                throw new SAXParseException(Messages.format("SAXUnmarshallerHandlerImpl.UnexpectedRootElement2", uri, local, this.computeExpectedRootElements()), this.getLocator());
            }
            this.result = unmarshaller.owner();
            this.pushContentHandler(unmarshaller, 0);
        }
        this.processText(true);
        this.getCurrentHandler().enterElement(uri, local, qname, atts);
    }
    
    public final void endElement(String uri, String local, String qname) throws SAXException {
        if (uri == null) {
            uri = "";
        }
        if (local == null || local.length() == 0) {
            local = qname;
        }
        if (qname == null || qname.length() == 0) {
            qname = local;
        }
        this.processText(false);
        this.getCurrentHandler().leaveElement(uri, local, qname);
    }
    
    public Object getResult() throws UnmarshalException {
        if (this.isUnmarshalInProgress) {
            throw new IllegalStateException();
        }
        if (!this.aborted) {
            return this.result;
        }
        throw new UnmarshalException((String)null);
    }
    
    public void pushContentHandler(final UnmarshallingEventHandler handler, final int memento) {
        if (this.handlerLen == this.handlers.length) {
            final UnmarshallingEventHandler[] h = new UnmarshallingEventHandler[this.handlerLen * 2];
            final int[] m = new int[this.handlerLen * 2];
            System.arraycopy(this.handlers, 0, h, 0, this.handlerLen);
            System.arraycopy(this.mementos, 0, m, 0, this.handlerLen);
            this.handlers = h;
            this.mementos = m;
        }
        this.handlers[this.handlerLen] = handler;
        this.mementos[this.handlerLen] = memento;
        ++this.handlerLen;
    }
    
    public void popContentHandler() throws SAXException {
        --this.handlerLen;
        this.handlers[this.handlerLen] = null;
        this.getCurrentHandler().leaveChild(this.mementos[this.handlerLen]);
    }
    
    public UnmarshallingEventHandler getCurrentHandler() {
        return this.handlers[this.handlerLen - 1];
    }
    
    protected void consumeText(final String str, final boolean ignorable) throws SAXException {
        if (ignorable && str.trim().length() == 0) {
            return;
        }
        this.getCurrentHandler().text(str);
    }
    
    private void processText(final boolean ignorable) throws SAXException {
        if (this.shouldCollectText()) {
            this.consumeText(this.buffer.toString(), ignorable);
        }
        if (this.buffer.length() < 1024) {
            this.buffer.setLength(0);
        }
        else {
            this.buffer = new StringBuffer();
        }
    }
    
    public final void characters(final char[] buf, final int start, final int len) {
        if (this.shouldCollectText()) {
            this.buffer.append(buf, start, len);
        }
    }
    
    public final void ignorableWhitespace(final char[] buf, final int start, final int len) {
        this.characters(buf, start, len);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) {
        if (this.nsBind.length == this.nsLen) {
            final String[] n = new String[this.nsLen * 2];
            System.arraycopy(this.nsBind, 0, n, 0, this.nsLen);
            this.nsBind = n;
        }
        this.nsBind[this.nsLen++] = prefix;
        this.nsBind[this.nsLen++] = uri;
    }
    
    public void endPrefixMapping(final String prefix) {
        this.nsLen -= 2;
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        for (int i = this.idxStack[this.stackTop] - 2; i >= 0; i -= 2) {
            if (prefix.equals(this.nsBind[i])) {
                return this.nsBind[i + 1];
            }
        }
        return null;
    }
    
    public String[] getNewlyDeclaredPrefixes() {
        return this.getPrefixList(this.idxStack[this.stackTop - 1]);
    }
    
    public String[] getAllDeclaredPrefixes() {
        return this.getPrefixList(2);
    }
    
    private String[] getPrefixList(final int startIndex) {
        final int size = (this.idxStack[this.stackTop] - startIndex) / 2;
        final String[] r = new String[size];
        for (int i = 0; i < r.length; ++i) {
            r[i] = this.nsBind[startIndex + i * 2];
        }
        return r;
    }
    
    public Iterator getPrefixes(final String uri) {
        return Collections.unmodifiableList((List<?>)this.getAllPrefixesInList(uri)).iterator();
    }
    
    private List getAllPrefixesInList(final String uri) {
        final List a = new ArrayList();
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            a.add("xml");
            return a;
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            a.add("xmlns");
            return a;
        }
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                a.add(this.nsBind[i]);
            }
        }
        return a;
    }
    
    public String getPrefix(final String uri) {
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.idxStack[this.stackTop] - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                return this.nsBind[i];
            }
        }
        return null;
    }
    
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.resolveNamespacePrefix(prefix);
    }
    
    public void pushAttributes(final Attributes atts, final boolean collectTextFlag) {
        if (this.attStack.length == this.elementDepth) {
            final AttributesImpl[] buf1 = new AttributesImpl[this.attStack.length * 2];
            System.arraycopy(this.attStack, 0, buf1, 0, this.attStack.length);
            this.attStack = buf1;
            final int[] buf2 = new int[this.idxStack.length * 2];
            System.arraycopy(this.idxStack, 0, buf2, 0, this.idxStack.length);
            this.idxStack = buf2;
            final boolean[] buf3 = new boolean[this.collectText.length * 2];
            System.arraycopy(this.collectText, 0, buf3, 0, this.collectText.length);
            this.collectText = buf3;
        }
        ++this.elementDepth;
        ++this.stackTop;
        AttributesImpl a = this.attStack[this.stackTop];
        if (a == null) {
            a = (this.attStack[this.stackTop] = new AttributesImpl());
        }
        else {
            a.clear();
        }
        for (int i = 0; i < atts.getLength(); ++i) {
            String auri = atts.getURI(i);
            String alocal = atts.getLocalName(i);
            final String avalue = atts.getValue(i);
            String aqname = atts.getQName(i);
            if (auri == null) {
                auri = "";
            }
            if (alocal == null || alocal.length() == 0) {
                alocal = aqname;
            }
            if (aqname == null || aqname.length() == 0) {
                aqname = alocal;
            }
            if (auri == "http://www.w3.org/2001/XMLSchema-instance" && alocal == "nil") {
                final String v = avalue.trim();
                if (v.equals("false")) {
                    continue;
                }
                if (v.equals("0")) {
                    continue;
                }
            }
            a.addAttribute(auri, alocal, aqname, atts.getType(i), avalue);
        }
        this.idxStack[this.stackTop] = this.nsLen;
        this.collectText[this.stackTop] = collectTextFlag;
    }
    
    public void popAttributes() {
        --this.stackTop;
        --this.elementDepth;
    }
    
    public Attributes getUnconsumedAttributes() {
        return this.attStack[this.stackTop];
    }
    
    public int getAttribute(final String uri, final String local) {
        return this.attStack[this.stackTop].getIndexFast(uri, local);
    }
    
    public void consumeAttribute(final int idx) throws SAXException {
        final AttributesImpl a = this.attStack[this.stackTop];
        final String uri = a.getURI(idx);
        final String local = a.getLocalName(idx);
        final String qname = a.getQName(idx);
        final String value = a.getValue(idx);
        a.removeAttribute(idx);
        this.getCurrentHandler().enterAttribute(uri, local, qname);
        this.consumeText(value, false);
        this.getCurrentHandler().leaveAttribute(uri, local, qname);
    }
    
    public String eatAttribute(final int idx) throws SAXException {
        final AttributesImpl a = this.attStack[this.stackTop];
        final String value = a.getValue(idx);
        a.removeAttribute(idx);
        return value;
    }
    
    public void addPatcher(final Runnable job) {
        if (this.patchers == null) {
            this.patchers = new Runnable[32];
        }
        if (this.patchers.length == this.patchersLen) {
            final Runnable[] buf = new Runnable[this.patchersLen * 2];
            System.arraycopy(this.patchers, 0, buf, 0, this.patchersLen);
            this.patchers = buf;
        }
        this.patchers[this.patchersLen++] = job;
    }
    
    private void runPatchers() {
        if (this.patchers != null) {
            for (int i = 0; i < this.patchersLen; ++i) {
                this.patchers[i].run();
            }
        }
    }
    
    public String addToIdTable(final String id) {
        if (this.idmap == null) {
            this.idmap = new Hashtable();
        }
        this.idmap.put(id, this.getCurrentHandler().owner());
        return id;
    }
    
    public Object getObjectFromId(final String id) {
        if (this.idmap == null) {
            return null;
        }
        return this.idmap.get(id);
    }
    
    public void skippedEntity(final String name) {
    }
    
    public void processingInstruction(final String target, final String data) {
    }
    
    public void setDocumentLocator(final Locator loc) {
        this.locator = loc;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
        ValidationEventHandler eventHandler;
        try {
            eventHandler = this.parent.getEventHandler();
        }
        catch (JAXBException e) {
            throw new JAXBAssertionError();
        }
        final boolean recover = eventHandler.handleEvent(event);
        if (!recover) {
            this.aborted = true;
        }
        if (!canRecover || !recover) {
            throw new SAXException(new UnmarshalException(event.getMessage(), event.getLinkedException()));
        }
    }
    
    public String getBaseUri() {
        return null;
    }
    
    public boolean isUnparsedEntity(final String s) {
        return true;
    }
    
    public boolean isNotation(final String s) {
        return true;
    }
    
    public void setTracer(final Tracer t) {
        this.tracer = t;
    }
    
    public Tracer getTracer() {
        if (this.tracer == null) {
            this.tracer = (Tracer)new Tracer.Standard();
        }
        return this.tracer;
    }
    
    private String computeExpectedRootElements() {
        String r = "";
        final String[] probePoints = this.grammarInfo.getProbePoints();
        for (int i = 0; i < probePoints.length; i += 2) {
            if (this.grammarInfo.recognize(probePoints[i], probePoints[i + 1])) {
                if (r.length() != 0) {
                    r += ',';
                }
                r = r + "<{" + probePoints[i] + "}" + probePoints[i + 1] + ">";
            }
        }
        return r;
    }
    
    static {
        DUMMY_LOCATOR = new LocatorImpl();
    }
}
