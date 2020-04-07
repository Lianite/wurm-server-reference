// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

public class OS
{
    public static boolean checkForLinux() {
        return checkForPresence("os.name", "linux");
    }
    
    public static boolean checkForHp() {
        return checkForPresence("os.name", "hp");
    }
    
    public static boolean checkForSolaris() {
        return checkForPresence("os.name", "sun");
    }
    
    public static boolean checkForWindows() {
        return checkForPresence("os.name", "win");
    }
    
    public static boolean checkForMac() {
        return checkForPresence("os.name", "mac");
    }
    
    private static boolean checkForPresence(final String key, final String value) {
        try {
            final String tmp = System.getProperty(key);
            return tmp != null && tmp.trim().toLowerCase().startsWith(value);
        }
        catch (Throwable t) {
            return false;
        }
    }
}
