// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

public interface Command<T>
{
    void execute(final ServiceManager<T> p0) throws Exception;
}
