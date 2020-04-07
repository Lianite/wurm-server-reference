// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public class Transform
{
    public final Quaternion rotation;
    public final Vector3f translation;
    
    public Transform() {
        this.rotation = new Quaternion();
        this.translation = new Vector3f();
    }
    
    public final void identity() {
        this.rotation.identity();
        this.translation.zero();
    }
}
