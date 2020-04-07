// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.ActionArgument;

public class MutableActionArgument
{
    public String name;
    public String relatedStateVariable;
    public ActionArgument.Direction direction;
    public boolean retval;
    
    public ActionArgument build() {
        return new ActionArgument(this.name, this.relatedStateVariable, this.direction, this.retval);
    }
}
