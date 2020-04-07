// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.cell;

import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import org.controlsfx.control.GridCell;

public class ColorGridCell extends GridCell<Color>
{
    private Rectangle colorRect;
    private static final boolean debug = false;
    
    public ColorGridCell() {
        this.getStyleClass().add((Object)"color-grid-cell");
        (this.colorRect = new Rectangle()).setStroke((Paint)Color.BLACK);
        this.colorRect.heightProperty().bind((ObservableValue)this.heightProperty());
        this.colorRect.widthProperty().bind((ObservableValue)this.widthProperty());
        this.setGraphic((Node)this.colorRect);
    }
    
    protected void updateItem(final Color item, final boolean empty) {
        super.updateItem((Object)item, empty);
        if (empty) {
            this.setGraphic((Node)null);
        }
        else {
            this.colorRect.setFill((Paint)item);
            this.setGraphic((Node)this.colorRect);
        }
    }
}
