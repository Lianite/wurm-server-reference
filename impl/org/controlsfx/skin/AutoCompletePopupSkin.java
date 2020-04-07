// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Skinnable;
import javafx.scene.Node;
import javafx.event.EventTarget;
import javafx.event.Event;
import javafx.util.StringConverter;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.binding.Bindings;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

public class AutoCompletePopupSkin<T> implements Skin<AutoCompletePopup<T>>
{
    private final AutoCompletePopup<T> control;
    private final ListView<T> suggestionList;
    final int LIST_CELL_HEIGHT = 24;
    
    public AutoCompletePopupSkin(final AutoCompletePopup<T> control) {
        this.control = control;
        this.suggestionList = (ListView<T>)new ListView((ObservableList)control.getSuggestions());
        this.suggestionList.getStyleClass().add((Object)"auto-complete-popup");
        this.suggestionList.getStylesheets().add((Object)AutoCompletionBinding.class.getResource("autocompletion.css").toExternalForm());
        this.suggestionList.prefHeightProperty().bind((ObservableValue)Bindings.min((ObservableNumberValue)control.visibleRowCountProperty(), (ObservableNumberValue)Bindings.size(this.suggestionList.getItems())).multiply(24).add(18));
        this.suggestionList.setCellFactory(TextFieldListCell.forListView((StringConverter)control.getConverter()));
        this.suggestionList.prefWidthProperty().bind((ObservableValue)control.prefWidthProperty());
        this.suggestionList.maxWidthProperty().bind((ObservableValue)control.maxWidthProperty());
        this.suggestionList.minWidthProperty().bind((ObservableValue)control.minWidthProperty());
        this.registerEventListener();
    }
    
    private void registerEventListener() {
        this.suggestionList.setOnMouseClicked(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                this.onSuggestionChoosen(this.suggestionList.getSelectionModel().getSelectedItem());
            }
        });
        this.suggestionList.setOnKeyPressed(ke -> {
            switch (ke.getCode()) {
                case TAB:
                case ENTER: {
                    this.onSuggestionChoosen(this.suggestionList.getSelectionModel().getSelectedItem());
                    break;
                }
                case ESCAPE: {
                    if (this.control.isHideOnEscape()) {
                        this.control.hide();
                        break;
                    }
                    break;
                }
            }
        });
    }
    
    private void onSuggestionChoosen(final T suggestion) {
        if (suggestion != null) {
            Event.fireEvent((EventTarget)this.control, (Event)new AutoCompletePopup.SuggestionEvent<Object>(suggestion));
        }
    }
    
    public Node getNode() {
        return (Node)this.suggestionList;
    }
    
    public AutoCompletePopup<T> getSkinnable() {
        return this.control;
    }
    
    public void dispose() {
    }
}
