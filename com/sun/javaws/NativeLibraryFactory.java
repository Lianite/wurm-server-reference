// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

public class NativeLibraryFactory
{
    public static NativeLibrary newInstance() {
        return new WinNativeLibrary();
    }
}
