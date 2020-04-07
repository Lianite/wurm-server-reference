// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.awt.print.Printable;
import java.awt.print.Pageable;
import java.awt.print.PageFormat;

public interface PrintService
{
    PageFormat getDefaultPage();
    
    PageFormat showPageFormatDialog(final PageFormat p0);
    
    boolean print(final Pageable p0);
    
    boolean print(final Printable p0);
}
