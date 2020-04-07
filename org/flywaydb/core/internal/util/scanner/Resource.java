// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner;

public interface Resource
{
    String getLocation();
    
    String getLocationOnDisk();
    
    String loadAsString(final String p0);
    
    byte[] loadAsBytes();
    
    String getFilename();
}
