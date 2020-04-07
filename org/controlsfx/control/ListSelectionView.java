// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.collections.FXCollections;
import impl.org.controlsfx.skin.ListSelectionViewSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.Label;
import impl.org.controlsfx.i18n.Localization;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.geometry.Orientation;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;

public class ListSelectionView<T> extends ControlsFXControl
{
    private static final String DEFAULT_STYLECLASS = "list-selection-view";
    private final ObjectProperty<Node> sourceHeader;
    private final ObjectProperty<Node> sourceFooter;
    private final ObjectProperty<Node> targetHeader;
    private final ObjectProperty<Node> targetFooter;
    private ObjectProperty<ObservableList<T>> sourceItems;
    private ObjectProperty<ObservableList<T>> targetItems;
    private final ObjectProperty<Orientation> orientation;
    private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;
    
    public ListSelectionView() {
        this.sourceHeader = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "sourceHeader");
        this.sourceFooter = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "sourceFooter");
        this.targetHeader = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "targetHeader");
        this.targetFooter = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "targetFooter");
        this.orientation = (ObjectProperty<Orientation>)new SimpleObjectProperty((Object)this, "orientation", (Object)Orientation.HORIZONTAL);
        this.getStyleClass().add((Object)"list-selection-view");
        final Label sourceHeader = new Label(Localization.localize(Localization.asKey("listSelectionView.header.source")));
        sourceHeader.getStyleClass().add((Object)"list-header-label");
        sourceHeader.setId("source-header-label");
        this.setSourceHeader((Node)sourceHeader);
        final Label targetHeader = new Label(Localization.localize(Localization.asKey("listSelectionView.header.target")));
        targetHeader.getStyleClass().add((Object)"list-header-label");
        targetHeader.setId("target-header-label");
        this.setTargetHeader((Node)targetHeader);
    }
    
    protected Skin<ListSelectionView<T>> createDefaultSkin() {
        return (Skin<ListSelectionView<T>>)new ListSelectionViewSkin((ListSelectionView<Object>)this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(ListSelectionView.class, "listselectionview.css");
    }
    
    public final ObjectProperty<Node> sourceHeaderProperty() {
        return this.sourceHeader;
    }
    
    public final Node getSourceHeader() {
        return (Node)this.sourceHeader.get();
    }
    
    public final void setSourceHeader(final Node node) {
        this.sourceHeader.set((Object)node);
    }
    
    public final ObjectProperty<Node> sourceFooterProperty() {
        return this.sourceFooter;
    }
    
    public final Node getSourceFooter() {
        return (Node)this.sourceFooter.get();
    }
    
    public final void setSourceFooter(final Node node) {
        this.sourceFooter.set((Object)node);
    }
    
    public final ObjectProperty<Node> targetHeaderProperty() {
        return this.targetHeader;
    }
    
    public final Node getTargetHeader() {
        return (Node)this.targetHeader.get();
    }
    
    public final void setTargetHeader(final Node node) {
        this.targetHeader.set((Object)node);
    }
    
    public final ObjectProperty<Node> targetFooterProperty() {
        return this.targetFooter;
    }
    
    public final Node getTargetFooter() {
        return (Node)this.targetFooter.get();
    }
    
    public final void setTargetFooter(final Node node) {
        this.targetFooter.set((Object)node);
    }
    
    public final void setSourceItems(final ObservableList<T> value) {
        this.sourceItemsProperty().set((Object)value);
    }
    
    public final ObservableList<T> getSourceItems() {
        return (ObservableList<T>)this.sourceItemsProperty().get();
    }
    
    public final ObjectProperty<ObservableList<T>> sourceItemsProperty() {
        if (this.sourceItems == null) {
            this.sourceItems = (ObjectProperty<ObservableList<T>>)new SimpleObjectProperty((Object)this, "sourceItems", (Object)FXCollections.observableArrayList());
        }
        return this.sourceItems;
    }
    
    public final void setTargetItems(final ObservableList<T> value) {
        this.targetItemsProperty().set((Object)value);
    }
    
    public final ObservableList<T> getTargetItems() {
        return (ObservableList<T>)this.targetItemsProperty().get();
    }
    
    public final ObjectProperty<ObservableList<T>> targetItemsProperty() {
        if (this.targetItems == null) {
            this.targetItems = (ObjectProperty<ObservableList<T>>)new SimpleObjectProperty((Object)this, "targetItems", (Object)FXCollections.observableArrayList());
        }
        return this.targetItems;
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        return this.orientation;
    }
    
    public final void setOrientation(final Orientation value) {
        this.orientationProperty().set((Object)value);
    }
    
    public final Orientation getOrientation() {
        return (Orientation)this.orientation.get();
    }
    
    public final void setCellFactory(final Callback<ListView<T>, ListCell<T>> value) {
        this.cellFactoryProperty().set((Object)value);
    }
    
    public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return (Callback<ListView<T>, ListCell<T>>)((this.cellFactory == null) ? null : ((Callback)this.cellFactory.get()));
    }
    
    public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
        if (this.cellFactory == null) {
            this.cellFactory = (ObjectProperty<Callback<ListView<T>, ListCell<T>>>)new SimpleObjectProperty((Object)this, "cellFactory");
        }
        return this.cellFactory;
    }
}
