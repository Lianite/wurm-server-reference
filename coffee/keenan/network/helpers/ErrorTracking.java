// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.helpers;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.HashMap;

public abstract class ErrorTracking
{
    private final HashMap<String, List<Exception>> exceptions;
    
    public ErrorTracking() {
        this.exceptions = new HashMap<String, List<Exception>>();
    }
    
    public void addException(@NotNull final String key, @NotNull final Exception ex) {
        if (!this.exceptions.containsKey(key)) {
            this.exceptions.put(key, new ArrayList<Exception>());
        }
        this.exceptions.get(key).add(ex);
    }
    
    public String[] getExceptions() {
        final List<String> ret = new ArrayList<String>();
        this.exceptions.forEach((key, list) -> list.forEach(l -> ret.add("[" + key + "] " + l.getMessage())));
        return ret.toArray(new String[0]);
    }
}
