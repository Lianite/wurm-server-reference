// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class MultipleHostsException extends JNLPException
{
    public MultipleHostsException() {
        super(ResourceManager.getString("launch.error.category.security"));
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.multiplehostsreferences");
    }
}
