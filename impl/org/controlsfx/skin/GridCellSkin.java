// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.control.Cell;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.GridCell;
import com.sun.javafx.scene.control.skin.CellSkinBase;

public class GridCellSkin<T> extends CellSkinBase<GridCell<T>, BehaviorBase<GridCell<T>>>
{
    public GridCellSkin(final GridCell<T> control) {
        super((Cell)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
    }
}
