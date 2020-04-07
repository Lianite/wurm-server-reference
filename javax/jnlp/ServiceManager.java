// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

public final class ServiceManager
{
    private static ServiceManagerStub _stub;
    
    public static Object lookup(final String s) throws UnavailableServiceException {
        if (ServiceManager._stub != null) {
            return ServiceManager._stub.lookup(s);
        }
        throw new UnavailableServiceException("uninitialized");
    }
    
    public static String[] getServiceNames() {
        if (ServiceManager._stub != null) {
            return ServiceManager._stub.getServiceNames();
        }
        return null;
    }
    
    public static synchronized void setServiceManagerStub(final ServiceManagerStub stub) {
        if (ServiceManager._stub == null) {
            ServiceManager._stub = stub;
        }
    }
    
    static {
        ServiceManager._stub = null;
    }
}
