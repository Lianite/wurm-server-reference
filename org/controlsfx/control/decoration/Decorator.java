// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.decoration;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.scene.Parent;
import impl.org.controlsfx.ImplUtils;
import java.util.LinkedList;
import javafx.collections.ObservableList;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import impl.org.controlsfx.skin.DecorationPane;
import java.util.function.Consumer;
import java.util.Map;
import javafx.scene.Scene;
import java.util.List;

public class Decorator
{
    private static final String DECORATIONS_PROPERTY_KEY = "$org.controlsfx.decorations$";
    private static List<Scene> currentlyInstallingScenes;
    private static Map<Scene, List<Consumer<DecorationPane>>> pendingTasksByScene;
    
    public static final void addDecoration(final Node target, final Decoration decoration) {
        getDecorations(target, true).add((Object)decoration);
        updateDecorationsOnNode(target, (List<Decoration>)FXCollections.observableArrayList((Object[])new Decoration[] { decoration }), null);
    }
    
    public static final void removeDecoration(final Node target, final Decoration decoration) {
        getDecorations(target, true).remove((Object)decoration);
        updateDecorationsOnNode(target, null, (List<Decoration>)FXCollections.observableArrayList((Object[])new Decoration[] { decoration }));
    }
    
    public static final void removeAllDecorations(final Node target) {
        final List<Decoration> decorations = (List<Decoration>)getDecorations(target, true);
        final List<Decoration> removed = (List<Decoration>)FXCollections.observableArrayList((Collection)decorations);
        target.getProperties().remove((Object)"$org.controlsfx.decorations$");
        updateDecorationsOnNode(target, null, removed);
    }
    
    public static final ObservableList<Decoration> getDecorations(final Node target) {
        return getDecorations(target, false);
    }
    
    private static final ObservableList<Decoration> getDecorations(final Node target, final boolean createIfAbsent) {
        ObservableList<Decoration> decorations = (ObservableList<Decoration>)target.getProperties().get((Object)"$org.controlsfx.decorations$");
        if (decorations == null && createIfAbsent) {
            decorations = (ObservableList<Decoration>)FXCollections.observableArrayList();
            target.getProperties().put((Object)"$org.controlsfx.decorations$", (Object)decorations);
        }
        return decorations;
    }
    
    private static void updateDecorationsOnNode(final Node target, final List<Decoration> added, final List<Decoration> removed) {
        getDecorationPane(target, pane -> pane.updateDecorationsOnNode(target, added, removed));
    }
    
    private static void getDecorationPane(final Node target, final Consumer<DecorationPane> task) {
        final DecorationPane pane = getDecorationPaneInParentHierarchy(target);
        if (pane != null) {
            task.accept(pane);
        }
        else {
            List<Consumer<DecorationPane>> pendingTasks;
            DecorationPane _pane;
            final Node oldRoot;
            final List<Consumer<DecorationPane>> pendingTasks2;
            final Iterator<Consumer<DecorationPane>> iterator;
            Consumer<DecorationPane> pendingTask;
            final Consumer<Scene> sceneConsumer = scene -> {
                if (Decorator.currentlyInstallingScenes.contains(scene)) {
                    pendingTasks = Decorator.pendingTasksByScene.get(scene);
                    if (pendingTasks == null) {
                        pendingTasks = new LinkedList<Consumer<DecorationPane>>();
                        Decorator.pendingTasksByScene.put(scene, pendingTasks);
                    }
                    pendingTasks.add(task);
                    return;
                }
                else {
                    _pane = getDecorationPaneInParentHierarchy(target);
                    if (_pane == null) {
                        Decorator.currentlyInstallingScenes.add(scene);
                        _pane = new DecorationPane();
                        oldRoot = (Node)scene.getRoot();
                        ImplUtils.injectAsRootPane(scene, (Parent)_pane, true);
                        _pane.setRoot(oldRoot);
                        Decorator.currentlyInstallingScenes.remove(scene);
                    }
                    task.accept(_pane);
                    pendingTasks2 = Decorator.pendingTasksByScene.remove(scene);
                    if (pendingTasks2 != null) {
                        pendingTasks2.iterator();
                        while (iterator.hasNext()) {
                            pendingTask = iterator.next();
                            pendingTask.accept(_pane);
                        }
                    }
                    return;
                }
            };
            final Scene scene2 = target.getScene();
            if (scene2 != null) {
                sceneConsumer.accept(scene2);
            }
            else {
                final InvalidationListener sceneListener = (InvalidationListener)new InvalidationListener() {
                    public void invalidated(final Observable o) {
                        if (target.getScene() != null) {
                            target.sceneProperty().removeListener((InvalidationListener)this);
                            sceneConsumer.accept(target.getScene());
                        }
                    }
                };
                target.sceneProperty().addListener(sceneListener);
            }
        }
    }
    
    private static DecorationPane getDecorationPaneInParentHierarchy(final Node target) {
        for (Parent p = target.getParent(); p != null; p = p.getParent()) {
            if (p instanceof DecorationPane) {
                return (DecorationPane)p;
            }
        }
        return null;
    }
    
    static {
        Decorator.currentlyInstallingScenes = new ArrayList<Scene>();
        Decorator.pendingTasksByScene = new HashMap<Scene, List<Consumer<DecorationPane>>>();
    }
}
