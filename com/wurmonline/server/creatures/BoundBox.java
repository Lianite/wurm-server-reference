// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.List;
import java.util.ArrayList;
import com.wurmonline.math.Vector2f;
import com.wurmonline.math.Vector3f;

public class BoundBox
{
    public BoxMatrix M;
    public Vector3f extent;
    
    public BoundBox() {
        this.M = new BoxMatrix(true);
        this.extent = new Vector3f(0.0f, 0.0f, 0.0f);
    }
    
    public BoundBox(final BoxMatrix m, final Vector3f e) {
        this.set(m, e);
    }
    
    public BoundBox(final BoxMatrix m, final Vector3f bl, final Vector3f bh) {
        this.set(m, bl, bh);
    }
    
    public void set(final BoxMatrix m, final Vector3f e) {
        this.M = m;
        this.extent = e;
    }
    
    public void set(final BoxMatrix m, final Vector3f bl, final Vector3f bh) {
        (this.M = m).translate(bh.add(bl).mult(0.5f));
        this.extent = bh.subtract(bl).divide(2.0f);
    }
    
    public final Vector3f getSize() {
        return this.extent.mult(2.0f);
    }
    
    public final Vector3f getCenterPoint() {
        return this.M.getTranslate();
    }
    
    public final boolean isPointInBox(final Vector3f inP) {
        final Vector3f P = this.M.InvertSimple().multiply(inP);
        return Math.abs(P.x) < this.extent.x && Math.abs(P.y) < this.extent.y;
    }
    
    private final Vector2f getIntersection(final Vector3f pp, final Vector3f cp, final Vector3f s1, final Vector3f s2) {
        final Vector2f L = new Vector2f(cp.x - pp.x, cp.y - pp.y);
        final Vector2f S = new Vector2f(s2.x - s1.x, s2.y - s1.y);
        final float dot = L.x * S.y - L.y * S.x;
        if (dot == 0.0f) {
            return null;
        }
        final Vector2f c = new Vector2f(s1.x - pp.x, s1.y - pp.y);
        final float t = (c.x * S.y - c.y * S.x) / dot;
        if (t < 0.0f || t > 1.0f) {
            return null;
        }
        final float u = (c.x * L.y - c.y * L.x) / dot;
        if (u < 0.0f || u > 1.0f) {
            return null;
        }
        Vector2f inter = null;
        final Vector2f LP = new Vector2f(pp.x, pp.y);
        inter = LP.add(L.mult(t));
        return inter;
    }
    
    public final float distOutside(final Vector3f inP, final Vector3f cpoint) {
        final BoxMatrix MInv = this.M.InvertSimple();
        final Vector3f LB1 = MInv.multiply(inP);
        final Vector3f LB2 = MInv.multiply(cpoint);
        final List<Vector2f> inters = new ArrayList<Vector2f>();
        Vector2f ii = this.getIntersection(LB1, LB2, new Vector3f(-this.extent.x, -this.extent.y, 1.0f), new Vector3f(-this.extent.x, this.extent.y, 1.0f));
        if (ii != null) {
            inters.add(ii);
        }
        ii = this.getIntersection(LB1, LB2, new Vector3f(-this.extent.x, this.extent.y, 1.0f), new Vector3f(this.extent.x, this.extent.y, 1.0f));
        if (ii != null) {
            inters.add(ii);
        }
        ii = this.getIntersection(LB1, LB2, new Vector3f(this.extent.x, this.extent.y, 1.0f), new Vector3f(this.extent.x, -this.extent.y, 1.0f));
        if (ii != null) {
            inters.add(ii);
        }
        ii = this.getIntersection(LB1, LB2, new Vector3f(this.extent.x, -this.extent.y, 1.0f), new Vector3f(-this.extent.x, -this.extent.y, 1.0f));
        if (ii != null) {
            inters.add(ii);
        }
        if (inters.size() > 0) {
            final Vector2f p2 = new Vector2f(LB1.x, LB1.y);
            float minLen = 0.0f;
            for (int i = 0; i < inters.size(); ++i) {
                final float len = p2.subtract(inters.get(i)).length();
                if (i == 0) {
                    minLen = len;
                }
                else if (len < minLen) {
                    minLen = len;
                }
            }
            return minLen;
        }
        return -1.0f;
    }
    
    public final Vector3f[] getInvRot() {
        final Vector3f[] result = { new Vector3f(this.M.mf[0], this.M.mf[1], this.M.mf[2]), new Vector3f(this.M.mf[4], this.M.mf[5], this.M.mf[6]), new Vector3f(this.M.mf[8], this.M.mf[9], this.M.mf[10]) };
        return result;
    }
}
