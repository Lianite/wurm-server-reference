// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Control;
import org.controlsfx.control.MaskerPane;
import javafx.scene.control.SkinBase;

public class MaskerPaneSkin extends SkinBase<MaskerPane>
{
    public MaskerPaneSkin(final MaskerPane maskerPane) {
        super((Control)maskerPane);
        this.getChildren().add((Object)this.createMasker(maskerPane));
    }
    
    private StackPane createMasker(final MaskerPane maskerPane) {
        final VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10.0);
        vBox.getStyleClass().add((Object)"masker-center");
        vBox.getChildren().add((Object)this.createLabel());
        vBox.getChildren().add((Object)this.createProgressIndicator());
        final HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll((Object[])new Node[] { vBox });
        final StackPane glass = new StackPane();
        glass.setAlignment(Pos.CENTER);
        glass.getStyleClass().add((Object)"masker-glass");
        glass.getChildren().add((Object)hBox);
        return glass;
    }
    
    private Label createLabel() {
        final Label text = new Label();
        text.textProperty().bind((ObservableValue)((MaskerPane)this.getSkinnable()).textProperty());
        text.getStyleClass().add((Object)"masker-text");
        return text;
    }
    
    private Label createProgressIndicator() {
        final Label graphic = new Label();
        graphic.setGraphic(((MaskerPane)this.getSkinnable()).getProgressNode());
        graphic.visibleProperty().bind((ObservableValue)((MaskerPane)this.getSkinnable()).progressVisibleProperty());
        graphic.getStyleClass().add((Object)"masker-graphic");
        return graphic;
    }
}
