// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

public class Node
{
    private boolean render;
    float[] normals;
    private float height;
    private float x;
    private float y;
    private float bbBottom;
    private float bbHeight;
    private boolean visible;
    private byte texture;
    byte data;
    private Object object;
    
    Node() {
        this.normals = new float[3];
    }
    
    boolean isRender() {
        return this.render;
    }
    
    void setRender(final boolean render) {
        this.render = render;
    }
    
    float[] getNormals() {
        return this.normals;
    }
    
    void setNormals(final float[] normals) {
        this.normals = normals;
    }
    
    float getHeight() {
        return this.height;
    }
    
    void setHeight(final float height) {
        this.height = height;
    }
    
    float getX() {
        return this.x;
    }
    
    void setX(final float x) {
        this.x = x;
    }
    
    float getY() {
        return this.y;
    }
    
    void setY(final float y) {
        this.y = y;
    }
    
    float getBbBottom() {
        return this.bbBottom;
    }
    
    void setBbBottom(final float bbBottom) {
        this.bbBottom = bbBottom;
    }
    
    float getBbHeight() {
        return this.bbHeight;
    }
    
    void setBbHeight(final float bbHeight) {
        this.bbHeight = bbHeight;
    }
    
    boolean isVisible() {
        return this.visible;
    }
    
    void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    byte getTexture() {
        return this.texture;
    }
    
    void setTexture(final byte texture) {
        this.texture = texture;
    }
    
    void setData(final byte data) {
        this.data = data;
    }
    
    Object getObject() {
        return this.object;
    }
    
    void setObject(final Object object) {
        this.object = object;
    }
}
