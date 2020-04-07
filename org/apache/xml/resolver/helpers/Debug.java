// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.helpers;

import org.apache.xml.resolver.CatalogManager;

public class Debug
{
    protected static int debug;
    
    public static void setDebug(final int debug) {
        Debug.debug = debug;
    }
    
    public static int getDebug() {
        return Debug.debug;
    }
    
    public static void message(final int n, final String s) {
        if (Debug.debug >= n) {
            System.out.println(s);
        }
    }
    
    public static void message(final int n, final String s, final String s2) {
        if (Debug.debug >= n) {
            System.out.println(s + ": " + s2);
        }
    }
    
    public static void message(final int n, final String s, final String s2, final String s3) {
        if (Debug.debug >= n) {
            System.out.println(s + ": " + s2);
            System.out.println("\t" + s3);
        }
    }
    
    static {
        Debug.debug = CatalogManager.verbosity();
    }
}
