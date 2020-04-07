// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.Skin;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class SpreadsheetGridView extends TableView<ObservableList<SpreadsheetCell>>
{
    private final SpreadsheetHandle handle;
    private String stylesheet;
    
    public SpreadsheetGridView(final SpreadsheetHandle handle) {
        this.handle = handle;
    }
    
    public String getUserAgentStylesheet() {
        if (this.stylesheet == null) {
            this.stylesheet = SpreadsheetView.class.getResource("spreadsheet.css").toExternalForm();
        }
        return this.stylesheet;
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new GridViewSkin(this.handle);
    }
    
    public GridViewSkin getGridViewSkin() {
        return this.handle.getCellsViewSkin();
    }
}
