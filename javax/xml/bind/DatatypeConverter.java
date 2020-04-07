// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

import java.util.Calendar;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class DatatypeConverter
{
    private static DatatypeConverterInterface theConverter;
    
    public static void setDatatypeConverter(final DatatypeConverterInterface converter) {
        if (converter == null) {
            throw new IllegalArgumentException(Messages.format("DatatypeConverter.ConverterMustNotBeNull"));
        }
        if (DatatypeConverter.theConverter == null) {
            DatatypeConverter.theConverter = converter;
        }
    }
    
    public static String parseString(final String lexicalXSDString) {
        return DatatypeConverter.theConverter.parseString(lexicalXSDString);
    }
    
    public static BigInteger parseInteger(final String lexicalXSDInteger) {
        return DatatypeConverter.theConverter.parseInteger(lexicalXSDInteger);
    }
    
    public static int parseInt(final String lexicalXSDInt) {
        return DatatypeConverter.theConverter.parseInt(lexicalXSDInt);
    }
    
    public static long parseLong(final String lexicalXSDLong) {
        return DatatypeConverter.theConverter.parseLong(lexicalXSDLong);
    }
    
    public static short parseShort(final String lexicalXSDShort) {
        return DatatypeConverter.theConverter.parseShort(lexicalXSDShort);
    }
    
    public static BigDecimal parseDecimal(final String lexicalXSDDecimal) {
        return DatatypeConverter.theConverter.parseDecimal(lexicalXSDDecimal);
    }
    
    public static float parseFloat(final String lexicalXSDFloat) {
        return DatatypeConverter.theConverter.parseFloat(lexicalXSDFloat);
    }
    
    public static double parseDouble(final String lexicalXSDDouble) {
        return DatatypeConverter.theConverter.parseDouble(lexicalXSDDouble);
    }
    
    public static boolean parseBoolean(final String lexicalXSDBoolean) {
        return DatatypeConverter.theConverter.parseBoolean(lexicalXSDBoolean);
    }
    
    public static byte parseByte(final String lexicalXSDByte) {
        return DatatypeConverter.theConverter.parseByte(lexicalXSDByte);
    }
    
    public static QName parseQName(final String lexicalXSDQName, final NamespaceContext nsc) {
        return DatatypeConverter.theConverter.parseQName(lexicalXSDQName, nsc);
    }
    
    public static Calendar parseDateTime(final String lexicalXSDDateTime) {
        return DatatypeConverter.theConverter.parseDateTime(lexicalXSDDateTime);
    }
    
    public static byte[] parseBase64Binary(final String lexicalXSDBase64Binary) {
        return DatatypeConverter.theConverter.parseBase64Binary(lexicalXSDBase64Binary);
    }
    
    public static byte[] parseHexBinary(final String lexicalXSDHexBinary) {
        return DatatypeConverter.theConverter.parseHexBinary(lexicalXSDHexBinary);
    }
    
    public static long parseUnsignedInt(final String lexicalXSDUnsignedInt) {
        return DatatypeConverter.theConverter.parseUnsignedInt(lexicalXSDUnsignedInt);
    }
    
    public static int parseUnsignedShort(final String lexicalXSDUnsignedShort) {
        return DatatypeConverter.theConverter.parseUnsignedShort(lexicalXSDUnsignedShort);
    }
    
    public static Calendar parseTime(final String lexicalXSDTime) {
        return DatatypeConverter.theConverter.parseTime(lexicalXSDTime);
    }
    
    public static Calendar parseDate(final String lexicalXSDDate) {
        return DatatypeConverter.theConverter.parseDate(lexicalXSDDate);
    }
    
    public static String parseAnySimpleType(final String lexicalXSDAnySimpleType) {
        return DatatypeConverter.theConverter.parseAnySimpleType(lexicalXSDAnySimpleType);
    }
    
    public static String printString(final String val) {
        return DatatypeConverter.theConverter.printString(val);
    }
    
    public static String printInteger(final BigInteger val) {
        return DatatypeConverter.theConverter.printInteger(val);
    }
    
    public static String printInt(final int val) {
        return DatatypeConverter.theConverter.printInt(val);
    }
    
    public static String printLong(final long val) {
        return DatatypeConverter.theConverter.printLong(val);
    }
    
    public static String printShort(final short val) {
        return DatatypeConverter.theConverter.printShort(val);
    }
    
    public static String printDecimal(final BigDecimal val) {
        return DatatypeConverter.theConverter.printDecimal(val);
    }
    
    public static String printFloat(final float val) {
        return DatatypeConverter.theConverter.printFloat(val);
    }
    
    public static String printDouble(final double val) {
        return DatatypeConverter.theConverter.printDouble(val);
    }
    
    public static String printBoolean(final boolean val) {
        return DatatypeConverter.theConverter.printBoolean(val);
    }
    
    public static String printByte(final byte val) {
        return DatatypeConverter.theConverter.printByte(val);
    }
    
    public static String printQName(final QName val, final NamespaceContext nsc) {
        return DatatypeConverter.theConverter.printQName(val, nsc);
    }
    
    public static String printDateTime(final Calendar val) {
        return DatatypeConverter.theConverter.printDateTime(val);
    }
    
    public static String printBase64Binary(final byte[] val) {
        return DatatypeConverter.theConverter.printBase64Binary(val);
    }
    
    public static String printHexBinary(final byte[] val) {
        return DatatypeConverter.theConverter.printHexBinary(val);
    }
    
    public static String printUnsignedInt(final long val) {
        return DatatypeConverter.theConverter.printUnsignedInt(val);
    }
    
    public static String printUnsignedShort(final int val) {
        return DatatypeConverter.theConverter.printUnsignedShort(val);
    }
    
    public static String printTime(final Calendar val) {
        return DatatypeConverter.theConverter.printTime(val);
    }
    
    public static String printDate(final Calendar val) {
        return DatatypeConverter.theConverter.printDate(val);
    }
    
    public static String printAnySimpleType(final String val) {
        return DatatypeConverter.theConverter.printAnySimpleType(val);
    }
    
    static {
        DatatypeConverter.theConverter = new DatatypeConverterImpl();
    }
}
