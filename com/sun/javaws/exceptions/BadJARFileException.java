// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public class BadJARFileException extends DownloadException
{
    public BadJARFileException(final URL url, final String s, final Exception ex) {
        super(null, url, s, ex);
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.badjarfile", this.getResourceString());
    }
}
