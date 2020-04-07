// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public class ToSouthwestChangeStrategy extends AbstractFixedPointChangeStrategy
{
    private final Point2D northeasternCorner;
    
    public ToSouthwestChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
        this.northeasternCorner = new Point2D(original.getMaxX(), original.getMinY());
    }
    
    @Override
    protected Point2D getFixedCorner() {
        return this.northeasternCorner;
    }
}
