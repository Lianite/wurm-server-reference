// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.util.List;

public interface Node<T>
{
    Long getId();
    
    T getParent();
    
    List<T> getChildren();
}
