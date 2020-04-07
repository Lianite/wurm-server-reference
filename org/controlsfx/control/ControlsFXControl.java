// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.version.VersionChecker;
import javafx.scene.control.Control;

abstract class ControlsFXControl extends Control
{
    private String stylesheet;
    
    public ControlsFXControl() {
        VersionChecker.doVersionCheck();
    }
    
    protected final String getUserAgentStylesheet(final Class<?> clazz, final String fileName) {
        if (this.stylesheet == null) {
            this.stylesheet = clazz.getResource(fileName).toExternalForm();
        }
        return this.stylesheet;
    }
}
