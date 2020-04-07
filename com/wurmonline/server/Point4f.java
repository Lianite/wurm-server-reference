// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

public final class Point4f
{
    private float posx;
    private float posy;
    private float posz;
    private float rot;
    
    public Point4f() {
        this.posx = 0.0f;
        this.posy = 0.0f;
        this.posz = 0.0f;
        this.rot = 0.0f;
    }
    
    public Point4f(final float posx, final float posy) {
        this.posx = posx;
        this.posy = posy;
        this.posz = 0.0f;
        this.rot = 0.0f;
    }
    
    public Point4f(final float posx, final float posy, final float posz) {
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.rot = 0.0f;
    }
    
    public Point4f(final float posx, final float posy, final float posz, final float rot) {
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.rot = rot;
    }
    
    public Point4f(final Point4f point) {
        this.posx = point.posx;
        this.posy = point.posy;
        this.posz = point.posz;
        this.rot = point.rot;
    }
    
    public float getPosX() {
        return this.posx;
    }
    
    public void setPosX(final float posx) {
        this.posx = posx;
    }
    
    public float getPosY() {
        return this.posy;
    }
    
    public void setPosY(final float posy) {
        this.posy = posy;
    }
    
    public float getPosZ() {
        return this.posz;
    }
    
    public void setPosZ(final float posz) {
        this.posz = posz;
    }
    
    public float getRot() {
        return this.rot;
    }
    
    public void setRot(final float rot) {
        this.rot = rot;
    }
    
    public void setXY(final float posx, final float posy) {
        this.posx = posx;
        this.posy = posy;
    }
    
    public void setXYZ(final float posx, final float posy, final float posz) {
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
    }
    
    public void setXYR(final float posx, final float posy, final float rot) {
        this.posx = posx;
        this.posy = posy;
        this.rot = rot;
    }
    
    public void setXYZR(final float posx, final float posy, final float posz, final float rot) {
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.rot = rot;
    }
    
    public int getTileX() {
        return (int)this.posx >> 2;
    }
    
    public int getTileY() {
        return (int)this.posy >> 2;
    }
}
