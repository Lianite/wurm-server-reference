// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.glyphfont;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;

public final class GlyphFontRegistry
{
    private static Map<String, GlyphFont> fontMap;
    
    public static void register(final String familyName, final String uri, final int defaultSize) {
        register(new GlyphFont(familyName, defaultSize, uri));
    }
    
    public static void register(final String familyName, final InputStream in, final int defaultSize) {
        register(new GlyphFont(familyName, defaultSize, in));
    }
    
    public static void register(final GlyphFont font) {
        if (font != null) {
            GlyphFontRegistry.fontMap.put(font.getName(), font);
        }
    }
    
    public static GlyphFont font(final String familyName) {
        final GlyphFont font = GlyphFontRegistry.fontMap.get(familyName);
        if (font != null) {
            font.ensureFontIsLoaded();
        }
        return font;
    }
    
    static {
        GlyphFontRegistry.fontMap = new HashMap<String, GlyphFont>();
        final ServiceLoader<GlyphFont> loader = ServiceLoader.load(GlyphFont.class);
        for (final GlyphFont font : loader) {
            register(font);
        }
    }
}
