// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

public final class ColorDefinitions
{
    public static final float[] COLOR_SYSTEM;
    public static final float[] COLOR_ERROR;
    public static final float[] COLOR_WHITE;
    public static final float[] COLOR_BLACK;
    public static final float[] COLOR_NAVY_BLUE;
    public static final float[] COLOR_GREEN;
    public static final float[] COLOR_RED;
    public static final float[] COLOR_MAROON;
    public static final float[] COLOR_PURPLE;
    public static final float[] COLOR_ORANGE;
    public static final float[] COLOR_YELLOW;
    public static final float[] COLOR_LIME;
    public static final float[] COLOR_TEAL;
    public static final float[] COLOR_CYAN;
    public static final float[] COLOR_ROYAL_BLUE;
    public static final float[] COLOR_FUCHSIA;
    public static final float[] COLOR_GREY;
    public static final float[] COLOR_SILVER;
    
    public static float[] getColor(final byte colorCode) {
        switch (colorCode) {
            case 0: {
                return ColorDefinitions.COLOR_WHITE;
            }
            case 1: {
                return ColorDefinitions.COLOR_BLACK;
            }
            case 2: {
                return ColorDefinitions.COLOR_NAVY_BLUE;
            }
            case 3: {
                return ColorDefinitions.COLOR_GREEN;
            }
            case 4: {
                return ColorDefinitions.COLOR_RED;
            }
            case 5: {
                return ColorDefinitions.COLOR_MAROON;
            }
            case 6: {
                return ColorDefinitions.COLOR_PURPLE;
            }
            case 7: {
                return ColorDefinitions.COLOR_ORANGE;
            }
            case 8: {
                return ColorDefinitions.COLOR_YELLOW;
            }
            case 9: {
                return ColorDefinitions.COLOR_LIME;
            }
            case 10: {
                return ColorDefinitions.COLOR_TEAL;
            }
            case 11: {
                return ColorDefinitions.COLOR_CYAN;
            }
            case 12: {
                return ColorDefinitions.COLOR_ROYAL_BLUE;
            }
            case 13: {
                return ColorDefinitions.COLOR_FUCHSIA;
            }
            case 14: {
                return ColorDefinitions.COLOR_GREY;
            }
            case 15: {
                return ColorDefinitions.COLOR_SILVER;
            }
            case 100: {
                return ColorDefinitions.COLOR_SYSTEM;
            }
            case 101: {
                return ColorDefinitions.COLOR_ERROR;
            }
            default: {
                return ColorDefinitions.COLOR_BLACK;
            }
        }
    }
    
    static {
        COLOR_SYSTEM = new float[] { 0.5f, 1.0f, 0.5f };
        COLOR_ERROR = new float[] { 1.0f, 0.3f, 0.3f };
        COLOR_WHITE = new float[] { 1.0f, 1.0f, 1.0f };
        COLOR_BLACK = new float[] { 0.0f, 0.0f, 0.0f };
        COLOR_NAVY_BLUE = new float[] { 0.23f, 0.39f, 1.0f };
        COLOR_GREEN = new float[] { 0.08f, 1.0f, 0.08f };
        COLOR_RED = new float[] { 1.0f, 0.0f, 0.0f };
        COLOR_MAROON = new float[] { 0.5f, 0.0f, 0.0f };
        COLOR_PURPLE = new float[] { 0.5f, 0.0f, 0.5f };
        COLOR_ORANGE = new float[] { 1.0f, 0.85f, 0.24f };
        COLOR_YELLOW = new float[] { 1.0f, 1.0f, 0.0f };
        COLOR_LIME = new float[] { 0.0f, 1.0f, 0.0f };
        COLOR_TEAL = new float[] { 0.0f, 0.5f, 0.5f };
        COLOR_CYAN = new float[] { 0.0f, 1.0f, 1.0f };
        COLOR_ROYAL_BLUE = new float[] { 0.23f, 0.39f, 1.0f };
        COLOR_FUCHSIA = new float[] { 1.0f, 0.0f, 1.0f };
        COLOR_GREY = new float[] { 0.5f, 0.5f, 0.5f };
        COLOR_SILVER = new float[] { 0.75f, 0.75f, 0.75f };
    }
}
