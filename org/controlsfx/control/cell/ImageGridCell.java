// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.cell;

import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.controlsfx.control.GridCell;

public class ImageGridCell extends GridCell<Image>
{
    private final ImageView imageView;
    private final boolean preserveImageProperties;
    
    public ImageGridCell() {
        this(true);
    }
    
    public ImageGridCell(final boolean preserveImageProperties) {
        this.getStyleClass().add((Object)"image-grid-cell");
        this.preserveImageProperties = preserveImageProperties;
        this.imageView = new ImageView();
        this.imageView.fitHeightProperty().bind((ObservableValue)this.heightProperty());
        this.imageView.fitWidthProperty().bind((ObservableValue)this.widthProperty());
    }
    
    protected void updateItem(final Image item, final boolean empty) {
        super.updateItem((Object)item, empty);
        if (empty) {
            this.setGraphic((Node)null);
        }
        else {
            if (this.preserveImageProperties) {
                this.imageView.setPreserveRatio(item.isPreserveRatio());
                this.imageView.setSmooth(item.isSmooth());
            }
            this.imageView.setImage(item);
            this.setGraphic((Node)this.imageView);
        }
    }
}
