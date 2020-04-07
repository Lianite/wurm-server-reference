// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.control;

public interface ActionMessage
{
    String getActionNamespace();
    
    boolean isBodyNonEmptyString();
    
    String getBodyString();
    
    void setBody(final String p0);
}
