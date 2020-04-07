// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public class ToSoutheastChangeStrategy extends AbstractFixedPointChangeStrategy
{
    private final Point2D northwesternCorner;
    
    public ToSoutheastChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
        this.northwesternCorner = new Point2D(original.getMinX(), original.getMinY());
    }
    
    @Override
    protected Point2D getFixedCorner() {
        return this.northwesternCorner;
    }
}
