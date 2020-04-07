// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;

public class image_jpeg extends image_gif
{
    private static ActivationDataFlavor myDF;
    static /* synthetic */ Class class$java$awt$Image;
    
    protected ActivationDataFlavor getDF() {
        return image_jpeg.myDF;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        image_jpeg.myDF = new ActivationDataFlavor((image_jpeg.class$java$awt$Image == null) ? (image_jpeg.class$java$awt$Image = class$("java.awt.Image")) : image_jpeg.class$java$awt$Image, "image/jpeg", "JPEG Image");
    }
}
