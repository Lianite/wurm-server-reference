// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.wrappers.upnp;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import coffee.keenan.network.helpers.port.Port;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Contract;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.UpnpServiceImpl;
import java.util.concurrent.Executors;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.UpnpService;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public enum UPNPService
{
    INSTANCE;
    
    private static final Logger logger;
    private final Object o;
    private final ExecutorService executorService;
    private boolean initialized;
    private UpnpService upnpService;
    private RemoteDevice router;
    private Service wanService;
    
    private UPNPService() {
        this.o = new Object();
        this.executorService = Executors.newSingleThreadExecutor();
        this.initialized = false;
    }
    
    public static void initialize() {
        if (getInstance().initialized) {
            return;
        }
        getInstance().upnpService = new UpnpServiceImpl();
        getInstance().upnpService.getRegistry().addListener(new FindRouterListener());
        getInstance().initialized = true;
        getInstance().refresh();
        Runtime.getRuntime().addShutdownHook(new Thread(getInstance().upnpService::shutdown));
        Runtime.getRuntime().addShutdownHook(new Thread(getInstance()::shutdownExecutor));
    }
    
    public static void shutdown() {
        if (!getInstance().initialized) {
            return;
        }
        getInstance().shutdownExecutor();
        getInstance().upnpService.shutdown();
    }
    
    @Contract(pure = true)
    public static UPNPService getInstance() {
        return UPNPService.INSTANCE;
    }
    
    @Contract(pure = true)
    static RemoteDevice getRouterDevice() {
        return getInstance().router;
    }
    
    @Contract(pure = true)
    static Service getWanService() {
        return getInstance().wanService;
    }
    
    private void shutdownExecutor() {
        if (!this.initialized) {
            return;
        }
        try {
            this.executorService.shutdown();
            this.executorService.awaitTermination(5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            this.executorService.shutdownNow();
        }
    }
    
    public void refresh() {
        if (!this.initialized) {
            return;
        }
        synchronized (this.o) {
            this.wanService = null;
            this.router = null;
            this.upnpService.getControlPoint().search();
        }
    }
    
    public synchronized void setRouterAndService(final RemoteDevice device, final Service service) {
        if (!this.initialized) {
            return;
        }
        synchronized (this.o) {
            this.wanService = service;
            this.router = device;
            this.o.notifyAll();
        }
    }
    
    public void openPort(final Port port) {
        if (!this.initialized) {
            UPNPService.logger.info("uPNP support not initialized, skipping mapping for " + port.toString());
            return;
        }
        for (final PortMapping portMapping : port.getMappings()) {
            final PortMapping x1;
            this.executorService.submit(() -> {
                if (this.wanService == null) {
                    synchronized (this.o) {
                        try {
                            this.o.wait(5000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                            port.addException(port.toString(), e);
                            return;
                        }
                    }
                }
                this.upnpService.getControlPoint().execute(new PortMappingAdd(this.wanService, x1) {
                    final /* synthetic */ Port val$port;
                    
                    @Override
                    public void success(final ActionInvocation actionInvocation) {
                        this.val$port.setMapped(true);
                        UPNPService.logger.info("port mapped for " + this.val$port.toString());
                    }
                    
                    @Override
                    public void failure(final ActionInvocation actionInvocation, final UpnpResponse upnpResponse, final String s) {
                        UPNPService.logger.warning("unable to map port for " + this.val$port.toString());
                        this.val$port.addException(this.val$port.toString(), new Exception(s));
                    }
                });
                return;
            });
        }
    }
    
    static {
        logger = Logger.getLogger(String.valueOf(UPNPService.class));
    }
}
