// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToggleButton;
import javafx.collections.ObservableList;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.scene.layout.HBox;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.SegmentedButton;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class SegmentedButtonSkin extends BehaviorSkinBase<SegmentedButton, BehaviorBase<SegmentedButton>>
{
    private static final String ONLY_BUTTON = "only-button";
    private static final String LEFT_PILL = "left-pill";
    private static final String CENTER_PILL = "center-pill";
    private static final String RIGHT_PILL = "right-pill";
    private final HBox container;
    
    public SegmentedButtonSkin(final SegmentedButton control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.container = new HBox();
        this.getChildren().add((Object)this.container);
        this.updateButtons();
        this.getButtons().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                SegmentedButtonSkin.this.updateButtons();
            }
        });
        control.toggleGroupProperty().addListener((observable, oldValue, newValue) -> this.getButtons().forEach(button -> button.setToggleGroup(newValue)));
    }
    
    private ObservableList<ToggleButton> getButtons() {
        return ((SegmentedButton)this.getSkinnable()).getButtons();
    }
    
    private void updateButtons() {
        final ObservableList<ToggleButton> buttons = this.getButtons();
        final ToggleGroup group = ((SegmentedButton)this.getSkinnable()).getToggleGroup();
        this.container.getChildren().clear();
        for (int i = 0; i < this.getButtons().size(); ++i) {
            final ToggleButton t = (ToggleButton)buttons.get(i);
            if (group != null) {
                t.setToggleGroup(group);
            }
            t.getStyleClass().removeAll((Object[])new String[] { "only-button", "left-pill", "center-pill", "right-pill" });
            this.container.getChildren().add((Object)t);
            if (i == buttons.size() - 1) {
                if (i == 0) {
                    t.getStyleClass().add((Object)"only-button");
                }
                else {
                    t.getStyleClass().add((Object)"right-pill");
                }
            }
            else if (i == 0) {
                t.getStyleClass().add((Object)"left-pill");
            }
            else {
                t.getStyleClass().add((Object)"center-pill");
            }
        }
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((SegmentedButton)this.getSkinnable()).prefWidth(height);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((SegmentedButton)this.getSkinnable()).prefHeight(width);
    }
}
