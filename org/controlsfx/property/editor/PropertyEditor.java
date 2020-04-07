// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import javafx.scene.Node;

public interface PropertyEditor<T>
{
    Node getEditor();
    
    T getValue();
    
    void setValue(final T p0);
}
