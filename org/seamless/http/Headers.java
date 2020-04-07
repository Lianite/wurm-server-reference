// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers implements Map<String, List<String>>
{
    static final byte CR = 13;
    static final byte LF = 10;
    final Map<String, List<String>> map;
    private boolean normalizeHeaders;
    
    public Headers() {
        this.map = new HashMap<String, List<String>>(32);
        this.normalizeHeaders = true;
    }
    
    public Headers(final Map<String, List<String>> map) {
        this.map = new HashMap<String, List<String>>(32);
        this.normalizeHeaders = true;
        this.putAll(map);
    }
    
    public Headers(final ByteArrayInputStream inputStream) {
        this.map = new HashMap<String, List<String>>(32);
        this.normalizeHeaders = true;
        final StringBuilder sb = new StringBuilder(256);
        final Headers headers = new Headers();
        String line = readLine(sb, inputStream);
        String lastHeader = null;
        if (line.length() != 0) {
            do {
                final char firstChar = line.charAt(0);
                if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                    final List<String> current = headers.get((Object)lastHeader);
                    final int lastPos = current.size() - 1;
                    final String newString = current.get(lastPos) + line.trim();
                    current.set(lastPos, newString);
                }
                else {
                    final String[] header = this.splitHeader(line);
                    headers.add(header[0], header[1]);
                    lastHeader = header[0];
                }
                sb.delete(0, sb.length());
                line = readLine(sb, inputStream);
            } while (line.length() != 0);
        }
        this.putAll(headers);
    }
    
    public Headers(final boolean normalizeHeaders) {
        this.map = new HashMap<String, List<String>>(32);
        this.normalizeHeaders = true;
        this.normalizeHeaders = normalizeHeaders;
    }
    
    public int size() {
        return this.map.size();
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public boolean containsKey(final Object key) {
        return key != null && key instanceof String && this.map.containsKey(this.normalize((String)key));
    }
    
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }
    
    public List<String> get(final Object key) {
        return this.map.get(this.normalize((String)key));
    }
    
    public List<String> put(final String key, final List<String> value) {
        return this.map.put(this.normalize(key), value);
    }
    
    public List<String> remove(final Object key) {
        return this.map.remove(this.normalize((String)key));
    }
    
    public void putAll(final Map<? extends String, ? extends List<String>> t) {
        for (final Entry<? extends String, ? extends List<String>> entry : t.entrySet()) {
            this.put((String)entry.getKey(), (List<String>)entry.getValue());
        }
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public Set<String> keySet() {
        return this.map.keySet();
    }
    
    public Collection<List<String>> values() {
        return this.map.values();
    }
    
    public Set<Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }
    
    public boolean equals(final Object o) {
        return this.map.equals(o);
    }
    
    public int hashCode() {
        return this.map.hashCode();
    }
    
    public String getFirstHeader(final String key) {
        final List<String> l = this.map.get(this.normalize(key));
        return (l != null && l.size() > 0) ? l.get(0) : null;
    }
    
    public void add(final String key, final String value) {
        final String k = this.normalize(key);
        List<String> l = this.map.get(k);
        if (l == null) {
            l = new LinkedList<String>();
            this.map.put(k, l);
        }
        l.add(value);
    }
    
    public void set(final String key, final String value) {
        final LinkedList<String> l = new LinkedList<String>();
        l.add(value);
        this.put(key, (List<String>)l);
    }
    
    private String normalize(final String key) {
        String result = key;
        if (this.normalizeHeaders) {
            if (key == null) {
                return null;
            }
            if (key.length() == 0) {
                return key;
            }
            final char[] b = key.toCharArray();
            final int caseDiff = 32;
            if (b[0] >= 'a' && b[0] <= 'z') {
                b[0] -= ' ';
            }
            for (int length = key.length(), i = 1; i < length; ++i) {
                if (b[i] >= 'A' && b[i] <= 'Z') {
                    b[i] += ' ';
                }
            }
            result = new String(b);
        }
        return result;
    }
    
    public static String readLine(final ByteArrayInputStream is) {
        return readLine(new StringBuilder(256), is);
    }
    
    public static String readLine(final StringBuilder sb, final ByteArrayInputStream is) {
        int nextByte;
        while ((nextByte = is.read()) != -1) {
            final char nextChar = (char)nextByte;
            if (nextChar == '\r') {
                nextByte = (char)is.read();
                if (nextByte == 10) {
                    break;
                }
            }
            else if (nextChar == '\n') {
                break;
            }
            sb.append(nextChar);
        }
        return sb.toString();
    }
    
    protected String[] splitHeader(final String sb) {
        int nameEnd;
        int nameStart;
        for (nameStart = (nameEnd = this.findNonWhitespace(sb, 0)); nameEnd < sb.length(); ++nameEnd) {
            final char ch = sb.charAt(nameEnd);
            if (ch == ':') {
                break;
            }
            if (Character.isWhitespace(ch)) {
                break;
            }
        }
        int colonEnd;
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt(colonEnd) == ':') {
                ++colonEnd;
                break;
            }
        }
        final int valueStart = this.findNonWhitespace(sb, colonEnd);
        final int valueEnd = this.findEndOfString(sb);
        return new String[] { sb.substring(nameStart, nameEnd), (sb.length() >= valueStart && sb.length() >= valueEnd && valueStart < valueEnd) ? sb.substring(valueStart, valueEnd) : null };
    }
    
    protected int findNonWhitespace(final String sb, final int offset) {
        int result;
        for (result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {}
        return result;
    }
    
    protected int findEndOfString(final String sb) {
        int result;
        for (result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {}
        return result;
    }
    
    public String toString() {
        final StringBuilder headerString = new StringBuilder(512);
        for (final Entry<String, List<String>> headerEntry : this.entrySet()) {
            headerString.append(headerEntry.getKey()).append(": ");
            for (final String v : headerEntry.getValue()) {
                headerString.append(v).append(",");
            }
            headerString.delete(headerString.length() - 1, headerString.length());
            headerString.append("\r\n");
        }
        return headerString.toString();
    }
}
