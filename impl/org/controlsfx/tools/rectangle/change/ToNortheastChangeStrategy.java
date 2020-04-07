// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public class ToNortheastChangeStrategy extends AbstractFixedPointChangeStrategy
{
    private final Point2D southwesternCorner;
    
    public ToNortheastChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
        this.southwesternCorner = new Point2D(original.getMinX(), original.getMaxY());
    }
    
    @Override
    protected Point2D getFixedCorner() {
        return this.southwesternCorner;
    }
}
