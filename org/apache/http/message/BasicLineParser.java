// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.ParseException;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Immutable;

@Immutable
public class BasicLineParser implements LineParser
{
    public static final BasicLineParser DEFAULT;
    protected final ProtocolVersion protocol;
    
    public BasicLineParser(ProtocolVersion proto) {
        if (proto == null) {
            proto = HttpVersion.HTTP_1_1;
        }
        this.protocol = proto;
    }
    
    public BasicLineParser() {
        this(null);
    }
    
    public static final ProtocolVersion parseProtocolVersion(final String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseProtocolVersion(buffer, cursor);
    }
    
    public ProtocolVersion parseProtocolVersion(final CharArrayBuffer buffer, final ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        final String protoname = this.protocol.getProtocol();
        final int protolength = protoname.length();
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        this.skipWhitespace(buffer, cursor);
        int i = cursor.getPos();
        if (i + protolength + 4 > indexTo) {
            throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
        }
        boolean ok = true;
        for (int j = 0; ok && j < protolength; ok = (buffer.charAt(i + j) == protoname.charAt(j)), ++j) {}
        if (ok) {
            ok = (buffer.charAt(i + protolength) == '/');
        }
        if (!ok) {
            throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
        }
        i += protolength + 1;
        final int period = buffer.indexOf(46, i, indexTo);
        if (period == -1) {
            throw new ParseException("Invalid protocol version number: " + buffer.substring(indexFrom, indexTo));
        }
        int major;
        try {
            major = Integer.parseInt(buffer.substringTrimmed(i, period));
        }
        catch (NumberFormatException e) {
            throw new ParseException("Invalid protocol major version number: " + buffer.substring(indexFrom, indexTo));
        }
        i = period + 1;
        int blank = buffer.indexOf(32, i, indexTo);
        if (blank == -1) {
            blank = indexTo;
        }
        int minor;
        try {
            minor = Integer.parseInt(buffer.substringTrimmed(i, blank));
        }
        catch (NumberFormatException e2) {
            throw new ParseException("Invalid protocol minor version number: " + buffer.substring(indexFrom, indexTo));
        }
        cursor.updatePos(blank);
        return this.createProtocolVersion(major, minor);
    }
    
    protected ProtocolVersion createProtocolVersion(final int major, final int minor) {
        return this.protocol.forVersion(major, minor);
    }
    
    public boolean hasProtocolVersion(final CharArrayBuffer buffer, final ParserCursor cursor) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        int index = cursor.getPos();
        final String protoname = this.protocol.getProtocol();
        final int protolength = protoname.length();
        if (buffer.length() < protolength + 4) {
            return false;
        }
        if (index < 0) {
            index = buffer.length() - 4 - protolength;
        }
        else if (index == 0) {
            while (index < buffer.length() && HTTP.isWhitespace(buffer.charAt(index))) {
                ++index;
            }
        }
        if (index + protolength + 4 > buffer.length()) {
            return false;
        }
        boolean ok = true;
        for (int j = 0; ok && j < protolength; ok = (buffer.charAt(index + j) == protoname.charAt(j)), ++j) {}
        if (ok) {
            ok = (buffer.charAt(index + protolength) == '/');
        }
        return ok;
    }
    
    public static final RequestLine parseRequestLine(final String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseRequestLine(buffer, cursor);
    }
    
    public RequestLine parseRequestLine(final CharArrayBuffer buffer, final ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        try {
            this.skipWhitespace(buffer, cursor);
            int i = cursor.getPos();
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            }
            final String method = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);
            this.skipWhitespace(buffer, cursor);
            i = cursor.getPos();
            blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            }
            final String uri = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);
            final ProtocolVersion ver = this.parseProtocolVersion(buffer, cursor);
            this.skipWhitespace(buffer, cursor);
            if (!cursor.atEnd()) {
                throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            }
            return this.createRequestLine(method, uri, ver);
        }
        catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
        }
    }
    
    protected RequestLine createRequestLine(final String method, final String uri, final ProtocolVersion ver) {
        return new BasicRequestLine(method, uri, ver);
    }
    
    public static final StatusLine parseStatusLine(final String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        final ParserCursor cursor = new ParserCursor(0, value.length());
        return parser.parseStatusLine(buffer, cursor);
    }
    
    public StatusLine parseStatusLine(final CharArrayBuffer buffer, final ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        try {
            final ProtocolVersion ver = this.parseProtocolVersion(buffer, cursor);
            this.skipWhitespace(buffer, cursor);
            int i = cursor.getPos();
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                blank = indexTo;
            }
            int statusCode = 0;
            final String s = buffer.substringTrimmed(i, blank);
            for (int j = 0; j < s.length(); ++j) {
                if (!Character.isDigit(s.charAt(j))) {
                    throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
                }
            }
            try {
                statusCode = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
            }
            i = blank;
            String reasonPhrase = null;
            if (i < indexTo) {
                reasonPhrase = buffer.substringTrimmed(i, indexTo);
            }
            else {
                reasonPhrase = "";
            }
            return this.createStatusLine(ver, statusCode, reasonPhrase);
        }
        catch (IndexOutOfBoundsException e2) {
            throw new ParseException("Invalid status line: " + buffer.substring(indexFrom, indexTo));
        }
    }
    
    protected StatusLine createStatusLine(final ProtocolVersion ver, final int status, final String reason) {
        return new BasicStatusLine(ver, status, reason);
    }
    
    public static final Header parseHeader(final String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        return parser.parseHeader(buffer);
    }
    
    public Header parseHeader(final CharArrayBuffer buffer) throws ParseException {
        return new BufferedHeader(buffer);
    }
    
    protected void skipWhitespace(final CharArrayBuffer buffer, final ParserCursor cursor) {
        int pos = cursor.getPos();
        for (int indexTo = cursor.getUpperBound(); pos < indexTo && HTTP.isWhitespace(buffer.charAt(pos)); ++pos) {}
        cursor.updatePos(pos);
    }
    
    static {
        DEFAULT = new BasicLineParser();
    }
}
