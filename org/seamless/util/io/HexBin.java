// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.io;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public final class HexBin
{
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 16;
    private static byte[] hexNumberTable;
    private static byte[] lookUpHexAlphabet;
    
    static boolean isHex(final byte octect) {
        return HexBin.hexNumberTable[octect] != -1;
    }
    
    public static String bytesToString(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        return new String(encode(binaryData));
    }
    
    public static String bytesToString(final byte[] binaryData, final String separator) {
        if (binaryData == null) {
            return null;
        }
        final String s = new String(encode(binaryData));
        final StringBuilder sb = new StringBuilder();
        int i = 1;
        final char[] arr$;
        final char[] chars = arr$ = s.toCharArray();
        for (final char c : arr$) {
            sb.append(c);
            if (i == 2) {
                sb.append(separator);
                i = 1;
            }
            else {
                ++i;
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public static byte[] stringToBytes(final String hexEncoded) {
        return decode(hexEncoded.getBytes());
    }
    
    public static byte[] stringToBytes(final String hexEncoded, final String separator) {
        return decode(hexEncoded.replaceAll(separator, "").getBytes());
    }
    
    public static byte[] encode(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        final int lengthData = binaryData.length;
        final int lengthEncode = lengthData * 2;
        final byte[] encodedData = new byte[lengthEncode];
        for (int i = 0; i < lengthData; ++i) {
            encodedData[i * 2] = HexBin.lookUpHexAlphabet[binaryData[i] >> 4 & 0xF];
            encodedData[i * 2 + 1] = HexBin.lookUpHexAlphabet[binaryData[i] & 0xF];
        }
        return encodedData;
    }
    
    public static byte[] decode(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        final int lengthData = binaryData.length;
        if (lengthData % 2 != 0) {
            return null;
        }
        final int lengthDecode = lengthData / 2;
        final byte[] decodedData = new byte[lengthDecode];
        for (int i = 0; i < lengthDecode; ++i) {
            if (!isHex(binaryData[i * 2]) || !isHex(binaryData[i * 2 + 1])) {
                return null;
            }
            decodedData[i] = (byte)(HexBin.hexNumberTable[binaryData[i * 2]] << 4 | HexBin.hexNumberTable[binaryData[i * 2 + 1]]);
        }
        return decodedData;
    }
    
    public static String decode(final String binaryData) {
        if (binaryData == null) {
            return null;
        }
        byte[] decoded = null;
        try {
            decoded = decode(binaryData.getBytes("utf-8"));
        }
        catch (UnsupportedEncodingException ex) {}
        return (decoded == null) ? null : new String(decoded);
    }
    
    public static String encode(final String binaryData) {
        if (binaryData == null) {
            return null;
        }
        byte[] encoded = null;
        try {
            encoded = encode(binaryData.getBytes("utf-8"));
        }
        catch (UnsupportedEncodingException ex) {}
        return (encoded == null) ? null : new String(encoded);
    }
    
    static {
        HexBin.hexNumberTable = new byte[255];
        HexBin.lookUpHexAlphabet = new byte[16];
        for (int i = 0; i < 255; ++i) {
            HexBin.hexNumberTable[i] = -1;
        }
        for (int i = 57; i >= 48; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 48);
        }
        for (int i = 70; i >= 65; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 65 + 10);
        }
        for (int i = 102; i >= 97; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 97 + 10);
        }
        for (int i = 0; i < 10; ++i) {
            HexBin.lookUpHexAlphabet[i] = (byte)(48 + i);
        }
        for (int i = 10; i <= 15; ++i) {
            HexBin.lookUpHexAlphabet[i] = (byte)(65 + i - 10);
        }
    }
}
