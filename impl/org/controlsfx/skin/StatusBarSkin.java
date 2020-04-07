// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.layout.Priority;
import javafx.scene.Node;
import java.util.Collection;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.GridPane;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.StatusBar;
import javafx.scene.control.SkinBase;

public class StatusBarSkin extends SkinBase<StatusBar>
{
    private HBox leftBox;
    private HBox rightBox;
    private Label label;
    private ProgressBar progressBar;
    
    public StatusBarSkin(final StatusBar statusBar) {
        super((Control)statusBar);
        final BooleanBinding notZeroProgressProperty = Bindings.notEqual(0, (ObservableNumberValue)statusBar.progressProperty());
        final GridPane gridPane = new GridPane();
        this.leftBox = new HBox();
        this.leftBox.getStyleClass().add((Object)"left-items");
        this.rightBox = new HBox();
        this.rightBox.getStyleClass().add((Object)"right-items");
        this.progressBar = new ProgressBar();
        this.progressBar.progressProperty().bind((ObservableValue)statusBar.progressProperty());
        this.progressBar.visibleProperty().bind((ObservableValue)notZeroProgressProperty);
        this.progressBar.managedProperty().bind((ObservableValue)notZeroProgressProperty);
        (this.label = new Label()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.label.textProperty().bind((ObservableValue)statusBar.textProperty());
        this.label.graphicProperty().bind((ObservableValue)statusBar.graphicProperty());
        this.label.styleProperty().bind((ObservableValue)((StatusBar)this.getSkinnable()).styleProperty());
        this.label.getStyleClass().add((Object)"status-label");
        this.leftBox.getChildren().setAll((Collection)((StatusBar)this.getSkinnable()).getLeftItems());
        this.rightBox.getChildren().setAll((Collection)((StatusBar)this.getSkinnable()).getRightItems());
        statusBar.getLeftItems().addListener(evt -> this.leftBox.getChildren().setAll((Collection)((StatusBar)this.getSkinnable()).getLeftItems()));
        statusBar.getRightItems().addListener(evt -> this.rightBox.getChildren().setAll((Collection)((StatusBar)this.getSkinnable()).getRightItems()));
        GridPane.setFillHeight((Node)this.leftBox, true);
        GridPane.setFillHeight((Node)this.rightBox, true);
        GridPane.setFillHeight((Node)this.label, true);
        GridPane.setFillHeight((Node)this.progressBar, true);
        GridPane.setVgrow((Node)this.leftBox, Priority.ALWAYS);
        GridPane.setVgrow((Node)this.rightBox, Priority.ALWAYS);
        GridPane.setVgrow((Node)this.label, Priority.ALWAYS);
        GridPane.setVgrow((Node)this.progressBar, Priority.ALWAYS);
        GridPane.setHgrow((Node)this.label, Priority.ALWAYS);
        gridPane.add((Node)this.leftBox, 0, 0);
        gridPane.add((Node)this.label, 1, 0);
        gridPane.add((Node)this.progressBar, 2, 0);
        gridPane.add((Node)this.rightBox, 3, 0);
        this.getChildren().add((Object)gridPane);
    }
}
