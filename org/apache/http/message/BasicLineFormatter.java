// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.RequestLine;
import org.apache.http.ProtocolVersion;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.annotation.Immutable;

@Immutable
public class BasicLineFormatter implements LineFormatter
{
    public static final BasicLineFormatter DEFAULT;
    
    protected CharArrayBuffer initBuffer(CharArrayBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
        }
        else {
            buffer = new CharArrayBuffer(64);
        }
        return buffer;
    }
    
    public static final String formatProtocolVersion(final ProtocolVersion version, LineFormatter formatter) {
        if (formatter == null) {
            formatter = BasicLineFormatter.DEFAULT;
        }
        return formatter.appendProtocolVersion(null, version).toString();
    }
    
    public CharArrayBuffer appendProtocolVersion(final CharArrayBuffer buffer, final ProtocolVersion version) {
        if (version == null) {
            throw new IllegalArgumentException("Protocol version may not be null");
        }
        CharArrayBuffer result = buffer;
        final int len = this.estimateProtocolVersionLen(version);
        if (result == null) {
            result = new CharArrayBuffer(len);
        }
        else {
            result.ensureCapacity(len);
        }
        result.append(version.getProtocol());
        result.append('/');
        result.append(Integer.toString(version.getMajor()));
        result.append('.');
        result.append(Integer.toString(version.getMinor()));
        return result;
    }
    
    protected int estimateProtocolVersionLen(final ProtocolVersion version) {
        return version.getProtocol().length() + 4;
    }
    
    public static final String formatRequestLine(final RequestLine reqline, LineFormatter formatter) {
        if (formatter == null) {
            formatter = BasicLineFormatter.DEFAULT;
        }
        return formatter.formatRequestLine(null, reqline).toString();
    }
    
    public CharArrayBuffer formatRequestLine(final CharArrayBuffer buffer, final RequestLine reqline) {
        if (reqline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        final CharArrayBuffer result = this.initBuffer(buffer);
        this.doFormatRequestLine(result, reqline);
        return result;
    }
    
    protected void doFormatRequestLine(final CharArrayBuffer buffer, final RequestLine reqline) {
        final String method = reqline.getMethod();
        final String uri = reqline.getUri();
        final int len = method.length() + 1 + uri.length() + 1 + this.estimateProtocolVersionLen(reqline.getProtocolVersion());
        buffer.ensureCapacity(len);
        buffer.append(method);
        buffer.append(' ');
        buffer.append(uri);
        buffer.append(' ');
        this.appendProtocolVersion(buffer, reqline.getProtocolVersion());
    }
    
    public static final String formatStatusLine(final StatusLine statline, LineFormatter formatter) {
        if (formatter == null) {
            formatter = BasicLineFormatter.DEFAULT;
        }
        return formatter.formatStatusLine(null, statline).toString();
    }
    
    public CharArrayBuffer formatStatusLine(final CharArrayBuffer buffer, final StatusLine statline) {
        if (statline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        }
        final CharArrayBuffer result = this.initBuffer(buffer);
        this.doFormatStatusLine(result, statline);
        return result;
    }
    
    protected void doFormatStatusLine(final CharArrayBuffer buffer, final StatusLine statline) {
        int len = this.estimateProtocolVersionLen(statline.getProtocolVersion()) + 1 + 3 + 1;
        final String reason = statline.getReasonPhrase();
        if (reason != null) {
            len += reason.length();
        }
        buffer.ensureCapacity(len);
        this.appendProtocolVersion(buffer, statline.getProtocolVersion());
        buffer.append(' ');
        buffer.append(Integer.toString(statline.getStatusCode()));
        buffer.append(' ');
        if (reason != null) {
            buffer.append(reason);
        }
    }
    
    public static final String formatHeader(final Header header, LineFormatter formatter) {
        if (formatter == null) {
            formatter = BasicLineFormatter.DEFAULT;
        }
        return formatter.formatHeader(null, header).toString();
    }
    
    public CharArrayBuffer formatHeader(final CharArrayBuffer buffer, final Header header) {
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        }
        CharArrayBuffer result = null;
        if (header instanceof FormattedHeader) {
            result = ((FormattedHeader)header).getBuffer();
        }
        else {
            result = this.initBuffer(buffer);
            this.doFormatHeader(result, header);
        }
        return result;
    }
    
    protected void doFormatHeader(final CharArrayBuffer buffer, final Header header) {
        final String name = header.getName();
        final String value = header.getValue();
        int len = name.length() + 2;
        if (value != null) {
            len += value.length();
        }
        buffer.ensureCapacity(len);
        buffer.append(name);
        buffer.append(": ");
        if (value != null) {
            buffer.append(value);
        }
    }
    
    static {
        DEFAULT = new BasicLineFormatter();
    }
}
