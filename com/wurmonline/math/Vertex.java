// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class Vertex
{
    public float[] point;
    public byte flags;
    public float[] vertex;
    public byte boneId;
    public byte refCount;
    public long lastRotateTime;
    public float[] rotatedVertex;
    public float[] rotatedNormal;
    
    public Vertex() {
        this.point = new float[3];
        this.vertex = new float[3];
        this.lastRotateTime = 0L;
        this.rotatedVertex = new float[3];
        this.rotatedNormal = new float[3];
    }
}
