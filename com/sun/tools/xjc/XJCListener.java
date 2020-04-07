// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.api.ErrorListener;

public abstract class XJCListener implements ErrorListener
{
    public void generatedFile(final String fileName) {
    }
    
    public void generatedFile(final String fileName, final int current, final int total) {
        this.generatedFile(fileName);
    }
    
    public void message(final String msg) {
    }
    
    public void compiled(final Outline outline) {
    }
    
    public boolean isCanceled() {
        return false;
    }
}
