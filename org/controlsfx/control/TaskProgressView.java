// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.skin.TaskProgressViewSkin;
import javafx.scene.control.Skin;
import java.util.Iterator;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class TaskProgressView<T extends Task<?>> extends ControlsFXControl
{
    private final ObservableList<T> tasks;
    private ObjectProperty<Callback<T, Node>> graphicFactory;
    
    public TaskProgressView() {
        this.tasks = (ObservableList<T>)FXCollections.observableArrayList();
        this.getStyleClass().add((Object)"task-progress-view");
        final EventHandler<WorkerStateEvent> taskHandler = (EventHandler<WorkerStateEvent>)(evt -> {
            if (evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_SUCCEEDED) || evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_CANCELLED) || evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_FAILED)) {
                this.getTasks().remove((Object)evt.getSource());
            }
        });
        this.getTasks().addListener((ListChangeListener)new ListChangeListener<Task<?>>() {
            public void onChanged(final ListChangeListener.Change<? extends Task<?>> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (final Task<?> task : c.getAddedSubList()) {
                            task.addEventHandler(WorkerStateEvent.ANY, taskHandler);
                        }
                    }
                    else {
                        if (!c.wasRemoved()) {
                            continue;
                        }
                        for (final Task<?> task : c.getRemoved()) {
                            task.removeEventHandler(WorkerStateEvent.ANY, taskHandler);
                        }
                    }
                }
            }
        });
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(TaskProgressView.class, "taskprogressview.css");
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new TaskProgressViewSkin((TaskProgressView<Task>)this);
    }
    
    public final ObservableList<T> getTasks() {
        return this.tasks;
    }
    
    public final ObjectProperty<Callback<T, Node>> graphicFactoryProperty() {
        if (this.graphicFactory == null) {
            this.graphicFactory = (ObjectProperty<Callback<T, Node>>)new SimpleObjectProperty((Object)this, "graphicFactory");
        }
        return this.graphicFactory;
    }
    
    public final Callback<T, Node> getGraphicFactory() {
        return (Callback<T, Node>)((this.graphicFactory == null) ? null : ((Callback)this.graphicFactory.get()));
    }
    
    public final void setGraphicFactory(final Callback<T, Node> factory) {
        this.graphicFactoryProperty().set((Object)factory);
    }
}
