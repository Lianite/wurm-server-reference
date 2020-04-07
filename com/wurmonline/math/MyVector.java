// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class MyVector
{
    private float[] mVector;
    
    MyVector() {
        this.mVector = new float[4];
        this.reset();
    }
    
    float[] getVector() {
        return this.mVector;
    }
    
    MyVector(final float[] values) {
        this.mVector = new float[4];
        this.set(values);
        this.mVector[3] = 1.0f;
    }
    
    void reset() {
        final float[] mVector = this.mVector;
        final int n = 0;
        final float[] mVector2 = this.mVector;
        final int n2 = 1;
        final float[] mVector3 = this.mVector;
        final int n3 = 2;
        final float n4 = 0.0f;
        mVector3[n3] = n4;
        mVector[n] = (mVector2[n2] = n4);
        this.mVector[3] = 1.0f;
    }
    
    void set(final float[] values) {
        this.mVector[0] = values[0];
        this.mVector[1] = values[1];
        this.mVector[2] = values[2];
    }
    
    void add(final MyVector v) {
        final float[] mVector = this.mVector;
        final int n = 0;
        mVector[n] += v.mVector[0];
        final float[] mVector2 = this.mVector;
        final int n2 = 1;
        mVector2[n2] += v.mVector[1];
        final float[] mVector3 = this.mVector;
        final int n3 = 2;
        mVector3[n3] += v.mVector[2];
        final float[] mVector4 = this.mVector;
        final int n4 = 3;
        mVector4[n4] += v.mVector[3];
    }
    
    void normalize() {
        final float len = this.length();
        final float[] mVector = this.mVector;
        final int n = 0;
        mVector[n] /= len;
        final float[] mVector2 = this.mVector;
        final int n2 = 1;
        mVector2[n2] /= len;
        final float[] mVector3 = this.mVector;
        final int n3 = 2;
        mVector3[n3] /= len;
    }
    
    float length() {
        return (float)Math.sqrt(this.mVector[0] * this.mVector[0] + this.mVector[1] * this.mVector[1] + this.mVector[2] * this.mVector[2]);
    }
    
    void transform(final Matrix m) {
        final float[] vector = new float[4];
        final float[] matrix = m.getMatrix();
        vector[0] = this.mVector[0] * matrix[0] + this.mVector[1] * matrix[4] + this.mVector[2] * matrix[8] + matrix[12];
        vector[1] = this.mVector[0] * matrix[1] + this.mVector[1] * matrix[5] + this.mVector[2] * matrix[9] + matrix[13];
        vector[2] = this.mVector[0] * matrix[2] + this.mVector[1] * matrix[6] + this.mVector[2] * matrix[10] + matrix[14];
        vector[3] = this.mVector[0] * matrix[3] + this.mVector[1] * matrix[7] + this.mVector[2] * matrix[11] + matrix[15];
        this.mVector[0] = vector[0];
        this.mVector[1] = vector[1];
        this.mVector[2] = vector[2];
        this.mVector[3] = vector[3];
    }
    
    void transform3(final Matrix m) {
        final float[] vector = new float[3];
        final float[] matrix = m.getMatrix();
        vector[0] = this.mVector[0] * matrix[0] + this.mVector[1] * matrix[4] + this.mVector[2] * matrix[8];
        vector[1] = this.mVector[0] * matrix[1] + this.mVector[1] * matrix[5] + this.mVector[2] * matrix[9];
        vector[2] = this.mVector[0] * matrix[2] + this.mVector[1] * matrix[6] + this.mVector[2] * matrix[10];
        this.mVector[0] = vector[0];
        this.mVector[1] = vector[1];
        this.mVector[2] = vector[2];
        this.mVector[3] = 1.0f;
    }
    
    public static void transform(final float[] vector, final float[] m_vector, final float[] matrix) {
        vector[0] = m_vector[0] * matrix[0] + m_vector[1] * matrix[4] + m_vector[2] * matrix[8] + matrix[12];
        vector[1] = m_vector[0] * matrix[1] + m_vector[1] * matrix[5] + m_vector[2] * matrix[9] + matrix[13];
        vector[2] = m_vector[0] * matrix[2] + m_vector[1] * matrix[6] + m_vector[2] * matrix[10] + matrix[14];
    }
    
    public static void transform3(final float[] vector, final float[] m_vector, final float[] matrix) {
        vector[0] = m_vector[0] * matrix[0] + m_vector[1] * matrix[4] + m_vector[2] * matrix[8];
        vector[1] = m_vector[0] * matrix[1] + m_vector[1] * matrix[5] + m_vector[2] * matrix[9];
        vector[2] = m_vector[0] * matrix[2] + m_vector[1] * matrix[6] + m_vector[2] * matrix[10];
    }
}
