// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import impl.org.controlsfx.tools.rectangle.Rectangles2D;
import javafx.geometry.Point2D;
import impl.org.controlsfx.tools.rectangle.Edge2D;
import javafx.geometry.Rectangle2D;

abstract class AbstractFixedEdgeChangeStrategy extends AbstractRatioRespectingChangeStrategy
{
    private final Rectangle2D bounds;
    private Edge2D fixedEdge;
    
    protected AbstractFixedEdgeChangeStrategy(final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio);
        this.bounds = bounds;
    }
    
    protected abstract Edge2D getFixedEdge();
    
    private final Rectangle2D createFromEdges(final Point2D point) {
        final Point2D pointInBounds = Rectangles2D.inRectangle(this.bounds, point);
        if (this.isRatioFixed()) {
            return Rectangles2D.forEdgeAndOpposingPointAndRatioWithinBounds(this.fixedEdge, pointInBounds, this.getRatio(), this.bounds);
        }
        return Rectangles2D.forEdgeAndOpposingPoint(this.fixedEdge, pointInBounds);
    }
    
    @Override
    protected final Rectangle2D doBegin(final Point2D point) {
        final boolean startPointNotInBounds = !this.bounds.contains(point);
        if (startPointNotInBounds) {
            throw new IllegalArgumentException("The change's start point (" + point + ") must lie within the bounds (" + this.bounds + ").");
        }
        this.fixedEdge = this.getFixedEdge();
        return this.createFromEdges(point);
    }
    
    @Override
    protected Rectangle2D doContinue(final Point2D point) {
        return this.createFromEdges(point);
    }
    
    @Override
    protected final Rectangle2D doEnd(final Point2D point) {
        final Rectangle2D newRectangle = this.createFromEdges(point);
        this.fixedEdge = null;
        return newRectangle;
    }
}
