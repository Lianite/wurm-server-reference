// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table;

import java.lang.reflect.Field;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.Skin;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.Control;
import javafx.scene.control.ContextMenu;
import java.util.function.Consumer;
import javafx.scene.control.TableView;

final class TableViewUtils
{
    public static void modifyTableMenu(final TableView<?> tableView, final Consumer<ContextMenu> consumer) {
        modifyTableMenu((Control)tableView, consumer);
    }
    
    public static void modifyTableMenu(final TreeTableView<?> treeTableView, final Consumer<ContextMenu> consumer) {
        modifyTableMenu((Control)treeTableView, consumer);
    }
    
    private static void modifyTableMenu(final Control control, final Consumer<ContextMenu> consumer) {
        if (control.getScene() == null) {
            control.sceneProperty().addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable o) {
                    control.sceneProperty().removeListener((InvalidationListener)this);
                    modifyTableMenu(control, consumer);
                }
            });
            return;
        }
        final Skin<?> skin = (Skin<?>)control.getSkin();
        if (skin == null) {
            control.skinProperty().addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable o) {
                    control.skinProperty().removeListener((InvalidationListener)this);
                    modifyTableMenu(control, consumer);
                }
            });
            return;
        }
        doModify(skin, consumer);
    }
    
    private static void doModify(final Skin<?> skin, final Consumer<ContextMenu> consumer) {
        if (!(skin instanceof TableViewSkinBase)) {
            return;
        }
        final TableViewSkin<?> tableSkin = (TableViewSkin<?>)skin;
        final TableHeaderRow headerRow = getHeaderRow(tableSkin);
        if (headerRow == null) {
            return;
        }
        final ContextMenu contextMenu = getContextMenu(headerRow);
        consumer.accept(contextMenu);
    }
    
    private static TableHeaderRow getHeaderRow(final TableViewSkin<?> tableSkin) {
        final ObservableList<Node> children = (ObservableList<Node>)tableSkin.getChildren();
        for (int i = 0, max = children.size(); i < max; ++i) {
            final Node child = (Node)children.get(i);
            if (child instanceof TableHeaderRow) {
                return (TableHeaderRow)child;
            }
        }
        return null;
    }
    
    private static ContextMenu getContextMenu(final TableHeaderRow headerRow) {
        try {
            final Field privateContextMenuField = TableHeaderRow.class.getDeclaredField("columnPopupMenu");
            privateContextMenuField.setAccessible(true);
            final ContextMenu contextMenu = (ContextMenu)privateContextMenuField.get(headerRow);
            return contextMenu;
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        catch (IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
        catch (NoSuchFieldException ex3) {
            ex3.printStackTrace();
        }
        catch (SecurityException ex4) {
            ex4.printStackTrace();
        }
        return null;
    }
}
