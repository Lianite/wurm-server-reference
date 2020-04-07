// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.util.logging.Logger;

public class XmlPullParserUtils
{
    private static final Logger log;
    static XmlPullParserFactory xmlPullParserFactory;
    
    public static XmlPullParser createParser(final String xml) throws XmlPullParserException {
        final XmlPullParser xpp = createParser();
        InputStream is;
        try {
            is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new XmlPullParserException("UTF-8: unsupported encoding");
        }
        xpp.setInput(is, "UTF-8");
        return xpp;
    }
    
    public static XmlPullParser createParser() throws XmlPullParserException {
        if (XmlPullParserUtils.xmlPullParserFactory == null) {
            throw new XmlPullParserException("no XML Pull parser factory");
        }
        return XmlPullParserUtils.xmlPullParserFactory.newPullParser();
    }
    
    public static XmlSerializer createSerializer() throws XmlPullParserException {
        if (XmlPullParserUtils.xmlPullParserFactory == null) {
            throw new XmlPullParserException("no XML Pull parser factory");
        }
        return XmlPullParserUtils.xmlPullParserFactory.newSerializer();
    }
    
    public static void setSerializerIndentation(final XmlSerializer serializer, final int indent) {
        if (indent > 0) {
            try {
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            }
            catch (Exception e) {
                XmlPullParserUtils.log.warning("error setting feature of XmlSerializer: " + e);
            }
        }
    }
    
    public static void skipTag(final XmlPullParser xpp, final String tag) throws IOException, XmlPullParserException {
        int event;
        do {
            event = xpp.next();
        } while (event != 1 && (event != 3 || !xpp.getName().equals(tag)));
    }
    
    public static void searchTag(final XmlPullParser xpp, final String tag) throws IOException, XmlPullParserException {
        int event;
        while ((event = xpp.next()) != 1) {
            if (event == 2 && xpp.getName().equals(tag)) {
                return;
            }
        }
        throw new IOException(String.format("tag '%s' not found", tag));
    }
    
    public static void serializeIfNotNullOrEmpty(final XmlSerializer serializer, final String ns, final String tag, final String value) throws Exception {
        if (isNullOrEmpty(value)) {
            return;
        }
        serializer.startTag(ns, tag);
        serializer.text(value);
        serializer.endTag(ns, tag);
    }
    
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    public static void serializeIfNotEqual(final XmlSerializer serializer, final String ns, final String tag, final Object value, final Object forbiddenValue) throws Exception {
        if (value == null || value.equals(forbiddenValue)) {
            return;
        }
        serializer.startTag(ns, tag);
        serializer.text(value.toString());
        serializer.endTag(ns, tag);
    }
    
    public static String fixXMLEntities(final String xml) {
        final StringBuilder fixedXml = new StringBuilder(xml.length());
        boolean isFixed = false;
        for (int i = 0; i < xml.length(); ++i) {
            final char c = xml.charAt(i);
            if (c == '&') {
                final String sub = xml.substring(i, Math.min(i + 10, xml.length()));
                if (!sub.startsWith("&#") && !sub.startsWith("&lt;") && !sub.startsWith("&gt;") && !sub.startsWith("&amp;") && !sub.startsWith("&apos;") && !sub.startsWith("&quot;")) {
                    isFixed = true;
                    fixedXml.append("&amp;");
                }
                else {
                    fixedXml.append(c);
                }
            }
            else {
                fixedXml.append(c);
            }
        }
        if (isFixed) {
            XmlPullParserUtils.log.warning("fixed badly encoded entities in XML");
        }
        return fixedXml.toString();
    }
    
    static {
        log = Logger.getLogger(XmlPullParserUtils.class.getName());
        try {
            (XmlPullParserUtils.xmlPullParserFactory = XmlPullParserFactory.newInstance()).setNamespaceAware(true);
        }
        catch (XmlPullParserException e) {
            XmlPullParserUtils.log.severe("cannot create XmlPullParserFactory instance: " + e);
        }
    }
}
