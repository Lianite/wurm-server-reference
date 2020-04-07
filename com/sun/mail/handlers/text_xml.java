// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.handlers;

import javax.mail.internet.ContentType;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;
import java.awt.datatransfer.DataFlavor;

public class text_xml extends text_plain
{
    private final DataFlavor[] flavors;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$javax$xml$transform$stream$StreamSource;
    
    public text_xml() {
        this.flavors = new DataFlavor[] { new ActivationDataFlavor((text_xml.class$java$lang$String == null) ? (text_xml.class$java$lang$String = class$("java.lang.String")) : text_xml.class$java$lang$String, "text/xml", "XML String"), new ActivationDataFlavor((text_xml.class$java$lang$String == null) ? (text_xml.class$java$lang$String = class$("java.lang.String")) : text_xml.class$java$lang$String, "application/xml", "XML String"), new ActivationDataFlavor((text_xml.class$javax$xml$transform$stream$StreamSource == null) ? (text_xml.class$javax$xml$transform$stream$StreamSource = class$("javax.xml.transform.stream.StreamSource")) : text_xml.class$javax$xml$transform$stream$StreamSource, "text/xml", "XML"), new ActivationDataFlavor((text_xml.class$javax$xml$transform$stream$StreamSource == null) ? (text_xml.class$javax$xml$transform$stream$StreamSource = class$("javax.xml.transform.stream.StreamSource")) : text_xml.class$javax$xml$transform$stream$StreamSource, "application/xml", "XML") };
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return this.flavors.clone();
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        int i = 0;
        while (i < this.flavors.length) {
            final DataFlavor aFlavor = this.flavors[i];
            if (aFlavor.equals(df)) {
                if (aFlavor.getRepresentationClass() == ((text_xml.class$java$lang$String == null) ? (text_xml.class$java$lang$String = class$("java.lang.String")) : text_xml.class$java$lang$String)) {
                    return super.getContent(ds);
                }
                if (aFlavor.getRepresentationClass() == ((text_xml.class$javax$xml$transform$stream$StreamSource == null) ? (text_xml.class$javax$xml$transform$stream$StreamSource = class$("javax.xml.transform.stream.StreamSource")) : text_xml.class$javax$xml$transform$stream$StreamSource)) {
                    return new StreamSource(ds.getInputStream());
                }
                return null;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!this.isXmlType(mimeType)) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for text/xml DCH");
        }
        if (obj instanceof String) {
            super.writeTo(obj, mimeType, os);
            return;
        }
        if (!(obj instanceof DataSource) && !(obj instanceof Source)) {
            throw new IOException("Invalid Object type = " + obj.getClass() + ". XmlDCH can only convert DataSource or Source to XML.");
        }
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final StreamResult result = new StreamResult(os);
            if (obj instanceof DataSource) {
                transformer.transform(new StreamSource(((DataSource)obj).getInputStream()), result);
            }
            else {
                transformer.transform((Source)obj, result);
            }
        }
        catch (Exception ex) {
            throw new IOException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
        }
    }
    
    private boolean isXmlType(final String type) {
        try {
            final ContentType ct = new ContentType(type);
            return ct.getSubType().equals("xml") && (ct.getPrimaryType().equals("text") || ct.getPrimaryType().equals("application"));
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
}
