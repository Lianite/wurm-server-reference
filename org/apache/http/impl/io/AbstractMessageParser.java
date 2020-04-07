// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.Header;
import java.util.ArrayList;
import org.apache.http.message.BasicLineParser;
import org.apache.http.params.HttpParams;
import org.apache.http.message.LineParser;
import org.apache.http.util.CharArrayBuffer;
import java.util.List;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.HttpMessage;

@NotThreadSafe
public abstract class AbstractMessageParser<T extends HttpMessage> implements HttpMessageParser<T>
{
    private static final int HEAD_LINE = 0;
    private static final int HEADERS = 1;
    private final SessionInputBuffer sessionBuffer;
    private final int maxHeaderCount;
    private final int maxLineLen;
    private final List<CharArrayBuffer> headerLines;
    protected final LineParser lineParser;
    private int state;
    private T message;
    
    public AbstractMessageParser(final SessionInputBuffer buffer, final LineParser parser, final HttpParams params) {
        if (buffer == null) {
            throw new IllegalArgumentException("Session input buffer may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.sessionBuffer = buffer;
        this.maxHeaderCount = params.getIntParameter("http.connection.max-header-count", -1);
        this.maxLineLen = params.getIntParameter("http.connection.max-line-length", -1);
        this.lineParser = ((parser != null) ? parser : BasicLineParser.DEFAULT);
        this.headerLines = new ArrayList<CharArrayBuffer>();
        this.state = 0;
    }
    
    public static Header[] parseHeaders(final SessionInputBuffer inbuffer, final int maxHeaderCount, final int maxLineLen, LineParser parser) throws HttpException, IOException {
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        final List<CharArrayBuffer> headerLines = new ArrayList<CharArrayBuffer>();
        return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, parser, headerLines);
    }
    
    public static Header[] parseHeaders(final SessionInputBuffer inbuffer, final int maxHeaderCount, final int maxLineLen, final LineParser parser, final List<CharArrayBuffer> headerLines) throws HttpException, IOException {
        if (inbuffer == null) {
            throw new IllegalArgumentException("Session input buffer may not be null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("Line parser may not be null");
        }
        if (headerLines == null) {
            throw new IllegalArgumentException("Header line list may not be null");
        }
        CharArrayBuffer current = null;
        CharArrayBuffer previous = null;
        while (true) {
            if (current == null) {
                current = new CharArrayBuffer(64);
            }
            else {
                current.clear();
            }
            final int l = inbuffer.readLine(current);
            if (l == -1 || current.length() < 1) {
                final Header[] headers = new Header[headerLines.size()];
                for (int i = 0; i < headerLines.size(); ++i) {
                    final CharArrayBuffer buffer = headerLines.get(i);
                    try {
                        headers[i] = parser.parseHeader(buffer);
                    }
                    catch (ParseException ex) {
                        throw new ProtocolException(ex.getMessage());
                    }
                }
                return headers;
            }
            if ((current.charAt(0) == ' ' || current.charAt(0) == '\t') && previous != null) {
                int i;
                for (i = 0; i < current.length(); ++i) {
                    final char ch = current.charAt(i);
                    if (ch != ' ' && ch != '\t') {
                        break;
                    }
                }
                if (maxLineLen > 0 && previous.length() + 1 + current.length() - i > maxLineLen) {
                    throw new IOException("Maximum line length limit exceeded");
                }
                previous.append(' ');
                previous.append(current, i, current.length() - i);
            }
            else {
                headerLines.add(current);
                previous = current;
                current = null;
            }
            if (maxHeaderCount > 0 && headerLines.size() >= maxHeaderCount) {
                throw new IOException("Maximum header count exceeded");
            }
        }
    }
    
    protected abstract T parseHead(final SessionInputBuffer p0) throws IOException, HttpException, ParseException;
    
    public T parse() throws IOException, HttpException {
        final int st = this.state;
        switch (st) {
            case 0: {
                try {
                    this.message = this.parseHead(this.sessionBuffer);
                }
                catch (ParseException px) {
                    throw new ProtocolException(px.getMessage(), px);
                }
                this.state = 1;
            }
            case 1: {
                final Header[] headers = parseHeaders(this.sessionBuffer, this.maxHeaderCount, this.maxLineLen, this.lineParser, this.headerLines);
                this.message.setHeaders(headers);
                final T result = this.message;
                this.message = null;
                this.headerLines.clear();
                this.state = 0;
                return result;
            }
            default: {
                throw new IllegalStateException("Inconsistent parser state");
            }
        }
    }
}
