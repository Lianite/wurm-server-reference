// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.tools;

import java.util.Iterator;
import javafx.scene.Node;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class Utils
{
    public static Window getWindow(final Object owner) throws IllegalArgumentException {
        if (owner == null) {
            Window window = null;
            final Iterator<Window> windows = (Iterator<Window>)Window.impl_getWindows();
            while (windows.hasNext()) {
                window = windows.next();
                if (window.isFocused() && !(window instanceof PopupWindow)) {
                    break;
                }
            }
            return window;
        }
        if (owner instanceof Window) {
            return (Window)owner;
        }
        if (owner instanceof Node) {
            return ((Node)owner).getScene().getWindow();
        }
        throw new IllegalArgumentException("Unknown owner: " + owner.getClass());
    }
    
    public static final String getExcelLetterFromNumber(int number) {
        String letter = "";
        while (number >= 0) {
            final int remainder = number % 26;
            letter = (char)(remainder + 65) + letter;
            number = number / 26 - 1;
        }
        return letter;
    }
    
    public static double clamp(final double min, final double value, final double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static double nearest(final double less, final double value, final double more) {
        final double lessDiff = value - less;
        final double moreDiff = more - value;
        if (lessDiff < moreDiff) {
            return less;
        }
        return more;
    }
}
