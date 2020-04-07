// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.behavior;

import javafx.event.EventType;
import com.sun.javafx.scene.control.behavior.OrientedKeyBinding;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import org.controlsfx.tools.Utils;
import javafx.geometry.Orientation;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Control;
import javafx.util.Callback;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import java.util.List;
import org.controlsfx.control.RangeSlider;
import com.sun.javafx.scene.control.behavior.BehaviorBase;

public class RangeSliderBehavior extends BehaviorBase<RangeSlider>
{
    private static final List<KeyBinding> RANGESLIDER_BINDINGS;
    private Callback<Void, FocusedChild> selectedValue;
    
    public RangeSliderBehavior(final RangeSlider slider) {
        super((Control)slider, (List)RangeSliderBehavior.RANGESLIDER_BINDINGS);
    }
    
    protected void callAction(final String s) {
        if ("Home".equals(s) || "Home2".equals(s)) {
            this.home();
        }
        else if ("End".equals(s) || "End2".equals(s)) {
            this.end();
        }
        else if ("IncrementValue".equals(s) || "IncrementValue2".equals(s)) {
            this.incrementValue();
        }
        else if ("DecrementValue".equals(s) || "DecrementValue2".equals(s)) {
            this.decrementValue();
        }
        else {
            super.callAction(s);
        }
    }
    
    public void setSelectedValue(final Callback<Void, FocusedChild> c) {
        this.selectedValue = c;
    }
    
    public void trackPress(final MouseEvent e, final double position) {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        if (!rangeSlider.isFocused()) {
            rangeSlider.requestFocus();
        }
        if (this.selectedValue != null) {
            double newPosition;
            if (rangeSlider.getOrientation().equals((Object)Orientation.HORIZONTAL)) {
                newPosition = position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
            }
            else {
                newPosition = (1.0 - position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
            }
            if (newPosition < rangeSlider.getLowValue()) {
                rangeSlider.adjustLowValue(newPosition);
            }
            else {
                rangeSlider.adjustHighValue(newPosition);
            }
        }
    }
    
    public void trackRelease(final MouseEvent e, final double position) {
    }
    
    public void lowThumbPressed(final MouseEvent e, final double position) {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        if (!rangeSlider.isFocused()) {
            rangeSlider.requestFocus();
        }
        rangeSlider.setLowValueChanging(true);
    }
    
    public void lowThumbDragged(final MouseEvent e, final double position) {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        final double newValue = Utils.clamp(rangeSlider.getMin(), position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin(), rangeSlider.getMax());
        rangeSlider.setLowValue(newValue);
    }
    
    public void lowThumbReleased(final MouseEvent e) {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        rangeSlider.setLowValueChanging(false);
        if (rangeSlider.isSnapToTicks()) {
            rangeSlider.setLowValue(this.snapValueToTicks(rangeSlider.getLowValue()));
        }
    }
    
    void home() {
        final RangeSlider slider = (RangeSlider)this.getControl();
        slider.adjustHighValue(slider.getMin());
    }
    
    void decrementValue() {
        final RangeSlider slider = (RangeSlider)this.getControl();
        if (this.selectedValue != null) {
            if (this.selectedValue.call((Object)null) == FocusedChild.HIGH_THUMB) {
                if (slider.isSnapToTicks()) {
                    slider.adjustHighValue(slider.getHighValue() - this.computeIncrement());
                }
                else {
                    slider.decrementHighValue();
                }
            }
            else if (slider.isSnapToTicks()) {
                slider.adjustLowValue(slider.getLowValue() - this.computeIncrement());
            }
            else {
                slider.decrementLowValue();
            }
        }
    }
    
    void end() {
        final RangeSlider slider = (RangeSlider)this.getControl();
        slider.adjustHighValue(slider.getMax());
    }
    
    void incrementValue() {
        final RangeSlider slider = (RangeSlider)this.getControl();
        if (this.selectedValue != null) {
            if (this.selectedValue.call((Object)null) == FocusedChild.HIGH_THUMB) {
                if (slider.isSnapToTicks()) {
                    slider.adjustHighValue(slider.getHighValue() + this.computeIncrement());
                }
                else {
                    slider.incrementHighValue();
                }
            }
            else if (slider.isSnapToTicks()) {
                slider.adjustLowValue(slider.getLowValue() + this.computeIncrement());
            }
            else {
                slider.incrementLowValue();
            }
        }
    }
    
    double computeIncrement() {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        double d = 0.0;
        if (rangeSlider.getMinorTickCount() != 0) {
            d = rangeSlider.getMajorTickUnit() / (Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
        }
        else {
            d = rangeSlider.getMajorTickUnit();
        }
        if (rangeSlider.getBlockIncrement() > 0.0 && rangeSlider.getBlockIncrement() < d) {
            return d;
        }
        return rangeSlider.getBlockIncrement();
    }
    
    private double snapValueToTicks(final double d) {
        final RangeSlider rangeSlider = (RangeSlider)this.getControl();
        double d2 = 0.0;
        if (rangeSlider.getMinorTickCount() != 0) {
            d2 = rangeSlider.getMajorTickUnit() / (Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
        }
        else {
            d2 = rangeSlider.getMajorTickUnit();
        }
        final int i = (int)((d - rangeSlider.getMin()) / d2);
        final double d3 = i * d2 + rangeSlider.getMin();
        final double d4 = (i + 1) * d2 + rangeSlider.getMin();
        final double d5 = Utils.nearest(d3, d, d4);
        return Utils.clamp(rangeSlider.getMin(), d5, rangeSlider.getMax());
    }
    
    public void highThumbReleased(final MouseEvent e) {
        final RangeSlider slider = (RangeSlider)this.getControl();
        slider.setHighValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setHighValue(this.snapValueToTicks(slider.getHighValue()));
        }
    }
    
    public void highThumbPressed(final MouseEvent e, final double position) {
        final RangeSlider slider = (RangeSlider)this.getControl();
        if (!slider.isFocused()) {
            slider.requestFocus();
        }
        slider.setHighValueChanging(true);
    }
    
    public void highThumbDragged(final MouseEvent e, final double position) {
        final RangeSlider slider = (RangeSlider)this.getControl();
        slider.setHighValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
    }
    
    public void moveRange(final double position) {
        final RangeSlider slider = (RangeSlider)this.getControl();
        final double min = slider.getMin();
        final double max = slider.getMax();
        final double lowValue = slider.getLowValue();
        final double newLowValue = Utils.clamp(min, lowValue + position * (max - min) / ((slider.getOrientation() == Orientation.HORIZONTAL) ? slider.getWidth() : slider.getHeight()), max);
        final double highValue = slider.getHighValue();
        final double newHighValue = Utils.clamp(min, highValue + position * (max - min) / ((slider.getOrientation() == Orientation.HORIZONTAL) ? slider.getWidth() : slider.getHeight()), max);
        if (newLowValue <= min || newHighValue >= max) {
            return;
        }
        slider.setLowValueChanging(true);
        slider.setHighValueChanging(true);
        slider.setLowValue(newLowValue);
        slider.setHighValue(newHighValue);
    }
    
    public void confirmRange() {
        final RangeSlider slider = (RangeSlider)this.getControl();
        slider.setLowValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setLowValue(this.snapValueToTicks(slider.getLowValue()));
        }
        slider.setHighValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setHighValue(this.snapValueToTicks(slider.getHighValue()));
        }
    }
    
    static {
        (RANGESLIDER_BINDINGS = new ArrayList<KeyBinding>()).add(new KeyBinding(KeyCode.F4, "TraverseDebug").alt().ctrl().shift());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.LEFT, "DecrementValue"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_LEFT, "DecrementValue"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.UP, "IncrementValue").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_UP, "IncrementValue").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.RIGHT, "IncrementValue"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_RIGHT, "IncrementValue"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.DOWN, "DecrementValue").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_DOWN, "DecrementValue").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.LEFT, "TraverseLeft").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_LEFT, "TraverseLeft").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.UP, "TraverseUp"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_UP, "TraverseUp"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.RIGHT, "TraverseRight").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_RIGHT, "TraverseRight").vertical());
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.DOWN, "TraverseDown"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add((KeyBinding)new RangeSliderKeyBinding(KeyCode.KP_DOWN, "TraverseDown"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add(new KeyBinding(KeyCode.HOME, KeyEvent.KEY_RELEASED, "Home"));
        RangeSliderBehavior.RANGESLIDER_BINDINGS.add(new KeyBinding(KeyCode.END, KeyEvent.KEY_RELEASED, "End"));
    }
    
    public static class RangeSliderKeyBinding extends OrientedKeyBinding
    {
        public RangeSliderKeyBinding(final KeyCode code, final String action) {
            super(code, action);
        }
        
        public RangeSliderKeyBinding(final KeyCode code, final EventType<KeyEvent> type, final String action) {
            super(code, (EventType)type, action);
        }
        
        public boolean getVertical(final Control control) {
            return ((RangeSlider)control).getOrientation() == Orientation.VERTICAL;
        }
    }
    
    public enum FocusedChild
    {
        LOW_THUMB, 
        HIGH_THUMB, 
        RANGE_BAR, 
        NONE;
    }
}
