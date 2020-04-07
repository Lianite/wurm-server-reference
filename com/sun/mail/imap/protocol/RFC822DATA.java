// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import java.io.ByteArrayInputStream;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.ByteArray;

public class RFC822DATA implements Item
{
    static final char[] name;
    public int msgno;
    public ByteArray data;
    
    public RFC822DATA(final FetchResponse r) throws ParsingException {
        this.msgno = r.getNumber();
        r.skipSpaces();
        this.data = r.readByteArray();
    }
    
    public ByteArray getByteArray() {
        return this.data;
    }
    
    public ByteArrayInputStream getByteArrayInputStream() {
        if (this.data != null) {
            return this.data.toByteArrayInputStream();
        }
        return null;
    }
    
    static {
        name = new char[] { 'R', 'F', 'C', '8', '2', '2' };
    }
}
