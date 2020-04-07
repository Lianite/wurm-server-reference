// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextArea;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.Node;
import javafx.scene.control.Label;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ExceptionDialog extends Dialog<ButtonType>
{
    public ExceptionDialog(final Throwable exception) {
        final DialogPane dialogPane = this.getDialogPane();
        this.setTitle(Localization.getString("exception.dlg.title"));
        dialogPane.setHeaderText(Localization.getString("exception.dlg.header"));
        dialogPane.getStyleClass().add((Object)"exception-dialog");
        dialogPane.getStylesheets().add((Object)ProgressDialog.class.getResource("dialogs.css").toExternalForm());
        dialogPane.getButtonTypes().addAll((Object[])new ButtonType[] { ButtonType.OK });
        final String contentText = this.getContentText();
        dialogPane.setContent((Node)new Label((contentText != null && !contentText.isEmpty()) ? contentText : exception.getMessage()));
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        final String exceptionText = sw.toString();
        final Label label = new Label(Localization.localize(Localization.getString("exception.dlg.label")));
        final TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow((Node)textArea, Priority.ALWAYS);
        GridPane.setHgrow((Node)textArea, Priority.ALWAYS);
        final GridPane root = new GridPane();
        root.setMaxWidth(Double.MAX_VALUE);
        root.add((Node)label, 0, 0);
        root.add((Node)textArea, 0, 1);
        dialogPane.setExpandableContent((Node)root);
    }
}
