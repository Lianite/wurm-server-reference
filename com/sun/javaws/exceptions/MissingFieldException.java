// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class MissingFieldException extends LaunchDescException
{
    private String _field;
    private String _launchDescSource;
    
    public MissingFieldException(final String launchDescSource, final String field) {
        this._field = field;
        this._launchDescSource = launchDescSource;
    }
    
    public String getRealMessage() {
        if (!this.isSignedLaunchDesc()) {
            return ResourceManager.getString("launch.error.missingfield", this._field);
        }
        return ResourceManager.getString("launch.error.missingfield-signedjnlp", this._field);
    }
    
    public String getField() {
        return this.getMessage();
    }
    
    public String getLaunchDescSource() {
        return this._launchDescSource;
    }
    
    public String toString() {
        return "MissingFieldException[ " + this.getField() + "]";
    }
}
