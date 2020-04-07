// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.Random;

public final class WurmColor
{
    private static final Random mixRand;
    
    public static final int createColor(final int r, final int g, final int b) {
        return ((b & 0xFF) << 16) + ((g & 0xFF) << 8) + (r & 0xFF);
    }
    
    public static final int getColorRed(final int color) {
        return color & 0xFF;
    }
    
    public static final int getColorGreen(final int color) {
        return color >> 8 & 0xFF;
    }
    
    public static final int getColorBlue(final int color) {
        return color >> 16 & 0xFF;
    }
    
    public static final int mixColors(final int color1, final int weight1, final int color2, final int weight2, final float avgQl) {
        float modifier = 0.0f;
        if (avgQl < 100.0f && WurmColor.mixRand.nextInt(3) == 0) {
            modifier = 0.01f * (100.0f - avgQl) / 100.0f;
        }
        int r = (getColorRed(color1) * weight1 + getColorRed(color2) * weight2) / (weight1 + weight2);
        if (r > 128) {
            r = (int)(128.0f + (r - 128) * (1.0f - modifier));
        }
        else {
            r += (int)((128 - r) * modifier);
        }
        int g = (getColorGreen(color1) * weight1 + getColorGreen(color2) * weight2) / (weight1 + weight2);
        if (g > 128) {
            g = (int)(128.0f + (g - 128) * (1.0f - modifier));
        }
        else {
            g += (int)((128 - g) * modifier);
        }
        int b = (getColorBlue(color1) * weight1 + getColorBlue(color2) * weight2) / (weight1 + weight2);
        if (b > 128) {
            b = (int)(128.0f + (b - 128) * (1.0f - modifier));
        }
        else {
            b += (int)((128 - b) * modifier);
        }
        return createColor(r, g, b);
    }
    
    public static int getInitialColor(final int itemTemplateId, final float qualityLevel) {
        if (itemTemplateId == 431) {
            return getBaseBlack(qualityLevel);
        }
        if (itemTemplateId == 432) {
            return getBaseWhite(qualityLevel);
        }
        if (itemTemplateId == 433) {
            return getBaseRed(qualityLevel);
        }
        if (itemTemplateId == 435) {
            return getBaseGreen(qualityLevel);
        }
        if (itemTemplateId == 434) {
            return getBaseBlue(qualityLevel);
        }
        return -1;
    }
    
    public static int getCompositeColor(final int color, final int weight, final int itemTemplateId, final float qualityLevel) {
        final int componentWeight = 1000;
        if (itemTemplateId == 439) {
            final int r = (getColorRed(color) * weight + getColorRed(getInitialColor(433, qualityLevel)) * 1000) / (weight + 1000);
            final int g = getColorGreen(color);
            final int b = getColorBlue(color);
            return createColor(r, g, b);
        }
        if (itemTemplateId == 47 || itemTemplateId == 195) {
            final int r = getColorRed(color);
            final int g = (getColorGreen(color) * weight + getColorGreen(getInitialColor(435, qualityLevel)) * 1000) / (weight + 1000);
            final int b = getColorBlue(color);
            return createColor(r, g, b);
        }
        if (itemTemplateId == 440) {
            final int r = getColorRed(color);
            final int g = getColorGreen(color);
            final int b = (getColorBlue(color) * weight + getColorBlue(getInitialColor(434, qualityLevel)) * 1000) / (weight + 1000);
            return createColor(r, g, b);
        }
        return color;
    }
    
    public static int getCompositeColor(final int color, final int itemTemplateId, final float qualityLevel) {
        if (itemTemplateId == 433) {
            int r = getColorRed(color);
            final int g = getColorGreen(color);
            final int b = getColorBlue(color);
            final int newR = getColorRed(getBaseRed(qualityLevel));
            if (newR > r) {
                r = newR;
            }
            return createColor(r, g, b);
        }
        if (itemTemplateId == 435) {
            final int r = getColorRed(color);
            int g = getColorGreen(color);
            final int b = getColorBlue(color);
            final int newG = getColorGreen(getBaseGreen(qualityLevel));
            if (newG > g) {
                g = newG;
            }
            return createColor(r, g, b);
        }
        if (itemTemplateId == 434) {
            final int r = getColorRed(color);
            final int g = getColorGreen(color);
            int b = getColorBlue(color);
            final int newB = getColorBlue(getBaseBlue(qualityLevel));
            if (newB > b) {
                b = newB;
            }
            return createColor(r, g, b);
        }
        return color;
    }
    
    static final int getBaseRed(final float ql) {
        return createColor(155 + (int)ql, 100 - (int)ql, 100 - (int)ql);
    }
    
    static final int getBaseGreen(final float ql) {
        return createColor(100 - (int)ql, 155 + (int)ql, 100 - (int)ql);
    }
    
    static final int getBaseBlue(final float ql) {
        return createColor(100 - (int)ql, 100 - (int)ql, 155 + (int)ql);
    }
    
    static final int getBaseWhite(final float ql) {
        return createColor(155 + (int)ql, 155 + (int)ql, 155 + (int)ql);
    }
    
    static final int getBaseBlack(final float ql) {
        return createColor(100 - (int)ql, 100 - (int)ql, 100 - (int)ql);
    }
    
    public static final String getRGBDescription(final int aWurmColor) {
        return "R=" + getColorRed(aWurmColor) + ", G=" + getColorGreen(aWurmColor) + ", B=" + getColorBlue(aWurmColor);
    }
    
    static {
        mixRand = new Random();
    }
}
