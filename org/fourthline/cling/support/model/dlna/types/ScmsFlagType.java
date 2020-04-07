// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

public class ScmsFlagType
{
    private boolean copyright;
    private boolean original;
    
    public ScmsFlagType() {
        this.copyright = true;
        this.original = true;
    }
    
    public ScmsFlagType(final boolean copyright, final boolean original) {
        this.copyright = copyright;
        this.original = original;
    }
    
    public boolean isCopyright() {
        return this.copyright;
    }
    
    public boolean isOriginal() {
        return this.original;
    }
}
