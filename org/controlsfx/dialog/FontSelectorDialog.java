// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.MultipleSelectionModel;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;
import javafx.collections.transformation.FilteredList;
import java.util.function.Predicate;
import javafx.scene.layout.GridPane;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.control.DialogPane;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.text.Font;
import javafx.scene.control.Dialog;

public class FontSelectorDialog extends Dialog<Font>
{
    private FontPanel fontPanel;
    
    public FontSelectorDialog(final Font defaultFont) {
        (this.fontPanel = new FontPanel()).setFont(defaultFont);
        this.setResultConverter(dialogButton -> (dialogButton == ButtonType.OK) ? this.fontPanel.getFont() : null);
        final DialogPane dialogPane = this.getDialogPane();
        this.setTitle(Localization.localize(Localization.asKey("font.dlg.title")));
        dialogPane.setHeaderText(Localization.localize(Localization.asKey("font.dlg.header")));
        dialogPane.getStyleClass().add((Object)"font-selector-dialog");
        dialogPane.getStylesheets().add((Object)FontSelectorDialog.class.getResource("dialogs.css").toExternalForm());
        dialogPane.getButtonTypes().addAll((Object[])new ButtonType[] { ButtonType.OK, ButtonType.CANCEL });
        dialogPane.setContent((Node)this.fontPanel);
    }
    
    private static class FontStyle implements Comparable<FontStyle>
    {
        private FontPosture posture;
        private FontWeight weight;
        
        public FontStyle(final FontWeight weight, final FontPosture posture) {
            this.posture = ((posture == null) ? FontPosture.REGULAR : posture);
            this.weight = weight;
        }
        
        public FontStyle() {
            this(null, null);
        }
        
        public FontStyle(final String styles) {
            this();
            final String[] split;
            final String[] fontStyles = split = ((styles == null) ? "" : styles.trim().toUpperCase()).split(" ");
            for (final String style : split) {
                final FontWeight w = FontWeight.findByName(style);
                if (w != null) {
                    this.weight = w;
                }
                else {
                    final FontPosture p = FontPosture.findByName(style);
                    if (p != null) {
                        this.posture = p;
                    }
                }
            }
        }
        
        public FontStyle(final Font font) {
            this(font.getStyle());
        }
        
        public FontPosture getPosture() {
            return this.posture;
        }
        
        public FontWeight getWeight() {
            return this.weight;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.posture == null) ? 0 : this.posture.hashCode());
            result = 31 * result + ((this.weight == null) ? 0 : this.weight.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object that) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            if (this.getClass() != that.getClass()) {
                return false;
            }
            final FontStyle other = (FontStyle)that;
            return this.posture == other.posture && this.weight == other.weight;
        }
        
        private static String makePretty(final Object o) {
            String s = (o == null) ? "" : o.toString();
            if (!s.isEmpty()) {
                s = s.replace("_", " ");
                s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            }
            return s;
        }
        
        @Override
        public String toString() {
            return String.format("%s %s", makePretty(this.weight), makePretty(this.posture)).trim();
        }
        
        private <T extends Enum<T>> int compareEnums(final T e1, final T e2) {
            if (e1 == e2) {
                return 0;
            }
            if (e1 == null) {
                return -1;
            }
            if (e2 == null) {
                return 1;
            }
            return e1.compareTo(e2);
        }
        
        @Override
        public int compareTo(final FontStyle fs) {
            final int result = this.compareEnums(this.weight, fs.weight);
            return (result != 0) ? result : this.compareEnums(this.posture, fs.posture);
        }
    }
    
    private static class FontPanel extends GridPane
    {
        private static final double HGAP = 10.0;
        private static final double VGAP = 5.0;
        private static final Predicate<Object> MATCH_ALL;
        private static final Double[] fontSizes;
        private final FilteredList<String> filteredFontList;
        private final FilteredList<FontStyle> filteredStyleList;
        private final FilteredList<Double> filteredSizeList;
        private final ListView<String> fontListView;
        private final ListView<FontStyle> styleListView;
        private final ListView<Double> sizeListView;
        private final Text sample;
        
        private static List<FontStyle> getFontStyles(final String fontFamily) {
            final Set<FontStyle> set = new HashSet<FontStyle>();
            for (final String f : Font.getFontNames(fontFamily)) {
                set.add(new FontStyle(f.replace(fontFamily, "")));
            }
            final List<FontStyle> result = new ArrayList<FontStyle>(set);
            Collections.sort(result);
            return result;
        }
        
        public FontPanel() {
            this.filteredFontList = (FilteredList<String>)new FilteredList(FXCollections.observableArrayList((Collection)Font.getFamilies()), (Predicate)FontPanel.MATCH_ALL);
            this.filteredStyleList = (FilteredList<FontStyle>)new FilteredList(FXCollections.observableArrayList(), (Predicate)FontPanel.MATCH_ALL);
            this.filteredSizeList = (FilteredList<Double>)new FilteredList(FXCollections.observableArrayList((Object[])FontPanel.fontSizes), (Predicate)FontPanel.MATCH_ALL);
            this.fontListView = (ListView<String>)new ListView((ObservableList)this.filteredFontList);
            this.styleListView = (ListView<FontStyle>)new ListView((ObservableList)this.filteredStyleList);
            this.sizeListView = (ListView<Double>)new ListView((ObservableList)this.filteredSizeList);
            this.sample = new Text(Localization.localize(Localization.asKey("font.dlg.sample.text")));
            this.setHgap(10.0);
            this.setVgap(5.0);
            this.setPrefSize(500.0, 300.0);
            this.setMinSize(500.0, 300.0);
            final ColumnConstraints c0 = new ColumnConstraints();
            c0.setPercentWidth(60.0);
            final ColumnConstraints c2 = new ColumnConstraints();
            c2.setPercentWidth(25.0);
            final ColumnConstraints c3 = new ColumnConstraints();
            c3.setPercentWidth(15.0);
            this.getColumnConstraints().addAll((Object[])new ColumnConstraints[] { c0, c2, c3 });
            final RowConstraints r0 = new RowConstraints();
            r0.setVgrow(Priority.NEVER);
            final RowConstraints r2 = new RowConstraints();
            r2.setVgrow(Priority.NEVER);
            final RowConstraints r3 = new RowConstraints();
            r3.setFillHeight(true);
            r3.setVgrow(Priority.NEVER);
            final RowConstraints r4 = new RowConstraints();
            r4.setPrefHeight(250.0);
            r4.setVgrow(Priority.NEVER);
            this.getRowConstraints().addAll((Object[])new RowConstraints[] { r0, r2, r3, r4 });
            this.add((Node)new Label(Localization.localize(Localization.asKey("font.dlg.font.label"))), 0, 0);
            this.add((Node)this.fontListView, 0, 1);
            this.fontListView.setCellFactory((Callback)new Callback<ListView<String>, ListCell<String>>() {
                public ListCell<String> call(final ListView<String> listview) {
                    return new ListCell<String>() {
                        protected void updateItem(final String family, final boolean empty) {
                            super.updateItem((Object)family, empty);
                            if (!empty) {
                                this.setFont(Font.font(family));
                                this.setText(family);
                            }
                            else {
                                this.setText((String)null);
                            }
                        }
                    };
                }
            });
            final ChangeListener<Object> sampleRefreshListener = (ChangeListener<Object>)new ChangeListener<Object>() {
                public void changed(final ObservableValue<?> arg0, final Object arg1, final Object arg2) {
                    FontPanel.this.refreshSample();
                }
            };
            ((MultipleSelectionModel)this.fontListView.selectionModelProperty().get()).selectedItemProperty().addListener((ChangeListener)new ChangeListener<String>() {
                public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                    final String fontFamily = (String)FontPanel.this.listSelection((javafx.scene.control.ListView<Object>)FontPanel.this.fontListView);
                    FontPanel.this.styleListView.setItems(FXCollections.observableArrayList((Collection)getFontStyles(fontFamily)));
                    FontPanel.this.refreshSample();
                }
            });
            this.add((Node)new Label(Localization.localize(Localization.asKey("font.dlg.style.label"))), 1, 0);
            this.add((Node)this.styleListView, 1, 1);
            ((MultipleSelectionModel)this.styleListView.selectionModelProperty().get()).selectedItemProperty().addListener((ChangeListener)sampleRefreshListener);
            this.add((Node)new Label(Localization.localize(Localization.asKey("font.dlg.size.label"))), 2, 0);
            this.add((Node)this.sizeListView, 2, 1);
            ((MultipleSelectionModel)this.sizeListView.selectionModelProperty().get()).selectedItemProperty().addListener((ChangeListener)sampleRefreshListener);
            final double height = 45.0;
            final DoubleBinding sampleWidth = new DoubleBinding() {
                {
                    this.bind(new Observable[] { FontPanel.this.fontListView.widthProperty(), FontPanel.this.styleListView.widthProperty(), FontPanel.this.sizeListView.widthProperty() });
                }
                
                protected double computeValue() {
                    return FontPanel.this.fontListView.getWidth() + FontPanel.this.styleListView.getWidth() + FontPanel.this.sizeListView.getWidth() + 30.0;
                }
            };
            final StackPane sampleStack = new StackPane(new Node[] { this.sample });
            sampleStack.setAlignment(Pos.CENTER_LEFT);
            sampleStack.setMinHeight(45.0);
            sampleStack.setPrefHeight(45.0);
            sampleStack.setMaxHeight(45.0);
            sampleStack.prefWidthProperty().bind((ObservableValue)sampleWidth);
            final Rectangle clip = new Rectangle(0.0, 45.0);
            clip.widthProperty().bind((ObservableValue)sampleWidth);
            sampleStack.setClip((Node)clip);
            this.add((Node)sampleStack, 0, 3, 1, 3);
        }
        
        public void setFont(final Font font) {
            final Font _font = (font == null) ? Font.getDefault() : font;
            if (_font != null) {
                this.selectInList(this.fontListView, _font.getFamily());
                this.selectInList(this.styleListView, new FontStyle(_font));
                this.selectInList(this.sizeListView, _font.getSize());
            }
        }
        
        public Font getFont() {
            try {
                final FontStyle style = this.listSelection(this.styleListView);
                if (style == null) {
                    return Font.font((String)this.listSelection(this.fontListView), (double)this.listSelection(this.sizeListView));
                }
                return Font.font((String)this.listSelection(this.fontListView), style.getWeight(), style.getPosture(), (double)this.listSelection(this.sizeListView));
            }
            catch (Throwable ex) {
                return null;
            }
        }
        
        private void refreshSample() {
            this.sample.setFont(this.getFont());
        }
        
        private <T> void selectInList(final ListView<T> listView, final T selection) {
            Platform.runLater((Runnable)new Runnable() {
                @Override
                public void run() {
                    listView.scrollTo(selection);
                    listView.getSelectionModel().select(selection);
                }
            });
        }
        
        private <T> T listSelection(final ListView<T> listView) {
            return (T)((MultipleSelectionModel)listView.selectionModelProperty().get()).getSelectedItem();
        }
        
        static {
            MATCH_ALL = new Predicate<Object>() {
                @Override
                public boolean test(final Object t) {
                    return true;
                }
            };
            fontSizes = new Double[] { 8.0, 9.0, 11.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 36.0, 48.0, 72.0 };
        }
    }
}
