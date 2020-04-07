// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Hashtable;

public class MimeTypeParameterList
{
    private Hashtable parameters;
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
    
    public MimeTypeParameterList() {
        this.parameters = new Hashtable();
    }
    
    public MimeTypeParameterList(final String parameterList) throws MimeTypeParseException {
        this.parameters = new Hashtable();
        this.parse(parameterList);
    }
    
    protected void parse(final String parameterList) throws MimeTypeParseException {
        if (parameterList == null) {
            return;
        }
        final int length = parameterList.length();
        if (length <= 0) {
            return;
        }
        int i;
        char c;
        for (i = skipWhiteSpace(parameterList, 0); i < length && (c = parameterList.charAt(i)) == ';'; i = skipWhiteSpace(parameterList, i)) {
            ++i;
            i = skipWhiteSpace(parameterList, i);
            if (i >= length) {
                return;
            }
            int lastIndex = i;
            while (i < length && isTokenChar(parameterList.charAt(i))) {
                ++i;
            }
            final String name = parameterList.substring(lastIndex, i).toLowerCase(Locale.ENGLISH);
            i = skipWhiteSpace(parameterList, i);
            if (i >= length || parameterList.charAt(i) != '=') {
                throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
            }
            ++i;
            i = skipWhiteSpace(parameterList, i);
            if (i >= length) {
                throw new MimeTypeParseException("Couldn't find a value for parameter named " + name);
            }
            c = parameterList.charAt(i);
            String value;
            if (c == '\"') {
                if (++i >= length) {
                    throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                }
                lastIndex = i;
                while (i < length) {
                    c = parameterList.charAt(i);
                    if (c == '\"') {
                        break;
                    }
                    if (c == '\\') {
                        ++i;
                    }
                    ++i;
                }
                if (c != '\"') {
                    throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                }
                value = unquote(parameterList.substring(lastIndex, i));
                ++i;
            }
            else {
                if (!isTokenChar(c)) {
                    throw new MimeTypeParseException("Unexpected character encountered at index " + i);
                }
                lastIndex = i;
                while (i < length && isTokenChar(parameterList.charAt(i))) {
                    ++i;
                }
                value = parameterList.substring(lastIndex, i);
            }
            this.parameters.put(name, value);
        }
        if (i < length) {
            throw new MimeTypeParseException("More characters encountered in input than expected.");
        }
    }
    
    public int size() {
        return this.parameters.size();
    }
    
    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }
    
    public String get(final String name) {
        return this.parameters.get(name.trim().toLowerCase(Locale.ENGLISH));
    }
    
    public void set(final String name, final String value) {
        this.parameters.put(name.trim().toLowerCase(Locale.ENGLISH), value);
    }
    
    public void remove(final String name) {
        this.parameters.remove(name.trim().toLowerCase(Locale.ENGLISH));
    }
    
    public Enumeration getNames() {
        return this.parameters.keys();
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(this.parameters.size() * 16);
        final Enumeration keys = this.parameters.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            buffer.append("; ");
            buffer.append(key);
            buffer.append('=');
            buffer.append(quote(this.parameters.get(key)));
        }
        return buffer.toString();
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
    }
    
    private static int skipWhiteSpace(final String rawdata, int i) {
        for (int length = rawdata.length(); i < length && Character.isWhitespace(rawdata.charAt(i)); ++i) {}
        return i;
    }
    
    private static String quote(final String value) {
        boolean needsQuotes = false;
        final int length = value.length();
        for (int i = 0; i < length && !needsQuotes; needsQuotes = !isTokenChar(value.charAt(i)), ++i) {}
        if (needsQuotes) {
            final StringBuffer buffer = new StringBuffer();
            buffer.ensureCapacity((int)(length * 1.5));
            buffer.append('\"');
            for (int j = 0; j < length; ++j) {
                final char c = value.charAt(j);
                if (c == '\\' || c == '\"') {
                    buffer.append('\\');
                }
                buffer.append(c);
            }
            buffer.append('\"');
            return buffer.toString();
        }
        return value;
    }
    
    private static String unquote(final String value) {
        final int valueLength = value.length();
        final StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(valueLength);
        boolean escaped = false;
        for (int i = 0; i < valueLength; ++i) {
            final char currentChar = value.charAt(i);
            if (!escaped && currentChar != '\\') {
                buffer.append(currentChar);
            }
            else if (escaped) {
                buffer.append(currentChar);
                escaped = false;
            }
            else {
                escaped = true;
            }
        }
        return buffer.toString();
    }
}
