// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;
import com.sun.javaws.jnl.LaunchDesc;

public class FailedDownloadingResourceException extends DownloadException
{
    public FailedDownloadingResourceException(final LaunchDesc launchDesc, final URL url, final String s, final Exception ex) {
        super(launchDesc, url, s, ex);
    }
    
    public FailedDownloadingResourceException(final URL url, final String s, final Exception ex) {
        this(null, url, s, ex);
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.failedloadingresource", this.getResourceString());
    }
}
