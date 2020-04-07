// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;
import javafx.scene.layout.Region;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.collections.ListChangeListener;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Accordion;
import java.util.ArrayList;
import java.util.TreeMap;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.control.ToggleButton;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.action.Action;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.scene.control.TextField;
import org.controlsfx.control.SegmentedButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.PropertySheet;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class PropertySheetSkin extends BehaviorSkinBase<PropertySheet, BehaviorBase<PropertySheet>>
{
    private static final int MIN_COLUMN_WIDTH = 100;
    private final BorderPane content;
    private final ScrollPane scroller;
    private final ToolBar toolbar;
    private final SegmentedButton modeButton;
    private final TextField searchField;
    
    public PropertySheetSkin(final PropertySheet control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.modeButton = ActionUtils.createSegmentedButton(new ActionChangeMode(PropertySheet.Mode.NAME), new ActionChangeMode(PropertySheet.Mode.CATEGORY));
        this.searchField = TextFields.createClearableTextField();
        (this.scroller = new ScrollPane()).setFitToWidth(true);
        this.toolbar = new ToolBar();
        this.toolbar.managedProperty().bind((ObservableValue)this.toolbar.visibleProperty());
        this.toolbar.setFocusTraversable(true);
        this.modeButton.managedProperty().bind((ObservableValue)this.modeButton.visibleProperty());
        ((ToggleButton)this.modeButton.getButtons().get(((PropertySheet.Mode)((PropertySheet)this.getSkinnable()).modeProperty().get()).ordinal())).setSelected(true);
        this.toolbar.getItems().add((Object)this.modeButton);
        this.searchField.setPromptText(Localization.localize(Localization.asKey("property.sheet.search.field.prompt")));
        this.searchField.setMinWidth(0.0);
        HBox.setHgrow((Node)this.searchField, Priority.SOMETIMES);
        this.searchField.managedProperty().bind((ObservableValue)this.searchField.visibleProperty());
        this.toolbar.getItems().add((Object)this.searchField);
        (this.content = new BorderPane()).setTop((Node)this.toolbar);
        this.content.setCenter((Node)this.scroller);
        this.getChildren().add((Object)this.content);
        this.registerChangeListener((ObservableValue)control.modeProperty(), "MODE");
        this.registerChangeListener((ObservableValue)control.propertyEditorFactory(), "EDITOR-FACTORY");
        this.registerChangeListener((ObservableValue)control.titleFilter(), "FILTER");
        this.registerChangeListener((ObservableValue)this.searchField.textProperty(), "FILTER-UI");
        this.registerChangeListener((ObservableValue)control.modeSwitcherVisibleProperty(), "TOOLBAR-MODE");
        this.registerChangeListener((ObservableValue)control.searchBoxVisibleProperty(), "TOOLBAR-SEARCH");
        control.getItems().addListener(change -> this.refreshProperties());
        this.refreshProperties();
        this.updateToolbar();
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if (p == "MODE" || p == "EDITOR-FACTORY" || p == "FILTER") {
            this.refreshProperties();
        }
        else if (p == "FILTER-UI") {
            ((PropertySheet)this.getSkinnable()).setTitleFilter(this.searchField.getText());
        }
        else if (p == "TOOLBAR-MODE") {
            this.updateToolbar();
        }
        else if (p == "TOOLBAR-SEARCH") {
            this.updateToolbar();
        }
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        this.content.resizeRelocate(x, y, w, h);
    }
    
    private void updateToolbar() {
        this.modeButton.setVisible(((PropertySheet)this.getSkinnable()).isModeSwitcherVisible());
        this.searchField.setVisible(((PropertySheet)this.getSkinnable()).isSearchBoxVisible());
        this.toolbar.setVisible(this.modeButton.isVisible() || this.searchField.isVisible());
    }
    
    private void refreshProperties() {
        this.scroller.setContent(this.buildPropertySheetContainer());
    }
    
    private Node buildPropertySheetContainer() {
        switch ((PropertySheet.Mode)((PropertySheet)this.getSkinnable()).modeProperty().get()) {
            case CATEGORY: {
                final Map<String, List<PropertySheet.Item>> categoryMap = new TreeMap<String, List<PropertySheet.Item>>();
                for (final PropertySheet.Item p : ((PropertySheet)this.getSkinnable()).getItems()) {
                    final String category = p.getCategory();
                    List<PropertySheet.Item> list = categoryMap.get(category);
                    if (list == null) {
                        list = new ArrayList<PropertySheet.Item>();
                        categoryMap.put(category, list);
                    }
                    list.add(p);
                }
                final Accordion accordion = new Accordion();
                final Iterator<String> iterator2 = categoryMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    final String category = iterator2.next();
                    final PropertyPane props = new PropertyPane(categoryMap.get(category));
                    if (props.getChildrenUnmodifiable().size() > 0) {
                        final TitledPane pane = new TitledPane(category, (Node)props);
                        pane.setExpanded(true);
                        accordion.getPanes().add((Object)pane);
                    }
                }
                if (accordion.getPanes().size() > 0) {
                    accordion.setExpandedPane((TitledPane)accordion.getPanes().get(0));
                }
                return (Node)accordion;
            }
            default: {
                return (Node)new PropertyPane((List<PropertySheet.Item>)((PropertySheet)this.getSkinnable()).getItems());
            }
        }
    }
    
    private class ActionChangeMode extends Action
    {
        private final Image CATEGORY_IMAGE;
        private final Image NAME_IMAGE;
        
        public ActionChangeMode(final PropertySheet.Mode mode) {
            super("");
            this.CATEGORY_IMAGE = new Image(PropertySheetSkin.class.getResource("/org/controlsfx/control/format-indent-more.png").toExternalForm());
            this.NAME_IMAGE = new Image(PropertySheetSkin.class.getResource("/org/controlsfx/control/format-line-spacing-triple.png").toExternalForm());
            this.setEventHandler(ae -> ((PropertySheet)PropertySheetSkin.this.getSkinnable()).modeProperty().set((Object)mode));
            if (mode == PropertySheet.Mode.CATEGORY) {
                this.setGraphic((Node)new ImageView(this.CATEGORY_IMAGE));
                this.setLongText(Localization.localize(Localization.asKey("property.sheet.group.mode.bycategory")));
            }
            else if (mode == PropertySheet.Mode.NAME) {
                this.setGraphic((Node)new ImageView(this.NAME_IMAGE));
                this.setLongText(Localization.localize(Localization.asKey("property.sheet.group.mode.byname")));
            }
            else {
                this.setText("???");
            }
        }
    }
    
    private class PropertyPane extends GridPane
    {
        public PropertyPane(final PropertySheetSkin propertySheetSkin, final List<PropertySheet.Item> properties) {
            this(properties, 0);
        }
        
        public PropertyPane(final List<PropertySheet.Item> properties, final int nestingLevel) {
            this.setVgap(5.0);
            this.setHgap(5.0);
            this.setPadding(new Insets(5.0, 15.0, 5.0, (double)(15 + nestingLevel * 10)));
            this.getStyleClass().add((Object)"property-pane");
            this.setItems(properties);
        }
        
        public void setItems(final List<PropertySheet.Item> properties) {
            this.getChildren().clear();
            String filter = ((PropertySheet)PropertySheetSkin.this.getSkinnable()).titleFilter().get();
            filter = ((filter == null) ? "" : filter.trim().toLowerCase());
            int row = 0;
            for (final PropertySheet.Item item : properties) {
                final String title = item.getName();
                if (!filter.isEmpty() && title.toLowerCase().indexOf(filter) < 0) {
                    continue;
                }
                final Label label = new Label(title);
                label.setMinWidth(100.0);
                final String description = item.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    label.setTooltip(new Tooltip(description));
                }
                this.add((Node)label, 0, row);
                final Node editor = this.getEditor(item);
                if (editor instanceof Region) {
                    ((Region)editor).setMinWidth(100.0);
                    ((Region)editor).setMaxWidth(Double.MAX_VALUE);
                }
                label.setLabelFor(editor);
                this.add(editor, 1, row);
                GridPane.setHgrow(editor, Priority.ALWAYS);
                ++row;
            }
        }
        
        private Node getEditor(final PropertySheet.Item item) {
            PropertyEditor editor = (PropertyEditor)((PropertySheet)PropertySheetSkin.this.getSkinnable()).getPropertyEditorFactory().call((Object)item);
            if (editor == null) {
                editor = new AbstractPropertyEditor<Object, TextField>(item, new TextField(), true) {
                    {
                        ((AbstractPropertyEditor<T, TextField>)this).getEditor().setEditable(false);
                        ((AbstractPropertyEditor<T, TextField>)this).getEditor().setDisable(true);
                    }
                    
                    @Override
                    protected ObservableValue<Object> getObservableValue() {
                        return (ObservableValue<Object>)((AbstractPropertyEditor<T, TextField>)this).getEditor().textProperty();
                    }
                    
                    @Override
                    public void setValue(final Object value) {
                        ((AbstractPropertyEditor<T, TextField>)this).getEditor().setText((value == null) ? "" : value.toString());
                    }
                };
            }
            else if (!item.isEditable()) {
                editor.getEditor().setDisable(true);
            }
            editor.setValue(item.getValue());
            return editor.getEditor();
        }
    }
}
