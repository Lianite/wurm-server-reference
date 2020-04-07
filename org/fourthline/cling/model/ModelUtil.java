// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Collections;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;

public class ModelUtil
{
    public static final boolean ANDROID_RUNTIME;
    public static final boolean ANDROID_EMULATOR;
    
    public static boolean isStringConvertibleType(final Set<Class> stringConvertibleTypes, final Class clazz) {
        if (clazz.isEnum()) {
            return true;
        }
        for (final Class toStringOutputType : stringConvertibleTypes) {
            if (toStringOutputType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidUDAName(final String name) {
        if (ModelUtil.ANDROID_RUNTIME) {
            return name != null && name.length() != 0;
        }
        return name != null && name.length() != 0 && !name.toLowerCase(Locale.ROOT).startsWith("xml") && name.matches("[a-zA-Z0-9^-_\\p{L}\\p{N}]{1}[a-zA-Z0-9^-_\\.\\\\p{L}\\\\p{N}\\p{Mc}\\p{Sk}]*");
    }
    
    public static InetAddress getInetAddressByName(final String name) {
        try {
            return InetAddress.getByName(name);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String toCommaSeparatedList(final Object[] o) {
        return toCommaSeparatedList(o, true, false);
    }
    
    public static String toCommaSeparatedList(final Object[] o, final boolean escapeCommas, final boolean escapeDoubleQuotes) {
        if (o == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final Object obj : o) {
            String objString = obj.toString();
            objString = objString.replaceAll("\\\\", "\\\\\\\\");
            if (escapeCommas) {
                objString = objString.replaceAll(",", "\\\\,");
            }
            if (escapeDoubleQuotes) {
                objString = objString.replaceAll("\"", "\\\"");
            }
            sb.append(objString).append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static String[] fromCommaSeparatedList(final String s) {
        return fromCommaSeparatedList(s, true);
    }
    
    public static String[] fromCommaSeparatedList(String s, final boolean unescapeCommas) {
        if (s == null || s.length() == 0) {
            return null;
        }
        final String QUOTED_COMMA_PLACEHOLDER = "XXX1122334455XXX";
        if (unescapeCommas) {
            s = s.replaceAll("\\\\,", "XXX1122334455XXX");
        }
        final String[] split = s.split(",");
        for (int i = 0; i < split.length; ++i) {
            split[i] = split[i].replaceAll("XXX1122334455XXX", ",");
            split[i] = split[i].replaceAll("\\\\\\\\", "\\\\");
        }
        return split;
    }
    
    public static String toTimeString(final long seconds) {
        final long hours = seconds / 3600L;
        final long remainder = seconds % 3600L;
        final long minutes = remainder / 60L;
        final long secs = remainder % 60L;
        return ((hours < 10L) ? "0" : "") + hours + ":" + ((minutes < 10L) ? "0" : "") + minutes + ":" + ((secs < 10L) ? "0" : "") + secs;
    }
    
    public static long fromTimeString(String s) {
        if (s.lastIndexOf(".") != -1) {
            s = s.substring(0, s.lastIndexOf("."));
        }
        final String[] split = s.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException("Can't parse time string: " + s);
        }
        return Long.parseLong(split[0]) * 3600L + Long.parseLong(split[1]) * 60L + Long.parseLong(split[2]);
    }
    
    public static String commaToNewline(final String s) {
        final StringBuilder sb = new StringBuilder();
        final String[] split2;
        final String[] split = split2 = s.split(",");
        for (final String splitString : split2) {
            sb.append(splitString).append(",").append("\n");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }
    
    public static String getLocalHostName(final boolean includeDomain) {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            return includeDomain ? hostname : ((hostname.indexOf(".") != -1) ? hostname.substring(0, hostname.indexOf(".")) : hostname);
        }
        catch (Exception ex) {
            return "UNKNOWN HOST";
        }
    }
    
    public static byte[] getFirstNetworkInterfaceHardwareAddress() {
        try {
            final Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for (final NetworkInterface iface : Collections.list(interfaceEnumeration)) {
                if (!iface.isLoopback() && iface.isUp() && iface.getHardwareAddress() != null) {
                    return iface.getHardwareAddress();
                }
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not discover first network interface hardware address");
        }
        throw new RuntimeException("Could not discover first network interface hardware address");
    }
    
    static {
        boolean foundAndroid = false;
        try {
            final Class androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            foundAndroid = (androidBuild.getField("ID").get(null) != null);
        }
        catch (Exception ex) {}
        ANDROID_RUNTIME = foundAndroid;
        boolean foundEmulator = false;
        try {
            final Class androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            final String product = (String)androidBuild.getField("PRODUCT").get(null);
            if ("google_sdk".equals(product) || "sdk".equals(product)) {
                foundEmulator = true;
            }
        }
        catch (Exception ex2) {}
        ANDROID_EMULATOR = foundEmulator;
    }
}
