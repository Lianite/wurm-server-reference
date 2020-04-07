// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import java.util.Iterator;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Action;
import java.util.ArrayList;
import java.util.List;

public class MutableAction
{
    public String name;
    public List<MutableActionArgument> arguments;
    
    public MutableAction() {
        this.arguments = new ArrayList<MutableActionArgument>();
    }
    
    public Action build() {
        return new Action(this.name, this.createActionArgumennts());
    }
    
    public ActionArgument[] createActionArgumennts() {
        final ActionArgument[] array = new ActionArgument[this.arguments.size()];
        int i = 0;
        for (final MutableActionArgument argument : this.arguments) {
            array[i++] = argument.build();
        }
        return array;
    }
}
