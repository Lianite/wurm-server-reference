// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import org.fourthline.cling.controlpoint.ControlPoint;
import android.os.Binder;
import android.os.IBinder;
import android.content.Intent;
import android.content.Context;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.UpnpService;
import android.app.Service;

public class AndroidUpnpServiceImpl extends Service
{
    protected UpnpService upnpService;
    protected Binder binder;
    
    public AndroidUpnpServiceImpl() {
        this.binder = new Binder();
    }
    
    public void onCreate() {
        super.onCreate();
        this.upnpService = new UpnpServiceImpl(this.createConfiguration(), new RegistryListener[0]) {
            @Override
            protected Router createRouter(final ProtocolFactory protocolFactory, final Registry registry) {
                return AndroidUpnpServiceImpl.this.createRouter(this.getConfiguration(), protocolFactory, (Context)AndroidUpnpServiceImpl.this);
            }
            
            @Override
            public synchronized void shutdown() {
                ((AndroidRouter)this.getRouter()).unregisterBroadcastReceiver();
                super.shutdown(true);
            }
        };
    }
    
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration();
    }
    
    protected AndroidRouter createRouter(final UpnpServiceConfiguration configuration, final ProtocolFactory protocolFactory, final Context context) {
        return new AndroidRouter(configuration, protocolFactory, context);
    }
    
    public IBinder onBind(final Intent intent) {
        return (IBinder)this.binder;
    }
    
    public void onDestroy() {
        this.upnpService.shutdown();
        super.onDestroy();
    }
    
    protected class Binder extends android.os.Binder implements AndroidUpnpService
    {
        public UpnpService get() {
            return AndroidUpnpServiceImpl.this.upnpService;
        }
        
        public UpnpServiceConfiguration getConfiguration() {
            return AndroidUpnpServiceImpl.this.upnpService.getConfiguration();
        }
        
        public Registry getRegistry() {
            return AndroidUpnpServiceImpl.this.upnpService.getRegistry();
        }
        
        public ControlPoint getControlPoint() {
            return AndroidUpnpServiceImpl.this.upnpService.getControlPoint();
        }
    }
}
