// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared.log;

import org.seamless.swing.logging.LogCategory;
import java.util.List;
import org.seamless.swing.logging.LogMessage;
import org.fourthline.cling.support.shared.View;

public interface LogView extends View<Presenter>
{
    void pushMessage(final LogMessage p0);
    
    void dispose();
    
    public interface LogCategories extends List<LogCategory>
    {
    }
    
    public interface Presenter
    {
        void init();
        
        void onExpand(final LogMessage p0);
        
        void pushMessage(final LogMessage p0);
    }
}
