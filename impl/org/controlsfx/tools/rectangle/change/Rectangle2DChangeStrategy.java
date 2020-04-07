// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public interface Rectangle2DChangeStrategy
{
    Rectangle2D beginChange(final Point2D p0);
    
    Rectangle2D continueChange(final Point2D p0);
    
    Rectangle2D endChange(final Point2D p0);
}
