// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class Position3D
{
    float x;
    float y;
    float z;
    
    public Position3D() {
    }
    
    public Position3D(final Vertex v) {
        this.x = v.vertex[0];
        this.y = v.vertex[1];
        this.z = v.vertex[2];
    }
    
    public Position3D(final Position3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    public Position3D(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void sub(final Position3D p) {
        this.x -= p.x;
        this.y -= p.y;
        this.z -= p.z;
    }
    
    public void sub(final Vector v) {
        this.x -= v.vector[0];
        this.y -= v.vector[1];
        this.z -= v.vector[2];
    }
    
    public void sub(final Vertex v1, final Vector v2) {
        this.x -= v1.point[0];
        this.y -= v1.point[1];
        this.z -= v1.point[2];
        this.sub(v2);
    }
    
    public void set(final Position3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    public void scale(final int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
    }
}
