// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class NGCCHandler implements NGCCEventReceiver
{
    protected final NGCCHandler _parent;
    protected final NGCCEventSource _source;
    protected final int _cookie;
    
    protected NGCCHandler(final NGCCEventSource source, final NGCCHandler parent, final int parentCookie) {
        this._parent = parent;
        this._source = source;
        this._cookie = parentCookie;
    }
    
    protected abstract NGCCRuntime getRuntime();
    
    protected abstract void onChildCompleted(final Object p0, final int p1, final boolean p2) throws SAXException;
    
    public void spawnChildFromEnterElement(final NGCCEventReceiver child, final String uri, final String localname, final String qname, final Attributes atts) throws SAXException {
        final int id = this._source.replace(this, child);
        this._source.sendEnterElement(id, uri, localname, qname, atts);
    }
    
    public void spawnChildFromEnterAttribute(final NGCCEventReceiver child, final String uri, final String localname, final String qname) throws SAXException {
        final int id = this._source.replace(this, child);
        this._source.sendEnterAttribute(id, uri, localname, qname);
    }
    
    public void spawnChildFromLeaveElement(final NGCCEventReceiver child, final String uri, final String localname, final String qname) throws SAXException {
        final int id = this._source.replace(this, child);
        this._source.sendLeaveElement(id, uri, localname, qname);
    }
    
    public void spawnChildFromLeaveAttribute(final NGCCEventReceiver child, final String uri, final String localname, final String qname) throws SAXException {
        final int id = this._source.replace(this, child);
        this._source.sendLeaveAttribute(id, uri, localname, qname);
    }
    
    public void spawnChildFromText(final NGCCEventReceiver child, final String value) throws SAXException {
        final int id = this._source.replace(this, child);
        this._source.sendText(id, value);
    }
    
    public void revertToParentFromEnterElement(final Object result, final int cookie, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        final int id = this._source.replace(this, this._parent);
        this._parent.onChildCompleted(result, cookie, true);
        this._source.sendEnterElement(id, uri, local, qname, atts);
    }
    
    public void revertToParentFromLeaveElement(final Object result, final int cookie, final String uri, final String local, final String qname) throws SAXException {
        if (uri == "\u0000" && uri == local && uri == qname && this._parent == null) {
            return;
        }
        final int id = this._source.replace(this, this._parent);
        this._parent.onChildCompleted(result, cookie, true);
        this._source.sendLeaveElement(id, uri, local, qname);
    }
    
    public void revertToParentFromEnterAttribute(final Object result, final int cookie, final String uri, final String local, final String qname) throws SAXException {
        final int id = this._source.replace(this, this._parent);
        this._parent.onChildCompleted(result, cookie, true);
        this._source.sendEnterAttribute(id, uri, local, qname);
    }
    
    public void revertToParentFromLeaveAttribute(final Object result, final int cookie, final String uri, final String local, final String qname) throws SAXException {
        final int id = this._source.replace(this, this._parent);
        this._parent.onChildCompleted(result, cookie, true);
        this._source.sendLeaveAttribute(id, uri, local, qname);
    }
    
    public void revertToParentFromText(final Object result, final int cookie, final String text) throws SAXException {
        final int id = this._source.replace(this, this._parent);
        this._parent.onChildCompleted(result, cookie, true);
        this._source.sendText(id, text);
    }
    
    public void unexpectedEnterElement(final String qname) throws SAXException {
        this.getRuntime().unexpectedX('<' + qname + '>');
    }
    
    public void unexpectedLeaveElement(final String qname) throws SAXException {
        this.getRuntime().unexpectedX("</" + qname + '>');
    }
    
    public void unexpectedEnterAttribute(final String qname) throws SAXException {
        this.getRuntime().unexpectedX('@' + qname);
    }
    
    public void unexpectedLeaveAttribute(final String qname) throws SAXException {
        this.getRuntime().unexpectedX("/@" + qname);
    }
}
