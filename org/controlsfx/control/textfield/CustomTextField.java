// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.textfield;

import impl.org.controlsfx.skin.CustomTextFieldSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;

public class CustomTextField extends TextField
{
    private ObjectProperty<Node> left;
    private ObjectProperty<Node> right;
    
    public CustomTextField() {
        this.left = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "left");
        this.right = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "right");
        this.getStyleClass().add((Object)"custom-text-field");
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
                return CustomTextField.this.leftProperty();
            }
            
            @Override
            public ObjectProperty<Node> rightProperty() {
                return CustomTextField.this.rightProperty();
            }
        };
    }
    
    public String getUserAgentStylesheet() {
        return CustomTextField.class.getResource("customtextfield.css").toExternalForm();
    }
}
