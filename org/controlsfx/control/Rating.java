// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.skin.RatingSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Orientation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;

public class Rating extends ControlsFXControl
{
    private DoubleProperty rating;
    private IntegerProperty max;
    private ObjectProperty<Orientation> orientation;
    private BooleanProperty partialRating;
    private BooleanProperty updateOnHover;
    
    public Rating() {
        this(5);
    }
    
    public Rating(final int max) {
        this(max, -1);
    }
    
    public Rating(final int max, final int rating) {
        this.rating = (DoubleProperty)new SimpleDoubleProperty((Object)this, "rating", 3.0);
        this.max = (IntegerProperty)new SimpleIntegerProperty((Object)this, "max", 5);
        this.partialRating = (BooleanProperty)new SimpleBooleanProperty((Object)this, "partialRating", false);
        this.updateOnHover = (BooleanProperty)new SimpleBooleanProperty((Object)this, "updateOnHover", false);
        this.getStyleClass().setAll((Object[])new String[] { "rating" });
        this.setMax(max);
        this.setRating((rating == -1) ? ((double)(int)Math.floor(max / 2.0)) : ((double)rating));
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new RatingSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(Rating.class, "rating.css");
    }
    
    public final DoubleProperty ratingProperty() {
        return this.rating;
    }
    
    public final void setRating(final double value) {
        this.ratingProperty().set(value);
    }
    
    public final double getRating() {
        return (this.rating == null) ? 3.0 : this.rating.get();
    }
    
    public final IntegerProperty maxProperty() {
        return this.max;
    }
    
    public final void setMax(final int value) {
        this.maxProperty().set(value);
    }
    
    public final int getMax() {
        return (this.max == null) ? 5 : this.max.get();
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        if (this.orientation == null) {
            this.orientation = (ObjectProperty<Orientation>)new SimpleObjectProperty((Object)this, "orientation", (Object)Orientation.HORIZONTAL);
        }
        return this.orientation;
    }
    
    public final void setOrientation(final Orientation value) {
        this.orientationProperty().set((Object)value);
    }
    
    public final Orientation getOrientation() {
        return (Orientation)((this.orientation == null) ? Orientation.HORIZONTAL : this.orientation.get());
    }
    
    public final BooleanProperty partialRatingProperty() {
        return this.partialRating;
    }
    
    public final void setPartialRating(final boolean value) {
        this.partialRatingProperty().set(value);
    }
    
    public final boolean isPartialRating() {
        return this.partialRating != null && this.partialRating.get();
    }
    
    public final BooleanProperty updateOnHoverProperty() {
        return this.updateOnHover;
    }
    
    public final void setUpdateOnHover(final boolean value) {
        this.updateOnHoverProperty().set(value);
    }
    
    public final boolean isUpdateOnHover() {
        return this.updateOnHover != null && this.updateOnHover.get();
    }
}
