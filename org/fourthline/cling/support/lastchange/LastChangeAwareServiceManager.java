// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.meta.StateVariable;
import java.util.ArrayList;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.DefaultServiceManager;

public class LastChangeAwareServiceManager<T extends LastChangeDelegator> extends DefaultServiceManager<T>
{
    protected final LastChangeParser lastChangeParser;
    
    public LastChangeAwareServiceManager(final LocalService<T> localService, final LastChangeParser lastChangeParser) {
        this(localService, null, lastChangeParser);
    }
    
    public LastChangeAwareServiceManager(final LocalService<T> localService, final Class<T> serviceClass, final LastChangeParser lastChangeParser) {
        super(localService, serviceClass);
        this.lastChangeParser = lastChangeParser;
    }
    
    protected LastChangeParser getLastChangeParser() {
        return this.lastChangeParser;
    }
    
    public void fireLastChange() {
        this.lock();
        try {
            this.getImplementation().getLastChange().fire(this.getPropertyChangeSupport());
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    protected Collection<StateVariableValue> readInitialEventedStateVariableValues() throws Exception {
        final LastChange lc = new LastChange(this.getLastChangeParser());
        final UnsignedIntegerFourBytes[] ids = this.getImplementation().getCurrentInstanceIds();
        if (ids.length > 0) {
            for (final UnsignedIntegerFourBytes instanceId : ids) {
                this.getImplementation().appendCurrentState(lc, instanceId);
            }
        }
        else {
            this.getImplementation().appendCurrentState(lc, new UnsignedIntegerFourBytes(0L));
        }
        final StateVariable variable = this.getService().getStateVariable("LastChange");
        final Collection<StateVariableValue> values = new ArrayList<StateVariableValue>();
        values.add(new StateVariableValue(variable, lc.toString()));
        return values;
    }
}
