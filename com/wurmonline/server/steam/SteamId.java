// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.steam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamId
{
    private long steamID64;
    private int accountNumber;
    private int accountInstance;
    private byte accountType;
    private byte accountUniverse;
    static Pattern idPattern;
    static Pattern id3Pattern;
    static long uIdentifier;
    static long gIdentifier;
    
    public static SteamId fromSteamID64(final long steamID64) {
        final SteamId id = new SteamId();
        id.accountNumber = (int)(0xFFFFFFFFL & steamID64);
        id.accountInstance = (int)((0xFFFFF00000000L & steamID64) >> 32);
        id.accountType = (byte)((0xF0000000000000L & steamID64) >> 52);
        id.accountUniverse = (byte)((0xFF00000000000000L & steamID64) >> 56);
        id.steamID64 = steamID64;
        return id;
    }
    
    public static SteamId fromSteamIDString(final String steamIDString) {
        return fromSteamIDString(steamIDString, true);
    }
    
    public static SteamId fromSteamIDString(final String steamIDString, final boolean individual) {
        final Matcher m = SteamId.idPattern.matcher(steamIDString);
        if (!m.matches() || m.groupCount() < 3) {
            return null;
        }
        final int y = Integer.valueOf(m.group("y"));
        final int z = Integer.valueOf(m.group("z"));
        return fromSteamID64(z * 2 + (individual ? SteamId.uIdentifier : SteamId.gIdentifier) + y);
    }
    
    public static SteamId fromSteamID3String(final String steamID3String) {
        final Matcher m = SteamId.id3Pattern.matcher(steamID3String);
        if (!m.matches()) {
            return null;
        }
        final int w = Integer.valueOf(m.group("w"));
        return fromSteamID64(w + SteamId.uIdentifier);
    }
    
    public String steamIDString() {
        return String.format("STEAM_%d:%d:%d", this.accountUniverse, this.accountNumber & 0x1, this.accountNumber >> 1);
    }
    
    public String steamID3String() {
        return String.format("[U:1:%d]", (this.accountNumber >> 1) * 2 + (this.accountNumber & 0x1));
    }
    
    public long getSteamID64() {
        return this.steamID64;
    }
    
    @Override
    public String toString() {
        return String.format("%d", this.steamID64);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof SteamId) {
            final SteamId id = (SteamId)obj;
            return id.getSteamID64() == this.getSteamID64();
        }
        if (obj instanceof String) {
            final String s = (String)obj;
            return this.steamID3String().equals(s) || this.steamIDString().equals(s) || this.toString().equals(s);
        }
        return obj instanceof Long && (long)obj == this.getSteamID64();
    }
    
    public static SteamId fromAnyString(final String input) {
        final long id64 = Long.valueOf(input);
        if (id64 != 0L) {
            return fromSteamID64(id64);
        }
        final SteamId id65 = fromSteamIDString(input);
        if (id65 != null) {
            return id65;
        }
        return fromSteamID3String(input);
    }
    
    static {
        SteamId.idPattern = Pattern.compile("^STEAM_(?<x>\\d):(?<y>[01]):(?<z>\\d+)$");
        SteamId.id3Pattern = Pattern.compile("^\\[U:1:(?<w>\\d+)]$");
        SteamId.uIdentifier = 76561197960265728L;
        SteamId.gIdentifier = 103582791429521408L;
    }
}
