// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.Icon;
import java.net.URI;

public class MutableIcon
{
    public String mimeType;
    public int width;
    public int height;
    public int depth;
    public URI uri;
    
    public Icon build() {
        return new Icon(this.mimeType, this.width, this.height, this.depth, this.uri);
    }
}
