// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind.annotation.adapters;

public final class NormalizedStringAdapter extends XmlAdapter<String, String>
{
    public String unmarshal(final String text) {
        if (text == null) {
            return null;
        }
        int i;
        for (i = text.length() - 1; i >= 0 && !isWhiteSpaceExceptSpace(text.charAt(i)); --i) {}
        if (i < 0) {
            return text;
        }
        final char[] buf = text.toCharArray();
        buf[i--] = ' ';
        while (i >= 0) {
            if (isWhiteSpaceExceptSpace(buf[i])) {
                buf[i] = ' ';
            }
            --i;
        }
        return new String(buf);
    }
    
    public String marshal(final String s) {
        return s;
    }
    
    protected static boolean isWhiteSpaceExceptSpace(final char ch) {
        return ch < ' ' && (ch == '\t' || ch == '\n' || ch == '\r');
    }
}
