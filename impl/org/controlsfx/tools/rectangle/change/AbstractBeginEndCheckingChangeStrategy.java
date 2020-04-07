// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import java.util.Objects;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

abstract class AbstractBeginEndCheckingChangeStrategy implements Rectangle2DChangeStrategy
{
    private boolean beforeBegin;
    
    protected AbstractBeginEndCheckingChangeStrategy() {
        this.beforeBegin = true;
    }
    
    @Override
    public final Rectangle2D beginChange(final Point2D point) {
        Objects.requireNonNull(point, "The specified point must not be null.");
        if (!this.beforeBegin) {
            throw new IllegalStateException("The change already began, so 'beginChange' must not be called again before 'endChange' was called.");
        }
        this.beforeBegin = false;
        this.beforeBeginHook(point);
        return this.doBegin(point);
    }
    
    @Override
    public final Rectangle2D continueChange(final Point2D point) {
        Objects.requireNonNull(point, "The specified point must not be null.");
        if (this.beforeBegin) {
            throw new IllegalStateException("The change did not begin. Call 'beginChange' before 'continueChange'.");
        }
        return this.doContinue(point);
    }
    
    @Override
    public final Rectangle2D endChange(final Point2D point) {
        Objects.requireNonNull(point, "The specified point must not be null.");
        if (this.beforeBegin) {
            throw new IllegalStateException("The change did not begin. Call 'beginChange' before 'endChange'.");
        }
        final Rectangle2D finalRectangle = this.doEnd(point);
        this.afterEndHook(point);
        this.beforeBegin = true;
        return finalRectangle;
    }
    
    protected void beforeBeginHook(final Point2D point) {
    }
    
    protected abstract Rectangle2D doBegin(final Point2D p0);
    
    protected abstract Rectangle2D doContinue(final Point2D p0);
    
    protected abstract Rectangle2D doEnd(final Point2D p0);
    
    protected void afterEndHook(final Point2D point) {
    }
}
