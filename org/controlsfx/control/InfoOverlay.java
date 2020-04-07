// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.InfoOverlaySkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;

public class InfoOverlay extends ControlsFXControl
{
    private ObjectProperty<Node> content;
    private StringProperty text;
    private BooleanProperty showOnHover;
    private static final String DEFAULT_STYLE_CLASS = "info-overlay";
    
    public InfoOverlay() {
        this((Node)null, null);
    }
    
    public InfoOverlay(final String imageUrl, final String text) {
        this((Node)new ImageView(imageUrl), text);
    }
    
    public InfoOverlay(final Node content, final String text) {
        this.content = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "content");
        this.text = (StringProperty)new SimpleStringProperty((Object)this, "text");
        this.showOnHover = (BooleanProperty)new SimpleBooleanProperty((Object)this, "showOnHover", true);
        this.getStyleClass().setAll((Object[])new String[] { "info-overlay" });
        this.setContent(content);
        this.setText(text);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new InfoOverlaySkin(this);
    }
    
    public final ObjectProperty<Node> contentProperty() {
        return this.content;
    }
    
    public final void setContent(final Node content) {
        this.contentProperty().set((Object)content);
    }
    
    public final Node getContent() {
        return (Node)this.contentProperty().get();
    }
    
    public final StringProperty textProperty() {
        return this.text;
    }
    
    public final String getText() {
        return (String)this.textProperty().get();
    }
    
    public final void setText(final String text) {
        this.textProperty().set((Object)text);
    }
    
    public final BooleanProperty showOnHoverProperty() {
        return this.showOnHover;
    }
    
    public final boolean isShowOnHover() {
        return this.showOnHoverProperty().get();
    }
    
    public final void setShowOnHover(final boolean value) {
        this.showOnHoverProperty().set(value);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(InfoOverlay.class, "info-overlay.css");
    }
}
