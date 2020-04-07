// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public class DownloadException extends JNLPException
{
    private URL _location;
    private String _version;
    private String _message;
    
    public DownloadException(final URL url, final String s) {
        this(null, url, s, null);
    }
    
    protected DownloadException(final LaunchDesc launchDesc, final URL location, final String version, final Exception ex) {
        super(ResourceManager.getString("launch.error.category.download"), launchDesc, ex);
        this._location = location;
        this._version = version;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public String getVersion() {
        return this._version;
    }
    
    public String getResourceString() {
        final String string = this._location.toString();
        if (this._version == null) {
            return ResourceManager.getString("launch.error.resourceID", string);
        }
        return ResourceManager.getString("launch.error.resourceID-version", string, this._version);
    }
    
    public String getRealMessage() {
        return this._message;
    }
}
