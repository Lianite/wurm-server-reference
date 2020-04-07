// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle;

import java.util.Objects;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;

public class Edge2D
{
    private final Point2D centerPoint;
    private final Orientation orientation;
    private final double length;
    
    public Edge2D(final Point2D centerPoint, final Orientation orientation, final double length) {
        Objects.requireNonNull(centerPoint, "The specified center point must not be null.");
        Objects.requireNonNull(orientation, "The specified orientation must not be null.");
        if (length < 0.0) {
            throw new IllegalArgumentException("The length must not be negative, i.e. zero or a positive value is alowed.");
        }
        this.centerPoint = centerPoint;
        this.orientation = orientation;
        this.length = length;
    }
    
    public Point2D getUpperLeft() {
        if (this.isHorizontal()) {
            final double cornersX = this.centerPoint.getX() - this.length / 2.0;
            final double edgesY = this.centerPoint.getY();
            return new Point2D(cornersX, edgesY);
        }
        final double edgesX = this.centerPoint.getX();
        final double cornersY = this.centerPoint.getY() - this.length / 2.0;
        return new Point2D(edgesX, cornersY);
    }
    
    public Point2D getLowerRight() {
        if (this.isHorizontal()) {
            final double cornersX = this.centerPoint.getX() + this.length / 2.0;
            final double edgesY = this.centerPoint.getY();
            return new Point2D(cornersX, edgesY);
        }
        final double edgesX = this.centerPoint.getX();
        final double cornersY = this.centerPoint.getY() + this.length / 2.0;
        return new Point2D(edgesX, cornersY);
    }
    
    public double getOrthogonalDifference(final Point2D otherPoint) {
        Objects.requireNonNull(otherPoint, "The other point must nt be null.");
        if (this.isHorizontal()) {
            return otherPoint.getY() - this.centerPoint.getY();
        }
        return otherPoint.getX() - this.centerPoint.getX();
    }
    
    public Point2D getCenterPoint() {
        return this.centerPoint;
    }
    
    public Orientation getOrientation() {
        return this.orientation;
    }
    
    public boolean isHorizontal() {
        return this.orientation == Orientation.HORIZONTAL;
    }
    
    public boolean isVertical() {
        return this.orientation == Orientation.VERTICAL;
    }
    
    public double getLength() {
        return this.length;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.centerPoint == null) ? 0 : this.centerPoint.hashCode());
        final long temp = Double.doubleToLongBits(this.length);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + ((this.orientation == null) ? 0 : this.orientation.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Edge2D other = (Edge2D)obj;
        if (this.centerPoint == null) {
            if (other.centerPoint != null) {
                return false;
            }
        }
        else if (!this.centerPoint.equals((Object)other.centerPoint)) {
            return false;
        }
        return Double.doubleToLongBits(this.length) == Double.doubleToLongBits(other.length) && this.orientation == other.orientation;
    }
    
    @Override
    public String toString() {
        return "Edge2D [centerX = " + this.centerPoint.getX() + ", centerY = " + this.centerPoint.getY() + ", orientation = " + this.orientation + ", length = " + this.length + "]";
    }
}
