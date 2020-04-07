// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.layout.Region;
import javafx.beans.Observable;
import java.util.Collection;
import org.controlsfx.control.spreadsheet.Picker;
import java.util.Iterator;
import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Rectangle;
import javafx.beans.InvalidationListener;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import java.util.Stack;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.scene.layout.StackPane;

public class HorizontalPicker extends StackPane
{
    private static final String PICKER_INDEX = "PickerIndex";
    private final HorizontalHeader horizontalHeader;
    private final SpreadsheetView spv;
    private final Stack<Label> pickerPile;
    private final Stack<Label> pickerUsed;
    private final InnerHorizontalPicker innerPicker;
    private final EventHandler<MouseEvent> pickerMouseEvent;
    private final InvalidationListener layoutListener;
    
    public HorizontalPicker(final HorizontalHeader horizontalHeader, final SpreadsheetView spv) {
        this.innerPicker = new InnerHorizontalPicker();
        this.pickerMouseEvent = (EventHandler<MouseEvent>)(mouseEvent -> {
            final Label picker = (Label)mouseEvent.getSource();
            ((Picker)picker.getProperties().get((Object)"PickerIndex")).onClick();
        });
        this.layoutListener = (arg0 -> this.innerPicker.requestLayout());
        this.horizontalHeader = horizontalHeader;
        this.spv = spv;
        this.pickerPile = new Stack<Label>();
        this.pickerUsed = new Stack<Label>();
        final Rectangle clip = new Rectangle();
        clip.setSmooth(true);
        clip.setHeight(16.0);
        clip.widthProperty().bind((ObservableValue)horizontalHeader.widthProperty());
        this.setClip((Node)clip);
        this.getChildren().add((Object)this.innerPicker);
        horizontalHeader.getRootHeader().getColumnHeaders().addListener(this.layoutListener);
        spv.getColumnPickers().addListener(this.layoutListener);
    }
    
    protected void layoutChildren() {
        this.innerPicker.relocate(this.horizontalHeader.getRootHeader().getLayoutX(), this.snappedTopInset());
        for (final Label label : this.pickerUsed) {
            label.setVisible(label.getLayoutX() + this.innerPicker.getLayoutX() + label.getWidth() > this.horizontalHeader.gridViewSkin.fixedColumnWidth);
        }
    }
    
    public void updateScrollX() {
        this.requestLayout();
    }
    
    private Label getPicker(final Picker picker) {
        Label pickerLabel;
        if (this.pickerPile.isEmpty()) {
            pickerLabel = new Label();
            pickerLabel.getStyleClass().addListener(this.layoutListener);
            pickerLabel.setOnMouseClicked((EventHandler)this.pickerMouseEvent);
        }
        else {
            pickerLabel = this.pickerPile.pop();
        }
        this.pickerUsed.push(pickerLabel);
        pickerLabel.getStyleClass().setAll((Collection)picker.getStyleClass());
        pickerLabel.getProperties().put((Object)"PickerIndex", (Object)picker);
        return pickerLabel;
    }
    
    private class InnerHorizontalPicker extends Region
    {
        protected void layoutChildren() {
            HorizontalPicker.this.pickerPile.addAll(HorizontalPicker.this.pickerUsed.subList(0, HorizontalPicker.this.pickerUsed.size()));
            for (final Label label : HorizontalPicker.this.pickerUsed) {
                label.layoutXProperty().unbind();
                label.setVisible(true);
            }
            HorizontalPicker.this.pickerUsed.clear();
            this.getChildren().clear();
            int index = 0;
            for (final TableColumnHeader column : HorizontalPicker.this.horizontalHeader.getRootHeader().getColumnHeaders()) {
                final int modelColumn = HorizontalPicker.this.spv.getModelColumn(index);
                if (HorizontalPicker.this.spv.getColumnPickers().containsKey((Object)modelColumn)) {
                    final Label label2 = HorizontalPicker.this.getPicker((Picker)HorizontalPicker.this.spv.getColumnPickers().get((Object)modelColumn));
                    label2.resize(column.getWidth(), 16.0);
                    label2.layoutXProperty().bind((ObservableValue)column.layoutXProperty());
                    this.getChildren().add(0, (Object)label2);
                }
                ++index;
            }
        }
    }
}
