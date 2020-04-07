// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import android.content.Intent;
import org.fourthline.cling.transport.Router;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.transport.spi.InitializationException;
import android.content.IntentFilter;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import android.content.BroadcastReceiver;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.content.Context;
import java.util.logging.Logger;
import org.fourthline.cling.transport.RouterImpl;

public class AndroidRouter extends RouterImpl
{
    private static final Logger log;
    private final Context context;
    private final WifiManager wifiManager;
    protected WifiManager.MulticastLock multicastLock;
    protected WifiManager.WifiLock wifiLock;
    protected NetworkInfo networkInfo;
    protected BroadcastReceiver broadcastReceiver;
    
    public AndroidRouter(final UpnpServiceConfiguration configuration, final ProtocolFactory protocolFactory, final Context context) throws InitializationException {
        super(configuration, protocolFactory);
        this.context = context;
        this.wifiManager = (WifiManager)context.getSystemService("wifi");
        this.networkInfo = NetworkUtils.getConnectedNetworkInfo(context);
        if (!ModelUtil.ANDROID_EMULATOR) {
            context.registerReceiver(this.broadcastReceiver = this.createConnectivityBroadcastReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }
    
    protected BroadcastReceiver createConnectivityBroadcastReceiver() {
        return new ConnectivityBroadcastReceiver();
    }
    
    @Override
    protected int getLockTimeoutMillis() {
        return 15000;
    }
    
    @Override
    public void shutdown() throws RouterException {
        super.shutdown();
        this.unregisterBroadcastReceiver();
    }
    
    @Override
    public boolean enable() throws RouterException {
        this.lock(this.writeLock);
        try {
            final boolean enabled;
            if ((enabled = super.enable()) && this.isWifi()) {
                this.setWiFiMulticastLock(true);
                this.setWifiLock(true);
            }
            return enabled;
        }
        finally {
            this.unlock(this.writeLock);
        }
    }
    
    @Override
    public boolean disable() throws RouterException {
        this.lock(this.writeLock);
        try {
            if (this.isWifi()) {
                this.setWiFiMulticastLock(false);
                this.setWifiLock(false);
            }
            return super.disable();
        }
        finally {
            this.unlock(this.writeLock);
        }
    }
    
    public NetworkInfo getNetworkInfo() {
        return this.networkInfo;
    }
    
    public boolean isMobile() {
        return NetworkUtils.isMobile(this.networkInfo);
    }
    
    public boolean isWifi() {
        return NetworkUtils.isWifi(this.networkInfo);
    }
    
    public boolean isEthernet() {
        return NetworkUtils.isEthernet(this.networkInfo);
    }
    
    public boolean enableWiFi() {
        AndroidRouter.log.info("Enabling WiFi...");
        try {
            return this.wifiManager.setWifiEnabled(true);
        }
        catch (Throwable t) {
            AndroidRouter.log.log(Level.WARNING, "SetWifiEnabled failed", t);
            return false;
        }
    }
    
    public void unregisterBroadcastReceiver() {
        if (this.broadcastReceiver != null) {
            this.context.unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }
    
    protected void setWiFiMulticastLock(final boolean enable) {
        if (this.multicastLock == null) {
            this.multicastLock = this.wifiManager.createMulticastLock(this.getClass().getSimpleName());
        }
        if (enable) {
            if (this.multicastLock.isHeld()) {
                AndroidRouter.log.warning("WiFi multicast lock already acquired");
            }
            else {
                AndroidRouter.log.info("WiFi multicast lock acquired");
                this.multicastLock.acquire();
            }
        }
        else if (this.multicastLock.isHeld()) {
            AndroidRouter.log.info("WiFi multicast lock released");
            this.multicastLock.release();
        }
        else {
            AndroidRouter.log.warning("WiFi multicast lock already released");
        }
    }
    
    protected void setWifiLock(final boolean enable) {
        if (this.wifiLock == null) {
            this.wifiLock = this.wifiManager.createWifiLock(3, this.getClass().getSimpleName());
        }
        if (enable) {
            if (this.wifiLock.isHeld()) {
                AndroidRouter.log.warning("WiFi lock already acquired");
            }
            else {
                AndroidRouter.log.info("WiFi lock acquired");
                this.wifiLock.acquire();
            }
        }
        else if (this.wifiLock.isHeld()) {
            AndroidRouter.log.info("WiFi lock released");
            this.wifiLock.release();
        }
        else {
            AndroidRouter.log.warning("WiFi lock already released");
        }
    }
    
    protected void onNetworkTypeChange(final NetworkInfo oldNetwork, final NetworkInfo newNetwork) throws RouterException {
        AndroidRouter.log.info(String.format("Network type changed %s => %s", (oldNetwork == null) ? "" : oldNetwork.getTypeName(), (newNetwork == null) ? "NONE" : newNetwork.getTypeName()));
        if (this.disable()) {
            AndroidRouter.log.info(String.format("Disabled router on network type change (old network: %s)", (oldNetwork == null) ? "NONE" : oldNetwork.getTypeName()));
        }
        this.networkInfo = newNetwork;
        if (this.enable()) {
            AndroidRouter.log.info(String.format("Enabled router on network type change (new network: %s)", (newNetwork == null) ? "NONE" : newNetwork.getTypeName()));
        }
    }
    
    protected void handleRouterExceptionOnNetworkTypeChange(final RouterException ex) {
        final Throwable cause = Exceptions.unwrap(ex);
        if (cause instanceof InterruptedException) {
            AndroidRouter.log.log(Level.INFO, "Router was interrupted: " + ex, cause);
        }
        else {
            AndroidRouter.log.log(Level.WARNING, "Router error on network change: " + ex, ex);
        }
    }
    
    static {
        log = Logger.getLogger(Router.class.getName());
    }
    
    class ConnectivityBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (!intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                return;
            }
            this.displayIntentInfo(intent);
            NetworkInfo newNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);
            if (AndroidRouter.this.networkInfo != null && newNetworkInfo == null) {
                for (int i = 1; i <= 3; ++i) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    AndroidRouter.log.warning(String.format("%s => NONE network transition, waiting for new network... retry #%d", AndroidRouter.this.networkInfo.getTypeName(), i));
                    newNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);
                    if (newNetworkInfo != null) {
                        break;
                    }
                }
            }
            if (this.isSameNetworkType(AndroidRouter.this.networkInfo, newNetworkInfo)) {
                AndroidRouter.log.info("No actual network change... ignoring event!");
            }
            else {
                try {
                    AndroidRouter.this.onNetworkTypeChange(AndroidRouter.this.networkInfo, newNetworkInfo);
                }
                catch (RouterException ex) {
                    AndroidRouter.this.handleRouterExceptionOnNetworkTypeChange(ex);
                }
            }
        }
        
        protected boolean isSameNetworkType(final NetworkInfo network1, final NetworkInfo network2) {
            return (network1 == null && network2 == null) || (network1 != null && network2 != null && network1.getType() == network2.getType());
        }
        
        protected void displayIntentInfo(final Intent intent) {
            final boolean noConnectivity = intent.getBooleanExtra("noConnectivity", false);
            final String reason = intent.getStringExtra("reason");
            final boolean isFailover = intent.getBooleanExtra("isFailover", false);
            final NetworkInfo currentNetworkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
            final NetworkInfo otherNetworkInfo = (NetworkInfo)intent.getParcelableExtra("otherNetwork");
            AndroidRouter.log.info("Connectivity change detected...");
            AndroidRouter.log.info("EXTRA_NO_CONNECTIVITY: " + noConnectivity);
            AndroidRouter.log.info("EXTRA_REASON: " + reason);
            AndroidRouter.log.info("EXTRA_IS_FAILOVER: " + isFailover);
            AndroidRouter.log.info("EXTRA_NETWORK_INFO: " + ((currentNetworkInfo == null) ? "none" : currentNetworkInfo));
            AndroidRouter.log.info("EXTRA_OTHER_NETWORK_INFO: " + ((otherNetworkInfo == null) ? "none" : otherNetworkInfo));
            AndroidRouter.log.info("EXTRA_EXTRA_INFO: " + intent.getStringExtra("extraInfo"));
        }
    }
}
