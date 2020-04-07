// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

public abstract class SpreadsheetHandle
{
    protected abstract SpreadsheetView getView();
    
    protected abstract SpreadsheetGridView getGridView();
    
    protected abstract GridViewSkin getCellsViewSkin();
    
    protected abstract boolean isColumnWidthSet(final int p0);
}
