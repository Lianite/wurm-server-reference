// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.bind.DatatypeConverter;
import java.util.GregorianCalendar;
import java.util.Calendar;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.bind.v2.TODO;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.bind.DatatypeConverterInterface;

public final class DatatypeConverterImpl implements DatatypeConverterInterface
{
    public static final DatatypeConverterInterface theInstance;
    private static final char[] hexCode;
    private static final byte[] decodeMap;
    private static final byte PADDING = Byte.MAX_VALUE;
    private static final char[] encodeMap;
    private static final DatatypeFactory datatypeFactory;
    
    public String parseString(final String lexicalXSDString) {
        return lexicalXSDString;
    }
    
    public BigInteger parseInteger(final String lexicalXSDInteger) {
        return _parseInteger(lexicalXSDInteger);
    }
    
    public static BigInteger _parseInteger(final CharSequence s) {
        return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }
    
    public String printInteger(final BigInteger val) {
        return _printInteger(val);
    }
    
    public static String _printInteger(final BigInteger val) {
        return val.toString();
    }
    
    public int parseInt(final String s) {
        return _parseInt(s);
    }
    
    public static int _parseInt(final CharSequence s) {
        final int len = s.length();
        int sign = 1;
        int r = 0;
        for (int i = 0; i < len; ++i) {
            final char ch = s.charAt(i);
            if (!WhiteSpaceProcessor.isWhiteSpace(ch)) {
                if ('0' <= ch && ch <= '9') {
                    r = r * 10 + (ch - '0');
                }
                else if (ch == '-') {
                    sign = -1;
                }
                else if (ch != '+') {
                    throw new NumberFormatException("Not a number: " + (Object)s);
                }
            }
        }
        return r * sign;
    }
    
    public long parseLong(final String lexicalXSLong) {
        return _parseLong(lexicalXSLong);
    }
    
    public static long _parseLong(final CharSequence s) {
        return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }
    
    public short parseShort(final String lexicalXSDShort) {
        return _parseShort(lexicalXSDShort);
    }
    
    public static short _parseShort(final CharSequence s) {
        return (short)_parseInt(s);
    }
    
    public String printShort(final short val) {
        return _printShort(val);
    }
    
    public static String _printShort(final short val) {
        return String.valueOf(val);
    }
    
    public BigDecimal parseDecimal(final String content) {
        return _parseDecimal(content);
    }
    
    public static BigDecimal _parseDecimal(CharSequence content) {
        content = WhiteSpaceProcessor.trim(content);
        return new BigDecimal(content.toString());
    }
    
    public float parseFloat(final String lexicalXSDFloat) {
        return _parseFloat(lexicalXSDFloat);
    }
    
    public static float _parseFloat(final CharSequence _val) {
        final String s = WhiteSpaceProcessor.trim(_val).toString();
        if (s.equals("NaN")) {
            return Float.NaN;
        }
        if (s.equals("INF")) {
            return Float.POSITIVE_INFINITY;
        }
        if (s.equals("-INF")) {
            return Float.NEGATIVE_INFINITY;
        }
        if (s.length() == 0 || !isDigitOrPeriodOrSign(s.charAt(0)) || !isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
            throw new NumberFormatException();
        }
        return Float.parseFloat(s);
    }
    
    public String printFloat(final float v) {
        return _printFloat(v);
    }
    
    public static String _printFloat(final float v) {
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }
    
    public double parseDouble(final String lexicalXSDDouble) {
        return _parseDouble(lexicalXSDDouble);
    }
    
    public static double _parseDouble(final CharSequence _val) {
        final String val = WhiteSpaceProcessor.trim(_val).toString();
        if (val.equals("NaN")) {
            return Double.NaN;
        }
        if (val.equals("INF")) {
            return Double.POSITIVE_INFINITY;
        }
        if (val.equals("-INF")) {
            return Double.NEGATIVE_INFINITY;
        }
        if (val.length() == 0 || !isDigitOrPeriodOrSign(val.charAt(0)) || !isDigitOrPeriodOrSign(val.charAt(val.length() - 1))) {
            throw new NumberFormatException(val);
        }
        return Double.parseDouble(val);
    }
    
    public boolean parseBoolean(final String lexicalXSDBoolean) {
        return _parseBoolean(lexicalXSDBoolean);
    }
    
    public static boolean _parseBoolean(final CharSequence literal) {
        int i = 0;
        final int len = literal.length();
        char ch;
        do {
            ch = literal.charAt(i++);
        } while (WhiteSpaceProcessor.isWhiteSpace(ch) && i < len);
        if (ch == 't' || ch == '1') {
            return true;
        }
        if (ch == 'f' || ch == '0') {
            return false;
        }
        TODO.checkSpec("issue #42");
        return false;
    }
    
    public String printBoolean(final boolean val) {
        return val ? "true" : "false";
    }
    
    public static String _printBoolean(final boolean val) {
        return val ? "true" : "false";
    }
    
    public byte parseByte(final String lexicalXSDByte) {
        return _parseByte(lexicalXSDByte);
    }
    
    public static byte _parseByte(final CharSequence literal) {
        return (byte)_parseInt(literal);
    }
    
    public String printByte(final byte val) {
        return _printByte(val);
    }
    
    public static String _printByte(final byte val) {
        return String.valueOf(val);
    }
    
    public QName parseQName(final String lexicalXSDQName, final NamespaceContext nsc) {
        return _parseQName(lexicalXSDQName, nsc);
    }
    
    public static QName _parseQName(final CharSequence text, final NamespaceContext nsc) {
        int length;
        int start;
        for (length = text.length(), start = 0; start < length && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)); ++start) {}
        int end;
        for (end = length; end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1)); --end) {}
        if (end == start) {
            throw new IllegalArgumentException("input is empty");
        }
        int idx;
        for (idx = start + 1; idx < end && text.charAt(idx) != ':'; ++idx) {}
        String uri;
        String localPart;
        String prefix;
        if (idx == end) {
            uri = nsc.getNamespaceURI("");
            localPart = text.subSequence(start, end).toString();
            prefix = "";
        }
        else {
            prefix = text.subSequence(start, idx).toString();
            localPart = text.subSequence(idx + 1, end).toString();
            uri = nsc.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
            }
        }
        return new QName(uri, localPart, prefix);
    }
    
    public Calendar parseDateTime(final String lexicalXSDDateTime) {
        return _parseDateTime(lexicalXSDDateTime);
    }
    
    public static GregorianCalendar _parseDateTime(final CharSequence s) {
        final String val = WhiteSpaceProcessor.trim(s).toString();
        return DatatypeConverterImpl.datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
    }
    
    public String printDateTime(final Calendar val) {
        return _printDateTime(val);
    }
    
    public static String _printDateTime(final Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
    }
    
    public byte[] parseBase64Binary(final String lexicalXSDBase64Binary) {
        return _parseBase64Binary(lexicalXSDBase64Binary);
    }
    
    public byte[] parseHexBinary(final String s) {
        final int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }
        final byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            final int h = hexToBin(s.charAt(i));
            final int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }
            out[i / 2] = (byte)(h * 16 + l);
        }
        return out;
    }
    
    private static int hexToBin(final char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + '\n';
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + '\n';
        }
        return -1;
    }
    
    public String printHexBinary(final byte[] data) {
        final StringBuilder r = new StringBuilder(data.length * 2);
        for (final byte b : data) {
            r.append(DatatypeConverterImpl.hexCode[b >> 4 & 0xF]);
            r.append(DatatypeConverterImpl.hexCode[b & 0xF]);
        }
        return r.toString();
    }
    
    public long parseUnsignedInt(final String lexicalXSDUnsignedInt) {
        return _parseLong(lexicalXSDUnsignedInt);
    }
    
    public String printUnsignedInt(final long val) {
        return _printLong(val);
    }
    
    public int parseUnsignedShort(final String lexicalXSDUnsignedShort) {
        return _parseInt(lexicalXSDUnsignedShort);
    }
    
    public Calendar parseTime(final String lexicalXSDTime) {
        return DatatypeConverterImpl.datatypeFactory.newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
    }
    
    public String printTime(final Calendar val) {
        return CalendarFormatter.doFormat("%h:%m:%s%z", val);
    }
    
    public Calendar parseDate(final String lexicalXSDDate) {
        return DatatypeConverterImpl.datatypeFactory.newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
    }
    
    public String printDate(final Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", val);
    }
    
    public String parseAnySimpleType(final String lexicalXSDAnySimpleType) {
        return lexicalXSDAnySimpleType;
    }
    
    public String printString(final String val) {
        return val;
    }
    
    public String printInt(final int val) {
        return _printInt(val);
    }
    
    public static String _printInt(final int val) {
        return String.valueOf(val);
    }
    
    public String printLong(final long val) {
        return _printLong(val);
    }
    
    public static String _printLong(final long val) {
        return String.valueOf(val);
    }
    
    public String printDecimal(final BigDecimal val) {
        return _printDecimal(val);
    }
    
    public static String _printDecimal(final BigDecimal val) {
        return val.toPlainString();
    }
    
    public String printDouble(final double v) {
        return _printDouble(v);
    }
    
    public static String _printDouble(final double v) {
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }
    
    public String printQName(final QName val, final NamespaceContext nsc) {
        return _printQName(val, nsc);
    }
    
    public static String _printQName(final QName val, final NamespaceContext nsc) {
        final String prefix = nsc.getPrefix(val.getNamespaceURI());
        final String localPart = val.getLocalPart();
        String qname;
        if (prefix == null || prefix.length() == 0) {
            qname = localPart;
        }
        else {
            qname = prefix + ':' + localPart;
        }
        return qname;
    }
    
    public String printBase64Binary(final byte[] val) {
        return _printBase64Binary(val);
    }
    
    public String printUnsignedShort(final int val) {
        return String.valueOf(val);
    }
    
    public String printAnySimpleType(final String val) {
        return val;
    }
    
    public static String installHook(final String s) {
        DatatypeConverter.setDatatypeConverter(DatatypeConverterImpl.theInstance);
        return s;
    }
    
    private static byte[] initDecodeMap() {
        final byte[] map = new byte[128];
        for (int i = 0; i < 128; ++i) {
            map[i] = -1;
        }
        for (int i = 65; i <= 90; ++i) {
            map[i] = (byte)(i - 65);
        }
        for (int i = 97; i <= 122; ++i) {
            map[i] = (byte)(i - 97 + 26);
        }
        for (int i = 48; i <= 57; ++i) {
            map[i] = (byte)(i - 48 + 52);
        }
        map[43] = 62;
        map[47] = 63;
        map[61] = 127;
        return map;
    }
    
    private static int guessLength(final String text) {
        final int len = text.length();
        int j = len - 1;
        while (j >= 0) {
            final byte code = DatatypeConverterImpl.decodeMap[text.charAt(j)];
            if (code == 127) {
                --j;
            }
            else {
                if (code == -1) {
                    return text.length() / 4 * 3;
                }
                break;
            }
        }
        ++j;
        final int padSize = len - j;
        if (padSize > 2) {
            return text.length() / 4 * 3;
        }
        return text.length() / 4 * 3 - padSize;
    }
    
    public static byte[] _parseBase64Binary(final String text) {
        final int buflen = guessLength(text);
        final byte[] out = new byte[buflen];
        int o = 0;
        final int len = text.length();
        final byte[] quadruplet = new byte[4];
        int q = 0;
        for (int i = 0; i < len; ++i) {
            final char ch = text.charAt(i);
            final byte v = DatatypeConverterImpl.decodeMap[ch];
            if (v != -1) {
                quadruplet[q++] = v;
            }
            if (q == 4) {
                out[o++] = (byte)(quadruplet[0] << 2 | quadruplet[1] >> 4);
                if (quadruplet[2] != 127) {
                    out[o++] = (byte)(quadruplet[1] << 4 | quadruplet[2] >> 2);
                }
                if (quadruplet[3] != 127) {
                    out[o++] = (byte)(quadruplet[2] << 6 | quadruplet[3]);
                }
                q = 0;
            }
        }
        if (buflen == o) {
            return out;
        }
        final byte[] nb = new byte[o];
        System.arraycopy(out, 0, nb, 0, o);
        return nb;
    }
    
    private static char[] initEncodeMap() {
        final char[] map = new char[64];
        for (int i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }
        for (int i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }
        for (int i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }
        map[62] = '+';
        map[63] = '/';
        return map;
    }
    
    public static char encode(final int i) {
        return DatatypeConverterImpl.encodeMap[i & 0x3F];
    }
    
    public static byte encodeByte(final int i) {
        return (byte)DatatypeConverterImpl.encodeMap[i & 0x3F];
    }
    
    public static String _printBase64Binary(final byte[] input) {
        return _printBase64Binary(input, 0, input.length);
    }
    
    public static String _printBase64Binary(final byte[] input, final int offset, final int len) {
        final char[] buf = new char[(len + 2) / 3 * 4];
        final int ptr = _printBase64Binary(input, offset, len, buf, 0);
        assert ptr == buf.length;
        return new String(buf);
    }
    
    public static int _printBase64Binary(final byte[] input, final int offset, final int len, final char[] buf, int ptr) {
        for (int i = offset; i < len; i += 3) {
            switch (len - i) {
                case 1: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4);
                    buf[ptr++] = '=';
                    buf[ptr++] = '=';
                    break;
                }
                case 2: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    buf[ptr++] = encode((input[i + 1] & 0xF) << 2);
                    buf[ptr++] = '=';
                    break;
                }
                default: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    buf[ptr++] = encode((input[i + 1] & 0xF) << 2 | (input[i + 2] >> 6 & 0x3));
                    buf[ptr++] = encode(input[i + 2] & 0x3F);
                    break;
                }
            }
        }
        return ptr;
    }
    
    public static int _printBase64Binary(final byte[] input, final int offset, final int len, final byte[] out, int ptr) {
        for (int max = len + offset, i = offset; i < max; i += 3) {
            switch (max - i) {
                case 1: {
                    out[ptr++] = encodeByte(input[i] >> 2);
                    out[ptr++] = encodeByte((input[i] & 0x3) << 4);
                    out[ptr++] = 61;
                    out[ptr++] = 61;
                    break;
                }
                case 2: {
                    out[ptr++] = encodeByte(input[i] >> 2);
                    out[ptr++] = encodeByte((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    out[ptr++] = encodeByte((input[i + 1] & 0xF) << 2);
                    out[ptr++] = 61;
                    break;
                }
                default: {
                    out[ptr++] = encodeByte(input[i] >> 2);
                    out[ptr++] = encodeByte((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    out[ptr++] = encodeByte((input[i + 1] & 0xF) << 2 | (input[i + 2] >> 6 & 0x3));
                    out[ptr++] = encodeByte(input[i + 2] & 0x3F);
                    break;
                }
            }
        }
        return ptr;
    }
    
    private static CharSequence removeOptionalPlus(CharSequence s) {
        final int len = s.length();
        if (len <= 1 || s.charAt(0) != '+') {
            return s;
        }
        s = s.subSequence(1, len);
        final char ch = s.charAt(0);
        if ('0' <= ch && ch <= '9') {
            return s;
        }
        if ('.' == ch) {
            return s;
        }
        throw new NumberFormatException();
    }
    
    private static boolean isDigitOrPeriodOrSign(final char ch) {
        return ('0' <= ch && ch <= '9') || (ch == '+' || ch == '-' || ch == '.');
    }
    
    static {
        theInstance = new DatatypeConverterImpl();
        hexCode = "0123456789ABCDEF".toCharArray();
        decodeMap = initDecodeMap();
        encodeMap = initEncodeMap();
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new Error(e);
        }
    }
    
    private static final class CalendarFormatter
    {
        public static String doFormat(final String format, final Calendar cal) throws IllegalArgumentException {
            int fidx = 0;
            final int flen = format.length();
            final StringBuilder buf = new StringBuilder();
            while (fidx < flen) {
                final char fch = format.charAt(fidx++);
                if (fch != '%') {
                    buf.append(fch);
                }
                else {
                    switch (format.charAt(fidx++)) {
                        case 'Y': {
                            formatYear(cal, buf);
                            continue;
                        }
                        case 'M': {
                            formatMonth(cal, buf);
                            continue;
                        }
                        case 'D': {
                            formatDays(cal, buf);
                            continue;
                        }
                        case 'h': {
                            formatHours(cal, buf);
                            continue;
                        }
                        case 'm': {
                            formatMinutes(cal, buf);
                            continue;
                        }
                        case 's': {
                            formatSeconds(cal, buf);
                            continue;
                        }
                        case 'z': {
                            formatTimeZone(cal, buf);
                            continue;
                        }
                        default: {
                            throw new InternalError();
                        }
                    }
                }
            }
            return buf.toString();
        }
        
        private static void formatYear(final Calendar cal, final StringBuilder buf) {
            final int year = cal.get(1);
            String s;
            if (year <= 0) {
                s = Integer.toString(1 - year);
            }
            else {
                s = Integer.toString(year);
            }
            while (s.length() < 4) {
                s = '0' + s;
            }
            if (year <= 0) {
                s = '-' + s;
            }
            buf.append(s);
        }
        
        private static void formatMonth(final Calendar cal, final StringBuilder buf) {
            formatTwoDigits(cal.get(2) + 1, buf);
        }
        
        private static void formatDays(final Calendar cal, final StringBuilder buf) {
            formatTwoDigits(cal.get(5), buf);
        }
        
        private static void formatHours(final Calendar cal, final StringBuilder buf) {
            formatTwoDigits(cal.get(11), buf);
        }
        
        private static void formatMinutes(final Calendar cal, final StringBuilder buf) {
            formatTwoDigits(cal.get(12), buf);
        }
        
        private static void formatSeconds(final Calendar cal, final StringBuilder buf) {
            formatTwoDigits(cal.get(13), buf);
            if (cal.isSet(14)) {
                final int n = cal.get(14);
                if (n != 0) {
                    String ms;
                    for (ms = Integer.toString(n); ms.length() < 3; ms = '0' + ms) {}
                    buf.append('.');
                    buf.append(ms);
                }
            }
        }
        
        private static void formatTimeZone(final Calendar cal, final StringBuilder buf) {
            final TimeZone tz = cal.getTimeZone();
            if (tz == null) {
                return;
            }
            int offset;
            if (tz.inDaylightTime(cal.getTime())) {
                offset = tz.getRawOffset() + (tz.useDaylightTime() ? 3600000 : 0);
            }
            else {
                offset = tz.getRawOffset();
            }
            if (offset == 0) {
                buf.append('Z');
                return;
            }
            if (offset >= 0) {
                buf.append('+');
            }
            else {
                buf.append('-');
                offset *= -1;
            }
            offset /= 60000;
            formatTwoDigits(offset / 60, buf);
            buf.append(':');
            formatTwoDigits(offset % 60, buf);
        }
        
        private static void formatTwoDigits(final int n, final StringBuilder buf) {
            if (n < 10) {
                buf.append('0');
            }
            buf.append(n);
        }
    }
}
