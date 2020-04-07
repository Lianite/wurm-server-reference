// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

public class LocalInstallHandlerFactory
{
    public static LocalInstallHandler newInstance() {
        return new WinInstallHandler();
    }
}
