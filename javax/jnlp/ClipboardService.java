// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.awt.datatransfer.Transferable;

public interface ClipboardService
{
    Transferable getContents();
    
    void setContents(final Transferable p0);
}
