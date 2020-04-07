// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.android;

import android.content.Context;

public class ContextHolder
{
    private static Context context;
    
    public static Context getContext() {
        return ContextHolder.context;
    }
    
    public static void setContext(final Context context) {
        ContextHolder.context = context;
    }
}
