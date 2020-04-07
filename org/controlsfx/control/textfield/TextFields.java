// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.textfield;

import javafx.scene.input.MouseEvent;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.util.Arrays;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.util.StringConverter;
import java.util.Collection;
import javafx.util.Callback;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class TextFields
{
    private static final Duration FADE_DURATION;
    
    public static TextField createClearableTextField() {
        final CustomTextField inputField = new CustomTextField();
        setupClearButtonField(inputField, inputField.rightProperty());
        return inputField;
    }
    
    public static PasswordField createClearablePasswordField() {
        final CustomPasswordField inputField = new CustomPasswordField();
        setupClearButtonField((TextField)inputField, inputField.rightProperty());
        return inputField;
    }
    
    private static void setupClearButtonField(final TextField inputField, final ObjectProperty<Node> rightProperty) {
        inputField.getStyleClass().add((Object)"clearable-field");
        final Region clearButton = new Region();
        clearButton.getStyleClass().addAll((Object[])new String[] { "graphic" });
        final StackPane clearButtonPane = new StackPane(new Node[] { clearButton });
        clearButtonPane.getStyleClass().addAll((Object[])new String[] { "clear-button" });
        clearButtonPane.setOpacity(0.0);
        clearButtonPane.setCursor(Cursor.DEFAULT);
        clearButtonPane.setOnMouseReleased(e -> inputField.clear());
        clearButtonPane.managedProperty().bind((ObservableValue)inputField.editableProperty());
        clearButtonPane.visibleProperty().bind((ObservableValue)inputField.editableProperty());
        rightProperty.set((Object)clearButtonPane);
        final FadeTransition fader = new FadeTransition(TextFields.FADE_DURATION, (Node)clearButtonPane);
        fader.setCycleCount(1);
        inputField.textProperty().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable arg0) {
                final String text = inputField.getText();
                final boolean isTextEmpty = text == null || text.isEmpty();
                final boolean isButtonVisible = fader.getNode().getOpacity() > 0.0;
                if (isTextEmpty && isButtonVisible) {
                    this.setButtonVisible(false);
                }
                else if (!isTextEmpty && !isButtonVisible) {
                    this.setButtonVisible(true);
                }
            }
            
            private void setButtonVisible(final boolean visible) {
                fader.setFromValue(visible ? 0.0 : 1.0);
                fader.setToValue(visible ? 1.0 : 0.0);
                fader.play();
            }
        });
    }
    
    public static <T> AutoCompletionBinding<T> bindAutoCompletion(final TextField textField, final Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider, final StringConverter<T> converter) {
        return new AutoCompletionTextFieldBinding<T>(textField, suggestionProvider, converter);
    }
    
    public static <T> AutoCompletionBinding<T> bindAutoCompletion(final TextField textField, final Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> suggestionProvider) {
        return new AutoCompletionTextFieldBinding<T>(textField, suggestionProvider);
    }
    
    public static <T> AutoCompletionBinding<T> bindAutoCompletion(final TextField textField, final T... possibleSuggestions) {
        return bindAutoCompletion(textField, Arrays.asList(possibleSuggestions));
    }
    
    public static <T> AutoCompletionBinding<T> bindAutoCompletion(final TextField textField, final Collection<T> possibleSuggestions) {
        return new AutoCompletionTextFieldBinding<T>(textField, (javafx.util.Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>>)SuggestionProvider.create(possibleSuggestions));
    }
    
    static {
        FADE_DURATION = Duration.millis(350.0);
    }
}
