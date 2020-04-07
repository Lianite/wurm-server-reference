// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

public abstract class Section
{
    public abstract List<Block> getBlocks(final HttpServletRequest p0, final LoginInfo p1);
    
    public abstract String getId();
    
    public abstract String getName();
    
    public void handlePost(final HttpServletRequest req, final LoginInfo loginInfo) {
    }
}
