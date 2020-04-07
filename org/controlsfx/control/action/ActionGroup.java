// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import javafx.scene.Node;
import javafx.collections.FXCollections;
import java.util.Collection;
import java.util.Arrays;
import javafx.collections.ObservableList;

public class ActionGroup extends Action
{
    private final ObservableList<Action> actions;
    
    public ActionGroup(final String text, final Action... actions) {
        this(text, Arrays.asList(actions));
    }
    
    public ActionGroup(final String text, final Collection<Action> actions) {
        super(text);
        this.actions = (ObservableList<Action>)FXCollections.observableArrayList();
        this.getActions().addAll((Collection)actions);
    }
    
    public ActionGroup(final String text, final Node icon, final Action... actions) {
        this(text, icon, Arrays.asList(actions));
    }
    
    public ActionGroup(final String text, final Node icon, final Collection<Action> actions) {
        super(text);
        this.actions = (ObservableList<Action>)FXCollections.observableArrayList();
        this.setGraphic(icon);
        this.getActions().addAll((Collection)actions);
    }
    
    public final ObservableList<Action> getActions() {
        return this.actions;
    }
    
    @Override
    public String toString() {
        return this.getText();
    }
}
