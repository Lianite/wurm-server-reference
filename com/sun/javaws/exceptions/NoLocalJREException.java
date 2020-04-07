// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;

public class NoLocalJREException extends JNLPException
{
    private String _message;
    
    public NoLocalJREException(final LaunchDesc launchDesc, final String s, final boolean b) {
        super(ResourceManager.getString("launch.error.category.config"), launchDesc);
        if (b) {
            this._message = ResourceManager.getString("launch.error.wont.download.jre", s);
        }
        else {
            this._message = ResourceManager.getString("launch.error.cant.download.jre", s);
        }
    }
    
    public String getRealMessage() {
        return this._message;
    }
}
