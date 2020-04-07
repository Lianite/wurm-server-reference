// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import java.util.Iterator;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

class DialogUtils
{
    static void forcefullyHideDialog(final Dialog<?> dialog) {
        final DialogPane dialogPane = dialog.getDialogPane();
        if (containsCancelButton(dialog)) {
            dialog.hide();
            return;
        }
        dialogPane.getButtonTypes().add((Object)ButtonType.CANCEL);
        dialog.hide();
        dialogPane.getButtonTypes().remove((Object)ButtonType.CANCEL);
    }
    
    static boolean containsCancelButton(final Dialog<?> dialog) {
        final DialogPane dialogPane = dialog.getDialogPane();
        for (final ButtonType type : dialogPane.getButtonTypes()) {
            if (type.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                return true;
            }
        }
        return false;
    }
}
