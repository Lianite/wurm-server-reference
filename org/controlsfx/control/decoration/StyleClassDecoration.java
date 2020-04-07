// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.decoration;

import java.util.List;
import javafx.scene.Node;

public class StyleClassDecoration extends Decoration
{
    private final String[] styleClasses;
    
    public StyleClassDecoration(final String... styleClass) {
        if (styleClass == null || styleClass.length == 0) {
            throw new IllegalArgumentException("var-arg style class array must not be null or empty");
        }
        this.styleClasses = styleClass;
    }
    
    @Override
    public Node applyDecoration(final Node targetNode) {
        final List<String> styleClassList = (List<String>)targetNode.getStyleClass();
        for (final String styleClass : this.styleClasses) {
            if (!styleClassList.contains(styleClass)) {
                styleClassList.add(styleClass);
            }
        }
        return null;
    }
    
    @Override
    public void removeDecoration(final Node targetNode) {
        targetNode.getStyleClass().removeAll((Object[])this.styleClasses);
    }
}
