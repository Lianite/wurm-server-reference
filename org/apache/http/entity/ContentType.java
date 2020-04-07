// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.NameValuePair;
import org.apache.http.HeaderElement;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.nio.charset.Charset;
import org.apache.http.annotation.Immutable;
import java.io.Serializable;

@Immutable
public final class ContentType implements Serializable
{
    private static final long serialVersionUID = -7768694718232371896L;
    public static final ContentType APPLICATION_ATOM_XML;
    public static final ContentType APPLICATION_FORM_URLENCODED;
    public static final ContentType APPLICATION_JSON;
    public static final ContentType APPLICATION_OCTET_STREAM;
    public static final ContentType APPLICATION_SVG_XML;
    public static final ContentType APPLICATION_XHTML_XML;
    public static final ContentType APPLICATION_XML;
    public static final ContentType MULTIPART_FORM_DATA;
    public static final ContentType TEXT_HTML;
    public static final ContentType TEXT_PLAIN;
    public static final ContentType TEXT_XML;
    public static final ContentType WILDCARD;
    public static final ContentType DEFAULT_TEXT;
    public static final ContentType DEFAULT_BINARY;
    private final String mimeType;
    private final Charset charset;
    
    ContentType(final String mimeType, final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.mimeType);
        if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }
    
    private static boolean valid(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch == '\"' || ch == ',' || ch == ';') {
                return false;
            }
        }
        return true;
    }
    
    public static ContentType create(final String mimeType, final Charset charset) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME type may not be null");
        }
        final String type = mimeType.trim().toLowerCase(Locale.US);
        if (type.length() == 0) {
            throw new IllegalArgumentException("MIME type may not be empty");
        }
        if (!valid(type)) {
            throw new IllegalArgumentException("MIME type may not contain reserved characters");
        }
        return new ContentType(type, charset);
    }
    
    public static ContentType create(final String mimeType) {
        return new ContentType(mimeType, null);
    }
    
    public static ContentType create(final String mimeType, final String charset) throws UnsupportedCharsetException {
        return create(mimeType, (charset != null && charset.length() > 0) ? Charset.forName(charset) : null);
    }
    
    private static ContentType create(final HeaderElement helem) {
        final String mimeType = helem.getName();
        String charset = null;
        final NameValuePair param = helem.getParameterByName("charset");
        if (param != null) {
            charset = param.getValue();
        }
        return create(mimeType, charset);
    }
    
    public static ContentType parse(final String s) throws ParseException, UnsupportedCharsetException {
        if (s == null) {
            throw new IllegalArgumentException("Content type may not be null");
        }
        final HeaderElement[] elements = BasicHeaderValueParser.parseElements(s, null);
        if (elements.length > 0) {
            return create(elements[0]);
        }
        throw new ParseException("Invalid content type: " + s);
    }
    
    public static ContentType get(final HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        if (entity == null) {
            return null;
        }
        final Header header = entity.getContentType();
        if (header != null) {
            final HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                return create(elements[0]);
            }
        }
        return null;
    }
    
    public static ContentType getOrDefault(final HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        final ContentType contentType = get(entity);
        return (contentType != null) ? contentType : ContentType.DEFAULT_TEXT;
    }
    
    static {
        APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
        APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
        APPLICATION_JSON = create("application/json", Consts.UTF_8);
        APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
        APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
        APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
        APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
        MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
        TEXT_HTML = create("text/html", Consts.ISO_8859_1);
        TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
        TEXT_XML = create("text/xml", Consts.ISO_8859_1);
        WILDCARD = create("*/*", (Charset)null);
        DEFAULT_TEXT = ContentType.TEXT_PLAIN;
        DEFAULT_BINARY = ContentType.APPLICATION_OCTET_STREAM;
    }
}
