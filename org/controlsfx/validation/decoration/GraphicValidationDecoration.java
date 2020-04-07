// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation.decoration;

import javafx.scene.control.Control;
import java.util.Arrays;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.control.decoration.Decoration;
import java.util.Collection;
import javafx.scene.control.Tooltip;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.image.Image;

public class GraphicValidationDecoration extends AbstractValidationDecoration
{
    private static final Image ERROR_IMAGE;
    private static final Image WARNING_IMAGE;
    private static final Image REQUIRED_IMAGE;
    private static final String SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
    private static final String POPUP_SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);";
    private static final String TOOLTIP_COMMON_EFFECTS = "-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;";
    private static final String ERROR_TOOLTIP_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;";
    private static final String WARNING_TOOLTIP_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;";
    
    protected Node createErrorNode() {
        return (Node)new ImageView(GraphicValidationDecoration.ERROR_IMAGE);
    }
    
    protected Node createWarningNode() {
        return (Node)new ImageView(GraphicValidationDecoration.WARNING_IMAGE);
    }
    
    private Node createDecorationNode(final ValidationMessage message) {
        final Node graphic = (Severity.ERROR == message.getSeverity()) ? this.createErrorNode() : this.createWarningNode();
        graphic.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        final Label label = new Label();
        label.setGraphic(graphic);
        label.setTooltip(this.createTooltip(message));
        label.setAlignment(Pos.CENTER);
        return (Node)label;
    }
    
    protected Tooltip createTooltip(final ValidationMessage message) {
        final Tooltip tooltip = new Tooltip(message.getText());
        tooltip.setOpacity(0.9);
        tooltip.setAutoFix(true);
        tooltip.setStyle((Severity.ERROR == message.getSeverity()) ? "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;" : "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;");
        return tooltip;
    }
    
    @Override
    protected Collection<Decoration> createValidationDecorations(final ValidationMessage message) {
        return Arrays.asList(new GraphicDecoration(this.createDecorationNode(message), Pos.BOTTOM_LEFT));
    }
    
    @Override
    protected Collection<Decoration> createRequiredDecorations(final Control target) {
        return Arrays.asList(new GraphicDecoration((Node)new ImageView(GraphicValidationDecoration.REQUIRED_IMAGE), Pos.TOP_LEFT, GraphicValidationDecoration.REQUIRED_IMAGE.getWidth() / 2.0, GraphicValidationDecoration.REQUIRED_IMAGE.getHeight() / 2.0));
    }
    
    static {
        ERROR_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-error.png").toExternalForm());
        WARNING_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-warning.png").toExternalForm());
        REQUIRED_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/required-indicator.png").toExternalForm());
    }
}
