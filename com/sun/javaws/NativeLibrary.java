// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

public abstract class NativeLibrary
{
    private static NativeLibrary nativeLibrary;
    
    public static synchronized NativeLibrary getInstance() {
        if (NativeLibrary.nativeLibrary == null) {
            NativeLibrary.nativeLibrary = NativeLibraryFactory.newInstance();
        }
        return NativeLibrary.nativeLibrary;
    }
    
    public void load() {
    }
}
