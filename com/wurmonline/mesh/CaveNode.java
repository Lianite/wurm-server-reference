// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

public final class CaveNode extends Node
{
    private int special;
    private float[] normals2;
    private int ceilingTexture;
    private float data;
    
    int getSpecial() {
        return this.special;
    }
    
    void setSpecial(final int special) {
        this.special = special;
    }
    
    float[] getNormals2() {
        return this.normals2;
    }
    
    void setNormals2(final float[] normals2) {
        this.normals2 = normals2;
    }
    
    int getCeilingTexture() {
        return this.ceilingTexture;
    }
    
    void setCeilingTexture(final int ceilingTexture) {
        this.ceilingTexture = ceilingTexture;
    }
    
    float getData() {
        return this.data;
    }
    
    void setData(final float data) {
        this.data = data;
    }
}
