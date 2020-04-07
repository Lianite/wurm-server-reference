// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx;

import java.lang.reflect.Method;
import javafx.scene.control.Skin;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Control;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ImplUtils
{
    public static void injectAsRootPane(final Scene scene, final Parent injectedParent, final boolean useReflection) {
        final Parent originalParent = scene.getRoot();
        scene.setRoot(injectedParent);
        if (originalParent != null) {
            getChildren(injectedParent, useReflection).add(0, (Node)originalParent);
            injectedParent.getProperties().putAll((Map)originalParent.getProperties());
        }
    }
    
    public static void injectPane(final Parent parent, final Parent injectedParent, final boolean useReflection) {
        if (parent == null) {
            throw new IllegalArgumentException("parent can not be null");
        }
        final List<Node> ownerParentChildren = getChildren(parent.getParent(), useReflection);
        final int ownerPos = ownerParentChildren.indexOf(parent);
        ownerParentChildren.remove(ownerPos);
        ownerParentChildren.add(ownerPos, (Node)injectedParent);
        getChildren(injectedParent, useReflection).add(0, (Node)parent);
        injectedParent.getProperties().putAll((Map)parent.getProperties());
    }
    
    public static void stripRootPane(final Scene scene, final Parent originalParent, final boolean useReflection) {
        final Parent oldParent = scene.getRoot();
        getChildren(oldParent, useReflection).remove(originalParent);
        originalParent.getStyleClass().remove((Object)"root");
        scene.setRoot(originalParent);
    }
    
    public static List<Node> getChildren(final Node n, final boolean useReflection) {
        return (n instanceof Parent) ? getChildren((Parent)n, useReflection) : Collections.emptyList();
    }
    
    public static List<Node> getChildren(final Parent p, final boolean useReflection) {
        ObservableList<Node> children = null;
        if (p instanceof Pane) {
            children = (ObservableList<Node>)((Pane)p).getChildren();
        }
        else if (p instanceof Group) {
            children = (ObservableList<Node>)((Group)p).getChildren();
        }
        else if (p instanceof Control) {
            final Control c = (Control)p;
            final Skin<?> s = (Skin<?>)c.getSkin();
            children = ((s instanceof SkinBase) ? ((SkinBase)s).getChildren() : getChildrenReflectively(p));
        }
        else if (useReflection) {
            children = getChildrenReflectively(p);
        }
        if (children == null) {
            throw new RuntimeException("Unable to get children for Parent of type " + p.getClass() + ". useReflection is set to " + useReflection);
        }
        return (List<Node>)((children == null) ? FXCollections.emptyObservableList() : children);
    }
    
    public static ObservableList<Node> getChildrenReflectively(final Parent p) {
        ObservableList<Node> children = null;
        try {
            final Method getChildrenMethod = Parent.class.getDeclaredMethod("getChildren", (Class<?>[])new Class[0]);
            if (getChildrenMethod != null) {
                if (!getChildrenMethod.isAccessible()) {
                    getChildrenMethod.setAccessible(true);
                }
                children = (ObservableList<Node>)getChildrenMethod.invoke(p, new Object[0]);
            }
        }
        catch (ReflectiveOperationException | IllegalArgumentException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new RuntimeException("Unable to get children for Parent of type " + p.getClass(), e);
        }
        return children;
    }
}
