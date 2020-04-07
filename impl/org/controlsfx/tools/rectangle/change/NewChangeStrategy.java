// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;

public class NewChangeStrategy extends AbstractFixedPointChangeStrategy
{
    private Point2D startingPoint;
    
    public NewChangeStrategy(final boolean ratioFixed, final double ratio, final Rectangle2D bounds) {
        super(ratioFixed, ratio, bounds);
    }
    
    @Override
    protected void beforeBeginHook(final Point2D point) {
        this.startingPoint = point;
    }
    
    @Override
    protected Point2D getFixedCorner() {
        return this.startingPoint;
    }
}
