// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.util.HashMap;
import java.awt.image.ImageProducer;
import sun.awt.image.URLImageSource;
import java.awt.Toolkit;
import java.awt.Image;
import java.net.URL;
import java.util.Map;

class ImageCache
{
    private static Map images;
    
    static synchronized Image getImage(final URL url) {
        Image image = ImageCache.images.get(url);
        if (image == null) {
            image = Toolkit.getDefaultToolkit().createImage(new URLImageSource(url));
            ImageCache.images.put(url, image);
        }
        return image;
    }
    
    public static void initialize() {
        ImageCache.images = new HashMap();
    }
    
    static {
        ImageCache.images = null;
    }
}
