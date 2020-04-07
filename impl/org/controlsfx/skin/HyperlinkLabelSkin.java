// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import java.util.Collection;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.Node;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.TextFlow;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.HyperlinkLabel;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class HyperlinkLabelSkin extends BehaviorSkinBase<HyperlinkLabel, BehaviorBase<HyperlinkLabel>>
{
    private static final String HYPERLINK_START = "[";
    private static final String HYPERLINK_END = "]";
    private final TextFlow textFlow;
    private final EventHandler<ActionEvent> eventHandler;
    
    public HyperlinkLabelSkin(final HyperlinkLabel control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.eventHandler = (EventHandler<ActionEvent>)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent event) {
                final EventHandler<ActionEvent> onActionHandler = ((HyperlinkLabel)HyperlinkLabelSkin.this.getSkinnable()).getOnAction();
                if (onActionHandler != null) {
                    onActionHandler.handle((Event)event);
                }
            }
        };
        this.textFlow = new TextFlow();
        this.getChildren().add((Object)this.textFlow);
        this.updateText();
        this.registerChangeListener((ObservableValue)control.textProperty(), "TEXT");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if (p == "TEXT") {
            this.updateText();
        }
    }
    
    private void updateText() {
        final String text = ((HyperlinkLabel)this.getSkinnable()).getText();
        if (text == null || text.isEmpty()) {
            this.textFlow.getChildren().clear();
            return;
        }
        final List<Node> nodes = new ArrayList<Node>();
        int endPos;
        for (int start = 0, textLength = text.length(); start != -1 && start < textLength; start = endPos + 1) {
            final int startPos = text.indexOf("[", start);
            endPos = text.indexOf("]", startPos);
            if ((startPos == -1 || endPos == -1) && textLength > start) {
                final Label label = new Label(text.substring(start));
                nodes.add((Node)label);
                break;
            }
            final Text label2 = new Text(text.substring(start, startPos));
            nodes.add((Node)label2);
            final Hyperlink hyperlink = new Hyperlink(text.substring(startPos + 1, endPos));
            hyperlink.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
            hyperlink.setOnAction((EventHandler)this.eventHandler);
            nodes.add((Node)hyperlink);
        }
        this.textFlow.getChildren().setAll((Collection)nodes);
    }
}
