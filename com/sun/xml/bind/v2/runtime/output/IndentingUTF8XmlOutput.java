// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.output;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;

public final class IndentingUTF8XmlOutput extends UTF8XmlOutput
{
    private final Encoded indent8;
    private final int unitLen;
    private int depth;
    private boolean seenText;
    
    public IndentingUTF8XmlOutput(final OutputStream out, final String indentStr, final Encoded[] localNames) {
        super(out, localNames);
        this.depth = 0;
        this.seenText = false;
        if (indentStr != null) {
            final Encoded e = new Encoded(indentStr);
            (this.indent8 = new Encoded()).ensureSize(e.len * 8);
            this.unitLen = e.len;
            for (int i = 0; i < 8; ++i) {
                System.arraycopy(e.buf, 0, this.indent8.buf, this.unitLen * i, this.unitLen);
            }
        }
        else {
            this.indent8 = null;
            this.unitLen = 0;
        }
    }
    
    public void beginStartTag(final int prefix, final String localName) throws IOException {
        this.indentStartTag();
        super.beginStartTag(prefix, localName);
    }
    
    public void beginStartTag(final Name name) throws IOException {
        this.indentStartTag();
        super.beginStartTag(name);
    }
    
    private void indentStartTag() throws IOException {
        this.closeStartTag();
        if (!this.seenText) {
            this.printIndent();
        }
        ++this.depth;
        this.seenText = false;
    }
    
    public void endTag(final Name name) throws IOException {
        this.indentEndTag();
        super.endTag(name);
    }
    
    public void endTag(final int prefix, final String localName) throws IOException {
        this.indentEndTag();
        super.endTag(prefix, localName);
    }
    
    private void indentEndTag() throws IOException {
        --this.depth;
        if (!this.closeStartTagPending && !this.seenText) {
            this.printIndent();
        }
        this.seenText = false;
    }
    
    private void printIndent() throws IOException {
        this.write(10);
        int i = this.depth % 8;
        this.write(this.indent8.buf, 0, i * this.unitLen);
        for (i >>= 3; i > 0; --i) {
            this.indent8.write(this);
        }
    }
    
    public void text(final String value, final boolean needSP) throws IOException {
        this.seenText = true;
        super.text(value, needSP);
    }
    
    public void text(final Pcdata value, final boolean needSP) throws IOException {
        this.seenText = true;
        super.text(value, needSP);
    }
    
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.write(10);
        super.endDocument(fragment);
    }
}
