// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

import com.sun.mail.imap.protocol.UIDSet;
import java.util.Vector;
import com.sun.mail.imap.protocol.MessageSet;
import javax.mail.Message;

public final class Utility
{
    public static MessageSet[] toMessageSet(final Message[] msgs, final Condition cond) {
        final Vector v = new Vector(1);
        for (int i = 0; i < msgs.length; ++i) {
            IMAPMessage msg = (IMAPMessage)msgs[i];
            if (!msg.isExpunged()) {
                int current = msg.getSequenceNumber();
                if (cond == null || cond.test(msg)) {
                    final MessageSet set = new MessageSet();
                    set.start = current;
                    ++i;
                    while (i < msgs.length) {
                        msg = (IMAPMessage)msgs[i];
                        if (!msg.isExpunged()) {
                            final int next = msg.getSequenceNumber();
                            if (cond == null || cond.test(msg)) {
                                if (next != current + 1) {
                                    --i;
                                    break;
                                }
                                current = next;
                            }
                        }
                        ++i;
                    }
                    set.end = current;
                    v.addElement(set);
                }
            }
        }
        if (v.isEmpty()) {
            return null;
        }
        final MessageSet[] sets = new MessageSet[v.size()];
        v.copyInto(sets);
        return sets;
    }
    
    public static UIDSet[] toUIDSet(final Message[] msgs) {
        final Vector v = new Vector(1);
        for (int i = 0; i < msgs.length; ++i) {
            IMAPMessage msg = (IMAPMessage)msgs[i];
            if (!msg.isExpunged()) {
                long current = msg.getUID();
                final UIDSet set = new UIDSet();
                set.start = current;
                ++i;
                while (i < msgs.length) {
                    msg = (IMAPMessage)msgs[i];
                    if (!msg.isExpunged()) {
                        final long next = msg.getUID();
                        if (next != current + 1L) {
                            --i;
                            break;
                        }
                        current = next;
                    }
                    ++i;
                }
                set.end = current;
                v.addElement(set);
            }
        }
        if (v.isEmpty()) {
            return null;
        }
        final UIDSet[] sets = new UIDSet[v.size()];
        v.copyInto(sets);
        return sets;
    }
    
    public interface Condition
    {
        boolean test(final IMAPMessage p0);
    }
}
