// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Externalizable;
import java.beans.Beans;

public class CommandInfo
{
    private String verb;
    private String className;
    
    public CommandInfo(final String verb, final String className) {
        this.verb = verb;
        this.className = className;
    }
    
    public String getCommandName() {
        return this.verb;
    }
    
    public String getCommandClass() {
        return this.className;
    }
    
    public Object getCommandObject(final DataHandler dh, final ClassLoader loader) throws IOException, ClassNotFoundException {
        Object new_bean = null;
        new_bean = Beans.instantiate(loader, this.className);
        if (new_bean != null) {
            if (new_bean instanceof CommandObject) {
                ((CommandObject)new_bean).setCommandContext(this.verb, dh);
            }
            else if (new_bean instanceof Externalizable && dh != null) {
                final InputStream is = dh.getInputStream();
                if (is != null) {
                    ((Externalizable)new_bean).readExternal(new ObjectInputStream(is));
                }
            }
        }
        return new_bean;
    }
}
