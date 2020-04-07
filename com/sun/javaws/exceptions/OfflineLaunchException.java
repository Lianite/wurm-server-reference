// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;

public class OfflineLaunchException extends JNLPException
{
    public OfflineLaunchException() {
        super(ResourceManager.getString("launch.error.category.download"));
    }
    
    public String getRealMessage() {
        return ResourceManager.getString("launch.error.offlinemissingresource");
    }
}
