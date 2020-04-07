// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import javafx.scene.Node;

public abstract class AbstractPropertyEditor<T, C extends Node> implements PropertyEditor<T>
{
    private final PropertySheet.Item property;
    private final C control;
    private boolean suspendUpdate;
    
    public AbstractPropertyEditor(final PropertySheet.Item property, final C control) {
        this(property, control, !property.isEditable());
    }
    
    public AbstractPropertyEditor(final PropertySheet.Item property, final C control, final boolean readonly) {
        this.control = control;
        this.property = property;
        if (!readonly) {
            this.getObservableValue().addListener((o, oldValue, newValue) -> {
                if (!this.suspendUpdate) {
                    this.suspendUpdate = true;
                    this.property.setValue(this.getValue());
                    this.suspendUpdate = false;
                }
            });
            if (property.getObservableValue().isPresent()) {
                property.getObservableValue().get().addListener((o, oldValue, newValue) -> {
                    if (!this.suspendUpdate) {
                        this.suspendUpdate = true;
                        this.setValue((T)property.getValue());
                        this.suspendUpdate = false;
                    }
                });
            }
        }
    }
    
    protected abstract ObservableValue<T> getObservableValue();
    
    public final PropertySheet.Item getProperty() {
        return this.property;
    }
    
    @Override
    public C getEditor() {
        return this.control;
    }
    
    @Override
    public T getValue() {
        return (T)this.getObservableValue().getValue();
    }
}
