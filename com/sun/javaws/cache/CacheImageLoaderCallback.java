// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.io.File;
import java.awt.Image;
import com.sun.javaws.jnl.IconDesc;

public interface CacheImageLoaderCallback
{
    void imageAvailable(final IconDesc p0, final Image p1, final File p2);
    
    void finalImageAvailable(final IconDesc p0, final Image p1, final File p2);
}
