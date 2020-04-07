// 
// Decompiled by Procyon v0.5.30
// 

package SteamJni;

import com.wurmonline.server.steam.SteamHandler;

public class SteamServerApi
{
    private final SteamHandler steamHandler;
    public final int eServerModeInvalid = 0;
    public final int eServerModeNoAuthentication = 1;
    public final int eServerModeAuthentication = 2;
    public final int eServerModeAuthenticationAndSecure = 3;
    public static final int beginAuthSessionResultOk = 0;
    public static final int beginAuthSessionResultDuplicateResult = 2;
    public static final boolean USE_GS_AUTH_API = true;
    
    public SteamServerApi(final SteamHandler inSteamHandler) {
        this.steamHandler = inSteamHandler;
    }
    
    public native void CreateCallback();
    
    public native void DeleteCallback();
    
    public native void SteamGameServer_RunCallbacks();
    
    public native boolean SteamGameServer_Init(final long p0, final short p1, final short p2, final short p3, final int p4, final String p5);
    
    public native void SetModDir(final String p0);
    
    public native void SetDedicatedServer(final boolean p0);
    
    public native void SetProduct(final String p0);
    
    public native void SetGameDescription(final String p0);
    
    public native void SetGameTags(final String p0);
    
    public native void LogOnAnonymous();
    
    public native void EnableHeartbeats(final boolean p0);
    
    public native void LogOff();
    
    public native void SteamGameServer_Shutdown();
    
    public native void setMaxPlayerCount(final int p0);
    
    public native void SetPasswordProtected(final boolean p0);
    
    public native void SetServerName(final String p0);
    
    public native void SetBotCount(final int p0);
    
    public native void SetMapName(final String p0);
    
    public native void SetUserAchievement(final int p0, final String p1);
    
    public native void GetUserAchievement(final int p0, final String p1);
    
    public native void StoreUserStats(final int p0);
    
    public native int BeginAuthSession(final String p0, final byte[] p1, final long p2);
    
    public native void EndAuthSession(final String p0);
    
    public void OnSteamServersConnected() {
        this.steamHandler.onSteamConnected();
    }
    
    public void OnValidateAuthTicketResponse(final String steamIdString, final boolean wasSucces) {
        this.steamHandler.onValidateAuthTicketResponse(steamIdString, wasSucces);
    }
    
    static {
        System.loadLibrary("SteamServerJni");
    }
}
