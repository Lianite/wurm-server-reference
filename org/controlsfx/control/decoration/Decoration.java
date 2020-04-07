// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.decoration;

import java.util.HashMap;
import javafx.scene.Node;
import java.util.Map;

public abstract class Decoration
{
    private volatile Map<String, Object> properties;
    
    public abstract Node applyDecoration(final Node p0);
    
    public abstract void removeDecoration(final Node p0);
    
    public final synchronized Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        return this.properties;
    }
}
