// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import javax.mail.Flags;

public class MailboxInfo
{
    public Flags availableFlags;
    public Flags permanentFlags;
    public int total;
    public int recent;
    public int first;
    public long uidvalidity;
    public long uidnext;
    public int mode;
    
    public MailboxInfo(final Response[] r) throws ParsingException {
        this.availableFlags = null;
        this.permanentFlags = null;
        this.total = -1;
        this.recent = -1;
        this.first = -1;
        this.uidvalidity = -1L;
        this.uidnext = -1L;
        for (int i = 0; i < r.length; ++i) {
            if (r[i] != null) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("EXISTS")) {
                        this.total = ir.getNumber();
                        r[i] = null;
                    }
                    else if (ir.keyEquals("RECENT")) {
                        this.recent = ir.getNumber();
                        r[i] = null;
                    }
                    else if (ir.keyEquals("FLAGS")) {
                        this.availableFlags = new FLAGS(ir);
                        r[i] = null;
                    }
                    else if (ir.isUnTagged() && ir.isOK()) {
                        ir.skipSpaces();
                        if (ir.readByte() != 91) {
                            ir.reset();
                        }
                        else {
                            boolean handled = true;
                            final String s = ir.readAtom();
                            if (s.equalsIgnoreCase("UNSEEN")) {
                                this.first = ir.readNumber();
                            }
                            else if (s.equalsIgnoreCase("UIDVALIDITY")) {
                                this.uidvalidity = ir.readLong();
                            }
                            else if (s.equalsIgnoreCase("PERMANENTFLAGS")) {
                                this.permanentFlags = new FLAGS(ir);
                            }
                            else if (s.equalsIgnoreCase("UIDNEXT")) {
                                this.uidnext = ir.readLong();
                            }
                            else {
                                handled = false;
                            }
                            if (handled) {
                                r[i] = null;
                            }
                            else {
                                ir.reset();
                            }
                        }
                    }
                }
            }
        }
        if (this.permanentFlags == null) {
            if (this.availableFlags != null) {
                this.permanentFlags = new Flags(this.availableFlags);
            }
            else {
                this.permanentFlags = new Flags();
            }
        }
    }
}
