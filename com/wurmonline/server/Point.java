// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

public final class Point
{
    private int px;
    private int py;
    private int ph;
    
    public Point(final int x, final int y) {
        this.px = x;
        this.py = y;
        this.ph = 0;
    }
    
    public Point(final int x, final int y, final int h) {
        this.px = x;
        this.py = y;
        this.ph = h;
    }
    
    public Point(final Point point) {
        this.px = point.px;
        this.py = point.py;
        this.ph = point.ph;
    }
    
    public int getX() {
        return this.px;
    }
    
    public void setX(final int x) {
        this.px = x;
    }
    
    public int getY() {
        return this.py;
    }
    
    public void setY(final int y) {
        this.py = y;
    }
    
    public int getH() {
        return this.ph;
    }
    
    public void setH(final int h) {
        this.ph = h;
    }
    
    public void setXY(final int x, final int y) {
        this.px = x;
        this.py = y;
    }
    
    public void setXYH(final int x, final int y, final int h) {
        this.px = x;
        this.py = y;
        this.ph = h;
    }
}
