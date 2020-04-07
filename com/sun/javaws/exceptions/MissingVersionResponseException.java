// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class MissingVersionResponseException extends DownloadException
{
    public MissingVersionResponseException(final URL url, final String s) {
        super(url, s);
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.missingversionresponse", this.getResourceString());
    }
}
