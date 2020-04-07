// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.autocompletion;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import java.util.List;
import java.util.Collection;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import javafx.util.Callback;

public abstract class SuggestionProvider<T> implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>>
{
    private final List<T> possibleSuggestions;
    private final Object possibleSuggestionsLock;
    private final BooleanProperty showAllIfEmptyProperty;
    
    public SuggestionProvider() {
        this.possibleSuggestions = new ArrayList<T>();
        this.possibleSuggestionsLock = new Object();
        this.showAllIfEmptyProperty = (BooleanProperty)new SimpleBooleanProperty(false);
    }
    
    public final BooleanProperty showAllIfEmptyProperty() {
        return this.showAllIfEmptyProperty;
    }
    
    public final boolean isShowAllIfEmpty() {
        return this.showAllIfEmptyProperty.get();
    }
    
    public final void setShowAllIfEmpty(final boolean showAllIfEmpty) {
        this.showAllIfEmptyProperty.set(showAllIfEmpty);
    }
    
    public void addPossibleSuggestions(final T... newPossible) {
        this.addPossibleSuggestions(Arrays.asList(newPossible));
    }
    
    public void addPossibleSuggestions(final Collection<T> newPossible) {
        synchronized (this.possibleSuggestionsLock) {
            this.possibleSuggestions.addAll((Collection<? extends T>)newPossible);
        }
    }
    
    public void clearSuggestions() {
        synchronized (this.possibleSuggestionsLock) {
            this.possibleSuggestions.clear();
        }
    }
    
    public final Collection<T> call(final AutoCompletionBinding.ISuggestionRequest request) {
        final List<T> suggestions = new ArrayList<T>();
        if (!request.getUserText().isEmpty()) {
            synchronized (this.possibleSuggestionsLock) {
                for (final T possibleSuggestion : this.possibleSuggestions) {
                    if (this.isMatch(possibleSuggestion, request)) {
                        suggestions.add(possibleSuggestion);
                    }
                }
            }
            Collections.sort(suggestions, this.getComparator());
        }
        else if (this.isShowAllIfEmpty()) {
            synchronized (this.possibleSuggestionsLock) {
                suggestions.addAll((Collection<? extends T>)this.possibleSuggestions);
            }
        }
        return suggestions;
    }
    
    protected abstract Comparator<T> getComparator();
    
    protected abstract boolean isMatch(final T p0, final AutoCompletionBinding.ISuggestionRequest p1);
    
    public static <T> SuggestionProvider<T> create(final Collection<T> possibleSuggestions) {
        return create(null, possibleSuggestions);
    }
    
    public static <T> SuggestionProvider<T> create(final Callback<T, String> stringConverter, final Collection<T> possibleSuggestions) {
        final SuggestionProviderString<T> suggestionProvider = new SuggestionProviderString<T>(stringConverter);
        suggestionProvider.addPossibleSuggestions(possibleSuggestions);
        return suggestionProvider;
    }
    
    private static class SuggestionProviderString<T> extends SuggestionProvider<T>
    {
        private Callback<T, String> stringConverter;
        private final Comparator<T> stringComparator;
        
        public SuggestionProviderString(final Callback<T, String> stringConverter) {
            this.stringComparator = new Comparator<T>() {
                @Override
                public int compare(final T o1, final T o2) {
                    final String o1str = (String)SuggestionProviderString.this.stringConverter.call((Object)o1);
                    final String o2str = (String)SuggestionProviderString.this.stringConverter.call((Object)o2);
                    return o1str.compareTo(o2str);
                }
            };
            this.stringConverter = stringConverter;
            if (this.stringConverter == null) {
                this.stringConverter = (Callback<T, String>)new Callback<T, String>() {
                    public String call(final T obj) {
                        return (obj != null) ? obj.toString() : "";
                    }
                };
            }
        }
        
        @Override
        protected Comparator<T> getComparator() {
            return this.stringComparator;
        }
        
        @Override
        protected boolean isMatch(final T suggestion, final AutoCompletionBinding.ISuggestionRequest request) {
            final String userTextLower = request.getUserText().toLowerCase();
            final String suggestionStr = ((String)this.stringConverter.call((Object)suggestion)).toLowerCase();
            return suggestionStr.contains(userTextLower);
        }
    }
}
