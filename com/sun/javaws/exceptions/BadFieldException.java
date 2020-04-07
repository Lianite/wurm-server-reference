// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class BadFieldException extends LaunchDescException
{
    private String _field;
    private String _value;
    private String _launchDescSource;
    
    public BadFieldException(final String launchDescSource, final String field, final String value) {
        this._value = value;
        this._field = field;
        this._launchDescSource = launchDescSource;
    }
    
    public String getField() {
        return this.getMessage();
    }
    
    public String getValue() {
        return this._value;
    }
    
    public String getRealMessage() {
        if (this.getValue().equals("https")) {
            return ResourceManager.getString("launch.error.badfield", this._field, this._value) + "\n" + ResourceManager.getString("launch.error.badfield.https");
        }
        if (!this.isSignedLaunchDesc()) {
            return ResourceManager.getString("launch.error.badfield", this._field, this._value);
        }
        return ResourceManager.getString("launch.error.badfield-signedjnlp", this._field, this._value);
    }
    
    public String getLaunchDescSource() {
        return this._launchDescSource;
    }
    
    public String toString() {
        if (this.getValue().equals("https")) {
            return "BadFieldException[ " + this.getRealMessage() + "]";
        }
        return "BadFieldException[ " + this.getField() + "," + this.getValue() + "]";
    }
}
