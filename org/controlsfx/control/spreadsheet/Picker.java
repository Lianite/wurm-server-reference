// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Picker
{
    private final ObservableList<String> styleClass;
    
    public Picker() {
        this(new String[] { "picker-label" });
    }
    
    public Picker(final String... styleClass) {
        (this.styleClass = (ObservableList<String>)FXCollections.observableArrayList()).addAll((Object[])styleClass);
    }
    
    public Picker(final Collection<String> styleClass) {
        (this.styleClass = (ObservableList<String>)FXCollections.observableArrayList()).addAll((Collection)styleClass);
    }
    
    public final ObservableList<String> getStyleClass() {
        return this.styleClass;
    }
    
    public abstract void onClick();
}
