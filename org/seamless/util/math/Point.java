// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.math;

public class Point
{
    private int x;
    private int y;
    
    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public Point multiply(final double by) {
        return new Point((this.x != 0) ? ((int)(this.x * by)) : 0, (this.y != 0) ? ((int)(this.y * by)) : 0);
    }
    
    public Point divide(final double by) {
        return new Point((this.x != 0) ? ((int)(this.x / by)) : 0, (this.y != 0) ? ((int)(this.y / by)) : 0);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Point point = (Point)o;
        return this.x == point.x && this.y == point.y;
    }
    
    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }
    
    public String toString() {
        return "Point(" + this.x + "/" + this.y + ")";
    }
}
