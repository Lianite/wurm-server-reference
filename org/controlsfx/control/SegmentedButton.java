// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.SegmentedButtonSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ToggleGroup;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ToggleButton;
import javafx.collections.ObservableList;

public class SegmentedButton extends ControlsFXControl
{
    public static final String STYLE_CLASS_DARK = "dark";
    private final ObservableList<ToggleButton> buttons;
    private final ObjectProperty<ToggleGroup> toggleGroup;
    
    public SegmentedButton() {
        this((ObservableList<ToggleButton>)null);
    }
    
    public SegmentedButton(final ToggleButton... buttons) {
        this((ObservableList<ToggleButton>)((buttons == null) ? FXCollections.observableArrayList() : FXCollections.observableArrayList((Object[])buttons)));
    }
    
    public SegmentedButton(final ObservableList<ToggleButton> buttons) {
        this.toggleGroup = (ObjectProperty<ToggleGroup>)new SimpleObjectProperty((Object)new ToggleGroup());
        this.getStyleClass().add((Object)"segmented-button");
        this.buttons = (ObservableList<ToggleButton>)((buttons == null) ? FXCollections.observableArrayList() : buttons);
        this.setFocusTraversable(false);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new SegmentedButtonSkin(this);
    }
    
    public final ObservableList<ToggleButton> getButtons() {
        return this.buttons;
    }
    
    public ObjectProperty<ToggleGroup> toggleGroupProperty() {
        return this.toggleGroup;
    }
    
    public ToggleGroup getToggleGroup() {
        return (ToggleGroup)this.toggleGroupProperty().getValue();
    }
    
    public void setToggleGroup(final ToggleGroup toggleGroup) {
        this.toggleGroupProperty().setValue((Object)toggleGroup);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(SegmentedButton.class, "segmentedbutton.css");
    }
}
