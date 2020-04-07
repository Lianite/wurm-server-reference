// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import java.util.logging.Logger;

public abstract class Util
{
    public static Logger getClassLogger() {
        try {
            final StackTraceElement[] trace = new Exception().getStackTrace();
            return Logger.getLogger(trace[1].getClassName());
        }
        catch (SecurityException _) {
            return Logger.getLogger("com.sun.xml.bind");
        }
    }
    
    public static String getSystemProperty(final String name) {
        try {
            return System.getProperty(name);
        }
        catch (SecurityException e) {
            return null;
        }
    }
}
