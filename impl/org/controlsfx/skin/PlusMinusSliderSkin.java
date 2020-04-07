// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.input.InputEvent;
import javafx.event.EventType;
import javafx.event.EventTarget;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.geometry.Orientation;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.control.Slider;
import org.controlsfx.control.PlusMinusSlider;
import javafx.scene.control.SkinBase;

public class PlusMinusSliderSkin extends SkinBase<PlusMinusSlider>
{
    private SliderReader reader;
    private Slider slider;
    private Region plusRegion;
    private Region minusRegion;
    private BorderPane borderPane;
    
    public PlusMinusSliderSkin(final PlusMinusSlider adjuster) {
        super((Control)adjuster);
        adjuster.addEventFilter(KeyEvent.ANY, (EventHandler)new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent event) {
                event.consume();
            }
        });
        this.slider = new Slider(-1.0, 1.0, 0.0);
        this.slider.valueProperty().addListener((ChangeListener)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
                ((PlusMinusSlider)PlusMinusSliderSkin.this.getSkinnable()).getProperties().put((Object)"plusminusslidervalue", (Object)newValue.doubleValue());
            }
        });
        this.slider.orientationProperty().bind((ObservableValue)adjuster.orientationProperty());
        this.slider.addEventHandler(MouseEvent.MOUSE_PRESSED, (EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent evt) {
                PlusMinusSliderSkin.this.reader = new SliderReader();
                PlusMinusSliderSkin.this.reader.start();
            }
        });
        this.slider.addEventHandler(MouseEvent.MOUSE_RELEASED, (EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent evt) {
                if (PlusMinusSliderSkin.this.reader != null) {
                    PlusMinusSliderSkin.this.reader.stop();
                }
                final KeyValue keyValue = new KeyValue((WritableValue)PlusMinusSliderSkin.this.slider.valueProperty(), (Object)0);
                final KeyFrame keyFrame = new KeyFrame(Duration.millis(100.0), new KeyValue[] { keyValue });
                final Timeline timeline = new Timeline(new KeyFrame[] { keyFrame });
                timeline.play();
            }
        });
        this.plusRegion = new Region();
        this.plusRegion.getStyleClass().add((Object)"adjust-plus");
        this.minusRegion = new Region();
        this.minusRegion.getStyleClass().add((Object)"adjust-minus");
        this.borderPane = new BorderPane();
        this.updateLayout(adjuster.getOrientation());
        this.getChildren().add((Object)this.borderPane);
        adjuster.orientationProperty().addListener((observable, oldValue, newValue) -> this.updateLayout(newValue));
    }
    
    private void updateLayout(final Orientation orientation) {
        this.borderPane.getChildren().clear();
        switch (orientation) {
            case HORIZONTAL: {
                this.borderPane.setLeft((Node)this.minusRegion);
                this.borderPane.setCenter((Node)this.slider);
                this.borderPane.setRight((Node)this.plusRegion);
                break;
            }
            case VERTICAL: {
                this.borderPane.setTop((Node)this.plusRegion);
                this.borderPane.setCenter((Node)this.slider);
                this.borderPane.setBottom((Node)this.minusRegion);
                break;
            }
        }
    }
    
    class SliderReader extends AnimationTimer
    {
        private long lastTime;
        
        SliderReader() {
            this.lastTime = System.currentTimeMillis();
        }
        
        public void handle(final long now) {
            if (now - this.lastTime > 10000000L) {
                this.lastTime = now;
                PlusMinusSliderSkin.this.slider.fireEvent((Event)new PlusMinusSlider.PlusMinusEvent(PlusMinusSliderSkin.this.slider, (EventTarget)PlusMinusSliderSkin.this.slider, PlusMinusSlider.PlusMinusEvent.VALUE_CHANGED, PlusMinusSliderSkin.this.slider.getValue()));
            }
        }
    }
}
