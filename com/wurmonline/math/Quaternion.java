// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class Quaternion
{
    private float[] quat;
    
    public Quaternion() {
        this.quat = new float[4];
    }
    
    public Quaternion(final float[] angles) {
        this.quat = new float[4];
        this.fromAngles(angles);
    }
    
    public Quaternion(final Quaternion q1, final Quaternion q2, final float interp) {
        this.quat = new float[4];
        this.slerp(q1, q2, interp);
    }
    
    public Quaternion mult(final Quaternion q) {
        final float x = this.quat[1] * q.quat[2] - this.quat[2] * q.quat[1] + this.quat[3] * q.quat[0] + this.quat[0] * q.quat[3];
        final float y = this.quat[2] * q.quat[0] - this.quat[0] * q.quat[2] + this.quat[3] * q.quat[1] + this.quat[1] * q.quat[3];
        final float z = this.quat[0] * q.quat[1] - this.quat[1] * q.quat[0] + this.quat[3] * q.quat[2] + this.quat[2] * q.quat[3];
        final float s = this.quat[3] * q.quat[3] - (this.quat[0] * q.quat[0] + this.quat[1] * q.quat[1] + this.quat[2] * q.quat[2]);
        this.quat[0] = x;
        this.quat[1] = y;
        this.quat[2] = z;
        this.quat[3] = s;
        return this;
    }
    
    public final Quaternion fromAngles(final float[] angles) {
        return this.fromAngles(angles[0], angles[1], angles[2]);
    }
    
    public final Quaternion fromAngles(final float x, final float y, final float z) {
        float angle = z * 0.5f;
        final double sy = Math.sin(angle);
        final double cy = Math.cos(angle);
        angle = y * 0.5f;
        final double sp = Math.sin(angle);
        final double cp = Math.cos(angle);
        angle = x * 0.5f;
        final double sr = Math.sin(angle);
        final double cr = Math.cos(angle);
        final double crcp = cr * cp;
        final double srsp = sr * sp;
        this.quat[0] = (float)(sr * cp * cy - cr * sp * sy);
        this.quat[1] = (float)(cr * sp * cy + sr * cp * sy);
        this.quat[2] = (float)(crcp * sy - srsp * cy);
        this.quat[3] = (float)(crcp * cy + srsp * sy);
        return this;
    }
    
    public final Quaternion fromAxisAngle(final Vector3f axis, final float angle) {
        final float halfangle = 0.5f * angle;
        final float sinval = (float)Math.sin(halfangle);
        this.quat[0] = sinval * axis.x;
        this.quat[1] = sinval * axis.y;
        this.quat[2] = sinval * axis.z;
        this.quat[3] = (float)Math.cos(halfangle);
        return this.normalize();
    }
    
    public void slerp(final Quaternion q1, final Quaternion q2, final float interp) {
        float a = 0.0f;
        float b = 0.0f;
        for (int i = 0; i < 4; ++i) {
            a += (q1.quat[i] - q2.quat[i]) * (q1.quat[i] - q2.quat[i]);
            b += (q1.quat[i] + q2.quat[i]) * (q1.quat[i] + q2.quat[i]);
        }
        if (a > b) {
            q2.negate();
        }
        final float cosom = q1.quat[0] * q2.quat[0] + q1.quat[1] * q2.quat[1] + q1.quat[2] * q2.quat[2] + q1.quat[3] * q2.quat[3];
        if (1.0 + cosom > 1.0E-8) {
            double sclq1;
            double sclq2;
            if (1.0 - cosom > 1.0E-8) {
                final double omega = Math.acos(cosom);
                final double sinom = Math.sin(omega);
                sclq1 = Math.sin((1.0 - interp) * omega) / sinom;
                sclq2 = Math.sin(interp * omega) / sinom;
            }
            else {
                sclq1 = 1.0 - interp;
                sclq2 = interp;
            }
            for (int i = 0; i < 4; ++i) {
                this.quat[i] = (float)(sclq1 * q1.quat[i] + sclq2 * q2.quat[i]);
            }
        }
        else {
            this.quat[0] = -q1.quat[1];
            this.quat[1] = q1.quat[0];
            this.quat[2] = -q1.quat[3];
            this.quat[3] = q1.quat[2];
            final double sclq1 = Math.sin((1.0 - interp) * 0.5 * 3.141592653589793);
            final double sclq2 = Math.sin(interp * 0.5 * 3.141592653589793);
            for (int i = 0; i < 3; ++i) {
                this.quat[i] = (float)(sclq1 * q1.quat[i] + sclq2 * this.quat[i]);
            }
        }
    }
    
    public void negate() {
        this.quat[0] = -this.quat[0];
        this.quat[1] = -this.quat[1];
        this.quat[2] = -this.quat[2];
        this.quat[3] = -this.quat[3];
    }
    
    public void conjugate() {
        this.quat[0] = -this.quat[0];
        this.quat[1] = -this.quat[1];
        this.quat[2] = -this.quat[2];
        this.quat[3] = this.quat[3];
    }
    
    public final void identity() {
        this.quat[0] = 0.0f;
        this.quat[1] = 0.0f;
        this.quat[2] = 0.0f;
        this.quat[3] = 1.0f;
    }
    
    public final Quaternion normalize() {
        final float norm = this.quat[0] * this.quat[0] + this.quat[1] * this.quat[1] + this.quat[2] * this.quat[2] + this.quat[3] * this.quat[3];
        final float invscale = 1.0f / (float)Math.sqrt(norm);
        final float[] quat = this.quat;
        final int n = 0;
        quat[n] *= invscale;
        final float[] quat2 = this.quat;
        final int n2 = 1;
        quat2[n2] *= invscale;
        final float[] quat3 = this.quat;
        final int n3 = 2;
        quat3[n3] *= invscale;
        final float[] quat4 = this.quat;
        final int n4 = 3;
        quat4[n4] *= invscale;
        return this;
    }
    
    public final Quaternion fromMatrix(final Matrix m) {
        final float[] mat = m.getMatrix();
        final float trace = mat[0] + mat[5] + mat[10];
        if (trace > 0.0f) {
            float root = (float)Math.sqrt(trace + 1.0f);
            this.quat[3] = 0.5f * root;
            root = 0.5f / root;
            this.quat[0] = (mat[6] - mat[9]) * root;
            this.quat[1] = (mat[8] - mat[2]) * root;
            this.quat[2] = (mat[1] - mat[4]) * root;
        }
        else {
            int i = 0;
            int j = 1;
            int k = 2;
            if (mat[5] > mat[0]) {
                i = 1;
                j = 2;
                k = 0;
            }
            else if (mat[10] > mat[i + 4 * i]) {
                i = 2;
                j = 0;
                k = 1;
            }
            float root = (float)Math.sqrt(mat[i + 4 * i] - mat[j + 4 * j] - mat[k + 4 * k] + 1.0f);
            this.quat[i] = 0.5f * root;
            root = 0.5f / root;
            this.quat[j] = (mat[j + 4 * i] + mat[i + 4 * j]) * root;
            this.quat[k] = (mat[k + 4 * i] + mat[i + 4 * k]) * root;
            this.quat[3] = (mat[k + 4 * j] - mat[j + 4 * k]) * root;
        }
        return this.normalize();
    }
    
    public final Vector3f rotate(final Vector3f v, Vector3f result) {
        if (result == null) {
            result = new Vector3f();
        }
        final float v1x = this.quat[1] * v.z - this.quat[2] * v.y + this.quat[3] * v.x;
        final float v1y = this.quat[2] * v.x - this.quat[0] * v.z + this.quat[3] * v.y;
        final float v1z = this.quat[0] * v.y - this.quat[1] * v.x + this.quat[3] * v.z;
        final float dotv = this.quat[0] * v.x + this.quat[1] * v.y + this.quat[2] * v.z;
        result.x = this.quat[0] * dotv + this.quat[3] * v1x - (v1y * this.quat[2] - v1z * this.quat[1]);
        result.y = this.quat[1] * dotv + this.quat[3] * v1y - (v1z * this.quat[0] - v1x * this.quat[2]);
        result.z = this.quat[2] * dotv + this.quat[3] * v1z - (v1x * this.quat[1] - v1y * this.quat[0]);
        return result;
    }
    
    public final Vector rotate(final Vector v) {
        final float vx = v.x();
        final float vy = v.y();
        final float vz = v.z();
        final float v1x = this.quat[1] * vz - this.quat[2] * vy + this.quat[3] * vx;
        final float v1y = this.quat[2] * vx - this.quat[0] * vz + this.quat[3] * vy;
        final float v1z = this.quat[0] * vy - this.quat[1] * vx + this.quat[3] * vz;
        final float dotv = this.quat[0] * vx + this.quat[1] * vy + this.quat[2] * vz;
        return v.set(this.quat[0] * dotv + this.quat[3] * v1x - (v1y * this.quat[2] - v1z * this.quat[1]), this.quat[1] * dotv + this.quat[3] * v1y - (v1z * this.quat[0] - v1x * this.quat[2]), this.quat[2] * dotv + this.quat[3] * v1z - (v1x * this.quat[1] - v1y * this.quat[0]));
    }
    
    public final float[] getQuat() {
        return this.quat;
    }
    
    public final void setQuat(final float[] quat) {
        this.quat = quat;
    }
    
    public final void set(final Quaternion q) {
        this.quat[0] = q.quat[0];
        this.quat[1] = q.quat[1];
        this.quat[2] = q.quat[2];
        this.quat[3] = q.quat[3];
    }
}
