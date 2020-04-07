// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class JNLParseException extends LaunchDescException
{
    private String _msg;
    private int _line;
    private String _launchDescSource;
    
    public JNLParseException(final String launchDescSource, final Exception ex, final String msg, final int line) {
        super(ex);
        this._msg = msg;
        this._line = line;
        this._launchDescSource = launchDescSource;
    }
    
    public int getLine() {
        return this._line;
    }
    
    public String getRealMessage() {
        if (!this.isSignedLaunchDesc()) {
            return ResourceManager.getString("launch.error.parse", this._line);
        }
        return ResourceManager.getString("launch.error.parse-signedjnlp", this._line);
    }
    
    public String getLaunchDescSource() {
        return this._launchDescSource;
    }
    
    public String toString() {
        return "JNLParseException[ " + this.getMessage() + "]";
    }
}
