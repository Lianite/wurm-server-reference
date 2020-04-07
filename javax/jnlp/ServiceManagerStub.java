// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

public interface ServiceManagerStub
{
    Object lookup(final String p0) throws UnavailableServiceException;
    
    String[] getServiceNames();
}
