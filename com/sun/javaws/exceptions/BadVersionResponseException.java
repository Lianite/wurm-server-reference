// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class BadVersionResponseException extends DownloadException
{
    private String _responseVersionID;
    
    public BadVersionResponseException(final URL url, final String s, final String responseVersionID) {
        super(url, s);
        this._responseVersionID = responseVersionID;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.badversionresponse", this.getResourceString(), this._responseVersionID);
    }
}
