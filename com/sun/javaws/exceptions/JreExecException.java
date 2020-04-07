// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class JreExecException extends JNLPException
{
    private String _version;
    
    public JreExecException(final String version, final Exception ex) {
        super(ResourceManager.getString("launch.error.category.unexpected"), ex);
        this._version = version;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.failedexec", this._version);
    }
    
    public String toString() {
        return "JreExecException[ " + this.getMessage() + "]";
    }
}
