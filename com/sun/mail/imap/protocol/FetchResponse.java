// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap.protocol;

import java.util.Vector;
import com.sun.mail.iap.ParsingException;
import java.util.HashMap;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ProtocolException;
import java.io.IOException;
import com.sun.mail.iap.Protocol;
import java.util.Map;

public class FetchResponse extends IMAPResponse
{
    private Item[] items;
    private Map extensionItems;
    private final FetchItem[] fitems;
    private static final char[] HEADER;
    private static final char[] TEXT;
    
    public FetchResponse(final Protocol p) throws IOException, ProtocolException {
        super(p);
        this.fitems = null;
        this.parse();
    }
    
    public FetchResponse(final IMAPResponse r) throws IOException, ProtocolException {
        this(r, null);
    }
    
    public FetchResponse(final IMAPResponse r, final FetchItem[] fitems) throws IOException, ProtocolException {
        super(r);
        this.fitems = fitems;
        this.parse();
    }
    
    public int getItemCount() {
        return this.items.length;
    }
    
    public Item getItem(final int index) {
        return this.items[index];
    }
    
    public Item getItem(final Class c) {
        for (int i = 0; i < this.items.length; ++i) {
            if (c.isInstance(this.items[i])) {
                return this.items[i];
            }
        }
        return null;
    }
    
    public static Item getItem(final Response[] r, final int msgno, final Class c) {
        if (r == null) {
            return null;
        }
        for (int i = 0; i < r.length; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse) {
                if (((FetchResponse)r[i]).getNumber() == msgno) {
                    final FetchResponse f = (FetchResponse)r[i];
                    for (int j = 0; j < f.items.length; ++j) {
                        if (c.isInstance(f.items[j])) {
                            return f.items[j];
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public Map getExtensionItems() {
        if (this.extensionItems == null) {
            this.extensionItems = new HashMap();
        }
        return this.extensionItems;
    }
    
    private void parse() throws ParsingException {
        this.skipSpaces();
        if (this.buffer[this.index] != 40) {
            throw new ParsingException("error in FETCH parsing, missing '(' at index " + this.index);
        }
        final Vector v = new Vector();
        Item i = null;
        do {
            ++this.index;
            if (this.index >= this.size) {
                throw new ParsingException("error in FETCH parsing, ran off end of buffer, size " + this.size);
            }
            i = this.parseItem();
            if (i != null) {
                v.addElement(i);
            }
            else {
                if (!this.parseExtensionItem()) {
                    throw new ParsingException("error in FETCH parsing, unrecognized item at index " + this.index);
                }
                continue;
            }
        } while (this.buffer[this.index] != 41);
        ++this.index;
        v.copyInto(this.items = new Item[v.size()]);
    }
    
    private Item parseItem() throws ParsingException {
        switch (this.buffer[this.index]) {
            case 69:
            case 101: {
                if (this.match(ENVELOPE.name)) {
                    return new ENVELOPE(this);
                }
                break;
            }
            case 70:
            case 102: {
                if (this.match(FLAGS.name)) {
                    return new FLAGS(this);
                }
                break;
            }
            case 73:
            case 105: {
                if (this.match(INTERNALDATE.name)) {
                    return new INTERNALDATE(this);
                }
                break;
            }
            case 66:
            case 98: {
                if (this.match(BODYSTRUCTURE.name)) {
                    return new BODYSTRUCTURE(this);
                }
                if (!this.match(BODY.name)) {
                    break;
                }
                if (this.buffer[this.index] == 91) {
                    return new BODY(this);
                }
                return new BODYSTRUCTURE(this);
            }
            case 82:
            case 114: {
                if (this.match(RFC822SIZE.name)) {
                    return new RFC822SIZE(this);
                }
                if (this.match(RFC822DATA.name)) {
                    if (!this.match(FetchResponse.HEADER)) {
                        if (this.match(FetchResponse.TEXT)) {}
                    }
                    return new RFC822DATA(this);
                }
                break;
            }
            case 85:
            case 117: {
                if (this.match(UID.name)) {
                    return new UID(this);
                }
                break;
            }
        }
        return null;
    }
    
    private boolean parseExtensionItem() throws ParsingException {
        if (this.fitems == null) {
            return false;
        }
        for (int i = 0; i < this.fitems.length; ++i) {
            if (this.match(this.fitems[i].getName())) {
                this.getExtensionItems().put(this.fitems[i].getName(), this.fitems[i].parseItem(this));
                return true;
            }
        }
        return false;
    }
    
    private boolean match(final char[] itemName) {
        final int len = itemName.length;
        int i = 0;
        int j = this.index;
        while (i < len) {
            if (Character.toUpperCase((char)this.buffer[j++]) != itemName[i++]) {
                return false;
            }
        }
        this.index += len;
        return true;
    }
    
    private boolean match(final String itemName) {
        final int len = itemName.length();
        int i = 0;
        int j = this.index;
        while (i < len) {
            if (Character.toUpperCase((char)this.buffer[j++]) != itemName.charAt(i++)) {
                return false;
            }
        }
        this.index += len;
        return true;
    }
    
    static {
        HEADER = new char[] { '.', 'H', 'E', 'A', 'D', 'E', 'R' };
        TEXT = new char[] { '.', 'T', 'E', 'X', 'T' };
    }
}
