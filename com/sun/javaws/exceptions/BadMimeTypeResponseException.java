// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class BadMimeTypeResponseException extends DownloadException
{
    private String _mimeType;
    
    public BadMimeTypeResponseException(final URL url, final String s, final String mimeType) {
        super(url, s);
        this._mimeType = mimeType;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.badmimetyperesponse", this.getResourceString(), this._mimeType);
    }
}
