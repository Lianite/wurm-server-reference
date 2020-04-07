// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.logging.Logger;
import java.io.Externalizable;

public final class Vector2f implements Externalizable, Cloneable
{
    private static final Logger logger;
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    
    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2f() {
        final float n = 0.0f;
        this.y = n;
        this.x = n;
    }
    
    public Vector2f(final Vector2f vector2f) {
        this.x = vector2f.x;
        this.y = vector2f.y;
    }
    
    public Vector2f set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2f set(final Vector2f vec) {
        this.x = vec.x;
        this.y = vec.y;
        return this;
    }
    
    public Vector2f add(final Vector2f vec) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, null returned.");
            return null;
        }
        return new Vector2f(this.x + vec.x, this.y + vec.y);
    }
    
    public Vector2f add(final float dx, final float dy) {
        return new Vector2f(this.x + dx, this.y + dy);
    }
    
    public Vector2f addLocal(final Vector2f vec) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, null returned.");
            return null;
        }
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }
    
    public Vector2f addLocal(final float addX, final float addY) {
        this.x += addX;
        this.y += addY;
        return this;
    }
    
    public Vector2f add(final Vector2f vec, Vector2f result) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, null returned.");
            return null;
        }
        if (result == null) {
            result = new Vector2f();
        }
        result.x = this.x + vec.x;
        result.y = this.y + vec.y;
        return result;
    }
    
    public float dot(final Vector2f vec) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, 0 returned.");
            return 0.0f;
        }
        return this.x * vec.x + this.y * vec.y;
    }
    
    public Vector3f cross(final Vector2f v) {
        return new Vector3f(0.0f, 0.0f, this.determinant(v));
    }
    
    public float determinant(final Vector2f v) {
        return this.x * v.y - this.y * v.x;
    }
    
    public void interpolate(final Vector2f finalVec, final float changeAmnt) {
        this.x = (1.0f - changeAmnt) * this.x + changeAmnt * finalVec.x;
        this.y = (1.0f - changeAmnt) * this.y + changeAmnt * finalVec.y;
    }
    
    public void interpolate(final Vector2f beginVec, final Vector2f finalVec, final float changeAmnt) {
        this.x = (1.0f - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
        this.y = (1.0f - changeAmnt) * beginVec.y + changeAmnt * finalVec.y;
    }
    
    public static boolean isValidVector(final Vector2f vector) {
        return vector != null && !Float.isNaN(vector.x) && !Float.isNaN(vector.y) && !Float.isInfinite(vector.x) && !Float.isInfinite(vector.y);
    }
    
    public float length() {
        return FastMath.sqrt(this.lengthSquared());
    }
    
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public float distanceSquared(final Vector2f v) {
        final double dx = this.x - v.x;
        final double dy = this.y - v.y;
        return (float)(dx * dx + dy * dy);
    }
    
    public float distanceSquared(final float otherX, final float otherY) {
        final double dx = this.x - otherX;
        final double dy = this.y - otherY;
        return (float)(dx * dx + dy * dy);
    }
    
    public float distance(final Vector2f v) {
        return FastMath.sqrt(this.distanceSquared(v));
    }
    
    public Vector2f mult(final float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }
    
    public Vector2f multLocal(final float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    
    public Vector2f multLocal(final Vector2f vec) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, null returned.");
            return null;
        }
        this.x *= vec.x;
        this.y *= vec.y;
        return this;
    }
    
    public Vector2f mult(final float scalar, Vector2f product) {
        if (null == product) {
            product = new Vector2f();
        }
        product.x = this.x * scalar;
        product.y = this.y * scalar;
        return product;
    }
    
    public Vector2f divide(final float scalar) {
        return new Vector2f(this.x / scalar, this.y / scalar);
    }
    
    public Vector2f divideLocal(final float scalar) {
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }
    
    public Vector2f negate() {
        return new Vector2f(-this.x, -this.y);
    }
    
    public Vector2f negateLocal() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }
    
    public Vector2f subtract(final Vector2f vec) {
        return this.subtract(vec, null);
    }
    
    public Vector2f subtract(final Vector2f vec, Vector2f store) {
        if (store == null) {
            store = new Vector2f();
        }
        store.x = this.x - vec.x;
        store.y = this.y - vec.y;
        return store;
    }
    
    public Vector2f subtract(final float valX, final float valY) {
        return new Vector2f(this.x - valX, this.y - valY);
    }
    
    public Vector2f subtractLocal(final Vector2f vec) {
        if (null == vec) {
            Vector2f.logger.warning("Provided vector is null, null returned.");
            return null;
        }
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }
    
    public Vector2f subtractLocal(final float valX, final float valY) {
        this.x -= valX;
        this.y -= valY;
        return this;
    }
    
    public Vector2f normalize() {
        final float length = this.length();
        if (length != 0.0f) {
            return this.divide(length);
        }
        return this.divide(1.0f);
    }
    
    public Vector2f normalizeLocal() {
        final float length = this.length();
        if (length != 0.0f) {
            return this.divideLocal(length);
        }
        return this.divideLocal(1.0f);
    }
    
    public float smallestAngleBetween(final Vector2f otherVector) {
        final float dotProduct = this.dot(otherVector);
        final float angle = FastMath.acos(dotProduct);
        return angle;
    }
    
    public float angleBetween(final Vector2f otherVector) {
        final float angle = FastMath.atan2(otherVector.y, otherVector.x) - FastMath.atan2(this.y, this.x);
        return angle;
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getAngle() {
        return -FastMath.atan2(this.y, this.x);
    }
    
    public void zero() {
        final float n = 0.0f;
        this.y = n;
        this.x = n;
    }
    
    @Override
    public int hashCode() {
        int hash = 37;
        hash += 37 * hash + Float.floatToIntBits(this.x);
        hash += 37 * hash + Float.floatToIntBits(this.y);
        return hash;
    }
    
    public Vector2f clone() {
        try {
            return (Vector2f)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    public float[] toArray(float[] floats) {
        if (floats == null) {
            floats = new float[2];
        }
        floats[0] = this.x;
        floats[1] = this.y;
        return floats;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Vector2f)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        final Vector2f comp = (Vector2f)o;
        return Float.compare(this.x, comp.x) == 0 && Float.compare(this.y, comp.y) == 0;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ')';
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readFloat();
        this.y = in.readFloat();
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
    }
    
    public Class<? extends Vector2f> getClassTag() {
        return this.getClass();
    }
    
    public void rotateAroundOrigin(float angle, final boolean cw) {
        if (cw) {
            angle = -angle;
        }
        final float newX = FastMath.cos(angle) * this.x - FastMath.sin(angle) * this.y;
        final float newY = FastMath.sin(angle) * this.x + FastMath.cos(angle) * this.y;
        this.x = newX;
        this.y = newY;
    }
    
    static {
        logger = Logger.getLogger(Vector2f.class.getName());
    }
}
