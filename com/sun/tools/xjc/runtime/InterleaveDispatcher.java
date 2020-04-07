// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.ValidationEvent;
import com.sun.xml.bind.unmarshaller.Tracer;
import java.util.Iterator;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.xml.bind.JAXBAssertionError;

public abstract class InterleaveDispatcher implements UnmarshallingEventHandler
{
    private final UnmarshallingContext parent;
    protected final Site[] sites;
    private boolean isJoining;
    private int nestLevel;
    private Site currentSite;
    
    protected InterleaveDispatcher(final UnmarshallingContext context, final int size) {
        this.nestLevel = 0;
        this.parent = context;
        this.sites = new Site[size];
        for (int i = 0; i < size; ++i) {
            this.sites[i] = new Site(this, (InterleaveDispatcher$1)null);
        }
    }
    
    protected void init(final UnmarshallingEventHandler[] handlers) {
        for (int i = 0; i < handlers.length; ++i) {
            this.sites[i].pushContentHandler(handlers[i], 0);
        }
    }
    
    protected abstract int getBranchForElement(final String p0, final String p1);
    
    protected abstract int getBranchForAttribute(final String p0, final String p1);
    
    protected abstract int getBranchForText();
    
    public Object owner() {
        if (this.nestLevel > 0) {
            return this.currentSite.getCurrentHandler().owner();
        }
        throw new JAXBAssertionError();
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        if (this.nestLevel++ == 0) {
            final int idx = this.getBranchForElement(uri, local);
            if (idx == -1) {
                this.joinByEnterElement(null, uri, local, qname, atts);
                return;
            }
            this.currentSite = this.sites[idx];
        }
        this.currentSite.getCurrentHandler().enterElement(uri, local, qname, atts);
    }
    
    private void joinByEnterElement(final Site source, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this.sites.length; ++i) {
            if (this.sites[i] != source) {
                this.sites[i].getCurrentHandler().enterElement(uri, local, qname, atts);
            }
        }
        this.parent.popContentHandler();
        this.parent.getCurrentHandler().enterElement(uri, local, qname, atts);
    }
    
    public void leaveElement(final String uri, final String local, final String qname) throws SAXException {
        if (this.nestLevel == 0) {
            this.joinByLeaveElement(null, uri, local, qname);
        }
        else {
            this.currentSite.getCurrentHandler().leaveElement(uri, local, qname);
            --this.nestLevel;
        }
    }
    
    private void joinByLeaveElement(final Site source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this.sites.length; ++i) {
            if (this.sites[i] != source) {
                this.sites[i].getCurrentHandler().leaveElement(uri, local, qname);
            }
        }
        this.parent.popContentHandler();
        this.parent.getCurrentHandler().leaveElement(uri, local, qname);
    }
    
    public void text(final String s) throws SAXException {
        if (this.nestLevel == 0) {
            final int idx = this.getBranchForText();
            if (idx == -1) {
                if (s.trim().length() != 0) {
                    this.joinByText(null, s);
                }
                return;
            }
            this.currentSite = this.sites[idx];
        }
        this.currentSite.getCurrentHandler().text(s);
    }
    
    private void joinByText(final Site source, final String s) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this.sites.length; ++i) {
            if (this.sites[i] != source) {
                this.sites[i].getCurrentHandler().text(s);
            }
        }
        this.parent.popContentHandler();
        this.parent.getCurrentHandler().text(s);
    }
    
    public void enterAttribute(final String uri, final String local, final String qname) throws SAXException {
        if (this.nestLevel++ == 0) {
            final int idx = this.getBranchForAttribute(uri, local);
            if (idx == -1) {
                this.joinByEnterAttribute(null, uri, local, qname);
                return;
            }
            this.currentSite = this.sites[idx];
        }
        this.currentSite.getCurrentHandler().enterAttribute(uri, local, qname);
    }
    
    private void joinByEnterAttribute(final Site source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this.sites.length; ++i) {
            if (this.sites[i] != source) {
                this.sites[i].getCurrentHandler().enterAttribute(uri, local, qname);
            }
        }
        this.parent.popContentHandler();
        this.parent.getCurrentHandler().enterAttribute(uri, local, qname);
    }
    
    public void leaveAttribute(final String uri, final String local, final String qname) throws SAXException {
        if (this.nestLevel == 0) {
            this.joinByLeaveAttribute(null, uri, local, qname);
        }
        else {
            --this.nestLevel;
            this.currentSite.getCurrentHandler().leaveAttribute(uri, local, qname);
        }
    }
    
    private void joinByLeaveAttribute(final Site source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this.sites.length; ++i) {
            if (this.sites[i] != source) {
                this.sites[i].getCurrentHandler().leaveAttribute(uri, local, qname);
            }
        }
        this.parent.popContentHandler();
        this.parent.getCurrentHandler().leaveAttribute(uri, local, qname);
    }
    
    public void leaveChild(final int nextState) throws SAXException {
        throw new JAXBAssertionError();
    }
    
    private class Site implements UnmarshallingContext, UnmarshallingEventHandler
    {
        private UnmarshallingEventHandler[] handlers;
        private int[] mementos;
        private int handlerLen;
        
        private Site(final InterleaveDispatcher this$0) {
            this.this$0 = this$0;
            this.handlers = new UnmarshallingEventHandler[8];
            this.mementos = new int[8];
            this.pushContentHandler((UnmarshallingEventHandler)this, this.handlerLen = 0);
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
        
        public Object owner() {
            return null;
        }
        
        public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
            InterleaveDispatcher.access$100(this.this$0, this, uri, local, qname, atts);
        }
        
        public void leaveElement(final String uri, final String local, final String qname) throws SAXException {
            InterleaveDispatcher.access$200(this.this$0, this, uri, local, qname);
        }
        
        public void enterAttribute(final String uri, final String local, final String qname) throws SAXException {
            InterleaveDispatcher.access$300(this.this$0, this, uri, local, qname);
        }
        
        public void leaveAttribute(final String uri, final String local, final String qname) throws SAXException {
            InterleaveDispatcher.access$400(this.this$0, this, uri, local, qname);
        }
        
        public void text(final String s) throws SAXException {
            InterleaveDispatcher.access$500(this.this$0, this, s);
        }
        
        public void leaveChild(final int nextState) throws SAXException {
        }
        
        public void addPatcher(final Runnable job) {
            this.this$0.parent.addPatcher(job);
        }
        
        public String addToIdTable(final String id) {
            return this.this$0.parent.addToIdTable(id);
        }
        
        public void consumeAttribute(final int idx) throws SAXException {
            this.this$0.parent.consumeAttribute(idx);
        }
        
        public String eatAttribute(final int idx) throws SAXException {
            return this.this$0.parent.eatAttribute(idx);
        }
        
        public int getAttribute(final String uri, final String name) {
            return this.this$0.parent.getAttribute(uri, name);
        }
        
        public String getBaseUri() {
            return this.this$0.parent.getBaseUri();
        }
        
        public GrammarInfo getGrammarInfo() {
            return this.this$0.parent.getGrammarInfo();
        }
        
        public Locator getLocator() {
            return this.this$0.parent.getLocator();
        }
        
        public String getNamespaceURI(final String prefix) {
            return this.this$0.parent.getNamespaceURI(prefix);
        }
        
        public Object getObjectFromId(final String id) {
            return this.this$0.parent.getObjectFromId(id);
        }
        
        public String getPrefix(final String namespaceURI) {
            return this.this$0.parent.getPrefix(namespaceURI);
        }
        
        public Iterator getPrefixes(final String namespaceURI) {
            return this.this$0.parent.getPrefixes(namespaceURI);
        }
        
        public Tracer getTracer() {
            return this.this$0.parent.getTracer();
        }
        
        public Attributes getUnconsumedAttributes() {
            return this.this$0.parent.getUnconsumedAttributes();
        }
        
        public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
            this.this$0.parent.handleEvent(event, canRecover);
        }
        
        public boolean isNotation(final String arg0) {
            return this.this$0.parent.isNotation(arg0);
        }
        
        public boolean isUnparsedEntity(final String arg0) {
            return this.this$0.parent.isUnparsedEntity(arg0);
        }
        
        public void popAttributes() {
            this.this$0.parent.popAttributes();
        }
        
        public void pushAttributes(final Attributes atts, final boolean collectTextFlag) {
            this.this$0.parent.pushAttributes(atts, collectTextFlag);
        }
        
        public String resolveNamespacePrefix(final String prefix) {
            return this.this$0.parent.resolveNamespacePrefix(prefix);
        }
        
        public String[] getNewlyDeclaredPrefixes() {
            return this.this$0.parent.getNewlyDeclaredPrefixes();
        }
        
        public String[] getAllDeclaredPrefixes() {
            return this.this$0.parent.getAllDeclaredPrefixes();
        }
    }
}
