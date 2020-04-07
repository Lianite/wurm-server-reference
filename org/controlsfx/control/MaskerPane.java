// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.MaskerPaneSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressIndicator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;

public class MaskerPane extends ControlsFXControl
{
    private final DoubleProperty progress;
    private final ObjectProperty<Node> progressNode;
    private final BooleanProperty progressVisible;
    private final StringProperty text;
    
    public MaskerPane() {
        this.progress = (DoubleProperty)new SimpleDoubleProperty((Object)this, "progress", -1.0);
        this.progressNode = (ObjectProperty<Node>)new SimpleObjectProperty<Node>() {
            {
                final ProgressIndicator node = new ProgressIndicator();
                node.progressProperty().bind((ObservableValue)MaskerPane.this.progress);
                this.setValue((Object)node);
            }
            
            public String getName() {
                return "progressNode";
            }
            
            public Object getBean() {
                return MaskerPane.this;
            }
        };
        this.progressVisible = (BooleanProperty)new SimpleBooleanProperty((Object)this, "progressVisible", true);
        this.text = (StringProperty)new SimpleStringProperty((Object)this, "text", "Please Wait...");
        this.getStyleClass().add((Object)"masker-pane");
    }
    
    public final DoubleProperty progressProperty() {
        return this.progress;
    }
    
    public final double getProgress() {
        return this.progress.get();
    }
    
    public final void setProgress(final double progress) {
        this.progress.set(progress);
    }
    
    public final ObjectProperty<Node> progressNodeProperty() {
        return this.progressNode;
    }
    
    public final Node getProgressNode() {
        return (Node)this.progressNode.get();
    }
    
    public final void setProgressNode(final Node progressNode) {
        this.progressNode.set((Object)progressNode);
    }
    
    public final BooleanProperty progressVisibleProperty() {
        return this.progressVisible;
    }
    
    public final boolean getProgressVisible() {
        return this.progressVisible.get();
    }
    
    public final void setProgressVisible(final boolean progressVisible) {
        this.progressVisible.set(progressVisible);
    }
    
    public final StringProperty textProperty() {
        return this.text;
    }
    
    public final String getText() {
        return (String)this.text.get();
    }
    
    public final void setText(final String text) {
        this.text.set((Object)text);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new MaskerPaneSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(MaskerPane.class, "maskerpane.css");
    }
}
