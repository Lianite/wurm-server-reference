// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.descriptor;

import java.util.Collection;

public interface JspConfigDescriptor
{
    Collection<TaglibDescriptor> getTaglibs();
    
    Collection<JspPropertyGroupDescriptor> getJspPropertyGroups();
}
