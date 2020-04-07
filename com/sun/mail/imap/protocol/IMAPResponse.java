// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import com.sun.mail.util.ASCIIUtility;
import java.util.Vector;
import com.sun.mail.iap.ProtocolException;
import java.io.IOException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.Response;

public class IMAPResponse extends Response
{
    private String key;
    private int number;
    
    public IMAPResponse(final Protocol c) throws IOException, ProtocolException {
        super(c);
        this.init();
    }
    
    private void init() throws IOException, ProtocolException {
        if (this.isUnTagged() && !this.isOK() && !this.isNO() && !this.isBAD() && !this.isBYE()) {
            this.key = this.readAtom();
            try {
                this.number = Integer.parseInt(this.key);
                this.key = this.readAtom();
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public IMAPResponse(final IMAPResponse r) {
        super(r);
        this.key = r.key;
        this.number = r.number;
    }
    
    public IMAPResponse(final String r) throws IOException, ProtocolException {
        super(r);
        this.init();
    }
    
    public String[] readSimpleList() {
        this.skipSpaces();
        if (this.buffer[this.index] != 40) {
            return null;
        }
        ++this.index;
        final Vector v = new Vector();
        int start = this.index;
        while (this.buffer[this.index] != 41) {
            if (this.buffer[this.index] == 32) {
                v.addElement(ASCIIUtility.toString(this.buffer, start, this.index));
                start = this.index + 1;
            }
            ++this.index;
        }
        if (this.index > start) {
            v.addElement(ASCIIUtility.toString(this.buffer, start, this.index));
        }
        ++this.index;
        final int size = v.size();
        if (size > 0) {
            final String[] s = new String[size];
            v.copyInto(s);
            return s;
        }
        return null;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public boolean keyEquals(final String k) {
        return this.key != null && this.key.equalsIgnoreCase(k);
    }
    
    public int getNumber() {
        return this.number;
    }
}
