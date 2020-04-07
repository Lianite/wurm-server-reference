// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation.decoration;

import java.util.Collections;
import javafx.scene.control.Control;
import java.util.Arrays;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.validation.Severity;
import org.controlsfx.control.decoration.Decoration;
import java.util.Collection;
import org.controlsfx.validation.ValidationMessage;

public class StyleClassValidationDecoration extends AbstractValidationDecoration
{
    private final String errorClass;
    private final String warningClass;
    
    public StyleClassValidationDecoration() {
        this(null, null);
    }
    
    public StyleClassValidationDecoration(final String errorClass, final String warningClass) {
        this.errorClass = ((errorClass != null) ? errorClass : "error");
        this.warningClass = ((warningClass != null) ? warningClass : "warning");
    }
    
    @Override
    protected Collection<Decoration> createValidationDecorations(final ValidationMessage message) {
        return Arrays.asList(new StyleClassDecoration(new String[] { (Severity.ERROR == message.getSeverity()) ? this.errorClass : this.warningClass }));
    }
    
    @Override
    protected Collection<Decoration> createRequiredDecorations(final Control target) {
        return (Collection<Decoration>)Collections.emptyList();
    }
}
