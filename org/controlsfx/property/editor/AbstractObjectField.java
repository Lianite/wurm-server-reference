// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Priority;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import org.controlsfx.control.textfield.CustomTextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

abstract class AbstractObjectField<T> extends HBox
{
    private static final Image image;
    private final CustomTextField textField;
    private ObjectProperty<T> objectProperty;
    
    public AbstractObjectField() {
        super(1.0);
        this.textField = new CustomTextField();
        this.objectProperty = (ObjectProperty<T>)new SimpleObjectProperty();
        this.textField.setEditable(false);
        this.textField.setFocusTraversable(false);
        final StackPane button = new StackPane(new Node[] { new ImageView(AbstractObjectField.image) });
        button.setCursor(Cursor.DEFAULT);
        button.setOnMouseReleased(e -> {
            if (MouseButton.PRIMARY == e.getButton()) {
                final T result = (T)this.edit(this.objectProperty.get());
                if (result != null) {
                    this.objectProperty.set((Object)result);
                }
            }
        });
        this.textField.setRight((Node)button);
        this.getChildren().add((Object)this.textField);
        HBox.setHgrow((Node)this.textField, Priority.ALWAYS);
        this.objectProperty.addListener((o, oldValue, newValue) -> this.textProperty().set((Object)this.objectToString(newValue)));
    }
    
    protected StringProperty textProperty() {
        return this.textField.textProperty();
    }
    
    public ObjectProperty<T> getObjectProperty() {
        return this.objectProperty;
    }
    
    protected String objectToString(final T object) {
        return (object == null) ? "" : object.toString();
    }
    
    protected abstract Class<T> getType();
    
    protected abstract T edit(final T p0);
    
    static {
        image = new Image(AbstractObjectField.class.getResource("/org/controlsfx/control/open-editor.png").toExternalForm());
    }
}
