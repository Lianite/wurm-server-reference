// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.util;

import java.io.File;

public final class ToolsJarNotFoundException extends Exception
{
    public final File toolsJar;
    
    public ToolsJarNotFoundException(final File toolsJar) {
        super(calcMessage(toolsJar));
        this.toolsJar = toolsJar;
    }
    
    private static String calcMessage(final File toolsJar) {
        return Messages.TOOLS_JAR_NOT_FOUND.format(toolsJar.getPath());
    }
}
