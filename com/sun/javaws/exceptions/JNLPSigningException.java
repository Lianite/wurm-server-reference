// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;

public class JNLPSigningException extends LaunchDescException
{
    String _signedSource;
    
    public JNLPSigningException(final LaunchDesc launchDesc, final String signedSource) {
        super(launchDesc, null);
        this._signedSource = null;
        this._signedSource = signedSource;
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.badsignedjnlp");
    }
    
    public String getSignedSource() {
        return this._signedSource;
    }
    
    public String toString() {
        return "JNLPSigningException[" + this.getMessage() + "]";
    }
}
