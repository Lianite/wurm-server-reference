// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation.decoration;

import java.util.Collections;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.validation.ValidationMessage;
import javafx.scene.control.Control;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public class CompoundValidationDecoration extends AbstractValidationDecoration
{
    private final Set<ValidationDecoration> decorators;
    
    public CompoundValidationDecoration(final Collection<ValidationDecoration> decorators) {
        (this.decorators = new HashSet<ValidationDecoration>()).addAll(decorators);
    }
    
    public CompoundValidationDecoration(final ValidationDecoration... decorators) {
        this(Arrays.asList(decorators));
    }
    
    @Override
    public void applyRequiredDecoration(final Control target) {
        this.decorators.stream().forEach(d -> d.applyRequiredDecoration(target));
    }
    
    @Override
    public void applyValidationDecoration(final ValidationMessage message) {
        this.decorators.stream().forEach(d -> d.applyValidationDecoration(message));
    }
    
    @Override
    protected Collection<Decoration> createValidationDecorations(final ValidationMessage message) {
        return (Collection<Decoration>)Collections.emptyList();
    }
    
    @Override
    protected Collection<Decoration> createRequiredDecorations(final Control target) {
        return (Collection<Decoration>)Collections.emptyList();
    }
}
