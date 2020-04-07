// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.beans.value.ObservableValue;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import java.util.HashMap;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import java.util.Iterator;
import javafx.scene.control.Button;
import java.util.List;
import java.util.Arrays;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import java.util.Map;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class CommandLinksDialog extends Dialog<ButtonType>
{
    private static final int gapSize = 10;
    private final Map<ButtonType, CommandLinksButtonType> typeMap;
    private Label contentTextLabel;
    private GridPane grid;
    
    public CommandLinksDialog(final CommandLinksButtonType... links) {
        this(Arrays.asList(links));
    }
    
    public CommandLinksDialog(final List<CommandLinksButtonType> links) {
        (this.grid = new GridPane() {
            protected double computePrefWidth(final double height) {
                boolean isDefault = true;
                double pw = 0.0;
                for (final ButtonType buttonType : CommandLinksDialog.this.getDialogPane().getButtonTypes()) {
                    final Button button = (Button)CommandLinksDialog.this.getDialogPane().lookupButton(buttonType);
                    final double buttonPrefWidth = button.getGraphic().prefWidth(-1.0);
                    if (isDefault) {
                        pw = buttonPrefWidth;
                        isDefault = false;
                    }
                    else {
                        pw = Math.min(pw, buttonPrefWidth);
                    }
                }
                return pw + 10.0;
            }
            
            protected double computePrefHeight(final double width) {
                double ph = (CommandLinksDialog.this.getDialogPane().getHeader() == null) ? 0.0 : 10.0;
                for (final ButtonType buttonType : CommandLinksDialog.this.getDialogPane().getButtonTypes()) {
                    final Button button = (Button)CommandLinksDialog.this.getDialogPane().lookupButton(buttonType);
                    ph += button.prefHeight(width) + 10.0;
                }
                return ph * 1.2;
            }
        }).setHgap(10.0);
        this.grid.setVgap(10.0);
        this.grid.getStyleClass().add((Object)"container");
        final DialogPane dialogPane = new DialogPane() {
            protected Node createButtonBar() {
                return null;
            }
            
            protected Node createButton(final ButtonType buttonType) {
                return (Node)CommandLinksDialog.this.createCommandLinksButton(buttonType);
            }
        };
        this.setDialogPane(dialogPane);
        this.setTitle(Localization.getString("Dialog.info.title"));
        dialogPane.getStyleClass().add((Object)"command-links-dialog");
        dialogPane.getStylesheets().add((Object)this.getClass().getResource("dialogs.css").toExternalForm());
        dialogPane.getStylesheets().add((Object)this.getClass().getResource("commandlink.css").toExternalForm());
        this.typeMap = new HashMap<ButtonType, CommandLinksButtonType>();
        for (final CommandLinksButtonType link : links) {
            this.addLinkToDialog(dialogPane, link);
        }
        this.addLinkToDialog(dialogPane, buildHiddenCancelLink());
        this.updateGrid();
        dialogPane.getButtonTypes().addListener(c -> this.updateGrid());
        this.contentTextProperty().addListener(o -> this.updateContentText());
    }
    
    private void addLinkToDialog(final DialogPane dialogPane, final CommandLinksButtonType link) {
        this.typeMap.put(link.getButtonType(), link);
        dialogPane.getButtonTypes().add((Object)link.getButtonType());
    }
    
    private void updateContentText() {
        final String contentText = this.getDialogPane().getContentText();
        this.grid.getChildren().remove((Object)this.contentTextLabel);
        if (contentText != null && !contentText.isEmpty()) {
            if (this.contentTextLabel != null) {
                this.contentTextLabel.setText(contentText);
            }
            else {
                this.contentTextLabel = new Label(this.getDialogPane().getContentText());
                this.contentTextLabel.getStyleClass().add((Object)"command-link-message");
            }
            this.grid.add((Node)this.contentTextLabel, 0, 0);
        }
    }
    
    private void updateGrid() {
        this.grid.getChildren().clear();
        this.updateContentText();
        int row = 1;
        for (final ButtonType buttonType : this.getDialogPane().getButtonTypes()) {
            if (buttonType == null) {
                continue;
            }
            final Button button = (Button)this.getDialogPane().lookupButton(buttonType);
            GridPane.setHgrow((Node)button, Priority.ALWAYS);
            GridPane.setVgrow((Node)button, Priority.ALWAYS);
            this.grid.add((Node)button, 0, row++);
        }
        this.getDialogPane().setContent((Node)this.grid);
        this.getDialogPane().requestLayout();
    }
    
    private Button createCommandLinksButton(final ButtonType buttonType) {
        final CommandLinksButtonType commandLink = this.typeMap.getOrDefault(buttonType, new CommandLinksButtonType(buttonType));
        final Button button = new Button();
        button.getStyleClass().addAll((Object[])new String[] { "command-link-button" });
        button.setMaxHeight(Double.MAX_VALUE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
        button.setDefaultButton(buttonData != null && buttonData.isDefaultButton());
        button.setOnAction(ae -> this.setResult((Object)buttonType));
        final Label titleLabel = new Label(commandLink.getButtonType().getText());
        titleLabel.minWidthProperty().bind((ObservableValue)new DoubleBinding() {
            {
                this.bind(new Observable[] { titleLabel.prefWidthProperty() });
            }
            
            protected double computeValue() {
                return titleLabel.getPrefWidth() + 400.0;
            }
        });
        titleLabel.getStyleClass().addAll((Object[])new String[] { "line-1" });
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.TOP_LEFT);
        GridPane.setVgrow((Node)titleLabel, Priority.NEVER);
        final Label messageLabel = new Label(commandLink.getLongText());
        messageLabel.getStyleClass().addAll((Object[])new String[] { "line-2" });
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.TOP_LEFT);
        messageLabel.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow((Node)messageLabel, Priority.SOMETIMES);
        final Node commandLinkImage = commandLink.getGraphic();
        final Node view = (Node)((commandLinkImage == null) ? new ImageView(CommandLinksDialog.class.getResource("arrow-green-right.png").toExternalForm()) : commandLinkImage);
        final Pane graphicContainer = new Pane(new Node[] { view });
        graphicContainer.getStyleClass().add((Object)"graphic-container");
        GridPane.setValignment((Node)graphicContainer, VPos.TOP);
        GridPane.setMargin((Node)graphicContainer, new Insets(0.0, 10.0, 0.0, 0.0));
        final GridPane grid = new GridPane();
        grid.minWidthProperty().bind((ObservableValue)titleLabel.prefWidthProperty());
        grid.setMaxHeight(Double.MAX_VALUE);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.getStyleClass().add((Object)"container");
        grid.add((Node)graphicContainer, 0, 0, 1, 2);
        grid.add((Node)titleLabel, 1, 0);
        grid.add((Node)messageLabel, 1, 1);
        button.setGraphic((Node)grid);
        button.minWidthProperty().bind((ObservableValue)titleLabel.prefWidthProperty());
        if (commandLink.isHidden) {
            button.setVisible(false);
            button.setPrefHeight(1.0);
        }
        return button;
    }
    
    public static class CommandLinksButtonType
    {
        private final ButtonType buttonType;
        private final String longText;
        private final Node graphic;
        private boolean isHidden;
        
        public CommandLinksButtonType(final String text, final boolean isDefault) {
            this(new ButtonType(text, buildButtonData(isDefault)), (String)null);
        }
        
        public CommandLinksButtonType(final String text, final String longText, final boolean isDefault) {
            this(new ButtonType(text, buildButtonData(isDefault)), longText, null);
        }
        
        public CommandLinksButtonType(final String text, final String longText, final Node graphic, final boolean isDefault) {
            this(new ButtonType(text, buildButtonData(isDefault)), longText, graphic);
        }
        
        private CommandLinksButtonType(final ButtonType buttonType) {
            this(buttonType, (String)null);
        }
        
        private CommandLinksButtonType(final ButtonType buttonType, final String longText) {
            this(buttonType, longText, null);
        }
        
        private CommandLinksButtonType(final ButtonType buttonType, final String longText, final Node graphic) {
            this.isHidden = false;
            this.buttonType = buttonType;
            this.longText = longText;
            this.graphic = graphic;
        }
        
        private static ButtonBar.ButtonData buildButtonData(final boolean isDeafault) {
            return isDeafault ? ButtonBar.ButtonData.OK_DONE : ButtonBar.ButtonData.OTHER;
        }
        
        private static CommandLinksButtonType buildHiddenCancelLink() {
            final CommandLinksButtonType link = new CommandLinksButtonType(new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE));
            link.isHidden = true;
            return link;
        }
        
        public ButtonType getButtonType() {
            return this.buttonType;
        }
        
        public Node getGraphic() {
            return this.graphic;
        }
        
        public String getLongText() {
            return this.longText;
        }
    }
}
