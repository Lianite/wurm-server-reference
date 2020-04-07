// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.event;

import java.util.EventListener;

public interface MessageChangedListener extends EventListener
{
    void messageChanged(final MessageChangedEvent p0);
}
