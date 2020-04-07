// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import impl.org.controlsfx.tools.MathTools;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class MoveChangeStrategy extends AbstractPreviousRectangleChangeStrategy
{
    private final Rectangle2D bounds;
    private Point2D startingPoint;
    
    public MoveChangeStrategy(final Rectangle2D previous, final Rectangle2D bounds) {
        super(previous, false, 0.0);
        Objects.requireNonNull(bounds, "The specified bounds must not be null.");
        this.bounds = bounds;
    }
    
    public MoveChangeStrategy(final Rectangle2D previous, final double maxX, final double maxY) {
        super(previous, false, 0.0);
        if (maxX < previous.getWidth()) {
            throw new IllegalArgumentException("The specified maximal x-coordinate must be greater than or equal to the previous rectangle's width.");
        }
        if (maxY < previous.getHeight()) {
            throw new IllegalArgumentException("The specified maximal y-coordinate must be greater than or equal to the previous rectangle's height.");
        }
        this.bounds = new Rectangle2D(0.0, 0.0, maxX, maxY);
    }
    
    private final Rectangle2D moveRectangleToPoint(final Point2D point) {
        final double xMove = point.getX() - this.startingPoint.getX();
        final double yMove = point.getY() - this.startingPoint.getY();
        final double upperLeftX = this.getPrevious().getMinX() + xMove;
        final double upperLeftY = this.getPrevious().getMinY() + yMove;
        final double maxX = this.bounds.getMaxX() - this.getPrevious().getWidth();
        final double maxY = this.bounds.getMaxY() - this.getPrevious().getHeight();
        final double correctedUpperLeftX = MathTools.inInterval(this.bounds.getMinX(), upperLeftX, maxX);
        final double correctedUpperLeftY = MathTools.inInterval(this.bounds.getMinY(), upperLeftY, maxY);
        return new Rectangle2D(correctedUpperLeftX, correctedUpperLeftY, this.getPrevious().getWidth(), this.getPrevious().getHeight());
    }
    
    @Override
    protected Rectangle2D doBegin(final Point2D point) {
        this.startingPoint = point;
        return this.getPrevious();
    }
    
    @Override
    protected Rectangle2D doContinue(final Point2D point) {
        return this.moveRectangleToPoint(point);
    }
    
    @Override
    protected Rectangle2D doEnd(final Point2D point) {
        return this.moveRectangleToPoint(point);
    }
}
