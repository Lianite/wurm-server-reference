// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class Matrix
{
    private float[] matrix;
    
    public Matrix() {
        this.matrix = new float[16];
    }
    
    public Matrix(final Matrix matrix) {
        this.matrix = new float[16];
        this.matrix = matrix.matrix;
    }
    
    public float[] inverseRotateVect(final float[] pVect) {
        final float[] vec = { pVect[0] * this.matrix[0] + pVect[1] * this.matrix[1] + pVect[2] * this.matrix[2], pVect[0] * this.matrix[4] + pVect[1] * this.matrix[5] + pVect[2] * this.matrix[6], pVect[0] * this.matrix[8] + pVect[1] * this.matrix[9] + pVect[2] * this.matrix[10] };
        return vec;
    }
    
    public float[] inverseTranslateVect(final float[] pVect) {
        pVect[0] -= this.matrix[12];
        pVect[1] -= this.matrix[13];
        pVect[2] -= this.matrix[14];
        return pVect;
    }
    
    public void postMultiply(final Matrix m2) {
        final float[] newMatrix = { this.matrix[0] * m2.matrix[0] + this.matrix[4] * m2.matrix[1] + this.matrix[8] * m2.matrix[2], this.matrix[1] * m2.matrix[0] + this.matrix[5] * m2.matrix[1] + this.matrix[9] * m2.matrix[2], this.matrix[2] * m2.matrix[0] + this.matrix[6] * m2.matrix[1] + this.matrix[10] * m2.matrix[2], 0.0f, this.matrix[0] * m2.matrix[4] + this.matrix[4] * m2.matrix[5] + this.matrix[8] * m2.matrix[6], this.matrix[1] * m2.matrix[4] + this.matrix[5] * m2.matrix[5] + this.matrix[9] * m2.matrix[6], this.matrix[2] * m2.matrix[4] + this.matrix[6] * m2.matrix[5] + this.matrix[10] * m2.matrix[6], 0.0f, this.matrix[0] * m2.matrix[8] + this.matrix[4] * m2.matrix[9] + this.matrix[8] * m2.matrix[10], this.matrix[1] * m2.matrix[8] + this.matrix[5] * m2.matrix[9] + this.matrix[9] * m2.matrix[10], this.matrix[2] * m2.matrix[8] + this.matrix[6] * m2.matrix[9] + this.matrix[10] * m2.matrix[10], 0.0f, this.matrix[0] * m2.matrix[12] + this.matrix[4] * m2.matrix[13] + this.matrix[8] * m2.matrix[14] + this.matrix[12], this.matrix[1] * m2.matrix[12] + this.matrix[5] * m2.matrix[13] + this.matrix[9] * m2.matrix[14] + this.matrix[13], this.matrix[2] * m2.matrix[12] + this.matrix[6] * m2.matrix[13] + this.matrix[10] * m2.matrix[14] + this.matrix[14], 1.0f };
        this.set(newMatrix);
    }
    
    public void postMultiplyFull(final Matrix m2) {
        final float[] newMatrix = { this.matrix[0] * m2.matrix[0] + this.matrix[4] * m2.matrix[1] + this.matrix[8] * m2.matrix[2] + this.matrix[12] * m2.matrix[3], this.matrix[1] * m2.matrix[0] + this.matrix[5] * m2.matrix[1] + this.matrix[9] * m2.matrix[2] + this.matrix[13] * m2.matrix[3], this.matrix[2] * m2.matrix[0] + this.matrix[6] * m2.matrix[1] + this.matrix[10] * m2.matrix[2] + this.matrix[14] * m2.matrix[3], this.matrix[3] * m2.matrix[0] + this.matrix[7] * m2.matrix[1] + this.matrix[11] * m2.matrix[2] + this.matrix[15] * m2.matrix[3], this.matrix[0] * m2.matrix[4] + this.matrix[4] * m2.matrix[5] + this.matrix[8] * m2.matrix[6] + this.matrix[12] * m2.matrix[7], this.matrix[1] * m2.matrix[4] + this.matrix[5] * m2.matrix[5] + this.matrix[9] * m2.matrix[6] + this.matrix[13] * m2.matrix[7], this.matrix[2] * m2.matrix[4] + this.matrix[6] * m2.matrix[5] + this.matrix[10] * m2.matrix[6] + this.matrix[14] * m2.matrix[7], this.matrix[3] * m2.matrix[4] + this.matrix[7] * m2.matrix[5] + this.matrix[11] * m2.matrix[6] + this.matrix[15] * m2.matrix[7], this.matrix[0] * m2.matrix[8] + this.matrix[4] * m2.matrix[9] + this.matrix[8] * m2.matrix[10] + this.matrix[12] * m2.matrix[11], this.matrix[1] * m2.matrix[8] + this.matrix[5] * m2.matrix[9] + this.matrix[9] * m2.matrix[10] + this.matrix[13] * m2.matrix[11], this.matrix[2] * m2.matrix[8] + this.matrix[6] * m2.matrix[9] + this.matrix[10] * m2.matrix[10] + this.matrix[14] * m2.matrix[11], this.matrix[3] * m2.matrix[8] + this.matrix[7] * m2.matrix[9] + this.matrix[11] * m2.matrix[10] + this.matrix[15] * m2.matrix[11], this.matrix[0] * m2.matrix[12] + this.matrix[4] * m2.matrix[13] + this.matrix[8] * m2.matrix[14] + this.matrix[12] * m2.matrix[15], this.matrix[1] * m2.matrix[12] + this.matrix[5] * m2.matrix[13] + this.matrix[9] * m2.matrix[14] + this.matrix[13] * m2.matrix[15], this.matrix[2] * m2.matrix[12] + this.matrix[6] * m2.matrix[13] + this.matrix[10] * m2.matrix[14] + this.matrix[14] * m2.matrix[15], this.matrix[3] * m2.matrix[12] + this.matrix[7] * m2.matrix[13] + this.matrix[11] * m2.matrix[14] + this.matrix[15] * m2.matrix[15] };
        this.set(newMatrix);
    }
    
    public final Matrix setTranslation(final float[] translation) {
        this.matrix[12] = translation[0];
        this.matrix[13] = translation[1];
        this.matrix[14] = translation[2];
        return this;
    }
    
    public final Matrix setTranslation(final float x, final float y, final float z) {
        this.matrix[12] = x;
        this.matrix[13] = y;
        this.matrix[14] = z;
        return this;
    }
    
    public final Matrix setTranslation(final Vector translation) {
        this.matrix[12] = translation.x();
        this.matrix[13] = translation.y();
        this.matrix[14] = translation.z();
        return this;
    }
    
    public void setInverseTranslation(final float[] translation) {
        this.matrix[12] = -translation[0];
        this.matrix[13] = -translation[1];
        this.matrix[14] = -translation[2];
    }
    
    public void setRotationDegrees(final float[] angles) {
        final float[] vec = { (float)(angles[0] * 180.0 / 3.141592653589793), (float)(angles[1] * 180.0 / 3.141592653589793), (float)(angles[2] * 180.0 / 3.141592653589793) };
        this.setRotationRadians(vec);
    }
    
    public void setInverseRotationDegrees(final float[] angles) {
        final float[] vec = { (float)(angles[0] * 180.0 / 3.141592653589793), (float)(angles[1] * 180.0 / 3.141592653589793), (float)(angles[2] * 180.0 / 3.141592653589793) };
        this.setInverseRotationRadians(vec);
    }
    
    public void setRotationRadians(final float[] angles) {
        final float cr = (float)Math.cos(angles[0]);
        final float sr = (float)Math.sin(angles[0]);
        final float cp = (float)Math.cos(angles[1]);
        final float sp = (float)Math.sin(angles[1]);
        final float cy = (float)Math.cos(angles[2]);
        final float sy = (float)Math.sin(angles[2]);
        this.matrix[0] = cp * cy;
        this.matrix[1] = cp * sy;
        this.matrix[2] = -sp;
        final float srsp = sr * sp;
        final float crsp = cr * sp;
        this.matrix[4] = srsp * cy - cr * sy;
        this.matrix[5] = srsp * sy + cr * cy;
        this.matrix[6] = sr * cp;
        this.matrix[8] = crsp * cy + sr * sy;
        this.matrix[9] = crsp * sy - sr * cy;
        this.matrix[10] = cr * cp;
    }
    
    public void setInverseRotationRadians(final float[] angles) {
        final float cr = (float)Math.cos(angles[0]);
        final float sr = (float)Math.sin(angles[0]);
        final float cp = (float)Math.cos(angles[1]);
        final float sp = (float)Math.sin(angles[1]);
        final float cy = (float)Math.cos(angles[2]);
        final float sy = (float)Math.sin(angles[2]);
        this.matrix[0] = cp * cy;
        this.matrix[4] = cp * sy;
        this.matrix[8] = -sp;
        final float srsp = sr * sp;
        final float crsp = cr * sp;
        this.matrix[1] = srsp * cy - cr * sy;
        this.matrix[5] = srsp * sy + cr * cy;
        this.matrix[9] = sr * cp;
        this.matrix[2] = crsp * cy + sr * sy;
        this.matrix[6] = crsp * sy - sr * cy;
        this.matrix[10] = cr * cp;
    }
    
    public final Matrix setRotationQuaternion(final Quaternion quaternion) {
        final float[] quat = quaternion.getQuat();
        this.matrix[0] = (float)(1.0 - 2.0 * quat[1] * quat[1] - 2.0 * quat[2] * quat[2]);
        this.matrix[1] = (float)(2.0 * quat[0] * quat[1] + 2.0 * quat[3] * quat[2]);
        this.matrix[2] = (float)(2.0 * quat[0] * quat[2] - 2.0 * quat[3] * quat[1]);
        this.matrix[4] = (float)(2.0 * quat[0] * quat[1] - 2.0 * quat[3] * quat[2]);
        this.matrix[5] = (float)(1.0 - 2.0 * quat[0] * quat[0] - 2.0 * quat[2] * quat[2]);
        this.matrix[6] = (float)(2.0 * quat[1] * quat[2] + 2.0 * quat[3] * quat[0]);
        this.matrix[8] = (float)(2.0 * quat[0] * quat[2] + 2.0 * quat[3] * quat[1]);
        this.matrix[9] = (float)(2.0 * quat[1] * quat[2] - 2.0 * quat[3] * quat[0]);
        this.matrix[10] = (float)(1.0 - 2.0 * quat[0] * quat[0] - 2.0 * quat[1] * quat[1]);
        final float[] matrix = this.matrix;
        final int n = 3;
        final float[] matrix2 = this.matrix;
        final int n2 = 7;
        final float[] matrix3 = this.matrix;
        final int n3 = 11;
        final float[] matrix4 = this.matrix;
        final int n4 = 12;
        final float[] matrix5 = this.matrix;
        final int n5 = 13;
        final float[] matrix6 = this.matrix;
        final int n6 = 14;
        final float n7 = 0.0f;
        matrix5[n5] = (matrix6[n6] = n7);
        matrix3[n3] = (matrix4[n4] = n7);
        matrix[n] = (matrix2[n2] = n7);
        this.matrix[15] = 1.0f;
        return this;
    }
    
    public void set(final float[] matrix) {
        this.matrix = matrix;
    }
    
    public float get(final int i, final int j) {
        return this.matrix[4 * i + j];
    }
    
    public void set(final int i, final int j, final float val) {
        this.matrix[4 * i + j] = val;
    }
    
    public final Matrix loadIdentity() {
        for (int i = 0; i < 16; ++i) {
            this.matrix[i] = 0.0f;
        }
        final float[] matrix = this.matrix;
        final int n = 0;
        final float[] matrix2 = this.matrix;
        final int n2 = 5;
        final float[] matrix3 = this.matrix;
        final int n3 = 10;
        final float[] matrix4 = this.matrix;
        final int n4 = 15;
        final float n5 = 1.0f;
        matrix3[n3] = (matrix4[n4] = n5);
        matrix[n] = (matrix2[n2] = n5);
        return this;
    }
    
    public void setScale(final float scalX, final float scalY, final float scalZ) {
        final float[] matrix = this.matrix;
        final int n = 0;
        matrix[n] *= scalX;
        final float[] matrix2 = this.matrix;
        final int n2 = 5;
        matrix2[n2] *= scalY;
        final float[] matrix3 = this.matrix;
        final int n3 = 10;
        matrix3[n3] *= scalZ;
    }
    
    public Matrix inverse() {
        this.matrix[0] = -this.matrix[0];
        this.matrix[1] = -this.matrix[1];
        this.matrix[2] = -this.matrix[2];
        this.matrix[4] = -this.matrix[4];
        this.matrix[5] = -this.matrix[5];
        this.matrix[6] = -this.matrix[6];
        this.matrix[8] = -this.matrix[8];
        this.matrix[9] = -this.matrix[9];
        this.matrix[10] = -this.matrix[10];
        return this;
    }
    
    public final float[] getMatrix() {
        return this.matrix;
    }
    
    public final void setMatrix(final float[] matrix) {
        this.matrix = matrix;
    }
    
    public final Matrix setAxisX(final float x, final float y, final float z) {
        this.matrix[0] = x;
        this.matrix[1] = y;
        this.matrix[2] = z;
        return this;
    }
    
    public final Matrix setAxisY(final float x, final float y, final float z) {
        this.matrix[4] = x;
        this.matrix[5] = y;
        this.matrix[6] = z;
        return this;
    }
    
    public final Matrix setAxisZ(final float x, final float y, final float z) {
        this.matrix[8] = x;
        this.matrix[9] = y;
        this.matrix[10] = z;
        return this;
    }
}
