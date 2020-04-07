// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.glyphfont;

import com.sun.javafx.css.StyleManager;
import java.util.Iterator;
import java.util.HashMap;
import javafx.scene.text.Font;
import java.io.InputStream;
import java.util.Map;

public class GlyphFont
{
    private final Map<String, Character> namedGlyphs;
    private final Runnable fontLoader;
    private final String fontName;
    private final double defaultSize;
    private boolean fontLoaded;
    
    public GlyphFont(final String fontName, final int defaultSize, final InputStream in) {
        this(fontName, defaultSize, in, false);
    }
    
    public GlyphFont(final String fontName, final int defaultSize, final String urlStr) {
        this(fontName, defaultSize, urlStr, false);
    }
    
    public GlyphFont(final String fontName, final int defaultSize, final InputStream in, final boolean lazyLoad) {
        this(fontName, defaultSize, () -> Font.loadFont(in, -1.0), lazyLoad);
    }
    
    public GlyphFont(final String fontName, final int defaultSize, final String urlStr, final boolean lazyLoad) {
        this(fontName, defaultSize, () -> Font.loadFont(urlStr, -1.0), lazyLoad);
    }
    
    private GlyphFont(final String fontName, final int defaultSize, final Runnable fontLoader, final boolean lazyLoad) {
        this.namedGlyphs = new HashMap<String, Character>();
        this.fontLoaded = false;
        this.fontName = fontName;
        this.defaultSize = defaultSize;
        this.fontLoader = fontLoader;
        if (!lazyLoad) {
            this.ensureFontIsLoaded();
        }
    }
    
    public String getName() {
        return this.fontName;
    }
    
    public double getDefaultSize() {
        return this.defaultSize;
    }
    
    public Glyph create(final char character) {
        return new Glyph(this.fontName, character);
    }
    
    public Glyph create(final String glyphName) {
        return new Glyph(this.fontName, glyphName);
    }
    
    public Glyph create(final Enum<?> glyph) {
        return new Glyph(this.fontName, glyph);
    }
    
    public Character getCharacter(final String glyphName) {
        return this.namedGlyphs.get(glyphName.toUpperCase());
    }
    
    public void registerAll(final Iterable<? extends INamedCharacter> namedCharacters) {
        for (final INamedCharacter e : namedCharacters) {
            this.register(e.name(), e.getChar());
        }
    }
    
    public void register(final String name, final Character character) {
        this.namedGlyphs.put(name.toUpperCase(), character);
    }
    
    synchronized void ensureFontIsLoaded() {
        if (!this.fontLoaded) {
            this.fontLoader.run();
            this.fontLoaded = true;
        }
    }
    
    static {
        StyleManager.getInstance().addUserAgentStylesheet(GlyphFont.class.getResource("glyphfont.css").toExternalForm());
    }
}
