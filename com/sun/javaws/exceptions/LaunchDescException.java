// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;

public class LaunchDescException extends JNLPException
{
    private String _message;
    private boolean _isSignedLaunchDesc;
    
    public LaunchDescException() {
        this((Exception)null);
    }
    
    public LaunchDescException(final Exception ex) {
        this(null, ex);
    }
    
    public void setIsSignedLaunchDesc() {
        this._isSignedLaunchDesc = true;
    }
    
    public boolean isSignedLaunchDesc() {
        return this._isSignedLaunchDesc;
    }
    
    public LaunchDescException(final LaunchDesc launchDesc, final Exception ex) {
        super(ResourceManager.getString("launch.error.category.launchdesc"), launchDesc, ex);
    }
    
    public LaunchDescException(final LaunchDesc launchDesc, final String message, final Exception ex) {
        this(launchDesc, ex);
        this._message = message;
    }
    
    public String getRealMessage() {
        return this._message;
    }
}
