// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.io;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Random;

public class MD5Crypt
{
    private static final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    private static final String to64(long v, int size) {
        final StringBuffer result = new StringBuffer();
        while (--size >= 0) {
            result.append("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt((int)(v & 0x3FL)));
            v >>>= 6;
        }
        return result.toString();
    }
    
    private static final void clearbits(final byte[] bits) {
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = 0;
        }
    }
    
    private static final int bytes2u(final byte inp) {
        return inp & 0xFF;
    }
    
    public static final String crypt(final String password) {
        final StringBuffer salt = new StringBuffer();
        final Random rnd = new Random();
        while (salt.length() < 8) {
            final int index = (int)(rnd.nextFloat() * "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".length());
            salt.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".substring(index, index + 1));
        }
        return crypt(password, salt.toString(), "$1$");
    }
    
    public static final String crypt(final String password, final String salt) {
        return crypt(password, salt, "$1$");
    }
    
    public static final String crypt(final String password, String salt, final String magic) {
        MessageDigest ctx;
        MessageDigest ctx2;
        try {
            ctx = MessageDigest.getInstance("md5");
            ctx2 = MessageDigest.getInstance("md5");
        }
        catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
            return null;
        }
        if (salt.startsWith(magic)) {
            salt = salt.substring(magic.length());
        }
        if (salt.indexOf(36) != -1) {
            salt = salt.substring(0, salt.indexOf(36));
        }
        if (salt.length() > 8) {
            salt = salt.substring(0, 8);
        }
        ctx.update(password.getBytes());
        ctx.update(magic.getBytes());
        ctx.update(salt.getBytes());
        ctx2.update(password.getBytes());
        ctx2.update(salt.getBytes());
        ctx2.update(password.getBytes());
        byte[] finalState = ctx2.digest();
        for (int pl = password.length(); pl > 0; pl -= 16) {
            ctx.update(finalState, 0, (pl > 16) ? 16 : pl);
        }
        clearbits(finalState);
        for (int i = password.length(); i != 0; i >>>= 1) {
            if ((i & 0x1) != 0x0) {
                ctx.update(finalState, 0, 1);
            }
            else {
                ctx.update(password.getBytes(), 0, 1);
            }
        }
        finalState = ctx.digest();
        for (int i = 0; i < 1000; ++i) {
            try {
                ctx2 = MessageDigest.getInstance("md5");
            }
            catch (NoSuchAlgorithmException e0) {
                return null;
            }
            if ((i & 0x1) != 0x0) {
                ctx2.update(password.getBytes());
            }
            else {
                ctx2.update(finalState, 0, 16);
            }
            if (i % 3 != 0) {
                ctx2.update(salt.getBytes());
            }
            if (i % 7 != 0) {
                ctx2.update(password.getBytes());
            }
            if ((i & 0x1) != 0x0) {
                ctx2.update(finalState, 0, 16);
            }
            else {
                ctx2.update(password.getBytes());
            }
            finalState = ctx2.digest();
        }
        final StringBuffer result = new StringBuffer();
        result.append(magic);
        result.append(salt);
        result.append("$");
        long l = bytes2u(finalState[0]) << 16 | bytes2u(finalState[6]) << 8 | bytes2u(finalState[12]);
        result.append(to64(l, 4));
        l = (bytes2u(finalState[1]) << 16 | bytes2u(finalState[7]) << 8 | bytes2u(finalState[13]));
        result.append(to64(l, 4));
        l = (bytes2u(finalState[2]) << 16 | bytes2u(finalState[8]) << 8 | bytes2u(finalState[14]));
        result.append(to64(l, 4));
        l = (bytes2u(finalState[3]) << 16 | bytes2u(finalState[9]) << 8 | bytes2u(finalState[15]));
        result.append(to64(l, 4));
        l = (bytes2u(finalState[4]) << 16 | bytes2u(finalState[10]) << 8 | bytes2u(finalState[5]));
        result.append(to64(l, 4));
        l = bytes2u(finalState[11]);
        result.append(to64(l, 2));
        clearbits(finalState);
        return result.toString();
    }
    
    public static boolean isEqual(final String clear, final String encrypted) {
        return isEqual(clear.toCharArray(), encrypted);
    }
    
    public static boolean isEqual(final char[] clear, final String encrypted) {
        final String[] split = encrypted.split("\\$");
        if (split.length != 4) {
            return false;
        }
        final char[] a = encrypted.toCharArray();
        final char[] b = crypt(new String(clear), split[2], "$" + split[1] + "$").toCharArray();
        boolean result = false;
        if (a == null || b == null) {
            return a == b;
        }
        if (a.length == b.length) {
            boolean equals = true;
            for (int i = 0; i < a.length && equals; equals = (a[i] == b[i]), ++i) {}
            result = equals;
        }
        return result;
    }
}
