// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation.decoration;

import org.controlsfx.validation.ValidationMessage;
import javafx.scene.control.Control;

public interface ValidationDecoration
{
    void removeDecorations(final Control p0);
    
    void applyValidationDecoration(final ValidationMessage p0);
    
    void applyRequiredDecoration(final Control p0);
}
