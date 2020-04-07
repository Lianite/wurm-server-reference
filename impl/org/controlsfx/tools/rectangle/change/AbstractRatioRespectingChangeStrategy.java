// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools.rectangle.change;

abstract class AbstractRatioRespectingChangeStrategy extends AbstractBeginEndCheckingChangeStrategy
{
    private final boolean ratioFixed;
    private final double ratio;
    
    protected AbstractRatioRespectingChangeStrategy(final boolean ratioFixed, final double ratio) {
        this.ratioFixed = ratioFixed;
        this.ratio = ratio;
    }
    
    protected final boolean isRatioFixed() {
        return this.ratioFixed;
    }
    
    protected final double getRatio() {
        if (!this.ratioFixed) {
            throw new IllegalStateException("The ratio is not fixed.");
        }
        return this.ratio;
    }
}
