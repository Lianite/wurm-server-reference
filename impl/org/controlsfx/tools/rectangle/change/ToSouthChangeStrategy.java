// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import impl.org.controlsfx.tools.rectangle.Edge2D;

public class ToSouthChangeStrategy extends AbstractFixedEdgeChangeStrategy
{
    private final Edge2D northernEdge;
    
    public ToSouthChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
        final Point2D edgeCenterPoint = new Point2D((original.getMinX() + original.getMaxX()) / 2.0, original.getMinY());
        this.northernEdge = new Edge2D(edgeCenterPoint, Orientation.HORIZONTAL, original.getMaxX() - original.getMinX());
    }
    
    public ToSouthChangeStrategy(final Rectangle2D original, final boolean ratioFixed, final double ratio, final double maxX, final double maxY) {
        this(original, ratioFixed, ratio, new Rectangle2D(0.0, 0.0, maxX, maxY));
    }
    
    @Override
    protected Edge2D getFixedEdge() {
        return this.northernEdge;
    }
}
