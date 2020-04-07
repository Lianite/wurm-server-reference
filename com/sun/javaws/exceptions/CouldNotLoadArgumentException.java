// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class CouldNotLoadArgumentException extends JNLPException
{
    private String _argument;
    
    public CouldNotLoadArgumentException(final String argument, final Exception ex) {
        super(ResourceManager.getString("launch.error.category.arguments"), ex);
        this._argument = argument;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.couldnotloadarg", this._argument);
    }
    
    public String getField() {
        return this.getMessage();
    }
    
    public String toString() {
        return "CouldNotLoadArgumentException[ " + this.getRealMessage() + "]";
    }
}
