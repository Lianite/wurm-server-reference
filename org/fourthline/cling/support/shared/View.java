// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.awt.Component;

public interface View<P>
{
    Component asUIComponent();
    
    void setPresenter(final P p0);
}
