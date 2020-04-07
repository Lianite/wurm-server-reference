// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public class ToNorthwestChangeStrategy extends AbstractFixedPointChangeStrategy
{
    private final Point2D southeasternCorner;
    
    public ToNorthwestChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
        this.southeasternCorner = new Point2D(original.getMaxX(), original.getMaxY());
    }
    
    @Override
    protected Point2D getFixedCorner() {
        return this.southeasternCorner;
    }
}
