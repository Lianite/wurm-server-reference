// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property;

import org.controlsfx.property.editor.PropertyEditor;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.control.Alert;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import javafx.beans.value.ObservableValue;
import java.util.Optional;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import org.controlsfx.control.PropertySheet;

public class BeanProperty implements PropertySheet.Item
{
    public static final String CATEGORY_LABEL_KEY = "propertysheet.item.category.label";
    private final Object bean;
    private final PropertyDescriptor beanPropertyDescriptor;
    private final Method readMethod;
    private boolean editable;
    private Optional<ObservableValue<?>> observableValue;
    
    public BeanProperty(final Object bean, final PropertyDescriptor propertyDescriptor) {
        this.editable = true;
        this.observableValue = Optional.empty();
        this.bean = bean;
        this.beanPropertyDescriptor = propertyDescriptor;
        this.readMethod = propertyDescriptor.getReadMethod();
        if (this.beanPropertyDescriptor.getWriteMethod() == null) {
            this.setEditable(false);
        }
        this.findObservableValue();
    }
    
    @Override
    public String getName() {
        return this.beanPropertyDescriptor.getDisplayName();
    }
    
    @Override
    public String getDescription() {
        return this.beanPropertyDescriptor.getShortDescription();
    }
    
    @Override
    public Class<?> getType() {
        return this.beanPropertyDescriptor.getPropertyType();
    }
    
    @Override
    public Object getValue() {
        try {
            return this.readMethod.invoke(this.bean, new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
            final Exception ex;
            final Exception e = ex;
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void setValue(final Object value) {
        final Method writeMethod = this.beanPropertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
            try {
                writeMethod.invoke(this.bean, value);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
                final Exception ex;
                final Exception e = ex;
                e.printStackTrace();
            }
            catch (Throwable e2) {
                if (!(e2 instanceof PropertyVetoException)) {
                    throw e2;
                }
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(Localization.localize(Localization.asKey("bean.property.change.error.title")));
                alert.setHeaderText(Localization.localize(Localization.asKey("bean.property.change.error.masthead")));
                alert.setContentText(e2.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }
    
    @Override
    public String getCategory() {
        String category = (String)this.beanPropertyDescriptor.getValue("propertysheet.item.category.label");
        if (category == null) {
            category = Localization.localize(Localization.asKey(this.beanPropertyDescriptor.isExpert() ? "bean.property.category.expert" : "bean.property.category.basic"));
        }
        return category;
    }
    
    public Object getBean() {
        return this.bean;
    }
    
    public PropertyDescriptor getPropertyDescriptor() {
        return this.beanPropertyDescriptor;
    }
    
    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        if (this.beanPropertyDescriptor.getPropertyEditorClass() != null && PropertyEditor.class.isAssignableFrom(this.beanPropertyDescriptor.getPropertyEditorClass())) {
            return Optional.of(this.beanPropertyDescriptor.getPropertyEditorClass());
        }
        return super.getPropertyEditorClass();
    }
    
    @Override
    public boolean isEditable() {
        return this.editable;
    }
    
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
    
    @Override
    public Optional<ObservableValue<?>> getObservableValue() {
        return this.observableValue;
    }
    
    private void findObservableValue() {
        try {
            final String propName = this.beanPropertyDescriptor.getName() + "Property";
            final Method m = this.getBean().getClass().getMethod(propName, (Class<?>[])new Class[0]);
            final Object val = m.invoke(this.getBean(), new Object[0]);
            if (val != null && val instanceof ObservableValue) {
                this.observableValue = Optional.of(val);
            }
        }
        catch (NoSuchMethodException ex) {}
        catch (SecurityException ex2) {}
        catch (IllegalAccessException ex3) {}
        catch (IllegalArgumentException ex4) {}
        catch (InvocationTargetException ex5) {}
    }
}
