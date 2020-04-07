// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.util.StringConverter;
import java.time.LocalDate;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DatePicker;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.util.List;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.NumberFormat;
import java.text.DecimalFormatSymbols;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.control.IndexRange;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.Control;
import java.text.DecimalFormat;

public abstract class SpreadsheetCellEditor
{
    private static final double MAX_EDITOR_HEIGHT = 50.0;
    private static final DecimalFormat decimalFormat;
    SpreadsheetView view;
    
    public SpreadsheetCellEditor(final SpreadsheetView view) {
        this.view = view;
    }
    
    public final void endEdit(final boolean b) {
        this.view.getCellsViewSkin().getSpreadsheetCellEditorImpl().endEdit(b);
    }
    
    public void startEdit(final Object item) {
        this.startEdit(item, null);
    }
    
    public abstract void startEdit(final Object p0, final String p1);
    
    public abstract Control getEditor();
    
    public abstract String getControlValue();
    
    public abstract void end();
    
    public double getMaxHeight() {
        return 50.0;
    }
    
    static {
        decimalFormat = new DecimalFormat("#.##########");
    }
    
    public static class ObjectEditor extends SpreadsheetCellEditor
    {
        private final TextField tf;
        
        public ObjectEditor(final SpreadsheetView view) {
            super(view);
            this.tf = new TextField();
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof String) {
                this.tf.setText(value.toString());
            }
            this.attachEnterEscapeEventHandler();
            this.tf.requestFocus();
            this.tf.end();
        }
        
        @Override
        public String getControlValue() {
            return this.tf.getText();
        }
        
        @Override
        public void end() {
            this.tf.setOnKeyPressed((EventHandler)null);
        }
        
        public TextField getEditor() {
            return this.tf;
        }
        
        private void attachEnterEscapeEventHandler() {
            this.tf.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        ObjectEditor.this.endEdit(true);
                    }
                    else if (t.getCode() == KeyCode.ESCAPE) {
                        ObjectEditor.this.endEdit(false);
                    }
                }
            });
        }
    }
    
    public static class StringEditor extends SpreadsheetCellEditor
    {
        private final TextField tf;
        
        public StringEditor(final SpreadsheetView view) {
            super(view);
            this.tf = new TextField();
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof String || value == null) {
                this.tf.setText((String)value);
            }
            this.attachEnterEscapeEventHandler();
            this.tf.requestFocus();
            this.tf.selectAll();
        }
        
        @Override
        public String getControlValue() {
            return this.tf.getText();
        }
        
        @Override
        public void end() {
            this.tf.setOnKeyPressed((EventHandler)null);
        }
        
        public TextField getEditor() {
            return this.tf;
        }
        
        private void attachEnterEscapeEventHandler() {
            this.tf.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        StringEditor.this.endEdit(true);
                    }
                    else if (t.getCode() == KeyCode.ESCAPE) {
                        StringEditor.this.endEdit(false);
                    }
                }
            });
        }
    }
    
    public static class TextAreaEditor extends SpreadsheetCellEditor
    {
        private final TextArea textArea;
        
        public TextAreaEditor(final SpreadsheetView view) {
            super(view);
            (this.textArea = new TextArea()).setWrapText(true);
            this.textArea.minHeightProperty().bind((ObservableValue)this.textArea.maxHeightProperty());
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof String || value == null) {
                this.textArea.setText((String)value);
            }
            this.attachEnterEscapeEventHandler();
            this.textArea.requestFocus();
            this.textArea.selectAll();
        }
        
        @Override
        public String getControlValue() {
            return this.textArea.getText();
        }
        
        @Override
        public void end() {
            this.textArea.setOnKeyPressed((EventHandler)null);
        }
        
        public TextArea getEditor() {
            return this.textArea;
        }
        
        @Override
        public double getMaxHeight() {
            return Double.MAX_VALUE;
        }
        
        private void attachEnterEscapeEventHandler() {
            this.textArea.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        if (keyEvent.isShiftDown()) {
                            TextAreaEditor.this.textArea.replaceSelection("\n");
                        }
                        else {
                            TextAreaEditor.this.endEdit(true);
                        }
                    }
                    else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                        TextAreaEditor.this.endEdit(false);
                    }
                    else if (keyEvent.getCode() == KeyCode.TAB) {
                        if (keyEvent.isShiftDown()) {
                            TextAreaEditor.this.textArea.replaceSelection("\t");
                            keyEvent.consume();
                        }
                        else {
                            TextAreaEditor.this.endEdit(true);
                        }
                    }
                }
            });
        }
    }
    
    public static class DoubleEditor extends SpreadsheetCellEditor
    {
        private final TextField tf;
        
        public DoubleEditor(final SpreadsheetView view) {
            super(view);
            this.tf = new TextField() {
                public void insertText(final int index, final String text) {
                    final String fixedText = this.fixText(text);
                    super.insertText(index, fixedText);
                }
                
                public void replaceText(final int start, final int end, final String text) {
                    final String fixedText = this.fixText(text);
                    super.replaceText(start, end, fixedText);
                }
                
                public void replaceText(final IndexRange range, final String text) {
                    this.replaceText(range.getStart(), range.getEnd(), text);
                }
                
                private String fixText(String text) {
                    final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Localization.getLocale());
                    text = text.replace(' ', ' ');
                    return text.replaceAll("\\.", Character.toString(symbols.getDecimalSeparator()));
                }
            };
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof Double) {
                SpreadsheetCellEditor.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Localization.getLocale()));
                this.tf.setText(((Double)value).isNaN() ? "" : SpreadsheetCellEditor.decimalFormat.format(value));
            }
            else {
                this.tf.setText((String)null);
            }
            this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
            this.attachEnterEscapeEventHandler();
            this.tf.requestFocus();
            this.tf.selectAll();
        }
        
        @Override
        public void end() {
            this.tf.setOnKeyPressed((EventHandler)null);
        }
        
        public TextField getEditor() {
            return this.tf;
        }
        
        @Override
        public String getControlValue() {
            final NumberFormat format = NumberFormat.getInstance(Localization.getLocale());
            final ParsePosition parsePosition = new ParsePosition(0);
            if (this.tf.getText() != null) {
                final Number number = format.parse(this.tf.getText(), parsePosition);
                if (number != null && parsePosition.getIndex() == this.tf.getText().length()) {
                    return String.valueOf(number.doubleValue());
                }
            }
            return this.tf.getText();
        }
        
        private void attachEnterEscapeEventHandler() {
            this.tf.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        try {
                            if (DoubleEditor.this.tf.getText().equals("")) {
                                DoubleEditor.this.endEdit(true);
                            }
                            else {
                                DoubleEditor.this.tryParsing();
                                DoubleEditor.this.endEdit(true);
                            }
                        }
                        catch (Exception ex) {}
                    }
                    else if (t.getCode() == KeyCode.ESCAPE) {
                        DoubleEditor.this.endEdit(false);
                    }
                }
            });
            this.tf.setOnKeyReleased((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    try {
                        if (DoubleEditor.this.tf.getText().equals("")) {
                            DoubleEditor.this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
                        }
                        else {
                            DoubleEditor.this.tryParsing();
                            DoubleEditor.this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
                        }
                    }
                    catch (Exception e) {
                        DoubleEditor.this.tf.getStyleClass().add((Object)"error");
                    }
                }
            });
        }
        
        private void tryParsing() throws ParseException {
            final NumberFormat format = NumberFormat.getNumberInstance(Localization.getLocale());
            final ParsePosition parsePosition = new ParsePosition(0);
            format.parse(this.tf.getText(), parsePosition);
            if (parsePosition.getIndex() != this.tf.getText().length()) {
                throw new ParseException("Invalid input", parsePosition.getIndex());
            }
        }
    }
    
    public static class IntegerEditor extends SpreadsheetCellEditor
    {
        private final TextField tf;
        
        public IntegerEditor(final SpreadsheetView view) {
            super(view);
            this.tf = new TextField();
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof Integer) {
                this.tf.setText(Integer.toString((int)value));
            }
            else {
                this.tf.setText((String)null);
            }
            this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
            this.attachEnterEscapeEventHandler();
            this.tf.requestFocus();
            this.tf.selectAll();
        }
        
        @Override
        public void end() {
            this.tf.setOnKeyPressed((EventHandler)null);
        }
        
        public TextField getEditor() {
            return this.tf;
        }
        
        @Override
        public String getControlValue() {
            return this.tf.getText();
        }
        
        private void attachEnterEscapeEventHandler() {
            this.tf.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        try {
                            if (IntegerEditor.this.tf.getText().equals("")) {
                                IntegerEditor.this.endEdit(true);
                            }
                            else {
                                Integer.parseInt(IntegerEditor.this.tf.getText());
                                IntegerEditor.this.endEdit(true);
                            }
                        }
                        catch (Exception ex) {}
                    }
                    else if (t.getCode() == KeyCode.ESCAPE) {
                        IntegerEditor.this.endEdit(false);
                    }
                }
            });
            this.tf.setOnKeyReleased((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    try {
                        if (IntegerEditor.this.tf.getText().equals("")) {
                            IntegerEditor.this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
                        }
                        else {
                            Integer.parseInt(IntegerEditor.this.tf.getText());
                            IntegerEditor.this.tf.getStyleClass().removeAll((Object[])new String[] { "error" });
                        }
                    }
                    catch (Exception e) {
                        IntegerEditor.this.tf.getStyleClass().add((Object)"error");
                    }
                }
            });
        }
    }
    
    public static class ListEditor<R> extends SpreadsheetCellEditor
    {
        private final List<String> itemList;
        private final ComboBox<String> cb;
        private String originalValue;
        
        public ListEditor(final SpreadsheetView view, final List<String> itemList) {
            super(view);
            this.itemList = itemList;
            (this.cb = (ComboBox<String>)new ComboBox()).setVisibleRowCount(5);
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof String) {
                this.originalValue = value.toString();
            }
            else {
                this.originalValue = null;
            }
            final ObservableList<String> items = (ObservableList<String>)FXCollections.observableList((List)this.itemList);
            this.cb.setItems((ObservableList)items);
            this.cb.setValue((Object)this.originalValue);
            this.attachEnterEscapeEventHandler();
            this.cb.show();
            this.cb.requestFocus();
        }
        
        @Override
        public void end() {
            this.cb.setOnKeyPressed((EventHandler)null);
        }
        
        public ComboBox<String> getEditor() {
            return this.cb;
        }
        
        @Override
        public String getControlValue() {
            return (String)this.cb.getSelectionModel().getSelectedItem();
        }
        
        private void attachEnterEscapeEventHandler() {
            this.cb.setOnKeyPressed((EventHandler)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ESCAPE) {
                        ListEditor.this.cb.setValue((Object)ListEditor.this.originalValue);
                        ListEditor.this.endEdit(false);
                    }
                    else if (t.getCode() == KeyCode.ENTER) {
                        ListEditor.this.endEdit(true);
                    }
                }
            });
        }
    }
    
    public static class DateEditor extends SpreadsheetCellEditor
    {
        private final DatePicker datePicker;
        private EventHandler<KeyEvent> eh;
        private ChangeListener<LocalDate> cl;
        private boolean ending;
        
        public DateEditor(final SpreadsheetView view, final StringConverter<LocalDate> converter) {
            super(view);
            this.ending = false;
            (this.datePicker = new DatePicker()).setConverter((StringConverter)converter);
        }
        
        @Override
        public void startEdit(final Object value, final String format) {
            if (value instanceof LocalDate) {
                this.datePicker.setValue((Object)value);
            }
            this.attachEnterEscapeEventHandler();
            this.datePicker.show();
            this.datePicker.getEditor().requestFocus();
        }
        
        @Override
        public void end() {
            if (this.datePicker.isShowing()) {
                this.datePicker.hide();
            }
            this.datePicker.removeEventFilter(KeyEvent.KEY_PRESSED, (EventHandler)this.eh);
            this.datePicker.valueProperty().removeListener((ChangeListener)this.cl);
        }
        
        public DatePicker getEditor() {
            return this.datePicker;
        }
        
        @Override
        public String getControlValue() {
            return this.datePicker.getEditor().getText();
        }
        
        private void attachEnterEscapeEventHandler() {
            this.eh = (EventHandler<KeyEvent>)new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        DateEditor.this.ending = true;
                        DateEditor.this.endEdit(true);
                        DateEditor.this.ending = false;
                    }
                    else if (t.getCode() == KeyCode.ESCAPE) {
                        DateEditor.this.endEdit(false);
                    }
                }
            };
            this.datePicker.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler)this.eh);
            this.cl = (ChangeListener<LocalDate>)new ChangeListener<LocalDate>() {
                public void changed(final ObservableValue<? extends LocalDate> arg0, final LocalDate arg1, final LocalDate arg2) {
                    if (!DateEditor.this.ending) {
                        DateEditor.this.endEdit(true);
                    }
                }
            };
            this.datePicker.valueProperty().addListener((ChangeListener)this.cl);
        }
    }
}
