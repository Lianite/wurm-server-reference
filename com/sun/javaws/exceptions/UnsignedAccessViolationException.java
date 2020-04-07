// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public class UnsignedAccessViolationException extends JNLPException
{
    URL _url;
    boolean _initial;
    
    public UnsignedAccessViolationException(final LaunchDesc launchDesc, final URL url, final boolean initial) {
        super(ResourceManager.getString("launch.error.category.security"), launchDesc);
        this._url = url;
        this._initial = initial;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.unsignedAccessViolation") + "\n" + ResourceManager.getString("launch.error.unsignedResource", this._url.toString());
    }
    
    public String getBriefMessage() {
        if (this._initial) {
            return null;
        }
        return ResourceManager.getString("launcherrordialog.brief.continue");
    }
}
