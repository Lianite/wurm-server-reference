// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

public interface DefaultEventListener<PAYLOAD> extends EventListener<DefaultEvent<PAYLOAD>>
{
    void handleEvent(final DefaultEvent<PAYLOAD> p0);
}
