// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle;

import javafx.geometry.Bounds;
import impl.org.controlsfx.tools.MathTools;
import javafx.geometry.Point2D;
import java.util.Objects;
import javafx.geometry.Rectangle2D;

public class Rectangles2D
{
    public static boolean contains(final Rectangle2D rectangle, final Edge2D edge) {
        Objects.requireNonNull(rectangle, "The argument 'rectangle' must not be null.");
        Objects.requireNonNull(edge, "The argument 'edge' must not be null.");
        final boolean edgeInBounds = rectangle.contains(edge.getUpperLeft()) && rectangle.contains(edge.getLowerRight());
        return edgeInBounds;
    }
    
    public static Point2D inRectangle(final Rectangle2D rectangle, final Point2D point) {
        Objects.requireNonNull(rectangle, "The argument 'rectangle' must not be null.");
        Objects.requireNonNull(point, "The argument 'point' must not be null.");
        if (rectangle.contains(point)) {
            return point;
        }
        final double newX = MathTools.inInterval(rectangle.getMinX(), point.getX(), rectangle.getMaxX());
        final double newY = MathTools.inInterval(rectangle.getMinY(), point.getY(), rectangle.getMaxY());
        return new Point2D(newX, newY);
    }
    
    public static Point2D getCenterPoint(final Rectangle2D rectangle) {
        Objects.requireNonNull(rectangle, "The argument 'rectangle' must not be null.");
        final double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2.0;
        final double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2.0;
        return new Point2D(centerX, centerY);
    }
    
    public static Rectangle2D intersection(final Rectangle2D a, final Rectangle2D b) {
        Objects.requireNonNull(a, "The argument 'a' must not be null.");
        Objects.requireNonNull(b, "The argument 'b' must not be null.");
        if (a.intersects(b)) {
            final double intersectionMinX = Math.max(a.getMinX(), b.getMinX());
            final double intersectionMaxX = Math.min(a.getMaxX(), b.getMaxX());
            final double intersectionWidth = intersectionMaxX - intersectionMinX;
            final double intersectionMinY = Math.max(a.getMinY(), b.getMinY());
            final double intersectionMaxY = Math.min(a.getMaxY(), b.getMaxY());
            final double intersectionHeight = intersectionMaxY - intersectionMinY;
            return new Rectangle2D(intersectionMinX, intersectionMinY, intersectionWidth, intersectionHeight);
        }
        return Rectangle2D.EMPTY;
    }
    
    public static Rectangle2D forDiagonalCorners(final Point2D oneCorner, final Point2D diagonalCorner) {
        Objects.requireNonNull(oneCorner, "The specified corner must not be null.");
        Objects.requireNonNull(diagonalCorner, "The specified diagonal corner must not be null.");
        final double minX = Math.min(oneCorner.getX(), diagonalCorner.getX());
        final double minY = Math.min(oneCorner.getY(), diagonalCorner.getY());
        final double width = Math.abs(oneCorner.getX() - diagonalCorner.getX());
        final double height = Math.abs(oneCorner.getY() - diagonalCorner.getY());
        return new Rectangle2D(minX, minY, width, height);
    }
    
    public static Rectangle2D forUpperLeftCornerAndSize(final Point2D upperLeft, final double width, final double height) {
        return new Rectangle2D(upperLeft.getX(), upperLeft.getY(), width, height);
    }
    
    public static Rectangle2D forDiagonalCornersAndRatio(final Point2D fixedCorner, final Point2D diagonalCorner, final double ratio) {
        Objects.requireNonNull(fixedCorner, "The specified fixed corner must not be null.");
        Objects.requireNonNull(diagonalCorner, "The specified diagonal corner must not be null.");
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        final double xDifference = diagonalCorner.getX() - fixedCorner.getX();
        final double yDifference = diagonalCorner.getY() - fixedCorner.getY();
        final double xDifferenceByRatio = correctCoordinateDifferenceByRatio(xDifference, yDifference, ratio);
        final double yDifferenceByRatio = correctCoordinateDifferenceByRatio(yDifference, xDifference, 1.0 / ratio);
        final double minX = getMinCoordinate(fixedCorner.getX(), xDifferenceByRatio);
        final double minY = getMinCoordinate(fixedCorner.getY(), yDifferenceByRatio);
        final double width = Math.abs(xDifferenceByRatio);
        final double height = Math.abs(yDifferenceByRatio);
        return new Rectangle2D(minX, minY, width, height);
    }
    
    private static double correctCoordinateDifferenceByRatio(final double difference, final double otherDifference, final double ratioAsMultiplier) {
        final double differenceByRatio = otherDifference * ratioAsMultiplier;
        final double correctedDistance = Math.min(Math.abs(difference), Math.abs(differenceByRatio));
        return correctedDistance * Math.signum(difference);
    }
    
    private static double getMinCoordinate(final double fixedCoordinate, final double difference) {
        if (difference < 0.0) {
            return fixedCoordinate + difference;
        }
        return fixedCoordinate;
    }
    
    public static Rectangle2D forCenterAndSize(final Point2D centerPoint, final double width, final double height) {
        Objects.requireNonNull(centerPoint, "The specified center point must not be null.");
        final double absoluteWidth = Math.abs(width);
        final double absoluteHeight = Math.abs(height);
        final double minX = centerPoint.getX() - absoluteWidth / 2.0;
        final double minY = centerPoint.getY() - absoluteHeight / 2.0;
        return new Rectangle2D(minX, minY, width, height);
    }
    
    public static Rectangle2D fixRatio(final Rectangle2D original, final double ratio) {
        Objects.requireNonNull(original, "The specified original rectangle must not be null.");
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        return createWithFixedRatioWithinBounds(original, ratio, null);
    }
    
    public static Rectangle2D fixRatioWithinBounds(final Rectangle2D original, final double ratio, final Rectangle2D bounds) {
        Objects.requireNonNull(original, "The specified original rectangle must not be null.");
        Objects.requireNonNull(bounds, "The specified bounds for the new rectangle must not be null.");
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        return createWithFixedRatioWithinBounds(original, ratio, bounds);
    }
    
    private static Rectangle2D createWithFixedRatioWithinBounds(final Rectangle2D original, final double ratio, final Rectangle2D bounds) {
        final Point2D centerPoint = getCenterPoint(original);
        final boolean centerPointInBounds = bounds == null || bounds.contains(centerPoint);
        if (!centerPointInBounds) {
            throw new IllegalArgumentException("The center point " + centerPoint + " of the original rectangle is out of the specified bounds.");
        }
        final double area = original.getWidth() * original.getHeight();
        return createForCenterAreaAndRatioWithinBounds(centerPoint, area, ratio, bounds);
    }
    
    public static Rectangle2D forCenterAndAreaAndRatio(final Point2D centerPoint, final double area, final double ratio) {
        Objects.requireNonNull(centerPoint, "The specified center point of the new rectangle must not be null.");
        if (area < 0.0) {
            throw new IllegalArgumentException("The specified area " + area + " must be larger than zero.");
        }
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        return createForCenterAreaAndRatioWithinBounds(centerPoint, area, ratio, null);
    }
    
    public static Rectangle2D forCenterAndAreaAndRatioWithinBounds(final Point2D centerPoint, final double area, final double ratio, final Rectangle2D bounds) {
        Objects.requireNonNull(centerPoint, "The specified center point of the new rectangle must not be null.");
        Objects.requireNonNull(bounds, "The specified bounds for the new rectangle must not be null.");
        final boolean centerPointInBounds = bounds.contains(centerPoint);
        if (!centerPointInBounds) {
            throw new IllegalArgumentException("The center point " + centerPoint + " of the original rectangle is out of the specified bounds.");
        }
        if (area < 0.0) {
            throw new IllegalArgumentException("The specified area " + area + " must be larger than zero.");
        }
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        return createForCenterAreaAndRatioWithinBounds(centerPoint, area, ratio, bounds);
    }
    
    private static Rectangle2D createForCenterAreaAndRatioWithinBounds(final Point2D centerPoint, final double area, final double ratio, final Rectangle2D bounds) {
        double newWidth = Math.sqrt(area * ratio);
        double newHeight = area / newWidth;
        final boolean boundsSpecified = bounds != null;
        if (boundsSpecified) {
            final double reductionFactor = lengthReductionToStayWithinBounds(centerPoint, newWidth, newHeight, bounds);
            newWidth *= reductionFactor;
            newHeight *= reductionFactor;
        }
        return forCenterAndSize(centerPoint, newWidth, newHeight);
    }
    
    private static double lengthReductionToStayWithinBounds(final Point2D centerPoint, final double width, final double height, final Rectangle2D bounds) {
        Objects.requireNonNull(centerPoint, "The specified center point of the new rectangle must not be null.");
        Objects.requireNonNull(bounds, "The specified bounds for the new rectangle must not be null.");
        final boolean centerPointInBounds = bounds.contains(centerPoint);
        if (!centerPointInBounds) {
            throw new IllegalArgumentException("The center point " + centerPoint + " of the original rectangle is out of the specified bounds.");
        }
        if (width < 0.0) {
            throw new IllegalArgumentException("The specified width " + width + " must be larger than zero.");
        }
        if (height < 0.0) {
            throw new IllegalArgumentException("The specified height " + height + " must be larger than zero.");
        }
        final double distanceToEast = Math.abs(centerPoint.getX() - bounds.getMinX());
        final double distanceToWest = Math.abs(centerPoint.getX() - bounds.getMaxX());
        final double distanceToNorth = Math.abs(centerPoint.getY() - bounds.getMinY());
        final double distanceToSouth = Math.abs(centerPoint.getY() - bounds.getMaxY());
        return MathTools.min(1.0, distanceToEast / width * 2.0, distanceToWest / width * 2.0, distanceToNorth / height * 2.0, distanceToSouth / height * 2.0);
    }
    
    public static Rectangle2D forEdgeAndOpposingPoint(final Edge2D edge, final Point2D point) {
        final double otherDimension = edge.getOrthogonalDifference(point);
        return createForEdgeAndOtherDimension(edge, otherDimension);
    }
    
    public static Rectangle2D forEdgeAndOpposingPointAndRatioWithinBounds(final Edge2D edge, final Point2D point, final double ratio, final Rectangle2D bounds) {
        Objects.requireNonNull(edge, "The specified edge must not be null.");
        Objects.requireNonNull(point, "The specified point must not be null.");
        Objects.requireNonNull(bounds, "The specified bounds must not be null.");
        final boolean edgeInBounds = contains(bounds, edge);
        if (!edgeInBounds) {
            throw new IllegalArgumentException("The specified edge " + edge + " is not entirely contained on the specified bounds.");
        }
        if (ratio < 0.0) {
            throw new IllegalArgumentException("The specified ratio " + ratio + " must be larger than zero.");
        }
        final Point2D boundedPoint = movePointIntoBounds(point, bounds);
        final Edge2D unboundedEdge = resizeEdgeForDistanceAndRatio(edge, boundedPoint, ratio);
        final Edge2D boundedEdge = resizeEdgeForBounds(unboundedEdge, bounds);
        double otherDimension = Math.signum(boundedEdge.getOrthogonalDifference(boundedPoint));
        if (boundedEdge.isHorizontal()) {
            otherDimension *= boundedEdge.getLength() / ratio;
        }
        else {
            otherDimension *= boundedEdge.getLength() * ratio;
        }
        return createForEdgeAndOtherDimension(boundedEdge, otherDimension);
    }
    
    private static Point2D movePointIntoBounds(final Point2D point, final Rectangle2D bounds) {
        if (bounds.contains(point)) {
            return point;
        }
        final double boundedPointX = MathTools.inInterval(bounds.getMinX(), point.getX(), bounds.getMaxX());
        final double boundedPointY = MathTools.inInterval(bounds.getMinY(), point.getY(), bounds.getMaxY());
        return new Point2D(boundedPointX, boundedPointY);
    }
    
    private static Edge2D resizeEdgeForDistanceAndRatio(final Edge2D edge, final Point2D point, final double ratio) {
        final double distance = Math.abs(edge.getOrthogonalDifference(point));
        if (edge.isHorizontal()) {
            final double xLength = distance * ratio;
            return new Edge2D(edge.getCenterPoint(), edge.getOrientation(), xLength);
        }
        final double yLength = distance / ratio;
        return new Edge2D(edge.getCenterPoint(), edge.getOrientation(), yLength);
    }
    
    private static Edge2D resizeEdgeForBounds(final Edge2D edge, final Rectangle2D bounds) {
        final boolean edgeInBounds = contains(bounds, edge);
        if (edgeInBounds) {
            return edge;
        }
        final boolean centerPointInBounds = bounds.contains(edge.getCenterPoint());
        if (!centerPointInBounds) {
            throw new IllegalArgumentException("The specified edge's center point (" + edge + ") is out of the specified bounds (" + bounds + ").");
        }
        if (edge.isHorizontal()) {
            final double leftPartLengthBound = Math.abs(bounds.getMinX() - edge.getCenterPoint().getX());
            final double rightPartLengthBound = Math.abs(bounds.getMaxX() - edge.getCenterPoint().getX());
            final double leftPartLength = MathTools.inInterval(0.0, edge.getLength() / 2.0, leftPartLengthBound);
            final double rightPartLength = MathTools.inInterval(0.0, edge.getLength() / 2.0, rightPartLengthBound);
            final double horizontalLength = Math.min(leftPartLength, rightPartLength) * 2.0;
            return new Edge2D(edge.getCenterPoint(), edge.getOrientation(), horizontalLength);
        }
        final double lowerPartLengthBound = Math.abs(bounds.getMinY() - edge.getCenterPoint().getY());
        final double upperPartLengthBound = Math.abs(bounds.getMaxY() - edge.getCenterPoint().getY());
        final double lowerPartLength = MathTools.inInterval(0.0, edge.getLength() / 2.0, lowerPartLengthBound);
        final double upperPartLength = MathTools.inInterval(0.0, edge.getLength() / 2.0, upperPartLengthBound);
        final double verticalLength = Math.min(lowerPartLength, upperPartLength) * 2.0;
        return new Edge2D(edge.getCenterPoint(), edge.getOrientation(), verticalLength);
    }
    
    private static Rectangle2D createForEdgeAndOtherDimension(final Edge2D edge, final double otherDimension) {
        if (edge.isHorizontal()) {
            return createForHorizontalEdgeAndHeight(edge, otherDimension);
        }
        return createForVerticalEdgeAndWidth(edge, otherDimension);
    }
    
    private static Rectangle2D createForHorizontalEdgeAndHeight(final Edge2D horizontalEdge, final double height) {
        final Point2D leftEdgeEndPoint = horizontalEdge.getUpperLeft();
        final double upperLeftX = leftEdgeEndPoint.getX();
        final double upperLeftY = leftEdgeEndPoint.getY() + Math.min(0.0, height);
        final double absoluteWidth = Math.abs(horizontalEdge.getLength());
        final double absoluteHeight = Math.abs(height);
        return new Rectangle2D(upperLeftX, upperLeftY, absoluteWidth, absoluteHeight);
    }
    
    private static Rectangle2D createForVerticalEdgeAndWidth(final Edge2D verticalEdge, final double width) {
        final Point2D upperEdgeEndPoint = verticalEdge.getUpperLeft();
        final double upperLeftX = upperEdgeEndPoint.getX() + Math.min(0.0, width);
        final double upperLeftY = upperEdgeEndPoint.getY();
        final double absoluteWidth = Math.abs(width);
        final double absoluteHeight = Math.abs(verticalEdge.getLength());
        return new Rectangle2D(upperLeftX, upperLeftY, absoluteWidth, absoluteHeight);
    }
    
    public static Rectangle2D fromBounds(final Bounds bounds) {
        return new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }
}
