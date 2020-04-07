// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.steam;

import com.wurmonline.server.Server;
import com.wurmonline.shared.constants.SteamVersion;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.LoginHandler;
import java.util.Map;
import SteamJni.SteamServerApi;

public class SteamHandler
{
    private SteamServerApi steamServerApi;
    private boolean isInitialized;
    private boolean isSteamServerStarted;
    public static short steamQueryPort;
    private Map<String, LoginHandler> loginHandlerList;
    private Map<String, Boolean> isPlayerAuthenticatedList;
    private boolean isOfflineServer;
    private Logger logger;
    
    public SteamHandler() {
        this.isInitialized = false;
        this.isSteamServerStarted = false;
        this.logger = Logger.getLogger(SteamHandler.class.getName());
        this.steamServerApi = new SteamServerApi(this);
        this.loginHandlerList = new ConcurrentHashMap<String, LoginHandler>();
        this.isPlayerAuthenticatedList = new ConcurrentHashMap<String, Boolean>();
        this.isOfflineServer = false;
    }
    
    public void initializeSteam() {
        this.steamServerApi.CreateCallback();
        this.isInitialized = true;
    }
    
    public void shutdownSteamHandler() {
        this.steamServerApi.DeleteCallback();
    }
    
    public void update() {
        if (!this.isInitialized) {
            return;
        }
        this.steamServerApi.SteamGameServer_RunCallbacks();
    }
    
    public void createServer(final String modDir, final String productName, final String gameDescription, final String versionNumber) {
        this.steamServerApi.getClass();
        int EserverMode = 3;
        if (this.isOfflineServer) {
            this.steamServerApi.getClass();
            EserverMode = 1;
        }
        if (!this.steamServerApi.SteamGameServer_Init(0L, (short)8766, Short.valueOf(Servers.localServer.EXTERNALPORT), SteamHandler.steamQueryPort, EserverMode, versionNumber)) {
            this.logger.log(Level.INFO, "Could not start server");
            System.out.println("Could not start server");
            return;
        }
        this.steamServerApi.SetModDir(modDir);
        this.steamServerApi.SetDedicatedServer(true);
        this.steamServerApi.SetProduct(productName);
        this.steamServerApi.SetGameDescription(gameDescription);
        this.steamServerApi.SetGameTags(SteamVersion.getCurrentVersion().getTag() + this.getOfflineTag());
        this.steamServerApi.LogOnAnonymous();
        this.steamServerApi.EnableHeartbeats(true);
        this.logger.log(Level.INFO, "Starting the server");
        System.out.println("Starting the server");
        this.isSteamServerStarted = true;
    }
    
    private String getOfflineTag() {
        if (this.isOfflineServer) {
            return "OFFLINE;";
        }
        return "";
    }
    
    public void closeServer() {
        if (!this.isSteamServerStarted) {
            return;
        }
        this.steamServerApi.EnableHeartbeats(false);
        this.steamServerApi.LogOff();
        this.steamServerApi.SteamGameServer_Shutdown();
        this.isSteamServerStarted = false;
    }
    
    public void onSteamConnected() {
        if (!Servers.localServer.getSteamServerPassword().isEmpty()) {
            this.steamServerApi.SetPasswordProtected(true);
        }
        else {
            this.steamServerApi.SetPasswordProtected(false);
        }
        this.steamServerApi.setMaxPlayerCount(Servers.localServer.pLimit);
        this.steamServerApi.SetServerName(Servers.localServer.getName());
        this.steamServerApi.SetBotCount(0);
        this.steamServerApi.SetMapName(Servers.localServer.mapname);
        this.logger.log(Level.INFO, "Server connected to steam");
        System.out.println("Server connected to steam");
    }
    
    public int BeginAuthSession(final String steamIdAsString, final byte[] tokenArray, final long tokenLen) {
        return this.steamServerApi.BeginAuthSession(steamIdAsString, tokenArray, tokenLen);
    }
    
    public void EndAuthSession(final String steamIdAsString) {
        this.steamServerApi.EndAuthSession(steamIdAsString);
    }
    
    public void onValidateAuthTicketResponse(final String steamIdString, final boolean wasSucces) {
        final LoginHandler loginHandler = this.loginHandlerList.get(steamIdString);
        if (loginHandler != null) {
            String failedMessage = "";
            if (wasSucces) {
                this.logger.log(Level.INFO, "Client was Authenticated");
                System.out.println("Client was Authenticated");
                Server.getInstance().steamHandler.setIsPlayerAuthenticated(steamIdString);
            }
            else {
                this.EndAuthSession(steamIdString);
                this.logger.log(Level.INFO, "Client was  NOT Authenticated");
                System.out.println("Client was  NOT Authenticated");
                failedMessage = "Steam could not authenticate the user";
                Server.getInstance().steamHandler.removeIsPlayerAuthenticated(steamIdString);
                this.loginHandlerList.remove(steamIdString);
            }
            loginHandler.sendAuthenticationAnswer(wasSucces, failedMessage);
        }
        else {
            this.EndAuthSession(steamIdString);
            Server.getInstance().steamHandler.removeIsPlayerAuthenticated(steamIdString);
            this.logger.log(Level.WARNING, "Steam user did not have a login handler");
            System.out.println("Steam user did not have a login handler");
        }
    }
    
    public boolean addLoginHandler(final String SteamIdAsString, final LoginHandler loginHandler) {
        final LoginHandler oldLogin = this.loginHandlerList.get(SteamIdAsString);
        if (oldLogin != null) {
            try {
                if (!oldLogin.getConnectionIp().isEmpty() && oldLogin.getConnectionIp().equals(loginHandler.getConnectionIp())) {
                    return true;
                }
            }
            catch (Exception ex) {
                this.loginHandlerList.put(SteamIdAsString, loginHandler);
                return false;
            }
        }
        this.loginHandlerList.put(SteamIdAsString, loginHandler);
        return false;
    }
    
    public void setIsPlayerAuthenticated(final String steamIdAsString) {
        this.isPlayerAuthenticatedList.put(steamIdAsString, true);
    }
    
    public void removeIsPlayerAuthenticated(final String steamIdAsString) {
        this.isPlayerAuthenticatedList.remove(steamIdAsString);
    }
    
    public boolean isPlayerAuthenticated(final String steamIdAsString) {
        final Boolean isAuthenticated = this.isPlayerAuthenticatedList.get(steamIdAsString);
        return isAuthenticated != null && isAuthenticated;
    }
    
    public void setIsOfflienServer(final boolean isOfflineServer) {
        this.isOfflineServer = isOfflineServer;
    }
    
    public boolean getIsOfflineServer() {
        return this.isOfflineServer;
    }
    
    static {
        SteamHandler.steamQueryPort = 27016;
    }
}
