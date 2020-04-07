// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class TooManyArgumentsException extends JNLPException
{
    private String[] _arguments;
    
    public TooManyArgumentsException(final String[] arguments) {
        super(ResourceManager.getString("launch.error.category.arguments"));
        this._arguments = arguments;
    }
    
    public String getRealMessage() {
        final StringBuffer sb = new StringBuffer("{");
        for (int i = 0; i < this._arguments.length - 1; ++i) {
            sb.append(this._arguments[i]);
            sb.append(", ");
        }
        sb.append(this._arguments[this._arguments.length - 1]);
        sb.append(" }");
        return ResourceManager.getString("launch.error.toomanyargs", sb.toString());
    }
    
    public String getField() {
        return this.getMessage();
    }
    
    public String toString() {
        return "TooManyArgumentsException[ " + this.getRealMessage() + "]";
    }
}
