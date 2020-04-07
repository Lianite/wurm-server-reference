// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.event.ActionEvent;
import javafx.util.Callback;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContentDisplay;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ListCell;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Control;
import org.controlsfx.control.TaskProgressView;
import javafx.scene.control.SkinBase;
import javafx.concurrent.Task;

public class TaskProgressViewSkin<T extends Task<?>> extends SkinBase<TaskProgressView<T>>
{
    public TaskProgressViewSkin(final TaskProgressView<T> monitor) {
        super((Control)monitor);
        final BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add((Object)"box");
        final ListView<T> listView = (ListView<T>)new ListView();
        listView.setPrefSize(500.0, 400.0);
        listView.setPlaceholder((Node)new Label("No tasks running"));
        listView.setCellFactory(param -> new TaskCell());
        listView.setFocusTraversable(false);
        Bindings.bindContent((List)listView.getItems(), (ObservableList)monitor.getTasks());
        borderPane.setCenter((Node)listView);
        this.getChildren().add((Object)listView);
    }
    
    class TaskCell extends ListCell<T>
    {
        private ProgressBar progressBar;
        private Label titleText;
        private Label messageText;
        private Button cancelButton;
        private T task;
        private BorderPane borderPane;
        
        public TaskCell() {
            this.titleText = new Label();
            this.titleText.getStyleClass().add((Object)"task-title");
            this.messageText = new Label();
            this.messageText.getStyleClass().add((Object)"task-message");
            (this.progressBar = new ProgressBar()).setMaxWidth(Double.MAX_VALUE);
            this.progressBar.setMaxHeight(8.0);
            this.progressBar.getStyleClass().add((Object)"task-progress-bar");
            this.cancelButton = new Button("Cancel");
            this.cancelButton.getStyleClass().add((Object)"task-cancel-button");
            this.cancelButton.setTooltip(new Tooltip("Cancel Task"));
            this.cancelButton.setOnAction(evt -> {
                if (this.task != null) {
                    this.task.cancel();
                }
            });
            final VBox vbox = new VBox();
            vbox.setSpacing(4.0);
            vbox.getChildren().add((Object)this.titleText);
            vbox.getChildren().add((Object)this.progressBar);
            vbox.getChildren().add((Object)this.messageText);
            BorderPane.setAlignment((Node)this.cancelButton, Pos.CENTER);
            BorderPane.setMargin((Node)this.cancelButton, new Insets(0.0, 0.0, 0.0, 4.0));
            (this.borderPane = new BorderPane()).setCenter((Node)vbox);
            this.borderPane.setRight((Node)this.cancelButton);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        
        public void updateIndex(final int index) {
            super.updateIndex(index);
            if (index == -1) {
                this.setGraphic((Node)null);
                this.getStyleClass().setAll((Object[])new String[] { "task-list-cell-empty" });
            }
        }
        
        protected void updateItem(final T task, final boolean empty) {
            super.updateItem((Object)task, empty);
            this.task = task;
            if (empty || task == null) {
                this.getStyleClass().setAll((Object[])new String[] { "task-list-cell-empty" });
                this.setGraphic((Node)null);
            }
            else if (task != null) {
                this.getStyleClass().setAll((Object[])new String[] { "task-list-cell" });
                this.progressBar.progressProperty().bind((ObservableValue)task.progressProperty());
                this.titleText.textProperty().bind((ObservableValue)task.titleProperty());
                this.messageText.textProperty().bind((ObservableValue)task.messageProperty());
                this.cancelButton.disableProperty().bind((ObservableValue)Bindings.not((ObservableBooleanValue)task.runningProperty()));
                final Callback<T, Node> factory = ((TaskProgressView)TaskProgressViewSkin.this.getSkinnable()).getGraphicFactory();
                if (factory != null) {
                    final Node graphic = (Node)factory.call((Object)task);
                    if (graphic != null) {
                        BorderPane.setAlignment(graphic, Pos.CENTER);
                        BorderPane.setMargin(graphic, new Insets(0.0, 4.0, 0.0, 0.0));
                        this.borderPane.setLeft(graphic);
                    }
                }
                else {
                    this.borderPane.setLeft((Node)null);
                }
                this.setGraphic((Node)this.borderPane);
            }
        }
    }
}
