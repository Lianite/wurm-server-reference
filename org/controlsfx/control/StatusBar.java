// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.StatusBarSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import impl.org.controlsfx.i18n.Localization;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class StatusBar extends ControlsFXControl
{
    private final StringProperty text;
    private final ObjectProperty<Node> graphic;
    private final StringProperty styleTextProperty;
    private final ObservableList<Node> leftItems;
    private final ObservableList<Node> rightItems;
    private final DoubleProperty progress;
    
    public StatusBar() {
        this.text = (StringProperty)new SimpleStringProperty((Object)this, "text", Localization.localize(Localization.asKey("statusbar.ok")));
        this.graphic = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "graphic");
        this.styleTextProperty = (StringProperty)new SimpleStringProperty();
        this.leftItems = (ObservableList<Node>)FXCollections.observableArrayList();
        this.rightItems = (ObservableList<Node>)FXCollections.observableArrayList();
        this.progress = (DoubleProperty)new SimpleDoubleProperty((Object)this, "progress");
        this.getStyleClass().add((Object)"status-bar");
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new StatusBarSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(StatusBar.class, "statusbar.css");
    }
    
    public final StringProperty textProperty() {
        return this.text;
    }
    
    public final void setText(final String text) {
        this.textProperty().set((Object)text);
    }
    
    public final String getText() {
        return (String)this.textProperty().get();
    }
    
    public final ObjectProperty<Node> graphicProperty() {
        return this.graphic;
    }
    
    public final Node getGraphic() {
        return (Node)this.graphicProperty().get();
    }
    
    public final void setGraphic(final Node node) {
        this.graphicProperty().set((Object)node);
    }
    
    public void setStyleText(final String style) {
        this.styleTextProperty.set((Object)style);
    }
    
    public String getStyleText() {
        return (String)this.styleTextProperty.get();
    }
    
    public final StringProperty styleTextProperty() {
        return this.styleTextProperty;
    }
    
    public final ObservableList<Node> getLeftItems() {
        return this.leftItems;
    }
    
    public final ObservableList<Node> getRightItems() {
        return this.rightItems;
    }
    
    public final DoubleProperty progressProperty() {
        return this.progress;
    }
    
    public final void setProgress(final double progress) {
        this.progressProperty().set(progress);
    }
    
    public final double getProgress() {
        return this.progressProperty().get();
    }
}
