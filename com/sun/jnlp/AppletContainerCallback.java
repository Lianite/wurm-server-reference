// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.awt.Dimension;
import java.net.URL;

public interface AppletContainerCallback
{
    void showDocument(final URL p0);
    
    void relativeResize(final Dimension p0);
}
