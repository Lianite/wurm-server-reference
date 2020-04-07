// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import javax.mail.MethodNotSupportedException;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.MessageRemovedException;
import javax.mail.FolderClosedException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;

public class IMAPNestedMessage extends IMAPMessage
{
    private IMAPMessage msg;
    
    IMAPNestedMessage(final IMAPMessage m, final BODYSTRUCTURE b, final ENVELOPE e, final String sid) {
        super(m._getSession());
        this.msg = m;
        this.bs = b;
        this.envelope = e;
        this.sectionId = sid;
        this.setPeek(m.getPeek());
    }
    
    protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
        return this.msg.getProtocol();
    }
    
    protected boolean isREV1() throws FolderClosedException {
        return this.msg.isREV1();
    }
    
    protected Object getMessageCacheLock() {
        return this.msg.getMessageCacheLock();
    }
    
    protected int getSequenceNumber() {
        return this.msg.getSequenceNumber();
    }
    
    protected void checkExpunged() throws MessageRemovedException {
        this.msg.checkExpunged();
    }
    
    public boolean isExpunged() {
        return this.msg.isExpunged();
    }
    
    protected int getFetchBlockSize() {
        return this.msg.getFetchBlockSize();
    }
    
    protected boolean ignoreBodyStructureSize() {
        return this.msg.ignoreBodyStructureSize();
    }
    
    public int getSize() throws MessagingException {
        return this.bs.size;
    }
    
    public synchronized void setFlags(final Flags flag, final boolean set) throws MessagingException {
        throw new MethodNotSupportedException("Cannot set flags on this nested message");
    }
}
