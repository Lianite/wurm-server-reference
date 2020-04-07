// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser.state;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public abstract class NGCCInterleaveFilter implements NGCCEventSource, NGCCEventReceiver
{
    protected NGCCEventReceiver[] _receivers;
    private final NGCCHandler _parent;
    private final int _cookie;
    private int lockedReceiver;
    private int lockCount;
    private boolean isJoining;
    
    protected NGCCInterleaveFilter(final NGCCHandler parent, final int cookie) {
        this.lockCount = 0;
        this.isJoining = false;
        this._parent = parent;
        this._cookie = cookie;
    }
    
    protected void setHandlers(final NGCCEventReceiver[] receivers) {
        this._receivers = receivers;
    }
    
    public int replace(final NGCCEventReceiver oldHandler, final NGCCEventReceiver newHandler) {
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] == oldHandler) {
                this._receivers[i] = newHandler;
                return i;
            }
        }
        throw new InternalError();
    }
    
    public void enterElement(final String uri, final String localName, final String qname, final Attributes atts) throws SAXException {
        if (this.isJoining) {
            return;
        }
        if (this.lockCount++ == 0) {
            this.lockedReceiver = this.findReceiverOfElement(uri, localName);
            if (this.lockedReceiver == -1) {
                this.joinByEnterElement(null, uri, localName, qname, atts);
                return;
            }
        }
        this._receivers[this.lockedReceiver].enterElement(uri, localName, qname, atts);
    }
    
    public void leaveElement(final String uri, final String localName, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        if (this.lockCount-- == 0) {
            this.joinByLeaveElement(null, uri, localName, qname);
        }
        else {
            this._receivers[this.lockedReceiver].leaveElement(uri, localName, qname);
        }
    }
    
    public void enterAttribute(final String uri, final String localName, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        if (this.lockCount++ == 0) {
            this.lockedReceiver = this.findReceiverOfAttribute(uri, localName);
            if (this.lockedReceiver == -1) {
                this.joinByEnterAttribute(null, uri, localName, qname);
                return;
            }
        }
        this._receivers[this.lockedReceiver].enterAttribute(uri, localName, qname);
    }
    
    public void leaveAttribute(final String uri, final String localName, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        if (this.lockCount-- == 0) {
            this.joinByLeaveAttribute(null, uri, localName, qname);
        }
        else {
            this._receivers[this.lockedReceiver].leaveAttribute(uri, localName, qname);
        }
    }
    
    public void text(final String value) throws SAXException {
        if (this.isJoining) {
            return;
        }
        if (this.lockCount != 0) {
            this._receivers[this.lockedReceiver].text(value);
        }
        else {
            final int receiver = this.findReceiverOfText();
            if (receiver != -1) {
                this._receivers[receiver].text(value);
            }
            else {
                this.joinByText(null, value);
            }
        }
    }
    
    protected abstract int findReceiverOfElement(final String p0, final String p1);
    
    protected abstract int findReceiverOfAttribute(final String p0, final String p1);
    
    protected abstract int findReceiverOfText();
    
    public void joinByEnterElement(final NGCCEventReceiver source, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] != source) {
                this._receivers[i].enterElement(uri, local, qname, atts);
            }
        }
        this._parent._source.replace(this, this._parent);
        this._parent.onChildCompleted(null, this._cookie, true);
        this._parent.enterElement(uri, local, qname, atts);
    }
    
    public void joinByLeaveElement(final NGCCEventReceiver source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] != source) {
                this._receivers[i].leaveElement(uri, local, qname);
            }
        }
        this._parent._source.replace(this, this._parent);
        this._parent.onChildCompleted(null, this._cookie, true);
        this._parent.leaveElement(uri, local, qname);
    }
    
    public void joinByEnterAttribute(final NGCCEventReceiver source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] != source) {
                this._receivers[i].enterAttribute(uri, local, qname);
            }
        }
        this._parent._source.replace(this, this._parent);
        this._parent.onChildCompleted(null, this._cookie, true);
        this._parent.enterAttribute(uri, local, qname);
    }
    
    public void joinByLeaveAttribute(final NGCCEventReceiver source, final String uri, final String local, final String qname) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] != source) {
                this._receivers[i].leaveAttribute(uri, local, qname);
            }
        }
        this._parent._source.replace(this, this._parent);
        this._parent.onChildCompleted(null, this._cookie, true);
        this._parent.leaveAttribute(uri, local, qname);
    }
    
    public void joinByText(final NGCCEventReceiver source, final String value) throws SAXException {
        if (this.isJoining) {
            return;
        }
        this.isJoining = true;
        for (int i = 0; i < this._receivers.length; ++i) {
            if (this._receivers[i] != source) {
                this._receivers[i].text(value);
            }
        }
        this._parent._source.replace(this, this._parent);
        this._parent.onChildCompleted(null, this._cookie, true);
        this._parent.text(value);
    }
    
    public void sendEnterAttribute(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this._receivers[threadId].enterAttribute(uri, local, qname);
    }
    
    public void sendEnterElement(final int threadId, final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        this._receivers[threadId].enterElement(uri, local, qname, atts);
    }
    
    public void sendLeaveAttribute(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this._receivers[threadId].leaveAttribute(uri, local, qname);
    }
    
    public void sendLeaveElement(final int threadId, final String uri, final String local, final String qname) throws SAXException {
        this._receivers[threadId].leaveElement(uri, local, qname);
    }
    
    public void sendText(final int threadId, final String value) throws SAXException {
        this._receivers[threadId].text(value);
    }
}
