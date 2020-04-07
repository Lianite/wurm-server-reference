// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.input.MouseButton;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import org.controlsfx.glyphfont.FontAwesome;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.beans.InvalidationListener;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import java.util.Objects;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.ListSelectionView;
import javafx.scene.control.SkinBase;

public class ListSelectionViewSkin<T> extends SkinBase<ListSelectionView<T>>
{
    private GridPane gridPane;
    private final HBox horizontalButtonBox;
    private final VBox verticalButtonBox;
    private Button moveToTarget;
    private Button moveToTargetAll;
    private Button moveToSourceAll;
    private Button moveToSource;
    private ListView<T> sourceListView;
    private ListView<T> targetListView;
    
    public ListSelectionViewSkin(final ListSelectionView<T> view) {
        super((Control)view);
        (this.sourceListView = Objects.requireNonNull(this.createSourceListView(), "source list view can not be null")).setId("source-list-view");
        this.sourceListView.setItems((ObservableList)view.getSourceItems());
        (this.targetListView = Objects.requireNonNull(this.createTargetListView(), "target list view can not be null")).setId("target-list-view");
        this.targetListView.setItems((ObservableList)view.getTargetItems());
        this.sourceListView.cellFactoryProperty().bind((ObservableValue)view.cellFactoryProperty());
        this.targetListView.cellFactoryProperty().bind((ObservableValue)view.cellFactoryProperty());
        this.gridPane = this.createGridPane();
        this.horizontalButtonBox = this.createHorizontalButtonBox();
        this.verticalButtonBox = this.createVerticalButtonBox();
        this.getChildren().add((Object)this.gridPane);
        final InvalidationListener updateListener = o -> this.updateView();
        view.sourceHeaderProperty().addListener(updateListener);
        view.sourceFooterProperty().addListener(updateListener);
        view.targetHeaderProperty().addListener(updateListener);
        view.targetFooterProperty().addListener(updateListener);
        this.updateView();
        this.getSourceListView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                this.moveToTarget();
            }
        });
        this.getTargetListView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                this.moveToSource();
            }
        });
        view.orientationProperty().addListener(observable -> this.updateView());
    }
    
    private GridPane createGridPane() {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add((Object)"grid-pane");
        return gridPane;
    }
    
    private void setHorizontalViewConstraints() {
        this.gridPane.getColumnConstraints().clear();
        this.gridPane.getRowConstraints().clear();
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setFillWidth(true);
        col1.setHgrow(Priority.ALWAYS);
        col1.setMaxWidth(Double.MAX_VALUE);
        col1.setPrefWidth(200.0);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setFillWidth(true);
        col2.setHgrow(Priority.NEVER);
        final ColumnConstraints col3 = new ColumnConstraints();
        col3.setFillWidth(true);
        col3.setHgrow(Priority.ALWAYS);
        col3.setMaxWidth(Double.MAX_VALUE);
        col3.setPrefWidth(200.0);
        this.gridPane.getColumnConstraints().addAll((Object[])new ColumnConstraints[] { col1, col2, col3 });
        final RowConstraints row1 = new RowConstraints();
        row1.setFillHeight(true);
        row1.setVgrow(Priority.NEVER);
        final RowConstraints row2 = new RowConstraints();
        row2.setMaxHeight(Double.MAX_VALUE);
        row2.setPrefHeight(200.0);
        row2.setVgrow(Priority.ALWAYS);
        final RowConstraints row3 = new RowConstraints();
        row3.setFillHeight(true);
        row3.setVgrow(Priority.NEVER);
        this.gridPane.getRowConstraints().addAll((Object[])new RowConstraints[] { row1, row2, row3 });
    }
    
    private void setVerticalViewConstraints() {
        this.gridPane.getColumnConstraints().clear();
        this.gridPane.getRowConstraints().clear();
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setFillWidth(true);
        col1.setHgrow(Priority.ALWAYS);
        col1.setMaxWidth(Double.MAX_VALUE);
        col1.setPrefWidth(200.0);
        this.gridPane.getColumnConstraints().addAll((Object[])new ColumnConstraints[] { col1 });
        final RowConstraints row1 = new RowConstraints();
        row1.setFillHeight(true);
        row1.setVgrow(Priority.NEVER);
        final RowConstraints row2 = new RowConstraints();
        row2.setMaxHeight(Double.MAX_VALUE);
        row2.setPrefHeight(200.0);
        row2.setVgrow(Priority.ALWAYS);
        final RowConstraints row3 = new RowConstraints();
        row3.setFillHeight(true);
        row3.setVgrow(Priority.NEVER);
        final RowConstraints row4 = new RowConstraints();
        row4.setFillHeight(true);
        row4.setVgrow(Priority.NEVER);
        final RowConstraints row5 = new RowConstraints();
        row5.setFillHeight(true);
        row5.setVgrow(Priority.NEVER);
        final RowConstraints row6 = new RowConstraints();
        row6.setMaxHeight(Double.MAX_VALUE);
        row6.setPrefHeight(200.0);
        row6.setVgrow(Priority.ALWAYS);
        final RowConstraints row7 = new RowConstraints();
        row7.setFillHeight(true);
        row7.setVgrow(Priority.NEVER);
        this.gridPane.getRowConstraints().addAll((Object[])new RowConstraints[] { row1, row2, row3, row4, row5, row6, row7 });
    }
    
    private VBox createVerticalButtonBox() {
        final VBox box = new VBox(5.0);
        box.setFillWidth(true);
        final FontAwesome fontAwesome = new FontAwesome();
        this.moveToTarget = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_RIGHT));
        this.moveToTargetAll = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT));
        this.moveToSource = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_LEFT));
        this.moveToSourceAll = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_LEFT));
        this.updateButtons();
        box.getChildren().addAll((Object[])new Node[] { this.moveToTarget, this.moveToTargetAll, this.moveToSource, this.moveToSourceAll });
        return box;
    }
    
    private HBox createHorizontalButtonBox() {
        final HBox box = new HBox(5.0);
        box.setFillHeight(true);
        final FontAwesome fontAwesome = new FontAwesome();
        this.moveToTarget = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_DOWN));
        this.moveToTargetAll = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_DOWN));
        this.moveToSource = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_UP));
        this.moveToSourceAll = new Button("", (Node)fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_UP));
        this.updateButtons();
        box.getChildren().addAll((Object[])new Node[] { this.moveToTarget, this.moveToTargetAll, this.moveToSource, this.moveToSourceAll });
        return box;
    }
    
    private void updateButtons() {
        this.moveToTarget.getStyleClass().add((Object)"move-to-target-button");
        this.moveToTargetAll.getStyleClass().add((Object)"move-to-target-all-button");
        this.moveToSource.getStyleClass().add((Object)"move-to-source-button");
        this.moveToSourceAll.getStyleClass().add((Object)"move-to-source-all-button");
        this.moveToTarget.setMaxWidth(Double.MAX_VALUE);
        this.moveToTargetAll.setMaxWidth(Double.MAX_VALUE);
        this.moveToSource.setMaxWidth(Double.MAX_VALUE);
        this.moveToSourceAll.setMaxWidth(Double.MAX_VALUE);
        this.getSourceListView().itemsProperty().addListener(it -> this.bindMoveAllButtonsToDataModel());
        this.getTargetListView().itemsProperty().addListener(it -> this.bindMoveAllButtonsToDataModel());
        this.getSourceListView().selectionModelProperty().addListener(it -> this.bindMoveButtonsToSelectionModel());
        this.getTargetListView().selectionModelProperty().addListener(it -> this.bindMoveButtonsToSelectionModel());
        this.bindMoveButtonsToSelectionModel();
        this.bindMoveAllButtonsToDataModel();
        this.moveToTarget.setOnAction(evt -> this.moveToTarget());
        this.moveToTargetAll.setOnAction(evt -> this.moveToTargetAll());
        this.moveToSource.setOnAction(evt -> this.moveToSource());
        this.moveToSourceAll.setOnAction(evt -> this.moveToSourceAll());
    }
    
    private void bindMoveAllButtonsToDataModel() {
        this.moveToTargetAll.disableProperty().bind((ObservableValue)Bindings.isEmpty(this.getSourceListView().getItems()));
        this.moveToSourceAll.disableProperty().bind((ObservableValue)Bindings.isEmpty(this.getTargetListView().getItems()));
    }
    
    private void bindMoveButtonsToSelectionModel() {
        this.moveToTarget.disableProperty().bind((ObservableValue)Bindings.isEmpty(this.getSourceListView().getSelectionModel().getSelectedItems()));
        this.moveToSource.disableProperty().bind((ObservableValue)Bindings.isEmpty(this.getTargetListView().getSelectionModel().getSelectedItems()));
    }
    
    private void updateView() {
        this.gridPane.getChildren().clear();
        final Node sourceHeader = ((ListSelectionView)this.getSkinnable()).getSourceHeader();
        final Node targetHeader = ((ListSelectionView)this.getSkinnable()).getTargetHeader();
        final Node sourceFooter = ((ListSelectionView)this.getSkinnable()).getSourceFooter();
        final Node targetFooter = ((ListSelectionView)this.getSkinnable()).getTargetFooter();
        final ListView<T> sourceList = this.getSourceListView();
        final ListView<T> targetList = this.getTargetListView();
        final StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        final Orientation orientation = ((ListSelectionView)this.getSkinnable()).getOrientation();
        if (orientation == Orientation.HORIZONTAL) {
            this.setHorizontalViewConstraints();
            if (sourceHeader != null) {
                this.gridPane.add(sourceHeader, 0, 0);
            }
            if (targetHeader != null) {
                this.gridPane.add(targetHeader, 2, 0);
            }
            if (sourceList != null) {
                this.gridPane.add((Node)sourceList, 0, 1);
            }
            if (targetList != null) {
                this.gridPane.add((Node)targetList, 2, 1);
            }
            if (sourceFooter != null) {
                this.gridPane.add(sourceFooter, 0, 2);
            }
            if (targetFooter != null) {
                this.gridPane.add(targetFooter, 2, 2);
            }
            stackPane.getChildren().add((Object)this.verticalButtonBox);
            this.gridPane.add((Node)stackPane, 1, 1);
        }
        else {
            this.setVerticalViewConstraints();
            if (sourceHeader != null) {
                this.gridPane.add(sourceHeader, 0, 0);
            }
            if (targetHeader != null) {
                this.gridPane.add(targetHeader, 0, 4);
            }
            if (sourceList != null) {
                this.gridPane.add((Node)sourceList, 0, 1);
            }
            if (targetList != null) {
                this.gridPane.add((Node)targetList, 0, 5);
            }
            if (sourceFooter != null) {
                this.gridPane.add(sourceFooter, 0, 2);
            }
            if (targetFooter != null) {
                this.gridPane.add(targetFooter, 0, 6);
            }
            stackPane.getChildren().add((Object)this.horizontalButtonBox);
            this.gridPane.add((Node)stackPane, 0, 3);
        }
    }
    
    private void moveToTarget() {
        this.move(this.getSourceListView(), this.getTargetListView());
        this.getSourceListView().getSelectionModel().clearSelection();
    }
    
    private void moveToTargetAll() {
        this.move(this.getSourceListView(), this.getTargetListView(), new ArrayList<T>((Collection<? extends T>)this.getSourceListView().getItems()));
        this.getSourceListView().getSelectionModel().clearSelection();
    }
    
    private void moveToSource() {
        this.move(this.getTargetListView(), this.getSourceListView());
        this.getTargetListView().getSelectionModel().clearSelection();
    }
    
    private void moveToSourceAll() {
        this.move(this.getTargetListView(), this.getSourceListView(), new ArrayList<T>((Collection<? extends T>)this.getTargetListView().getItems()));
        this.getTargetListView().getSelectionModel().clearSelection();
    }
    
    private void move(final ListView<T> viewA, final ListView<T> viewB) {
        final List<T> selectedItems = new ArrayList<T>((Collection<? extends T>)viewA.getSelectionModel().getSelectedItems());
        this.move(viewA, viewB, selectedItems);
    }
    
    private void move(final ListView<T> viewA, final ListView<T> viewB, final List<T> items) {
        viewA.getItems().removeAll((Collection)items);
        viewB.getItems().addAll((Collection)items);
    }
    
    public final ListView<T> getSourceListView() {
        return this.sourceListView;
    }
    
    public final ListView<T> getTargetListView() {
        return this.targetListView;
    }
    
    protected ListView<T> createSourceListView() {
        return this.createListView();
    }
    
    protected ListView<T> createTargetListView() {
        return this.createListView();
    }
    
    private ListView<T> createListView() {
        final ListView<T> view = (ListView<T>)new ListView();
        view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return view;
    }
}
