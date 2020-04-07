// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.autocompletion;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import java.util.Collection;
import javafx.util.Callback;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;

public class AutoCompletionTextFieldBinding<T> extends AutoCompletionBinding<T>
{
    private StringConverter<T> converter;
    private final ChangeListener<String> textChangeListener;
    private final ChangeListener<Boolean> focusChangedListener;
    
    private static <T> StringConverter<T> defaultStringConverter() {
        return new StringConverter<T>() {
            public String toString(final T t) {
                return (t == null) ? null : t.toString();
            }
            
            public T fromString(final String string) {
                return (T)string;
            }
        };
    }
    
    public AutoCompletionTextFieldBinding(final TextField textField, final Callback<ISuggestionRequest, Collection<T>> suggestionProvider) {
        this(textField, (javafx.util.Callback<ISuggestionRequest, Collection<Object>>)suggestionProvider, defaultStringConverter());
    }
    
    public AutoCompletionTextFieldBinding(final TextField textField, final Callback<ISuggestionRequest, Collection<T>> suggestionProvider, final StringConverter<T> converter) {
        super((Node)textField, suggestionProvider, converter);
        this.textChangeListener = (ChangeListener<String>)new ChangeListener<String>() {
            public void changed(final ObservableValue<? extends String> obs, final String oldText, final String newText) {
                if (AutoCompletionTextFieldBinding.this.getCompletionTarget().isFocused()) {
                    AutoCompletionTextFieldBinding.this.setUserInput(newText);
                }
            }
        };
        this.focusChangedListener = (ChangeListener<Boolean>)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> obs, final Boolean oldFocused, final Boolean newFocused) {
                if (!newFocused) {
                    AutoCompletionTextFieldBinding.this.hidePopup();
                }
            }
        };
        this.converter = converter;
        this.getCompletionTarget().textProperty().addListener((ChangeListener)this.textChangeListener);
        this.getCompletionTarget().focusedProperty().addListener((ChangeListener)this.focusChangedListener);
    }
    
    public TextField getCompletionTarget() {
        return (TextField)super.getCompletionTarget();
    }
    
    @Override
    public void dispose() {
        this.getCompletionTarget().textProperty().removeListener((ChangeListener)this.textChangeListener);
        this.getCompletionTarget().focusedProperty().removeListener((ChangeListener)this.focusChangedListener);
    }
    
    @Override
    protected void completeUserInput(final T completion) {
        final String newText = this.converter.toString((Object)completion);
        this.getCompletionTarget().setText(newText);
        this.getCompletionTarget().positionCaret(newText.length());
    }
}
