// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.event.Event;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import javafx.scene.control.ListView;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.PrefixSelectionComboBox;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.Objects;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import java.util.Optional;
import javafx.scene.control.ComboBox;
import java.util.function.BiFunction;

public class PrefixSelectionCustomizer
{
    public static final int DEFAULT_TYPING_DELAY = 500;
    private static final String SELECTION_PREFIX_STRING = "selectionPrefixString";
    private static final Object SELECTION_PREFIX_TASK;
    public static final BiFunction<ComboBox, String, Optional> DEFAULT_LOOKUP_COMBOBOX;
    public static final BiFunction<ChoiceBox, String, Optional> DEFAULT_LOOKUP_CHOICEBOX;
    private static EventHandler<KeyEvent> handler;
    
    public static void customize(final ComboBox<?> comboBox) {
        if (!comboBox.isEditable()) {
            comboBox.addEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)PrefixSelectionCustomizer.handler);
        }
        comboBox.editableProperty().addListener((o, oV, nV) -> {
            if (!nV) {
                comboBox.addEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)PrefixSelectionCustomizer.handler);
            }
            else {
                comboBox.removeEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)PrefixSelectionCustomizer.handler);
            }
        });
    }
    
    public static void customize(final ChoiceBox<?> choiceBox) {
        choiceBox.addEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)PrefixSelectionCustomizer.handler);
    }
    
    static {
        SELECTION_PREFIX_TASK = "selectionPrefixTask";
        final String s;
        DEFAULT_LOOKUP_COMBOBOX = ((comboBox, selection) -> {
            if (comboBox == null || selection == null || selection.isEmpty()) {
                return Optional.empty();
            }
            else {
                return comboBox.getItems().stream().filter(Objects::nonNull).filter(item -> {
                    s = ((comboBox.getConverter() == null) ? item.toString() : comboBox.getConverter().toString(item));
                    return s != null && !s.isEmpty() && s.toUpperCase(Locale.ROOT).startsWith(selection);
                }).findFirst();
            }
        });
        final String s2;
        DEFAULT_LOOKUP_CHOICEBOX = ((choiceBox, selection) -> {
            if (choiceBox == null || selection == null || selection.isEmpty()) {
                return Optional.empty();
            }
            else {
                return choiceBox.getItems().stream().filter(Objects::nonNull).filter(item -> {
                    s2 = ((choiceBox.getConverter() == null) ? item.toString() : choiceBox.getConverter().toString(item));
                    return s2 != null && !s2.isEmpty() && s2.toUpperCase(Locale.ROOT).startsWith(selection);
                }).findFirst();
            }
        });
        PrefixSelectionCustomizer.handler = (EventHandler<KeyEvent>)new EventHandler<KeyEvent>() {
            private ScheduledExecutorService executorService = null;
            private PrefixSelectionComboBox prefixSelectionComboBox;
            private int typingDelay;
            private Object result;
            
            public void handle(final KeyEvent event) {
                this.keyPressed(event);
            }
            
            private <T> void keyPressed(final KeyEvent event) {
                final KeyCode code = event.getCode();
                if (code.isLetterKey() || code.isDigitKey() || code == KeyCode.SPACE || code == KeyCode.BACK_SPACE) {
                    if (event.getSource() instanceof PrefixSelectionComboBox && code == KeyCode.BACK_SPACE && !((PrefixSelectionComboBox)event.getSource()).isBackSpaceAllowed()) {
                        return;
                    }
                    final String letter = code.impl_getChar();
                    if (event.getSource() instanceof ComboBox) {
                        final ComboBox<T> comboBox = (ComboBox<T>)event.getSource();
                        final T item = this.getEntryWithKey(letter, (Control)comboBox);
                        if (item != null) {
                            comboBox.setValue((Object)item);
                            final ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>)comboBox.getSkin();
                            ((ListView)skin.getPopupContent()).scrollTo((Object)item);
                        }
                    }
                    else if (event.getSource() instanceof ChoiceBox) {
                        final ChoiceBox<T> choiceBox = (ChoiceBox<T>)event.getSource();
                        final T item = this.getEntryWithKey(letter, (Control)choiceBox);
                        if (item != null) {
                            choiceBox.setValue((Object)item);
                        }
                    }
                }
            }
            
            private <T> T getEntryWithKey(final String letter, final Control control) {
                this.result = null;
                this.typingDelay = 500;
                this.prefixSelectionComboBox = ((control instanceof PrefixSelectionComboBox) ? control : null);
                final String selectionPrefixString = this.processInput((String)control.getProperties().get((Object)"selectionPrefixString"), letter);
                control.getProperties().put((Object)"selectionPrefixString", (Object)selectionPrefixString);
                if (this.prefixSelectionComboBox != null) {
                    this.typingDelay = this.prefixSelectionComboBox.getTypingDelay();
                    final BiFunction<ComboBox, String, Optional> lookup = (BiFunction<ComboBox, String, Optional>)this.prefixSelectionComboBox.getLookup();
                    if (lookup != null) {
                        lookup.apply(this.prefixSelectionComboBox, selectionPrefixString).ifPresent(t -> this.result = t);
                    }
                }
                else if (control instanceof ComboBox) {
                    PrefixSelectionCustomizer.DEFAULT_LOOKUP_COMBOBOX.apply((ComboBox)control, selectionPrefixString).ifPresent(t -> this.result = t);
                }
                else if (control instanceof ChoiceBox) {
                    PrefixSelectionCustomizer.DEFAULT_LOOKUP_CHOICEBOX.apply((ChoiceBox)control, selectionPrefixString).ifPresent(t -> this.result = t);
                }
                ScheduledFuture<?> task = (ScheduledFuture<?>)control.getProperties().get(PrefixSelectionCustomizer.SELECTION_PREFIX_TASK);
                if (task != null) {
                    task.cancel(false);
                }
                task = this.getExecutorService().schedule(() -> control.getProperties().put((Object)"selectionPrefixString", (Object)""), this.typingDelay, TimeUnit.MILLISECONDS);
                control.getProperties().put(PrefixSelectionCustomizer.SELECTION_PREFIX_TASK, (Object)task);
                return (T)this.result;
            }
            
            private ScheduledExecutorService getExecutorService() {
                if (this.executorService == null) {
                    final Thread result;
                    this.executorService = Executors.newScheduledThreadPool(1, runnabble -> {
                        result = new Thread(runnabble);
                        result.setDaemon(true);
                        return result;
                    });
                }
                return this.executorService;
            }
            
            private String processInput(String initialText, final String letter) {
                if (initialText == null) {
                    initialText = "";
                }
                final StringBuilder sb = new StringBuilder();
                for (final char c : initialText.concat(letter).toCharArray()) {
                    if (c == '\b') {
                        if (sb.length() > 0) {
                            sb.delete(0, sb.length());
                            break;
                        }
                    }
                    else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }
        };
    }
}
