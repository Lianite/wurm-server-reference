// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

public class ColoredChar
{
    public char chr;
    public float color;
    
    public ColoredChar(final char chr, final byte color) {
        this.chr = chr;
        this.color = color;
    }
    
    public char getChr() {
        return this.chr;
    }
    
    public void setChr(final char chr) {
        this.chr = chr;
    }
    
    public float getColor() {
        return this.color;
    }
    
    public void setColor(final float color) {
        this.color = color;
    }
}
