// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class CacheAccessException extends JNLPException
{
    private String _message;
    
    public CacheAccessException(final boolean b) {
        super(ResourceManager.getString("launch.error.category.config"));
        if (b) {
            this._message = ResourceManager.getString("launch.error.cant.access.system.cache");
        }
        else {
            this._message = ResourceManager.getString("launch.error.cant.access.user.cache");
        }
    }
    
    public String getRealMessage() {
        return this._message;
    }
}
