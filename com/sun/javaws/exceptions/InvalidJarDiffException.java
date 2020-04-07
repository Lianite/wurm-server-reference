// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public class InvalidJarDiffException extends DownloadException
{
    public InvalidJarDiffException(final URL url, final String s, final Exception ex) {
        super(null, url, s, ex);
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.invalidjardiff", this.getResourceString());
    }
}
