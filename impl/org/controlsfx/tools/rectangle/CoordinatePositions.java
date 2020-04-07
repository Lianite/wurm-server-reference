// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle;

import java.util.EnumSet;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class CoordinatePositions
{
    public static EnumSet<CoordinatePosition> onRectangleAndEdges(final Rectangle2D rectangle, final Point2D point, final double edgeTolerance) {
        final EnumSet<CoordinatePosition> positions = EnumSet.noneOf(CoordinatePosition.class);
        positions.add(inRectangle(rectangle, point));
        positions.add(onEdges(rectangle, point, edgeTolerance));
        return positions;
    }
    
    public static CoordinatePosition inRectangle(final Rectangle2D rectangle, final Point2D point) {
        if (rectangle.contains(point)) {
            return CoordinatePosition.IN_RECTANGLE;
        }
        return CoordinatePosition.OUT_OF_RECTANGLE;
    }
    
    public static CoordinatePosition onEdges(final Rectangle2D rectangle, final Point2D point, final double edgeTolerance) {
        final CoordinatePosition vertical = closeToVertical(rectangle, point, edgeTolerance);
        final CoordinatePosition horizontal = closeToHorizontal(rectangle, point, edgeTolerance);
        return extractSingleCardinalDirection(vertical, horizontal);
    }
    
    private static CoordinatePosition closeToVertical(final Rectangle2D rectangle, final Point2D point, final double edgeTolerance) {
        final double xDistanceToLeft = Math.abs(point.getX() - rectangle.getMinX());
        final double xDistanceToRight = Math.abs(point.getX() - rectangle.getMaxX());
        final boolean xCloseToLeft = xDistanceToLeft < edgeTolerance && xDistanceToLeft < xDistanceToRight;
        final boolean xCloseToRight = xDistanceToRight < edgeTolerance && xDistanceToRight < xDistanceToLeft;
        if (!xCloseToLeft && !xCloseToRight) {
            return null;
        }
        final boolean yCloseToVertical = rectangle.getMinY() - edgeTolerance < point.getY() && point.getY() < rectangle.getMaxY() + edgeTolerance;
        if (yCloseToVertical) {
            if (xCloseToLeft) {
                return CoordinatePosition.WEST_EDGE;
            }
            if (xCloseToRight) {
                return CoordinatePosition.EAST_EDGE;
            }
        }
        return null;
    }
    
    private static CoordinatePosition closeToHorizontal(final Rectangle2D rectangle, final Point2D point, final double edgeTolerance) {
        final double yDistanceToUpper = Math.abs(point.getY() - rectangle.getMinY());
        final double yDistanceToLower = Math.abs(point.getY() - rectangle.getMaxY());
        final boolean yCloseToUpper = yDistanceToUpper < edgeTolerance && yDistanceToUpper < yDistanceToLower;
        final boolean yCloseToLower = yDistanceToLower < edgeTolerance && yDistanceToLower < yDistanceToUpper;
        if (!yCloseToUpper && !yCloseToLower) {
            return null;
        }
        final boolean xCloseToHorizontal = rectangle.getMinX() - edgeTolerance < point.getX() && point.getX() < rectangle.getMaxX() + edgeTolerance;
        if (xCloseToHorizontal) {
            if (yCloseToUpper) {
                return CoordinatePosition.NORTH_EDGE;
            }
            if (yCloseToLower) {
                return CoordinatePosition.SOUTH_EDGE;
            }
        }
        return null;
    }
    
    private static CoordinatePosition extractSingleCardinalDirection(final CoordinatePosition vertical, final CoordinatePosition horizontal) {
        if (vertical == null) {
            return horizontal;
        }
        if (horizontal == null) {
            return vertical;
        }
        if (horizontal == CoordinatePosition.NORTH_EDGE && vertical == CoordinatePosition.EAST_EDGE) {
            return CoordinatePosition.NORTHEAST_EDGE;
        }
        if (horizontal == CoordinatePosition.NORTH_EDGE && vertical == CoordinatePosition.WEST_EDGE) {
            return CoordinatePosition.NORTHWEST_EDGE;
        }
        if (horizontal == CoordinatePosition.SOUTH_EDGE && vertical == CoordinatePosition.EAST_EDGE) {
            return CoordinatePosition.SOUTHEAST_EDGE;
        }
        if (horizontal == CoordinatePosition.SOUTH_EDGE && vertical == CoordinatePosition.WEST_EDGE) {
            return CoordinatePosition.SOUTHWEST_EDGE;
        }
        throw new IllegalArgumentException();
    }
}
