// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Region;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import impl.org.controlsfx.i18n.Localization;
import javafx.concurrent.Worker;
import javafx.scene.control.Dialog;

public class ProgressDialog extends Dialog<Void>
{
    public ProgressDialog(final Worker<?> worker) {
        if (worker != null && (worker.getState() == Worker.State.CANCELLED || worker.getState() == Worker.State.FAILED || worker.getState() == Worker.State.SUCCEEDED)) {
            return;
        }
        this.setResultConverter(dialogButton -> null);
        final DialogPane dialogPane = this.getDialogPane();
        this.setTitle(Localization.getString("progress.dlg.title"));
        dialogPane.setHeaderText(Localization.getString("progress.dlg.header"));
        dialogPane.getStyleClass().add((Object)"progress-dialog");
        dialogPane.getStylesheets().add((Object)ProgressDialog.class.getResource("dialogs.css").toExternalForm());
        final Label progressMessage = new Label();
        progressMessage.textProperty().bind((ObservableValue)worker.messageProperty());
        final WorkerProgressPane content = new WorkerProgressPane(this);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setWorker(worker);
        final VBox vbox = new VBox(10.0, new Node[] { progressMessage, content });
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setPrefSize(300.0, 100.0);
        final Label contentText = new Label();
        contentText.setWrapText(true);
        vbox.getChildren().add(0, (Object)contentText);
        contentText.textProperty().bind((ObservableValue)dialogPane.contentTextProperty());
        dialogPane.setContent((Node)vbox);
    }
    
    private static class WorkerProgressPane extends Region
    {
        private Worker<?> worker;
        private boolean dialogVisible;
        private boolean cancelDialogShow;
        private ChangeListener<Worker.State> stateListener;
        private final ProgressDialog dialog;
        private final ProgressBar progressBar;
        
        public final void setWorker(final Worker<?> newWorker) {
            if (newWorker != this.worker) {
                if (this.worker != null) {
                    this.worker.stateProperty().removeListener((ChangeListener)this.stateListener);
                    this.end();
                }
                if ((this.worker = newWorker) != null) {
                    newWorker.stateProperty().addListener((ChangeListener)this.stateListener);
                    if (newWorker.getState() == Worker.State.RUNNING || newWorker.getState() == Worker.State.SCHEDULED) {
                        this.begin();
                    }
                }
            }
        }
        
        public WorkerProgressPane(final ProgressDialog dialog) {
            this.dialogVisible = false;
            this.cancelDialogShow = false;
            this.stateListener = (ChangeListener<Worker.State>)new ChangeListener<Worker.State>() {
                public void changed(final ObservableValue<? extends Worker.State> observable, final Worker.State old, final Worker.State value) {
                    switch (value) {
                        case CANCELLED:
                        case FAILED:
                        case SUCCEEDED: {
                            if (!WorkerProgressPane.this.dialogVisible) {
                                WorkerProgressPane.this.cancelDialogShow = true;
                                WorkerProgressPane.this.end();
                                break;
                            }
                            if (old == Worker.State.SCHEDULED || old == Worker.State.RUNNING) {
                                WorkerProgressPane.this.end();
                                break;
                            }
                            break;
                        }
                        case SCHEDULED: {
                            WorkerProgressPane.this.begin();
                            break;
                        }
                    }
                }
            };
            this.dialog = dialog;
            (this.progressBar = new ProgressBar()).setMaxWidth(Double.MAX_VALUE);
            this.getChildren().add((Object)this.progressBar);
            if (this.worker != null) {
                this.progressBar.progressProperty().bind((ObservableValue)this.worker.progressProperty());
            }
        }
        
        private void begin() {
            this.cancelDialogShow = false;
            Platform.runLater(() -> {
                if (!this.cancelDialogShow) {
                    this.progressBar.progressProperty().bind((ObservableValue)this.worker.progressProperty());
                    this.dialogVisible = true;
                    this.dialog.show();
                }
            });
        }
        
        private void end() {
            this.progressBar.progressProperty().unbind();
            this.dialogVisible = false;
            DialogUtils.forcefullyHideDialog(this.dialog);
        }
        
        protected void layoutChildren() {
            if (this.progressBar != null) {
                final Insets insets = this.getInsets();
                final double w = this.getWidth() - insets.getLeft() - insets.getRight();
                final double h = this.getHeight() - insets.getTop() - insets.getBottom();
                final double prefH = this.progressBar.prefHeight(-1.0);
                final double x = insets.getLeft() + (w - w) / 2.0;
                final double y = insets.getTop() + (h - prefH) / 2.0;
                this.progressBar.resizeRelocate(x, y, w, prefH);
            }
        }
    }
}
