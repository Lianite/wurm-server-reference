// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.android;

import org.flywaydb.core.internal.util.logging.Log;

public class AndroidLog implements Log
{
    private static final String TAG = "Flyway";
    
    @Override
    public void debug(final String message) {
        android.util.Log.d("Flyway", message);
    }
    
    @Override
    public void info(final String message) {
        android.util.Log.i("Flyway", message);
    }
    
    @Override
    public void warn(final String message) {
        android.util.Log.w("Flyway", message);
    }
    
    @Override
    public void error(final String message) {
        android.util.Log.e("Flyway", message);
    }
    
    @Override
    public void error(final String message, final Exception e) {
        android.util.Log.e("Flyway", message, (Throwable)e);
    }
}
