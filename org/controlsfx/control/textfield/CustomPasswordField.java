// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.textfield;

import javafx.scene.control.TextField;
import impl.org.controlsfx.skin.CustomTextFieldSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.PasswordField;

public class CustomPasswordField extends PasswordField
{
    private ObjectProperty<Node> left;
    private ObjectProperty<Node> right;
    
    public CustomPasswordField() {
        this.left = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "left");
        this.right = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "right");
        this.getStyleClass().addAll((Object[])new String[] { "custom-text-field", "custom-password-field" });
    }
    
    public final ObjectProperty<Node> leftProperty() {
        return this.left;
    }
    
    public final Node getLeft() {
        return (Node)this.left.get();
    }
    
    public final void setLeft(final Node value) {
        this.left.set((Object)value);
    }
    
    public final ObjectProperty<Node> rightProperty() {
        return this.right;
    }
    
    public final Node getRight() {
        return (Node)this.right.get();
    }
    
    public final void setRight(final Node value) {
        this.right.set((Object)value);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new CustomTextFieldSkin(this) {
            @Override
            public ObjectProperty<Node> leftProperty() {
                return CustomPasswordField.this.leftProperty();
            }
            
            @Override
            public ObjectProperty<Node> rightProperty() {
                return CustomPasswordField.this.rightProperty();
            }
        };
    }
    
    public String getUserAgentStylesheet() {
        return CustomTextField.class.getResource("customtextfield.css").toExternalForm();
    }
}
