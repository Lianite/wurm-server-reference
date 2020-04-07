// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2.output;

import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import java.io.IOException;
import com.sun.xml.txw2.TxwException;
import java.io.FileOutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;

public class StreamSerializer implements XmlSerializer
{
    private final SaxSerializer serializer;
    private final XMLWriter writer;
    
    public StreamSerializer(final OutputStream out) {
        this(createWriter(out));
    }
    
    public StreamSerializer(final OutputStream out, final String encoding) throws UnsupportedEncodingException {
        this(createWriter(out, encoding));
    }
    
    public StreamSerializer(final Writer out) {
        this(new StreamResult(out));
    }
    
    public StreamSerializer(final StreamResult streamResult) {
        final OutputStream[] autoClose = { null };
        if (streamResult.getWriter() != null) {
            this.writer = createWriter(streamResult.getWriter());
        }
        else if (streamResult.getOutputStream() != null) {
            this.writer = createWriter(streamResult.getOutputStream());
        }
        else {
            if (streamResult.getSystemId() == null) {
                throw new IllegalArgumentException();
            }
            String fileURL = streamResult.getSystemId();
            fileURL = this.convertURL(fileURL);
            try {
                final FileOutputStream fos = new FileOutputStream(fileURL);
                autoClose[0] = fos;
                this.writer = createWriter(fos);
            }
            catch (IOException e) {
                throw new TxwException(e);
            }
        }
        this.serializer = new SaxSerializer(this.writer, this.writer, false) {
            public void endDocument() {
                super.endDocument();
                if (autoClose[0] != null) {
                    try {
                        autoClose[0].close();
                    }
                    catch (IOException e) {
                        throw new TxwException(e);
                    }
                    autoClose[0] = null;
                }
            }
        };
    }
    
    private StreamSerializer(final XMLWriter writer) {
        this.writer = writer;
        this.serializer = new SaxSerializer(writer, writer, false);
    }
    
    private String convertURL(String url) {
        url = url.replace('\\', '/');
        url = url.replaceAll("//", "/");
        url = url.replaceAll("//", "/");
        if (url.startsWith("file:/")) {
            if (url.substring(6).indexOf(":") > 0) {
                url = url.substring(6);
            }
            else {
                url = url.substring(5);
            }
        }
        return url;
    }
    
    public void startDocument() {
        this.serializer.startDocument();
    }
    
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        this.serializer.beginStartTag(uri, localName, prefix);
    }
    
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        this.serializer.writeAttribute(uri, localName, prefix, value);
    }
    
    public void writeXmlns(final String prefix, final String uri) {
        this.serializer.writeXmlns(prefix, uri);
    }
    
    public void endStartTag(final String uri, final String localName, final String prefix) {
        this.serializer.endStartTag(uri, localName, prefix);
    }
    
    public void endTag() {
        this.serializer.endTag();
    }
    
    public void text(final StringBuilder text) {
        this.serializer.text(text);
    }
    
    public void cdata(final StringBuilder text) {
        this.serializer.cdata(text);
    }
    
    public void comment(final StringBuilder comment) {
        this.serializer.comment(comment);
    }
    
    public void endDocument() {
        this.serializer.endDocument();
    }
    
    public void flush() {
        this.serializer.flush();
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new TxwException(e);
        }
    }
    
    private static XMLWriter createWriter(final Writer w) {
        final DataWriter dw = new DataWriter(new BufferedWriter(w));
        dw.setIndentStep("  ");
        return dw;
    }
    
    private static XMLWriter createWriter(final OutputStream os, final String encoding) throws UnsupportedEncodingException {
        final XMLWriter writer = createWriter(new OutputStreamWriter(os, encoding));
        writer.setEncoding(encoding);
        return writer;
    }
    
    private static XMLWriter createWriter(final OutputStream os) {
        try {
            return createWriter(os, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
