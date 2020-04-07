// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

public class AWTExceptionHandler
{
    public void handle(final Throwable ex) {
        System.err.println("============= The application encountered an unrecoverable error, exiting... =============");
        ex.printStackTrace(System.err);
        System.err.println("==========================================================================================");
        System.exit(1);
    }
}
