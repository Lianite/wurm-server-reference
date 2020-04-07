// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.glyphfont;

import javafx.beans.Observable;
import javafx.scene.text.Font;
import java.util.Collection;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import org.controlsfx.tools.Duplicatable;
import javafx.scene.control.Label;

public class Glyph extends Label implements Duplicatable<Glyph>
{
    public static final String DEFAULT_CSS_CLASS = "glyph-font";
    public static final String STYLE_GRADIENT = "gradient";
    public static final String STYLE_HOVER_EFFECT = "hover-effect";
    private final ObjectProperty<Object> icon;
    
    public static Glyph create(final String fontAndGlyph) {
        final String[] args = fontAndGlyph.split("\\|");
        return new Glyph(args[0], args[1]);
    }
    
    public Glyph() {
        this.icon = (ObjectProperty<Object>)new SimpleObjectProperty();
        this.getStyleClass().add((Object)"glyph-font");
        this.icon.addListener(x -> this.updateIcon());
        this.fontProperty().addListener(x -> this.updateIcon());
    }
    
    public Glyph(final String fontFamily, final char unicode) {
        this();
        this.setFontFamily(fontFamily);
        this.setTextUnicode(unicode);
    }
    
    public Glyph(final String fontFamily, final Object icon) {
        this();
        this.setFontFamily(fontFamily);
        this.setIcon(icon);
    }
    
    public Glyph fontFamily(final String fontFamily) {
        this.setFontFamily(fontFamily);
        return this;
    }
    
    public Glyph color(final Color color) {
        this.setColor(color);
        return this;
    }
    
    public Glyph size(final double size) {
        this.setFontSize(size);
        return this;
    }
    
    public Glyph sizeFactor(final int factor) {
        Optional.ofNullable(GlyphFontRegistry.font(this.getFont().getFamily())).ifPresent(glyphFont -> this.setFontSize(glyphFont.getDefaultSize() * ((factor < 1) ? 1 : factor)));
        return this;
    }
    
    public Glyph useHoverEffect() {
        this.getStyleClass().add((Object)"hover-effect");
        return this;
    }
    
    public Glyph useGradientEffect() {
        if (this.getTextFill() instanceof Color) {
            final Color currentColor = (Color)this.getTextFill();
            final Stop[] stops = { new Stop(0.0, Color.BLACK), new Stop(1.0, currentColor) };
            final LinearGradient lg1 = new LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, stops);
            this.setTextFill((Paint)lg1);
        }
        this.getStyleClass().add((Object)"gradient");
        return this;
    }
    
    public Glyph duplicate() {
        final Paint color = this.getTextFill();
        final Object icon = this.getIcon();
        final ObservableList<String> classes = (ObservableList<String>)this.getStyleClass();
        return new Glyph() {
            {
                this.setIcon(icon);
                this.setTextFill(color);
                this.getStyleClass().addAll((Collection)classes);
            }
        }.fontFamily(this.getFontFamily()).size(this.getFontSize());
    }
    
    public void setFontFamily(final String family) {
        if (!this.getFont().getFamily().equals(family)) {
            final Font newFont;
            Optional.ofNullable(GlyphFontRegistry.font(family)).ifPresent(glyphFont -> {
                glyphFont.ensureFontIsLoaded();
                newFont = Font.font(family, glyphFont.getDefaultSize());
                this.setFont(newFont);
            });
        }
    }
    
    public String getFontFamily() {
        return this.getFont().getFamily();
    }
    
    public void setFontSize(final double size) {
        final Font newFont = Font.font(this.getFont().getFamily(), size);
        this.setFont(newFont);
    }
    
    public double getFontSize() {
        return this.getFont().getSize();
    }
    
    public void setColor(final Color color) {
        this.setTextFill((Paint)color);
    }
    
    public ObjectProperty<Object> iconProperty() {
        return this.icon;
    }
    
    public void setIcon(final Object iconValue) {
        this.icon.set(iconValue);
    }
    
    public Object getIcon() {
        return this.icon.get();
    }
    
    private void updateIcon() {
        final Object iconValue = this.getIcon();
        if (iconValue != null) {
            if (iconValue instanceof Character) {
                this.setTextUnicode((char)iconValue);
            }
            else {
                final GlyphFont glyphFont = GlyphFontRegistry.font(this.getFontFamily());
                if (glyphFont != null) {
                    final String name = iconValue.toString();
                    final Character unicode = glyphFont.getCharacter(name);
                    if (unicode != null) {
                        this.setTextUnicode(unicode);
                    }
                    else {
                        this.setText(name);
                    }
                }
            }
        }
    }
    
    private void setTextUnicode(final char unicode) {
        this.setText(String.valueOf(unicode));
    }
}
