// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class Vector
{
    public float[] vector;
    
    public Vector() {
        this.vector = new float[4];
    }
    
    public Vector(final float[] vector) {
        this.vector = new float[4];
        for (int i = 0; i < vector.length; ++i) {
            this.vector[i] = vector[i];
        }
    }
    
    public Vector(final float v1, final float v2, final float v3) {
        (this.vector = new float[4])[0] = v1;
        this.vector[1] = v2;
        this.vector[2] = v3;
    }
    
    public final Vector sub(final Position3D p) {
        final float[] vector = this.vector;
        final int n = 0;
        vector[n] -= p.x;
        final float[] vector2 = this.vector;
        final int n2 = 1;
        vector2[n2] -= p.y;
        final float[] vector3 = this.vector;
        final int n3 = 2;
        vector3[n3] -= p.z;
        return this;
    }
    
    public final float[] getVector() {
        return this.vector;
    }
    
    public final float[] getVector3() {
        final float[] v = { this.vector[0], this.vector[1], this.vector[2] };
        return v;
    }
    
    public final Vector transform(final Matrix m) {
        final float[] matrix = m.getMatrix();
        final float vx = this.vector[0] * matrix[0] + this.vector[1] * matrix[4] + this.vector[2] * matrix[8] + matrix[12];
        final float vy = this.vector[0] * matrix[1] + this.vector[1] * matrix[5] + this.vector[2] * matrix[9] + matrix[13];
        final float vz = this.vector[0] * matrix[2] + this.vector[1] * matrix[6] + this.vector[2] * matrix[10] + matrix[14];
        final float vw = this.vector[0] * matrix[3] + this.vector[1] * matrix[7] + this.vector[2] * matrix[11] + matrix[15];
        this.vector[0] = vx;
        this.vector[1] = vy;
        this.vector[2] = vz;
        this.vector[3] = vw;
        return this;
    }
    
    public final Vector transform3(final Matrix m) {
        final double[] v = new double[3];
        final float[] matrix = m.getMatrix();
        v[0] = this.vector[0] * matrix[0] + this.vector[1] * matrix[4] + this.vector[2] * matrix[8];
        v[1] = this.vector[0] * matrix[1] + this.vector[1] * matrix[5] + this.vector[2] * matrix[9];
        v[2] = this.vector[0] * matrix[2] + this.vector[1] * matrix[6] + this.vector[2] * matrix[10];
        this.vector[0] = (float)v[0];
        this.vector[1] = (float)v[1];
        this.vector[2] = (float)v[2];
        this.vector[3] = 1.0f;
        return this;
    }
    
    public final Vector reset() {
        this.vector[0] = 0.0f;
        this.vector[1] = 0.0f;
        this.vector[2] = 0.0f;
        this.vector[3] = 1.0f;
        return this;
    }
    
    public final Vector set(final float x, final float y, final float z) {
        this.vector[0] = x;
        this.vector[1] = y;
        this.vector[2] = z;
        return this;
    }
    
    public final Vector set(final float x, final float y, final float z, final float w) {
        this.vector[0] = x;
        this.vector[1] = y;
        this.vector[2] = z;
        this.vector[3] = w;
        return this;
    }
    
    public final Vector set(final float[] values) {
        this.vector[0] = values[0];
        this.vector[1] = values[1];
        this.vector[2] = values[2];
        return this;
    }
    
    public final Vector set(final Vector v) {
        this.vector[0] = v.vector[0];
        this.vector[1] = v.vector[1];
        this.vector[2] = v.vector[2];
        this.vector[3] = v.vector[3];
        return this;
    }
    
    public final Vector add(final Vector v) {
        final float[] vector = this.vector;
        final int n = 0;
        vector[n] += v.vector[0];
        final float[] vector2 = this.vector;
        final int n2 = 1;
        vector2[n2] += v.vector[1];
        final float[] vector3 = this.vector;
        final int n3 = 2;
        vector3[n3] += v.vector[2];
        final float[] vector4 = this.vector;
        final int n4 = 3;
        vector4[n4] += v.vector[3];
        return this;
    }
    
    public final Vector add(final Vector v1, final Vector v2) {
        this.add(v1);
        return this.add(v2);
    }
    
    public final Vector scale(final float scale) {
        final float[] vector = this.vector;
        final int n = 0;
        vector[n] *= scale;
        final float[] vector2 = this.vector;
        final int n2 = 1;
        vector2[n2] *= scale;
        final float[] vector3 = this.vector;
        final int n3 = 2;
        vector3[n3] *= scale;
        return this;
    }
    
    public final Vector normalize() {
        final float len = this.length();
        final float[] vector = this.vector;
        final int n = 0;
        vector[n] /= len;
        final float[] vector2 = this.vector;
        final int n2 = 1;
        vector2[n2] /= len;
        final float[] vector3 = this.vector;
        final int n3 = 2;
        vector3[n3] /= len;
        return this;
    }
    
    public final float x() {
        return this.vector[0];
    }
    
    public final float y() {
        return this.vector[1];
    }
    
    public final float z() {
        return this.vector[2];
    }
    
    public final float w() {
        return this.vector[3];
    }
    
    public final Vector negate() {
        this.vector[0] = -this.vector[0];
        this.vector[1] = -this.vector[1];
        this.vector[2] = -this.vector[2];
        return this;
    }
    
    public final Vector cross(final Vector v) {
        return this.set(this.vector[1] * v.vector[2] - this.vector[2] * v.vector[1], this.vector[2] * v.vector[0] - this.vector[0] * v.vector[2], this.vector[0] * v.vector[1] - this.vector[1] * v.vector[0]);
    }
    
    public final float length() {
        return (float)Math.sqrt(this.vector[0] * this.vector[0] + this.vector[1] * this.vector[1] + this.vector[2] * this.vector[2]);
    }
    
    public final float dot(final Vector v) {
        return this.vector[0] * v.vector[0] + this.vector[1] * v.vector[1] + this.vector[2] * v.vector[2];
    }
}
