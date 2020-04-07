// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.i18n;

import javafx.beans.property.SimpleStringProperty;

public class SimpleLocalizedStringProperty extends SimpleStringProperty
{
    public SimpleLocalizedStringProperty() {
    }
    
    public SimpleLocalizedStringProperty(final String initialValue) {
        super(initialValue);
    }
    
    public SimpleLocalizedStringProperty(final Object bean, final String name) {
        super(bean, name);
    }
    
    public SimpleLocalizedStringProperty(final Object bean, final String name, final String initialValue) {
        super(bean, name, initialValue);
    }
    
    public String getValue() {
        return Localization.localize(super.getValue());
    }
}
