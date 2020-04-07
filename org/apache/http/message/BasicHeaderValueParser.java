// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.protocol.HTTP;
import org.apache.http.NameValuePair;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.ParseException;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Immutable;

@Immutable
public class BasicHeaderValueParser implements HeaderValueParser
{
    public static final BasicHeaderValueParser DEFAULT;
    private static final char PARAM_DELIMITER = ';';
    private static final char ELEM_DELIMITER = ',';
    private static final char[] ALL_DELIMITERS;
    
    public static final HeaderElement[] parseElements(final String value, HeaderValueParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = BasicHeaderValueParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseElements(buffer, cursor);
    }
    
    public HeaderElement[] parseElements(final CharArrayBuffer buffer, final ParserCursor cursor) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        final List<HeaderElement> elements = new ArrayList<HeaderElement>();
        while (!cursor.atEnd()) {
            final HeaderElement element = this.parseHeaderElement(buffer, cursor);
            if (element.getName().length() != 0 || element.getValue() != null) {
                elements.add(element);
            }
        }
        return elements.toArray(new HeaderElement[elements.size()]);
    }
    
    public static final HeaderElement parseHeaderElement(final String value, HeaderValueParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = BasicHeaderValueParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseHeaderElement(buffer, cursor);
    }
    
    public HeaderElement parseHeaderElement(final CharArrayBuffer buffer, final ParserCursor cursor) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        final NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
        NameValuePair[] params = null;
        if (!cursor.atEnd()) {
            final char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch != ',') {
                params = this.parseParameters(buffer, cursor);
            }
        }
        return this.createHeaderElement(nvp.getName(), nvp.getValue(), params);
    }
    
    protected HeaderElement createHeaderElement(final String name, final String value, final NameValuePair[] params) {
        return new BasicHeaderElement(name, value, params);
    }
    
    public static final NameValuePair[] parseParameters(final String value, HeaderValueParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = BasicHeaderValueParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseParameters(buffer, cursor);
    }
    
    public NameValuePair[] parseParameters(final CharArrayBuffer buffer, final ParserCursor cursor) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        int pos = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(); pos < indexTo; ++pos) {
            final char ch = buffer.charAt(pos);
            if (!HTTP.isWhitespace(ch)) {
                break;
            }
        }
        cursor.updatePos(pos);
        if (cursor.atEnd()) {
            return new NameValuePair[0];
        }
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            final NameValuePair param = this.parseNameValuePair(buffer, cursor);
            params.add(param);
            final char ch2 = buffer.charAt(cursor.getPos() - 1);
            if (ch2 == ',') {
                break;
            }
        }
        return params.toArray(new NameValuePair[params.size()]);
    }
    
    public static final NameValuePair parseNameValuePair(final String value, HeaderValueParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = BasicHeaderValueParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseNameValuePair(buffer, cursor);
    }
    
    public NameValuePair parseNameValuePair(final CharArrayBuffer buffer, final ParserCursor cursor) {
        return this.parseNameValuePair(buffer, cursor, BasicHeaderValueParser.ALL_DELIMITERS);
    }
    
    private static boolean isOneOf(final char ch, final char[] chs) {
        if (chs != null) {
            for (int i = 0; i < chs.length; ++i) {
                if (ch == chs[i]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public NameValuePair parseNameValuePair(final CharArrayBuffer buffer, final ParserCursor cursor, final char[] delimiters) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        boolean terminated = false;
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        String name = null;
        while (pos < indexTo) {
            final char ch = buffer.charAt(pos);
            if (ch == '=') {
                break;
            }
            if (isOneOf(ch, delimiters)) {
                terminated = true;
                break;
            }
            ++pos;
        }
        if (pos == indexTo) {
            terminated = true;
            name = buffer.substringTrimmed(indexFrom, indexTo);
        }
        else {
            name = buffer.substringTrimmed(indexFrom, pos);
            ++pos;
        }
        if (terminated) {
            cursor.updatePos(pos);
            return this.createNameValuePair(name, null);
        }
        String value = null;
        int i1 = pos;
        boolean qouted = false;
        boolean escaped = false;
        while (pos < indexTo) {
            final char ch2 = buffer.charAt(pos);
            if (ch2 == '\"' && !escaped) {
                qouted = !qouted;
            }
            if (!qouted && !escaped && isOneOf(ch2, delimiters)) {
                terminated = true;
                break;
            }
            escaped = (!escaped && qouted && ch2 == '\\');
            ++pos;
        }
        int i2;
        for (i2 = pos; i1 < i2 && HTTP.isWhitespace(buffer.charAt(i1)); ++i1) {}
        while (i2 > i1 && HTTP.isWhitespace(buffer.charAt(i2 - 1))) {
            --i2;
        }
        if (i2 - i1 >= 2 && buffer.charAt(i1) == '\"' && buffer.charAt(i2 - 1) == '\"') {
            ++i1;
            --i2;
        }
        value = buffer.substring(i1, i2);
        if (terminated) {
            ++pos;
        }
        cursor.updatePos(pos);
        return this.createNameValuePair(name, value);
    }
    
    protected NameValuePair createNameValuePair(final String name, final String value) {
        return new BasicNameValuePair(name, value);
    }
    
    static {
        DEFAULT = new BasicHeaderValueParser();
        ALL_DELIMITERS = new char[] { ';', ',' };
    }
}
