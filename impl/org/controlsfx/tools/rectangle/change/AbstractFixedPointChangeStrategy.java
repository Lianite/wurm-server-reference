// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import impl.org.controlsfx.tools.rectangle.Rectangles2D;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

abstract class AbstractFixedPointChangeStrategy extends AbstractRatioRespectingChangeStrategy
{
    private final Rectangle2D bounds;
    private Point2D fixedCorner;
    
    protected AbstractFixedPointChangeStrategy(final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio);
        Objects.requireNonNull(bounds, "The argument 'bounds' must not be null.");
        this.bounds = bounds;
    }
    
    protected abstract Point2D getFixedCorner();
    
    private final Rectangle2D createFromCorners(final Point2D point) {
        final Point2D pointInBounds = Rectangles2D.inRectangle(this.bounds, point);
        if (this.isRatioFixed()) {
            return Rectangles2D.forDiagonalCornersAndRatio(this.fixedCorner, pointInBounds, this.getRatio());
        }
        return Rectangles2D.forDiagonalCorners(this.fixedCorner, pointInBounds);
    }
    
    @Override
    protected final Rectangle2D doBegin(final Point2D point) {
        final boolean startPointNotInBounds = !this.bounds.contains(point);
        if (startPointNotInBounds) {
            throw new IllegalArgumentException("The change's start point (" + point + ") must lie within the bounds (" + this.bounds + ").");
        }
        this.fixedCorner = this.getFixedCorner();
        return this.createFromCorners(point);
    }
    
    @Override
    protected Rectangle2D doContinue(final Point2D point) {
        return this.createFromCorners(point);
    }
    
    @Override
    protected final Rectangle2D doEnd(final Point2D point) {
        final Rectangle2D newRectangle = this.createFromCorners(point);
        this.fixedCorner = null;
        return newRectangle;
    }
}
